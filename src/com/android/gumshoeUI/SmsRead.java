package com.android.gumshoeUI;

import java.util.ArrayList;
import java.util.List;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class SmsRead extends Activity {
	private ArrayList<String> body, address;
	private String bodyText, decryptKey;
	private ImageView imgvPrevious, imgvNext;
	private ListView list;
	private Dialog decriptDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.smsread);
	        list = (ListView) findViewById(R.id.lsvMessages);
	        address = new ArrayList<String>();
	        body = new ArrayList<String>();
	        List<String> msgList = getSMS();
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.messagelisttext,msgList);
	        list.setAdapter(adapter);
	        list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
			        decriptDialog = new DecryptDialog(arg1.getContext(), body.get(arg2), address.get(arg2));
			        decriptDialog.show();
				}
			});
	        }
	
	private List<String> getSMS(){
	        List<String> sms = new ArrayList<String>();
	        Uri uri = Uri.parse("content://sms/inbox");
	        Cursor cur = getContentResolver().query(uri, null, null, null, null);

	        while (cur.moveToNext()) {
	              String address = cur.getString(cur.getColumnIndex("address"));
	              String date = cur.getString(cur.getColumnIndex("date"));
	              String body = cur.getString(cur.getColumnIndexOrThrow("body"));
	           	  sms.add("From: " + GumshoeMethodAppendix.getInstance().contactLookup(address, this.getApplicationContext()) + "\nDate: "+
	           			  GumshoeMethodAppendix.getInstance().dateCovnert(date)+"\n\n" + body);  
	              this.body.add(body);
	              this.address.add(address);
	              }
	        return sms;
	   }
	}
