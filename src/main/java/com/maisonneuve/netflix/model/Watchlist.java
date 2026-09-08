package com.maisonneuve.netflix.model;

import java.util.ArrayList;
import java.util.List;

public class Watchlist {
    private final List<Media> medias = new ArrayList<>();

    public List<Media> getMedias() {
        return new ArrayList<>(medias);
    }

    public boolean contient(Media media) {
        return medias.contains(media);
    }

    public boolean ajouter(Media media) {
        if (media == null || contient(media)) {
            return false;
        }
        return medias.add(media);
    }

    public boolean retirer(Media media) {
        return medias.remove(media);
    }

}