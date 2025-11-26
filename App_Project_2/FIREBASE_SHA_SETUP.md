# Firebase SHA Key Setup Instructions

## Your Current SHA Fingerprints

**SHA-1:** `F4:EB:17:8A:48:8D:C7:83:72:3F:A4:9D:DA:39:E5:EB:E6:5D:5E:96`

**SHA-256:** `4B:10:38:4D:22:02:C7:CA:14:A9:38:BA:63:A1:6E:64:90:A2:62:C7:39:23:BD:F9:D1:0C:AB:82:EE:7C:C5:68`

## Steps to Add SHA Keys to Firebase

### 1. Go to Firebase Console
1. Open [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **younext-aa460**

### 2. Navigate to Project Settings
1. Click the **gear icon** (⚙️) next to "Project Overview"
2. Select **Project Settings**

### 3. Add SHA Fingerprints
1. Scroll down to **Your apps** section
2. Find your Android app: `com.example.myapplication`
3. Click on the app to expand it
4. Click **"Add fingerprint"** button
5. Add both SHA-1 and SHA-256:

   **SHA-1:**
   ```
   F4:EB:17:8A:48:8D:C7:83:72:3F:A4:9D:DA:39:E5:EB:E6:5D:5E:96
   ```

   **SHA-256:**
   ```
   4B:10:38:4D:22:02:C7:CA:14:A9:38:BA:63:A1:6E:64:90:A2:62:C7:39:23:BD:F9:D1:0C:AB:82:EE:7C:C5:68
   ```

### 4. Download Updated google-services.json
1. After adding the SHA keys, click **"Download google-services.json"**
2. Replace the existing file at: `app/google-services.json`
3. **Rebuild your project** in Android Studio

## Alternative: Get SHA Keys from Android Studio

1. In Android Studio, open **Gradle** panel (right side)
2. Navigate to: `Your App` → `Tasks` → `android` → `signingReport`
3. Double-click **signingReport**
4. Check the **Run** tab at the bottom
5. Look for **SHA1** and **SHA256** values
6. Copy those values and add them to Firebase Console

## Why This Matters

- **Google Sign-In** requires SHA-1/SHA-256 to verify your app's identity
- **Firebase Authentication** uses SHA keys to ensure requests come from your app
- Without matching SHA keys, authentication will fail with errors like:
  - "10:" (DEVELOPER_ERROR)
  - "12500:" (SIGN_IN_CANCELLED)
  - Authentication credential errors

## After Adding SHA Keys

1. Download the updated `google-services.json` from Firebase Console
2. Replace `app/google-services.json` in your project
3. **Sync Project with Gradle Files** (File → Sync Project with Gradle Files)
4. **Rebuild** your project
5. Try authentication again

## For Production Builds

When you create a release build, you'll need to:
1. Get the SHA-1/SHA-256 from your **release keystore**
2. Add those SHA keys to Firebase Console as well
3. Download the updated `google-services.json` again



