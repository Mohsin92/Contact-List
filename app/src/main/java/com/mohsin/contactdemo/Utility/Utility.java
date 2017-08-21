package com.mohsin.contactdemo.Utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;
import android.view.WindowManager;

import java.util.Hashtable;

public class Utility {
	@SuppressLint("NewApi")
	public static int[] getScreenDimensions(Context context) {

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		Point size = new Point();

		int width = 0;
		int height = 0;

		if (android.os.Build.VERSION.SDK_INT > 12) {
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			width = display.getWidth(); // deprecated
			height = display.getHeight();
		}

		int[] dimen = { width, height };
		return dimen;
	}

	private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();
	public static Typeface get(String name, Context context) {
		Typeface tf = fontCache.get(name);
		if (tf == null) {
			try {
				tf = Typeface.createFromAsset(context.getAssets(), name);
			} catch (Exception e) {
				return null;
			}
			fontCache.put(name, tf);
		}
		return tf;
	}

}
