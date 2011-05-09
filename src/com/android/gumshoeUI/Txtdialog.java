package com.android.gumshoeUI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class Txtdialog extends AlertDialog {
	private Context parentContext;
	private Intent i;
	
	protected Txtdialog(Context context) {
		super(context);
		parentContext = context;
		CharSequence[] items = { "Send encrypted SMS", "Decrypt Messages"};
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Pick a feature");
		builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:
						i = new Intent(parentContext, SmsSend.class);
						i.putExtra("address", "Double tap for contacts");
						parentContext.startActivity(i);
						break;
					case 1:
						i = new Intent(parentContext, SmsRead.class);
						parentContext.startActivity(i);
						break;
					}
			}
		});
		builder.show();
	}
		
}
