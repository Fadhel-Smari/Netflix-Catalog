package com.maisonneuve.netflix.controller;

import com.maisonneuve.netflix.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.UUID;

public class FormulaireMediaController {

    @FXML private Label lblTitreFormulaire;
    @FXML private ComboBox<String> comboTypeMedia;
    @FXML private TextField txtTitre;
    @FXML private TextField txtTitreOriginal;
    @FXML private TextField txtAnnee;
    @FXML private TextField txtNote;
    @FXML private ComboBox<Genre> comboGenre;
    @FXML private TextField txtRealisateur;
    @FXML private TextField txtPays;

    @FXML private Label lblDuree;
    @FXML private TextField txtDuree;
    @FXML private Label lblSaisons;
    @FXML private TextField txtSaisons;
    @FXML private Label lblEpisodes;
    @FXML private TextField txtEpisodes;
    @FXML private Label lblStatut;
    @FXML private ComboBox<StatutSerie> comboStatut;
    @FXML private TextArea txtDescription;

    private Media mediaAEditer;
    private boolean confirme = false;

    @FXML
    public void initialize() {
        comboTypeMedia.setItems(FXCollections.observableArrayList("Film", "Série"));
        comboTypeMedia.getSelectionModel().selectFirst();
        comboGenre.setItems(FXCollections.observableArrayList(Genre.values()));
        comboGenre.getSelectionModel().selectFirst();
        comboStatut.setItems(FXCollections.observableArrayList(StatutSerie.values()));
        comboStatut.getSelectionModel().selectFirst();

        comboTypeMedia.valueProperty().addListener((obs, oldV, newV) -> basculerChampsType("Série".equals(newV)));
        basculerChampsType(false);
    }

    private void basculerChampsType(boolean estSerie) {
        lblDuree.setVisible(!estSerie); lblDuree.setManaged(!estSerie);
        txtDuree.setVisible(!estSerie); txtDuree.setManaged(!estSerie);

        lblSaisons.setVisible(estSerie); lblSaisons.setManaged(estSerie);
        txtSaisons.setVisible(estSerie); txtSaisons.setManaged(estSerie);
        lblEpisodes.setVisible(estSerie); lblEpisodes.setManaged(estSerie);
        txtEpisodes.setVisible(estSerie); txtEpisodes.setManaged(estSerie);
        lblStatut.setVisible(estSerie); lblStatut.setManaged(estSerie);
        comboStatut.setVisible(estSerie); comboStatut.setManaged(estSerie);
    }

    public void setMediaPourEdition(Media media) {
        this.mediaAEditer = media;
        lblTitreFormulaire.setText("Modifier le Média");
        comboTypeMedia.setDisable(true);

        txtTitre.setText(media.getTitre());
        txtTitreOriginal.setText(media.getTitreOriginal());
        txtAnnee.setText(String.valueOf(media.getAnnee()));
        txtNote.setText(String.valueOf(media.getNote()));
        comboGenre.getSelectionModel().select(media.getGenre());
        txtRealisateur.setText(media.getRealisateur());
        txtPays.setText(media.getPays());
        txtDescription.setText(media.getDescription());

        if (media instanceof Film film) {
            comboTypeMedia.getSelectionModel().select("Film");
            txtDuree.setText(String.valueOf(film.getDuree()));
        } else if (media instanceof Serie serie) {
            comboTypeMedia.getSelectionModel().select("Série");
            txtSaisons.setText(String.valueOf(serie.getNbSaisons()));
            txtEpisodes.setText(String.valueOf(serie.getNbEpisodes()));
            comboStatut.getSelectionModel().select(serie.getStatut());
        }
    }

    @FXML
    private void sauvegarder() {
        if (!validerSaisies()) return;

        try {
            String titre = txtTitre.getText().trim();
            String titreOriginal = txtTitreOriginal.getText().trim();
            int annee = Integer.parseInt(txtAnnee.getText().trim());
            double note = Double.parseDouble(txtNote.getText().trim());
            Genre genre = comboGenre.getValue();
            String realisateur = txtRealisateur.getText().trim();
            String pays = txtPays.getText().trim();
            String desc = txtDescription.getText().trim();

            if ("Film".equals(comboTypeMedia.getValue())) {
                int duree = Integer.parseInt(txtDuree.getText().trim());
                Film film;
                if (mediaAEditer == null) {
                    film = new Film();
                    film.setId(UUID.randomUUID());
                    mediaAEditer = film;
                } else {
                    film = (Film) mediaAEditer;
                }

                film.setTitre(titre);
                film.setTitreOriginal(titreOriginal);
                film.setAnnee(annee);
                film.setNote(note);
                film.setGenre(genre);
                film.setRealisateur(realisateur);
                film.setPays(pays);
                film.setDescription(desc);
                film.setDuree(duree);

            } else {
                int saisons = Integer.parseInt(txtSaisons.getText().trim());
                int episodes = Integer.parseInt(txtEpisodes.getText().trim());
                StatutSerie statut = comboStatut.getValue();

                Serie serie;
                if (mediaAEditer == null) {
                    serie = new Serie();
                    serie.setId(UUID.randomUUID());
                    mediaAEditer = serie;
                } else {
                    serie = (Serie) mediaAEditer;
                }

                serie.setTitre(titre);
                serie.setTitreOriginal(titreOriginal);
                serie.setAnnee(annee);
                serie.setNote(note);
                serie.setGenre(genre);
                serie.setRealisateur(realisateur);
                serie.setPays(pays);
                serie.setDescription(desc);
                serie.setNbSaisons(saisons);
                serie.setNbEpisodes(episodes);
                serie.setStatut(statut);
            }

            confirme = true;
            fermerFenetre();
        } catch (NumberFormatException e) {
            afficherErreur("Nombre invalide", "Vérifiez que l'année, la note et les durées/épisodes sont des valeurs numériques valides.");
        }
    }

    private boolean validerSaisies() {
        if (txtTitre.getText().trim().isEmpty()) {
            afficherErreur("Champ obligatoire", "Le titre ne peut pas être vide.");
            return false;
        }
        try {
            int annee = Integer.parseInt(txtAnnee.getText().trim());
            if (annee < 1900 || annee > 2100) throw new Exception();
        } catch (Exception e) {
            afficherErreur("Année invalide", "L'année doit être comprise entre 1900 et 2100.");
            return false;
        }
        try {
            double note = Double.parseDouble(txtNote.getText().trim());
            if (note < 0.0 || note > 10.0) throw new Exception();
        } catch (Exception e) {
            afficherErreur("Note invalide", "La note doit être comprise entre 0.0 et 10.0.");
            return false;
        }
        return true;
    }

    private void afficherErreur(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de saisie");
        alert.setHeaderText(titre);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private void annuler() { fermerFenetre(); }
    private void fermerFenetre() { ((Stage) txtTitre.getScene().getWindow()).close(); }
    public boolean estConfirme() { return confirme; }
    public Media getMedia() { return mediaAEditer; }
}