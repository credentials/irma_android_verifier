package nl.tno.seclab.irmacredcheck;

import java.util.List;

import nl.tno.seclab.irmacredcheck.R;
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
		
		if (cr.getValue()) {
			icon.setImageResource(R.drawable.green_check0064);
		} else {
			icon.setImageResource(R.drawable.red_cross0064);
		}
		
		TextView messageView = (TextView) rowView.findViewById(R.id.checkmessage);
		messageView.setText(cr.getFormattedValue());
		
		TextView dateView = (TextView) rowView.findViewById(R.id.checktimestamp);
		dateView.setText(cr.getFormattedDate());
		
		return rowView;
	}
}
