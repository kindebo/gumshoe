package com.android.gumshoeUI;

import java.io.File;
import java.io.FileWriter;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import com.android.gumshoe.R;

public class MakeScript extends Activity{
	private Button btnSave, btnExit;
	private EditText edtName, edtScript;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.makescript);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnExit = (Button) findViewById(R.id.btnExit);
        edtScript = (EditText) findViewById(R.id.edtScript);
        edtName = (EditText) findViewById(R.id.edtName);
        btnSave.setOnClickListener(l);
        btnExit.setOnClickListener(l);
        
	}
	
	private OnClickListener l = new OnClickListener(){

			@Override
			public void onClick(View v) {
				switch (v.getId()){
				case R.id.btnExit:
					finish();
					break;
				case R.id.btnSave:
					try {
						writeScript(edtName.getText().toString(), edtScript.getText().toString());
						finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			}
		};	
	};

	private void writeScript(String filename, String Contents) throws Exception{
		File script = new  File("/sdcard/scripts/", filename);
		FileWriter fileWriter = new FileWriter(script);
		fileWriter.write(Contents);
		fileWriter.flush();
		fileWriter.close();
	}
}