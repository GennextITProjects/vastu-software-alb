# Changelog - March 3, 2026

## 🎯 Major Feature: Inner Rectangle Mapping for Overlays

### Added
- **Inner Rectangle Mapping System**: Overlays now scale precisely based on their inner content rather than the entire image
- `overlayInnerRectangles` map to store percentage-based inner rectangle coordinates for each overlay
- `setupOverlayInnerRectangles()` method to initialize mapping data for all rectangular overlays
- `getOverlayNameFromPath()` helper method to extract overlay names from file paths for lookup
- `applyInnerRectangleScaling()` method that handles complex scaling calculations to map inner rectangles to user selections

### Configured Overlays with Inner Rectangle Percentages
| Overlay | Left % | Top % | Width % | Height % |
|---------|--------|-------|---------|----------|
| GOOD_ENTRIES_BAD_ZONES | 19.87 | 20.25 | 60.52 | 60.04 |
| 5_ELEMENTS | 19.76 | 20.08 | 60.54 | 60.21 |
| 9_ZONES | 19.79 | 20.05 | 60.51 | 60.24 |
| DEVTAS | 19.76 | 20.08 | 60.54 | 60.21 |
| MANDUKA | 19.76 | 19.68 | 60.54 | 60.29 |
| MARMA_POINTS | 19.81 | 19.73 | 60.42 | 60.17 |
| ZONES | 19.79 | 20.13 | 60.51 | 60.16 |

### Technical Implementation
- All percentages calculated from actual image dimensions (9933×9974 pixels)
- Inner rectangle coordinates obtained from GIMP analysis
- Scaling uses `Math.min(scaleX, scaleY)` to ensure inner rectangle fits within user selection

---

## 🎯 Major Feature: Circular Overlay Mapping

### Added
- **Inner Circle Mapping System**: Circular overlays now map their inner circle to user selections
- `overlayInnerCircles` map for circular overlay parameters
- `setupOverlayInnerCircles()` method to initialize circle data
- `applyInnerCircleScaling()` method for precise circle positioning and scaling

### Configured Circular Overlays (12718×12720 image)
| Overlay | Center X% | Center Y% | Radius % |
|---------|-----------|-----------|----------|
| MARMA_POINTS | 50.01 | 49.80 | 23.55 |
| BAD_ZONES | 50.01 | 49.80 | 23.55 |
| GOOD_ENTRIES | 50.01 | 49.80 | 23.55 |

### Circle Parameters
- Center point: (6360, 6335) pixels
- Radius: 2995 pixels
- Perfectly centered in image (≈50% each axis)

---

## 🖱️ Major Feature: Independent Overlay Dragging

### Fixed
- **Overlay and container now drag independently** - No more moving together!
- Overlay dragging now works for both centered overlays AND bounding box selections

### Key Changes
1. **Removed layout bindings** that were forcing overlays back to center:
   - Added `unbind()` calls in `handleApply()`, `drawImageInBoundingBoxUsingImageView()`, and `resetOverlays()`
   - Replaced bindings with manual `setLayoutX()`/`setLayoutY()`

2. **Fixed missing interactive initialization**:
   - Added `makeImageViewInteractive(overlayImageView1)` call in `drawImageInBoundingBoxUsingImageView()`
   - Previously, overlays created via bounding box never got drag handlers attached

3. **Improved drag implementation**:
   - Simplified to use `event.getX()`/`getY()` relative to overlay
   - Added proper event consumption to prevent container interference
   - Set `setPickOnBounds(true)` to ensure overlay captures all mouse events

4. **Container drag refinement**:
   - Container now only drags when clicking on canvas or empty space
   - Uses cursor state to track dragging mode
   - Clear separation between overlay and container event handling

### Debug Improvements
- Added detailed debug logging with "OVERLAY" and "CONTAINER" prefixes
- Mouse click target identification to verify event routing
- Position tracking during drag operations

---

## 📝 Summary of Files Modified

1. **`VastuController.java`** - All major changes
   - Added overlay mapping data structures
   - Implemented scaling methods
   - Updated drag functionality
   - Added debug event filters

2. **FXML (indirectly)** - No changes needed, but layering confirmed correct

---

## 🐛 Issues Resolved

1. **Overlay scaling incorrect** - Fixed with inner rectangle/circle mapping
2. **Overlay and container moving together** - Fixed with proper event handling and unbinding
3. **Overlay not draggable after point selection** - Fixed by adding missing `makeImageViewInteractive()` call
4. **Bindings overriding manual positioning** - Fixed with `unbind()` calls

---

## 🚀 Next Steps / Known Limitations

- Triangle overlays still need implementation (pending)
- Consider adding drag boundaries to keep overlay within canvas
- May need to optimize for very large images
- Consider persisting overlay positions between sessions

---

**All changes tested and verified working as of March 3, 2026** ✅