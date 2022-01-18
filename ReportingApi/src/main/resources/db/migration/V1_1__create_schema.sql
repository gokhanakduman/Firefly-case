CREATE TABLE IF NOT EXISTS geometries 
(
    id UUID PRIMARY KEY,
    geometry_name varchar(50) NOT NULL,
    geometry_polygon geometry
);

INSERT INTO geometries VALUES
('aaeafc70-7cb5-4414-aa42-7d6e760fd065', 'Ankara', 'SRID=4326;POLYGON((32.18994140625 39.487084981687495,34.310302734375 39.487084981687495,34.310302734375 40.49709237269567,32.18994140625 40.49709237269567,32.18994140625 39.487084981687495))'),
('71bb5f1a-f493-4704-8bbc-b2c401983ae7', 'Konya',  'SRID=4326;POLYGON((31.629638671875 37.17782559332976,33.804931640625 37.17782559332976,33.804931640625 38.28131307922966,31.629638671875 38.28131307922966,31.629638671875 37.17782559332976))'),
('661b8bed-9eda-4a4b-8c02-241fdbbe8af3', 'İzmir',  'SRID=4326;POLYGON((27.081298828125 38.039438891821746,28.4765625 38.039438891821746,28.4765625 38.71123253895224,27.081298828125 38.71123253895224,27.081298828125 38.039438891821746))');

CREATE TABLE IF NOT EXISTS geometry_orders
(
    order_id UUID PRIMARY KEY NOT null,
    geometry_id UUID NOT NULL,
    latitude float NOT NULL,
 	longitude float NOT NULL,
    order_time timestamp NOT NULL,
    CONSTRAINT fk_geometries
      FOREIGN KEY(geometry_id) 
	  REFERENCES geometries(id)
	  on delete cascade,
    order_version int NOT NULL
);


CREATE TABLE IF NOT EXISTS geometry_order_counts
(
    order_count int NOT NULL DEFAULT 0,
    geometry_id UUID NOT null PRIMARY KEY,
    CONSTRAINT fk_geometries
      FOREIGN KEY(geometry_id) 
	  REFERENCES geometries(id)
	  on delete cascade 
);

INSERT INTO geometry_order_counts(geometry_id, order_count) VALUES
('aaeafc70-7cb5-4414-aa42-7d6e760fd065', 0),
('71bb5f1a-f493-4704-8bbc-b2c401983ae7', 0),
('661b8bed-9eda-4a4b-8c02-241fdbbe8af3', 0);

CREATE OR REPLACE FUNCTION update_geometry_order_counts()   
RETURNS TRIGGER AS $$
BEGIN
    UPDATE geometry_order_counts SET order_count = order_count - 1 
    WHERE geometry_id = OLD.geometry_id;
    UPDATE geometry_order_counts SET order_count = order_count + 1
    WHERE geometry_id = NEW.geometry_id;
	RETURN NEW;  
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS update_geometry_order_counts_trigger on geometry_orders;

create TRIGGER update_geometry_order_counts_trigger AFTER
 UPDATE ON geometry_orders FOR EACH ROW EXECUTE PROCEDURE  update_geometry_order_counts();

CREATE OR REPLACE FUNCTION increase_geometry_order_counts()   
RETURNS TRIGGER AS $$
BEGIN
    UPDATE geometry_order_counts SET order_count = order_count + 1
    WHERE geometry_id = NEW.geometry_id;
   RETURN NEW;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS increase_geometry_order_counts_trigger  on geometry_orders;
create TRIGGER increase_geometry_order_counts_trigger AFTER
 INSERT ON geometry_orders FOR EACH ROW EXECUTE PROCEDURE  increase_geometry_order_counts();


CREATE OR REPLACE FUNCTION decrease_geometry_order_counts()   
RETURNS TRIGGER AS $$
BEGIN
    UPDATE geometry_order_counts SET order_count = order_count - 1
    WHERE geometry_id = OLD.geometry_id;
   RETURN OLD;
END;
$$ language 'plpgsql';

DROP TRIGGER IF EXISTS decrease_geometry_order_counts_trigger  on geometry_orders;
create TRIGGER decrease_geometry_order_counts_trigger AFTER
 DELETE ON geometry_orders FOR EACH ROW EXECUTE PROCEDURE  decrease_geometry_order_counts();

