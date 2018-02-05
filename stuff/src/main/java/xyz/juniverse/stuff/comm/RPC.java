package xyz.juniverse.stuff.comm;

/**
 * Created by juniverse on 20/03/2017.
 *
 */

abstract public class RPC
{

    protected static String serverUrl;

    public static void setServerCredential(String server)
    {
        RPC.serverUrl = server;
    }

    public interface OnResultListener
    {
        void onResult(String response);
    }

    public void call(final String cmd, final String params, final OnResultListener listener)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = request(cmd, params);
                    if (listener != null)
                        listener.onResult(response);
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
    abstract public String request(String rpc, String params) throws Exception;

}
