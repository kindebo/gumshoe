package com.android.gumshoeUI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class SmsSend extends Activity{
	private int PICK_CONTACT = 1;
	private byte[] key;
	private Button btnSend, btnCancel, btnEncrypt;
	private Intent contactPicker;
	private EditText edtBody, edtContact, edtKey;
	private String keyToString, address;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.txtsend);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnEncrypt = (Button) findViewById(R.id.btnEncrypt);
        edtBody = (EditText) findViewById(R.id.edtBody);
        edtContact = (EditText) findViewById(R.id.edtContact);
        edtKey = (EditText) findViewById(R.id.edtKey);
        address = getIntent().getStringExtra("address");
        edtContact.append(address);
        btnCancel.setOnClickListener(l);
        btnEncrypt.setOnClickListener(l);
        btnSend.setOnClickListener(l);
        edtContact.setOnClickListener(l);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) 
	{

	  if (resultCode == RESULT_OK)
	  {         
	      Cursor cursor =  managedQuery(intent.getData(), null, null, null, null);
	      cursor.moveToNext();
	      String  number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
	      edtContact.setText("");
	      edtContact.append(number);
	      return;
	  }
	}
	
	public String contactLookup(String number) {
		Uri lookup = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));  
		Cursor res = getContentResolver().query(lookup, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (res.moveToFirst()) {
			String name = res.getString(res
					.getColumnIndex(Contacts.DISPLAY_NAME));
			return name;
			}
			return number;
		}
	
		private OnClickListener l = new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch (v.getId()){
				case R.id.btnCancel:
					finish();
					break;
				case R.id.edtContact:
					contactPicker = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
			        startActivityForResult(contactPicker, PICK_CONTACT);
					break;
				case R.id.btnEncrypt:
					try { key = GumshoeMethodAppendix.getInstance().hashkey(edtKey.getText().toString());
					} catch (Exception e) { e.printStackTrace(); }
					try { edtBody.setText(GumshoeMethodAppendix.getInstance().encryptTxt(edtBody.getText().toString(), key, getApplicationContext()));
					} catch (Exception e) { e.printStackTrace(); }
					break;
				case R.id.btnSearch:
					break;
				case R.id.btnSend:
					SMSSend();
					break;
			}
		};	
		};

		private void SMSSend() {
			PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, SmsSend.class), 0);            
			SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(edtContact.getText().toString(), null, edtBody.getText().toString(), pi, null);
	        Toast.makeText(getBaseContext(), "Message Sent!", Toast.LENGTH_SHORT).show();
		}
}