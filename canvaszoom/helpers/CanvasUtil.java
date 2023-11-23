package com.bosonshiggs.canvaszoom.helpers;

import com.google.appinventor.components.runtime.Canvas;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Paint;

import java.util.LinkedList;
import java.util.Queue;

import java.util.concurrent.ArrayBlockingQueue; 
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.Stack;

//import java.awt.Point;

import android.util.Log;

public class CanvasUtil {
    private android.view.View view;
    private int width, height;
    Bitmap bitmap;
    Canvas canvas;
    
    private Stack<Bitmap> undoStack = new Stack<>();
    private Stack<Bitmap> redoStack = new Stack<>();
    
    // Pilhas para estados de zoom
    private Stack<Bitmap> zoomInStack = new Stack<>();
    private Stack<Bitmap> zoomOutStack = new Stack<>();
    
    private String LOG_NAME = "CanvasUtil";
    private boolean flagLog = false;
    
    public CanvasUtil(Canvas canvas) {
    	Log.d(LOG_NAME, "Setting canvas");
        this.view = canvas.getView();
        this.width = canvas.Width();
        this.height = canvas.Height();
        this.canvas = canvas;
        
        recycleBitmap(this.bitmap);
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }
    
    // Método para obter o Bitmap atual
    public Bitmap getBitmap() {
        return this.bitmap;
    }
    
    public void setBitmap(Bitmap newBitmap) {
        recycleBitmap(this.bitmap); // Recicla o Bitmap atual
        this.bitmap = newBitmap;
    }

    
    private void recycleBitmap(Bitmap bitmap) {
        if (this.bitmap != null && !this.bitmap.isRecycled()) {
            this.bitmap.recycle();
        }
    }
	
    public void clearMemory() {
        if (flagLog) Log.d(LOG_NAME, "Clearing memory");
        clearStack(undoStack);
        clearStack(redoStack);
        recycleBitmap(this.bitmap); // Só recicle this.bitmap se você for recriá-lo depois
        this.bitmap = null; // Defina como null após a reciclagem
        this.canvas.Clear();
    }

    private void clearStack(Stack<Bitmap> stack) {
    	if (flagLog) Log.d(LOG_NAME, "Clearing stack");
        while (!stack.isEmpty()) {
            Bitmap bitmap = stack.pop();
            if (bitmap != null) {
                bitmap.recycle(); // Libera os recursos associados ao bitmap
            }
        }
    }
    
    public void copiesCurrentState() {
    	if (this.bitmap == null) {
            // Opção: Recriar o bitmap aqui se for necessário
            this.bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
        }
    	
    	if (flagLog) Log.d(LOG_NAME, "Copying current state");
        
    	// Aqui, você está copiando o estado do canvas para this.bitmap
    	try {
	        for (int i = 0; i < this.width; i++) {
	            for (int j = 0; j < this.height; j++) {
	                int color = this.canvas.GetPixelColor(i, j);
	                this.bitmap.setPixel(i, j, color);
	            }
	        }
	        if (flagLog) Log.d(LOG_NAME, "Copy completed successfully!");
    	} catch (Exception e) {
    		if (flagLog) Log.e(LOG_NAME, "Copying current state", e);
    	}
    }
	
    public void saveCurrentState() {
        if (flagLog) Log.d(LOG_NAME, "Saving current state");
        copiesCurrentState();
        
        // Cria uma cópia do estado atual do bitmap
        Bitmap currentState = Bitmap.createBitmap(this.bitmap.getWidth(), this.bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        new android.graphics.Canvas(currentState).drawBitmap(this.bitmap, 0, 0, null);
        
        // Empurra o estado atual para a stack de undo
        undoStack.push(currentState);

        // Limpa a stack de redo
        clearStack(redoStack);

        if (flagLog) Log.d(LOG_NAME, "Canvas state saved");
    }


	
    public void copyBitmapToCanvas(Bitmap bitmap) {
    	if (flagLog) Log.d(LOG_NAME, "Copying bitmap to canvas");
        if (bitmap == null || bitmap.isRecycled()) {
        	if (flagLog) Log.d(LOG_NAME, "Bitmap is null or recycled");
            return;
        }
        
        int oldColor = this.canvas.PaintColor();
        try {
	        for (int i = 0; i < width; i++) {
	            for (int j = 0; j < height; j++) {
	                int color = bitmap.getPixel(i, j);
	                this.canvas.PaintColor(color);
	                this.canvas.DrawPoint(i, j);
	            }
	        }
	        if (flagLog) Log.d(LOG_NAME, "Bitmap copy successful!");
        } catch (Exception e) {
    		if (flagLog) Log.e(LOG_NAME, "Error: ", e);
    	}
        
        this.canvas.PaintColor(oldColor);
        this.view.invalidate();
        if (flagLog) Log.d(LOG_NAME, "The copy loop has ended!");
    }
	
    public void undo() {
        if (flagLog) Log.d(LOG_NAME, "Performing undo");
        if (!undoStack.isEmpty()) {
            Bitmap currentState = Bitmap.createBitmap(this.bitmap);
            redoStack.push(currentState);

            this.canvas.Clear();
            Bitmap previousState = undoStack.pop();
            this.bitmap = previousState; // Atualiza o bitmap
            copyBitmapToCanvas(previousState);
        }
        this.view.invalidate();
    }

    public void redo() {
        Log.d(LOG_NAME, "Performing redo");
        if (!redoStack.isEmpty()) {
            Bitmap currentState = Bitmap.createBitmap(this.bitmap);
            undoStack.push(currentState);

            this.canvas.Clear();
            Bitmap nextState = redoStack.pop();
            this.bitmap = nextState; // Atualiza o bitmap
            copyBitmapToCanvas(nextState);
        }
        this.view.invalidate();
    }
    
    // Método para salvar o estado de zoom
    public void saveZoomState(Bitmap bitmap) {
        Bitmap stateCopy = Bitmap.createBitmap(bitmap);
        zoomInStack.push(stateCopy);
    }

    // Método para recuperar o último estado de zoom
    public Bitmap undoZoom() {
        if (!zoomInStack.isEmpty()) {
            Bitmap lastState = zoomInStack.pop();
            zoomOutStack.push(lastState);
            return lastState;
        }
        return null; // ou retornar o estado padrão
    }

    // Método para refazer o último estado de zoom desfeito
    public Bitmap redoZoom() {
        if (!zoomOutStack.isEmpty()) {
            Bitmap nextState = zoomOutStack.pop();
            zoomInStack.push(nextState);
            return nextState;
        }
        return null; // ou retornar o estado padrão
    }   
    
    public boolean isBitmapRecycled() {
        return this.bitmap != null && this.bitmap.isRecycled();
    }
}    