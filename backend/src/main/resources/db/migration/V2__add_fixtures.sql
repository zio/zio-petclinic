-- Add vet fixtures
INSERT INTO vet (last_name, specialty)
VALUES ('Dr. Baloo', 'Cardiology'),
       ('Dr. Bartok', 'Oncology'),
       ('Dr. Totoro', 'Large Animal Internal Medicine'),
       ('Dr. Rajah', 'Dentistry'),
       ('Dr. Sid', 'Neurology'),
       ('Dr. Stuart', 'Small Animal Internal Medicine');


-- Add owner fixtures
INSERT INTO owner (first_name, last_name, address, phone, email)
VALUES ('Emily', 'Elizabeth', '1 Birdwell Island, New York, NY', '212-215-1928', 'emily@bigreddog.com'),
       ('Sherlock', 'Holmes', '221B Baker St, London, England, UK', '+44-20-7224-3688', 'sherlock@sherlockholmes.com'),
       ('Fern', 'Arable', 'Arable Farm, Brooklin, ME', '207-711-1899', 'fern@charlottesweb.com'),
       ('Jon', 'Arbuckle', '711 Maple St, Muncie, IN', '812-728-1945', 'jon@garfield.com'),
       ('Elizabeth', 'Hunter', 'Ontario, Canada', '807-511-1918', 'elizabeth@incrediblejourney.com'),
       ('Peter', 'Hunter', 'Ontario, Canada', '807-511-1918', 'peter@incrediblejourney.com'),
       ('Jim', 'Hunter', 'Ontario, Canada', '807-511-1918', 'jim@incrediblejourney.com'),
       ('Harry', 'Potter', '4 Privet Drive, Little Whinging, Surrey, UK', '+44-20-7224-3688', 'harry@hogwarts.edu');


-- Add pet fixtures
INSERT INTO pet (name, birthdate, species, owner_id)
VALUES ('Clifford', '1962-02-14', 'Canine', (SELECT id FROM owner WHERE email = 'emily@bigreddog.com')),
       ('Toby', '1888-04-17', 'Canine', (SELECT id FROM owner WHERE email = 'sherlock@sherlockholmes.com')),
       ('Wilbur', '1952-10-15', 'Suidae', (SELECT id FROM owner WHERE email = 'fern@charlottesweb.com')),
       ('Garfield', '1978-06-19', 'Feline', (SELECT id FROM owner WHERE email = 'jon@garfield.com')),
       ('Tao', '1963-11-20', 'Feline', (SELECT id FROM owner WHERE email = 'elizabeth@incrediblejourney.com')),
       ('Bodger', '1963-11-20', 'Canine', (SELECT id FROM owner WHERE email = 'peter@incrediblejourney.com')),
       ('Luath', '1963-11-20', 'Canine', (SELECT id FROM owner WHERE email = 'jim@incrediblejourney.com')),
       ('Hedwig', '1991-01-01', 'Avia', (SELECT id FROM owner WHERE email = 'harry@hogwarts.edu'));


-- Add visit fixtures
INSERT INTO visit (date, description, vet_id, pet_id)
VALUES ('2001-01-01', 'Check weight', (SELECT id FROM vet WHERE last_name = 'Dr. Totoro'),
        (SELECT id FROM pet WHERE name = 'Clifford')),
       ('2022, 8, 23', 'Have scent detection measured', (SELECT id FROM vet WHERE last_name = 'Dr. Sid'),
        (SELECT id FROM pet WHERE name = 'Toby')),
       ('2022, 7, 1', 'Lasagna infusion', (SELECT id FROM vet WHERE last_name = 'Dr. Stuart'),
        (SELECT id FROM pet WHERE name = 'Garfield')),
       ('2022, 7, 11', 'Monday allergy test', (SELECT id FROM vet WHERE last_name = 'Dr. Rajah'),
        (SELECT id FROM pet WHERE name = 'Garfield')),
       ('2022, 5, 22', 'Immunization', (SELECT id FROM vet WHERE last_name = 'Dr. Baloo'),
        (SELECT id FROM pet WHERE name = 'Tao')),
       ('2022, 5, 22', 'Immunization', (SELECT id FROM vet WHERE last_name = 'Dr. Baloo'),
        (SELECT id FROM pet WHERE name = 'Bodger')),
       ('2022, 5, 22', 'Immunization', (SELECT id FROM vet WHERE last_name = 'Dr. Baloo'),
        (SELECT id FROM pet WHERE name = 'Luath')),
       ('2022, 7, 27', 'Broken wing', (SELECT id FROM vet WHERE last_name = 'Dr. Bartok'),
        (SELECT id FROM pet WHERE name = 'Hedwig'));