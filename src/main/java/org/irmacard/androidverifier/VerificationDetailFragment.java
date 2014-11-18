package org.irmacard.androidverifier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v4.app.Fragment;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class VerificationDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private static DateFormat dateFormat = new SimpleDateFormat("EEEE, MMMMM dd, yyyy 'at' HH:mm:ss");
    private Cursor mCursor;

    public VerificationDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
        	long itemId = getArguments().getLong(ARG_ITEM_ID);
        	Uri uri = ContentUris.withAppendedId(VerificationData.Verifications.CONTENT_ID_URI_BASE, itemId);
            mCursor = getActivity().getContentResolver().query(uri,
              		null, null, null, null);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_verification_detail, container, false);
        if (mCursor.moveToNext()) {
        	int result = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("result")));
        	long timestamp = Long.parseLong(mCursor.getString(mCursor.getColumnIndex("timestamp")));
        	Date timestampDate = new Date(timestamp);
        	String info = mCursor.getString(mCursor.getColumnIndex("info"));
        	String carduid = mCursor.getString(mCursor.getColumnIndex("carduid"));
        	int resultStringRes = 0;
        	switch (result) {
			case Verification.RESULT_VALID:
				resultStringRes = R.string.verification_list_valid;
				break;
			case Verification.RESULT_INVALID:
				resultStringRes = R.string.verification_list_invalid;
				break;
			case Verification.RESULT_FAILED:
				resultStringRes = R.string.verification_list_failed;
				break;

			default:
				break;
			}
        	((TextView)rootView.findViewById(R.id.resultfield)).setText(resultStringRes);
        	((TextView)rootView.findViewById(R.id.timestampfield)).setText(dateFormat.format(timestampDate));
        	((TextView)rootView.findViewById(R.id.carduidfield)).setText(carduid);
        	((TextView)rootView.findViewById(R.id.infofield)).setText(info);
        }
        return rootView;
    }
}
