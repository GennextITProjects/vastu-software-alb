# Technical Architecture

This document provides a comprehensive overview of the Master Vastu application's technical architecture, design patterns, and implementation details.

## Table of Contents

- [System Overview](#system-overview)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Core Components](#core-components)
- [Data Flow](#data-flow)
- [Design Patterns](#design-patterns)
- [Performance Considerations](#performance-considerations)
- [Security Considerations](#security-considerations)
- [Extensibility](#extensibility)

## System Overview

Master Vastu is a desktop JavaFX application that provides Vastu Shastra analysis tools through an intuitive graphical interface. The application processes property plans, applies traditional Vastu diagrams, and enables interactive analysis with export capabilities.

### Key Characteristics

- **Platform**: Cross-platform Java desktop application
- **UI Framework**: JavaFX with FXML layouts
- **Architecture**: MVC (Model-View-Controller) pattern
- **Build System**: Maven with wrapper support
- **Packaging**: Modular JAR with dependencies
- **Resource Management**: Embedded assets and overlays

## Technology Stack

### Core Technologies

| Component | Technology | Version | Purpose |
|-----------|------------|---------|---------|
| **Runtime** | Java | 17 | Core language and JVM |
| **UI Framework** | JavaFX | 17.0.13 | Desktop GUI components |
| **Build Tool** | Maven | 3.6+ | Dependency management and build |
| **PDF Processing** | Apache PDFBox | 3.0.2 | PDF rendering and export |
| **UI Controls** | ControlsFX | 11.1.2 | Enhanced JavaFX controls |
| **Styling** | BootstrapFX | 0.4.0 | CSS styling framework |

### Development Tools

- **IDE**: Compatible with IntelliJ IDEA, Eclipse, VS Code
- **Version Control**: Git
- **Documentation**: Markdown
- **Code Quality**: Maven plugins for testing and validation

## Project Structure

```
vastu-software-alb/
├── docs/                          # Documentation
│   ├── README.md                  # Main documentation
│   ├── setup.md                   # Environment setup
│   ├── usage.md                   # User guide
│   └── architecture.md            # This file
├── src/
│   └── main/
│       ├── java/com/cps/vastuapp/ # Source code
│       │   ├── VastuApplication.java    # Main application class
│       │   ├── VastuController.java     # UI controller
│       │   ├── CropTool.java           # Image cropping utility
│       │   ├── Point.java              # Point model
│       │   ├── ResizableRectangle.java # Interactive rectangle
│       │   └── utils/
│       │       └── AppUtils.java       # Utility functions
│       └── resources/                  # Application resources
│           ├── com/cps/vastuapp/       # FXML layouts
│           │   ├── VastuApplication.fxml
│           │   └── CropWindow.fxml
│           ├── css/                    # Stylesheets
│           │   └── styles.css
│           ├── icons/                  # Application icons
│           │   ├── app_icon.jpg
│           │   └── Master_Vastu_Logo.jpg
│           ├── images/                 # Vastu diagram images
│           │   ├── 16_zone.png
│           │   ├── 32_zone.png
│           │   └── 45_devtas.png
│           └── Directory/              # Organized overlays
│               ├── Circle/ALL/         # Circular property overlays
│               ├── Square/[DSN|LSN|RSN|USN]/  # Square overlays by direction
│               ├── Rectangle/[DSN|LSN|RSN|USN]/ # Rectangle overlays
│               └── Triangle/ALL/       # Triangle overlays
├── target/                     # Build output (generated)
├── pom.xml                     # Maven configuration
├── mvnw                        # Maven wrapper (Unix)
├── mvnw.cmd                    # Maven wrapper (Windows)
└── README.md                   # Project README
```

## Core Components

### 1. Application Layer

#### VastuApplication.java
**Purpose**: Main application entry point and JavaFX lifecycle management

**Key Responsibilities**:
- JavaFX application initialization
- Primary stage setup and configuration
- Multi-monitor positioning logic
- Application icon loading
- Global exception handling

**Key Methods**:
- `start(Stage)`: Main UI initialization
- `setApplicationIcon(Stage)`: Icon loading with error handling
- `positionWindowOnScreen(Stage)`: Multi-monitor positioning

### 2. Controller Layer

#### VastuController.java
**Purpose**: Main UI controller implementing business logic and user interactions

**Key Features**:
- FXML injection for UI components
- Event handling for all user interactions
- Canvas management and graphics rendering
- File I/O operations
- Overlay management and positioning

**Major Functional Areas**:

##### Image Management
- Import handling (PNG, JPG, PDF)
- PDF rendering with Apache PDFBox
- Image cropping workflow
- Canvas rendering and scaling

##### Overlay System
- Dynamic overlay loading from resources
- Shape and direction-based selection
- Rotation, scaling, and opacity controls
- Interactive positioning and resizing

##### Point Selection System
- Mouse event handling for point placement
- Undo/redo stack management
- Bounding box calculations
- Color customization

##### Export System
- PNG export with high-resolution scaling
- PDF generation with embedded images
- Snapshot management with handle hiding

### 3. Model Layer

#### Point.java
**Purpose**: Data model for coordinate points in analysis

```java
public class Point {
    private double x;
    private double y;

    // Constructors, getters, setters
}
```

#### ResizableRectangle.java
**Purpose**: Interactive rectangle component with resize handles

**Features**:
- 8 resize handles (corners and edges)
- Aspect ratio preservation
- Drag and drop positioning
- Visual feedback during interaction

### 4. Utility Layer

#### AppUtils.java
**Purpose**: Shared utility functions

**Key Functions**:
- Alert creation with consistent styling
- Icon loading for dialogs
- Common validation helpers
- Resource path management

#### CropTool.java
**Purpose**: Dedicated image cropping interface

**Features**:
- Modal dialog for cropping
- Interactive selection rectangle
- Preview and confirmation
- Return cropped image to main controller

## Data Flow

### 1. Application Startup
```
main() → JavaFX Platform.start() → VastuApplication.start()
    ↓
initialize() → setup UI components → enable/disable controls
```

### 2. Image Import Process
```
User selects file → File validation → Format detection
    ↓
PDF: PDFBox rendering → Image conversion
Images: Direct loading → Optional cropping prompt
    ↓
Canvas setup → Center marker placement → UI state update
```

### 3. Overlay Application
```
Shape selection → Direction selection → Overlay selection
    ↓
Resource path construction → Image loading → Position centering
    ↓
UI controls activation → Interactive features enabled
```

### 4. Point Selection Workflow
```
Point Selection mode → Mouse click handling → Point creation
    ↓
Coordinate storage → Visual rendering → Line drawing
    ↓
Bounding box calculation → Color application → Selection completion
```

### 5. Export Process
```
Export button → Format selection → File dialog
    ↓
Snapshot preparation → Handle hiding → High-res rendering
    ↓
Image/PDF generation → File writing → Success confirmation
```

## Design Patterns

### MVC Pattern (Model-View-Controller)

- **Model**: `Point.java`, `ResizableRectangle.java`
- **View**: FXML layouts (`VastuApplication.fxml`, `CropWindow.fxml`)
- **Controller**: `VastuController.java`

### Observer Pattern

- **Usage**: UI component bindings (sliders, text fields)
- **Implementation**: JavaFX property bindings for real-time updates

### Command Pattern

- **Usage**: Undo/redo functionality
- **Implementation**: Stack-based command storage and execution

### Factory Pattern

- **Usage**: Alert creation, UI component instantiation
- **Implementation**: `AppUtils.setAlertIcon()` for consistent dialog styling

### Singleton Pattern

- **Usage**: Application-wide resources, shared utilities
- **Implementation**: Static utility methods and constants

### Strategy Pattern

- **Usage**: Different export formats (PNG vs PDF)
- **Implementation**: Polymorphic export methods with format-specific logic

## Performance Considerations

### Memory Management

#### Image Handling
- Progressive loading with `Task<Image>` for large files
- Canvas-based rendering instead of multiple ImageView instances
- Garbage collection hints for large image operations

#### Resource Optimization
- Lazy loading of overlay images
- Cached font and graphics context objects
- Thread pool for concurrent operations (4 threads)

### UI Responsiveness

#### Asynchronous Processing
```java
Task<Image> loadImageTask = new Task<>() {
    @Override
    protected Image call() throws Exception {
        // Heavy processing off UI thread
        return processImage(file);
    }
};
```

#### Progress Feedback
- Background task progress monitoring
- UI thread safety with `Platform.runLater()`
- Cancelable operations

### Rendering Optimization

#### Canvas-based Drawing
- Single canvas for point rendering instead of multiple nodes
- Efficient graphics context reuse
- Minimal scene graph updates

#### Layer Management
- Separate canvases for different content types
- Opacity-based compositing
- Selective redraw operations

## Security Considerations

### File Access
- Restricted file chooser with format validation
- Safe path handling to prevent directory traversal
- Temporary file management for PDF processing

### Input Validation
- Numeric input constraints for angles and zoom levels
- File extension verification
- Bounds checking for coordinates

### Resource Protection
- Embedded resources prevent external modification
- Classpath resource loading
- No network communication (offline application)

## Extensibility

### Adding New Overlay Types

1. **Create overlay images** in appropriate directory structure
2. **Update data model** in `setupData()` method
3. **Add selection logic** in dropdown handlers
4. **Test rendering** and positioning

### Supporting New File Formats

1. **Extend import logic** in `handleImport()` method
2. **Add format detection** in `getFileExtension()`
3. **Implement rendering** similar to PDF processing
4. **Update file chooser** filters

### UI Customization

1. **Modify FXML layouts** for new components
2. **Update controller** with new `@FXML` injections
3. **Add event handlers** in `initialize()` method
4. **Style with CSS** in `styles.css`

### Plugin Architecture (Future)

The current modular design allows for future plugin support:
- ServiceLoader for dynamic component loading
- Interface-based extension points
- Configuration-based feature toggles

## Build and Deployment

### Maven Configuration

#### Dependencies
```xml
<!-- Core JavaFX -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>${javafx.version}</version>
</dependency>

<!-- PDF Processing -->
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.2</version>
</dependency>
```

#### Build Plugins
- **JavaFX Maven Plugin**: Native packaging and module configuration
- **Maven Assembly Plugin**: Fat JAR creation with dependencies
- **Maven Compiler Plugin**: Java 17 source/target compatibility

### Packaging Options

#### Fat JAR (Recommended)
```bash
./mvnw clean package
java -jar target/VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
```

#### Native Packaging
```bash
./mvnw clean javafx:jlink
# Creates native runtime image
```

#### Platform-Specific Installers (Future)
- MSI for Windows
- DMG for macOS
- DEB/RPM for Linux

## Testing Strategy

### Unit Testing
- JUnit 5 for core logic testing
- Mockito for dependency mocking
- Test-driven development for utilities

### Integration Testing
- UI interaction testing with TestFX
- File I/O operation verification
- Cross-platform compatibility testing

### Performance Testing
- Memory usage monitoring
- Large file processing benchmarks
- UI responsiveness metrics

## Maintenance and Support

### Logging Strategy
- Java Util Logging throughout application
- Configurable log levels
- Error reporting with context information

### Error Handling
- Try-catch blocks with user-friendly messages
- Graceful degradation for non-critical failures
- Recovery mechanisms for common issues

### Documentation Updates
- Inline code documentation
- API documentation generation
- User guide maintenance alongside code changes

---

This architecture provides a solid foundation for the Master Vastu application, balancing functionality, performance, and maintainability while following Java and JavaFX best practices.
