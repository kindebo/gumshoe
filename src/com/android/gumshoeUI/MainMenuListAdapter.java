package com.android.gumshoeUI;

import com.android.gumshoe.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainMenuListAdapter extends ArrayAdapter<String> {

	public MainMenuListAdapter(Context context, int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
		menuText = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
		View row = inflater.inflate(R.layout.row, parent, false);
		TextView label = (TextView) row.findViewById(R.id.text);
		//Typeface font = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/Coalition.ttf");
		ImageView icon = (ImageView) row.findViewById(R.id.icon);
		//label.setTypeface(font);
		label.setText(menuText[position]);
		
		icon.setImageResource(img[position]);
		return row;
	}

	String[] menuText;
	int[] img = { 0x7f020001, 0x7f02000e, 0x7f020015, 0x7f02000f, 0x7f020011, 0x7f020013, 0x7f020005, 0x7f02000b, 0x7f020014, 0x7f020012 };
}
