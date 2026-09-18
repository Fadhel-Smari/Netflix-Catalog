package com.maisonneuve.netflix.dao;

import com.maisonneuve.netflix.model.Film;
import com.maisonneuve.netflix.model.Genre;
import com.maisonneuve.netflix.model.Media;
import com.maisonneuve.netflix.model.Serie;
import com.maisonneuve.netflix.model.StatutSerie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MediaDaoPostgres implements MediaDao {

    private static final String SQL_SELECT_ALL =
            "SELECT m.id, m.titre, m.annee, m.note, m.description, m.pays, m.realisateur, m.titre_original, " +
                    "g.nom AS genre_nom, f.duree, s.nb_saisons, s.nb_episodes, s.statut " +
                    "FROM media m " +
                    "JOIN genre g ON m.genre_id = g.id " +
                    "LEFT JOIN film f ON m.id = f.media_id " +
                    "LEFT JOIN serie s ON m.id = s.media_id";

    private static final String SQL_SELECT_BY_ID = SQL_SELECT_ALL + " WHERE m.id = ?";

    private static final String SQL_INSERT_MEDIA =
            "INSERT INTO media (id, titre, annee, note, description, pays, realisateur, titre_original, genre_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM genre WHERE nom = ?))";

    private static final String SQL_INSERT_FILM =
            "INSERT INTO film (media_id, duree) VALUES (?, ?)";

    private static final String SQL_INSERT_SERIE =
            "INSERT INTO serie (media_id, nb_saisons, nb_episodes, statut) VALUES (?, ?, ?, ?)";

    private static final String SQL_UPDATE_MEDIA =
            "UPDATE media SET titre = ?, annee = ?, note = ?, description = ?, pays = ?, realisateur = ?, " +
                    "titre_original = ?, genre_id = (SELECT id FROM genre WHERE nom = ?) WHERE id = ?";

    private static final String SQL_UPDATE_FILM =
            "UPDATE film SET duree = ? WHERE media_id = ?";

    private static final String SQL_UPDATE_SERIE =
            "UPDATE serie SET nb_saisons = ?, nb_episodes = ?, statut = ? WHERE media_id = ?";

    private static final String SQL_DELETE_MEDIA =
            "DELETE FROM media WHERE id = ?";

    @Override
    public List<Media> trouverTous() {
        List<Media> medias = new ArrayList<>();
        try (Connection conn = Connexion.getConnexion();
             PreparedStatement pStm = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = pStm.executeQuery()) {

            while (rs.next()) {
                medias.add(mapResultSetToMedia(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return medias;
    }

    @Override
    public Optional<Media> trouverParId(UUID id) {
        try (Connection conn = Connexion.getConnexion();
             PreparedStatement pStm = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            pStm.setObject(1, id);
            try (ResultSet rs = pStm.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMedia(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Media ajouter(Media media) {
        if (media.getId() == null) {
            media.setId(UUID.randomUUID());
        }

        try (Connection conn = Connexion.getConnexion()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement statementMedia = conn.prepareStatement(SQL_INSERT_MEDIA)) {
                    statementMedia.setObject(1, media.getId());
                    statementMedia.setString(2, media.getTitre());
                    statementMedia.setInt(3, media.getAnnee());
                    statementMedia.setDouble(4, media.getNote());
                    statementMedia.setString(5, media.getDescription());
                    statementMedia.setString(6, media.getPays());
                    statementMedia.setString(7, media.getRealisateur());
                    statementMedia.setString(8, media.getTitreOriginal());
                    statementMedia.setString(9, media.getGenre().name());
                    statementMedia.executeUpdate();
                }

                if (media instanceof Film film) {
                    try (PreparedStatement statementFilm = conn.prepareStatement(SQL_INSERT_FILM)) {
                        statementFilm.setObject(1, film.getId());
                        statementFilm.setInt(2, film.getDuree());
                        statementFilm.executeUpdate();
                    }
                } else if (media instanceof Serie serie) {
                    try (PreparedStatement statementSerie = conn.prepareStatement(SQL_INSERT_SERIE)) {
                        statementSerie.setObject(1, serie.getId());
                        statementSerie.setInt(2, serie.getNbSaisons());
                        statementSerie.setInt(3, serie.getNbEpisodes());
                        statementSerie.setString(4, serie.getStatut().name());
                        statementSerie.executeUpdate();
                    }
                }

                conn.commit();
                return media;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de l'ajout du media", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion lors de l'ajout", e);
        }
    }

    @Override
    public boolean modifier(Media media) {
        try (Connection conn = Connexion.getConnexion()) {
            conn.setAutoCommit(false);
            try {
                int lignesModifiees;
                try (PreparedStatement statementMedia = conn.prepareStatement(SQL_UPDATE_MEDIA)) {
                    statementMedia.setString(1, media.getTitre());
                    statementMedia.setInt(2, media.getAnnee());
                    statementMedia.setDouble(3, media.getNote());
                    statementMedia.setString(4, media.getDescription());
                    statementMedia.setString(5, media.getPays());
                    statementMedia.setString(6, media.getRealisateur());
                    statementMedia.setString(7, media.getTitreOriginal());
                    statementMedia.setString(8, media.getGenre().name());
                    statementMedia.setObject(9, media.getId());
                    lignesModifiees = statementMedia.executeUpdate();
                }

                if (media instanceof Film film) {
                    try (PreparedStatement statementFilm = conn.prepareStatement(SQL_UPDATE_FILM)) {
                        statementFilm.setInt(1, film.getDuree());
                        statementFilm.setObject(2, film.getId());
                        statementFilm.executeUpdate();
                    }
                } else if (media instanceof Serie serie) {
                    try (PreparedStatement statementSerie = conn.prepareStatement(SQL_UPDATE_SERIE)) {
                        statementSerie.setInt(1, serie.getNbSaisons());
                        statementSerie.setInt(2, serie.getNbEpisodes());
                        statementSerie.setString(3, serie.getStatut().name());
                        statementSerie.setObject(4, serie.getId());
                        statementSerie.executeUpdate();
                    }
                }

                conn.commit();
                return lignesModifiees > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Erreur lors de la modification du media", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion lors de la modification", e);
        }
    }

    @Override
    public boolean supprimer(UUID id) {
        try (Connection conn = Connexion.getConnexion();
             PreparedStatement pStm = conn.prepareStatement(SQL_DELETE_MEDIA)) {
            pStm.setObject(1, id);
            int lignesModifiees = pStm.executeUpdate();
            return lignesModifiees > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du media", e);
        }
    }

    private Media mapResultSetToMedia(ResultSet rs) throws SQLException {
        UUID id = rs.getObject("id", UUID.class);
        String titre = rs.getString("titre");
        int annee = rs.getInt("annee");
        double note = rs.getDouble("note");
        String description = rs.getString("description");
        String pays = rs.getString("pays");
        String realisateur = rs.getString("realisateur");
        String titreOriginal = rs.getString("titre_original");
        Genre genre = Genre.valueOf(rs.getString("genre_nom").toUpperCase());

        int duree = rs.getInt("duree");
        if (!rs.wasNull()) {
            return new Film(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal, duree);
        } else {
            int nbSaisons = rs.getInt("nb_saisons");
            int nbEpisodes = rs.getInt("nb_episodes");
            String statutStr = rs.getString("statut");
            StatutSerie statut = (statutStr != null) ? StatutSerie.valueOf(statutStr.toUpperCase()) : StatutSerie.EN_COURS;
            return new Serie(id, titre, annee, note, genre, pays, realisateur, description, titreOriginal, nbSaisons, nbEpisodes, statut);
        }
    }
}