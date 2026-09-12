module com.maisonneuve.netflix {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.maisonneuve.netflix;
    opens com.maisonneuve.netflix to javafx.fxml;

    exports com.maisonneuve.netflix.controller;
    opens com.maisonneuve.netflix.controller to javafx.fxml;

    exports com.maisonneuve.netflix.model;
    opens com.maisonneuve.netflix.model to javafx.base;

    exports com.maisonneuve.netflix.benchmark;
    opens com.maisonneuve.netflix.benchmark to javafx.base;

    exports com.maisonneuve.netflix.service;
    exports com.maisonneuve.netflix.util;
    exports com.maisonneuve.netflix.algorithmes;
}