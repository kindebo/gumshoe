package com.android.gumshoeUI;

import java.io.File;
import java.io.IOException;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SingleInputDialog extends Dialog{
	private Button btnOk, btnCancel;
	private EditText edtInput;
	private File inFile;
	private String titleText;
	private StateEnum curState;

	public SingleInputDialog(Context context, StateEnum state, File file) {
		super(context);
		this.setContentView(R.layout.singleinputdialog);
		edtInput = (EditText) findViewById(R.id.edtInput);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		curState = state;
		inFile = file;
		if (curState == StateEnum.COPY) {
			this.setTitle("New Filename");
			edtInput.setHint("e.g. filecopy");
		} else if (curState == StateEnum.MOVE) {
			this.setTitle("File Path");
			edtInput.setHint("e.g. /sdcard/newfolder/");
		} else {
			this.setTitle("New Filename");
			edtInput.setHint("e.g. mynewfile");
		}
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switch (curState) {
				case COPY:
					try {
					GumshoeMethodAppendix.getInstance().copyFile(inFile, edtInput.getText().toString());
					} catch (IOException e) {
						e.printStackTrace();
					}
					dismiss();
					break;
				case MOVE:
					GumshoeMethodAppendix.getInstance().moveFile(inFile, edtInput.getText().toString());
					dismiss();
					break;
				case RENAME:
					GumshoeMethodAppendix.getInstance().renameFile(inFile, edtInput.getText().toString());
					dismiss();
					break;
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
