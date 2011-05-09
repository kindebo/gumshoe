package com.android.gumshoeUI;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class ProfileSearch extends Activity implements OnClickListener{
	private TextView edtContact;
	private Button btnSearch;
	private RadioButton rdbSMS;
	private int PICK_CONTACT = 1;
	private Intent contactPicker, newActivity;

	 @Override
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.search);
	        edtContact = (TextView) findViewById(R.id.edtContact);
	        btnSearch = (Button) findViewById(R.id.btnSearch);
	        rdbSMS = (RadioButton) findViewById(R.id.rdbSMS);
	        btnSearch.setOnClickListener(this);
	        edtContact.setOnClickListener(this);
	 }
	 
		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent intent) 
		{

		  if (resultCode == RESULT_OK)
		  {         
		      Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
		      cursor.moveToNext();
		      String  number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
			  edtContact.setText(number);
		      return;
		  }
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.edtContact:
					contactPicker = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
			        startActivityForResult(contactPicker, PICK_CONTACT);
					break;
				case R.id.btnSearch:
					String contactID = edtContact.getText().toString();
					if (rdbSMS.isChecked()){
						newActivity = new Intent(this, ProfileDistMessages.class);
					} else {
						newActivity = new Intent(this, ProfileLog.class);
					}
					newActivity.putExtra("contactNumber", contactID);
					newActivity.putExtra("lookupName", GumshoeMethodAppendix.getInstance().contactLookup(contactID, this.getApplicationContext()));
					startActivity(newActivity);
					break;
			}	
		}
	}	
