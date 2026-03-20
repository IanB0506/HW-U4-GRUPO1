Abrir esto en sus navegadores para confirmar el funcionamiento

Prueba de API
http://localhost:8080/pedidos/resumen?region=Norte
Dashboard de Grafana
http://localhost:3000
Prometheus
http://localhost:9090/targets

------------------------------------------------------------------------------------

-- Primer paso (despues de hacer cd a la carpeta) para levanter docker
docker compose up --build -d

-- confirmer estado bd
docker logs -f db_hw4

-- importante instalar artillery
npm install -g artillery


-------------
Pruebas      |
-------------

-- una vez confirmed que la bd esta lista ejecutar primera ronda
artillery run artillery/load-test.yml --output artillery/results-sin-index.json

-- reporte sin indices
npx artillery@2.0.0-31 report artillery/results-sin-index.json --output reporte-sin-index.html


-- Comando para inserter index
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "CREATE INDEX idx_pedidos_region_status ON pedidos(region, status, created_at DESC); CREATE INDEX idx_pedidos_resumen_covering ON pedidos(region, status) INCLUDE (total); ANALYZE pedidos;"

-- Ejecutar de nuevo con index (Ronda 2)
artillery run artillery/load-test.yml --output artillery/results-con-index.json

-- reporte con indices 
npx artillery@2.0.0-31 report artillery/results-con-index.json --output reporte-con-index.html

-- revisar index existentes
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'pedidos';"


-- eliminar index si necesitan rehacer pruebas
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "DROP INDEX idx_pedidos_region_status; DROP INDEX idx_pedidos_resumen_covering;"

-- Hacer vacuum para eliminar dead tuples
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "VACUUM ANALYZE pedidos;"


-- Comando para terminar docker
docker compose down -v

-------------
Experimentos |
-------------

--Experimento 1--
------------------------------------------------------------------------------------
--Iniciar docker compose
docker compose up --build -d
docker compose up -d

--Correr artilery para hacer prueba sin index
artillery run artillery/load-test.yml

--Creación de los indices excesivos
docker exec -i db_hw4 psql -U postgres -d dbii_hw4 -c "CREATE INDEX IF NOT EXISTS idx_pedidos_region ON pedidos(region); CREATE INDEX IF NOT EXISTS idx_pedidos_status ON pedidos(status); CREATE INDEX IF NOT EXISTS idx_pedidos_status_region ON pedidos(status, region); CREATE INDEX IF NOT EXISTS idx_pedidos_cliente ON pedidos(cliente_id); CREATE INDEX IF NOT EXISTS idx_pedidos_producto ON pedidos(producto_id); CREATE INDEX IF NOT EXISTS idx_pedidos_updated_at ON pedidos(updated_at); CREATE INDEX IF NOT EXISTS idx_pedidos_region_status_cat ON pedidos(region, status, categoria_id);"

--Correr artilery para hacer prueba con los indices excesivos
artillery run artillery/load-test.yml
------------------------------------------------------------------------------------



------------------------------------------------------------------------------------
--Experimento 2--
------------------------------------------------------------------------------------
--Iniciar docker compose
docker compose up --build -d
docker compose up -d

--Correr artilery para hacer prueba con el escenario analítico
artillery run artillery/test-analitico.yml --output results-analytical-no-index.json

--Creación del índice cubriente idx_pedidos_resumen_covering
docker exec -i db_hw4 psql -U postgres -d dbii_hw4 -c "CREATE INDEX IF NOT EXISTS idx_pedidos_resumen_covering ON pedidos(region, status) INCLUDE (total); ANALYZE pedidos;"

--Correr artilery para hacer prueba con los indices excesivos
artillery run artillery/test-analitico.yml --output results-analytical-with-index.json
------------------------------------------------------------------------------------



------------------------------------------------------------------------------------
--Experimento 3--
------------------------------------------------------------------------------------
--Iniciar docker compose
docker compose up --build -d
docker compose up -d

-- Comando para inserter index
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "CREATE INDEX idx_pedidos_region_status ON pedidos(region, status, created_at DESC); CREATE INDEX idx_pedidos_resumen_covering ON pedidos(region, status) INCLUDE (total); ANALYZE pedidos;"

--Correr artilery para hacer prueba con con los índices correctos
artillery run artillery/test-analitico.yml

--Al finalizar las pruebs de artillery ejecutar VACCUM ANALYZE
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "VACUUM ANALYZE pedidos;"

--Para verificar inmediatamente la cantidad de Dead Tuples (opcional)
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "SELECT n_dead_tup FROM pg_stat_user_tables WHERE relname = 'pedidos';"
------------------------------------------------------------------------------------


--
docker compose down
docker compose build --no-cache api
docker compose up -d
--
docker compose stop

docker compose restart api

