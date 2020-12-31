package com.gameofcoding.fontgenerator.utils;

import android.graphics.Color;

public class ColorWrapper {
   private static final String TAG = "ColorWrapper";
   public static Color wrap(com.badlogic.gdx.graphics.Color gdxColor) {
	  return Color.valueOf(gdxColor.r, gdxColor.g, gdxColor.b, gdxColor.a);
   }

   public static com.badlogic.gdx.graphics.Color wrap(Color andColor) {
	  return new com.badlogic.gdx.graphics.Color(andColor.red(), andColor.green(), andColor.blue(), andColor.alpha());
   }

   public static String toHex(Color color) {
	  return toHex(color.toArgb());
   }

   public static String toHex(com.badlogic.gdx.graphics.Color color) {
	  return toHex(wrap(color).toArgb());
   }

   public static String toHex(int color) {
	  return String.format("#%06X", (0xFFFFFFFF & color));
   }
}
