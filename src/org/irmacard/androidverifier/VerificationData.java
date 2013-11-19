package org.irmacard.androidverifier;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A helper class that for using a ContentProvider together with a sqlite database (
 * for storing Verifications).
 * @author Maarten Everts, TNO.
 *
 */
public final class VerificationData {
	public static final String AUTHORITY = "org.irmacard.demo.provider.VerificationData";
	
	// This class cannot be instantiated
	private VerificationData() {
		
	}
    /**
     * Verifications table contract
     */
    public static final class Verifications implements BaseColumns {
    	// This class cannot be instantiated
        private Verifications() {}
        
        /**
         * The table name offered by this provider
         */
        public static final String TABLE_NAME = "verifications";

        /*
         * URI definitions
         */

        /**
         * The scheme part for this provider's URI
         */
        private static final String SCHEME = "content://";

        /**
         * Path parts for the URIs
         */

        /**
         * Path part for the Verifications URI
         */
        private static final String PATH_VERIFICATIONS = "/verifications";

        /**
         * Path part for the Verification ID URI
         */
        private static final String PATH_VERIFICATION_ID = "/verifications/";

        /**
         * 0-relative position of a verification ID segment in the path part of a verification ID URI
         */
        public static final int VERIFICATION_ID_PATH_POSITION = 1;

        /**
         * The content:// style URL for this table
         */
        public static final Uri CONTENT_URI =  Uri.parse(SCHEME + AUTHORITY + PATH_VERIFICATIONS);

        /**
         * The content URI base for a single verification. Callers must
         * append a numeric verification id to this Uri to retrieve a verification
         */
        public static final Uri CONTENT_ID_URI_BASE
            = Uri.parse(SCHEME + AUTHORITY + PATH_VERIFICATION_ID);

        /**
         * The content URI match pattern for a single verification, specified by its ID. Use this to match
         * incoming URIs or to construct an Intent.
         */
        public static final Uri CONTENT_ID_URI_PATTERN
            = Uri.parse(SCHEME + AUTHORITY + PATH_VERIFICATION_ID + "/#");


        /**
         * The default sort order for this table
         */
        public static final String DEFAULT_SORT_ORDER = "timestamp DESC";

        /*
         * Column definitions
         */

        /**
         * Column name for the result of the verification
         * <P>Type: INT</P>
         */
        public static final String COLUMN_NAME_RESULT = "result";
        
        /**
         * Column name for the card uid
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_CARDUID = "carduid";        

        /**
         * Column name for the card uid
         * <P>Type: TEXT</P>
         */
        public static final String COLUMN_NAME_INFO = "info";        

        
        /**
         * Column name for the verification timestamp
         * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
         */
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
