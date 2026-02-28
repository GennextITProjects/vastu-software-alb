# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.1] - 2026-02-28

### Fixed
- **Circle Overlay Distortion Issue**: Fixed circular overlays (MARMA_POINTS, BAD_ZONES, GOOD_ENTRIES) appearing as ellipses instead of circles when applied to rectangular bounding boxes
  - Added aspect ratio preservation for circular overlays (Circle shape with ALL direction)
  - Implemented proper centering of circular images within bounding boxes
  - Maintained backward compatibility for rectangular overlays (Square, Rectangle, Triangle shapes)

### Technical Details
- Modified `drawImageInBoundingBoxUsingImageView` method in `VastuController.java`
- Added detection logic for circular overlay combinations
- Implemented square fitting algorithm to maintain circular appearance
- Preserved existing behavior for non-circular overlays

## [1.0.0] - 2026-02-28

### Added
- Initial release of MasterVastu application
- Image import and cropping functionality
- Overlay system with multiple shape and direction options
- Point selection and bounding box creation
- Compass visualization with rotation support
- Export functionality for images and PDFs
- Drag and drop support for image files

### Features
- Support for PNG, JPG, JPEG, and PDF file formats
- Interactive overlay system with Circle, Square, Rectangle, and Triangle shapes
- Multiple overlay types including MARMA_POINTS, DEVTAS, ZONES, etc.
- Real-time rotation and zoom controls
- Color customization for points and bounding boxes
- Undo/redo functionality for point selection

### Technical Stack
- JavaFX for GUI framework
- Apache PDFBox for PDF rendering
- Maven for build management
- Java 17+ compatibility