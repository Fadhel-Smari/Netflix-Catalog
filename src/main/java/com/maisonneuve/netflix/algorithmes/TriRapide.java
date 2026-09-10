package com.maisonneuve.netflix.algorithmes;

import com.maisonneuve.netflix.model.Media;
import java.util.Comparator;
import java.util.List;

public class TriRapide implements Algorithme {

    @Override
    public String nom() {
        return "Tri Rapide";
    }

    @Override
    public String complexiteTheorique() {
        return "O(n log n) moy";
    }

    @Override
    public void trier(List<Media> liste, Comparator<Media> comparator) {
        if (liste == null || liste.size() <= 1) {
            return;
        }

        Media[] donnees = liste.toArray(new Media[0]);
        trierRapide(donnees, 0, donnees.length - 1, comparator);

        for (int i = 0; i < donnees.length; i++) {
            liste.set(i, donnees[i]);
        }
    }

    private void trierRapide(Media[] donnees, int debut, int fin, Comparator<Media> comparator) {
        if (debut < fin) {
            int p = partitionner(donnees, debut, fin, comparator);
            trierRapide(donnees, debut, p - 1, comparator);
            trierRapide(donnees, p + 1, fin, comparator);
        }
    }

    private int partitionner(Media[] tab, int d, int f, Comparator<Media> comparator) {
        Media pivot = tab[f];
        int i = d - 1;

        for (int j = d; j < f; j++) {
            if (comparator.compare(tab[j], pivot) <= 0) {
                i++;
                Media temp = tab[i];
                tab[i] = tab[j];
                tab[j] = temp;
            }
        }

        Media temp = tab[i + 1];
        tab[i + 1] = tab[f];
        tab[f] = temp;
        return i + 1;
    }
}