irma_android_verifier
=====================

Android application to verify attributes stored on an IRMA card. At the moment you can use the settings menu to select which credential you want to verify. This package has been preconfigured to build two version of the application one with the demo branch of `irma_configuration` and one with the pilot branch of `irma_configuration`.

## Prerequisites

This application has the following dependencies.  All these dependencies will be automatically downloaded by gradle when building or installing the library.

External depenencies:

 * Android support v4

Internal dependencies:

 * [irma_android_library](https://github.com/credentials/irma_android_library/), The IRMA android library
 * [Scuba](https://github.com/credentials/scuba), The smartcard abstraction layer, uses `scuba_sc_android` and `scuba_smartcard`

Gradle will take care of the transitive dependencies. However, you must make sure that you [build and install the idemix_library](https://github.com/credentials/idemix_library/) yourself.

The build system depends on gradle version at least 2.1, which is why we've included the gradle wrapper, so you always have the right version.

## Getting the irma_configurations

To make building as easy as possible, we've included submodules for the `irma_configuration`s. Don't forget to check these out as otherwise your projects will be defunct. If this is your fresh checkout, just run

    git submodule update --init 
    
Unfortunately, while the submodules are set to track the correct branches, we have to manually update them to pull in new changes. To do so, run

    git submodule update --remote

don't forget to create a *seperate* commit for this.

## Building

Run

    ./gradlew assemble

this will create the required `.apk`s and place them in `build/outputs/apk`. Note that there will be different files for the demo and the pilot version of this application.

## Installing on your own device

You can install the application to you own device by running

    ./gradlew installDemoDebug

or

    ./gradlew installPilotDebug
