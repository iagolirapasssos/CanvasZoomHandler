package com.bosonshiggs.canvaszoom;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.common.ComponentCategory;

import com.bosonshiggs.canvaszoom.helpers.CanvasZoomHandler;

@DesignerComponent(
    version = 1,
    description = "Componente para manipular o zoom em um Canvas",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png"
)
@SimpleObject(external = true)
public class CanvasZoom extends AndroidNonvisibleComponent {
    private CanvasZoomHandler zoomHandler;

    public CanvasZoom(ComponentContainer container) {
        super(container.$form());
    }

    @SimpleProperty(description = "Configura o Canvas para manipulação do zoom.")
    public void SetCanvas(Canvas canvas) {
        zoomHandler = new CanvasZoomHandler(canvas);
    }

    @SimpleFunction(description = "Aumenta o zoom do Canvas.")
    public void ZoomIn() {
        if (zoomHandler != null) {
            form.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    zoomHandler.zoomIn();
                }
            });
        }
    }

    @SimpleFunction(description = "Diminui o zoom do Canvas.")
    public void ZoomOut() {
        if (zoomHandler != null) {
            form.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    zoomHandler.zoomOut();
                }
            });
        }
    }
    
    @SimpleFunction(description = "In this clearMemory method, the Undo and Redo stacks are emptied, and the bitmaps in each stack are recycled.\n"
    		+ "The recycle method is called on each Bitmap to free up the memory resources it is using.\n"
    		+ "Note that after a bitmap is recycled, it can no longer be used. "
    		+ "Therefore, it is important to ensure that clearMemory is only called when you are sure that these bitmaps will no longer be needed.\n"
    		+ "Additionally, the call to recycle and emptying the stacks help signal to the Java garbage collector that these resources can be freed, which can help reduce the application's memory usage.")
    public void clearMemory() {
    	new Thread(new Runnable() {
            @Override
            public void run() {
            	zoomHandler.clearMemory();
            }
		}).start();
    }
    
    @SimpleFunction(description = "Reseta o zoom para o estado original do Canvas.")
    public void ResetZoom() {
        if (zoomHandler != null) {
            zoomHandler.resetZoom();
        }
    }
    
    @SimpleProperty(description = "Obtém o fator atual de zoom.")
    public float ScaleFactor() {
        return zoomHandler != null ? zoomHandler.getScaleFactor() : 1.0f;
    }
    
    @SimpleFunction(description = "Aplica zoom em um ponto específico do Canvas.")
    public void ZoomToPoint(int x, int y, float scale) {
        if (zoomHandler != null) {
            zoomHandler.zoomToPoint(x, y, scale);
        }
    }
    
    @SimpleProperty(description = "Define o fator de escala para operações de zoom.")
    public void SetZoomScaleFactor(float factor) {
        if (zoomHandler != null) {
            zoomHandler.setZoomScaleFactor(factor);
        }
    }
}
