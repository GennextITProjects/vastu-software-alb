# Usage Guide

This comprehensive guide walks you through using the Master Vastu application for property analysis and Vastu Shastra visualization.

## Table of Contents

- [Getting Started](#getting-started)
- [Main Interface Overview](#main-interface-overview)
- [Importing Property Plans](#importing-property-plans)
- [Applying Vastu Overlays](#applying-vastu-overlays)
- [Interactive Analysis](#interactive-analysis)
- [Exporting Results](#exporting-results)
- [Advanced Features](#advanced-features)
- [Workflow Examples](#workflow-examples)
- [Troubleshooting](#troubleshooting)

## Getting Started

### Launching the Application

After [setting up your environment](setup.md), launch the application:

```bash
# Navigate to project directory
cd vastu-software-alb

# Run with Maven wrapper
./mvnw clean javafx:run
```

The application will start and position itself on your primary monitor (or secondary monitor if available).

### First Time Setup

1. **Import an Image**: The application starts with a clean interface. Click the "Import" button or drag-and-drop an image file.
2. **Supported Formats**: PNG, JPG, JPEG, PDF files are supported.
3. **Initial Setup**: Most controls are disabled until you import an image.

## Main Interface Overview

### Primary Components

- **Image Canvas**: Central area displaying the imported property plan
- **Control Panel**: Left sidebar with analysis tools
- **Menu Bar**: Top menu with File, Edit, View, and Help options
- **Status Bar**: Bottom bar showing current operation status
- **Compass**: Integrated directional reference tool

### Key UI Elements

#### Dropdown Menus
- **Shape Selection**: Choose property shape (Circle, Square, Rectangle, Triangle)
- **Direction Selection**: Select north-facing orientation
- **Overlay Selection**: Choose specific Vastu diagram to apply

#### Control Buttons
- **Import**: Load property plan images
- **Export**: Save analysis results
- **Reset**: Clear current analysis
- **Crop**: Edit imported image boundaries

#### Sliders and Controls
- **Zoom Controls**: Scale overlay and base image
- **Rotation Slider**: Adjust overlay orientation
- **Opacity Controls**: Adjust overlay transparency
- **Color Pickers**: Customize point and zone colors

## Importing Property Plans

### Supported File Types

- **Images**: PNG, JPG, JPEG formats
- **Documents**: PDF files (rendered as images)
- **Future Support**: DWG files (currently not supported)

### Import Process

1. **Click "Import" Button**
   - Opens file chooser dialog
   - Navigate to your property plan file

2. **Drag and Drop**
   - Drag image files directly onto the application window
   - Automatic file type detection

3. **PDF Import**
   - PDFs are automatically converted to images
   - First page is rendered at 150 DPI
   - Large PDFs may take time to process

### Image Cropping

After import, you'll be prompted to crop the image:

```text
"Would you like to crop this image?"
[OK] - Proceed to cropping tool
[Cancel] - Use full image
```

**Cropping Tool Features:**
- Interactive rectangle selection
- Maintain aspect ratio (hold Shift)
- Real-time preview
- Center point marking

### Post-Import Setup

Once imported:
- Image displays in center canvas
- Center marker appears (blue dot)
- All control buttons become enabled
- Compass initializes to default direction

## Applying Vastu Overlays

### Shape and Direction Selection

1. **Select Property Shape**
   - Circle: For circular properties or round rooms
   - Square: Most common rectangular properties
   - Rectangle: Elongated properties
   - Triangle: Unique triangular spaces

2. **Choose North Orientation**
   - Left Side North (LSN)
   - Right Side North (RSN)
   - Up Side North (USN)
   - Down Side North (DSN)
   - All (for circular properties)

3. **Select Overlay Type**
   - **5 Elements**: Traditional earth, water, fire, air, space
   - **9 Zones**: Energy zones based on Bagua principles
   - **45 Devtas**: Traditional deities and their positions
   - **Marma Points**: Vital energy points to protect
   - **Bad Zones**: Areas requiring correction
   - **Good Entries**: Auspicious entry points
   - **Colours**: Traditional Vastu color schemes
   - **Zones**: Detailed zone classifications

### Overlay Application

1. **Make Selections**: Choose Shape → Direction → Overlay
2. **Click "Apply"**: Overlay appears on your property plan
3. **Adjust Position**: Drag overlay to align with property
4. **Fine-tune**: Use zoom, rotation, and opacity controls

### Overlay Controls

#### Rotation (0-360°)
- Use slider or input field
- Real-time compass synchronization
- Keyboard shortcuts: Left/Right arrows

#### Zoom (25%-400%)
- Independent zoom for overlay and base image
- Mouse wheel support when Ctrl+Scroll
- Reset to 100% with double-click

#### Opacity (0-100%)
- Transparent overlays for comparison
- Separate controls for overlay and canvas

## Interactive Analysis

### Point Selection Mode

1. **Enable Point Selection**
   - Click "Point Selection" button
   - Cursor changes to hand icon
   - Left-click to add points
   - Right-click to finish selection

2. **Adding Points**
   - Click anywhere on the property plan
   - Blue dots mark selected points
   - Lines connect sequential points
   - Bounding box automatically calculated

3. **Point Management**
   - **Undo**: Ctrl+Z to remove last point
   - **Redo**: Ctrl+Y to restore removed point
   - **Clear**: Remove all points and selections

### Color Customization

#### Point Colors
- 8 predefined colors: Red, Green, Blue, Orange, Yellow, Indigo, Violet
- Visual color picker interface
- Real-time line color updates

#### Box Colors
- Separate color selection for bounding boxes
- Visual feedback with center markers
- Crosshair indicators for precision

### Bounding Box Analysis

When point selection completes:
- Automatic bounding box calculation
- Overlay snaps to selected area (for applicable overlays)
- Center point marked with crosshairs
- Color-coded perimeter lines

## Exporting Results

### Export Options

1. **Image Export (PNG)**
   - High-resolution snapshot (2x scale)
   - Includes all overlays and markings
   - Transparent background option

2. **PDF Export**
   - Professional document format
   - Embedded images with proper sizing
   - Suitable for reports and presentations

### Export Process

1. **Click "Export"**
2. **Choose Format**: PNG or PDF
3. **Select Location**: File save dialog
4. **Automatic Processing**: Resize handles hidden during export
5. **Confirmation**: Success message with file path

### Export Settings

- **Resolution**: 2x scale for crisp output
- **Format**: PNG for images, PDF for documents
- **Quality**: High-quality rendering
- **Metadata**: Includes analysis timestamp

## Advanced Features

### Multi-Monitor Support

- Automatic detection of multiple displays
- Application positions on secondary monitor
- Extended workspace for large property plans

### Drag and Drop

- Direct file dropping on interface
- Automatic import processing
- Batch file handling (future feature)

### Undo/Redo System

- **Image Overlay Stack**: 3-level undo for overlay changes
- **Point Selection Stack**: Unlimited undo/redo for points
- **Keyboard Shortcuts**: Ctrl+Z (undo), Ctrl+Y (redo)

### Compass Integration

- **Directional Reference**: Visual north indicator
- **Rotation Sync**: Compass updates with overlay rotation
- **Direction Labels**: N, E, S, W cardinal directions
- **Degree Markings**: 30° increments

### Resize Handles

- **Interactive Scaling**: 8 resize handles per overlay
- **Aspect Ratio Lock**: Hold Shift to maintain proportions
- **Live Preview**: Real-time scaling feedback

## Workflow Examples

### Residential Property Analysis

1. **Import Floor Plan**: Load architectural drawing
2. **Select Square Shape**: Most common for homes
3. **Choose North Direction**: Based on property orientation
4. **Apply 9 Zones**: Basic energy analysis
5. **Mark Entry Points**: Use point selection for doors
6. **Add Good Entries Overlay**: Verify auspicious entries
7. **Export Analysis**: Save as PDF report

### Commercial Building Analysis

1. **Import Site Plan**: Load large-scale property map
2. **Select Rectangle Shape**: For elongated commercial spaces
3. **Multiple Overlays**: Apply 5 Elements, then Marma Points
4. **Zone Analysis**: Use point selection for different areas
5. **Color Coding**: Different colors for different zones
6. **Detailed Report**: Export multiple views

### Room-Specific Analysis

1. **Import Room Plan**: Focus on individual rooms
2. **Select Appropriate Shape**: Based on room geometry
3. **Apply Element Analysis**: Check 5 Elements distribution
4. **Identify Issues**: Mark Bad Zones for correction
5. **Suggest Improvements**: Use Good Entries for optimization

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| Ctrl+Z | Undo (points or overlays) |
| Ctrl+Y | Redo (points or overlays) |
| Ctrl+S | Export (opens save dialog) |
| Ctrl+O | Import (opens file dialog) |
| Ctrl+R | Reset application |
| F1 | Show help/about |
| Esc | Cancel current operation |

## Troubleshooting

### Common Issues

#### Overlay Not Appearing
- **Cause**: Incorrect shape/direction/overlay selection
- **Solution**: Verify all three dropdowns are selected
- **Check**: Ensure overlay path exists in resources

#### Points Not Selecting
- **Cause**: Point selection mode not active
- **Solution**: Click "Point Selection" button first
- **Check**: Cursor should be hand icon

#### Export Failing
- **Cause**: No image imported or memory issues
- **Solution**: Ensure image is loaded, try smaller resolution
- **Check**: Sufficient disk space available

#### Compass Not Updating
- **Cause**: Direction not set in dropdown
- **Solution**: Select direction before applying overlay
- **Check**: Compass updates when rotation changes

#### Performance Issues
- **Cause**: Large images or complex overlays
- **Solution**: Reduce image size, use simpler overlays
- **Check**: System has adequate RAM (4GB+ recommended)

### Error Messages

- **"No image imported"**: Import an image before proceeding
- **"Overlay path not found"**: Check dropdown selections
- **"Cropping failed"**: Try importing a different image format
- **"Memory error"**: Close other applications, increase heap size

### Getting Help

- **About Dialog**: Help → About for version information
- **Log Files**: Check console output for detailed errors
- **Reset Function**: Use Reset button to clear problematic state
- **Restart Application**: Close and relaunch if issues persist

## Best Practices

### File Preparation
- Use high-resolution images (minimum 1000x1000px)
- Ensure north direction is clearly marked on plans
- Convert PDFs to images if import issues occur

### Analysis Workflow
- Start with basic overlays (9 Zones, 5 Elements)
- Use point selection for specific areas of interest
- Apply multiple overlays for comprehensive analysis
- Save intermediate results during complex analysis

### Performance Optimization
- Close unnecessary applications before running
- Use smaller images for initial testing
- Clear undo stacks periodically on long sessions
- Export regularly to free memory

### Data Management
- Organize property plans by project/client
- Use descriptive filenames for exported results
- Keep original files as backup
- Document analysis methodology for reports

## Support Resources

- **Documentation**: Complete guides in `/docs` directory
- **Video Tutorials**: Visit www.cpscompany.com/tutorials
- **Community Forum**: Connect with other Vastu professionals
- **Technical Support**: support@cpscompany.com

---

For technical architecture details, see [architecture.md](architecture.md).
