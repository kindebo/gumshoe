package com.android.gumshoeUI;

import java.io.File;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ExtensionEraser extends Dialog{
	private Button btnOk, btnCancel;
	private Context appContext;
	private EditText edtInput;
	private ProgressDialog progD;
	
	public ExtensionEraser(Context context) {
		super(context);
		this.setContentView(R.layout.extensioneraser);
		edtInput = (EditText) findViewById(R.id.edtInput);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		this.setTitle("Extension Eraser");
		appContext = context;
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				progD = ProgressDialog.show(appContext, "", "Shredding, Please wait...");
				new Thread() 
				{
				  @Override
				public void run() 
				  {
					try {
						GumshoeMethodAppendix.getInstance().searchAndShred(new File("/sdcard/"), edtInput.getText().toString());
					} catch (Exception e) {
						e.printStackTrace();
					}
					progD.dismiss();
				}
			}.start();
			dismiss();
			}
			});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
}
