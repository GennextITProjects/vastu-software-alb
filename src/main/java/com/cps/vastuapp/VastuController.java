package com.cps.vastuapp;

import com.cps.vastuapp.utils.AppUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

public class VastuController {

  private static final Logger logger = Logger.getLogger(VastuController.class.getName());
  private final Map<String, Map<String, ObservableList<String>>> shapSelection = new HashMap<>();
  public Button exportButton;
  public Button resetButton;
  public Button pointSelectionButtton;
  public Button clearButton;
  public CheckBox colorCheckBox;
  public CheckBox pointBoxCheckBox;
  @FXML private TextField angleInput = new TextField();
  private Boolean isLastOption = false;
  private boolean isPointSelectionInProgress = false;
  int pointCount = 0;
  @FXML private Canvas imageCanvas;
  @FXML private ImageView overlayImageView1; // Overlay for 45 Devtas
  @FXML private Pane imageContainer;
  @FXML private Circle centerMarker; // Blue dot indicating center of the image
  @FXML private Slider zoomSlider;
  @FXML private Button zoomInButton;
  @FXML private Button zoomOutButton;
  @FXML private Label zoomPercentageLabel;
  @FXML private Slider angleSlider;
  @FXML private TextField devtasZoomInput;
  @FXML private Slider devtasZoomSlider;
  // Variables to store initial position
  private double initialX;
  private double initialY;
  private double planeCenterX;
  private double planeCenterY;

  private static final int MAX_SIZE_STACK = 3;
  private final Stack<String> undoStackForImageOverlay = new Stack<>(); // Max size of 3
  private final Stack<String> redoStackForImageOverlay = new Stack<>();

  @FXML private ComboBox<String> primaryDropdown;
  @FXML private ComboBox<String> secondaryDropdown;
  @FXML private ComboBox<String> tertiaryDropdown;
  @FXML private ComboBox<String> selectedPointColourDropdown;
  @FXML private ComboBox<String> selectedPointBoxColourDropdown;
  // TODO dop box  refrence

  @FXML private Slider opacitySlider;
  @FXML private Slider opacityCanvasSlider;
  private String overlayPathBuilder;
  private GraphicsContext gc;
  // List to hold the selected points
  private List<Point> points = new ArrayList<>();
  private final double scale = 1.0;
  private Image importedImage;
  private Image croppedImportedImage;
  private final Rotate imageRotate = new Rotate();
  private final Scale imageScale = new Scale(1, 1);
  private double minX = Double.MAX_VALUE;
  private double maxX = Double.MIN_VALUE;
  private double minY = Double.MAX_VALUE;
  private double maxY = Double.MIN_VALUE;
  Map<String, String> shortNamesForSelection = new HashMap<>();
  Map<String, Color> colorListForSelectedPointColourDropdown = new HashMap<>();
  @FXML private Canvas compassCanvas;
  @FXML private GraphicsContext gco;

  private PauseTransition delay;

  private double initialWidth, initialHeight, initialXIV, initialYIV;
  private Rectangle topLeftResizeHandle,
      topRightResizeHandle,
      bottomLeftResizeHandle,
      bottomRightResizeHandle;
  private Rectangle leftResizeHandle, rightResizeHandle, topResizeHandle, bottomResizeHandle;
  private final ExecutorService executor = Executors.newFixedThreadPool(4);

  private static String compassDirection = "UP";

  private static final double COMPASS_PADDING = 20;
  private static final double NEEDLE_PADDING = 40;
  private static final double LABEL_OFFSET_X = 5;
  private static final double LABEL_OFFSET_Y = 6;
  private static final double CENTER_CIRCLE_RADIUS = 5;
  private Canvas offScreenCanvas;
  // Stack to store the points list for undo functionality
  private Stack<List<Point>> undoStack = new Stack<>();
  // Stack to store the points list for redo functionality
  private Stack<List<Point>> redoStack = new Stack<>();

  // ===== NEW: Stacks for overlay undo/redo =====
  private final Stack<OverlayState> overlayUndoStack = new Stack<>();
  private final Stack<OverlayState> overlayRedoStack = new Stack<>();
  private static final int MAX_UNDO_STACK_SIZE = 20;

  // Overlay inner rectangle mappings for precise scaling
  private final Map<String, double[]> overlayInnerRectangles = new HashMap<>();

  // Map for circular overlay inner circle parameters
  private final Map<String, double[]> overlayInnerCircles = new HashMap<>();

  // Map for triangular overlay parameters
  private final Map<String, double[][]> overlayTriangles = new HashMap<>();

  @FXML
  private void initialize() {
    initializeGraphicsContexts();
    initializeCompass();
    initializeShortNames();
    initializeInputs();
    initializeSliders();
    initializeZoomControls();
    initializeDropdowns();
    addDragFunctionality();
    setupData();
    setupOverlayInnerRectangles();
    setupOverlayInnerCircles();
    setupOverlayTriangles();
    disableUIComponents(true);
    disableColourSelectionDropdown(true);
    initializeResizeHandles();
    removeResizeHandles();
    setupDragAndDrop();

    // ADD THIS TEMPORARY DEBUG CODE
    imageContainer.addEventFilter(
        MouseEvent.MOUSE_PRESSED,
        event -> {
          System.out.println("Clicked on: " + event.getTarget().getClass().getSimpleName());
          System.out.println("  Target: " + event.getTarget());
          System.out.println("  Source: " + event.getSource());
        });
        
    // ===== NEW: Keyboard shortcuts for overlay undo/redo =====
    imageContainer.sceneProperty().addListener((observable, oldScene, newScene) -> {
        if (newScene != null) {
            newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                    handleOverlayUndo();
                    event.consume();
                } else if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                    handleOverlayRedo();
                    event.consume();
                }
            });
        }
    });
  }

  private void disableUIComponents(boolean value) {
    exportButton.setDisable(value);

    angleInput.setDisable(value);
    angleSlider.setDisable(value);

    pointSelectionButtton.setDisable(value);
    clearButton.setDisable(value);

    opacityCanvasSlider.setDisable(value);
    opacitySlider.setDisable(value);

    devtasZoomInput.setDisable(value);
    devtasZoomSlider.setDisable(value);
    zoomOutButton.setDisable(value);
    zoomSlider.setDisable(value);
    zoomInButton.setDisable(value);
    colorCheckBox.setDisable(value);
    pointBoxCheckBox.setDisable(value);
  }

  private void initializeGraphicsContexts() {
    gco = compassCanvas.getGraphicsContext2D();
    gc = imageCanvas.getGraphicsContext2D();
  }

  private void initializeCompass() {
    drawCompass(gco, compassCanvas.getWidth(), compassCanvas.getHeight(), 0, compassDirection);
  }

  private void initializeShortNames() {
    shortNamesForSelection.put("LEFT SIDE NORTH", "LSN");
    shortNamesForSelection.put("RIGHT SIDE NORTH", "RSN");
    shortNamesForSelection.put("UP SIDE NORTH", "USN");
    shortNamesForSelection.put("DOWN SIDE NORTH", "DSN");
    shortNamesForSelection.put("ALL", "ALL");
  }

  private void initializeInputs() {
    angleInput.setPromptText("Enter angle (-45 to +45)");
    angleInput.setPrefWidth(150);
    overlayImageView1.getTransforms().addAll(imageRotate, imageScale);

    // Set up a PauseTransition for delayed validation
    delay = new PauseTransition(Duration.millis(3000)); // 3000ms delay
    delay.setOnFinished(event -> handleAngleInputChange(angleInput.getText()));

    // Add listener for when the input text changes
    angleInput
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // Cancel the previous validation if user is still typing
              delay.stop();
              // Restart the delay countdown
              delay.playFromStart();
            });

    // Add listener for the Enter key press to trigger angle change
    angleInput.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            delay.stop(); // Stop the delay
            handleAngleInputChange(angleInput.getText()); // Apply validation immediately
          }
        });

    angleInput.setTextFormatter(
        createAngleTextFormatter()); // TextFormatter added here to restrict input to valid values.
  }

  private TextFormatter<String> createAngleTextFormatter() {
    UnaryOperator<TextFormatter.Change> filter =
        change -> {
          String newText = change.getControlNewText();
          // Allow input of negative or positive decimal/integer, with max 2 decimal places
          return newText.matches("-?[0-9]*\\.?[0-9]{0,2}?") ? change : null;
        };
    return new TextFormatter<>(filter);
  }

  private void handleAngleInputChange(String newValue) {
    if (newValue.isEmpty()) {
      angleInput.setText("0.0");
      return;
    }

    try {
      double angle = Double.parseDouble(newValue);
      if (angle >= -45 && angle <= 45) {
        angleSlider.setValue(angle);
        applyRotation(overlayImageView1, angle);
      } else {
        showTooltip(angleInput, "Please enter a value between -45 and +45");
      }
    } catch (NumberFormatException e) {
      showTooltip(angleInput, "Invalid input. Please enter a numeric value.");
    }
  }

  private void initializeSliders() {
    // Configure angle slider for -45 to +45 degree range
    angleSlider.setMin(-45.0);
    angleSlider.setMax(45.0);
    angleSlider.setValue(0.0);

    angleSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              // ===== NEW: Save state before rotation =====
              if (overlayImageView1.getImage() != null) {
                  saveOverlayState("Rotation");
              }
              angleInput.setText(String.format("%.1f", newValue.doubleValue()));
              applyRotation(overlayImageView1, newValue.doubleValue());
            });

    bindOpacityProperties();
  }

  private void bindOpacityProperties() {
    if (overlayImageView1 != null) {
      overlayImageView1.opacityProperty().bind(opacitySlider.valueProperty());
    }
    if (imageCanvas != null) {
      imageCanvas.opacityProperty().bind(opacityCanvasSlider.valueProperty());
    }
    
    // ===== NEW: Save state before opacity change =====
    opacitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
        if (overlayImageView1.getImage() != null) {
            saveOverlayState("Opacity change");
        }
    });
  }

  private void initializeZoomControls() {
    zoomPercentageLabel.textProperty().bind(zoomSlider.valueProperty().asString("%.0f%%"));

    zoomSlider.valueProperty().addListener(createZoomListener(imageContainer));
    zoomInButton.setOnAction(event -> zoomSlider.increment());
    zoomOutButton.setOnAction(event -> zoomSlider.decrement());

    // Initialize overlay zoom text field with formatter
    devtasZoomInput.setTextFormatter(createZoomTextFormatter());

    // Bidirectional binding between text field and slider
    devtasZoomSlider
        .valueProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              devtasZoomInput.setText(String.format("%.0f", newValue.doubleValue()));
            });

    // Set up PauseTransition for delayed validation on text input
    PauseTransition devtasZoomDelay = new PauseTransition(Duration.millis(500));
    devtasZoomDelay.setOnFinished(
        event -> {
          // Only validate if not empty, allow user to clear and type new value
          if (!devtasZoomInput.getText().isEmpty()) {
            handleDevtasZoomInputChange(devtasZoomInput.getText());
          }
        });

    devtasZoomInput
        .textProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              devtasZoomDelay.stop();
              // Only start delay if not empty
              if (!newValue.isEmpty()) {
                devtasZoomDelay.playFromStart();
              }
            });

    devtasZoomInput.setOnKeyPressed(
        event -> {
          if (event.getCode() == KeyCode.ENTER) {
            devtasZoomDelay.stop();
            handleDevtasZoomInputChange(devtasZoomInput.getText());
          }
        });

    // Restore to current slider value if field is empty on focus loss
    devtasZoomInput
        .focusedProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              if (!newValue && devtasZoomInput.getText().isEmpty()) {
                devtasZoomInput.setText(String.format("%.0f", devtasZoomSlider.getValue()));
              }
            });

    devtasZoomSlider.valueProperty().addListener(createZoomListener(overlayImageView1));
    imageContainer.addEventFilter(ScrollEvent.SCROLL, createScrollZoomListener(zoomSlider));
  }

  private TextFormatter<String> createZoomTextFormatter() {
    UnaryOperator<TextFormatter.Change> filter =
        change -> {
          String newText = change.getControlNewText();
          // Allow only integer values
          return newText.matches("([0-9]*)?") ? change : null;
        };
    return new TextFormatter<>(filter);
  }

  private void handleDevtasZoomInputChange(String newValue) {
    // Don't auto-fill when empty - allow user to clear and type new value
    if (newValue.isEmpty()) {
      return;
    }

    try {
      int zoom = Integer.parseInt(newValue);
      if (zoom >= 10 && zoom <= 300) {
        devtasZoomSlider.setValue(zoom);
      } else {
        showTooltip(devtasZoomInput, "Please enter a value between 10 and 300");
        // Clamp value to valid range
        if (zoom < 10) {
          devtasZoomInput.setText("10");
          devtasZoomSlider.setValue(10);
        } else {
          devtasZoomInput.setText("300");
          devtasZoomSlider.setValue(300);
        }
      }
    } catch (NumberFormatException e) {
      showTooltip(devtasZoomInput, "Invalid input. Please enter a numeric value.");
      // Restore to current slider value on invalid input
      devtasZoomInput.setText(String.format("%.0f", devtasZoomSlider.getValue()));
    }
  }

  private void initializeResizeHandles() {
    // Create resize handles
    topLeftResizeHandle = createResizeHandle();
    topRightResizeHandle = createResizeHandle();
    bottomLeftResizeHandle = createResizeHandle();
    bottomRightResizeHandle = createResizeHandle();
    leftResizeHandle = createResizeHandle();
    rightResizeHandle = createResizeHandle();
    topResizeHandle = createResizeHandle();
    bottomResizeHandle = createResizeHandle();

    // Add handles to the imageContainer (StackPane)
    imageContainer
        .getChildren()
        .addAll(
            topLeftResizeHandle,
            topRightResizeHandle,
            bottomLeftResizeHandle,
            bottomRightResizeHandle,
            leftResizeHandle,
            rightResizeHandle,
            topResizeHandle,
            bottomResizeHandle);

    // Add resize functionality
    addResizeListener(topLeftResizeHandle);
    addResizeListener(topRightResizeHandle);
    addResizeListener(bottomLeftResizeHandle);
    addResizeListener(bottomRightResizeHandle);
    addResizeListener(leftResizeHandle);
    addResizeListener(rightResizeHandle);
    addResizeListener(topResizeHandle);
    addResizeListener(bottomResizeHandle);

    // Update positions of resize handles
    updateResizeHandles();
  }

  private Rectangle createResizeHandle() {
    Rectangle handle = new Rectangle(5, 5);
    handle.setFill(Color.DARKGREEN);
    handle.setCursor(Cursor.HAND);
    handle.setMouseTransparent(false); // Ensure it captures mouse events
    return handle;
  }

  private void updateResizeHandles() {
    if (topLeftResizeHandle != null
        && topRightResizeHandle != null
        && bottomLeftResizeHandle != null
        && bottomRightResizeHandle != null
        && leftResizeHandle != null
        && rightResizeHandle != null
        && topResizeHandle != null
        && bottomResizeHandle != null) {
      // Get bounds of overlayImageView1 in the parent (Pane inside ScrollPane) coordinates
      Bounds bounds = overlayImageView1.localToParent(overlayImageView1.getBoundsInLocal());

      // Position handles at the corners of overlayImageView1
      topLeftResizeHandle.setLayoutX(bounds.getMinX() - 5);
      topLeftResizeHandle.setLayoutY(bounds.getMinY() - 5);

      topRightResizeHandle.setLayoutX(bounds.getMaxX() - 5);
      topRightResizeHandle.setLayoutY(bounds.getMinY() - 5);

      bottomLeftResizeHandle.setLayoutX(bounds.getMinX() - 5);
      bottomLeftResizeHandle.setLayoutY(bounds.getMaxY() - 5);

      bottomRightResizeHandle.setLayoutX(bounds.getMaxX() - 5);
      bottomRightResizeHandle.setLayoutY(bounds.getMaxY() - 5);

      // Position handles at the sides of overlayImageView1
      leftResizeHandle.setLayoutX(bounds.getMinX() - 5);
      leftResizeHandle.setLayoutY(bounds.getMinY() + bounds.getHeight() / 2 - 5);

      rightResizeHandle.setLayoutX(bounds.getMaxX() - 5);
      rightResizeHandle.setLayoutY(bounds.getMinY() + bounds.getHeight() / 2 - 5);

      topResizeHandle.setLayoutX(bounds.getMinX() + bounds.getWidth() / 2 - 5);
      topResizeHandle.setLayoutY(bounds.getMinY() - 5);

      bottomResizeHandle.setLayoutX(bounds.getMinX() + bounds.getWidth() / 2 - 5);
      bottomResizeHandle.setLayoutY(bounds.getMaxY() - 5);
    } else {
      logger.log(Level.WARNING, "Resize handles are not initialized.");
    }
  }

  private void addResizeListener(Rectangle resizeHandle) {
    resizeHandle.setOnMousePressed(
        event -> {
          // ===== NEW: Save state before resize =====
          if (overlayImageView1.getImage() != null) {
              saveOverlayState("Resize start");
          }
          initialXIV = event.getSceneX();
          initialYIV = event.getSceneY();
          initialWidth = overlayImageView1.getFitWidth();
          initialHeight = overlayImageView1.getFitHeight();
          initialX = overlayImageView1.getLayoutX();
          initialY = overlayImageView1.getLayoutY();
          event.consume();
        });

    resizeHandle.setOnMouseDragged(
        event -> {
          double deltaX = event.getSceneX() - initialXIV;
          double deltaY = event.getSceneY() - initialYIV;

          boolean shiftPressed = event.isShiftDown();
          double aspectRatio = initialWidth / initialHeight;

          double newWidth = initialWidth;
          double newHeight = initialHeight;

          if (resizeHandle == topLeftResizeHandle) {
            if (shiftPressed) {
              double delta = Math.min(deltaX, deltaY);
              newWidth = initialWidth - delta;
              newHeight = newWidth / aspectRatio;
            } else {
              newWidth = initialWidth - deltaX;
              newHeight = initialHeight - deltaY;
            }
          } else if (resizeHandle == topRightResizeHandle) {
            if (shiftPressed) {
              double delta = Math.min(deltaX, -deltaY);
              newWidth = initialWidth + delta;
              newHeight = newWidth / aspectRatio;
            } else {
              newWidth = initialWidth + deltaX;
              newHeight = initialHeight - deltaY;
            }
          } else if (resizeHandle == bottomLeftResizeHandle) {
            if (shiftPressed) {
              // Fixed behavior: invert the calculation for bottom-left handle
              double delta = Math.min(deltaX, -deltaY); // Correct inversion
              newWidth = initialWidth - delta;
              newHeight = newWidth / aspectRatio;
            } else {
              newWidth = initialWidth - deltaX;
              newHeight = initialHeight + deltaY;
            }
          } else if (resizeHandle == bottomRightResizeHandle) {
            if (shiftPressed) {
              double delta = Math.min(deltaX, deltaY);
              newWidth = initialWidth + delta;
              newHeight = newWidth / aspectRatio;
            } else {
              newWidth = initialWidth + deltaX;
              newHeight = initialHeight + deltaY;
            }
          } else if (resizeHandle == leftResizeHandle) {
            newWidth = initialWidth - deltaX;
            if (shiftPressed) {
              newHeight = newWidth / aspectRatio;
            }
          } else if (resizeHandle == rightResizeHandle) {
            newWidth = initialWidth + deltaX;
            if (shiftPressed) {
              newHeight = newWidth / aspectRatio;
            }
          } else if (resizeHandle == topResizeHandle) {
            newHeight = initialHeight - deltaY;
            if (shiftPressed) {
              newWidth = newHeight * aspectRatio;
            }
          } else if (resizeHandle == bottomResizeHandle) {
            newHeight = initialHeight + deltaY;
            if (shiftPressed) {
              newWidth = newHeight * aspectRatio;
            }
          }

          // Center adjustment for all handles
          double centerX = initialX + initialWidth / 2;
          double centerY = initialY + initialHeight / 2;
          double newX = centerX - newWidth / 2;
          double newY = centerY - newHeight / 2;

          // Ensure minimum size constraints
          newWidth = Math.max(10, newWidth);
          newHeight = Math.max(10, newHeight);

          // Prevent the image from disappearing by ensuring dimensions are positive
          if (newWidth > 0 && newHeight > 0) {
            overlayImageView1.setFitWidth(newWidth);
            overlayImageView1.setFitHeight(newHeight);
            //                overlayImageView1.setLayoutX(newX); // TODO Needed to cehck with
            // sanket kya kam me aata he
            //                overlayImageView1.setLayoutY(newY);
          }

          // Update the positions of the resize handles
          updateResizeHandles();
          event.consume();
        });
  }

  private ChangeListener<Number> createZoomListener(Node container) {
    return (observable, oldValue, newValue) -> {
      double scale = newValue.doubleValue() / 100.0;
      container.setScaleX(scale);
      container.setScaleY(scale);
      updateResizeHandles();
    };
  }

  private EventHandler<ScrollEvent> createScrollZoomListener(Slider zoomSlider) {
    return event -> {
      if (event.isControlDown()) {
        double deltaY = event.getDeltaY();
        if (deltaY > 0) {
          zoomSlider.setValue(zoomSlider.getValue() + 5); // Increment by 5
        } else {
          zoomSlider.setValue(zoomSlider.getValue() - 5); // Decrement by 5
        }
        event.consume();
      }
    };
  }

  private void initializeDropdowns() {
    setupDropdown(primaryDropdown, event -> handlePrimarySelection());
    setupDropdown(secondaryDropdown, event -> handleSecondarySelection());
    setupDropdown(tertiaryDropdown, event -> handleTertiarySelection());

    setupColorDropdown(selectedPointColourDropdown, "BLUE");
    setupColorDropdown(selectedPointBoxColourDropdown, "RED");
    setUpShapesDropDown(primaryDropdown, "Select Shape");
    setUpShapesDropDown(secondaryDropdown, "Select Direction");
    setUpShapesDropDown(tertiaryDropdown, "Select Overlay");
  }

  private void setupDropdown(ComboBox<String> dropdown, EventHandler<ActionEvent> handler) {
    dropdown.setOnAction(handler);
  }

  private void setupColorDropdown(ComboBox<String> dropdown, String defaultValue) {
    dropdown.setValue(defaultValue);
    dropdown.setOnAction(
        event -> {
          if (dropdown == selectedPointColourDropdown) {
            drawLines();
          } else if (dropdown == selectedPointBoxColourDropdown) {
            changePointBoxColour();
          }
        });
  }

  private void setUpShapesDropDown(ComboBox<String> dropdown, String defaultValue) {
    dropdown.setValue(defaultValue);
  }

  private void showTooltip(TextField textField, String message) {
    Tooltip tooltip = new Tooltip(message);
    tooltip.setAutoHide(true);
    Tooltip.install(textField, tooltip);
    textField.setTooltip(tooltip);
  }

  private void addDragFunctionality() {
    imageContainer.setPickOnBounds(true);

    imageContainer.setOnMousePressed(
        event -> {
          // ONLY drag container if clicking on canvas or container background
          if (event.getTarget() == imageCanvas || event.getTarget() == imageContainer) {
            initialX = event.getSceneX() - imageContainer.getLayoutX();
            initialY = event.getSceneY() - imageContainer.getLayoutY();
            imageContainer.setCursor(Cursor.MOVE);
            System.out.println("CONTAINER PRESSED");
            event.consume();
          }
        });

    imageContainer.setOnMouseDragged(
        event -> {
          if (imageContainer.getCursor() == Cursor.MOVE) {
            double newX = event.getSceneX() - initialX;
            double newY = event.getSceneY() - initialY;
            imageContainer.setLayoutX(newX);
            imageContainer.setLayoutY(newY);
            System.out.println("CONTAINER DRAGGED");
            event.consume();
          }
        });

    imageContainer.setOnMouseReleased(
        event -> {
          if (imageContainer.getCursor() == Cursor.MOVE) {
            imageContainer.setCursor(Cursor.DEFAULT);
            System.out.println("CONTAINER RELEASED");
            event.consume();
          }
        });
  }

  private void redrawCanvas() {
    if (croppedImportedImage != null) {
      GraphicsContext gc = imageCanvas.getGraphicsContext2D();
      gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
      gc.save();
      gc.scale(scale, scale);
      gc.drawImage(croppedImportedImage, 0, 0);
      gc.restore();
    }
  }

  private String getFileExtension(File file) {
    String name = file.getName();
    int lastIndex = name.lastIndexOf('.');
    return (lastIndex == -1) ? "" : name.substring(lastIndex + 1);
  }

  private Image renderPdfAsImage(File file) throws IOException {
    try (PDDocument document = Loader.loadPDF(file)) {
      PDFRenderer renderer = new PDFRenderer(document);
      BufferedImage bufferedImage =
          renderer.renderImageWithDPI(0, 150); // Render first page at 300 DPI
      return SwingFXUtils.toFXImage(bufferedImage, null);
    }
  }

  //    private Image renderDwgAsImage(File file) {
  //
  //    }

  private Image promptCropImage(Image image) {
    // Display a confirmation dialog to the user
    Alert cropDialog = new Alert(AlertType.CONFIRMATION);
    cropDialog.setTitle("Crop Image");
    cropDialog.setHeaderText("Would you like to crop this image?");
    cropDialog.setContentText(
        "Click OK to proceed with cropping, or Cancel to use the original image.");
    AppUtils.setAlertIcon(cropDialog);
    // Process the user's response
    Optional<ButtonType> result = cropDialog.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      // Launch cropping tool
      Image croppedImage = CropTool.openInNewStage(image); // Ensure the cropped image is returned
      if (croppedImage != null) {
        // If cropping was successful, return the cropped image
        return croppedImage;
      } else {
        showError("Cropping failed or was canceled.");
      }
    }

    // Return the original image if cropping is not performed
    return image;
  }

  @FXML
  private void promptCropImageFileMenu() {
    if (importedImage != null) {
      croppedImportedImage = promptCropImage(importedImage);
      imageCanvas.setWidth(croppedImportedImage.getWidth());
      imageCanvas.setHeight(croppedImportedImage.getHeight());
      resetAll();
      redrawCanvas();
      markCenter();
    } else {
      showAlert(AlertType.ERROR, "Crop", "Import image before Crop.");
    }
  }

  // Import Image
  @FXML
  private void handleImport() {
    FileChooser fileChooser = new FileChooser();
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter(
                "Supported Files", "*.png", "*.jpg", "*.jpeg", "*.pdf", "*.dwg"),
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
            new FileChooser.ExtensionFilter("DWG Files", "*.dwg"));

    File file = fileChooser.showOpenDialog(null);
    if (file != null) {
      String fileExtension = getFileExtension(file);

      // Load image asynchronously
      Task<Image> loadImageTask =
          new Task<>() {
            @Override
            protected Image call() throws Exception {
              Image image = null;
              if (fileExtension.equalsIgnoreCase("png")
                  || fileExtension.equalsIgnoreCase("jpg")
                  || fileExtension.equalsIgnoreCase("jpeg")) {
                // image = new Image(file.toURI().toString(), 2048, 2048, true, true); // Resize if
                // necessary
                image = new Image(file.toURI().toString()); // Resize if necessary
              } else if (fileExtension.equalsIgnoreCase("pdf")) {
                image = renderPdfAsImage(file);
              } else if (fileExtension.equalsIgnoreCase("dwg")) {
                // image = renderDwgAsImage(file);
              }
              return image;
            }
          };

      loadImageTask.setOnSucceeded(
          event -> {
            try {
              importedImage = loadImageTask.get();
              croppedImportedImage = promptCropImage(importedImage);
              disableUIComponents(false);

              if (croppedImportedImage != null) {
                imageCanvas.setWidth(croppedImportedImage.getWidth());
                imageCanvas.setHeight(croppedImportedImage.getHeight());
                double centerXimageCanvas = imageCanvas.getWidth() / 2;
                double centerYimageCanvas = imageCanvas.getHeight() / 2;
                System.out.println(
                    "centerXimageCanvas : "
                        + centerXimageCanvas
                        + " centerYimageCanvas: "
                        + centerYimageCanvas);
              } else {
                croppedImportedImage = importedImage;
                imageCanvas.setWidth(importedImage.getWidth());
                imageCanvas.setHeight(importedImage.getHeight());
              }
              redrawCanvas();
              markCenter();
              resetOverlays();
            } catch (Exception e) {
              logger.log(Level.SEVERE, "Failed to load the selected file", e);
              showError("Failed to load the selected file: " + e.getMessage());
            }
          });

      loadImageTask.setOnFailed(
          event -> {
            Throwable exception = loadImageTask.getException();
            logger.log(Level.SEVERE, "Failed to load the selected file", exception);
            showError("Failed to load the selected file: " + exception.getMessage());
          });

      new Thread(loadImageTask).start();
    }
  }

  // Reset application state
  @FXML
  private void handleReset() {
    // Check if no image is imported
    if (importedImage == null && croppedImportedImage == null) {
      // Show a message indicating that no image is imported
      showError("No image imported. Resetting is not necessary.");
      return;
    }

    // Show confirmation dialog before resetting
    Alert alert =
        new Alert(
            AlertType.CONFIRMATION,
            "Are you sure you want to reset?",
            ButtonType.YES,
            ButtonType.NO);
    AppUtils.setAlertIcon(alert);
    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.YES) {
                // Reset imported image and other related states
                importedImage = null;
                croppedImportedImage = null;
                disableUIComponents(true);
                removeResizeHandles();

                // Reset all other states and UI components
                resetAll();
              }
            });
  }

  private void removeResizeHandles() {
    imageContainer
        .getChildren()
        .removeAll(
            topLeftResizeHandle,
            topRightResizeHandle,
            bottomLeftResizeHandle,
            bottomRightResizeHandle);
    imageContainer
        .getChildren()
        .removeAll(leftResizeHandle, rightResizeHandle, topResizeHandle, bottomResizeHandle);
  }

  private void resetAll() {
    disableColourSelectionDropdown(true);
    selectedPointColourDropdown.setValue("BLUE");
    selectedPointBoxColourDropdown.setValue("RED");
    // devata zooms reset
    devtasZoomInput.setText("100");
    devtasZoomSlider.adjustValue(100.00);

    // main plane zooms reset
    zoomSlider.adjustValue(100.00);

    // overlay image opacity reset
    opacitySlider.adjustValue(1.0);

    // overlay image opacity reset
    opacityCanvasSlider.adjustValue(1.0);

    // Reset the dropdown
    resetComboBox();

    // reset compass
    drawCompass(gco, compassCanvas.getWidth(), compassCanvas.getHeight(), 0, compassDirection);

    // Reset if angle is out of range
    angleInput.setText("0.0");

    // Hide the center marker
    centerMarker.setVisible(false);

    // Reset overlays or bounding box coordinates
    resetOverlays();
    minX = Double.MAX_VALUE;
    maxX = Double.MIN_VALUE;
    minY = Double.MAX_VALUE;
    maxY = Double.MIN_VALUE;

    // Reset image transformations
    imageRotate.setAngle(0); // Reset rotation to 0 degrees
    imageScale.setX(1); // Reset X-axis scaling to 1
    imageScale.setY(1); // Reset Y-axis scaling to 1
    initialX = 0; // Reset image X position to origin
    initialY = 0; // Reset image Y position to origin
    imageRotate.setAngle(0);
    imageScale.setX(1);
    imageScale.setY(1);

    // Clear the canvas
    gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());

    // Reset any interactive flags or variables
    isLastOption = false;
    // Redraw the canvas if needed
    clearPoints();
    redrawCanvas();
    
    // ===== NEW: Clear undo/redo stacks on reset =====
    overlayUndoStack.clear();
    overlayRedoStack.clear();
  }

  /**
   * Extracts the overlay name from the overlay path for inner rectangle mapping lookup.
   *
   * @param overlayPath The full overlay path (e.g.,
   *     "/Directory/Rectangle/USN/GOOD_ENTRIES_BAD_ZONES.png")
   * @return The overlay name (e.g., "GOOD_ENTRIES_BAD_ZONES")
   */
  private String getOverlayNameFromPath(String overlayPath) {
    if (overlayPath == null || overlayPath.isEmpty()) {
      return "";
    }

    // Extract the filename without extension
    String filename = overlayPath.substring(overlayPath.lastIndexOf('/') + 1);
    int lastDotIndex = filename.lastIndexOf('.');
    if (lastDotIndex > 0) {
      return filename.substring(0, lastDotIndex);
    }
    return filename;
  }

  /**
   * Applies enhanced scaling logic that maps the overlay's inner rectangle to the user's selected
   * rectangle.
   *
   * @param image The overlay image to scale
   * @param userRectWidth Width of the user's selected rectangle
   * @param userRectHeight Height of the user's selected rectangle
   * @param innerRect Array containing {left_percentage, top_percentage, width_percentage,
   *     height_percentage}
   */
private void applyInnerRectangleScaling(
    Image image, double userRectWidth, double userRectHeight, double[] innerRect) {

    System.out.println("=== INNER RECTANGLE SCALING (EXACT STRETCH) ===");
    System.out.println("Points count: " + points.size());
    System.out.println("Bounding rect - minX: " + minX + ", minY: " + minY);
    System.out.println("Bounding rect - width: " + userRectWidth + ", height: " + userRectHeight);
    System.out.println("Inner rect percentages: " + Arrays.toString(innerRect));

    // Extract inner rectangle coordinates as percentages
    double innerLeftPercent = innerRect[0];
    double innerTopPercent = innerRect[1];
    double innerWidthPercent = innerRect[2];
    double innerHeightPercent = innerRect[3];

    // Calculate the actual dimensions of the inner rectangle in the original image
    double innerRectWidth = (innerWidthPercent / 100.0) * image.getWidth();
    double innerRectHeight = (innerHeightPercent / 100.0) * image.getHeight();

    // Calculate INDEPENDENT scales for X and Y (exact stretch)
    double scaleX = userRectWidth / innerRectWidth;
    double scaleY = userRectHeight / innerRectHeight;

    System.out.println("scaleX: " + scaleX + ", scaleY: " + scaleY);
    System.out.println("Using independent scales for EXACT stretch (image will distort)");

    // Apply DIFFERENT scaling for width and height (exact stretch)
    overlayImageView1.setPreserveRatio(false);
    overlayImageView1.setFitWidth(image.getWidth() * scaleX);
    overlayImageView1.setFitHeight(image.getHeight() * scaleY);

    // Calculate where the inner rectangle sits - USE THE SAME SCALES consistently
    double innerRectX = (innerLeftPercent / 100.0) * image.getWidth() * scaleX;  // Use scaleX for X position
    double innerRectY = (innerTopPercent / 100.0) * image.getHeight() * scaleY; // Use scaleY for Y position

    // Position the overlay so the inner rectangle aligns with the user's selection
    double overlayX = minX - innerRectX;
    double overlayY = minY - innerRectY;

    // Align the ImageView's top-left corner with the StackPane's top-left corner
    StackPane.setAlignment(overlayImageView1, Pos.TOP_LEFT);
    overlayImageView1.setLayoutX(overlayX);
    overlayImageView1.setLayoutY(overlayY);

    System.out.println("Inner rect size: " + innerRectWidth + " x " + innerRectHeight);
    System.out.println("User rect size: " + userRectWidth + " x " + userRectHeight);
    System.out.println("Overlay position: (" + overlayX + ", " + overlayY + ")");
}

  /**
   * Applies enhanced scaling logic for circular overlays. Maps the inner circle to the user's
   * selected rectangle.
   *
   * @param image The overlay image to scale
   * @param userRectWidth Width of the user's selected rectangle
   * @param userRectHeight Height of the user's selected rectangle
   * @param innerCircle Array containing {centerX%, centerY%, radius%}
   */
  private void applyInnerCircleScaling(
      Image image, double userRectWidth, double userRectHeight, double[] innerCircle) {
    double centerXPercent = innerCircle[0];
    double centerYPercent = innerCircle[1];
    double radiusPercent = innerCircle[2];

    // Calculate actual pixel values in original image
    double innerRadius = (radiusPercent / 100.0) * Math.min(image.getWidth(), image.getHeight());
    double innerCenterX = (centerXPercent / 100.0) * image.getWidth();
    double innerCenterY = (centerYPercent / 100.0) * image.getHeight();

    // Calculate scale needed to make inner circle fit in user's rectangle
    // For a circle, we want it to fit within the smaller dimension of the user's rectangle
    double userRectMinDim = Math.min(userRectWidth, userRectHeight);
    double scale = userRectMinDim / (2 * innerRadius);

    // Apply scaling - preserve ratio to keep circle circular!
    overlayImageView1.setPreserveRatio(true);
    overlayImageView1.setFitWidth(image.getWidth() * scale);
    overlayImageView1.setFitHeight(image.getHeight() * scale);

    // Position so inner circle center aligns with user's rectangle center
    double userCenterX = minX + userRectWidth / 2.0;
    double userCenterY = minY + userRectHeight / 2.0;

    double innerCenterX_scaled = innerCenterX * scale;
    double innerCenterY_scaled = innerCenterY * scale;

    double overlayX = userCenterX - innerCenterX_scaled;
    double overlayY = userCenterY - innerCenterY_scaled;

    StackPane.setAlignment(overlayImageView1, Pos.TOP_LEFT);
    overlayImageView1.setLayoutX(overlayX);
    overlayImageView1.setLayoutY(overlayY);
    System.out.println("Circle Scale: " + scale);
    System.out.println("Inner radius: " + innerRadius);
    System.out.println("User rect min dim: " + userRectMinDim);
  }

  private void setResizeHandlesVisibility(boolean visible) {
    topLeftResizeHandle.setVisible(visible);
    topRightResizeHandle.setVisible(visible);
    bottomLeftResizeHandle.setVisible(visible);
    bottomRightResizeHandle.setVisible(visible);
  }

  @FXML
  private void handleExport() {
    // Check if an image has been imported before proceeding with the export
    if (importedImage == null && croppedImportedImage == null) {
      // If no image is imported, show an error message
      showError("No image has been imported. Please import an image before exporting.");
      return; // Exit the method early
    }

    // Let the user choose the export type
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Export As");
    fileChooser
        .getExtensionFilters()
        .addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    File selectedFile = fileChooser.showSaveDialog(imageContainer.getScene().getWindow());

    // Validate if the user selected a file and if it has a valid extension
    if (selectedFile != null) {
      String fileExtension = getFileExtension(selectedFile);

      try {
        // Export the image or PDF based on the selected file extension
        if (fileExtension.equalsIgnoreCase("png")) {
          exportAsImage(imageContainer, selectedFile);
        } else if (fileExtension.equalsIgnoreCase("pdf")) {
          exportAsPDF(imageContainer, selectedFile);
        } else {
          // If the file extension is not supported, show an error message
          showError("Unsupported file format. Please choose a .png or .pdf file.");
        }

      } catch (Exception e) {
        // Handle any errors that might occur during export
        logger.log(Level.SEVERE, "Failed to export the file", e);
        showError("Failed to export the file: " + e.getMessage());
      }
    } else {
      // Handle the case where the user cancels the file chooser dialog
      System.out.println("Export canceled by the user.");
    }
  }

  private void exportAsImage(Pane imageContainer, File outputFile) {
    // Hide resize handles
    setResizeHandlesVisibility(false);

    // Set up SnapshotParameters for higher resolution
    SnapshotParameters params = new SnapshotParameters();
    params.setTransform(new Scale(2, 2)); // Scale up for higher resolution

    // Take a snapshot of the content
    WritableImage snapshot = imageContainer.snapshot(params, null);

    // Convert WritableImage to BufferedImage
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

    try {
      // Save the BufferedImage to the output file
      ImageIO.write(bufferedImage, "png", outputFile);
      showAlert(
          AlertType.INFORMATION,
          "Export Successful",
          "Image exported successfully to: " + outputFile.getAbsolutePath());
      System.out.println("Exported image successfully: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
      showAlert(AlertType.ERROR, "Export Failed", "Failed to export image: " + e.getMessage());
      System.err.println("Error exporting image: " + e.getMessage());
    } finally {
      // Show resize handles again
      setResizeHandlesVisibility(true);
    }
  }

  private void exportAsPDF(Pane imageContainer, File outputFile) {
    // Hide resize handles
    setResizeHandlesVisibility(false);

    // Set up SnapshotParameters for higher resolution
    SnapshotParameters params = new SnapshotParameters();
    params.setTransform(new Scale(2, 2)); // Scale up for higher resolution

    WritableImage snapshot = imageContainer.snapshot(params, null);
    // Check if an image is loaded
    if (snapshot == null) {
      System.out.println("No image loaded to export.");
      return;
    }

    // Convert JavaFX Image to BufferedImage
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

    // Create PDF document using PDFBox
    try (PDDocument document = new PDDocument()) {
      // Create a new page with dimensions based on the image size
      PDPage page = new PDPage();
      document.addPage(page);

      // Create a PDImageXObject from the BufferedImage
      PDImageXObject pdImage =
          PDImageXObject.createFromFileByContent(
              convertBufferedImageToFile(bufferedImage), document);

      // Get page dimensions
      float pageWidth = (float) snapshot.getWidth();
      float pageHeight = (float) snapshot.getHeight();
      page.setMediaBox(new PDRectangle(pageWidth, pageHeight));

      // Draw image on the page
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        contentStream.drawImage(pdImage, 0, 0, pageWidth, pageHeight);
      }

      // Save PDF to file
      document.save(outputFile.getAbsolutePath());
      showAlert(
          AlertType.INFORMATION,
          "Export Successful",
          "PDF exported successfully to: " + outputFile.getAbsolutePath());
      System.out.println("PDF exported successfully to: " + outputFile.getAbsolutePath());
    } catch (IOException e) {
      logger.log(Level.SEVERE, "Failed to export PDF", e);
    } finally {
      // Show resize handles again
      setResizeHandlesVisibility(true);
    }
  }

  // Helper method to convert BufferedImage to temporary file for PDFBox
  private File convertBufferedImageToFile(BufferedImage bufferedImage) throws IOException {
    File tempFile = File.createTempFile("temp_image", ".png");
    ImageIO.write(bufferedImage, "PNG", tempFile);
    return tempFile;
  }

  private void makeImageViewInteractive(ImageView imageView) {
    // CRITICAL: Ensure overlay captures all mouse events
    imageView.setPickOnBounds(true);

    // Simple drag implementation
    final double[] dragDelta = new double[2];

    imageView.setOnMousePressed(
        event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            // ===== NEW: Save state before dragging =====
            saveOverlayState("Drag start");
            
            dragDelta[0] = event.getX();
            dragDelta[1] = event.getY();
            imageView.setCursor(Cursor.MOVE);
            System.out.println("OVERLAY PRESSED at: " + event.getX() + ", " + event.getY());
            event.consume();
          }
        });

    imageView.setOnMouseDragged(
        event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            double newX = imageView.getLayoutX() + (event.getX() - dragDelta[0]);
            double newY = imageView.getLayoutY() + (event.getY() - dragDelta[1]);

            imageView.setLayoutX(newX);
            imageView.setLayoutY(newY);

            System.out.println("OVERLAY DRAGGED to: " + newX + ", " + newY);
            updateResizeHandles();
            event.consume();
          }
        });

    imageView.setOnMouseReleased(
        event -> {
          if (event.getButton() == MouseButton.PRIMARY) {
            imageView.setCursor(Cursor.HAND);
            System.out.println("OVERLAY RELEASED");
            event.consume();
          }
        });

    // Hover effects
    imageView.setOnMouseEntered(
        event -> {
          imageView.setCursor(Cursor.HAND);
        });

    imageView.setOnMouseExited(
        event -> {
          imageView.setCursor(Cursor.DEFAULT);
        });
  }

  // Apply selected overlays based on checkboxes
  @FXML
  private void handleApply() {
    if (croppedImportedImage == null) {
      showError("Please import an image before applying overlays.");
      return;
    }

    // Validate overlay path
    if (overlayPathBuilder == null || overlayPathBuilder.isBlank()) {
      showError("Overlay Path not found. Please select a valid option.");
      return;
    }

    // Create a background task to load and apply the overlay image
    Task<Image> loadOverlayTask =
        new Task<>() {
          @Override
          protected Image call() throws Exception {
            double downscaleFactor = 0.25;
            String overlayPath = overlayPathBuilder;
            try (InputStream inputStream = getClass().getResourceAsStream(overlayPath)) {
              if (inputStream == null) {
                // TODO handle exception
                // logger.log(Level.SEVERE, "Overlay image not found: " + overlayPath);
                // showError("Overlay image not found: " + e.getMessage());
                throw new FileNotFoundException("Overlay image not found: " + overlayPath);
              }
              return new Image(
                  inputStream, 11812 * downscaleFactor, 17970 * downscaleFactor, true, true);
            }
          }
        };

    // Handle success (on the JavaFX application thread)
    // Handle success (on the JavaFX application thread)
    loadOverlayTask.setOnSucceeded(
        event -> {
          // ===== NEW: Save state before applying new overlay =====
          saveOverlayState("New overlay applied");
          
          Image overlayPathBuilderImage = loadOverlayTask.getValue();
          resetOverlays();

          if (isLastOption) {
            disableColourSelectionDropdown(false);
            drawImageInBoundingBoxUsingImageView(overlayPathBuilderImage);
          } else {
            overlayImageView1.setImage(overlayPathBuilderImage);

            // Set initial size
            overlayImageView1.setFitWidth(overlayImageView1.getFitWidth());
            overlayImageView1.setFitHeight(overlayImageView1.getFitHeight());

            // IMPORTANT: Remove any existing bindings first
            overlayImageView1.layoutXProperty().unbind();
            overlayImageView1.layoutYProperty().unbind();

            // Set initial position manually (not with binding)
            double centerX = (imageCanvas.getWidth() - overlayImageView1.getFitWidth()) / 2;
            double centerY = (imageCanvas.getHeight() - overlayImageView1.getFitHeight()) / 2;
            overlayImageView1.setLayoutX(centerX);
            overlayImageView1.setLayoutY(centerY);

            // Ensure overlay is on top
            overlayImageView1.toFront();

            overlayImageView1.setVisible(true);
            makeImageViewInteractive(overlayImageView1);
          }
          removeResizeHandles();
          initializeResizeHandles();
        });

    // Handle failure (on the JavaFX application thread)
    loadOverlayTask.setOnFailed(
        event -> {
          Throwable exception = loadOverlayTask.getException();
          logger.log(Level.SEVERE, "Failed to load overlay image", exception);
          showError("Failed to load overlay image: " + exception.getMessage());
        });

    // Run the task in a cached thread pool
    executor.submit(loadOverlayTask);
  }

  private void disableColourSelectionDropdown(boolean value) {
    // drawImageInBoundingBox(overlayPathBuilderImage);
    selectedPointColourDropdown.setDisable(value);
    selectedPointBoxColourDropdown.setDisable(value);
    colorCheckBox.setDisable(value);
    pointBoxCheckBox.setDisable(value);
  }

  private void drawImageInBoundingBoxUsingImageView(Image image) {
    // Remove any existing bindings first
    overlayImageView1.layoutXProperty().unbind();
    overlayImageView1.layoutYProperty().unbind();

    if (minX != Double.MAX_VALUE
        && maxX != Double.MIN_VALUE
        && minY != Double.MAX_VALUE
        && maxY != Double.MIN_VALUE) {
      double width = maxX - minX;
      double height = maxY - minY;

      overlayImageView1.setImage(image);
      overlayImageView1.toFront();
      overlayImageView1.setVisible(true);

      // Check if this is a circular overlay (Circle shape with ALL direction)
      String selectedCategory = primaryDropdown.getValue();
      String selectedSubcategory = secondaryDropdown.getValue();
      boolean isCircularOverlay =
          "Circle".equals(selectedCategory) && "ALL".equals(selectedSubcategory);

      // Check if this is a triangular overlay
      boolean isTriangularOverlay = "Triangle".equals(selectedCategory);
      System.out.println(
          "Category: " + selectedCategory + ", isTriangular: " + isTriangularOverlay);

      if (isTriangularOverlay) {
        // Triangle handling
        String overlayName = getOverlayNameFromPath(overlayPathBuilder);
        System.out.println("Triangle overlay name: " + overlayName);

        double[][] triangle = overlayTriangles.get(overlayName);
        System.out.println("Triangle found: " + (triangle != null ? "YES" : "NO"));

        if (triangle != null) {
          System.out.println("Triangle vertices: " + Arrays.deepToString(triangle));
          System.out.println("User rect - width: " + width + ", height: " + height);
          applyTriangleScaling(image, width, height, triangle);
        } else {
          System.out.println("Using FALLBACK for triangle");
          overlayImageView1.setPreserveRatio(false);
          overlayImageView1.setFitWidth(width);
          overlayImageView1.setFitHeight(height);
          overlayImageView1.setLayoutX(minX);
          overlayImageView1.setLayoutY(minY);
        }
      } else if (isCircularOverlay) {
        // Circle handling
        String overlayName = getOverlayNameFromPath(overlayPathBuilder);
        double[] innerCircle = overlayInnerCircles.get(overlayName);

        if (innerCircle != null) {
          applyInnerCircleScaling(image, width, height, innerCircle);
        } else {
          overlayImageView1.setPreserveRatio(false);
          double maxSquareSize = Math.min(width, height);
          overlayImageView1.setFitWidth(maxSquareSize);
          overlayImageView1.setFitHeight(maxSquareSize);

          double centerX = minX + width / 2.0;
          double centerY = minY + height / 2.0;
          double imageCenterX = maxSquareSize / 2.0;
          double imageCenterY = maxSquareSize / 2.0;

          StackPane.setAlignment(overlayImageView1, Pos.CENTER);
          overlayImageView1.setLayoutX(centerX - imageCenterX);
          overlayImageView1.setLayoutY(centerY - imageCenterY);
        }
      } else {
        // Rectangle handling
        String overlayName = getOverlayNameFromPath(overlayPathBuilder);
        double[] innerRect = overlayInnerRectangles.get(overlayName);

        // ===== ADD THIS DEBUG BLOCK =====
        System.out.println("=== RECTANGLE HANDLING ===");
        System.out.println("Overlay name: " + overlayName);
        System.out.println("Points count: " + points.size());
        System.out.println("Bounding rect - minX: " + minX + ", minY: " + minY);
        System.out.println("Bounding rect - maxX: " + maxX + ", maxY: " + maxY);
        System.out.println("Bounding rect - width: " + width + ", height: " + height);
        System.out.println("Inner rect found: " + (innerRect != null ? "YES" : "NO"));
        if (innerRect != null) {
          System.out.println("Inner rect percentages: " + Arrays.toString(innerRect));
        }
        // ===== END DEBUG BLOCK =====

        if (innerRect != null) {
          applyInnerRectangleScaling(image, width, height, innerRect);
        } else {
          overlayImageView1.setPreserveRatio(false);
          overlayImageView1.setFitWidth(width);
          overlayImageView1.setFitHeight(height);
          StackPane.setAlignment(overlayImageView1, Pos.TOP_LEFT);
          overlayImageView1.setLayoutX(minX);
          overlayImageView1.setLayoutY(minY);
        }
      }

      // ADD THIS LINE - Make the overlay draggable
      makeImageViewInteractive(overlayImageView1);

      // Draw the bounding box for reference
      changePointBoxColour();
    }
  }

  private void changePointBoxColour() {
    double width = maxX - minX;
    double height = maxY - minY;

    // Draw the rectangle
    gc.setStroke(
        colorListForSelectedPointColourDropdown.get(
            selectedPointBoxColourDropdown.getValue())); // Set color
    gc.setLineWidth(3); // Set stroke width
    gc.strokeRect(minX, minY, width, height); // Draw rectangle

    // Calculate the center point
    double centerX = minX + width / 2.0;
    double centerY = minY + height / 2.0;

    // Draw a center point marker (e.g., a small filled rectangle or circle)
    gc.setFill(
        colorListForSelectedPointColourDropdown.get(
            selectedPointBoxColourDropdown.getValue())); // Set fill color for the center point
    gc.fillOval(centerX - 3, centerY - 3, 6, 6); // Draw a small circle (radius = 3)

    // Optional: Draw crosshairs for better visibility
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(1);
    gc.strokeLine(centerX - 10, centerY, centerX + 10, centerY); // Horizontal line
    gc.strokeLine(centerX, centerY - 10, centerX, centerY + 10); // Vertical line
  }

  // Reset overlays
  private void resetOverlays() {
    // Remove any existing bindings first - ADD THESE LINES
    overlayImageView1.layoutXProperty().unbind();
    overlayImageView1.layoutYProperty().unbind();

    overlayImageView1.setImage(null);
    overlayImageView1.setVisible(false);
    StackPane.clearConstraints(overlayImageView1);
    overlayImageView1.setTranslateX(0);
    overlayImageView1.setTranslateY(0);
    overlayImageView1.setScaleX(1);
    overlayImageView1.setScaleY(1);
    overlayImageView1.setLayoutX(0); // Also reset layout
    overlayImageView1.setLayoutY(0); // Also reset layout
    removeResizeHandles();
  }

  /**
   * Applies rotation to the given ImageView by the specified angle.
   *
   * @param imageView The ImageView to rotate.
   * @param angle The rotation angle in degrees.
   */
  private void applyRotation(ImageView imageView, double angle) {
    Platform.runLater(
        () -> {
          imageView.setRotate(angle);
          updateResizeHandles();
          drawCompass(
              gco, compassCanvas.getWidth(), compassCanvas.getHeight(), angle, compassDirection);
        });
  }

  /**
   * Handle zooming with mouse scroll, based on the cursor position on the image.
   *
   * @param event The scroll event triggered by the user.
   * @param imageView The ImageView to apply zoom to.
   */
  private void handleZoom(ScrollEvent event, ImageView imageView) {
    double delta = event.getDeltaY() / 1000.0;
    double newScale = imageView.getScaleX() + delta;

    // Limit zoom scale (optional)
    newScale = Math.max(0.1, Math.min(5.0, newScale));

    imageView.setScaleX(newScale);
    imageView.setScaleY(newScale);
  }

  private void markCenter() {
    if (croppedImportedImage != null) {
      planeCenterX = croppedImportedImage.getWidth() / 2;
      planeCenterY = croppedImportedImage.getHeight() / 2;
      centerMarker.setLayoutX(planeCenterX);
      centerMarker.setLayoutY(planeCenterY);
      centerMarker.setVisible(true);
    }
  }

  @FXML
  public void pointSelection() {
    if (isPointSelectionInProgress) {
      showAlert(
          Alert.AlertType.INFORMATION,
          "Point Selection",
          "Point selection is already in progress. Please complete the current selection first.");
      return;
    }
    // Add the event filter for point selection
    if (overlayImageView1.getImage() != null) {
      Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
      alert.setTitle("Clear Existing Selection");
      alert.setHeaderText(null);
      alert.setContentText(
          "An existing selection is detected. Do you want to clear it and start a new selection?");
      AppUtils.setAlertIcon(alert);
      Optional<ButtonType> result = alert.showAndWait();
      if (result.isPresent() && result.get() == ButtonType.OK) {
        clearPoints();
      } else {
        return; // Do not start a new selection if user cancels
      }
    }
    imageCanvas.addEventFilter(MouseEvent.MOUSE_CLICKED, pointSelectionHandler);
    showAlert(
        Alert.AlertType.INFORMATION,
        "Point Selection",
        "You can start selecting points with a left click. Use the right click to finish or end the"
            + " selection.");
    isPointSelectionInProgress = true;
    imageCanvas.setCursor(Cursor.HAND);
    imageCanvas
        .getScene()
        .addEventFilter(
            KeyEvent.KEY_PRESSED,
            event -> {
              if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                undo();
              } else if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                redo();
              }
            });
  }

  // Save the current state to the undo stack
  private void saveStateToUndoStack() {
    undoStack.push(new ArrayList<>(points));
    // Clear the redo stack since we have a new state
    redoStack.clear();
  }

  private final EventHandler<MouseEvent> pointSelectionHandler =
      event -> {
        if (event.getButton() == MouseButton.PRIMARY) {
          saveStateToUndoStack();
          if (isLastOption) {
            System.out.println("this is the last option left");
            pointCount = 1;
            return;
          }
          double x = event.getX();
          double y = event.getY();
          minX = Math.min(minX, x);
          maxX = Math.max(maxX, x);
          minY = Math.min(minY, y);
          maxY = Math.max(maxY, y);

          // Add the new point to the list
          points.add(new Point(x, y));

          // Draw the point (as a small circle)
          drawPoints(x, y);

          // If there are more than one point, draw lines between the points
          drawLines();
        } else if (event.getButton() == MouseButton.SECONDARY) {
          saveStateToUndoStack();
          isLastOption = true;
          if (pointCount == 1) {
            System.out.println("this is the last option right");
            return;
          }
          System.out.print("value of last option" + isLastOption);
          double x = event.getX();
          double y = event.getY();
          minX = Math.min(minX, x);
          maxX = Math.max(maxX, x);
          minY = Math.min(minY, y);
          maxY = Math.max(maxY, y);

          // Add the new point to the list
          points.add(new Point(x, y));

          // Draw the point (as a small circle)
          drawPoints(x, y);

          // If there are more than one point, draw lines between the points
          drawLines();
          isPointSelectionInProgress = false;
          imageCanvas.setCursor(Cursor.DEFAULT);
          showAlert(
              Alert.AlertType.INFORMATION,
              "Selection Complete",
              "You have completed the selection of points.");
        }
      };

  private void drawPoints(double x, double y) {
    gc.setFill(Color.BLUE);
    gc.fillOval(x - 3, y - 3, 11, 11);
  }

  private void drawLines() {
    if (points.size() > 1) {
      gc.setStroke(
          colorListForSelectedPointColourDropdown.get(
              selectedPointColourDropdown.getValue())); // TODO added line point
      gc.setLineWidth(3);
      for (int i = 1; i < points.size(); i++) {
        Point p1 = points.get(i - 1);
        Point p2 = points.get(i);
        gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
      }
    }
  }

  @FXML
  public void clearPoints() {
    // Clear the points list
    points.clear();
    // Remove bindings
    overlayImageView1.layoutXProperty().unbind();
    overlayImageView1.layoutYProperty().unbind();
    disableColourSelectionDropdown(true);
    // Redraw the canvas without any points
    resetOverlays();
    gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    gc.drawImage(croppedImportedImage, 0, 0);
    // Reset bounding box variables
    minX = Double.MAX_VALUE;
    maxX = Double.MIN_VALUE;
    minY = Double.MAX_VALUE;
    maxY = Double.MIN_VALUE;
    isLastOption = false;
    isPointSelectionInProgress = false; // Reset the flag before starting new selection
    imageCanvas.setCursor(Cursor.DEFAULT);
    pointCount = 0;
    removeResizeHandles();
    // Remove the event filter to disable point selection
    imageCanvas.removeEventFilter(MouseEvent.MOUSE_CLICKED, pointSelectionHandler);
  }

  /**
   * Setup overlay inner rectangle mappings for precise scaling. Defines the inner rectangle
   * coordinates for each overlay image as percentages of the image size. Format: {left_percentage,
   * top_percentage, width_percentage, height_percentage}
   */
  private void setupOverlayInnerRectangles() {
    // GOOD_ENTRIES_BAD_ZONES
    overlayInnerRectangles.put("GOOD_ENTRIES_BAD_ZONES", new double[] {19.87, 20.25, 60.52, 60.04});

    // 5_ELEMENTS
    overlayInnerRectangles.put("5_ELEMENTS", new double[] {19.76, 20.08, 60.54, 60.21});

    // 9_ZONES
    overlayInnerRectangles.put("9_ZONES", new double[] {19.79, 20.05, 60.51, 60.24});

    // DEVTAS (same as 5_ELEMENTS)
    overlayInnerRectangles.put("DEVTAS", new double[] {19.76, 20.08, 60.54, 60.21});

    // MANDUKA
    overlayInnerRectangles.put("MANDUKA", new double[] {19.76, 19.68, 60.54, 60.29});

    // MARMA_POINTS
    overlayInnerRectangles.put("MARMA_POINTS", new double[] {19.81, 19.73, 60.42, 60.17});

    // ZONES
    overlayInnerRectangles.put("ZONES", new double[] {19.79, 20.13, 60.51, 60.16});
  }

  /**
   * Setup overlay inner circle mappings for precise scaling of circular overlays. Format:
   * {centerX_percentage, centerY_percentage, radius_percentage}
   */
  private void setupOverlayInnerCircles() {
    // Image: 12718 × 12720
    // Inner circle: Center (6360, 6335), Radius 2995 pixels
    // Calculated: CenterX=50.01%, CenterY=49.80%, Radius=23.55%

    overlayInnerCircles.put("MARMA_POINTS", new double[] {50.01, 49.80, 23.55});
    overlayInnerCircles.put("BAD_ZONES", new double[] {50.01, 49.80, 23.55});
    overlayInnerCircles.put("GOOD_ENTRIES", new double[] {50.01, 49.80, 23.55});
  }

  /**
   * Setup overlay triangle mappings for precise scaling of triangular overlays. Format: array of 3
   * vertices, each {x_percentage, y_percentage}
   */
  private void setupOverlayTriangles() {
    System.out.println("=== Setting up Triangle Mappings ===");

    // BAD_ZONES and GOOD_ENTRIES triangle
    // Image: 12790 × 12143
    // Vertices: Top (6393,2930), Bottom Left (3009,9599), Bottom Right (9778,9600)
    double[][] badZonesTriangle = {
      {49.98, 24.13}, // Top vertex
      {23.53, 79.05}, // Bottom Left
      {76.46, 79.06} // Bottom Right
    };

    // For DEVTAS and ZONES, they likely use the SAME triangle coordinates
    // Based on your earlier data, these overlays have similar inner rectangles
    // Let's use the same triangle coordinates for consistency
    double[][] devtasZonesTriangle = {
      {49.98, 24.13}, // Top vertex
      {23.53, 79.05}, // Bottom Left
      {76.46, 79.06} // Bottom Right
    };

    // Put all triangle mappings
    overlayTriangles.put("BAD_ZONES", badZonesTriangle);
    overlayTriangles.put("GOOD_ENTRIES", badZonesTriangle);
    overlayTriangles.put("DEVTAS", devtasZonesTriangle);
    overlayTriangles.put("ZONES", devtasZonesTriangle);

    System.out.println("Added BAD_ZONES triangle: " + Arrays.deepToString(badZonesTriangle));
    System.out.println("Added GOOD_ENTRIES triangle: " + Arrays.deepToString(badZonesTriangle));
    System.out.println("Added DEVTAS triangle: " + Arrays.deepToString(devtasZonesTriangle));
    System.out.println("Added ZONES triangle: " + Arrays.deepToString(devtasZonesTriangle));
    System.out.println("Total triangles in map: " + overlayTriangles.size());
  }

  /**
   * Applies scaling for triangular overlays. Maps the triangle's bottom base to the user's
   * rectangle bottom edge.
   */
  private void applyTriangleScaling(
      Image image, double userRectWidth, double userRectHeight, double[][] triangle) {
    System.out.println("=== Applying Triangle Scaling ===");

    // Get triangle vertices
    double[] top = triangle[0];
    double[] bottomLeft = triangle[1];
    double[] bottomRight = triangle[2];

    // Calculate triangle dimensions in original image
    double topY = (top[1] / 100.0) * image.getHeight();
    double bottomLeftY = (bottomLeft[1] / 100.0) * image.getHeight();

    double bottomLeftX = (bottomLeft[0] / 100.0) * image.getWidth();
    double bottomRightX = (bottomRight[0] / 100.0) * image.getWidth();

    // Triangle properties
    double triangleHeight = bottomLeftY - topY;
    double triangleBaseWidth = bottomRightX - bottomLeftX;
    double triangleCenterX = bottomLeftX + (triangleBaseWidth / 2.0);

    System.out.println(
        "Triangle - Base width: " + triangleBaseWidth + ", Height: " + triangleHeight);
    System.out.println("Triangle center X: " + triangleCenterX);
    System.out.println("User rect - Width: " + userRectWidth + ", Height: " + userRectHeight);

    // Calculate scale to fit the triangle in user's rectangle
    double scale = userRectWidth / triangleBaseWidth;

    System.out.println("Using scale: " + scale);

    // Apply scaling to entire image
    overlayImageView1.setPreserveRatio(false);
    overlayImageView1.setFitWidth(image.getWidth() * scale);
    overlayImageView1.setFitHeight(image.getHeight() * scale);

    // Calculate where the triangle's bottom edge sits in the scaled image
    double scaledBottomY = bottomLeftY * scale;
    double scaledTriangleCenterX = triangleCenterX * scale;

    // Calculate user rectangle center and bottom
    double userCenterX = minX + (userRectWidth / 2.0);
    double userBottomY = minY + userRectHeight;

    // POSITION FIX: Align triangle's BOTTOM edge with user's rectangle BOTTOM edge
    // The triangle's bottom edge is at scaledBottomY in the image coordinates
    // We want this to align with userBottomY
    double overlayX = userCenterX - scaledTriangleCenterX;
    double overlayY = userBottomY - scaledBottomY; // This aligns bottom with bottom

    System.out.println("User bottom Y: " + userBottomY);
    System.out.println("Scaled bottom Y: " + scaledBottomY);
    System.out.println("Position - overlayX: " + overlayX + ", overlayY: " + overlayY);
    System.out.println("minX: " + minX + ", minY: " + minY);

    StackPane.setAlignment(overlayImageView1, Pos.TOP_LEFT);
    overlayImageView1.setLayoutX(overlayX);
    overlayImageView1.setLayoutY(overlayY);

    System.out.println("=== Triangle Scaling Complete ===");
  }

  private void setupData() {

    // TODO Set Colour in dropdown
    colorListForSelectedPointColourDropdown.put("RED", Color.RED);
    colorListForSelectedPointColourDropdown.put("GREEN", Color.GREEN);
    colorListForSelectedPointColourDropdown.put("BLUE", Color.BLUE);
    colorListForSelectedPointColourDropdown.put("ORANGE", Color.ORANGE);
    colorListForSelectedPointColourDropdown.put("YELLOW", Color.YELLOW);
    colorListForSelectedPointColourDropdown.put("INDIGO", Color.INDIGO);
    colorListForSelectedPointColourDropdown.put("VIOLET", Color.VIOLET);
    selectedPointColourDropdown.getItems().addAll(colorListForSelectedPointColourDropdown.keySet());
    selectedPointBoxColourDropdown
        .getItems()
        .addAll(colorListForSelectedPointColourDropdown.keySet());

    // Example shapSelection
    Map<String, ObservableList<String>> circleData = new HashMap<>();
    circleData.put(
        "ALL", FXCollections.observableArrayList("MARMA_POINTS", "BAD_ZONES", "GOOD_ENTRIES"));

    Map<String, ObservableList<String>> squareData = new HashMap<>();
    squareData.put(
        "DOWN SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    squareData.put(
        "LEFT SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    squareData.put(
        "UP SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    squareData.put(
        "RIGHT SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));

    Map<String, ObservableList<String>> rectangleData = new HashMap<>();
    rectangleData.put(
        "DOWN SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    rectangleData.put(
        "LEFT SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    rectangleData.put(
        "UP SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));
    rectangleData.put(
        "RIGHT SIDE NORTH",
        FXCollections.observableArrayList(
            "5_ELEMENTS",
            "9_ZONES",
            "DEVTAS",
            "MARMA_POINTS",
            "ZONES",
            "MANDUKA",
            "GOOD_ENTRIES_BAD_ZONES"));

    Map<String, ObservableList<String>> triangleData = new HashMap<>();
    triangleData.put(
        "ALL", FXCollections.observableArrayList("BAD_ZONES", "DEVTAS", "GOOD_ENTRIES", "ZONES"));

    shapSelection.put("Circle", circleData);
    shapSelection.put("Square", squareData);
    shapSelection.put("Rectangle", rectangleData);
    shapSelection.put("Triangle", triangleData);

    primaryDropdown.setItems(FXCollections.observableArrayList(shapSelection.keySet()));
  }

  private void handlePrimarySelection() {
    String selectedCategory = primaryDropdown.getValue();
    overlayPathBuilder = "";
    resetOverlays();

    if (selectedCategory != null) {
      // Populate secondary dropdown
      secondaryDropdown.setItems(
          FXCollections.observableArrayList(shapSelection.get(selectedCategory).keySet()));
      secondaryDropdown.setDisable(false);
      System.out.println(selectedCategory);
      // Clear tertiary dropdown
      tertiaryDropdown.setItems(FXCollections.observableArrayList());
      tertiaryDropdown.setDisable(true);
    }
  }

  private void handleSecondarySelection() {
    String selectedCategory = primaryDropdown.getValue();
    String selectedSubcategory = secondaryDropdown.getValue();
    overlayPathBuilder = "";
    resetOverlays();

    if (selectedCategory != null && selectedSubcategory != null) {
      // Populate tertiary dropdown
      secondaryDropdown.setItems(
          FXCollections.observableArrayList(shapSelection.get(selectedCategory).keySet()));
      secondaryDropdown.setDisable(false);
      System.out.println(selectedCategory);
      // Clear tertiary dropdown
      tertiaryDropdown.setItems(shapSelection.get(selectedCategory).get(selectedSubcategory));
      System.out.println(selectedSubcategory);
      switch (selectedSubcategory) {
        case "LEFT SIDE NORTH":
          compassDirection = "LEFT";
          break;
        case "RIGHT SIDE NORTH":
          compassDirection = "RIGHT";
          break;
        case "UP SIDE NORTH":
          compassDirection = "UP";
          break;
        case "DOWN SIDE NORTH":
          compassDirection = "DOWN";
          break;
        case "ALL":
          compassDirection = "UP";
          break;
        default:
          compassDirection = "UP"; // Default value
          break;
      }
      // System.out.println("Compass Direction: " + compassDirection);
      initializeCompass();
      tertiaryDropdown.setDisable(false);
    }
  }

  private void handleTertiarySelection() {
    String selectedCategory = primaryDropdown.getValue();
    String selectedSubcategory = secondaryDropdown.getValue();
    String selectedTertiarycategory = tertiaryDropdown.getValue();

    if (selectedCategory != null
        && selectedSubcategory != null
        && selectedTertiarycategory != null) {
      // Populate tertiary dropdown

      tertiaryDropdown.setItems(shapSelection.get(selectedCategory).get(selectedSubcategory));
      System.out.println(selectedTertiarycategory);
      tertiaryDropdown.setDisable(false);

      overlayPathBuilder =
          "/Directory/"
              + selectedCategory
              + "/"
              + shortNamesForSelection.get(selectedSubcategory)
              + "/"
              + selectedTertiarycategory
              + ".png";
      System.out.println(overlayPathBuilder);

      if (overlayPathBuilder.isEmpty() || overlayPathBuilder.isBlank()) {
        showError("Overlay Path not found. Please select valid option");
      } else {
        handleApply();
      }
    }
  }

  // Show an error message in an alert
  private void showError(String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    AppUtils.setAlertIcon(alert);
    alert.showAndWait();
  }

  private void showAlert(AlertType alertType, String title, String message) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    AppUtils.setAlertIcon(alert);
    alert.showAndWait();
  }

  public void handleExit() {
    Alert alert =
        new Alert(
            AlertType.CONFIRMATION,
            "Are you sure you want to exit?",
            ButtonType.YES,
            ButtonType.NO);
    AppUtils.setAlertIcon(alert);
    alert
        .showAndWait()
        .ifPresent(
            response -> {
              if (response == ButtonType.YES) {
                executor.shutdown();
                Platform.exit();
              }
            });
  }

  @FXML
  public void onClose(WindowEvent windowEvent) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to exit?");
    AppUtils.setAlertIcon(alert);
    alert.setHeaderText(null);
    Optional<ButtonType> result = alert.showAndWait();

    if (result.isPresent() && result.get() == ButtonType.OK) {
      executor.shutdown();
      Platform.exit();
    } else {
      windowEvent.consume();
    }
  }

  private void drawCompass(
      GraphicsContext gc, double width, double height, double angle, String northDirection) {
    // Lazy initialize off-screen canvas
    if (offScreenCanvas == null
        || offScreenCanvas.getWidth() != width
        || offScreenCanvas.getHeight() != height) {
      offScreenCanvas = new Canvas(width, height);
    }

    GraphicsContext offGc = offScreenCanvas.getGraphicsContext2D();

    // Clear the canvas
    offGc.clearRect(0, 0, width, height);

    // Calculate compass dimensions
    double centerX = width / 2;
    double centerY = height / 2;
    double radius = Math.min(centerX, centerY) - COMPASS_PADDING;

    // Draw the compass circle
    offGc.setStroke(Color.DARKSLATEGRAY);
    offGc.setLineWidth(4);
    offGc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

    // Draw cardinal directions and degree labels
    offGc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
    for (int degree = 0; degree < 360; degree += 30) {
      double labelRadius = radius - COMPASS_PADDING;
      double adjustedDegree = adjustDegreeForDirection(degree, northDirection);
      double radian = Math.toRadians(adjustedDegree);

      double x = centerX + labelRadius * Math.cos(radian);
      double y = centerY + labelRadius * Math.sin(radian);

      offGc.setFill(Color.DARKSLATEGRAY);
      if (degree == 0) offGc.fillText("N", x - LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
      else if (degree == 90) offGc.fillText("E", x - LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
      else if (degree == 180) offGc.fillText("S", x - LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
      else if (degree == 270) offGc.fillText("W", x - LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
      else offGc.fillText(degree + "°", x - LABEL_OFFSET_X, y + LABEL_OFFSET_Y);
    }

    // Draw compass needle
    drawNeedle(offGc, centerX, centerY, radius, angle, northDirection);

    // Draw center circle
    offGc.setFill(Color.BLACK);
    offGc.fillOval(
        centerX - CENTER_CIRCLE_RADIUS,
        centerY - CENTER_CIRCLE_RADIUS,
        CENTER_CIRCLE_RADIUS * 2,
        CENTER_CIRCLE_RADIUS * 2);

    // Transfer the off-screen canvas to the main canvas
    WritableImage offScreenImage = offScreenCanvas.snapshot(null, null);
    gc.drawImage(offScreenImage, 0, 0);
  }

  // Adjust degree based on northDirection
  private double adjustDegreeForDirection(int degree, String northDirection) {
    switch (northDirection.toUpperCase()) {
      case "UP":
        return degree - 90;
      case "DOWN":
        return degree + 90;
      case "LEFT":
        return degree + 180;
      case "RIGHT":
        return degree;
      default:
        return degree - 90; // Default to UP
    }
  }

  // Draw the compass needle
  private void drawNeedle(
      GraphicsContext gc,
      double centerX,
      double centerY,
      double radius,
      double angle,
      String northDirection) {
    gc.save(); // Save the current state
    gc.translate(centerX, centerY); // Translate to the center of the compass

    // Adjust rotation for needle based on northDirection
    switch (northDirection.toUpperCase()) {
      case "UP":
        gc.rotate(angle);
        break;
      case "DOWN":
        gc.rotate(angle + 180);
        break;
      case "LEFT":
        gc.rotate(angle - 90);
        break;
      case "RIGHT":
        gc.rotate(angle + 90);
        break;
    }

    // Draw red needle pointing north
    gc.setFill(Color.RED);
    double needleRadius = radius - NEEDLE_PADDING;
    gc.fillPolygon(new double[] {0, -10, 10}, new double[] {-needleRadius, 0, 0}, 3);

    // Draw blue needle pointing south
    gc.setFill(Color.BLUE);
    gc.fillPolygon(new double[] {0, -10, 10}, new double[] {needleRadius, 0, 0}, 3);

    gc.restore(); // Restore the previous state
  }

  private void resetComboBox() {

    primaryDropdown.getSelectionModel().clearSelection();
    secondaryDropdown.getSelectionModel().clearSelection();
    tertiaryDropdown.getSelectionModel().clearSelection();
    setUpShapesDropDown(primaryDropdown, "Circle");
  }

  @FXML
  private void showAbout() {
    Alert aboutAlert = new Alert(AlertType.INFORMATION);
    aboutAlert.setTitle("About MasterVastu");
    aboutAlert.setHeaderText("MasterVastu - Version 1.0");
    ImageView icon =
        new ImageView(
            (new Image(
                Objects.requireNonNull(getClass().getResourceAsStream("/icons/app_icon.jpg")))));
    icon.setFitWidth(52); // Adjust width
    icon.setFitHeight(52); // Adjust height
    icon.setPreserveRatio(true);
    aboutAlert.setGraphic(icon);
    AppUtils.setAlertIcon(aboutAlert);
    aboutAlert.setContentText(
        """
        MasterVastu
        Developed by: CPS
        A comprehensive tool designed for vastu-related visualizations and calculations.

        License: Proprietary (CPS Company)
        For more information, visit: www.cpscompany.com

        Thank you for using MasterVastu!\
        """);
    aboutAlert.showAndWait();
  }

  // Load Image From File (updated to handle different file types)
  private void loadImageFromFile(File file) {
    String fileExtension = getFileExtension(file);

    Task<Image> loadImageTask =
        new Task<>() {
          @Override
          protected Image call() throws Exception {
            Image image = null;
            if (fileExtension.equalsIgnoreCase("png")
                || fileExtension.equalsIgnoreCase("jpg")
                || fileExtension.equalsIgnoreCase("jpeg")) {
              // image = new Image(file.toURI().toString(), 2048, 2048, true, true);
              image = new Image(file.toURI().toString());
            } else if (fileExtension.equalsIgnoreCase("pdf")) {
              image = renderPdfAsImage(file);
            } else if (fileExtension.equalsIgnoreCase("dwg")) {
              Platform.runLater(
                  () ->
                      showAlert(
                          Alert.AlertType.INFORMATION,
                          "Information",
                          "DWG files are not currently supported."));
            }
            return image;
          }
        };

    loadImageTask.setOnSucceeded(
        event -> {
          try {
            importedImage = loadImageTask.get();
            croppedImportedImage = promptCropImage(importedImage);
            disableUIComponents(false);

            if (croppedImportedImage != null) {
              imageCanvas.setWidth(croppedImportedImage.getWidth());
              imageCanvas.setHeight(croppedImportedImage.getHeight());
            } else {
              croppedImportedImage = importedImage;
              imageCanvas.setWidth(importedImage.getWidth());
              imageCanvas.setHeight(importedImage.getHeight());
            }
            redrawCanvas();
            markCenter();
            resetOverlays();
          } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load the selected file", e);
            showError("Failed to load the selected file: " + e.getMessage());
          }
        });

    loadImageTask.setOnFailed(
        event -> {
          Throwable exception = loadImageTask.getException();
          logger.log(Level.SEVERE, "Failed to load the selected file", exception);
          showError("Failed to load the selected file: " + exception.getMessage());
        });

    executor.submit(loadImageTask);
  }

  // Drag and Drop Setup
  private void setupDragAndDrop() {
    imageContainer.setOnDragOver(
        event -> {
          Dragboard dragboard = event.getDragboard();
          if (dragboard.hasFiles()
              && dragboard
                  .getFiles()
                  .get(0)
                  .getName()
                  .matches("(?i).*\\.(png|jpg|jpeg|pdf|dwg)$")) {
            event.acceptTransferModes(TransferMode.COPY);
          }
          event.consume();
        });

    imageContainer.setOnDragDropped(
        event -> {
          Dragboard dragboard = event.getDragboard();
          boolean success = false;
          if (dragboard.hasFiles()) {
            File file = dragboard.getFiles().get(0);
            if (file.getName().matches("(?i).*\\.(png|jpg|jpeg|pdf|dwg)$")) {
              if (getFileExtension(file).equalsIgnoreCase("dwg")) {
                showAlert(
                    Alert.AlertType.INFORMATION,
                    "Information",
                    "DWG files are not currently supported.");
              } else {
                loadImageFromFile(file);
                success = true;
              }
            }
          }
          event.setDropCompleted(success);
          event.consume();
        });
  }

  // TODO Clear the redo / undo stack
  private void resetStateImageOverlay() {
    undoStackForImageOverlay.clear();
    redoStackForImageOverlay.clear(); // Clear the redo stack on a new action
  }

  // TODO save the redo / undo stack
  private void saveStateImageOverlay(String event) {
    if (!overlayPathBuilder.isEmpty() && (event.isEmpty() || event.isBlank())) {
      undoStackForImageOverlay.push(overlayPathBuilder);
      redoStackForImageOverlay.clear();
    } else if (!overlayPathBuilder.isEmpty() && event.equalsIgnoreCase("UNDO")) {

    } else if (!overlayPathBuilder.isEmpty() && event.equalsIgnoreCase("REDO")) {

    }
  }

  @FXML
  private void handleColorCheckBoxAction() {
    boolean isColorSelected = colorCheckBox.isSelected();
    selectedPointColourDropdown.setDisable(!isColorSelected);
    updateCanvas();
  }

  @FXML
  private void handlePointBoxCheckBoxAction() {
    boolean isPointBoxSelected = pointBoxCheckBox.isSelected();
    selectedPointBoxColourDropdown.setDisable(!isPointBoxSelected);
    updateCanvas();
  }

  private void updateCanvas() {
    gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    gc.drawImage(croppedImportedImage, 0, 0);

    if (colorCheckBox.isSelected()) {
      drawLines();
    }

    if (pointBoxCheckBox.isSelected()) {
      changePointBoxColour();
    }
  }

  private void undo() {
    if (isLastOption) {
      showAlert(
          Alert.AlertType.WARNING,
          "Undo Disabled",
          "Undo functionality is disabled after the selection is complete.");
      return;
    }
    if (!undoStack.isEmpty()) {
      redoStack.push(new ArrayList<>(points));
      points = undoStack.pop();
      redrawCanvasForPointSelection();
    }
  }

  private void redo() {
    if (isLastOption) {
      showAlert(
          Alert.AlertType.WARNING,
          "Redo Disabled",
          "Redo functionality is disabled after the selection is complete.");
      return;
    }
    if (!redoStack.isEmpty()) {
      undoStack.push(new ArrayList<>(points));
      points = redoStack.pop();
      redrawCanvasForPointSelection();
    }
  }

  private void redrawCanvasForPointSelection() {
    gc.clearRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    gc.drawImage(croppedImportedImage, 0, 0);
    for (Point point : points) {
      drawPoints(point.getX(), point.getY());
    }
    drawLines();
  }

  // ===== NEW: Overlay state save method =====
  private void saveOverlayState(String actionDescription) {
      if (overlayImageView1.getImage() == null) return;
      
      // Limit stack size
      if (overlayUndoStack.size() >= MAX_UNDO_STACK_SIZE) {
          overlayUndoStack.remove(0); // Remove oldest state
      }
      
      overlayUndoStack.push(new OverlayState(overlayImageView1));
      overlayRedoStack.clear(); // Clear redo stack on new action
      System.out.println("Overlay state saved: " + actionDescription + ". Undo size: " + overlayUndoStack.size());
  }
  
  // ===== NEW: Overlay undo handler =====
  @FXML
  private void handleOverlayUndo() {
      if (overlayUndoStack.isEmpty()) {
          showAlert(Alert.AlertType.INFORMATION, "Undo", "No more undo actions available");
          return;
      }
      
      // Save current state to redo stack
      overlayRedoStack.push(new OverlayState(overlayImageView1));
      
      // Restore previous state
      OverlayState previousState = overlayUndoStack.pop();
      previousState.restore(overlayImageView1);
      
      updateResizeHandles();
      System.out.println("Overlay UNDO completed. Undo size: " + overlayUndoStack.size() + 
                        ", Redo size: " + overlayRedoStack.size());
  }
  
  // ===== NEW: Overlay redo handler =====
  @FXML
  private void handleOverlayRedo() {
      if (overlayRedoStack.isEmpty()) {
          showAlert(Alert.AlertType.INFORMATION, "Redo", "No more redo actions available");
          return;
      }
      
      // Save current state to undo stack
      overlayUndoStack.push(new OverlayState(overlayImageView1));
      
      // Restore next state
      OverlayState nextState = overlayRedoStack.pop();
      nextState.restore(overlayImageView1);
      
      updateResizeHandles();
      System.out.println("Overlay REDO completed. Undo size: " + overlayUndoStack.size() + 
                        ", Redo size: " + overlayRedoStack.size());
  }
}

// ===== OverlayState class goes HERE - AFTER VastuController ends =====
class OverlayState {
    final Image image;
    final double layoutX;
    final double layoutY;
    final double fitWidth;
    final double fitHeight;
    final double rotate;
    final double scaleX;
    final double scaleY;
    final double opacity;
    
    OverlayState(ImageView overlay) {
        this.image = overlay.getImage();
        this.layoutX = overlay.getLayoutX();
        this.layoutY = overlay.getLayoutY();
        this.fitWidth = overlay.getFitWidth();
        this.fitHeight = overlay.getFitHeight();
        this.rotate = overlay.getRotate();
        this.scaleX = overlay.getScaleX();
        this.scaleY = overlay.getScaleY();
        this.opacity = overlay.getOpacity();
    }
    
    void restore(ImageView overlay) {
        overlay.setImage(image);
        overlay.setLayoutX(layoutX);
        overlay.setLayoutY(layoutY);
        overlay.setFitWidth(fitWidth);
        overlay.setFitHeight(fitHeight);
        overlay.setRotate(rotate);
        overlay.setScaleX(scaleX);
        overlay.setScaleY(scaleY);
        overlay.setOpacity(opacity);
    }
}