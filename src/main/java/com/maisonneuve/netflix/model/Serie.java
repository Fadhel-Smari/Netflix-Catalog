package com.maisonneuve.netflix.model;

public class Serie extends Media {
    private int nbSaisons;
    private int nbEpisodes;
    private StatutSerie statut;

    public Serie() {
        super();
    }

    public Serie(String id, String titre, int annee, double note, Genre genre,
                 String pays, String realisateur, String description, String titreOriginal,
                 int nbSaisons, int nbEpisodes, StatutSerie statut) {
        super(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal);
        this.nbSaisons = nbSaisons;
        this.nbEpisodes = nbEpisodes;
        this.statut = statut;
    }

    public int getNbSaisons() {
        return nbSaisons;
    }

    public void setNbSaisons(int nbSaisons) {
        this.nbSaisons = nbSaisons;
    }

    public int getNbEpisodes() {
        return nbEpisodes;
    }

    public void setNbEpisodes(int nbEpisodes) {
        this.nbEpisodes = nbEpisodes;
    }

    public StatutSerie getStatut() {
        return statut;
    }

    public void setStatut(StatutSerie statut) {
        this.statut = statut;
    }

}