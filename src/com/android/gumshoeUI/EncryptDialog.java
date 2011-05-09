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
import android.widget.TextView;
import android.widget.Toast;

public class EncryptDialog extends Dialog {
	private ProgressDialog progDialog;
	private File fileToCipher;
	private EditText edtKey;
	private StateEnum state;

	public EncryptDialog(Context context, File file) {

		super(context);
		this.setContentView(R.layout.encdialog);
		Button btnOk = (Button) findViewById(R.id.btnOk);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		TextView tvFileName = (TextView) findViewById(R.id.tvFileName);
		tvFileName.setText(file.getName());
		edtKey = (EditText) findViewById(R.id.edtKey);
		fileToCipher = file;
		updateMode(file.getName());

		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				progDialog = ProgressDialog.show(getContext(), "", "Processing, Please wait...");
				new Thread() 
				{
				  @Override
				public void run() 
				  {
					String key = edtKey.getText().toString();
					try {
						if(state == StateEnum.ENCRYPTION){
							GumshoeMethodAppendix.getInstance().encryptFile(fileToCipher, GumshoeMethodAppendix.getInstance().hashkey(key), getContext());
							Toast.makeText(getContext(), "256-AES/CBC Encryption complete!", Toast.LENGTH_SHORT).show();
						}
						else {
							GumshoeMethodAppendix.getInstance().decryptFile(fileToCipher, GumshoeMethodAppendix.getInstance().hashkey(key), getContext());
							Toast.makeText(getContext(), "256-AES/CBC Decryption complete!", Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					dismiss();
					progDialog.dismiss();
				  }
			}.start();
			}
		});

		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	private void updateMode(String name){
		String ext = name.substring(name.lastIndexOf('.') + 1);
		if(ext.equals("enc")){
			this.setTitle("AES-256-CBC: Decrypt");
			state = StateEnum.DECRYPTION;
		}else{
			this.setTitle("AES-256-CBC: Encrypt");
			state = StateEnum.ENCRYPTION;
		}
	}
}
