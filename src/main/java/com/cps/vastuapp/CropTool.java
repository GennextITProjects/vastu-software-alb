package com.cps.vastuapp;

import javafx.beans.value.ChangeListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;


public class CropTool {
    private final Image originalImage;


    private final ImageView imageView;
    private final Rectangle cropRectangle;
    
    private ImageView originalImageView;
    private boolean isAreaSelected = false;
    private Image croppedImage;

    public CropTool(Image image) {
        this.originalImage = image;
        this.imageView = new ImageView(image);
        this.imageView.setPreserveRatio(true);
        this.imageView.setSmooth(true);

        // Initialize the crop rectangle
        this.cropRectangle = new Rectangle(200, 200, Color.TRANSPARENT);
        cropRectangle.setStroke(Color.RED);
        cropRectangle.setStrokeWidth(2);
        cropRectangle.getStrokeDashArray().addAll(10.0, 5.0);
        cropRectangle.setManaged(false);
        
    }

    /**
     * Returns the UI of the Crop Tool.
     */
    public BorderPane getUI(Stage parentStage) {
        final BorderPane borderPane = new BorderPane();
        final ScrollPane rootPane = new ScrollPane();
        originalImageView = new ImageView();
        originalImageView.setImage(originalImage);
        final AreaSelection areaSelection = new AreaSelection();
        final Group selectionGroup = new Group();
        final MenuBar menuBar = new MenuBar();

        final Menu menu1 = new Menu("File");
        final Menu menu2 = new Menu("Options");

        final MenuItem clear = new MenuItem("Clear");
        menu1.getItems().addAll(clear);
        clear.setOnAction(event -> {
            clearSelection(selectionGroup);
            originalImageView.setImage(null);
            System.gc();
        });

        final MenuItem select = new MenuItem("Select Area");
        menu2.getItems().add(select);
        select.setOnAction(event -> areaSelection.selectArea(selectionGroup));

        final MenuItem crop = new MenuItem("Crop");
        menu2.getItems().add(crop);
        crop.setOnAction(event -> {
            if (isAreaSelected) {
                cropImage(areaSelection.selectArea(selectionGroup).getBoundsInParent(), originalImageView);
                parentStage.close();
            }
        });

        final MenuItem clearSelectionItem = new MenuItem("Clear Selection");
        menu2.getItems().add(clearSelectionItem);
        clearSelectionItem.setOnAction(event -> clearSelection(selectionGroup));

        selectionGroup.getChildren().add(originalImageView);
        rootPane.setContent(selectionGroup);
        borderPane.setCenter(rootPane);
        menuBar.getMenus().addAll(menu1, menu2);
        borderPane.setTop(menuBar);

        // Update ScrollPane to show scrollbars
        rootPane.setFitToWidth(false);
        rootPane.setFitToHeight(false);
        rootPane.setPannable(true);

        // Set the stage to be maximized
        parentStage.setMaximized(true);

        // Add a listener to adjust ImageView size based on the window size, maintaining aspect ratio
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            double widthRatio = (parentStage.getWidth() - 20) / originalImage.getWidth();
            double heightRatio = (parentStage.getHeight() - 70) / originalImage.getHeight();
            double ratio = Math.min(widthRatio, heightRatio);

            originalImageView.setFitWidth(originalImage.getWidth() * ratio);
            originalImageView.setFitHeight(originalImage.getHeight() * ratio);
        };

        parentStage.widthProperty().addListener(stageSizeListener);
        parentStage.heightProperty().addListener(stageSizeListener);

        changeStageSizeImageDimensions(parentStage, originalImage);
        return borderPane;
    }

    /**
     * Get the cropped image after the tool is closed.
     */
    public Image getCroppedImage() {
        return croppedImage;
    }

    public static Image openInNewStage(Image image) {
        CropTool cropTool = new CropTool(image);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("Crop Image");

        // Get the UI and set it on the scene
        BorderPane root = cropTool.getUI(stage);
        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);

        // Show the stage and wait for the cropping action
        stage.showAndWait();

        // Return the cropped image after closing the crop window
        return cropTool.getCroppedImage();
    }

    private void cropImage(Bounds bounds, ImageView imageView) {

        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), width, height));

        WritableImage wi = new WritableImage(width, height);
        Image croppedImage = imageView.snapshot(parameters, wi);
        this.croppedImage = croppedImage;
        //showCroppedImageNewStage(wi, croppedImage);
    }

//    private void showCroppedImageNewStage(WritableImage wi, Image croppedImage) {
//        final Stage croppedImageStage = new Stage();
//        croppedImageStage.setResizable(true);
//        croppedImageStage.setTitle("Cropped Image");
//        changeStageSizeImageDimensions(croppedImageStage,croppedImage);
//        final BorderPane borderPane = new BorderPane();
//        final MenuBar menuBar = new MenuBar();
//        final Menu menu1 = new Menu("File");
//        final MenuItem save = new MenuItem("Save");
//        save.setOnAction(event -> saveCroppedImage(croppedImageStage,wi));
//        menu1.getItems().add(save);
//        menuBar.getMenus().add(menu1);
//        borderPane.setTop(menuBar);
//        borderPane.setCenter(new ImageView(croppedImage));
//        final Scene scene = new Scene(borderPane);
//        croppedImageStage.setScene(scene);
//    }

    private void saveCroppedImage(Stage stage, WritableImage wi) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialFileName("cats.png");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null)
            return;

        Runnable runnable = () -> {
            BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(wi, null);
            BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(),
                    bufImageARGB.getHeight(), BufferedImage.BITMASK);

            Graphics2D graphics = bufImageRGB.createGraphics();
            graphics.drawImage(bufImageARGB, 0, 0, null);

            try {
                ImageIO.write(bufImageRGB, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                graphics.dispose();
                System.gc();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
        stage.close();
    }

    private void clearSelection(Group group) {
        //deletes everything except for base container layer
        isAreaSelected = false;
        group.getChildren().remove(1,group.getChildren().size());

    }

    private void changeStageSizeImageDimensions(Stage stage, Image image) {
        if (image != null) {
            stage.setMinHeight(250);
            stage.setMinWidth(250);
            stage.setWidth(image.getWidth() + 4);
            stage.setHeight(image.getHeight() + 56);
        }
    }

    private class AreaSelection {

        private Group group;

        private ResizableRectangle selectionRectangle = null;
        private double rectangleStartX;
        private double rectangleStartY;
        private Paint darkAreaColor = Color.color(0,0,0,0.5);

        private ResizableRectangle selectArea(Group group) {
            this.group = group;

            // group.getChildren().get(0) == originalImageView. We assume image view as base container layer
            if (originalImageView != null && originalImage != null) {
                this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
                this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
                this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
            }

            return selectionRectangle;
        }

        EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            rectangleStartX = event.getX();
            rectangleStartY = event.getY();

            clearSelection(group);

            selectionRectangle = new ResizableRectangle(rectangleStartX, rectangleStartY, 0, 0, group);

            darkenOutsideRectangle(selectionRectangle);

        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            double offsetX = event.getX() - rectangleStartX;
            double offsetY = event.getY() - rectangleStartY;

            if (offsetX > 0) {
                if (event.getX() > originalImage.getWidth())
                    selectionRectangle.setWidth(originalImage.getWidth() - rectangleStartX);
                else
                    selectionRectangle.setWidth(offsetX);
            } else {
                if (event.getX() < 0)
                    selectionRectangle.setX(0);
                else
                    selectionRectangle.setX(event.getX());
                selectionRectangle.setWidth(rectangleStartX - selectionRectangle.getX());
            }

            if (offsetY > 0) {
                if (event.getY() > originalImage.getHeight())
                    selectionRectangle.setHeight(originalImage.getHeight() - rectangleStartY);
                else
                    selectionRectangle.setHeight(offsetY);
            } else {
                if (event.getY() < 0)
                    selectionRectangle.setY(0);
                else
                    selectionRectangle.setY(event.getY());
                selectionRectangle.setHeight(rectangleStartY - selectionRectangle.getY());
            }

        };

        EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
            if (selectionRectangle != null)
                isAreaSelected = true;
        };


        private void darkenOutsideRectangle(Rectangle rectangle) {
            Rectangle darkAreaTop = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaLeft = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaRight = new Rectangle(0,0,darkAreaColor);
            Rectangle darkAreaBottom = new Rectangle(0,0,darkAreaColor);

            darkAreaTop.widthProperty().bind(originalImage.widthProperty());
            darkAreaTop.heightProperty().bind(rectangle.yProperty());

            darkAreaLeft.yProperty().bind(rectangle.yProperty());
            darkAreaLeft.widthProperty().bind(rectangle.xProperty());
            darkAreaLeft.heightProperty().bind(rectangle.heightProperty());

            darkAreaRight.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
            darkAreaRight.yProperty().bind(rectangle.yProperty());
            darkAreaRight.widthProperty().bind(originalImage.widthProperty().subtract(
                    rectangle.xProperty().add(rectangle.widthProperty())));
            darkAreaRight.heightProperty().bind(rectangle.heightProperty());

            darkAreaBottom.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()));
            darkAreaBottom.widthProperty().bind(originalImage.widthProperty());
            darkAreaBottom.heightProperty().bind(originalImage.heightProperty().subtract(
                    rectangle.yProperty().add(rectangle.heightProperty())));

            // adding dark area rectangles before the selectionRectangle. So it can't overlap rectangle
            group.getChildren().add(1,darkAreaTop);
            group.getChildren().add(1,darkAreaLeft);
            group.getChildren().add(1,darkAreaBottom);
            group.getChildren().add(1,darkAreaRight);

            // make dark area container layer as well
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaTop.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaLeft.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaRight.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaRight.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaRight.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            darkAreaBottom.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
        }
    }


}
