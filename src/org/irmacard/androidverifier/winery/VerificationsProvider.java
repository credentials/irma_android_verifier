package org.irmacard.androidverifier.winery;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


/**
 * A ContentProvider for verifications. Feels a bit overkill for what we
 * want, but apparently it is the 'best' way  to combine a view with a
 * sqlite database.
 * @author Maarten Everts, TNO.
 *
 */
public class VerificationsProvider extends ContentProvider {

	private static final String TAG = "VerificationsProvider";
	
    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;
    
    /*
     * Constants used by the Uri matcher to choose an action based on the pattern
     * of the incoming URI
     */
    // The incoming URI matches the Verifications URI pattern
    private static final int VERIFICATIONS = 1;

    // The incoming URI matches the Verification ID URI pattern
    private static final int VERIFICATION_ID = 2;    
    
    /**
     * A UriMatcher instance
     */
    private static final UriMatcher sUriMatcher;
    
    
    /**
     * A projection map used to select columns from the database
     */
    private static HashMap<String, String> sVerificationsProjectionMap;
    
    
    /**
     * A block that instantiates and sets static objects
     */
    static {

        /*
         * Creates and initializes the URI matcher
         */
        // Create a new instance
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        
        // Add a pattern that routes URIs terminated with "verifications" to a VERIFICATIONS operation
        sUriMatcher.addURI(VerificationData.AUTHORITY, "verifications", VERIFICATIONS);

        // Add a pattern that routes URIs terminated with "verifications" plus an integer
        // to a verification ID operation
        sUriMatcher.addURI(VerificationData.AUTHORITY, "verifications/#", VERIFICATION_ID);
        
        /*
         * Creates and initializes a projection map that returns all columns
         */

        // Creates a new projection map instance. The map returns a column name
        // given a string. The two are usually equal.
        sVerificationsProjectionMap = new HashMap<String, String>();

        // Maps the string "_ID" to the column name "_ID"
        sVerificationsProjectionMap.put(VerificationData.Verifications._ID, VerificationData.Verifications._ID);

        // Maps "result" to "result"
        sVerificationsProjectionMap.put(VerificationData.Verifications.COLUMN_NAME_RESULT, VerificationData.Verifications.COLUMN_NAME_RESULT);

        // Maps "carduid" to "carduid"
        sVerificationsProjectionMap.put(VerificationData.Verifications.COLUMN_NAME_CARDUID, VerificationData.Verifications.COLUMN_NAME_CARDUID);

        // Maps "info" to "info"
        sVerificationsProjectionMap.put(VerificationData.Verifications.COLUMN_NAME_INFO, VerificationData.Verifications.COLUMN_NAME_INFO);

        // Maps "timestamp" to "timestamp"
        sVerificationsProjectionMap.put(VerificationData.Verifications.COLUMN_NAME_TIMESTAMP, VerificationData.Verifications.COLUMN_NAME_TIMESTAMP);
  
    }
    
	/**
     * The database that the provider uses as its underlying data store
     */
    private static final String DATABASE_NAME = "verifications.db";
	
    /**
     * The database version
     */
    private static final int DATABASE_VERSION = 1;
    
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// This content provider does not support deletes
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		Log.i(TAG,"Inserting!");
        // Validates the incoming URI. Only the full provider URI is allowed for inserts.
        if (sUriMatcher.match(uri) != VERIFICATIONS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // A map to hold the new record's values.
        ContentValues values;

        // If the incoming values map is not null, uses it for the new values.
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            // Otherwise, create a new value map
            values = new ContentValues();
        }

        // Gets the current system time in milliseconds
        Long now = Long.valueOf(System.currentTimeMillis());

        // If the values map doesn't contain the timestamp date, set the value to the current time.
        if (values.containsKey(VerificationData.Verifications.COLUMN_NAME_TIMESTAMP) == false) {
            values.put(VerificationData.Verifications.COLUMN_NAME_TIMESTAMP, now);
        }

        // If the values map doesn't contain a result, set the value to the default result.
        if (values.containsKey(VerificationData.Verifications.COLUMN_NAME_RESULT) == false) {
            values.put(VerificationData.Verifications.COLUMN_NAME_RESULT, Verification.RESULT_FAILED);
        }
        
        // If the values map doesn't contain a carduid, set the value to an empty string.
        if (values.containsKey(VerificationData.Verifications.COLUMN_NAME_CARDUID) == false) {
            values.put(VerificationData.Verifications.COLUMN_NAME_CARDUID, "");
        }
        
        // If the values map doesn't contain a info field, set the value to an empty string.
        if (values.containsKey(VerificationData.Verifications.COLUMN_NAME_INFO) == false) {
            values.put(VerificationData.Verifications.COLUMN_NAME_INFO, "");
        }
        
        // Opens the database object in "write" mode.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        // Performs the insert and returns the ID of the new verification.
        long rowId = db.insert(
        	VerificationData.Verifications.TABLE_NAME,        // The table to insert into.
        	VerificationData.Verifications.COLUMN_NAME_RESULT,  // A hack, SQLite sets this column value to null
                                             // if values is empty.
            values                           // A map of column names, and the values to insert
                                             // into the columns.
        );

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            // Creates a URI with the verification ID pattern and the new row ID appended to it.
            Uri verificationUri = ContentUris.withAppendedId(VerificationData.Verifications.CONTENT_ID_URI_BASE, rowId);

            // Notifies observers registered against this provider that the data changed.
            getContext().getContentResolver().notifyChange(verificationUri, null);
            Log.i(TAG, "Insert success?!");
            return verificationUri;
        }

        // If the insert didn't succeed, then the rowID is <= 0. Throws an exception.
        throw new SQLException("Failed to insert row into " + uri);
	}

   /**
    *
    * Initializes the provider by creating a new DatabaseHelper. onCreate() is called
    * automatically when Android creates the provider in response to a resolver request from a
    * client.
    */
   @Override
   public boolean onCreate() {

       // Creates a new helper object. Note that the database itself isn't opened until
       // something tries to access it, and it's only created if it doesn't already exist.
       mOpenHelper = new DatabaseHelper(getContext());

       // Assumes that any failures will be reported by a thrown exception.
       return true;
   }

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// Constructs a new query builder and sets its table name
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(VerificationData.Verifications.TABLE_NAME);
		/**
		 * Choose the projection and adjust the "where" clause based on URI
		 * pattern-matching.
		 */
		switch (sUriMatcher.match(uri)) {
		// If the incoming URI is for verifications, chooses the Verifications projection
		case VERIFICATIONS:
			qb.setProjectionMap(sVerificationsProjectionMap);
			break;

		/*
		 * If the incoming URI is for a single verification identified by its ID,
		 * chooses the verification ID projection, and appends "_ID = <verificationID>" to the
		 * where clause, so that it selects that single verification
		 */
		case VERIFICATION_ID:
			qb.setProjectionMap(sVerificationsProjectionMap);
			qb.appendWhere(VerificationData.Verifications._ID + // the name of
																// the ID column
					"="
					+
					// the position of the verification ID itself in the incoming URI
					uri.getPathSegments()
							.get(VerificationData.Verifications.VERIFICATION_ID_PATH_POSITION));
			break;

		default:
			// If the URI doesn't match any of the known patterns, throw an
			// exception.
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		String orderBy;
		// If no sort order is specified, uses the default
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = VerificationData.Verifications.DEFAULT_SORT_ORDER;
		} else {
			// otherwise, uses the incoming sort order
			orderBy = sortOrder;
		}
		// Opens the database object in "read" mode, since no writes need to be
		// done.
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();

		/*
		 * Performs the query. If no problems occur trying to read the database,
		 * then a Cursor object is returned; otherwise, the cursor variable
		 * contains null. If no records were selected, then the Cursor object is
		 * empty, and Cursor.getCount() returns 0.
		 */
		Cursor c = qb.query(db, // The database to query
				projection, // The columns to return from the query
				selection, // The columns for the where clause
				selectionArgs, // The values for the where clause
				null, // don't group the rows
				null, // don't filter by row groups
				orderBy // The sort order
				);
		// Tells the Cursor what URI to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// This content provider does not support updates
		return 0;
	}

	static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {

			// calls the super constructor, requesting the default cursor
			// factory.
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + VerificationData.Verifications.TABLE_NAME + " (" 
					+ VerificationData.Verifications._ID + " INTEGER PRIMARY KEY,"
					+ VerificationData.Verifications.COLUMN_NAME_RESULT + " INTEGER,"
					+ VerificationData.Verifications.COLUMN_NAME_CARDUID + " TEXT,"
					+ VerificationData.Verifications.COLUMN_NAME_INFO + " TEXT,"
					+ VerificationData.Verifications.COLUMN_NAME_TIMESTAMP + " INTEGER"
					+ ");");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO 
		}
	}
}
