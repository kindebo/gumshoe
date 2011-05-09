package com.android.gumshoe;

import com.android.gumshoe.R;
import com.android.gumshoeUI.MainMenuListAdapter;
import com.android.gumshoeUI.MainMenuOnClickListener;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
/*
 * This class implements a menu system then calls external activities on list item press.
 * The menu is a custom scroll list with icons and text labels
 */
public class Application extends Activity {
    String[] menuText = {"Backup / Restore", "Data Hiding & Encryption", "SMS Encryption", "Log Manipulator", "DB Nuker", "Data Stalker", "Secret SQL", "Panic Button", "Extension Eraser", "Custom Scripts Manager"};    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        TextView titleTV = (TextView) findViewById(R.id.textView);
        TextView versionTV = (TextView) findViewById(R.id.textView1);
        TextView subtitleTV = (TextView) findViewById(R.id.textView3);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/defused.ttf");
        Typeface font2 = Typeface.createFromAsset(getAssets(), "fonts/PostinkantajaJob.ttf");
        titleTV.setTypeface(font);
        versionTV.setTypeface(font);
        subtitleTV.setTypeface(font2);
        ListView lv = (ListView) findViewById(R.id.list);
        lv.setAdapter(new MainMenuListAdapter(this, R.layout.row, menuText));
        // Set listener to menu class
        lv.setOnItemClickListener(new MainMenuOnClickListener());
    }
}
