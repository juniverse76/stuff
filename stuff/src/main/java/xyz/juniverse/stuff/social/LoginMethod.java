package xyz.juniverse.stuff.social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

import xyz.juniverse.stuff.console;

/**
 * Created by juniverse on 21/03/2017.
 *
 * todo 얘는 library로 빼도 되겠다
 */

public class LoginMethod
{
    private static final String PREF_LAST_LOGIN_METHOD = "loginMethod";

    // todo 사실 얘들도 protocol 종속성이긴 하다.
//    public static final int None = 0;
    public static final int Facebook = 1;
    public static final int GUEST = 2;
    public static final int GameCenter = 3;
    public static final int Google = 4;

    public static class Factory
    {
        public static LoginMethod create(int method, FragmentActivity activity)
        {
            LoginMethod m;
            switch (method)
            {
                case Facebook:      m = new FacebookLogin(activity);        break;
                case Google:        m = new GooglePlusLogin(activity);      break;
                default:            m = new LoginMethod(activity);          break;
            }
            console.i("created for", method);
            m.methodId = method;
            return m;
        }

        public static LoginMethod getLastUsedMethod(FragmentActivity activity)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
            return create(preferences.getInt(PREF_LAST_LOGIN_METHOD, LoginMethod.GUEST), activity);
        }

        public static LoginMethod getLastUsedMethod(AppCompatActivity activity)
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
            return create(preferences.getInt(PREF_LAST_LOGIN_METHOD, LoginMethod.GUEST), activity);
        }
    }

    public interface OnLoginResultListener
    {
        void onResult(boolean success, String message);
    }

    private int methodId = GUEST;
    FragmentActivity activity;
    @NonNull OnLoginResultListener listener;

    LoginMethod(FragmentActivity activity)
    {
        this.activity = activity;
    }

    public void login(OnLoginResultListener listener)
    {
        this.listener = listener;
    }

    public boolean isLoggedIn()
    {
        return false;
    }

    public int getPlatformId()
    {
        return methodId;
    }

    public String getUserId()
    {
        return null;
    }



    void saveLastUsedLoginMethod()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity.getBaseContext());
        preferences.edit().putInt(PREF_LAST_LOGIN_METHOD, methodId).apply();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { }
}
