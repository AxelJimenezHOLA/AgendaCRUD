module org.axel.agenda {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.mariadb.jdbc;

    opens org.axel.agenda to javafx.fxml;
    opens org.axel.agenda.model to javafx.base;
    exports org.axel.agenda;
    exports org.axel.agenda.controller;
    opens org.axel.agenda.controller to javafx.fxml;
}