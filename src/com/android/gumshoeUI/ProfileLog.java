package com.android.gumshoeUI;

import java.util.ArrayList;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class ProfileLog extends Activity implements android.view.View.OnClickListener{
	private Integer dYear, dMonth, dDay, tHour, tMinute, gotDate, page = 0;
	private long posix;
	private RadioButton rdbOut, rdbMissed; 
	private EditText edtMinute, edtSecond;
	private Button btnNext, btnPrevious, btnModify, btnDate, btnTime;
	private TextView tvContact, tvLength, tvDate, tvType;
	private ArrayList<String> durArray, dateArray, typeArray;
	final int DATE_DIALOG_ID = 1, TIME_DIALOG_ID = 2;
	private String contactID, lookupName;
	private Calendar c;
	private ContentResolver res;
	private ContentValues cv;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profilelog);
        edtMinute = (EditText) findViewById(R.id.edtMinute);
        edtSecond= (EditText) findViewById(R.id.edtSecond);
        rdbOut = (RadioButton) findViewById(R.id.rdbOut);
        rdbMissed = (RadioButton) findViewById(R.id.rdbMissed);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        btnModify = (Button) findViewById(R.id.btnModify);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        edtMinute = (EditText) findViewById(R.id.edtMinute);
        edtSecond = (EditText) findViewById(R.id.edtSecond);
        tvContact = (TextView) findViewById(R.id.tvContact);
        tvType = (TextView) findViewById(R.id.tvType);
        tvLength = (TextView) findViewById(R.id.tvLength);
        tvDate = (TextView) findViewById(R.id.tvDate);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        dateArray = new ArrayList<String>();
        durArray = new ArrayList<String>();
        typeArray = new ArrayList<String>();
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        btnModify.setOnClickListener(this);
        c = Calendar.getInstance();
        res = getContentResolver();
        Bundle extras = getIntent().getExtras();
        contactID = extras.getString("contactNumber");
        lookupName = extras.getString("lookupName");
        tvContact.setText(lookupName);
        pullLogs(contactID);
        edtMinute.append("00");
        edtSecond.append("00");
	}
	
	private void pullLogs(String contactID){
           Cursor cur = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
           while (cur.moveToNext()) {
        	   String tempNumber = cur.getString(cur.getColumnIndex("number"));
               if (tempNumber.regionMatches(tempNumber.length() -5, contactID, contactID.length() - 5, 5)) {
            	   dateArray.add(cur.getString(cur.getColumnIndex("date")));
            	   durArray.add(cur.getString(cur.getColumnIndexOrThrow("duration")));
            	   typeArray.add(cur.getString(cur.getColumnIndex("type")));
               }
           }
           if (dateArray.size() == 0){
        	   new AlertDialog.Builder(this)
        	   .setTitle("Error!")
        	   .setMessage("No logs from "+lookupName)
        	   .setNeutralButton("Ok", new android.content.DialogInterface.OnClickListener() {
        		   @Override
				public void onClick(DialogInterface dialog, int which) {
        			   finish();
        		   }
        	   }
        	   )
        	   .show();
           } else {
           int tmpMin = (int) Math.floor(Integer.parseInt(durArray.get(0)) / 60);
           int tmpSec = Integer.parseInt(durArray.get(0)) - tmpMin * 60;
           tvLength.setText(Integer.toString(tmpMin)+"."+Integer.toString(tmpSec));
           tvType.setText(GumshoeMethodAppendix.getInstance().typeLookup(typeArray.get(0)));
           tvDate.setText(GumshoeMethodAppendix.getInstance().dateCovnert(dateArray.get(0)));
           }
	}
	
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        dDay = dayOfMonth;
        dMonth = monthOfYear;
        dYear = year;
        }
    };

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        tHour = hourOfDay;
        tMinute = minute;
        }
    };
		
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this, mDateSetListener, c.get(Calendar.YEAR), 
            		c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        case TIME_DIALOG_ID:
            return new TimePickerDialog(this, mTimeSetListener, 
            		c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);
        }
        return null;
    }
	
	private void getNext() {
		page += 1;
		if (page >= dateArray.size()){ page = 0;}
	      int tmpMin = (int) Math.floor(Integer.parseInt(durArray.get(page)) / 60);
          int tmpSec = Integer.parseInt(durArray.get(page)) - tmpMin * 60;
          tvLength.setText(Integer.toString(tmpMin)+"."+Integer.toString(tmpSec));
          tvType.setText(GumshoeMethodAppendix.getInstance().typeLookup(typeArray.get(page)));
          tvDate.setText(GumshoeMethodAppendix.getInstance().dateCovnert(dateArray.get(page)));
	}
	
	private void getPrevious() {
		page -= 1;
		if (page < 0){ page = dateArray.size() -1;}
	      int tmpMin = (int) Math.floor(Integer.parseInt(durArray.get(page)) / 60);
          int tmpSec = Integer.parseInt(durArray.get(page)) - tmpMin * 60;
          tvLength.setText(Integer.toString(tmpMin)+"."+Integer.toString(tmpSec));
          tvType.setText(GumshoeMethodAppendix.getInstance().typeLookup(typeArray.get(page)));
          tvDate.setText(GumshoeMethodAppendix.getInstance().dateCovnert(dateArray.get(page)));
	}

	public void modifyLog(int gotDate){
    	res = getContentResolver();
    	cv = new ContentValues();
    	if (rdbOut.isChecked()){
    		cv.put("type", 2);
    	} else if (rdbMissed.isChecked()) {
    		cv.put("type", 3);
    	} else {
    		cv.put("type", 1);
    	}
    	if (!edtMinute.getText().toString().matches("00") || !edtSecond.getText().toString().matches("00")){
    		Integer i = Integer.parseInt(edtSecond.getText().toString()) + Integer.parseInt(edtMinute.getText().toString()) * 60;
    		cv.put("duration", i);
    	}
    	if (gotDate == 1) { cv.put("date", posix); }
    	res.update(CallLog.Calls.CONTENT_URI, cv, "date="+dateArray.get(page), null);
    	Toast.makeText(getApplicationContext(), "Record sucessfully updated!", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onClick(View v) {
		 switch(v.getId()){
    	 case R.id.btnDate:
    		 showDialog(DATE_DIALOG_ID);
    		 break;
    	 case R.id.btnTime:
    		 showDialog(TIME_DIALOG_ID);
    		 break;
    	 case R.id.btnModify:
    		 if (dYear != null || tHour != null){
    			 c.set(dYear, dMonth, dDay, tHour, tMinute, 0);
    		 	 posix = c.getTimeInMillis();
    			 gotDate = 1;
    		 } else { gotDate = 0; }
    		 modifyLog(gotDate);
    		 break;
    	 case R.id.btnNext:
    		 getNext();
    		 break;
    	 case R.id.btnPrevious:
    		 getPrevious();
    		 break;
		 }
	}
}
