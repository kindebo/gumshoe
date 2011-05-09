package com.android.gumshoeUI;

import java.io.File;
import java.io.FileFilter;
import java.util.Stack;

import com.android.gumshoe.R;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class FileBrowser extends ListActivity {
	
	private File[] files;
	private FileFilter filter;
	private Stack<File> dirStack;
	private File curDir;
	private StateEnum state;
	private TextView txtPath;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.filebrowser);
		filter = new HiddenFileFilter();
		dirStack = new Stack<File>();
		dirStack.add(Environment.getExternalStorageDirectory());
		files = Environment.getExternalStorageDirectory().listFiles(filter);
		curDir = Environment.getExternalStorageDirectory();
		populateList(files);
		txtPath = (TextView) findViewById(R.id.tvPath);
		updatePath();
		ImageView btnBack = (ImageView) findViewById(R.id.imageView1);
		btnBack.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(!dirStack.isEmpty()){
					curDir = dirStack.pop();
					files = curDir.listFiles(filter);
					updatePath();
					populateList(files);
				} else {
					finish();
				}
			}
		});
		
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		File file = files[position];
		if (file.isDirectory()) {
			files = file.listFiles(filter);
			curDir = file;
			dirStack.add(new File(file.getParent()));
			updatePath();
			populateList(files);
		} else {
			Dialog dialog = new FileActionsDialog(this, file);
			dialog.show();
		}
	}

	private void populateList(File[] files) {
		ArrayAdapter<File> fileList = new FileBrowserListAdapter(this, R.layout.fbrow, files);
		fileList.sort(null);
		setListAdapter(fileList);
	}

	private void updatePath(){
		txtPath.setText(curDir.getPath());
	}
	
	public void refreshList(){
		files = curDir.listFiles(filter);
		populateList(files);
	}
	
	private class HiddenFileFilter implements FileFilter{
		@Override
		public boolean accept(File f) {
			return !f.isHidden();
		}
	}
}
