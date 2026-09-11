package com.maisonneuve.netflix.util;

import com.maisonneuve.netflix.model.Media;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GenerateurDonnees {

    private static final Random RANDOM = new Random(42);

    /**
     * Utilitaires pour la génération et la manipulation de jeux de données de test pour Benchmark.
     */
    public static List<Media> genererDonneesGonflees(List<Media> source, int n) {
        if (source == null || source.isEmpty()) return new ArrayList<>();

        List<Media> resultat = new ArrayList<>(n);
        int sourceSize = source.size();

        for (int i = 0; i < n; i++) {
            resultat.add(source.get(i % sourceSize));
        }

        Collections.shuffle(resultat, RANDOM);
        return resultat;
    }
}