CREATE TYPE ROLE_TYPE AS ENUM ('owner', 'client');

CREATE TABLE IF NOT EXISTS Users (
	username VARCHAR(100) PRIMARY KEY,
	h_password VARCHAR(255) NOT NULL,
	name VARCHAR(100) NOT NULL,
	surname VARCHAR(100) NOT NULL,
	birth_date DATE CHECK ( birth_date BETWEEN (CURRENT_DATE - INTERVAL '150 years') AND CURRENT_DATE ),
	role ROLE_TYPE NOT NULL,
    latitude DECIMAL(9, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,
	address_id SERIAL REFERENCES Addresses(address_id) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT check_latitude CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT check_longitude CHECK (longitude BETWEEN -180 AND 180)
);
