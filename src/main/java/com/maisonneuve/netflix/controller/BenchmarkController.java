package com.maisonneuve.netflix.controller;

import com.maisonneuve.netflix.algorithmes.*;
import com.maisonneuve.netflix.benchmark.ResultatMesure;
import com.maisonneuve.netflix.model.Media;
import com.maisonneuve.netflix.service.BenchmarkService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BenchmarkController {

    @FXML private ComboBox<String> comboCritere;
    @FXML private Spinner<Integer> spinnerRepetitions;
    @FXML private CheckBox chkBulle, chkMerge, chkRapide;
    @FXML private Button btnLancer, btnReset;
    @FXML private LineChart<Number, Number> graphique;

    private static final int[] TAILLES = {300, 600, 1200, 2400, 4800};

    private List<Media> mediasABenchmarker;
    private final BenchmarkService benchmarkService = new BenchmarkService();

    @FXML
    public void initialize() {
        comboCritere.setItems(FXCollections.observableArrayList("Titre", "Note", "Année"));
        comboCritere.getSelectionModel().selectFirst();

        spinnerRepetitions.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 10));
        graphique.setAnimated(false);

        chkBulle.setSelected(true);
        chkMerge.setSelected(true);
        chkRapide.setSelected(true);
    }

    public void setMedias(List<Media> medias) {
        this.mediasABenchmarker = medias;
    }

    @FXML
    private void lancerBenchmark() {
        if (mediasABenchmarker == null || mediasABenchmarker.isEmpty()) {
            afficherAlert("Aucune donnée chargée à benchmarker.");
            return;
        }

        List<Algorithme> algos = collecterAlgorithmes();
        if (algos.isEmpty()) {
            afficherAlert("Sélectionnez au moins un algorithme !");
            return;
        }

        graphique.getData().clear();
        btnLancer.setDisable(true);
        btnReset.setDisable(true);

        String critere = comboCritere.getValue();
        Comparator<Media> comparator = obtenirComparator(critere);
        int repetitions = spinnerRepetitions.getValue();

        new Thread(() -> {
            try {
                for (Algorithme algo : algos) {
                    XYChart.Series<Number, Number> serie = new XYChart.Series<>();
                    serie.setName(algo.nom());

                    for (int n : TAILLES) {
                        ResultatMesure res = benchmarkService.executerMesure(
                                algo, mediasABenchmarker, n, comparator, repetitions
                        );

                        Platform.runLater(() ->
                                serie.getData().add(new XYChart.Data<>(res.getTaille(), res.getTempsNanoSec()))
                        );
                    }

                    Platform.runLater(() -> graphique.getData().add(serie));
                }
            } finally {
                Platform.runLater(() -> {
                    btnLancer.setDisable(false);
                    btnReset.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    private void reinitialiserBenchmark() {

        graphique.getData().clear();

        comboCritere.getSelectionModel().selectFirst();
        spinnerRepetitions.getValueFactory().setValue(10);

        chkBulle.setSelected(true);
        chkMerge.setSelected(true);
        chkRapide.setSelected(true);
    }

    private List<Algorithme> collecterAlgorithmes() {
        List<Algorithme> algos = new ArrayList<>();
        if (chkBulle.isSelected()) algos.add(new TriBulle());
        if (chkMerge.isSelected()) algos.add(new TriMerge());
        if (chkRapide.isSelected()) algos.add(new TriRapide());
        return algos;
    }

    private Comparator<Media> obtenirComparator(String critere) {
        if ("Note".equals(critere)) {
            return Comparator.comparingDouble(Media::getNote);
        } else if ("Année".equals(critere)) {
            return Comparator.comparingInt(Media::getAnnee);
        } else {
            return Comparator.comparing(Media::getTitre, String.CASE_INSENSITIVE_ORDER);
        }
    }

    private void afficherAlert(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }
}