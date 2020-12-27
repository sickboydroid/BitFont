package com.gameofcoding.fontgenerator.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.gameofcoding.fontgenerator.R;
import com.gameofcoding.fontgenerator.generator.FontGenerator;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import com.gameofcoding.fontgenerator.utils.Utils;
import java.io.File;
import javax.microedition.khronos.opengles.GL10;
import android.app.Activity;
import com.gameofcoding.fontgenerator.utils.XLog;
import android.view.Gravity;

public class ConfigFontActivity extends Activity implements OnClickListener {
   public static final String TAG = "ConfigFontActivity";
   public static final int REQUEST_CODE_PICK_DIR = 0x1;
   private File mFontFile;
   File mDestDir;
   EditText edFontName;
   EditText edFontSize;
   EditText edPageSize;
   Button btnSelectDestDir;
   TextView tvSelectDestDir;
   Button btnGenerateFont;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  // Load source font file
	  mFontFile = new File(getIntent().getStringExtra(AppConstants.EXTRA_FILE_PATH));
	  if (!mFontFile.exists()) {
		 Toast.makeText(getApplicationContext(), R.string.file_does_not_exist, Toast.LENGTH_LONG).show();
		 finish();
	  }

	  setContentView(R.layout.activity_config_font);
	  edFontName = findViewById(R.id.ed_font_name);
	  edFontSize = findViewById(R.id.ed_font_size);
	  edPageSize = findViewById(R.id.ed_page_size);
	  btnSelectDestDir = findViewById(R.id.btn_select_dest_dir);
	  tvSelectDestDir = findViewById(R.id.tv_dest_dir);
	  btnGenerateFont = findViewById(R.id.btn_generate_font);

	  // configure EditTexts
	  edFontName.setText(mFontFile.getName().substring(0, mFontFile.getName().lastIndexOf(".")));
	  edFontSize.setText(String.valueOf(FontGenerator.DEF_FONT_SIZE));
	  edPageSize.setText(String.valueOf(FontGenerator.DEF_PAGE_SIZE));
	  TextWatcher textWatcher = new TextWatcher() {
		 @Override public void beforeTextChanged(CharSequence p1, int p2, int p3, int p4) {}

		 @Override
		 public void onTextChanged(CharSequence text, int p2, int p3, int p4) {
			handleTextChangedEvents();
		 }

		 @Override public void afterTextChanged(Editable ed) {}
	  };

	  edFontName.addTextChangedListener(textWatcher);
	  edFontSize.addTextChangedListener(textWatcher);
	  edPageSize.addTextChangedListener(textWatcher);

	  // configure Buttons
	  btnSelectDestDir.setOnClickListener(this);
	  btnGenerateFont.setOnClickListener(this);

	  setDestDir(Utils.getDefaultExtDir());
   };

   @Override
   public void onClick(View view) {
	  switch (view.getId()) {
		 case R.id.btn_select_dest_dir:
			selectDestDir();
			break;
		 case R.id.btn_generate_font:
			generateFont();
			break;
	  }
   }

   public void handleTextChangedEvents() {
	  boolean canGenerateFont = true;
	  String fontName = edFontName.getText().toString();
	  int fontSize = -1;
	  try {
		 if (!edFontSize.getText().toString().isEmpty())
			fontSize = Integer.valueOf(edFontSize.getText().toString());
	  } catch (NumberFormatException e) {
		 XLog.v(TAG, "Excep. occured while getting font size from EditText", e);
		 fontSize = Integer.MAX_VALUE;
	  }

	  int pageSize = -1;
	  try {
		 if (!edPageSize.getText().toString().isEmpty())
			pageSize = Integer.valueOf(edPageSize.getText().toString());
	  } catch (NumberFormatException e) {
		 XLog.v(TAG, "Excep. occured while getting page size from EditText", e);
		 pageSize = Integer.MAX_VALUE;
	  }
	  
	  // Font name
	  if (fontName.length() <= 0) {
		 canGenerateFont = false;
		 edFontName.setError(getString(R.string.warn_empty_edittext));
	  } else {
		 edFontName.setError(null);
	  }

	  // Font size
	  if (fontSize < 0) {
		 canGenerateFont = false;
		 edFontSize.setError(getString(R.string.warn_invalid_number));
	  }
	  if (fontSize < FontGenerator.MIN_FONT_SIZE) {
		 canGenerateFont = false;
		 edFontSize.setError(String.format(getString(R.string.warn_enter_higher_num),
										   FontGenerator.MIN_FONT_SIZE));
	  } else if (fontSize > FontGenerator.MAX_FONT_SIZE) {
		 canGenerateFont = false;
		 edFontSize.setError(String.format(getString(R.string.warn_enter_smaller_num),
										   FontGenerator.MAX_FONT_SIZE));
	  } else if (fontSize > pageSize) {
		 canGenerateFont = false;
		 edFontSize.setError(getString(R.string.warn_font_greater_than_page));
	  } else {
		 edFontSize.setError(null);
	  }

	  // Page size
	  if (pageSize < 0) {
		 canGenerateFont = false;
		 edPageSize.setError(getString(R.string.warn_invalid_number));
	  }
	  if (pageSize < FontGenerator.MIN_PAGE_SIZE) {
		 canGenerateFont = false;
		 edPageSize.setError(String.format(getString(R.string.warn_enter_higher_num),
										   FontGenerator.MIN_PAGE_SIZE));
	  } else if (pageSize > FontGenerator.MAX_PAGE_SIZE) {
		 canGenerateFont = false;
		 edPageSize.setError(String.format(getString(R.string.warn_enter_smaller_num),
										   FontGenerator.MAX_PAGE_SIZE));
	  } else {
		 edPageSize.setError(null);
	  }

	  // Set 'Generate' button either 'enabled' or 'disabled'
	  btnGenerateFont.setEnabled(canGenerateFont);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
		 case REQUEST_CODE_PICK_DIR:
			if (resultCode == RESULT_OK)
			   setDestDir(new File(data.getStringExtra(AppConstants.EXTRA_FILE_PATH)));
			break;
	  }
	  super.onActivityResult(requestCode, resultCode, data);
   }

   private void selectDestDir() {
	  Intent intent = new Intent(this, FilePickerActivity.class);
	  intent.putExtra(AppConstants.EXTRA_FILE_TYPE,
					  FilePickerActivity.SELECT_DIR);
	  startActivityForResult(intent, REQUEST_CODE_PICK_DIR);
   }

   private void generateFont() {
	  if (!getDestDir().isDirectory()) {
		 Utils.showToast(getApplicationContext(), R.string.request_select_dir);
		 return;
	  }
	  Toast toast = Toast.makeText(getApplicationContext(), R.string.generating, Toast.LENGTH_LONG);
	  toast.setGravity(Gravity.TOP, 0, 0);
	  toast.show();

	  FontGenerator generator = new FontGenerator()
		 .setFontName(edFontName.getText().toString())
		 .setDestDir(getDestDir())
		 .setFontFile(mFontFile)
		 .setFontSize(Integer.valueOf(edFontSize.getText().toString()))
		 .setPageSize(Integer.valueOf(edPageSize.getText().toString()));
	  GeneratorLibGDXActivity.generator = generator;
	  startActivity(new Intent(ConfigFontActivity.this, GeneratorLibGDXActivity.class));
   }

   public void setDestDir(File dir) {
	  if (dir != null && dir.isDirectory()) {
		 mDestDir = dir;
		 tvSelectDestDir.setText(dir.toString());
	  }
   }

   public File getDestDir() {
	  if (mDestDir == null)
		 return mDestDir;
	  return Utils.getDefaultExtDir();
   }

}
