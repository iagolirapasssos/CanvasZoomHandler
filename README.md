## CanvasZoom: A Flexible Zoom Management Extension for App Inventor

## Overview

The `CanvasZoom` extension provides an advanced solution for managing zoom operations on a canvas in App Inventor projects. It allows precise control over zoom functionalities, including zooming in, zooming out, focusing zoom on specific points, and managing canvas states.

## Features

- **Zoom In/Out**: Easily enlarge or reduce the view of the canvas with simple function calls.
- **Zoom to Point**: Focus the zoom on a specific point on the canvas, providing a more targeted view.
- **State Management**: Undo and redo capabilities to revert or reapply zoom actions.
- **Memory Management**: Efficient handling of bitmap resources to ensure optimal performance and memory usage.
- **Customizable Scale Factor**: Adjust the zoom level according to your needs.
- **Reset Functionality**: Quickly return to the original state of the canvas with a reset function.

## Usage

### Setting the Canvas

```java
public void SetCanvas(Canvas canvas) {
    zoomHandler = new CanvasZoomHandler(canvas);
}
```

### Zoom In

```java
public void ZoomIn() {
    if (zoomHandler != null) {
        zoomHandler.zoomIn();
    }
}
```

### Zoom Out

```java
public void ZoomOut() {
    if (zoomHandler != null) {
        zoomHandler.zoomOut();
    }
}
```

### Zoom to a Specific Point

```java
public void ZoomToPoint(int x, int y, float scale) {
    if (zoomHandler != null) {
        zoomHandler.zoomToPoint(x, y, scale);
    }
}
```

### Reset Zoom

```java
public void ResetZoom() {
    if (zoomHandler != null) {
        zoomHandler.resetZoom();
    }
}
```

### Clear Memory

```java
public void clearMemory() {
    zoomHandler.clearMemory();
}
```

## Implementation Details

The extension's core functionality is encapsulated within `CanvasZoomHandler` and `CanvasUtil` classes. `CanvasZoomHandler` manages the zoom operations and state transitions, while `CanvasUtil` handles bitmap operations and memory management.

### Ensuring Bitmap Integrity

```java
private void ensureBitmap() {
    // Code to handle bitmap recycling and restoration
}
```

### Redrawing the Canvas

```java
private void redrawCanvas() {
    // Code to apply zoom and update the canvas
}
```

### Managing Undo and Redo Operations

```java
public void undo() {
    // Undo the last zoom operation
}

public void redo() {
    // Redo the last undone zoom operation
}
```

## Integration with App Inventor

The `CanvasZoom` extension is designed to integrate seamlessly with the App Inventor environment, offering a user-friendly interface and simple annotations for easy understanding and usage.

---

This extension enhances the capabilities of App Inventor, allowing developers to create more dynamic and interactive applications. Whether it's for educational purposes, game development, or interactive media, `CanvasZoom` provides the tools needed for effective zoom management on a canvas.