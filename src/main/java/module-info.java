module com.cps.vastuapplication {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires java.desktop;
    requires javafx.swing;
    requires org.apache.pdfbox;
    requires java.logging;

    opens com.cps.vastuapp to javafx.fxml;
    exports com.cps.vastuapp;
}