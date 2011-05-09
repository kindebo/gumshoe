package com.android.gumshoeUI;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class DecryptDialog extends Dialog {
	private String oldMessage, sender;
	private Button btnDecrypt, btnReply, btnCancel;
	private TextView tvMessage;
	private EditText edtKey;

	public DecryptDialog(Context context, String messageBody, String address) {
		super(context);
		this.setContentView(R.layout.decryptdialog);
		this.setTitle("Decrypt Message");
		btnDecrypt = (Button) findViewById(R.id.btnDecrypt);
		btnReply = (Button) findViewById(R.id.btnReply);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		tvMessage = (TextView) findViewById(R.id.tvMessage);
		edtKey = (EditText) findViewById(R.id.edtKey);
		tvMessage.setText(messageBody);
		oldMessage = messageBody;
		sender = address;
		btnReply.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(v.getContext(), SmsSend.class);
				i.putExtra("address", sender);
				v.getContext().startActivity(i);
			}
			
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		btnDecrypt.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try { tvMessage.setText(GumshoeMethodAppendix.getInstance().decryptTxt(oldMessage, 
							GumshoeMethodAppendix.getInstance().hashkey(edtKey.getText().toString()), getContext()));
				} catch (Exception e) { 
					e.printStackTrace(); }				
			}
		});
	}
}
