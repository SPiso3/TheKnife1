module it.uninsubria.client {
    requires it.uninsubria.common;
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires java.sql;
    requires java.rmi;

    opens it.uninsubria.controller to javafx.fxml;
    exports it.uninsubria.controller;
    exports it.uninsubria.session;
    opens it.uninsubria.session to javafx.fxml;
}