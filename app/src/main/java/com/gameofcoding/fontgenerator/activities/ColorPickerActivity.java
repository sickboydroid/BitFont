package com.gameofcoding.fontgenerator.activities;

import android.app.Activity;
import android.os.Bundle;
import com.gameofcoding.fontgenerator.R;
import android.content.Intent;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.SeekBar;
import android.text.TextWatcher;
import android.text.Editable;
import android.graphics.Color;
import android.view.ViewGroup;

public class ColorPickerActivity extends Activity {
   EditText edColor;
   TextView tvAlpha;
   SeekBar sbAlpha;
   TextView tvRed;
   SeekBar sbRed;
   TextView tvGreen;
   SeekBar sbGreen;
   TextView tvBlue;
   SeekBar sbBlue;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_color_picker);
	  getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);

	  // Initialize views
	  edColor = findViewById(R.id.ed_color);
	  tvAlpha = findViewById(R.id.tv_alpha);
	  sbAlpha = findViewById(R.id.sb_alpha);
	  tvRed = findViewById(R.id.tv_red);
	  sbRed = findViewById(R.id.sb_red);
	  tvGreen = findViewById(R.id.tv_green);
	  sbGreen = findViewById(R.id.sb_green);
	  tvBlue = findViewById(R.id.tv_blue);
	  sbBlue = findViewById(R.id.sb_blue);

	  // Setup color EditText
	  edColor.addTextChangedListener(new TextWatcher() {
			String prevText;
			@Override public void beforeTextChanged(CharSequence text, int p2, int p3, int p4) {
			   prevText = text.toString();
			}
			@Override public void afterTextChanged(Editable p1) {}

			@Override
			public void onTextChanged(CharSequence text, int p2, int p3, int p4) {
			}
		 });

	  // Setup seekbars
	  sbAlpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onStartTrackingTouch(SeekBar seekbar) {}
			@Override public void onStopTrackingTouch(SeekBar seekbar) {}

			@Override
			public void onProgressChanged(SeekBar seekbar, int prog, boolean fromTouch) {
			   tvAlpha.setText(String.valueOf(prog));
			   updateColorEdittext();
			}
		 });
	  sbRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onStartTrackingTouch(SeekBar seekbar) {}
			@Override public void onStopTrackingTouch(SeekBar seekbar) {}

			@Override
			public void onProgressChanged(SeekBar seekbar, int prog, boolean fromTouch) {
			   tvRed.setText(String.valueOf(prog));
			   updateColorEdittext();
			}
		 });
	  sbGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onStartTrackingTouch(SeekBar seekbar) {}
			@Override public void onStopTrackingTouch(SeekBar seekbar) {}

			@Override
			public void onProgressChanged(SeekBar seekbar, int prog, boolean fromTouch) {
			   tvGreen.setText(String.valueOf(prog));
			   updateColorEdittext();
			}
		 });
	  sbBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override public void onStartTrackingTouch(SeekBar seekbar) {}
			@Override public void onStopTrackingTouch(SeekBar seekbar) {}

			@Override
			public void onProgressChanged(SeekBar seekbar, int prog, boolean fromTouch) {
			   tvBlue.setText(String.valueOf(prog));
			   updateColorEdittext();
			}
		 });
   }

   public void updateColorEdittext() {
	  int colorARGB = Color.argb(sbAlpha.getProgress(),
								 sbRed.getProgress(),
								 sbGreen.getProgress(),
								 sbBlue.getProgress());
	  int colorRGB = Color.rgb(sbRed.getProgress(),
							   sbGreen.getProgress(),
							   sbBlue.getProgress());
	  String hexColor = String.format("#%06X", (0xFFFFFFFF & colorARGB));
	  int textColor = colorRGB ^ 0xFFFFFF;
	  edColor.setTextColor(textColor);
	  edColor.setText(hexColor);
	  edColor.setBackgroundColor(colorARGB);
   }

   @Override
   public void onBackPressed() {
	  Intent result = new Intent();
	  result.putExtra(AppConstants.EXTRA_COLOR_ALPHA, sbAlpha.getProgress());
	  result.putExtra(AppConstants.EXTRA_COLOR_RED, sbRed.getProgress());
	  result.putExtra(AppConstants.EXTRA_COLOR_GREEN, sbGreen.getProgress());
	  result.putExtra(AppConstants.EXTRA_COLOR_BLUE, sbBlue.getProgress());
	  setResult(RESULT_OK, result);
	  super.onBackPressed();
   }
}
