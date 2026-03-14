-- init provisional, fue el que usé para crear la bd e insertar los registros 

-- Tabla principal de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
    id              BIGSERIAL       PRIMARY KEY,
    pedido_ref      VARCHAR(40)     NOT NULL UNIQUE,
    cliente_id      BIGINT          NOT NULL,
    producto_id     BIGINT          NOT NULL,
    categoria_id    INT             NOT NULL,
    region          VARCHAR(40)     NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    cantidad        INT             NOT NULL DEFAULT 1,
    precio_unit     NUMERIC(10,2)   NOT NULL,
    total           NUMERIC(12,2)   NOT NULL,
    canal           VARCHAR(20)     NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ
);

-- Tabla de clientes
CREATE TABLE IF NOT EXISTS clientes (
    id              BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(100)    NOT NULL,
    email           VARCHAR(150)    NOT NULL UNIQUE,
    region          VARCHAR(40)     NOT NULL,
    segmento        VARCHAR(30)     NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

-- Habilitar pg_stat_statements
CREATE EXTENSION IF NOT EXISTS pg_stat_statements;


-- 1. CARGA DE CLIENTES (500,000 registros)
INSERT INTO clientes (
    nombre, email, region, segmento, activo, created_at
)
SELECT
    'Cliente ' || gs::text,
    'cliente_' || gs::text || '@dominio.simulado.com',
    CASE (gs % 4)
        WHEN 0 THEN 'Norte'
        WHEN 1 THEN 'Sur'
        WHEN 2 THEN 'Este'
        ELSE 'Oeste'
    END,
    CASE (gs % 3)
        WHEN 0 THEN 'Retail'
        WHEN 1 THEN 'Mayorista'
        ELSE 'Corporativo'
    END,
    CASE WHEN random() < 0.90 THEN TRUE ELSE FALSE END,
    NOW() - (random() * 365 * 2)::int * INTERVAL '1 day'
FROM generate_series(1, 500000) gs;

-- Actualizamos estadísticas de la tabla clientes
ANALYZE clientes;


-- 2. CARGA DE PEDIDOS (5,000,000 registros)
INSERT INTO pedidos (
    pedido_ref, cliente_id, producto_id, categoria_id,
    region, status, cantidad, precio_unit, total,
    canal, created_at
)
SELECT
    'PED-REF-' || LPAD(gs::text, 9, '0'),
    (1 + floor(random() * 500000))::bigint, 
    (1 + floor(random() * 10000))::bigint,
    (1 + floor(random() * 50))::int,
    CASE (gs % 4)
        WHEN 0 THEN 'Norte'
        WHEN 1 THEN 'Sur'
        WHEN 2 THEN 'Este'
        ELSE 'Oeste'
    END,
    CASE (gs % 5)
        WHEN 0 THEN 'completed'
        WHEN 1 THEN 'completed'
        WHEN 2 THEN 'completed'
        WHEN 3 THEN 'pending'
        ELSE 'cancelled'
    END,
    (1 + floor(random() * 20))::int,
    ROUND((10 + random() * 990)::numeric, 2),
    ROUND(((1 + floor(random() * 20)) * (10 + random() * 990))::numeric, 2),
    CASE (gs % 4)
        WHEN 0 THEN 'Web'
        WHEN 1 THEN 'App Movil'
        WHEN 2 THEN 'Tienda Fisica'
        ELSE 'Call Center'
    END,
    NOW() - (random() * 365 * 2)::int * INTERVAL '1 day'
FROM generate_series(1, 5000000) gs;

-- Actualizamos estadísticas de la tabla pedidos
ANALYZE pedidos;