package com.maisonneuve.netflix.algorithmes;

import com.maisonneuve.netflix.model.Media;
import java.util.Comparator;
import java.util.List;

public class TriBulle implements Algorithme {

    @Override
    public String nom() {
        return "Tri Bulle";
    }

    @Override
    public String complexiteTheorique() {
        return "O(n^2)";
    }

    @Override
    public void trier(List<Media> liste, Comparator<Media> comparator) {
        if (liste == null || liste.size() <= 1) {
            return;
        }

        Media[] tab = liste.toArray(new Media[0]);
        int n = tab.length;

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (comparator.compare(tab[j], tab[j + 1]) > 0) {
                    Media temp = tab[j];
                    tab[j] = tab[j + 1];
                    tab[j + 1] = temp;
                }
            }
        }

        for (int i = 0; i < n; i++) {
            liste.set(i, tab[i]);
        }
    }
}