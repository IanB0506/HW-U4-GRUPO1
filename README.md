Abrir esto en sus navegadores para confirmar el funcionamiento

http://localhost:8080
http://localhost:3000
http://localhost:9090

------------------------------------------------------------------------------------

-- Primer paso (despues de hacer cd a la carpeta) para levanter docker
docker compose up --build -d


-- confirmer estado bd
docker logs -f db_hw4

-- importante instalar artillery
npm install -g artillery

-- una vez confirmed que la bd esta lista ejecutar primera ronda
artillery run artillery/load-test.yml --output artillery/results-sin-index.json

-- reporte sin indices
npx artillery@2.0.0-31 report artillery/results-no-index.json --output reporte-no-index.html


-- Comando para inserter index
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "CREATE INDEX idx_pedidos_region_status ON pedidos(region, status, created_at DESC); CREATE INDEX idx_pedidos_resumen_covering ON pedidos(region, status) INCLUDE (total); ANALYZE pedidos;"

-- Ejecutar de nuevo con index
artillery run artillery/load-test.yml --output artillery/results-con-index.json

-- reporte con indices 
npx artillery@2.0.0-31 report artillery/results-with-index.json --output reporte-con-index.html

-- revisar index existentes
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "SELECT indexname, indexdef FROM pg_indexes WHERE tablename = 'usuarios';"


-- eliminar index si necesitan rehacer pruebas
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "DROP INDEX idx_pedidos_region_status; DROP INDEX idx_pedidos_resumen_covering;"

-- Hacer vacuum para eliminar dead tuples
docker exec -it db_hw4 psql -U postgres -d dbii_hw4 -c "VACUUM ANALYZE pedidos;"


-- Comando para terminar docker
docker compose down -v




