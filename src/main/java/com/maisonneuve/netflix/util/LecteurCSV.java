package com.maisonneuve.netflix.util;

import com.maisonneuve.netflix.model.Film;
import com.maisonneuve.netflix.model.Genre;
import com.maisonneuve.netflix.model.Media;
import com.maisonneuve.netflix.model.Serie;
import com.maisonneuve.netflix.model.StatutSerie;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LecteurCSV implements SourceDonnees {

    private final String cheminFichier;

    public LecteurCSV() {
        this.cheminFichier = "/data/Netflix.csv";
    }

    @Override
    public List<Media> chargerDonnees() {
        List<Media> listeMedias = new ArrayList<>();

        InputStream csvStream = getClass().getResourceAsStream(cheminFichier);
        if (csvStream == null) {
            System.err.println("Erreur : Impossible de trouver le fichier CSV : " + cheminFichier);
            return listeMedias;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvStream, StandardCharsets.UTF_8))) {
            String ligneMedia;
            boolean premiereLigne = true;

            while ((ligneMedia = reader.readLine()) != null) {
                if (premiereLigne) {
                    premiereLigne = false;
                    continue;
                }
                if (ligneMedia.trim().isEmpty()) {
                    continue;
                }

                Media media = convertirLigneEnMedia(ligneMedia);
                if (media != null) {
                    listeMedias.add(media);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture du fichier CSV : " + e.getMessage());
        }

        return listeMedias;
    }

    private Media convertirLigneEnMedia(String ligne) {
        String[] champs = ligne.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        if (champs.length < 14) {
            return null;
        }

        try {
            String id = nettoyerChamps(champs[0]);
            String type = nettoyerChamps(champs[1]);
            String titre = nettoyerChamps(champs[2]);
            int annee = parseInt(nettoyerChamps(champs[3]));
            double note = parseDouble(nettoyerChamps(champs[4]));
            Genre genre = parseGenre(nettoyerChamps(champs[5]));
            String pays = nettoyerChamps(champs[6]);
            String realisateur = nettoyerChamps(champs[7]);
            String description = nettoyerChamps(champs[12]);
            String titreOriginal = nettoyerChamps(champs[13]);

            if ("FILM".equalsIgnoreCase(type)) {
                int duree = parseInt(nettoyerChamps(champs[8]));
                return new Film(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal, duree);
            } else if ("SERIE".equalsIgnoreCase(type)) {
                int nbSaisons = parseInt(nettoyerChamps(champs[9]));
                int nbEpisodes = parseInt(nettoyerChamps(champs[10]));
                StatutSerie statut = parseStatutSerie(nettoyerChamps(champs[11]));
                return new Serie(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal, nbSaisons, nbEpisodes, statut);
            }
        } catch (Exception e) {
            System.err.println("Erreur de parsing sur la ligne : " + ligne + " | " + e.getMessage());
        }

        return null;
    }

    private String nettoyerChamps(String champ) {
        if (champ == null) return "";
        String valeur = champ.trim();
        if (valeur.length() >= 2) {
            valeur = valeur.replaceAll("^\"|\"$", "");
        }
        return valeur;
    }

    private int parseInt(String valeur) {
        if (valeur == null || valeur.isEmpty()) return 0;
        try {
            return (int) Double.parseDouble(valeur);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseDouble(String valeur) {
        if (valeur == null || valeur.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(valeur);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Genre parseGenre(String valeur) {
        if (valeur == null || valeur.isEmpty()) return null;
        String normalise = valeur.toUpperCase().replace('-', '_').replace(' ', '_');
        try {
            return Genre.valueOf(normalise);
        } catch (Exception e) {
            return null;
        }
    }

    private StatutSerie parseStatutSerie(String valeur) {
        if (valeur == null || valeur.isEmpty()) return null;
        try {
            return StatutSerie.valueOf(valeur.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}