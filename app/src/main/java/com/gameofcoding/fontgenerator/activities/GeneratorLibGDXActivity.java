package com.gameofcoding.fontgenerator.activities;

import android.os.Bundle;
import android.widget.Toast;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gameofcoding.fontgenerator.R;
import com.gameofcoding.fontgenerator.generator.FontGenerator;
import javax.microedition.khronos.opengles.GL10;
import android.widget.LinearLayout;
import android.view.ViewGroup.LayoutParams;
import com.badlogic.gdx.utils.TimeUtils;

public class GeneratorLibGDXActivity extends AndroidApplication {
   public static FontGenerator generator;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  final AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
	  cfg.hideStatusBar = false;
	  cfg.useImmersiveMode = false;
	  cfg.disableAudio = true;
	  cfg.useAccelerometer = false;
	  cfg.useGyroscope = false;
	  cfg.useCompass = false;
	  LinearLayout rootView = new LinearLayout(getApplicationContext());
	  rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	  rootView.addView(initializeForView(new LibGDXGame(generator), cfg));
	  setContentView(rootView);
   }

   class LibGDXGame extends ApplicationAdapter {
	  private int WORLD_WIDTH;
	  private int WORLD_HEIGHT;
	  private long startTime;
	  private float previewFontScale;
	  private boolean generatingFont;
	  private BitmapFont generatedFont;
	  GlyphLayout layoutFontPreview;
	  FontGenerator generator;
	  SpriteBatch batch;
	  BitmapFont fontBold;

	  public LibGDXGame(FontGenerator generator) {
		 this.generator = generator;
	  }

	  @Override
	  public void create() {
		 batch = new SpriteBatch();
		 fontBold = new BitmapFont(Gdx.files.internal("fonts/NotoSans-Bold.fnt"), 
								   Gdx.files.internal("fonts/NotoSans-Bold.png"), false);
		 startGeneratingFont();
		 startTime = TimeUtils.millis();
	  }

	  private void startGeneratingFont() {
		 Thread fontGeneratingThread = new Thread(new Runnable() {
			   @Override
			   public void run() {
				  generatingFont = true;
				  generator.saveFont();
				  generatingFont = false;
			   }
			});
		 fontGeneratingThread.setPriority(Thread.MAX_PRIORITY);
		 fontGeneratingThread.start();
	  }

	  @Override
	  public void render() {
		 Gdx.gl.glClearColor(1, 1, 1, 1f);
		 Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		 batch.begin();
		 if (generatingFont || TimeUtils.millis() - startTime < 3500) {
			showGeneratingText();
		 } else  {
			showDoneText();
			showFontPreview();
			if (Gdx.input.isTouched())
			   finish();
		 }
		 batch.end();
	  }

	  private void showGeneratingText() {
		 // Text "Generating..."
		 fontBold.getData().setScale(0.5f);
		 final GlyphLayout layoutGenerating = new GlyphLayout(fontBold,
															  "Generating...");
		 fontBold.draw(batch, layoutGenerating,
					   (WORLD_WIDTH - layoutGenerating.width) / 2, 
					   (WORLD_HEIGHT - layoutGenerating.height) / 2);
		 // Text "Generating..."
		 fontBold.getData().setScale(0.3f);
		 final GlyphLayout layoutPleaseWait = new GlyphLayout(fontBold,
															  "(Please wait)");
		 fontBold.draw(batch, layoutPleaseWait,
					   (WORLD_WIDTH - layoutPleaseWait.width) / 2, 
					   (WORLD_HEIGHT - layoutPleaseWait.height) / 3);
	  }

	  private void showDoneText() {
		 // Text "Don!"
		 fontBold.getData().setScale(0.5f);
		 final GlyphLayout layoutGenerated = new GlyphLayout(fontBold,
															 "Done!");
		 fontBold.draw(batch, layoutGenerated, 
					   (WORLD_WIDTH - layoutGenerated.width) / 2, 
					   (WORLD_HEIGHT - layoutGenerated.height) / 2);

		 // Text "(Tap anywhere to continue)"
		 fontBold.getData().setScale(0.3f);
		 final GlyphLayout layoutTapToContinue = new GlyphLayout(fontBold,
																 "(Tap anywhere to continue)");
		 fontBold.draw(batch, layoutTapToContinue,
					   (WORLD_WIDTH - layoutTapToContinue.width) / 2, 
					   (WORLD_HEIGHT - layoutTapToContinue.height) / 3);
	  }

	  public void showFontPreview() {
		 if (generatedFont == null) {
			generatedFont = new BitmapFont(generator.getDestFntFile(), false);
			previewFontScale = 1;
			do {
			   generatedFont.getData().setScale(previewFontScale);
			   layoutFontPreview = new GlyphLayout(generatedFont,
												   "Preview of generated font");
			   previewFontScale -= 0.001f;
			} while(layoutFontPreview.width >= (WORLD_WIDTH - 20));
		 }
		 // Text "Preview of generated font"
		 generatedFont.draw(batch, layoutFontPreview, 
							(WORLD_WIDTH - layoutFontPreview.width) / 2, 
							(WORLD_HEIGHT - layoutFontPreview.height));

		 // Text "(Scale: #%)"
		 GlyphLayout layoutFontScale = new GlyphLayout(generatedFont,
													   "(Scale: " + Math.round((previewFontScale * 100)) + "%)");
		 generatedFont.draw(batch, layoutFontScale, 
							(WORLD_WIDTH - layoutFontScale.width) / 2, 
							(WORLD_HEIGHT - layoutFontScale.height) - (layoutFontPreview.height * 1.3f));
	  }

	  @Override
	  public void resize(int width, int height) {
		 super.resize(width, height);
		 WORLD_WIDTH = width;
		 WORLD_HEIGHT = height;
//		 if (width > height) {
//			WORLD_WIDTH = height;
//			WORLD_HEIGHT = width;
//		 } else {
//			WORLD_WIDTH = width;
//			WORLD_HEIGHT = height;
//		 }
	  }
   }

   @Override
   public void onBackPressed() {
   }
}
