package com.maisonneuve.netflix.algorithmes;

import com.maisonneuve.netflix.model.Media;
import java.util.Comparator;
import java.util.List;

public interface Algorithme {

    String nom();

    String complexiteTheorique();

    void trier(List<Media> liste, Comparator<Media> comparator);
}