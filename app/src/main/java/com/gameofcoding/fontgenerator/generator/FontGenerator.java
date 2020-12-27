package com.gameofcoding.fontgenerator.generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.File;
import com.badlogic.gdx.graphics.Color;

public class FontGenerator {
   private static final String TAG = "FontGenerator";
   public static final int MIN_FONT_SIZE = 3;
   public static final int MAX_FONT_SIZE = 10_000;
   public static final int MIN_PAGE_SIZE = 10;
   public static final int MAX_PAGE_SIZE = 20_000;
   public static final int DEF_FONT_SIZE = 27;
   public static final int DEF_PAGE_SIZE = 512;

   private FileHandle mFontFile;
   private FileHandle mDestDir;
   private String mFontName;
   private int mFontSize = DEF_FONT_SIZE;
   // TODO: Figure out optimal page size automatically
   private int mPageSize = DEF_PAGE_SIZE;
   private Color mFontColor = new Color(Color.BLACK);

   public FontGenerator() {}

   public FontGenerator(String fontName, File fontFile, File destDir) {
	  setFontName(fontName);
	  setDestDir(destDir);
	  setFontFile(fontFile);
   }

   public boolean saveFont(int fontSize) {
	  File destDir = new File(mDestDir.file(), mFontName);
	  for (int i = 1; destDir.exists(); i++) {
		 destDir = new File(mDestDir.file(), mFontName + " (" + i + ")");
	  }
	  setDestDir(destDir);
	  
	  FreeTypeFontGenerator generator = new FreeTypeFontGenerator(mFontFile);
	  PixmapPacker packer = new PixmapPacker(getPageSize(), getPageSize(), Pixmap.Format.RGBA8888, 2, false);
	  FreeTypeFontParameter parameter = new FreeTypeFontParameter();
	  parameter.size = fontSize;
	  parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;
	  parameter.flip = false;
	  parameter.color.set(getFontColor());
//	  parameter.borderColor.set(getBorderColor());
	  parameter.packer = packer;
	  FreeTypeFontGenerator.FreeTypeBitmapFontData fontData = generator.generateData(parameter);
	  Array<PixmapPacker.Page> pages = packer.getPages();
	  Array<TextureRegion> texRegions = new Array<>();
	  for (int i = 0; i < pages.size; i++) {
		 PixmapPacker.Page p = pages.get(i);
		 Texture tex = new Texture(new PixmapTextureData(p.getPixmap(), p.getPixmap().getFormat(), false, false, true)) {
			@Override
			public void dispose() {
			   super.dispose();
			   getTextureData().consumePixmap().dispose();
			}
		 };
		 tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		 texRegions.add(new TextureRegion(tex));
	  }
	  BitmapFont font = new BitmapFont((BitmapFont.BitmapFontData) fontData, texRegions, false);
	  writeFont(fontSize, font, packer);
	  generator.dispose();
	  packer.dispose();
	  return true;
   }

   public boolean saveFont() {
	  return saveFont(getFontSize());
   }

   private void writeFont(int fontSize, BitmapFont font, PixmapPacker packer) {
	  FileHandle destFntFile = new FileHandle(new File(mDestDir.file(), mFontName + ".fnt"));
	  BitmapFontWriter.setOutputFormat(BitmapFontWriter.OutputFormat.Text);
	  String[] pageRefs = BitmapFontWriter.writePixmaps(packer.getPages(), mDestDir, mFontName);
	  // here we must add the png dir to the page refs
	  for (int i = 0; i < pageRefs.length; i++) {
		 pageRefs[i] = fontSize + "_" + mFontName + "/" + pageRefs[i];
	  }
	  BitmapFontWriter.writeFont(font.getData(), pageRefs, destFntFile, new BitmapFontWriter.FontInfo(mFontName, fontSize), 1, 1);
   }

   //////////////////////
   // GETTERS, SETTERS //
   //////////////////////
   public FontGenerator setDestDir(File dir) {
	  if (dir != null) {
		 if (!dir.isDirectory()) {
			if (!dir.mkdirs())
			   return this;
		 }
		 mDestDir = new FileHandle(dir);
	  }
	  return this;
   }

   public FileHandle getDestDir() {
	  return mDestDir;
   }

   public FontGenerator setFontFile(File file) {
	  if (file != null)
		 mFontFile = new FileHandle(file);
	  return this;
   }

   public FileHandle getFontFile() {
	  return mFontFile;
   }

   public FontGenerator setFontSize(int size) {
	  mFontSize = size;
	  return this;
   } 

   public int getFontSize() {
	  return mFontSize;
   }

   public FontGenerator setFontName(String name) {
	  mFontName = name;
	  return this;
   }

   public String getFontName() {
	  return mFontName;
   }

   public FontGenerator setPageSize(int size) {
	  mPageSize = size;
	  return this;
   }

   public int getPageSize() {
	  return mPageSize;
   }
   
   public void setFontColor(int r, int g, int b, int alpha) {
	  mFontColor.set(r/255, g/255, b/255, alpha/255);
   }
   
   public Color getFontColor() {
	  return mFontColor;
   }
}
