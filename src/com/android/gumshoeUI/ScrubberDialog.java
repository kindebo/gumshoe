package com.android.gumshoeUI;

import java.io.File;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class ScrubberDialog extends Dialog{
	private ProgressDialog progDialog;
	Spinner spinner;
	File fileToShred;
	FileBrowser f;
	
	public ScrubberDialog(Context context, File file){
		super(context);
		this.setContentView(R.layout.scrub);
		this.setTitle("File Scrubber");
		Button btnScrub = (Button) findViewById(R.id.btnScrub);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		spinner = (Spinner) findViewById(R.id.pass_spin);
		setCancelable(true);
		fileToShred = file;
		
	    ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getContext(), R.array.passes_choice, R.layout.droptext);
	    adapt.setDropDownViewResource(R.layout.droplisttext);
	    spinner.setAdapter(adapt);
		
	    f = (FileBrowser) context;
	    
	    btnScrub.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				progDialog = ProgressDialog.show(getContext(), "", "Shredding, Please wait...");
				new Thread() 
				{
				  @Override
				public void run() 
				  {
					try {
						char c = ((String) spinner.getSelectedItem()).charAt(0);
						int passes = Character.getNumericValue(c);
						GumshoeMethodAppendix.getInstance().wipe(fileToShred, passes);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dismiss();
					progDialog.dismiss();
				  }
			}.start();
			}
	    });
	    
	    btnCancel.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				dismiss();
			}
	    });
	}
}
