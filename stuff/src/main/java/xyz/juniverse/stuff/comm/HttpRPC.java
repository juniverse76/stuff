package xyz.juniverse.stuff.comm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by juniverse on 20/03/2017.
 */

public class HttpRPC extends RPC
{
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    @Override
    protected String request(String rpc, String query) throws Exception {
        URL url = new URL(RPC.serverUrl + rpc);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setConnectTimeout(CONNECT_TIMEOUT);

        // parameter가 있는 경우...
        if (query != null && query.length() > 0) {
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
        }
        else
            connection.setRequestMethod("GET");

        connection.connect();

        int responseCode = connection.getResponseCode();
        String response = "";
        if (responseCode == HttpURLConnection.HTTP_OK)
        {
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                response += line;
            }
        }

        return response;
    }
}
