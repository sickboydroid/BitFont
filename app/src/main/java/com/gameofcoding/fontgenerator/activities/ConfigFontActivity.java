package com.gameofcoding.fontgenerator.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.gameofcoding.fontgenerator.R;
import com.gameofcoding.fontgenerator.generator.FontGenerator;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import com.gameofcoding.fontgenerator.utils.ColorWrapper;
import com.gameofcoding.fontgenerator.utils.Utils;
import com.gameofcoding.fontgenerator.utils.XLog;
import java.io.File;

public class ConfigFontActivity extends Activity implements OnClickListener {
   public static final String TAG = "ConfigFontActivity";
   public static final int REQUEST_CODE_PICK_DIR = 0x1;
   public static final int REQUEST_CODE_PICK_COLOR = 0x2;
   private File mFontFile;
   File mDestDir;
   EditText edFontName;
   EditText edFontSize;
   EditText edPageSize;
   Button btnSelectColor;
   TextView tvSelectedColor;
   Button btnSelectDestDir;
   TextView tvSelectDestDir;
   Button btnGenerateFont;
   Color mFontColor;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  // Load source font file
	  mFontFile = new File(getIntent().getStringExtra(AppConstants.EXTRA_FILE_PATH));
	  if (!mFontFile.exists()) {
		 Toast.makeText(getApplicationContext(), R.string.file_does_not_exist, Toast.LENGTH_LONG).show();
		 finish();
	  }

	  // configure layout
	  setContentView(R.layout.activity_config_font);
	  edFontName = findViewById(R.id.ed_font_name);
	  edFontSize = findViewById(R.id.ed_font_size);
	  edPageSize = findViewById(R.id.ed_page_size);
	  btnSelectColor = findViewById(R.id.btn_select_color);
	  tvSelectedColor = findViewById(R.id.tv_selected_color);
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
	  setDestDir(null);
	  setFontColor(getFontColor());
	  btnSelectColor.setOnClickListener(this);
	  tvSelectedColor.setOnClickListener(this);
	  btnSelectDestDir.setOnClickListener(this);
	  btnGenerateFont.setOnClickListener(this);
   }

   public void setFontColor(Color fontColor) {
	  if (fontColor != null) {
		 String hexColor = ColorWrapper.toHex(fontColor);
		 tvSelectedColor.setText(hexColor);

		 int backgroundColor = fontColor.toArgb();
		 int textColor = backgroundColor ^ 0x00FFFFFF;
		 tvSelectedColor.setBackgroundColor(backgroundColor);
		 tvSelectedColor.setTextColor(textColor);
		 mFontColor = fontColor;
	  }
   }

   public Color getFontColor() {
	  if (mFontColor == null)
		 mFontColor = ColorWrapper.wrap(FontGenerator.DEF_FONT_COLOR);
	  return mFontColor;
   }

   public FontGenerator initFontGenerator() {
	  return new FontGenerator()
		 .setFontName(edFontName.getText().toString())
		 .setDestDir(getDestDir())
		 .setFontFile(mFontFile)
		 .setFontSize(Integer.valueOf(edFontSize.getText().toString()))
		 .setFontColor(ColorWrapper.wrap(getFontColor()))
		 .setPageSize(Integer.valueOf(edPageSize.getText().toString()));
   }

   @Override
   public void onClick(View view) {
	  switch (view.getId()) {
		 case R.id.btn_select_dest_dir:
			selectDestDir();
			break;
		 case R.id.btn_generate_font:
			generateFont();
			break;
		 case R.id.btn_select_color:
			selectColor(view);
			break;
		 case R.id.tv_selected_color:
			copyColor();
			break;
	  }
   }

   private void copyColor() {
	  ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE); 

	  ClipData clip = ClipData.newPlainText("color",
											ColorWrapper.toHex(getFontColor()));
	  clipboard.setPrimaryClip(clip);
	  Utils.showToast(getApplicationContext(), R.string.text_copied_confirmation);

   }

   private void selectColor(View view) {
	  PopupMenu popup = new PopupMenu(ConfigFontActivity.this, view);  
	  popup.getMenuInflater().inflate(R.menu.menu_color_picke_type, popup.getMenu());  

	  popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
			public boolean onMenuItemClick(MenuItem item) {
			   Intent colorPickerActivity = new Intent(ConfigFontActivity.this, ColorPickerActivity.class);
			   switch (item.getItemId()) {
				  case R.id.color_platte:
					 Utils.showToast(getApplicationContext(), R.string.coming_soon);
					 return false;
//					 colorPickerActivity.putExtra(AppConstants.EXTRA_PICKER_TYPE,
//												  ColorPickerActivity.TYPE_PICK_FROM_PLATTE);
					 //break;
				  case R.id.color_picker:
					 colorPickerActivity.putExtra(AppConstants.EXTRA_PICKER_TYPE,
												  ColorPickerActivity.TYPE_PICK_FROM_PICKER);
					 break;
				  case R.id.gradient_color:
					 Utils.showToast(getApplicationContext(), R.string.coming_soon);
					 return false;
//					 colorPickerActivity.putExtra(AppConstants.EXTRA_PICKER_TYPE,
//												  ColorPickerActivity.TYPE_PICK_FROM_GRADIENT);
//					 break;
			   }
			   colorPickerActivity.putExtra(AppConstants.EXTRA_COLOR, mFontColor.toArgb());
			   startActivityForResult(colorPickerActivity, REQUEST_CODE_PICK_COLOR);
			   return true;  
			}  
		 });  
	  popup.show();
   }

   public boolean handleTextChangedEvents() {
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
	  return canGenerateFont;
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
		 case REQUEST_CODE_PICK_DIR:
			if (resultCode == RESULT_OK)
			   setDestDir(new File(data.getStringExtra(AppConstants.EXTRA_FILE_PATH)));
			break;
		 case REQUEST_CODE_PICK_COLOR:
			if (resultCode == RESULT_OK)
			   setFontColor(Color.valueOf(data.getIntExtra(AppConstants.EXTRA_COLOR, 
														   FontGenerator.DEF_FONT_COLOR.toIntBits())));
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
	  toast.setGravity(Gravity.TOP, 0, 120);
	  toast.show();

	  // Check whether all values are valid
	  if (handleTextChangedEvents()) {
		 try {
			GeneratorLibGDXActivity.generator = initFontGenerator();
			startActivity(new Intent(ConfigFontActivity.this, GeneratorLibGDXActivity.class));
		 } finally {
			finish();
		 }
	  }
   }

   public void setDestDir(File dir) {
	  if (dir == null || !dir.isDirectory()) 
		 dir = Utils.getDefaultExtDir();
	  mDestDir = dir;
	  tvSelectDestDir.setText(dir.toString());
   }

   public File getDestDir() {
	  if (mDestDir == null)
		 return mDestDir;
	  return Utils.getDefaultExtDir();
   }

}
