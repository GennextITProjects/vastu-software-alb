# Master Vastu Application

## Overview

Master Vastu is a comprehensive JavaFX desktop application designed for Vastu Shastra analysis and visualization. Vastu Shastra is an ancient Indian architectural science that focuses on harmonizing buildings and spaces with natural energies through proper design, orientation, and spatial arrangement.

The application provides Vastu consultants, architects, and enthusiasts with powerful tools to analyze property layouts by overlaying traditional Vastu diagrams, selecting analysis points, and generating professional reports.

## Key Features

### 🏠 Property Analysis
- Import architectural plans and property layouts (PNG, JPG, JPEG, PDF)
- Interactive cropping and center point marking
- Support for various property shapes (Circle, Square, Rectangle, Triangle)

### 🧭 Vastu Overlays
- Comprehensive library of Vastu diagrams:
  - **5 Elements** - Traditional elements (Earth, Water, Fire, Air, Space)
  - **9 Zones** - Bagua-inspired energy zones
  - **45 Devtas** - Traditional deities and their positions
  - **Marma Points** - Vital energy points
  - **Bad Zones** - Areas to avoid or correct
  - **Good Entries** - Auspicious entry points
  - **Colors** - Traditional Vastu color schemes
- Directional orientations (North-facing from all sides)

### 🎯 Interactive Analysis
- Point selection for detailed area analysis
- Color-coded points and bounding boxes
- Real-time overlay rotation and scaling
- Compass integration for directional reference
- Undo/redo functionality

### 💾 Export & Reporting
- Export analyzed plans as high-resolution PNG images
- PDF export for professional reports
- Multi-monitor support for extended workspaces

### 🖥️ User Experience
- Drag-and-drop file loading
- Intuitive JavaFX interface
- Multi-monitor positioning
- Professional UI with customizable themes

## Technology Stack

- **Java 17** - Modern Java with latest features
- **JavaFX 17** - Rich desktop UI framework
- **Maven** - Build automation and dependency management
- **PDFBox** - PDF processing and rendering
- **ControlsFX** - Enhanced JavaFX controls
- **BootstrapFX** - Modern CSS styling

## Project Structure

```
vastu-software-alb/
├── docs/                          # Documentation
├── src/main/java/com/cps/vastuapp/ # Source code
│   ├── VastuApplication.java      # Main application class
│   ├── VastuController.java       # UI controller and logic
│   ├── CropTool.java             # Image cropping utility
│   ├── Point.java                # Point model for selections
│   ├── ResizableRectangle.java   # Interactive rectangle component
│   └── utils/
│       └── AppUtils.java         # Utility functions
├── src/main/resources/            # Application resources
│   ├── com/cps/vastuapp/         # FXML layouts
│   ├── css/                      # Stylesheets
│   ├── icons/                    # Application icons
│   ├── images/                   # Vastu diagram images
│   └── Directory/                # Organized Vastu overlays
├── pom.xml                       # Maven configuration
├── mvnw                          # Maven wrapper (Unix)
└── mvnw.cmd                      # Maven wrapper (Windows)
```

## Quick Start

For detailed setup instructions, see [setup.md](setup.md).

For comprehensive usage guide, see [usage.md](usage.md).

For technical architecture details, see [architecture.md](architecture.md).

For troubleshooting and known issues, see [issues.md](issues.md).

## Requirements

- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)
- Minimum 4GB RAM recommended
- 1920x1080 display resolution minimum

## License

Proprietary software developed by CPS Company.

## Support

For support and inquiries:
- Website: www.cpscompany.com
- Email: support@cpscompany.com

---

**Master Vastu** - Harmonizing spaces with ancient wisdom, powered by modern technology.
