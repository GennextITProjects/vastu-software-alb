# What We Did: Inner Rectangle Mapping for Overlays

## The Problem We Solved

We had an issue where overlay images (like `GOOD_ENTRIES_BAD_ZONES.png`) weren't scaling correctly when users selected a rectangle. The overlay would either be huge, misaligned, or not fit properly within the user's selected area.

The root cause was that these overlay images contain important content **inside** them (an inner rectangle), and we needed to map that inner content to exactly match the user's selected rectangle, rather than scaling the entire image arbitrarily.

## The Solution: Inner Rectangle Mapping

We implemented a system that:

### 1. **Defines Inner Rectangle Coordinates**
For each overlay, we store the inner rectangle's position as percentages of the total image size:
- **Left percentage**: How far from the left edge the inner rectangle starts
- **Top percentage**: How far from the top edge the inner rectangle starts  
- **Width percentage**: How wide the inner rectangle is
- **Height percentage**: How tall the inner rectangle is

For `GOOD_ENTRIES_BAD_ZONES.png`:
- Image size: 9933 × 9974 pixels
- Inner rectangle: Top Left (1974, 2020) to Bottom Right (7971, 8008)
- Calculated percentages: `{19.87, 20.25, 60.52, 60.04}`

### 2. **Calculates Proper Scaling**
When a user selects a rectangle, we:
- Calculate the actual pixel dimensions of the inner rectangle
- Determine the scale needed to make those dimensions match the user's rectangle
- Apply that scale to the **entire** overlay image

### 3. **Positions Precisely**
We then calculate where the inner rectangle sits within the scaled overlay and position the whole overlay so that inner rectangle aligns exactly with the user's selection.

## Key Code Additions

| Method | Purpose |
|--------|---------|
| `overlayInnerRectangles` map | Stores inner rectangle percentages for each overlay |
| `setupOverlayInnerRectangles()` | Initializes the mapping data |
| `getOverlayNameFromPath()` | Extracts overlay name from file path for lookup |
| `applyInnerRectangleScaling()` | Does the math to scale and position correctly |
| Updated `drawImageInBoundingBoxUsingImageView()` | Uses the new mapping when available |

## The "Aha!" Moment

The compilation error we hit was trying to use `width` and `height` variables inside `applyInnerRectangleScaling()` without passing them as parameters. Once we fixed that by passing them explicitly, everything worked perfectly!

---

# Changelog

## [1.1.0] - 2026-03-03

### Added
- **Inner Rectangle Mapping System**: Overlays now scale precisely based on their inner content
- `overlayInnerRectangles` map to store percentage-based inner rectangle coordinates
- `setupOverlayInnerRectangles()` method to initialize mapping data
- `getOverlayNameFromPath()` helper method to extract overlay names for lookup
- `applyInnerRectangleScaling()` method that handles the complex scaling calculations

### Changed
- Enhanced `drawImageInBoundingBoxUsingImageView()` to use inner rectangle mapping when available
- Modified scaling logic to use `Math.min` instead of `Math.max` to ensure inner rectangle fits within user selection
- Updated initialization sequence to include `setupOverlayInnerRectangles()`

### Fixed
- Compilation error in `applyInnerRectangleScaling()` by properly passing `width` and `height` as parameters
- Overlay scaling issue where images would appear huge or misaligned
- Inner rectangle now correctly maps to user's selected rectangle

### Technical Details
- Inner rectangle for `GOOD_ENTRIES_BAD_ZONES.png` configured with: 
  - Left: 19.87%, Top: 20.25%, Width: 60.52%, Height: 60.04%
  - Based on actual image dimensions (9933×9974) and inner rectangle coordinates (1974,2020) to (7971,8008)

### How It Works
1. User selects points to define a rectangle
2. System calculates rectangle dimensions (`width`, `height`)
3. Looks up overlay's inner rectangle percentages
4. Calculates actual inner rectangle dimensions in pixels
5. Determines required scale to match user's rectangle
6. Applies scale to entire overlay
7. Positions overlay so inner rectangle aligns with user's selection

---

## Future Enhancement Notes

When adding new overlays:
1. Get image dimensions (width × height)
2. Get inner rectangle corner coordinates (in pixels)
3. Calculate percentages:
   ```
   left% = (left_x / width) * 100
   top% = (top_y / height) * 100
   width% = ((right_x - left_x) / width) * 100
   height% = ((bottom_y - top_y) / height) * 100
   ```
4. Add to `overlayInnerRectangles` map in `setupOverlayInnerRectangles()`