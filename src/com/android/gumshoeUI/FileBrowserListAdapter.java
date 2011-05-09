package com.android.gumshoeUI;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gumshoe.R;

public class FileBrowserListAdapter extends ArrayAdapter<File> {
	private int megaByte = 1048576;
	private String metric;

	public FileBrowserListAdapter(Context context, int resource, File[] files) {
		super(context, resource, files);
		this.files = files;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		View row = inflater.inflate(R.layout.fbrow, parent, false);

		TextView label = (TextView) row.findViewById(R.id.text);
		label.setText(files[position].getName());
		TextView label2 = (TextView) row.findViewById(R.id.tvSize);
		
		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		if (files[position].isDirectory()) {
			icon.setImageResource(R.drawable.filebrowser_folder);
			label2.setText("Directory");
		} else {
			icon.setImageResource(R.drawable.filebrowser_file);
			DecimalFormat df = new DecimalFormat("#.##");
			Long length = files[position].length();
			if ( length < megaByte){
				metric = "KB";
			} else {
				metric = "MB";
			}
			label2.setText(df.format(inMB(files[position].length()))+" "+metric);
		}
		
		return row;
	}

	File[] files;

	private double inMB(long bytes){
		double mb = bytes;
		if (bytes < megaByte ){
			mb /= 1024;
		} else {
		mb /= 1024;
		mb /= 1024;
		}
		return mb;
	}
}
