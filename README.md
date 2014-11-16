irma_android_verifier
=====================

Android application to verify attributes stored on an IRMA card.

## Setting up irma_configuration

First, make sure you have the right version of irma_configuration. Then you have to link irma_configuration into `assets`

## Notes when adding the project to Eclipse

 1. Go to Properties -> Android, and check that the two required libraries (from scuba and credentials/irma_android_library) are properly linked.
 2. Also install the necessary android libraries
 3. Eclipse can be obnoxious about errors. It sometimes helps to clean and rebuild, after you're sure you've set the libraries correctly.
