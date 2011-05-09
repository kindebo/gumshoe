package com.android.gumshoeUI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class ProfileDistMessages extends Activity implements OnClickListener {
	private Integer dYear, dMonth, dDay, tHour, tMinute, gotDate, page = 0;
	private long posix;
	private String contactID, lookupName; 
	private List<String> bodyArray, dateArray;
	private Button btnModify, btnPrevious, btnNext, btnDate, btnTime;
	final int DATE_DIALOG_ID = 1, TIME_DIALOG_ID = 2;
	private Calendar c;
	private ContentResolver res;
	private ContentValues cv;
	private TextView tvDate, tvContact;
	private EditText edtBody;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profilemessage);
        c = Calendar.getInstance();
        res = getContentResolver();
        btnModify = (Button) findViewById(R.id.btnModify);
        btnDate = (Button) findViewById(R.id.btnDate);
        btnTime = (Button) findViewById(R.id.btnTime);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnPrevious = (Button) findViewById(R.id.btnPrevious);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvContact = (TextView) findViewById(R.id.tvContact);
        edtBody = (EditText) findViewById(R.id.edtBody);
 		bodyArray = new ArrayList<String>();
        dateArray = new ArrayList<String>();
        btnModify.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnTime.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrevious.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        contactID = extras.getString("contactNumber");
        lookupName = extras.getString("lookupName");
        tvContact.setText(lookupName);
        getSMS();
	}
	
 	private void getSMS(){
           Uri uri = Uri.parse("content://sms/inbox");
           Cursor cur = getContentResolver().query(uri, null, null, null, null);
           while (cur.moveToNext()) {
        	   String tempNumber = cur.getString(cur.getColumnIndex("address"));
               if (tempNumber.regionMatches(tempNumber.length() -5, contactID, contactID.length() - 5, 5)) {
            	   bodyArray.add(cur.getString(cur.getColumnIndexOrThrow("body")));
            	   dateArray.add(cur.getString(cur.getColumnIndex("date")));
               }
           }
           if (bodyArray.size() == 0){
        	   new AlertDialog.Builder(this)
        	   .setTitle("Error!")
        	   .setMessage("No messages from "+lookupName)
        	   .setNeutralButton("Ok", new android.content.DialogInterface.OnClickListener() {
        		   @Override
				public void onClick(DialogInterface dialog, int which) {
        			   finish();
        		   }
        	   }
        	   )
        	   .show();
           } else {
           edtBody.setText("");
           edtBody.append(bodyArray.get(0));
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

    private void modifyMessage(int gotDate){
    	res = getContentResolver();
    	cv = new ContentValues();
    	String body = edtBody.getText().toString();
    	cv.put("body", body);
    	if (gotDate == 1) { cv.put("date", posix); }
    	Uri smsUri = Uri.parse("content://sms/inbox");
    	res.update(smsUri, cv, "date="+dateArray.get(page), null);
    	Toast.makeText(getApplicationContext(), "Record sucessfully updated!", Toast.LENGTH_LONG).show();
    	
    }
    
    @Override
	public void onClick(View v){
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
    		modifyMessage(gotDate);
    		break;
    	case R.id.btnNext:
    		getNext();
    		break;
    	case R.id.btnPrevious:
    		getPrevious();
    		break;
    	}
    }

	private void getNext() {
		page += 1;
		if (page >= bodyArray.size()){ page = 0;}
		edtBody.setText("");
        edtBody.append(bodyArray.get(page));
        tvDate.setText(GumshoeMethodAppendix.getInstance().dateCovnert(dateArray.get(page)));
	}
	
	private void getPrevious() {
		page -= 1;
		if (page < 0){ page = bodyArray.size() -1;}
		edtBody.setText("");
        edtBody.append(bodyArray.get(page));
        tvDate.setText(GumshoeMethodAppendix.getInstance().dateCovnert(dateArray.get(page)));
	}
}