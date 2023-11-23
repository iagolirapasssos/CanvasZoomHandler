package com.bosonshiggs.canvaszoom.helpers;

import com.google.appinventor.components.runtime.Canvas;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Paint;

import com.bosonshiggs.canvaszoom.helpers.CanvasUtil;

public class CanvasZoomHandler {
    private Canvas kodularCanvas;
    private Bitmap originalBitmap;
    private CanvasUtil canvasUtil;
    private Bitmap originalBitmapState;
    
    private float scaleFactor = 1.0f;

    public CanvasZoomHandler(Canvas canvas) {
        this.kodularCanvas = canvas;
        this.canvasUtil = new CanvasUtil(canvas);
        
        // Inicializar originalBitmap com o estado atual do Canvas
        canvasUtil.copiesCurrentState();
        originalBitmap = canvasUtil.getBitmap();
        originalBitmapState = Bitmap.createBitmap(originalBitmap);
    }
    
    private void ensureBitmap() {
        if (canvasUtil.isBitmapRecycled()) {
            // Tenta restaurar o estado mais recente do Canvas
            Bitmap lastState = canvasUtil.undoZoom();
            if (lastState != null) {
                originalBitmap = lastState;
            } else {
                // Se não houver estados disponíveis, cria um novo Bitmap
                originalBitmap = Bitmap.createBitmap(kodularCanvas.Width(), kodularCanvas.Height(), Bitmap.Config.ARGB_8888);
            }
            canvasUtil.setBitmap(originalBitmap);
        } else {
            // Atualiza o estado do originalBitmap com o Canvas atual
            canvasUtil.copiesCurrentState();
            originalBitmap = canvasUtil.getBitmap();
        }
    }

    public void zoomIn() {
    	ensureBitmap();
        scaleFactor *= 1.1;
        redrawCanvas();
    }

    public void zoomOut() {
    	ensureBitmap();
        scaleFactor *= 0.9;
        redrawCanvas();
    }
    
    public void zoomToPoint(int x, int y, float scale) {
        ensureBitmap();
        scaleFactor *= scale;

        // Garante que o scaleFactor não seja menor que 1
        scaleFactor = Math.max(1.0f, scaleFactor);

        // Calcula o deslocamento com base nas coordenadas fornecidas
        float dx = x - (kodularCanvas.Width() / 2f);
        float dy = y - (kodularCanvas.Height() / 2f);

        // Usa o estado original para evitar a perda de qualidade
        Bitmap currentBitmap = originalBitmapState;

        int scaledWidth = (int)(currentBitmap.getWidth() * scaleFactor);
        int scaledHeight = (int)(currentBitmap.getHeight() * scaleFactor);

        Bitmap scaledBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas tempCanvas = new android.graphics.Canvas(scaledBitmap);

        // Configura a matriz para aplicar escala e deslocamento
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        // Calcula o deslocamento adicional para centralizar o ponto (x, y)
        float additionalDx = (kodularCanvas.Width() - scaledWidth) / 2f - dx * scaleFactor;
        float additionalDy = (kodularCanvas.Height() - scaledHeight) / 2f - dy * scaleFactor;
        matrix.postTranslate(additionalDx, additionalDy);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        tempCanvas.drawBitmap(currentBitmap, matrix, paint);

        canvasUtil.setBitmap(scaledBitmap);
        canvasUtil.copyBitmapToCanvas(scaledBitmap);

        originalBitmap = scaledBitmap;
    }

    private void redrawCanvas() {
        if (canvasUtil.isBitmapRecycled() || originalBitmapState == null || originalBitmapState.isRecycled()) {
            return; // Se o bitmap original foi reciclado ou é nulo, não faz nada.
        }

        int scaledWidth = (int)(originalBitmapState.getWidth() * scaleFactor);
        int scaledHeight = (int)(originalBitmapState.getHeight() * scaleFactor);

        Bitmap scaledBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        android.graphics.Canvas tempCanvas = new android.graphics.Canvas(scaledBitmap);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
        tempCanvas.drawBitmap(originalBitmapState, matrix, paint);

        canvasUtil.setBitmap(scaledBitmap);
        canvasUtil.copyBitmapToCanvas(scaledBitmap);

        originalBitmap = scaledBitmap;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }
    
    public void undo() {
        Bitmap previousState = canvasUtil.undoZoom();
        if (previousState != null) {
            originalBitmap = previousState;
            canvasUtil.copyBitmapToCanvas(previousState);
        }
    }

    public void redo() {
        Bitmap nextState = canvasUtil.redoZoom();
        if (nextState != null) {
            originalBitmap = nextState;
            canvasUtil.copyBitmapToCanvas(nextState);
        }
    }
    
    public void clearMemory() {
    	canvasUtil.clearMemory();
    }
    
    public void setZoomScaleFactor(float factor) {
        this.scaleFactor = factor;
    }
    
    public void resetZoom() {
        if (originalBitmapState != null && !originalBitmapState.isRecycled()) {
            scaleFactor = 1.0f;
            canvasUtil.setBitmap(Bitmap.createBitmap(originalBitmapState));
            canvasUtil.copyBitmapToCanvas(originalBitmapState);
            originalBitmap = originalBitmapState;
        }
    }
}
