package com.android.gumshoeUI;

import java.io.File;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.gumshoe.GumshoeMethodAppendix;
import com.android.gumshoe.R;

public class FileActionsDialog extends Dialog {
	private ListView lsvFileActions;
	private File file;
	private FileBrowser f;
	private Dialog actionDialog;
	private Context parentContext;
	private StateEnum setState;
	
	public FileActionsDialog(Context context, File chosenFile){
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.fileactions);
		lsvFileActions = (ListView) findViewById(R.id.lsvFileActions);
		ArrayAdapter<CharSequence> actionAdapter = ArrayAdapter.createFromResource(getContext(), R.array.actionsChoice, R.layout.actiontext);
		lsvFileActions.setAdapter(actionAdapter);
		lsvFileActions.setOnItemClickListener(l);
		file = chosenFile;
		parentContext = context;
		f = (FileBrowser) context;
		this.setOnDismissListener(onDismissed);
	}
	
	private OnDismissListener onDismissed = new OnDismissListener() {
		
		@Override
		public void onDismiss(DialogInterface dialog) {
			f.refreshList();
			dismiss();
		}
	};
		
	private OnItemClickListener l = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
		switch (arg2) {
		case 2:
			setState = StateEnum.COPY;
			actionDialog = new SingleInputDialog(parentContext, setState, file);
			actionDialog.show();
			break;
		case 3:
			setState = StateEnum.MOVE;
			actionDialog = new SingleInputDialog(parentContext, setState, file);
			actionDialog.show();
			break;
		case 4:
			setState = StateEnum.RENAME;
			actionDialog = new SingleInputDialog(parentContext, setState, file);
			actionDialog.show();
			break;
		case 6:
			actionDialog = new EncryptDialog(parentContext, file);
			actionDialog.show();
			break;
		case 7:
			actionDialog = new EncryptDialog(parentContext, file);
			actionDialog.show();
			break;
		case 8:
			actionDialog = new ScrubberDialog(parentContext, file);
			actionDialog.show();
			break;
		case 9:
			actionDialog = new EncryptDialog(parentContext, file);
			actionDialog.show();
			actionDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					try {
						GumshoeMethodAppendix.getInstance().binaryHider(file.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}					
				}
			});
			break;
		case 12:
			onBackPressed();
		default:
			break;
		}
			
		}
	};
}
