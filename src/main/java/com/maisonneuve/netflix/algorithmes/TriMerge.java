package com.maisonneuve.netflix.algorithmes;

import com.maisonneuve.netflix.model.Media;
import java.util.Comparator;
import java.util.List;

public class TriMerge implements Algorithme {

    @Override
    public String nom() {
        return "Tri par Fusion (Merge)";
    }

    @Override
    public String complexiteTheorique() {
        return "O(n log n)";
    }

    @Override
    public void trier(List<Media> liste, Comparator<Media> comparator) {
        if (liste == null || liste.size() <= 1) {
            return;
        }

        Media[] tab = liste.toArray(new Media[0]);
        division(tab, 0, tab.length - 1, comparator);

        for (int i = 0; i < tab.length; i++) {
            liste.set(i, tab[i]);
        }
    }

    private void division(Media[] tab, int debut, int fin, Comparator<Media> comparator) {
        if (debut >= fin) return;

        int milieu = (debut + fin) / 2;

        division(tab, debut, milieu, comparator);
        division(tab, milieu + 1, fin, comparator);
        fusion(tab, debut, milieu, fin, comparator);
    }

    private void fusion(Media[] tab, int d, int m, int f, Comparator<Media> comparator) {
        Media[] temp = new Media[f - d + 1];
        int i = d;
        int j = m + 1;
        int k = 0;

        while (i <= m && j <= f) {
            temp[k++] = comparator.compare(tab[i], tab[j]) <= 0 ? tab[i++] : tab[j++];
        }

        while (i <= m) temp[k++] = tab[i++];

        while (j <= f) temp[k++] = tab[j++];

        for (int x = 0; x < temp.length; x++) tab[d + x] = temp[x];
    }
}