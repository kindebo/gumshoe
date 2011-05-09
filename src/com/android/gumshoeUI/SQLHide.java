package com.android.gumshoeUI;

import java.util.ArrayList;
import java.util.List;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SQLHide extends Activity {
	private CheckBox chbCreate;
	private EditText edtContents, edtDesc, edtTable;
	private Button btnHide;
	private String dbPath;
	private List<String> SQLCmds;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.ssqlmake);
	        btnHide = (Button) findViewById(R.id.btnHide);
	        chbCreate = (CheckBox) findViewById(R.id.chbCreate);
	        edtDesc = (EditText) findViewById(R.id.edtDes);
	        edtContents = (EditText) findViewById(R.id.edtContents);
	        edtTable = (EditText) findViewById(R.id.edtTable);
	        dbPath = getIntent().getStringExtra("dbPath");
	        SQLCmds = new ArrayList<String>();
	        btnHide.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					if (chbCreate.isChecked()){
						SQLCmds.add("sqlite3 "+dbPath+" \"CREATE TABLE "+edtTable.getText().toString()+" (Description char(50), Content char(500))\"");
					}
					SQLCmds.add("sqlite3 "+dbPath+" \"INSERT INTO "+edtTable.getText().toString()+" (Description, Content) " + 
							"VALUES (\'"+edtDesc.getText().toString()+"\', \'"+edtContents.getText().toString()+"\')\"");
					try {
						GumshoeMethodAppendix.getInstance().terminalParse(SQLCmds);
					} catch (Exception e) {
						e.printStackTrace();
					}
					Toast.makeText(getBaseContext(), "Data hidden!", Toast.LENGTH_LONG).show();
				}
	        });
	}
}
