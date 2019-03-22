CREATE TABLE IF NOT EXISTS vets (
                    id         INTEGER IDENTITY PRIMARY KEY,
                    first_name VARCHAR(30),
                    last_name  VARCHAR(30)
);
CREATE INDEX IF NOT EXISTS vets_last_name
  ON vets (last_name);

CREATE TABLE IF NOT EXISTS specialties (
                           id   INTEGER IDENTITY PRIMARY KEY,
                           name VARCHAR(80)
);
CREATE INDEX IF NOT EXISTS specialties_name
  ON specialties (name);

CREATE TABLE IF NOT EXISTS vet_specialties (
                               vet_id       INTEGER NOT NULL,
                               specialty_id INTEGER NOT NULL,

                               CONSTRAINT fk_vet_specialties_vets,
                                 FOREIGN KEY (vet_id)
                                   REFERENCES vets (id),

                               CONSTRAINT fk_vet_specialties_specialties
                                 FOREIGN KEY (specialty_id)
                                   REFERENCES specialties (id)

);

CREATE TABLE IF NOT EXISTS types (
                     id   INTEGER IDENTITY PRIMARY KEY,
                     name VARCHAR(80)
);
CREATE INDEX IF NOT EXISTS types_name
  ON types (name);

CREATE TABLE IF NOT EXISTS owners (
                      id         INTEGER IDENTITY PRIMARY KEY,
                      first_name VARCHAR(30),
                      last_name  VARCHAR_IGNORECASE(30),
                      address    VARCHAR(255),
                      city       VARCHAR(80),
                      telephone  VARCHAR(20)
);
CREATE INDEX IF NOT EXISTS owners_last_name
  ON owners (last_name);

CREATE TABLE IF NOT EXISTS pets (
                    id         INTEGER IDENTITY PRIMARY KEY,
                    name       VARCHAR(30),
                    birth_date DATE,
                    type_id    INTEGER NOT NULL,
                    owner_id   INTEGER NOT NULL,

                    CONSTRAINT fk_pets_owners
                      FOREIGN KEY (owner_id)
                        REFERENCES owners (id),

                    CONSTRAINT fk_pets_types
                      FOREIGN KEY (type_id)
                        REFERENCES types (id)

);
CREATE INDEX IF NOT EXISTS pets_name
  ON pets (name);

CREATE TABLE IF NOT EXISTS visits (
                      id          INTEGER IDENTITY PRIMARY KEY,
                      pet_id      INTEGER NOT NULL,
                      visit_date  DATE,
                      description VARCHAR(255),

                      CONSTRAINT fk_visits_pets
                        FOREIGN KEY (pet_id)
                          REFERENCES pets (id)
);
CREATE INDEX IF NOT EXISTS visits_pet_id
  ON visits (pet_id);
