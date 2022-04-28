CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS owner (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name text NOT NULL,
    last_name text NOT NULL,
    address text NOT NULL,
    phone_number text NOT NULL
);

CREATE TABLE IF NOT EXISTS pet (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    name text NOT NULL,
    birthdate date NOT NULL,
    species text NOT NULL,
    owner_id uuid NOT NULL REFERENCES owner(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vet {
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name text NOT NULL,
    last_name text NOT NULL,
    specialty test NOT NULL
};

-- Vet data for display list
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Baloo', 'Cardiology');
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Hedwig', 'Oncology');
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Totoro', 'Large Animal Internal Medicine');
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Rajah', 'Dentistry');
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Sid', 'Neurology');
INSERT INTO vet (first_name, last_name, specialty) VALUES ('Dr.', 'Stuart', 'Small Animal Internal Medicine');

