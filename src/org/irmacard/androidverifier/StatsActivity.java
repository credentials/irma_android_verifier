package org.irmacard.androidverifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.irmacard.pilot.androidverifier.R;

import android.app.Activity;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class StatsActivity extends Activity implements OnClickListener {

    private String DBPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        DBPath = getBaseContext().getFilesDir().getPath().replaceFirst("/data", "").replace("files", "databases/verifications.db");
        // Prevent the screen from turning off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        findViewById(R.id.button2).setOnClickListener(this);
        TextView sCount = (TextView) findViewById(R.id.stat_successcount);
        TextView fCount = (TextView) findViewById(R.id.stat_failcount);
        TextView eCount = (TextView) findViewById(R.id.stat_errorcount);
        TextView tCount = (TextView) findViewById(R.id.stat_totalcount);
        TextView sPerc = (TextView) findViewById(R.id.stat_successpercentage);
        TextView fPerc = (TextView) findViewById(R.id.stat_failpercentage);
        TextView ePerc = (TextView) findViewById(R.id.stat_errorpercentage);
        
        SQLiteDatabase db = SQLiteDatabase.openDatabase("/data" + DBPath, null, SQLiteDatabase.OPEN_READONLY);
        long total = DatabaseUtils.queryNumEntries(db, "verifications");
        long success = 0, fail = 0, error = 0;
        if (total > 0) {
        	success = DatabaseUtils.queryNumEntries(db, "verifications", "result = ?", new String[] { Integer.toString(Verification.RESULT_VALID) });
        	fail = DatabaseUtils.queryNumEntries(db, "verifications", "result = ?", new String[] { Integer.toString(Verification.RESULT_FAILED) });
        	error = DatabaseUtils.queryNumEntries(db, "verifications", "result = ?", new String[] { Integer.toString(Verification.RESULT_INVALID) });
        }
        
        tCount.setText(Long.toString(total));
        sCount.setText(Long.toString(success));
        fCount.setText(Long.toString(fail));
        eCount.setText(Long.toString(error));
        
        if (total > 0) {
        	sPerc.setText(Double.toString((100.0 * success) / total) + " %");
        	fPerc.setText(Double.toString((100.0 * fail) / total) + " %");
        	ePerc.setText(Double.toString((100.0 * error) / total) + " %");
        } else {
        	sPerc.setText("");
        	fPerc.setText("");
        	ePerc.setText("");
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.button2:
			exportDB();
			break;
		}
	}
	
	private void exportDB(){
        File sd = Environment.getExternalStorageDirectory();
	    File data = Environment.getDataDirectory();
	    FileChannel source=null;
	    FileChannel destination=null;
	    String backupDBPath = "IRMA_verifications.db";
	    File currentDB = new File(data, DBPath);
	    File backupDB = new File(sd, backupDBPath);
	    try {
	         source = new FileInputStream(currentDB).getChannel();
	         destination = new FileOutputStream(backupDB).getChannel();
	         destination.transferFrom(source, 0, source.size());
	         source.close();
	         destination.close();
	         Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
	    } catch(IOException e) {
	      	e.printStackTrace();
	    }
	}
}
