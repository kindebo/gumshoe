package com.android.gumshoeUI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class DatabaseNuker extends Activity{
	private AlertDialog confirmAlert;
	private boolean allChecked = false;
	private Button btnSelectAll, btnShred;
	private Context context;
	private ListView dbView;
	private ProgressDialog progD;
	private List<String> dbCmd, dbList, dbHardList;
	private String formatMe;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.databasenuke);
        btnSelectAll = (Button) findViewById(R.id.btnSelect);
        btnShred = (Button) findViewById(R.id.btnShred);
        dbView = (ListView) findViewById(R.id.lsvDBList);
        btnShred.setOnClickListener(l);
        btnSelectAll.setOnClickListener(x);
		dbCmd = new ArrayList<String>();
		dbList = new ArrayList<String>();
		dbHardList = new ArrayList<String>();
		dbCmd.add("find /data/data -name *.db > /data/data/com.android.gumshoe/dbList");
		try {
			GumshoeMethodAppendix.getInstance().terminalParse(dbCmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			xferFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.checkedtextview,dbList);
        dbView.setAdapter(adapter);
        dbView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dbView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			}
		});
	}
	
	private void checkAll(){
		if (allChecked == true){
			allChecked = false;
		} else {
			allChecked = true;
		}
		for(int i = 0; i < dbHardList.size() ; i++){
			dbView.setItemChecked(i, allChecked);
		}
		
	}
	
	private OnClickListener x = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			checkAll();
			
		}
	};
	
	private OnClickListener l = new OnClickListener() {
		@Override
		public void onClick(View v) {
			context = v.getContext();
			confirmAlert = new AlertDialog.Builder(v.getContext()).create();
			confirmAlert.setTitle("Are you sure?");
			confirmAlert.setMessage("Your data will be permanenatly wiped!");
			confirmAlert.setButton("Ok", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progD = ProgressDialog.show(context, "", "Nuking, Please wait...", true);
					int dbCount = dbNuker();
					progD.dismiss();
					confirmAlert.dismiss();
					Toast.makeText(getBaseContext(), dbCount+" database(s) nuked!", Toast.LENGTH_SHORT).show();
					finish();
					startActivity(getIntent());
				}
			});
			confirmAlert.setButton2("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					confirmAlert.dismiss();
				}
			});
			confirmAlert.show();
		}
	};

	private int dbNuker(){
		boolean hitZero = false;
			dbCmd.clear();
			SparseBooleanArray a = dbView.getCheckedItemPositions();
			for(int i = 0; i < a.size() ; i++){
				if (a.keyAt(i) == 0 && hitZero == false){
					// needs optimising
					//dbCmd.add("dd if=/dev/urandom bs=3000 count=1000 > "+dbHardList.get((a.keyAt(i))));
					dbCmd.add("rm "+dbHardList.get((a.keyAt(i))));
					hitZero = true;
				} else {
					//dbCmd.add("dd if=/dev/urandom bs=3000 count=1000 > "+dbHardList.get((a.keyAt(i))));
					dbCmd.add("rm "+dbHardList.get((a.keyAt(i))));
				}
			}
			try {
				GumshoeMethodAppendix.getInstance().terminalParse(dbCmd);
				dbCmd.clear();
				dbCmd.add("cat /dev/urandom > /data/shredmem");
				dbCmd.add("sleep 10");
				dbCmd.add("rm /data/shredmem");
				GumshoeMethodAppendix.getInstance().terminalParse(dbCmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return a.size();
		}
	
	private void xferFile() throws IOException{
		FileReader fileReader = new FileReader("/data/data/com.android.gumshoe/dbList");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        	dbHardList.add(line);
        	formatMe = line.substring(11,line.indexOf("/",11));
            dbList.add("App: "+formatMe+"\nDatabase: "
            		+line.substring(line.lastIndexOf("/")+1));
        }
        bufferedReader.close();
	}
}
