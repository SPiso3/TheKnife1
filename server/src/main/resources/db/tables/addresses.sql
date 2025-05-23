CREATE TABLE IF NOT EXISTS Addresses (
    address_id SERIAL PRIMARY KEY,
    country VARCHAR(50) NOT NULL,
    city VARCHAR(168) NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number VARCHAR(63) NOT NULL,
    CONSTRAINT unique_addr UNIQUE(country, city, street, house_number)
);