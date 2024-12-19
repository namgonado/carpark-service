-- carpark.car_park definition

-- Drop table

-- DROP TABLE carpark.car_park;

CREATE TABLE car_park (
	carpark_number varchar(255) NOT NULL,
	update_datetime timestamp(6) NULL,
	CONSTRAINT car_park_pkey PRIMARY KEY (carpark_number)
);

-- carpark.car_park_info definition

-- Drop table

-- DROP TABLE carpark.car_park_info;

CREATE TABLE car_park_info (
	id bigserial NOT NULL,
	lot_type varchar(255) NULL,
	lots_available int4 NOT NULL,
	total_lots int4 NOT NULL,
	carpark_number varchar(255) NULL,
	CONSTRAINT car_park_info_pkey PRIMARY KEY (id)
);


-- carpark.car_park_info foreign keys

ALTER TABLE car_park_info ADD CONSTRAINT fkllm6ockya2ijk31vg7m2cp19w FOREIGN KEY (carpark_number) REFERENCES car_park(carpark_number);