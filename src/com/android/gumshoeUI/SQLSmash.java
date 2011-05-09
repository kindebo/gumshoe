package com.android.gumshoeUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class SQLSmash extends Activity{
	private ArrayAdapter<String> queryAdapter, tableAdapter, stdoutAdapter;
	private BufferedReader bufferedReader;
	private Button btnExec;
	private EditText edtMod, edtWhere;
	private FileReader fileReader;
	private String sqlPath;
	private List<String> tableList, statementList, stdOut, sendCmd;
	private ListView lsvStdOut;
	private String opFile = "/data/data/com.android.gumshoe/sqlSmash";
	private String dbName;
	private Spinner spnTables, spnQuery;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.sqlfront);
	        String[] options = { "Columns", "Delete", "Drop Table", "Find", "Replace", "Show all" };
	        btnExec = (Button) findViewById(R.id.btnExec);
	        edtMod = (EditText) findViewById(R.id.edtMod);
	        edtWhere = (EditText) findViewById(R.id.edtWhere);
	        spnQuery = (Spinner) findViewById(R.id.spnQuery);
	        spnTables = (Spinner) findViewById(R.id.spnTables);
	        lsvStdOut = (ListView) findViewById(R.id.lsvStdOut);
	        sendCmd = new ArrayList<String>();
	        stdOut = new ArrayList<String>();
	        tableList = new ArrayList<String>();
	        btnExec.setOnClickListener(l);
	        sqlPath = getIntent().getStringExtra("dbPath");
	        dbName = sqlPath.substring(sqlPath.lastIndexOf("/")+1, sqlPath.length());
	        String[] splash = { "", "### SQL SMASHER ###", "--------------------", "", "Current Database: "+dbName,  "",
	        		"Inputs:", "1. Table Name", "2. Function", "3. Field name", "4. Search string"};
			sendCmd.add("sqlite3 "+sqlPath+" .tables > "+opFile);
			try {
				GumshoeMethodAppendix.getInstance().terminalParse(sendCmd);
				tableList = arrayFiller("table");
			} catch (Exception e) {
				e.printStackTrace();
			}
			stdOut = new ArrayList<String>(Arrays.asList(splash));
	        tableAdapter = new ArrayAdapter<String>(this,R.layout.droptext,tableList);
	        queryAdapter = new ArrayAdapter<String>(this,R.layout.droptext,options);
	        stdoutAdapter = new ArrayAdapter<String>(this,R.layout.messagelisttext,stdOut);
	        queryAdapter.setDropDownViewResource(R.layout.droplisttext);
	        tableAdapter.setDropDownViewResource(R.layout.droplisttext);
	        spnQuery.setAdapter(queryAdapter);
	        spnTables.setAdapter(tableAdapter);
	        lsvStdOut.setAdapter(stdoutAdapter);
	}
	
	private OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			sendCmd.clear();
			sendCmd=(queryBuilder(edtWhere.getText().toString(),
					spnTables.getSelectedItem().toString(), spnQuery.getSelectedItemPosition(), edtMod.getText().toString()));
			try {
				GumshoeMethodAppendix.getInstance().terminalParse(sendCmd);
				stdOut.clear();
				stdOut = arrayFiller("stdout");
			} catch (Exception e) {
				e.printStackTrace();
			}
			stdoutAdapter = new ArrayAdapter<String>(v.getContext(),R.layout.messagelisttext,stdOut);
			lsvStdOut.setAdapter(stdoutAdapter);
	        stdoutAdapter.notifyDataSetChanged();
		}
	};
	
	private List<String> queryBuilder(String query, String tableName, Integer selectedItem, String mod){
		List<String> buildList = new ArrayList<String>();
		switch (selectedItem){
		case 0:
			buildList.add("sqlite3 "+sqlPath+" 'PRAGMA table_Info("+tableName+")' > "+opFile);
			break;
		case 1:
			buildList.add("sqlite3 "+sqlPath+" 'DELETE FROM "+tableName+" WHERE "+mod+" = '"+query+"''");
			break;
		case 2:
			buildList.add("sqlite3 "+sqlPath+" 'DROP TABLE "+tableName+"'");
			break;
		case 3:
			buildList.add("sqlite3 -line -echo "+sqlPath+" \"SELECT * FROM "+tableName+" WHERE "+mod+" LIKE '%"+query+"%';\""+" > "+opFile);
			break;
		case 4:
			buildList.add("sqlite3 "+sqlPath+" \"UPDATE "+tableName+" SET "+mod+" = REPLACE("+mod+", "+query+")\"");
			break;
		case 5:
			buildList.add("sqlite3 -line -echo "+sqlPath+" \"SELECT * FROM "+tableName+"\" > "+opFile);
			break;
		}
		return buildList;
	}
	
	private List<String> arrayFiller(String mode) throws Exception{
		List<String> buildList = new ArrayList<String>();
		fileReader = new FileReader(opFile);
        bufferedReader = new BufferedReader(fileReader);
        String line = null;
        String[] tmp = null;
        while ((line = bufferedReader.readLine()) != null) {
        	if (mode == "table"){
        		tmp = line.split("\\s+",3);
        		for(int i =0; i < tmp.length ; i++){
        		buildList.add(tmp[i].replaceAll("\\s+", ""));
        	}
        	} else {
        		buildList.add(line.trim());
        	}
        }
        bufferedReader.close();
        return buildList;
	}
	
}
