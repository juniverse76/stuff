# stuff
Library stuff for Android projects

social 기능
LoginMethod class 사용
* Facebook
  - 페이스북에 앱 등록 시 정보 다 나와있음.
  - strings.xml
    ```
    <string name="facebook_app_id">[Facebook App ID]</string>
    <string name="fb_login_protocol_scheme">[Facebook App Scheme]</string>
    ```
  - manifest.xml
    ```
    <activity
            android:name="com.facebook.FacebookActivity"
            android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
            android:label="@string/app_name" />
    <activity
            android:name="com.facebook.CustomTabActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.VIEW" />

                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />

                <data android:scheme="@string/fb_login_protocol_scheme" />
            </intent-filter>
    </activity>
    ```
* Google
  - project gradle
    ```
    classpath 'com.google.gms:google-services:3.0.0'
    ```
  - app gradle
    ```
    apply plugin: 'com.google.gms.google-services'
    ```
  - google-services.json 파일 추가.
  - 자세한 것은 firebase에 앱 등록할 떄 나와있음.
  - todo 아직도 좀 남은 게 있음.
