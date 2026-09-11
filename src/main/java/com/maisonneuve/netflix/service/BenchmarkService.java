package com.maisonneuve.netflix.service;

import com.maisonneuve.netflix.algorithmes.Algorithme;
import com.maisonneuve.netflix.benchmark.Chronometre;
import com.maisonneuve.netflix.benchmark.ResultatMesure;
import com.maisonneuve.netflix.model.Media;
import com.maisonneuve.netflix.util.GenerateurDonnees;

import java.util.Comparator;
import java.util.List;

public class BenchmarkService {

    /**
     * Exécute une mesure chronométrée pour un algorithme et une taille de données spécifique.
     */
    public ResultatMesure executerMesure(Algorithme algo, List<Media> sourceOriginale, int n, Comparator<Media> comparator, int repetitions) {

        List<Media> donneesGonflees = GenerateurDonnees.genererDonneesGonflees(sourceOriginale, n);

        return Chronometre.mesurer(algo, donneesGonflees, n, comparator, repetitions);
    }
}