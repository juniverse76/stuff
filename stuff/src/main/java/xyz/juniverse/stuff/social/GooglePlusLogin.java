package xyz.juniverse.stuff.social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 21/03/2017.
 */

class GooglePlusLogin extends LoginMethod implements GoogleApiClient.OnConnectionFailedListener, FirebaseAuth.AuthStateListener
{
    private static final int REQUEST_SIGN_IN = 2395;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private FirebaseAuth auth;

    GooglePlusLogin(FragmentActivity activity)
    {
        super(activity);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .enableAutoManage(activity, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        String token = preferences.getString("token", null);
        if (token != null)
            auth.signInWithCustomToken(token);

        console.i("Google Login created");
    }

    @Override
    public void login(OnLoginResultListener listener)
    {
        super.login(listener);

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, REQUEST_SIGN_IN);
    }

    @Override
    public boolean isLoggedIn()
    {
        return auth.getCurrentUser() != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // todo 이 토큰 저장하고, 가지고 있으면 로그인 할 때 자동으로 로그인 하는 거 처리해보자...
                String token = result.getSignInAccount().getIdToken();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
                preferences.edit().putString("token", token).apply();
                saveLastUsedLoginMethod();
                listener.onResult(true, null);
            }
            else {
                console.i("failed..", result.getStatus().getStatusMessage());
                listener.onResult(false, result.getStatus().getStatusMessage());
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        console.i("onConnectionFailed???", connectionResult);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        console.i("onAuthStateChanged", firebaseAuth.getCurrentUser());
    }

}
