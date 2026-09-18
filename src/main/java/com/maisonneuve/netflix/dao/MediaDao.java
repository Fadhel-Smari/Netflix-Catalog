package com.maisonneuve.netflix.dao;

import com.maisonneuve.netflix.model.Media;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaDao {

    List<Media> trouverTous();

    Optional<Media> trouverParId(UUID id);

    Media ajouter(Media media);

    boolean modifier(Media media);

    boolean supprimer(UUID id);
}