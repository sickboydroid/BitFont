package com.gameofcoding.fontgenerator.utils;

import android.os.Environment;
import java.io.File;

public abstract class AppConstants {
   /////////////////////////
   // Package name of app //
   /////////////////////////
   public static final String PACKAGE_NAME = "com.gameofcoding.fontgenerator";

   ///////////////////////////////////////
   // Constants for controlling logging //
   ///////////////////////////////////////
   public static final boolean DEBUG = true;
   public static final boolean EXTREME_LOGGING = false;

   //////////////////////////
   // File names and paths //
   //////////////////////////
   @SuppressWarnings("deprecation")
   public static final File EXTERNAL_STORAGE_DIR = Environment.getExternalStorageDirectory();
   public static final String DEF_EXTERNAL_DIR_NAME = "bitmap-fonts";
   

   /////////////
   // Extras //
   ////////////
   public static final String EXTRA_COLOR = "extra_color";
   public static final String EXTRA_COLOR_ALPHA = "extra_color_alpha";
   public static final String EXTRA_COLOR_RED = "extra_color_red";
   public static final String EXTRA_COLOR_GREEN = "extra_color_green";
   public static final String EXTRA_COLOR_BLUE = "extra_color_blue";
   public static final String EXTRA_FILE_PATH = "extra_file_path";
   public static final String EXTRA_FILE_TYPE = "extra_file_type";
   public static final String EXTRA_PICKER_TYPE = "extra_picker_type";

   /////////////////////
   // Preference keys //
   /////////////////////
   public static final class preference {
	  private preference() {}
   }
}
