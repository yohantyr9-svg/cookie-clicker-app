# Cookie Clicker — Android starter project

An offline-first Cookie Clicker game for Android with:
- Every tap earns exactly 1 cookie before upgrades.
- 100 purchasable upgrade levels (10 upgrade types, each with 10 levels).
- Local save for cookies and upgrades.
- A local admin panel protected by a PIN that the owner sets on first use.
- Dark gaming-style interface.

## Admin panel security
On first launch, open **Admin Panel** and set an admin PIN. The PIN is stored locally on this device. This is a basic local lock for a single-player offline game, not strong security against someone who can modify the app or inspect its local storage. Do not reuse an important password.

## Build the APK
1. Install Android Studio: https://developer.android.com/studio
2. Extract this ZIP.
3. Open the `cookie-clicker-app` folder in Android Studio.
4. Let Gradle sync and install requested SDK components.
5. Choose **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. The debug APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

This is source code, not a precompiled APK.


## Build from your phone using GitHub Actions
This project includes `.github/workflows/build-apk.yml` so you can build the APK from a mobile browser without installing Android Studio.

1. Sign in or create an account at https://github.com.
2. Create a new repository named `cookie-clicker-app` and set it to **Private** if you want to keep the source private.
3. Upload the contents of this project folder to the repository (the `.github/workflows/build-apk.yml` file must be included at that exact path).
4. Open the repository's **Actions** tab and select **Build Cookie Clicker APK**.
5. Tap **Run workflow** (or wait for the build to start after pushing files).
6. Open the completed workflow run, scroll to **Artifacts**, and download `cookie-clicker-debug-apk`.
7. Extract the downloaded artifact ZIP; it contains `app-debug.apk`. Tap it to install, allowing installs from your browser or file manager if Android prompts you.

GitHub's mobile web interface can make uploading multiple project files awkward. If uploading the extracted folder is difficult, use GitHub's web upload feature from a browser with Desktop site enabled. Keep the hidden `.github` folder and its workflow file in the repository.
