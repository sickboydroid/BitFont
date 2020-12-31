package com.gameofcoding.fontgenerator.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.gameofcoding.fontgenerator.R;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import com.gameofcoding.fontgenerator.utils.Utils;
import com.rarepebble.colorpicker.ColorPickerView;
import android.graphics.Color;
import com.gameofcoding.fontgenerator.generator.FontGenerator;
import com.gameofcoding.fontgenerator.utils.XLog;

public class ColorPickerActivity extends Activity {
   private static final String TAG = "ColorPickerActivity";
   public static final int TYPE_PICK_FROM_PICKER = 0x0;
   public static final int TYPE_PICK_FROM_GRADIENT = 0x1;
   public static final int TYPE_PICK_FROM_PLATTE = 0x2;
   private int mPickerType;
   Color color;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  handleIntent();
	  switch (mPickerType) {
		 case TYPE_PICK_FROM_PLATTE:
			setUpLayoutPlatte();
			break;
		 case TYPE_PICK_FROM_PICKER:
			setUpLayoutPicker();
			break;
		 case TYPE_PICK_FROM_GRADIENT:
			setUpLayoutGradient();
			break;
	  }
	  getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
   }

   private void setUpLayoutPlatte() {
   }
   
   private void setUpLayoutPicker() {
	  setContentView(R.layout.layout_color_picker);
	  final ColorPickerView colorPickerView = findViewById(R.id.colorPicker);
	  colorPickerView.setColor(getColor().toArgb());
	  Button btnOk = findViewById(R.id.btn_ok);
	  Button btnCancel = findViewById(R.id.btn_cancel);
	  btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			   setColor(Color.valueOf(colorPickerView.getColor()));
			   finish();
			}
		 });
	  btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			   finish();
			}
		 });
   }

   private void setUpLayoutGradient() {
   }
   
   private void handleIntent() {
	  Intent intent = getIntent();
	  setColor(Color.valueOf(intent.getIntExtra(AppConstants.EXTRA_COLOR, 
												FontGenerator.DEF_FONT_COLOR.toIntBits())));
	  mPickerType = intent.getIntExtra(AppConstants.EXTRA_PICKER_TYPE, TYPE_PICK_FROM_PLATTE);
   }

   @Override
   public void finish() {
	  Color color = getColor();
	  if (color != null) {
		 Intent result = new Intent();
		 result.putExtra(AppConstants.EXTRA_COLOR, getColor().toArgb());
		 setResult(RESULT_OK, result);
	  } else {
		 setResult(RESULT_CANCELED);
	  }
	  super.finish();
   }

   public void setColor(Color color) {
	  this.color = color;
   }

   public Color getColor() {
	  return color;
   }
}
