package com.android.gumshoeUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class SQLFind extends Activity{
	private AlertDialog confirmAlert;
	private boolean allChecked = false;
	private Button btnSelectAll, btnShred;
	private Context context;
	private ListView dbView;
	private ProgressDialog progD;
	private List<String> dbCmd, dbList, dbHardList;
	private String formatMe;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sqlfind);
        dbView = (ListView) findViewById(R.id.lsvDBList);
		dbCmd = new ArrayList<String>();
		dbList = new ArrayList<String>();
		dbHardList = new ArrayList<String>();
		dbCmd.add("find /data/data -name *.db > /data/data/com.android.gumshoe/dbList");
		try {
			GumshoeMethodAppendix.getInstance().terminalParse(dbCmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			xferFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.actiontext,dbList);
        dbView.setAdapter(adapter);
        dbView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(arg1.getContext(), SQLHide.class);
				i.putExtra("dbPath", dbHardList.get(arg2));
				arg1.getContext().startActivity(i);
			}
		});
	}
	
	private void xferFile() throws IOException{
		FileReader fileReader = new FileReader("/data/data/com.android.gumshoe/dbList");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        	dbHardList.add(line);
        	formatMe = line.substring(11,line.indexOf("/",11));
            dbList.add(line.substring(line.lastIndexOf("/")+1));
        }
        bufferedReader.close();
	}
}
