package com.android.gumshoeUI;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/*
 * This is the menu click listener which will handle how the program responds to user input
 */
public class MainMenuOnClickListener implements OnItemClickListener {
	private AlertDialog chosenDialog;
	
	
	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {

		/*
		 * String selection = l.getItemAtPosition(position).toString();
		 * Toast.makeText(v.getContext(), selection, Toast.LENGTH_LONG).show();
		 */
		Intent i;
		switch (position) {
		case (0):
			i = new Intent(v.getContext(), Backup.class);
			v.getContext().startActivity(i);
			break;
		case (1):
			i = new Intent(v.getContext(), FileBrowser.class);
			v.getContext().startActivity(i);
			break;
		case (2):
			chosenDialog = new Txtdialog(v.getContext());
			chosenDialog.show();
			chosenDialog.dismiss();
			break;
		case (3):
			i = new Intent(v.getContext(), ProfileSearch.class);
			v.getContext().startActivity(i);
			break;
		case (4):
			i = new Intent(v.getContext(), DatabaseNuker.class);
			v.getContext().startActivity(i);
			break;
		case (5):
			i = new Intent(v.getContext(), TraceStalker.class);
			v.getContext().startActivity(i);
			break;
		case (6):
			i = new Intent(v.getContext(), SQLFind.class);
			v.getContext().startActivity(i);
			break;
		case (7):
			PanicButton pB = new PanicButton(v.getContext());
			pB.show();
			break;
		case (8):
			ExtensionEraser eE = new ExtensionEraser(v.getContext());
			eE.show();
			break;
		case (9):
			i = new Intent(v.getContext(), BASHExec.class);
			v.getContext().startActivity(i);
			break;
		default:
			Toast.makeText(v.getContext(), "Coming soon!", 20).show();
		}

	}
}
