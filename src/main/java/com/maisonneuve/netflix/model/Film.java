package com.maisonneuve.netflix.model;

import java.util.UUID;

public class Film extends Media {
    private int duree;

    public Film() {
        super();
    }

    public Film(UUID id, String titre, int annee, double note, Genre genre,
                String pays, String realisateur, String description, String titreOriginal, int duree) {
        super(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal);
        this.duree = duree;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

}