package com.gameofcoding.fontgenerator.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.gameofcoding.fontgenerator.R;
import java.io.File;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Arrays;
import java.io.FileFilter;
import com.gameofcoding.fontgenerator.activities.FilePickerActivity;
import com.gameofcoding.fontgenerator.utils.XLog;

public class FilePickerListViewAdapter extends BaseAdapter {
   private static final String TAG = "FilePickerListViewAdapter";
   private Context context;
   private int mFileType;
   ArrayList<File> files;
   File root;

   public FilePickerListViewAdapter(int fileType, Context context, File root) {
	  mFileType = fileType;
	  this.context = context;
	  this.root = root;
	  loadFiles();
   }

   public File getCurrentRoot() {
	  return root;
   }

   public void loadFiles() {
	  ArrayList<File> files = new ArrayList<File>();
	  if (getCurrentRoot() == null) {
		 XLog.w(TAG, "loadFile(): getCurrentRoot() returned null, cannot load files!");
		 return; 
	  }
	  File[] arrFiles = getCurrentRoot().listFiles();
	  Arrays.sort(arrFiles);

	  // dirs
	  for (File file : arrFiles) {
		 if (file.isDirectory() && !(file.isHidden()))
			files.add(file);
	  }

	  // files
	  if (mFileType == FilePickerActivity.PICK_FILE || mFileType == FilePickerActivity.PICK_FILE_TYPE_FONT) {
		 for (File file : arrFiles) {
			if (file.isFile()) {
			   if (mFileType == FilePickerActivity.PICK_FILE_TYPE_FONT) {
				  String fileName = file.getName();
				  if (fileName.endsWith(".ttf") || fileName.endsWith(".otf") || fileName.endsWith(".ttf"))
					 files.add(file);
			   } else {
				  files.add(file);
			   }
			}
		 }
	  }
	  this.files = files;
   }

   @Override
   public int getCount() {
	  return files.size();
   }

   @Override
   public File getItem(int position) {
	  return files.get(position);
   }

   @Override
   public long getItemId(int position) {
	  return 0;
   }

   @Override
   public View getView(int position, View view, ViewGroup parent) {
	  View layout = LayoutInflater.from(context).inflate(R.layout.layout_file_picker_row, null);
	  ImageView icon = layout.findViewById(R.id.img_icon);
	  TextView fileName = layout.findViewById(R.id.tv_file_name);
	  File file = files.get(position);
	  if (file.isDirectory())
		 icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_folder));
	  else
		 icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_file));
	  fileName.setText(file.getName());
	  return layout;
   }
}
