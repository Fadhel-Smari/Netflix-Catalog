package com.maisonneuve.netflix.controller;

import com.maisonneuve.netflix.algorithmes.*;
import com.maisonneuve.netflix.model.*;
import com.maisonneuve.netflix.service.MediaService;
import com.maisonneuve.netflix.util.LecteurCSV;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.Comparator;
import java.util.List;

public class PrincipalController {

    @FXML private TextField txtRecherche;
    @FXML private ComboBox<String> comboType;
    @FXML private ComboBox<Genre> comboGenre;
    @FXML private ComboBox<String> comboDecennie;
    @FXML private Slider sliderNoteMin;
    @FXML private Label lblNoteMin;
    @FXML private ComboBox<String> comboTri;
    @FXML private ComboBox<Algorithme> comboAlgo;
    @FXML private TableView<Media> tableMedias;
    @FXML private TableColumn<Media, String> colTitre;
    @FXML private TableColumn<Media, String> colType;
    @FXML private TableColumn<Media, Integer> colAnnee;
    @FXML private TableColumn<Media, Double> colNote;
    @FXML private TableColumn<Media, String> colGenre;
    @FXML private TableColumn<Media, String> colPays;
    @FXML private Label lblPage;
    @FXML private Button btnPagePrecedente;
    @FXML private Button btnPageSuivante;
    @FXML private Button btnWatchlist;
    @FXML private Label lblTitreDetail;
    @FXML private Label lblTitreOriginal;
    @FXML private Label lblTypeDetail;
    @FXML private Label lblAnneeDetail;
    @FXML private Label lblGenreDetail;
    @FXML private Label lblRealisateurDetail;
    @FXML private Label lblPaysDetail;
    @FXML private Label lblNoteDetail;
    @FXML private Label lblSpecifique1Nom;
    @FXML private Label lblSpecifique1Valeur;
    @FXML private Label lblSpecifique2Nom;
    @FXML private Label lblSpecifique2Valeur;
    @FXML private TextArea txtDescriptionDetail;
    @FXML private Button btnAjouterWatchlist;

    private MediaService mediaService;
    private final Watchlist watchlist = new Watchlist();
    private List<Media> listeFiltreeEtTriee;
    private int pageActuelle = 1;
    private final int ELEMENTS_PAR_PAGE = 20;
    private boolean vueWatchlistSeule = false;

    @FXML
    public void initialize() {
        mediaService = new MediaService(new LecteurCSV());
        configurerTableau();
        configurerFiltres();
        tableMedias.getSelectionModel().selectedItemProperty().addListener((obs, a, n) -> afficherDetailsMedia(n));
        rafraichirDonnees();
    }

    private void configurerTableau() {
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue() instanceof Film ? "Film" : "Série"));
        colAnnee.setCellValueFactory(new PropertyValueFactory<>("annee"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
        colGenre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getGenre() != null ? c.getValue().getGenre().name() : "N/A"));
        colPays.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPays() != null && !c.getValue().getPays().isEmpty() ? c.getValue().getPays() : "N/A"));
    }

    private void configurerFiltres() {
        comboType.setItems(FXCollections.observableArrayList("Médias", "FILMS", "SERIES"));
        comboType.getSelectionModel().selectFirst();
        comboGenre.getItems().add(null);
        comboGenre.getItems().addAll(Genre.values());
        comboDecennie.setItems(FXCollections.observableArrayList("Décennies", "1990s", "2000s", "2010s", "2020s"));
        comboDecennie.getSelectionModel().selectFirst();

        if (sliderNoteMin != null) {
            sliderNoteMin.setMin(0);
            sliderNoteMin.setMax(10);
            sliderNoteMin.setValue(0);
            sliderNoteMin.setBlockIncrement(0.5);
            sliderNoteMin.valueProperty().addListener((obs, o, n) -> {
                if (lblNoteMin != null) lblNoteMin.setText(String.format("%.1f", n.doubleValue()));
                rafraichirDonnees();
            });
        }

        comboTri.setItems(FXCollections.observableArrayList("Titre (A-Z)", "Note (Décroissant)", "Année (Décroissant)", "Pays (A-Z)"));
        comboTri.getSelectionModel().selectFirst();
        comboAlgo.setItems(FXCollections.observableArrayList(new TriRapide(), new TriMerge(), new TriBulle()));
        comboAlgo.getSelectionModel().selectFirst();
        comboAlgo.setCellFactory(p -> new ListCell<>() {
            @Override
            protected void updateItem(Algorithme item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.nom());
            }
        });

        comboAlgo.setButtonCell(comboAlgo.getCellFactory().call(null));

        txtRecherche.textProperty().addListener((obs, o, n) -> rafraichirDonnees());
        comboType.valueProperty().addListener((obs, o, n) -> rafraichirDonnees());
        comboGenre.valueProperty().addListener((obs, o, n) -> rafraichirDonnees());
        comboDecennie.valueProperty().addListener((obs, o, n) -> rafraichirDonnees());
        comboTri.valueProperty().addListener((obs, o, n) -> rafraichirDonnees());
        comboAlgo.valueProperty().addListener((obs, o, n) -> rafraichirDonnees());
    }

    private void rafraichirDonnees() {
        if (vueWatchlistSeule) {
            listeFiltreeEtTriee = FXCollections.observableArrayList(watchlist.getMedias());
        } else {
            String recherche = txtRecherche.getText();
            String type = "Médias".equals(comboType.getValue()) ? null : comboType.getValue();
            Genre genre = comboGenre.getValue();
            Double noteMin = sliderNoteMin != null ? sliderNoteMin.getValue() : 0.0;
            String decennie = comboDecennie.getValue();
            listeFiltreeEtTriee = mediaService.filtrer(recherche, type, genre, noteMin, decennie);
        }
        appliquerTri();
        pageActuelle = 1;
        mettreAJourPagination();
    }

    private void appliquerTri() {
        Algorithme algo = comboAlgo.getValue();
        String critere = comboTri.getValue();
        Comparator<Media> comparator;

        if ("Note (Décroissant)".equals(critere)) {
            comparator = Comparator.comparingDouble(Media::getNote).reversed();
        } else if ("Année (Décroissant)".equals(critere)) {
            comparator = Comparator.comparingInt(Media::getAnnee).reversed();
        } else if ("Pays (A-Z)".equals(critere)) {
            comparator = Comparator.comparing(m -> (m.getPays() != null ? m.getPays() : "").toLowerCase());
        } else {
            comparator = Comparator.comparing(m -> (m.getTitre().toLowerCase()));
        }

        if (algo != null) mediaService.trier(listeFiltreeEtTriee, algo, comparator);
    }

    private void mettreAJourPagination() {
        int totalPages = mediaService.calculerNombreTotalPages(listeFiltreeEtTriee.size(), ELEMENTS_PAR_PAGE);
        if (pageActuelle > totalPages) pageActuelle = Math.max(1, totalPages);

        List<Media> pageMedias = mediaService.obtenirPage(listeFiltreeEtTriee, pageActuelle, ELEMENTS_PAR_PAGE);
        tableMedias.setItems(FXCollections.observableArrayList(pageMedias));

        lblPage.setText("Page " + pageActuelle + " / " + Math.max(1, totalPages));
        btnPagePrecedente.setDisable(pageActuelle <= 1);
        btnPageSuivante.setDisable(pageActuelle >= totalPages);
    }

    @FXML
    private void pagePrecedente() {
        if (pageActuelle > 1) {
            pageActuelle--;
            mettreAJourPagination();
        }
    }

    @FXML
    private void pageSuivante() {
        if (pageActuelle < mediaService.calculerNombreTotalPages(listeFiltreeEtTriee.size(), ELEMENTS_PAR_PAGE)) {
            pageActuelle++;
            mettreAJourPagination();
        }
    }

    @FXML
    private void reinitialiserFiltres() {
        txtRecherche.clear();
        comboType.getSelectionModel().selectFirst();
        comboGenre.getSelectionModel().clearSelection();
        comboDecennie.getSelectionModel().selectFirst();
        if (sliderNoteMin != null) sliderNoteMin.setValue(0);
        comboTri.getSelectionModel().selectFirst();
        comboAlgo.getSelectionModel().selectFirst();
        vueWatchlistSeule = false;
        btnWatchlist.setText("Ma Watchlist (" + watchlist.size() + ")");
        rafraichirDonnees();
    }

    private void afficherDetailsMedia(Media media) {
        if (media == null) {
            lblTitreDetail.setText("Sélectionnez un média");
            lblTitreOriginal.setText("");
            lblTypeDetail.setText("");
            lblAnneeDetail.setText("");
            lblGenreDetail.setText("");
            lblRealisateurDetail.setText("");
            lblPaysDetail.setText("");
            lblNoteDetail.setText("");
            lblSpecifique1Nom.setText("");
            lblSpecifique1Valeur.setText("");
            lblSpecifique2Nom.setText("");
            lblSpecifique2Valeur.setText("");
            txtDescriptionDetail.clear();
            btnAjouterWatchlist.setDisable(true);
            return;
        }

        btnAjouterWatchlist.setDisable(false);
        lblTitreDetail.setText(media.getTitre());

        boolean aTitreOriginal = media.getTitreOriginal() != null
                && !media.getTitreOriginal().trim().isEmpty()
                && !media.getTitreOriginal().equalsIgnoreCase(media.getTitre());
        lblTitreOriginal.setText(aTitreOriginal ? "(" + media.getTitreOriginal() + ")" : "");
        lblTitreOriginal.setVisible(aTitreOriginal);
        lblTitreOriginal.setManaged(aTitreOriginal);

        lblAnneeDetail.setText(String.valueOf(media.getAnnee()));
        lblGenreDetail.setText(media.getGenre() != null ? media.getGenre().name() : "N/A");
        lblRealisateurDetail.setText(media.getRealisateur() != null ? media.getRealisateur() : "N/A");
        lblPaysDetail.setText(media.getPays() != null ? media.getPays() : "N/A");
        lblNoteDetail.setText(media.getNote() + " / 10");
        txtDescriptionDetail.setText(media.getDescription());

        if (media instanceof Film) {
            Film film = (Film) media;
            lblTypeDetail.setText("Film");
            lblSpecifique1Nom.setText("Durée :");
            lblSpecifique1Valeur.setText(film.getDuree() + " min");
            lblSpecifique2Nom.setText("");
            lblSpecifique2Valeur.setText("");
        } else if (media instanceof Serie) {
            Serie serie = (Serie) media;
            lblTypeDetail.setText("Série");
            lblSpecifique1Nom.setText("Saisons :");
            lblSpecifique1Valeur.setText(serie.getNbSaisons() + " saison(s)");
            lblSpecifique2Nom.setText("Épisodes :");
            lblSpecifique2Valeur.setText(serie.getNbEpisodes() + " épisode(s)");
        }
        mettreAJourBoutonWatchlist(media);
    }

    private void mettreAJourBoutonWatchlist(Media media) {
        btnAjouterWatchlist.setText(watchlist.contient(media) ? "Retirer de la Watchlist" : "Ajouter à la Watchlist");
    }

    @FXML
    private void toggleWatchlistMedia() {
        Media media = tableMedias.getSelectionModel().getSelectedItem();
        if (media == null) return;

        if (watchlist.contient(media)) watchlist.retirer(media);
        else watchlist.ajouter(media);

        btnWatchlist.setText(vueWatchlistSeule ? "Afficher Tout" : "Ma Watchlist (" + watchlist.size() + ")");
        mettreAJourBoutonWatchlist(media);
        if (vueWatchlistSeule) rafraichirDonnees();
    }

    @FXML
    private void toggleWatchlistVue() {
        vueWatchlistSeule = !vueWatchlistSeule;
        btnWatchlist.setText(vueWatchlistSeule ? "Afficher Tout" : "Ma Watchlist (" + watchlist.size() + ")");
        rafraichirDonnees();
    }

    @FXML
    private void ouvrirBenchmark() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/benchmark.fxml"));
            Parent root = loader.load();
            ((BenchmarkController) loader.getController()).setMedias(mediaService.getTousLesMedias());

            Stage stage = new Stage();
            stage.setTitle("Benchmark des Tris");
            stage.initModality(Modality.NONE);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'ouverture du benchmark", e);
        }
    }
}