package com.android.gumshoeUI;

import android.app.DatePickerDialog;
import android.content.Context;

public class DateDialog extends DatePickerDialog{
	
	public DateDialog(Context context, OnDateSetListener callBack, int year,
			int monthOfYear, int dayOfMonth) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		
	}
	 
}
