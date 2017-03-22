package xyz.juniverse.stuff.social;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 21/03/2017.
 */

class FacebookLogin extends LoginMethod
{
    private CallbackManager callbackManager;
    FacebookLogin(FragmentActivity activity)
    {
        super(activity);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                console.i("facebook onSuccess", loginResult);
                saveLastUsedLoginMethod();
                listener.onResult(true, null);
            }

            @Override
            public void onCancel() {
                console.i("facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                console.i("facebook onError", error);
                listener.onResult(false, error.getLocalizedMessage());
            }
        });
    }

    @Override
    public void login(OnLoginResultListener listener)
    {
        super.login(listener);
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));
    }

    @Override
    public boolean isLoggedIn()
    {
        return AccessToken.getCurrentAccessToken() != null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
