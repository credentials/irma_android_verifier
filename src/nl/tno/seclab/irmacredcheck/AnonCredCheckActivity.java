package nl.tno.seclab.irmacredcheck;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;

import com.ibm.zurich.idmx.showproof.Proof;
import com.ibm.zurich.idmx.showproof.ProofSpec;
import com.ibm.zurich.idmx.showproof.Verifier;
import com.ibm.zurich.idmx.utils.StructureStore;
import com.ibm.zurich.idmx.utils.SystemParameters;

import credentials.Attributes;
import credentials.CredentialsException;
import credentials.idemix.IdemixCredentials;
import credentials.idemix.spec.IdemixVerifySpecification;

import service.IdemixService;

import net.sourceforge.scuba.smartcards.CardService;
import net.sourceforge.scuba.smartcards.CardServiceException;
import net.sourceforge.scuba.smartcards.IsoDepCardService;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

public class AnonCredCheckActivity extends Activity {
	
	CheckResultAdapter checkresults;
	private NfcAdapter nfcA;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private IsoDep lastTag;
	private final String TAG = "AnonCredCheck";
	private IdemixVerifySpecification idemixVerifySpec;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ArrayList<CheckResult> emptyList = new ArrayList<CheckResult>();
        checkresults = new CheckResultAdapter(getApplicationContext(), R.layout.checkresultlistitem, emptyList);
        ListView lv = (ListView)findViewById(R.id.resultslistview);
        lv.setAdapter(checkresults);
        
        // NFC stuff
        nfcA = NfcAdapter.getDefaultAdapter(getApplicationContext());
        mPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        // Setup an intent filter for all TECH based dispatches
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        mFilters = new IntentFilter[] { tech };

        // Setup a tech list for all IsoDep cards
        mTechLists = new String[][] { new String[] { IsoDep.class.getName() } };
        
        setupIdemix();
    }
    
    public void setupIdemix() {
		StructureStore.getInstance().get("http://www.irmacard.org/credentials/phase1/RU/sp.xml",
        		getApplicationContext().getResources().openRawResource(R.raw.sp));
		
		StructureStore.getInstance().get("http://www.irmacard.org/credentials/phase1/RU/gp.xml",
        		getApplicationContext().getResources().openRawResource(R.raw.gp));

        StructureStore.getInstance().get("http://www.irmacard.org/credentials/phase1/RU/ipk.xml",
        		getApplicationContext().getResources().openRawResource(R.raw.ipk));
		
        StructureStore.getInstance().get("http://www.irmacard.org/credentials/phase1/RU/studentCard/structure.xml",
        		getApplicationContext().getResources().openRawResource(R.raw.structure));

        ProofSpec spec = (ProofSpec) StructureStore.getInstance().get("specification",
        		getApplicationContext().getResources().openRawResource(R.raw.specification));
        
        // 0x0064 is the id of the student credential
        idemixVerifySpec = new IdemixVerifySpecification(spec, (short)0x0064);
        
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if (nfcA != null) nfcA.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())) {
            onNewIntent(getIntent());
        }
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if (nfcA != null) nfcA.disableForegroundDispatch(this);
    }
    
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "Discovered tag with intent: " + intent);
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	IsoDep tag = IsoDep.get(tagFromIntent);
    	if (tag != null) {
    		lastTag = tag;
    		Log.i(TAG,"Found IsoDep tag!");
    		showProgressDialog();
    		new CheckCardCredentialTask().execute(tag);
    	}
    }
    
    private void showResultDialog(boolean value) {
    	DialogFragment df = (DialogFragment)getFragmentManager().findFragmentByTag("checkingdialog");
    	if (df != null) {
    		df.dismiss();
    	}
    	DialogFragment newFragment = CheckResultDialogFragment.newInstance(value);
    	newFragment.show(getFragmentManager(), "resultdialog");    	
    }
    
    private void showProgressDialog() {
    	DialogFragment df = (DialogFragment)getFragmentManager().findFragmentByTag("resultdialog");
    	if (df != null) {
    		df.dismiss();
    	}    	
    	DialogFragment newFragment = ProgressDialogFragment.newInstance(R.string.checkcredentialstitle);
    	newFragment.show(getFragmentManager(), "checkingdialog");    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_test:
    		showProgressDialog();
    		if (lastTag != null) {
    			new CheckCardCredentialTask().execute(lastTag);
    		}
//        	checkresults.insert(new CheckResult((checkresults.getCount() % 2) == 0), 0);
        	return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }
    
    public static class ProgressDialogFragment extends DialogFragment {

        public static ProgressDialogFragment newInstance(int title) {
            ProgressDialogFragment frag = new ProgressDialogFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(getArguments().getInt("title"))
                    .setView(new ProgressBar(getActivity().getApplicationContext(), null,
        					android.R.attr.progressBarStyleLarge))
                    .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            	dialog.dismiss();
                            }
                        }
                    )
                    .create();
        }
    }

    public static class CheckResultDialogFragment extends DialogFragment {

        public static CheckResultDialogFragment newInstance(boolean value) {
            CheckResultDialogFragment frag = new CheckResultDialogFragment();
            Bundle args = new Bundle();
            args.putBoolean("value", value);
            frag.setArguments(args);
            return frag;
        }

       
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	ImageView iv = new ImageView(getActivity().getApplicationContext());
        	boolean value = getArguments().getBoolean("value");
        	iv.setImageResource(value ? R.drawable.green_check0350 : R.drawable.red_cross0350);
            return new AlertDialog.Builder(getActivity())
                    .setTitle(value ? R.string.foundcredential_title : R.string.nocredential_title)
                    .setView(iv)
                    .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            	dialog.dismiss();
                            }
                        }
                    )
                    .create();
        }
    }
    
    private class CheckCardCredentialTask extends AsyncTask<IsoDep, Void, Boolean> {

		@Override
		protected Boolean doInBackground(IsoDep... arg0) {
			IsoDep tag = arg0[0];
			
			// Make sure time-out is long enough (10 seconds)
			tag.setTimeout(10000);
			
			CardService cs = new IsoDepCardService(tag);

			IdemixCredentials ic = new IdemixCredentials(cs);
			Attributes attr = null;
			try {
				attr = ic.verify(idemixVerifySpec);
				if (attr == null) {
		            Log.i(TAG,"The proof does not verify");
		            return false;
		        } else {
		        	Log.i(TAG,"The proof verified!");
		        	return true;
		        }				
			} catch (CredentialsException e) {
				Log.e(TAG, "Idemix verification threw an Exception!");
				e.printStackTrace();
				// TODO: possibly handle this differently to be able to indicate
				// in the GUI that an error has occurred (instead of just that
				// the verification failed).
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			AnonCredCheckActivity.this.checkresults.insert(new CheckResult(result.booleanValue()), 0);
			AnonCredCheckActivity.this.showResultDialog(result.booleanValue());
		}
			
    }
}