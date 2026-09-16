CREATE EXTENSION IF NOT EXISTS "pgcrypto";

DROP TABLE IF EXISTS watchlist CASCADE;
DROP TABLE IF EXISTS film CASCADE;
DROP TABLE IF EXISTS serie CASCADE;
DROP TABLE IF EXISTS media CASCADE;
DROP TABLE IF EXISTS genre CASCADE;

CREATE TABLE genre (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titre VARCHAR(255) NOT NULL,
    annee INT NOT NULL CHECK (annee >= 1900 AND annee <= 2100),
    note NUMERIC(3, 1) CHECK (note >= 0.0 AND note <= 10.0),
    description TEXT,
    pays VARCHAR(100),
    realisateur VARCHAR(150),
    titre_original VARCHAR(255),
    genre_id UUID NOT NULL,
    CONSTRAINT fk_media_genre FOREIGN KEY (genre_id)
       REFERENCES genre(id)
       ON DELETE RESTRICT
);

CREATE TABLE film (
    media_id UUID PRIMARY KEY,
    duree INT NOT NULL CHECK (duree > 0),
    CONSTRAINT fk_film_media FOREIGN KEY (media_id)
      REFERENCES media(id)
      ON DELETE CASCADE
);

CREATE TABLE serie (
    media_id UUID PRIMARY KEY,
    nb_saisons INT NOT NULL CHECK (nb_saisons > 0),
    nb_episodes INT NOT NULL CHECK (nb_episodes > 0),
    statut VARCHAR(30) NOT NULL CHECK (statut IN ('EN_COURS', 'TERMINEE', 'ANNULEE')),
    CONSTRAINT fk_serie_media FOREIGN KEY (media_id)
       REFERENCES media(id)
       ON DELETE CASCADE
);