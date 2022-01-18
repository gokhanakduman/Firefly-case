DO $$ BEGIN
    CREATE TYPE payment_type_enum AS ENUM ('Cash', 'CreditCard', 'Check');
    CREATE CAST (character varying as payment_type_enum) WITH INOUT AS IMPLICIT;
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

CREATE TABLE IF NOT EXISTS orders
(
    id UUID PRIMARY KEY,
    price float NOT NULL,
	payment_type payment_type_enum NOT NULL,
	created_at timestamp NOT NULL default now(),
    updated_at timestamp NOT NULL default now(),
	latitude float NOT NULL,
 	longitude float NOT NULL,
    "version" int NOT NULL default 0
);

/*
CREATE OR REPLACE FUNCTION update_updated_at_column()   
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;   
END;
$$ language 'plpgsql';

CREATE TRIGGER update_order_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE PROCEDURE  update_updated_at_column();
*/
CREATE TABLE IF NOT EXISTS outbox
(
    event_id UUID PRIMARY KEY,
    event_name varchar(50) NOT NULL,
    event_time timestamp NOT NULL default now(),
    aggragate_type varchar(50) NOT NULL,
    aggragate_id UUID,
    aggragate_version int,
    payload varchar(1000)
);