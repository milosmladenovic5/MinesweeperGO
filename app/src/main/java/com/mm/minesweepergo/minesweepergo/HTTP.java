package com.mm.minesweepergo.minesweepergo;

import android.net.Uri;
import android.util.Log;

import com.mm.minesweepergo.minesweepergo.Constants;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan Nikolić on 31-May-17.
 */

public class HTTP {

    private static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = bf.readLine()) != null) {
                total.append(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return total.toString();
    }

    public static String createUser(User user) {

        String retStr = "";

        try {
            URL url = new URL(Constants.URL + "/api/register");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            Log.e("http", "por1");


            JSONObject body = new JSONObject();

            body.put("username", user.username);
            body.put("password", user.password);
            body.put("email", user.email);
            body.put("firstname", user.firstName);
            body.put("lastname", user.lastName);
            body.put("phonenumber", user.phoneNumber);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();



            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            Log.e("http", String.valueOf(responseCode));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else
                retStr = String.valueOf("Error: " + responseCode);

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "error");
        }
        return retStr;
    }

    public static List<String> getAllUsernames() {
        List<String> names = new ArrayList<String>();
        String retStr = null;
        try {
            URL url = new URL(Constants.URL + "/api/getAllUsernames");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

//            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", "yes");
//            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            //bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String name = jsonArray.getString(i);
                    names.add(name);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return names;
    }
}
