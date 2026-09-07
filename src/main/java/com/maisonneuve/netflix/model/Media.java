package com.maisonneuve.netflix.model;

public abstract class Media {
    private String id;
    private String titre;
    private int annee;
    private double note;
    private Genre genre;
    private String pays;
    private String realisateur;
    private String description;
    private String titreOriginal;

    public Media() {
    }

    public Media(String id, String titre, int annee, double note, Genre genre,
                 String pays, String realisateur, String description, String titreOriginal) {
        this.id = id;
        this.titre = titre;
        this.annee = annee;
        this.note = note;
        this.genre = genre;
        this.pays = pays;
        this.realisateur = realisateur;
        this.description = description;
        this.titreOriginal = titreOriginal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getNote() {
        return note;
    }

    public void setNote(double note) {
        this.note = note;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getRealisateur() {
        return realisateur;
    }

    public void setRealisateur(String realisateur) {
        this.realisateur = realisateur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitreOriginal() {
        return titreOriginal;
    }

    public void setTitreOriginal(String titreOriginal) {
        this.titreOriginal = titreOriginal;
    }
}