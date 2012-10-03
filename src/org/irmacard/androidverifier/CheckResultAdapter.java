package org.irmacard.androidverifier;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckResultAdapter extends ArrayAdapter<CheckResult>{
	public CheckResultAdapter(Context context, int textViewResourceId,
			List<CheckResult> objects) {
		super(context, textViewResourceId, objects);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.checkresultlistitem, parent, false);
		
		CheckResult cr = getItem(position);
		
		ImageView icon = (ImageView) rowView.findViewById(R.id.checkicon);
		
		switch (cr.getState()) {
		case CheckResult.STATE_VALID:
			icon.setImageResource(R.drawable.green_check0064);
			break;
		case CheckResult.STATE_INVALID:
			icon.setImageResource(R.drawable.red_cross0064);
			break;
		case CheckResult.STATE_FAILED:
			icon.setImageResource(R.drawable.orange_questionmark0064);
		}
				
		TextView messageView = (TextView) rowView.findViewById(R.id.checkmessage);
		messageView.setText(cr.getFormattedValue());
		
		TextView dateView = (TextView) rowView.findViewById(R.id.checktimestamp);
		dateView.setText(cr.getFormattedDate());
		
		return rowView;
	}
}
