package org.irmacard.androidverifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * An adapter class for showing verifications in a ListView.
 * @author Maarten Everts, TNO.
 *
 */
public class VerificationCursorAdapter extends SimpleCursorAdapter {
	private static DateFormat dateFormat = new SimpleDateFormat("MMM dd 'at' HH:mm:ss");
	public VerificationCursorAdapter(Context context, Cursor c) {
		super(context, R.layout.activity_verification_list_item, c, 
				new String[] { VerificationData.Verifications.COLUMN_NAME_RESULT, VerificationData.Verifications.COLUMN_NAME_RESULT,  VerificationData.Verifications.COLUMN_NAME_TIMESTAMP }, 
        		new int[] { R.id.checkicon, R.id.checkmessage, R.id.checktimestamp}, 0);
		// TODO Auto-generated constructor stub
		
	}
	@Override
	public void setViewImage(ImageView v, String value) {
		if (value.equals(Integer.toString(Verification.RESULT_VALID))) {
			v.setImageResource(R.drawable.green_check0064);
		} else if (value.equals(Integer.toString(Verification.RESULT_INVALID))) {
			v.setImageResource(R.drawable.red_cross0064);
		} else if (value.equals(Integer.toString(Verification.RESULT_FAILED))) {
			v.setImageResource(R.drawable.orange_questionmark0064);
		}
	}
	@Override
	public void setViewText(TextView v, String text) {
		switch (v.getId()) {
		case R.id.checktimestamp:
			long ts = Long.parseLong(text);
			v.setText(dateFormat.format(new Date(ts)));
			break;
		case R.id.checkmessage:
			switch (Integer.parseInt(text)) {
			case Verification.RESULT_VALID:
				v.setText(R.string.verification_list_valid);
				break;
			case Verification.RESULT_INVALID:
				v.setText(R.string.verification_list_invalid);
				break;
			case Verification.RESULT_FAILED:
				v.setText(R.string.verification_list_failed);
				break;
			}
			break;
		default:
			break;
		}
	}
}
