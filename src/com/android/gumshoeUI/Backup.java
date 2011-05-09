package com.android.gumshoeUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class Backup extends Activity{
	private Button btnFull, btnImg, btnDB, btnRecov;
	private EditText edtPass, edtFilename, edtRecover;
	private CheckBox chbEnc;
	private Context context;
	private float fileSize;
	private ProgressDialog progDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.backup);
        btnFull = (Button) findViewById(R.id.button1);
        btnImg = (Button) findViewById(R.id.button2);
        btnDB = (Button) findViewById(R.id.button3);
        btnRecov = (Button) findViewById(R.id.btnRecover);
        chbEnc = (CheckBox) findViewById(R.id.chbEnc);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtFilename = (EditText) findViewById(R.id.edtFilename);
        edtRecover = (EditText) findViewById(R.id.edtRecovery);
        TextView tv = (TextView) findViewById(R.id.textView5);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/defused.ttf");
        tv.setTypeface(font);
        btnFull.setOnClickListener(l);
        btnDB.setOnClickListener(l);
        btnImg.setOnClickListener(l);
        btnRecov.setOnClickListener(l);
        context = getApplicationContext();
	}
	
	private OnClickListener l = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.button1:
					exec(0);
				break;
			case R.id.button2:
				exec(1);
				break;
			case R.id.button3:
				exec(2);
				break;
			case R.id.btnRecover:
				if (new File("/sdcard/"+edtRecover.getText()+".tar").exists()){
				progDialog = ProgressDialog.show(Backup.this, "", "Restoring, Please wait...");
				new Thread() 
				{
				  @Override
				public void run() 
				  {
					  List<String> recv = new ArrayList<String>();
					  recv.add("tar xf /sdcard/"+edtRecover.getText().toString()+".tar -C /");
					  try {
					  GumshoeMethodAppendix.getInstance().terminalParse(recv);
					  } catch (Exception e) {
					  e.printStackTrace();
					  }
					  progDialog.dismiss();
				  }
				}.start();
				} else {
					Toast.makeText(context, "/sdcard/"+edtRecover.getText()+".tar, File not found", Toast.LENGTH_LONG).show();
				}
				break;
			}
		}
	};
	
	private void exec(Integer type) {
		try {
			GumshoeMethodAppendix.getInstance().makeBackup(context, edtFilename.getText().toString(), 
					type, chbEnc.isChecked(), edtPass.getText().toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(context, "/sdcard/"+edtFilename.getText()+" created, Backup complete..", Toast.LENGTH_LONG).show();
	}
}
