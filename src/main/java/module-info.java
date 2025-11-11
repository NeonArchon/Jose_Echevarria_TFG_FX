module com.example.tfg_fx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires com.almasb.fxgl.all;
    requires org.hibernate.orm.core;
    requires java.sql;
    requires jakarta.persistence;
    requires java.naming;
    requires jdk.compiler;

    opens com.example.tfg_fx.model.entities to org.hibernate.orm.core;

    opens com.example.tfg_fx to javafx.fxml;
    exports com.example.tfg_fx.app;
    opens com.example.tfg_fx.app to javafx.fxml;
    exports com.example.tfg_fx.controller;
    opens com.example.tfg_fx.controller to javafx.fxml;
}