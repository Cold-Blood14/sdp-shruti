# YouNext (Android) — Setup & Firebase Checklist

This repository contains the Android app for YouNext, built with Kotlin + Jetpack Compose and Firebase.

## Prerequisites
- Android Studio Giraffe+ (Koala recommended)
- JDK 17
- Google Services JSON (Firebase)
- Node.js 18+ (for Cloud Functions)

## Firebase Setup
1. Create a Firebase project named "YouNext" (or reuse an existing one).
2. Enable Authentication: Email/Password and Google Sign-In.
3. Create an Android app in Firebase Console with your applicationId (default `com.example.myapplication` — update to `com.younext.app` when you rename the package).
4. Download `google-services.json` and place it at:
   - `app/google-services.json`
5. Enable Firestore (production mode) and Cloud Storage.
6. Set up Cloud Functions (Node 18, TypeScript). Recommended directory: `functions/` (see docs for endpoints).
7. Enable Firebase Cloud Messaging (for notifications).
8. Configure Remote Config keys (optional initial values):
   - `feature.community` = true
   - `recommendation.weights` = "v1"

## Android Project Configuration
- In `settings.gradle.kts` ensure Google services classpath is applied in the root build.
- In `app/build.gradle.kts` apply:
  - `com.google.gms.google-services`
  - Dependencies: Firebase BOM, Auth, Firestore, Storage, Messaging, Analytics, Crashlytics

## Local Environment
- Create a file `gradle.properties` (already present) with:
  - `org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8`
- Optional: Use Firebase Emulators for local testing.

## Run App
1. Sync Gradle.
2. Build and run on a device/emulator with Google Play Services.

## Next Steps
- Follow `docs/YouNext_Blueprint.md` for architecture, package layout, and feature roadmap.
- Rename package to `com.younext.app` and refactor namespaces.
