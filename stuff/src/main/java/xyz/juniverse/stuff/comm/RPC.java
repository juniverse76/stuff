package xyz.juniverse.stuff.comm;

import android.net.Uri;

import xyz.juniverse.stuff.crypt.Crypt;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by juniverse on 20/03/2017.
 */

abstract public class RPC
{
    protected static String serverUrl;

    public static void setServerCredential(String server)
    {
        RPC.serverUrl = server;
    }



    private Crypt crypt = null;
    public interface OnResultListener
    {
        void onResult(String result);
    }

    public void addEncryption(Crypt crypt)
    {
        this.crypt = crypt;
    }

    public void call(final String cmd, final OnResultListener listener)
    {
        call(cmd, null, listener);
    }

    public void call(final String cmd, final HashMap params, final OnResultListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String param = "";
                if (crypt != null && params != null)
                {
                    JSONObject json = new JSONObject(params);
                    param = crypt.encrypt(json.toString());

                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("cmd", param);
                    param = builder.build().getEncodedQuery();
                }

                try {
                    String response = request(cmd, param);
                    if (listener != null)
                        listener.onResult(response);

//                    JSONObject json = new JSONObject(response);
//                    String result = json.getString("response");
//                    boolean isEncrypted = json.getBoolean("isResEncrypted");
//                    if (isEncrypted && crypt != null && result != null)
//                        result = crypt.decrypt(result, resKey);
//
//                    JSONObject resultJson = new JSONObject(result);
//                    String module = resultJson.getString("moduleName");
//                    if (!cmd.equals(module))
//                    {
//                        // todo something definitely wrong!!!
//                    }
//                    if (listener != null) {
//                        if (resultJson.getBoolean("success")))
//                            listener.onSuccess(result);
//                        else
//                            listener.onError(resultJson.getInt("ecode"), resultJson.getString("message");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Must be called in other thread!!
     * @param rpc
     * @param params
     * @return
     * @throws Exception
     */
    abstract protected String request(String rpc, String params) throws Exception;
}
