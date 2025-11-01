package com.cps.vastuapp.utils;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class AppUtils {

    private static final Image appIcon = new Image(Objects.requireNonNull(AppUtils.class.getResourceAsStream("/icons/Master_Vastu_Logo.jpg")));


    // This method will set the icon for any alert
    public static void setAlertIcon(Alert alert) {
        // Set the icon for the alert dialog window
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(appIcon);
    }
}
