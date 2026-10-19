# Setting up Google Sign-In (single sign-on)

The app signs in with Google, then sends the Google ID token to the API (`google_login.php`), which verifies it with Google and issues BrightStart's own token. Three things must match: the **Web client ID** (app and server), and an **Android client** for your app's package name and signing key.

## 1. Create the OAuth clients (Google Cloud Console)
1. Go to https://console.cloud.google.com and create a project (e.g. `BrightStart`).
2. **APIs & Services -> OAuth consent screen:** choose **External**, fill in the app name and your email, and add your own Google account(s) under **Test users** while the app is in testing.
3. **APIs & Services -> Credentials -> Create credentials -> OAuth client ID:**
   - **Web application**: name it `BrightStart Web`. Copy its **Client ID**. This is the one the app and the server use.
   - **Android**: package name `com.brightstart.app` and the **SHA-1** of the key you build with (next section).

## 2. Get your SHA-1
In Android Studio open the **Gradle** panel -> `app` -> `Tasks` -> `android` -> **signingReport**, or run:
```
./gradlew signingReport
```
Copy the **SHA1** under `Variant: debug`. Add one Android OAuth client per machine that builds the app (each laptop has its own debug key).

## 3. Put the Web client ID in the app
Add this line to `gradle.properties` (a client ID is public, not a secret):
```
GOOGLE_WEB_CLIENT_ID=1234567890-abcdef.apps.googleusercontent.com
```
Rebuild. Until it is set, the Google button shows "Google sign-in is not set up in this build yet."

## 4. Put the same Web client ID on the server
Azure Portal -> your web app -> **Settings -> Environment variables** -> add `GOOGLE_CLIENT_ID` = the same Web client ID -> **Apply**.

Optional: `SSO_AUTO_APPROVE` = `1` lets new Google users in immediately; `0` (default) makes an administrator approve them first.

## 5. Test
Tap **Continue with Google** on the login screen. The phone needs a Google account and Google Play services. On a first sign-in with `SSO_AUTO_APPROVE=0` you will see "pending administrator approval"; an Admin approves you in **Manage Admin Users**, and the next attempt works.

## Troubleshooting
| Message | Cause |
|---|---|
| "Google sign-in is not set up in this build yet" | `GOOGLE_WEB_CLIENT_ID` is missing in `gradle.properties` |
| "Google sign-in failed" | Android OAuth client missing, wrong SHA-1, or wrong package name |
| "Google sign-in could not be verified" | Server `GOOGLE_CLIENT_ID` differs from the app's Web client ID |
| "No Google account found on this phone" | Add a Google account in the phone's settings |
