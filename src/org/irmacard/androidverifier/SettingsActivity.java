package org.irmacard.androidverifier;

import java.util.Collection;
import java.util.List;
import org.irmacard.credentials.info.DescriptionStore;
import org.irmacard.credentials.info.InfoException;
import org.irmacard.credentials.info.IssuerDescription;
import org.irmacard.credentials.info.VerificationDescription;
import org.irmacard.demo.androidverifier.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity {
	public static final String KEY_PREF_LANGUAGE = "language_list";
	public static final String KEY_PREF_VERIFIER = "verifier_list";
	public static final String KEY_PREF_VERIFICATIONDESCRIPTION = "verificationspec_list";
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
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
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// Add 'verifier' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_verifier);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_verifier);
		
		final ListPreference verifierlist = (ListPreference)findPreference(KEY_PREF_VERIFIER);
		
		fillVerifierList(verifierlist, PreferenceManager.getDefaultSharedPreferences(
				verifierlist.getContext()).getString(verifierlist.getKey(),
				""));
		String selectedVerifier = verifierlist.getValue();

		
		final ListPreference verificationspeclist = (ListPreference)findPreference(KEY_PREF_VERIFICATIONDESCRIPTION);
		
		String selectedDescription = PreferenceManager.getDefaultSharedPreferences(
				verificationspeclist.getContext()).getString(verificationspeclist.getKey(),
				"");
		fillVerificationDescriptionsList(verificationspeclist, selectedVerifier, selectedDescription);

		
		// This setting needs a custom change listener:
		// If the verifierlist changes, then we verificationspec list also needs to change
		
		verifierlist.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);
				
				String selectedVerifier = newValue.toString();
				fillVerificationDescriptionsList(verificationspeclist, selectedVerifier, "");
				
				return true;
			}
		});

		// Bind the summaries the preferences to their values. When their values change, 
		// their summaries are updated to reflect the new value, per the Android Design 
		// guidelines.
		bindPreferenceSummaryToValue(findPreference(KEY_PREF_VERIFICATIONDESCRIPTION));
		bindPreferenceSummaryToValue(findPreference(KEY_PREF_LANGUAGE));
	}

	private void fillVerifierList(ListPreference listpref, String selectedVerifier) {
		DescriptionStore ds = null;
		try {
			 ds = DescriptionStore.getInstance();
		} catch (InfoException e) {
			e.printStackTrace();
		}
		Collection<IssuerDescription> issuers = ds.getIssuerDescriptions();
		
		String[] verifierIDs = new String[issuers.size()];
		String[] verifierNames = new String[issuers.size()];
		
		int selectedIndex = 0;
		int i=0;
		for (IssuerDescription issuerDescription : issuers) {
			verifierIDs[i] = issuerDescription.getID();
			verifierNames[i] = issuerDescription.getName();
			if (verifierIDs[i].equals(selectedVerifier)) {
				selectedIndex = i;
			}
			i += 1;
		}
		
		listpref.setEntries(verifierNames);
		listpref.setEntryValues(verifierIDs);
		listpref.setValueIndex(selectedIndex);
		listpref.setValue(verifierIDs[selectedIndex]);
		listpref.setSummary(verifierNames[selectedIndex]);
		
	}
	
	private void fillVerificationDescriptionsList(ListPreference listpref, String verifierID, String selectedDescription) {
		DescriptionStore ds = null;
		try {
			 ds = DescriptionStore.getInstance();
		} catch (InfoException e) {
			e.printStackTrace();
		}
		Collection<VerificationDescription> verificationDescriptions = ds.getVerificationDescriptionsForVerifier(verifierID);
		String[] verificationDescriptionIDs = new String[verificationDescriptions.size()];
		String[] verificationDescriptionNames = new String[verificationDescriptions.size()];
		
		int selectedIndex = 0;
		int i = 0;
		for (VerificationDescription verificationDescription : verificationDescriptions) {
			verificationDescriptionIDs[i] = verificationDescription.getVerificationID();
			verificationDescriptionNames[i] = verificationDescription.getName();
			if (verificationDescriptionIDs[i].equals(selectedDescription)) {
				selectedIndex = i;
			}
			i += 1;
		}

		listpref.setEntries(verificationDescriptionNames);
		listpref.setEntryValues(verificationDescriptionIDs);
		listpref.setValueIndex(selectedIndex);
		listpref.setSummary(verificationDescriptionNames[selectedIndex]);
		listpref.setValue(verificationDescriptionIDs[selectedIndex]);

	}
	
	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();
			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}

}
