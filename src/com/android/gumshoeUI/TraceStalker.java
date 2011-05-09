package com.android.gumshoeUI;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;


public class TraceStalker extends Activity {
	private ArrayAdapter<String> adapter;
	private Button btnSearch, btnSendTo;
	private EditText edtSearch, edtPath;
	private List<String> cmdList, stdOut, filePath;
	private ListView lsvStdOut;
	private ProgressDialog progDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tracestalker);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSendTo = (Button) findViewById(R.id.btnSendTo);
        btnSearch.setOnClickListener(l);
        btnSendTo.setOnClickListener(x);
        edtSearch = (EditText) findViewById(R.id.edtQuery);
        edtPath = (EditText) findViewById(R.id.edtPath);
        String[] splash = { "", "### Help ###", "Use the textbox below to scan your smartphones internal memory to find databases " +
        				"that referance the input", "*Click* to transport the file that contains the query to " +
        						"the SQL editor", "Examples: (CASE SENSITIVE)", " Porn", " MyPassword", " www.dodgeysite.com", " Ex-GirlFriends name/number" };
        cmdList = new ArrayList<String>();
        stdOut =  new ArrayList<String>(Arrays.asList(splash));
        filePath = new ArrayList<String>();
        lsvStdOut = (ListView) findViewById(R.id.lsvStdOut);
        adapter = new ArrayAdapter<String>(this,R.layout.messagelisttext,stdOut);
        lsvStdOut.setAdapter(adapter);
        lsvStdOut.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				edtPath.setText("");
				edtPath.append(filePath.get(arg2));
			}
		});
        
	}
	
	
	private OnClickListener x = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent i = new Intent(v.getContext(), SQLSmash.class);
			i.putExtra("dbPath", edtPath.getText().toString());
			v.getContext().startActivity(i);
		}
	};
	
	private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			progDialog = ProgressDialog.show(TraceStalker.this, "", "Executing, Please wait...");
			new Thread() 
			{
			  @Override
			public void run() 
			  {
					cmdList.clear();
					cmdList.add("grep '"+edtSearch.getText().toString()+"' `find /data/data/ -name *.db`" +
							" > /data/data/com.android.gumshoe/grepOut");
					try {
						GumshoeMethodAppendix.getInstance().terminalParse(cmdList);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						getStdOut();
					} catch (Exception e) {
						e.printStackTrace();
					}
					InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE); 
					imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0); 
					progDialog.dismiss();
			  }
		}.start();
	};
	};
	
	private void getStdOut() throws Exception{
		stdOut.clear();
		filePath.clear();
		String removeNonAscii;
		FileReader fileReader = new FileReader("/data/data/com.android.gumshoe/grepOut");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        	removeNonAscii = new String(line.replaceAll("[^a-zA-Z0-9:;\\.,/| £\\$%&@#!<>()+\\-=]", ""));
        	stdOut.add("\nFile: "+removeNonAscii.substring(0, removeNonAscii.indexOf(":"))+"\n\nContext:\n"+removeNonAscii.substring(removeNonAscii.indexOf(":")+1)+"\n");
        	filePath.add(line.substring(0, line.indexOf(":")));
        }
        bufferedReader.close();
	}
}
