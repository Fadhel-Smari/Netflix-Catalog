package com.maisonneuve.netflix.service;

import com.maisonneuve.netflix.algorithmes.Algorithme;
import com.maisonneuve.netflix.model.Film;
import com.maisonneuve.netflix.model.Genre;
import com.maisonneuve.netflix.model.Media;
import com.maisonneuve.netflix.model.Serie;
import com.maisonneuve.netflix.util.SourceDonnees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MediaService {
    private final List<Media> tousLesMedias;

    public MediaService(SourceDonnees sourceDonnees) {
        this.tousLesMedias = sourceDonnees.chargerDonnees();
    }

    public List<Media> getTousLesMedias() {
        return new ArrayList<>(tousLesMedias);
    }

    public List<Media> filtrer(String rechercheTextuelle,
                               String typeFilter,
                               Genre genreFilter,
                               Double noteMin,
                               String decennie) {
        return tousLesMedias.stream()
                .filter(m -> {
                    if (rechercheTextuelle != null && !rechercheTextuelle.trim().isEmpty()) {
                        String query = rechercheTextuelle.toLowerCase().trim();
                        boolean matchTitre = m.getTitre() != null && m.getTitre().toLowerCase().contains(query);
                        boolean matchRealisateur = m.getRealisateur() != null && m.getRealisateur().toLowerCase().contains(query);
                        if (!matchTitre && !matchRealisateur) return false;
                    }

                    if ("FILMS".equalsIgnoreCase(typeFilter) && !(m instanceof Film)) return false;
                    if ("SERIES".equalsIgnoreCase(typeFilter) && !(m instanceof Serie)) return false;

                    if (genreFilter != null && m.getGenre() != genreFilter) return false;

                    if (noteMin != null && m.getNote() < noteMin) return false;

                    if (decennie != null && !"Toutes".equalsIgnoreCase(decennie)) {
                        int annee = m.getAnnee();
                        switch (decennie) {
                            case "1990s": if (annee < 1990 || annee > 1999) return false; break;
                            case "2000s": if (annee < 2000 || annee > 2009) return false; break;
                            case "2010s": if (annee < 2010 || annee > 2019) return false; break;
                            case "2020s": if (annee < 2020 || annee > 2029) return false; break;
                            default: break;
                        }
                    }

                    return true;
                }).collect(Collectors.toList());
    }

    public void trier(List<Media> liste, Algorithme algo, Comparator<Media> comparator) {
        if (liste == null || algo == null || comparator == null) return;
        algo.trier(liste, comparator);
    }

    public List<Media> obtenirPage(List<Media> liste, int numeroPage, int elementsParPage) {
        if (liste == null || liste.isEmpty() || numeroPage < 1 || elementsParPage < 1) {
            return new ArrayList<>();
        }

        int debut = (numeroPage - 1) * elementsParPage;
        if (debut >= liste.size()) {
            return new ArrayList<>();
        }

        int fin = Math.min(debut + elementsParPage, liste.size());
        return new ArrayList<>(liste.subList(debut, fin));
    }

    public int calculerNombreTotalPages(int totalElements, int elementsParPage) {
        if (elementsParPage <= 0 || totalElements <= 0) return 1;
        return (int) Math.ceil((double) totalElements / elementsParPage);
    }
}