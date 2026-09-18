package com.maisonneuve.netflix.util;

import com.maisonneuve.netflix.dao.MediaDao;
import com.maisonneuve.netflix.dao.MediaDaoPostgres;
import com.maisonneuve.netflix.model.Media;

import java.util.List;

public class SourceDonneesPostgres implements SourceDonnees {

    private final MediaDao mediaDao;

    public SourceDonneesPostgres() {
        this.mediaDao = new MediaDaoPostgres();
    }

    @Override
    public List<Media> chargerDonnees() {
        return mediaDao.trouverTous();
    }
}