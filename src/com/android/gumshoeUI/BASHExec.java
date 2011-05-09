package com.android.gumshoeUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class BASHExec extends Activity {
	private AssetManager assetManager;
	private ArrayAdapter<String> stdoutAdapter, scriptAdapter;
	private Button btnExec;
	private BufferedReader bufferedReader;
	private EditText edtStdIn;
	private FileReader fileReader;
	private File[] files;
	private ProgressDialog progDialog;
	private ListView lsvStdOut;
	private List<String> scriptList, stdOut, sendCmd;
	private String opFile = "/sdcard/scripts/out";
	private Spinner spnScript;
	

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		   super.onCreate(savedInstanceState);
	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.execbash);
	        btnExec = (Button) findViewById(R.id.btnExec);
	        edtStdIn = (EditText) findViewById(R.id.edtStdIn);
	        lsvStdOut = (ListView) findViewById(R.id.lsvStdOut);
	        spnScript = (Spinner) findViewById(R.id.spnScript);
	        btnExec.setOnClickListener(l);
	        scriptList = new ArrayList<String>();
	        sendCmd = new ArrayList<String>();
	        stdOut = new ArrayList<String>();
	        File scriptsDir = new File("/sdcard/scripts");
	        if (!scriptsDir.exists()){
	        	scriptsDir.mkdir();
	        }
	        shFileFilter shFilter = new shFileFilter();
	        files = scriptsDir.listFiles(shFilter);
	        scriptList.add("Download More!");
	        for (int i = 0; i < files.length ; i++){
	        	scriptList.add(files[i].getName());
	        }
	        String[] splash = { "", "### Gumshoe Script Manager ###", "", "[ - ] Place custom scripts in the scripts directory on your SDcard", 
    		"", "[ - ] Choosing the Download More! option will grab new scripts from the NerdZoo servers" };
			stdOut = new ArrayList<String>(Arrays.asList(splash));
	        scriptAdapter = new ArrayAdapter<String>(this, R.layout.droptext, scriptList);
			scriptAdapter.sort(null);
			scriptAdapter.setDropDownViewResource(R.layout.droplisttext);
			spnScript.setAdapter(scriptAdapter);
	        stdoutAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.messagelisttext,stdOut);
	        lsvStdOut.setAdapter(stdoutAdapter);
	        edtStdIn.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_ENTER){
						btnExec.performClick();
						return true;
					}
					return false;
				}
			});
	        spnScript.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					if (!(arg0.getSelectedItemPosition()==0)){
					sendCmd.clear();
					sendCmd.add("cat /sdcard/scripts/"+scriptList.get(spnScript.getSelectedItemPosition())+" > "+opFile);
					try {
						GumshoeMethodAppendix.getInstance().terminalParse(sendCmd);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					stdOut.clear();
					try {
						stdOut = arrayFiller();
					} catch (Exception e) {
						e.printStackTrace();
					}
					updateLists();
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
				}
			});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.script_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case R.id.create_script:
			Intent i = new Intent(getApplicationContext(), MakeScript.class);
			finish();
			startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			progDialog = ProgressDialog.show(BASHExec.this, "", "Executing, Please wait...");
			new Thread() 
			{
			  @Override
			public void run() 
			  {
			        sendCmd.clear();
			        if (spnScript.getSelectedItemPosition() == 0){
						try {
							getExamples();
						} catch (Exception e) {
							e.printStackTrace();
						}
			        } else {
					sendCmd.add("sh /sdcard/scripts/"+scriptList.get(spnScript.getSelectedItemPosition())+" \""
							+edtStdIn.getText()+"\" > "+opFile);
					try {
						GumshoeMethodAppendix.getInstance().terminalParse(sendCmd);
						stdOut.clear();
						stdOut = arrayFiller();
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(0);
			  }
			 }
			}.start();
		}
	};
	
	private List<String> arrayFiller() throws Exception{
		List<String> buildList = new ArrayList<String>();
		fileReader = new FileReader(opFile);
        bufferedReader = new BufferedReader(fileReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
        		buildList.add(line.trim());
        	}
        bufferedReader.close();
        return buildList;
	}
	
	private void getExamples() throws Exception {
		sendCmd.add("cd /sdcard/scripts/");
		sendCmd.add("wget http://91.206.143.65/gumshoe-scripts/ScriptsUpdate.tar");
		sendCmd.add("tar xf ScriptsUpdate.tar");
		sendCmd.add("rm ScriptsUpdate.tar");
		GumshoeMethodAppendix.getInstance().terminalParse(sendCmd);
		Intent intent = getIntent();
		finish();
		startActivity(intent);
	}
	
	private void updateLists(){
		stdoutAdapter = new ArrayAdapter<String>(getBaseContext(),R.layout.messagelisttext,stdOut);
		lsvStdOut.setAdapter(stdoutAdapter);
	    stdoutAdapter.notifyDataSetChanged();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg){
			  progDialog.dismiss();
			  updateLists();
		}
	};
	
	private class shFileFilter implements FileFilter{
		@Override
		public boolean accept(File f) {
			String suffix = ".sh";
	        if (f.getName().toLowerCase().endsWith(suffix) ) {
	            return true;
	        }
	        return false;
		}
	}
}
