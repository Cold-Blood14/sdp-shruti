# Google Sign-In Setup Instructions

## Prerequisites
1. Firebase project is created and `google-services.json` is in place
2. Firebase Authentication is enabled in Firebase Console

## Steps to Enable Google Sign-In

### 1. Enable Google Sign-In in Firebase Console
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **younext-aa460**
3. Navigate to **Authentication** > **Sign-in method**
4. Click on **Google** provider
5. Enable the Google provider
6. Set the **Support email** (required)
7. Click **Save**

### 2. Get the Web Client ID
After enabling Google Sign-In, you need to get the Web Client ID:

1. In Firebase Console, go to **Project Settings** (gear icon)
2. Scroll down to **Your apps** section
3. Find the **Web app** (if you don't have one, create a Web app)
4. Copy the **Web Client ID** (it looks like: `999497067839-xxxxxxxxxxxxx.apps.googleusercontent.com`)

### 3. Update google-services.json
1. After enabling Google Sign-In, download the updated `google-services.json` from Firebase Console
2. Replace the existing file at `app/google-services.json`
3. The new file should contain the `oauth_client` configuration with the Web Client ID

### 4. Alternative: Manual Configuration
If the Web Client ID is not automatically added to `google-services.json`, you can manually configure it:

1. Open `app/src/main/java/com/example/myapplication/features/auth/AuthViewModel.kt`
2. Find the line with the placeholder Web Client ID:
   ```kotlin
   .requestIdToken("999497067839-xxxxx.apps.googleusercontent.com")
   ```
3. Replace `999497067839-xxxxx.apps.googleusercontent.com` with your actual Web Client ID from step 2

## Testing Google Sign-In

1. Build and run the app
2. Click "Login with Google" button on the Login or SignUp screen
3. Select a Google account
4. Grant permissions
5. You should be signed in and redirected to the dashboard

## Troubleshooting

### Error: "10:" or "DEVELOPER_ERROR"
- Ensure Google Sign-In is enabled in Firebase Console
- Verify the Web Client ID is correct
- Make sure you've downloaded the updated `google-services.json` after enabling Google Sign-In

### Error: "12500:" or "SIGN_IN_CANCELLED"
- User cancelled the sign-in process
- This is normal behavior

### Error: "7:" or "NETWORK_ERROR"
- Check internet connection
- Ensure Google Play Services is up to date on the device/emulator

## Notes
- The app will automatically use the Web Client ID from Firebase configuration if available
- If the default Web Client ID is not found, it will use the fallback value that needs to be manually updated
- Make sure your app's package name matches the one in Firebase Console: `com.example.myapplication`

