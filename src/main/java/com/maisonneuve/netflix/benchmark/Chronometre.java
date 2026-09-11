package com.maisonneuve.netflix.benchmark;

import com.maisonneuve.netflix.algorithmes.Algorithme;
import com.maisonneuve.netflix.model.Media;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Chronometre {


    /**
     * Mesure le temps d'exécution moyen d'un algorithme de tri sur une liste donnée.
     *
     */
    public static ResultatMesure mesurer(Algorithme algo, List<Media> listeMelangee, int taille, Comparator<Media> comparator, int repetitions) {

        for (int i = 0; i < 3; i++) {
            List<Media> copieWarmup = new ArrayList<>(listeMelangee);
            algo.trier(copieWarmup, comparator);
        }

        long debut = System.nanoTime();

        for (int i = 0; i < repetitions; i++) {
            List<Media> copieATrier = new ArrayList<>(listeMelangee);
            algo.trier(copieATrier, comparator);
        }

        long fin = System.nanoTime();
        long tempsMoyenNs = (fin - debut) / repetitions;

        return new ResultatMesure(algo.nom(), taille, tempsMoyenNs);
    }
}