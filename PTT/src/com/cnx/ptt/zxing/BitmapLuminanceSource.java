package com.cnx.ptt.zxing;

import android.graphics.Bitmap;

import com.google.zxing.LuminanceSource;

public class BitmapLuminanceSource extends LuminanceSource {

	private byte bitmapPixels[];

	protected BitmapLuminanceSource(Bitmap bitmap) {
		super(bitmap.getWidth(), bitmap.getHeight()); 


		int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];  
		this.bitmapPixels = new byte[bitmap.getWidth() * bitmap.getHeight()]; 
		bitmap.getPixels(data, 0, getWidth(), 0, 0, getWidth(), getHeight()); 
		
		for (int i = 0; i < data.length; i++) {  
            this.bitmapPixels[i] = (byte) data[i];  
        }
		
		data = null;
	}

	@Override
	public byte[] getMatrix() {
		return bitmapPixels; 
	}

	@Override
	public byte[] getRow(int arg0, byte[] arg1) {
		System.arraycopy(bitmapPixels, arg0 * getWidth(), arg1, 0, getWidth());  
        return arg1;
	}

}
