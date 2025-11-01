package com.cps.vastuapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VastuApplication extends Application {

    private static final Logger logger = Logger.getLogger(VastuApplication.class.getName());

    // Resource paths as constants
    private static final String FXML_RESOURCE_PATH = "VastuApplication.fxml";
    private static final String ICON_RESOURCE_PATH = "/icons/Master_Vastu_Logo.jpg";
    //private static final String STYLESHEET_RESOURCE_PATH = "/css/styles.css"; // Uncomment if needed

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML layout
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_RESOURCE_PATH));
            BorderPane root = fxmlLoader.load();

            // Set up the scene
            Scene scene = new Scene(root);
            // scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource(STYLESHEET_RESOURCE_PATH)).toExternalForm()); // Uncomment to add CSS styling

            // Set window properties
            primaryStage.setTitle("Master Vastu");

            // Set application icon
            setApplicationIcon(primaryStage);

            // Position the window on a multi-monitor setup
            positionWindowOnScreen(primaryStage);

            VastuController controller = fxmlLoader.getController();
            primaryStage.setOnCloseRequest(controller::onClose);

            // Set scene and show window
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading application", e);
        }
    }

    /**
     * Sets the application icon for the primary stage.
     * This method ensures that the icon is loaded properly and handles potential errors.
     */
    private void setApplicationIcon(Stage stage) {
        try (InputStream iconStream = getClass().getResourceAsStream(ICON_RESOURCE_PATH)) {
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                logger.warning("Icon resource not found: " + ICON_RESOURCE_PATH);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error loading application icon", e);
        }
    }

    /**
     * Positions the application window on a specific monitor (multi-monitor setups).
     * By default, the application will open on the primary screen.
     */
    private void positionWindowOnScreen(Stage stage) {
        // Get available screens
        var screens = Screen.getScreens();

        // If there's more than one screen, let's place the window on the second monitor
        if (screens.size() > 1) {
            Screen secondScreen = screens.get(1); // Get second screen
            var bounds = secondScreen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
        } else {
            // If only one screen, place it on the primary screen (default)
            Screen primaryScreen = Screen.getPrimary();
            var bounds = primaryScreen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
        }
        stage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
