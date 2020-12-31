package com.gameofcoding.fontgenerator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.gameofcoding.fontgenerator.R;
import java.io.File;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.view.View;
import com.gameofcoding.fontgenerator.utils.Utils;
import com.gameofcoding.fontgenerator.adapters.FilePickerListViewAdapter;
import android.widget.TextView;
import android.os.Environment;
import android.content.Intent;
import com.gameofcoding.fontgenerator.utils.AppConstants;
import android.widget.Button;
import android.view.ViewGroup;

public class FilePickerActivity extends Activity {
   public static final int PICK_FILE = 0x0;
   public static final int PICK_FILE_TYPE_FONT = 0x0;
   public static final int SELECT_DIR = 0x1;

   ListView mListView;
   TextView mTvEmptyFolder;
   ArrayList<File> currentFiles;
   int mFileType;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  setContentView(R.layout.activity_file_picker);
	  getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
							ViewGroup.LayoutParams.MATCH_PARENT);

	  // initialize views
	  TextView tvGoUp = findViewById(R.id.tv_go_up);
	  mListView = findViewById(R.id.list_view);
	  mTvEmptyFolder = findViewById(R.id.tv_empty_directory);
	  Button btnOk = findViewById(R.id.btn_ok);
	  Button btnCancel = findViewById(R.id.btn_cancel);

	  mFileType = getIntent().getIntExtra(AppConstants.EXTRA_FILE_TYPE, PICK_FILE);

	  tvGoUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			   goUpDir();
			}
		 });

	  resetAdapter(Environment.getExternalStorageDirectory());
	  mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
			   File selectedItem = ((FilePickerListViewAdapter) mListView.getAdapter()).getItem(position);
			   if (selectedItem.isDirectory()) {
				  resetAdapter(selectedItem);
			   } else {
				  finish(selectedItem);
			   }
			}
		 });

	  if (mFileType != SELECT_DIR) {
		 findViewById(R.id.view_btn_space).setVisibility(View.GONE);
		 btnOk.setVisibility(View.GONE);
	  } else {
		 btnOk.setOnClickListener(new View.OnClickListener() {
			   @Override
			   public void onClick(View view) {
				  File currDir = ((FilePickerListViewAdapter) mListView.getAdapter()).getCurrentRoot();
				  finish(currDir);
			   }
			});
	  }
	  btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
			   finish(null);
			}
		 });
   }

   public void finish(File result) {
	  if (result != null)
		 setResult(RESULT_OK, new Intent().putExtra(AppConstants.EXTRA_FILE_PATH, result.toString()));
	  else
		 setResult(RESULT_CANCELED);
	  finish();
   } 

   @Override
   public void onBackPressed() {
	  if (!goUpDir()) {
		 finish(null);
	  }
   }

   public boolean goUpDir() {
	  File file = ((FilePickerListViewAdapter) mListView.getAdapter()).getCurrentRoot();
	  if (!file.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
		 resetAdapter(file.getParentFile());
		 return true;
	  }
	  return false;
   }

   public void resetAdapter(File dir) {
	  FilePickerListViewAdapter adapter =
		 new FilePickerListViewAdapter(mFileType, getApplicationContext(), dir);
	  mListView.setAdapter(adapter);
	  if (adapter.getCount() == 0) {
		 if (mListView.getVisibility() != View.GONE) {
			mListView.setVisibility(View.GONE);
			mTvEmptyFolder.setVisibility(View.VISIBLE);
		 }
	  } else {
		 if (mListView.getVisibility() != View.VISIBLE) {
			mListView.setVisibility(View.VISIBLE);
			mTvEmptyFolder.setVisibility(View.GONE);
		 }
	  }
   }
}
