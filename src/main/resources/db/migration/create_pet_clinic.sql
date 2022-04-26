CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS owner (
    id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    first_name text NOT NULL,
    last_name text NOT NULL,
    phone_number text NOT NULL
);

CREATE TABLE IF NOT EXISTS address (
    owner_id uuid NOT NULL REFERENCES owner(id) ON DELETE CASCADE,
    street text NOT NULL,
    city text NOT NULL,
    state text NOT NULL,
    zip_code text NOT NULL
);
