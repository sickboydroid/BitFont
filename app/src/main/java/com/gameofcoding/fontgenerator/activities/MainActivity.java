package com.gameofcoding.fontgenerator.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import com.gameofcoding.fontgenerator.R;
import com.gameofcoding.fontgenerator.activities.MainActivity;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import com.gameofcoding.fontgenerator.utils.Utils;
import com.gameofcoding.fontgenerator.utils.XLog;

public class MainActivity extends Activity {
   private static final int REQUEST_CODE_PICK_FONT = 0x0;
   private static final int REQUEST_CODE_PERMISSION = 0x1;
   private static final int REQUEST_CODE_OPEN_SETTINGS = 0x2;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_main);

	  Button btnSelectFont = findViewById(R.id.select_font_file);
	  btnSelectFont.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			   if (!hasStoragePermission()) {
				  requestStoragePermission();
				  return;
			   }
			   Intent filePicker = new Intent(MainActivity.this, FilePickerActivity.class);
			   filePicker.putExtra(AppConstants.EXTRA_FILE_TYPE,
								   FilePickerActivity.PICK_FILE_TYPE_FONT);
			   startActivityForResult(filePicker, REQUEST_CODE_PICK_FONT);
			}
		 });
   }

   private void requestStoragePermission() {
	  requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
						 REQUEST_CODE_PERMISSION);
   }

   public boolean hasStoragePermission() {
	  return new Utils(getApplicationContext()).hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
   }

   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	  switch (requestCode) {
		 case REQUEST_CODE_PICK_FONT:
			if (resultCode == RESULT_OK) {
			   Intent configBitmapFontActivity = new Intent(MainActivity.this,
															ConfigFontActivity.class);
			   configBitmapFontActivity.putExtra(AppConstants.EXTRA_FILE_PATH,
												 data.getStringExtra(AppConstants.EXTRA_FILE_PATH));
			   startActivity(configBitmapFontActivity);
			} else {
			   Utils.showToast(getApplicationContext(), R.string.request_pick_file);
			}
			break;
		 case REQUEST_CODE_OPEN_SETTINGS:
			handlePermissionResult();
			break;
	  }
	  super.onActivityResult(requestCode, resultCode, data);
   }

   @Override
   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
	  super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	  switch(requestCode) {
		 case REQUEST_CODE_PERMISSION:
			handlePermissionResult();
			break;
	  }
   }

   private void handlePermissionResult() {
	  if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
		 return;
	  if (hasStoragePermission()) {
		 // User granted this permission, return
		 return;
	  }

	  // User not granted permission
	  AlertDialog.Builder permissionRequestDialog = new AlertDialog.Builder(this)
		 .setTitle(R.string.dialog_permission_title)
		 .setMessage(R.string.dialog_permission_message)
		 .setCancelable(false)
		 .setNegativeButton(R.string.exit,
		 new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
			   Utils.showToast(getApplicationContext(), R.string.closing_app);
			   finish();
			}
		 });
	  if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
		 // User clicked on "Don't ask again", show dialog to navigate him to
		 // settings
		 permissionRequestDialog
			.setPositiveButton(R.string.go_to_settings,
			new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog,
								   int whichButton) {
				  Intent intent =
					 new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
				  Uri uri =
					 Uri.fromParts("package", getPackageName(), null);
				  intent.setData(uri);
				  startActivityForResult(intent,
										 REQUEST_CODE_OPEN_SETTINGS);
			   }
			})
			.show();
	  } else {
		 // User clikced on 'deny', prompt again for permissions
		 permissionRequestDialog
			.setPositiveButton(R.string.try_again,
			new DialogInterface.OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog,
								   int whichButton) {
				  requestStoragePermission();
			   }
			})
			.show();
	  }
	  return;
   }
}
