package com.android.gumshoeUI;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class PanicButton extends Dialog{
	private Button btnOk, btnCancel;
	private CheckBox chbSDcard;
	private EditText edtInput;
	private ProgressDialog progDialog;
	private List<String> cmdList;
	private SeekBar seekBar;
	
	public PanicButton(Context context) {
		super(context);
		this.setContentView(R.layout.panicdialog);
		this.setTitle("  Forensically wipe all user data? ");
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		chbSDcard = (CheckBox) findViewById(R.id.chbSDcard);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		cmdList = new ArrayList<String>();
		cmdList.add("rm -rf /data/*");
		cmdList.add("dd if=/dev/urandom/ > /data/com.google.maps.apk");
		cmdList.add("sleep 10");
		cmdList.add("rm /data/com.google.maps.apk");
		if (chbSDcard.isChecked()){
			cmdList.add("rm -rf /sdcard/*");
			cmdList.add("dd if=/dev/urandom/ > /sdcard/com.google.maps.apk");
			cmdList.add("sleep 10");
			cmdList.add("rm /sdcard/com.google.maps.apk");
		}
		btnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (seekBar.getProgress() == seekBar.getMax()){
					progDialog = ProgressDialog.show(getContext(), "", "Shredding, Please wait...");
					new Thread() 
					{
					  @Override
					public void run() 
					  {
						try {
							GumshoeMethodAppendix.getInstance().terminalParse(cmdList);
						} catch (Exception e) {
							e.printStackTrace();
						}
						dismiss();
						progDialog.dismiss();
					  }
					}.start();
				} else {
					Toast.makeText(v.getContext(), "Drag the seeker to the right must point!", Toast.LENGTH_LONG).show();
				}
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