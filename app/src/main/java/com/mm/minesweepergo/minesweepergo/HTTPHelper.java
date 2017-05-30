package com.mm.minesweepergo.minesweepergo;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import com.mm.minesweepergo.minesweepergo.DomainModel.*;

/**
 * Created by Milan Nikolić on 30-May-17.
 */

public class HTTPHelper {




//    public static String inputStreamToString(InputStream is) {
//        String line = "";
//        StringBuilder total = new StringBuilder();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        try {
//            while ((line = br.readLine()) != null) {
//                total.append(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return total.toString();
//    }
//
    public static String createUser(User user) {

        try {
            URL url = new URL(Constants.URL + "/register");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject holder = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("username", user.username);
            data.put("password", user.password);
            data.put("phoneNumber", user.phoneNumber);
            data.put("firstName", user.firstName);
            data.put("lastName", user.lastName);
            data.put("email", user.email);
            holder.put("user", data);

            Uri.Builder builder = new Uri.Builder();

            String query = builder.build().getEncodedQuery();

            String retStr = "";
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());
            } else {
                retStr = String.valueOf("Error:" + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            retStr = "Malformed URL!";
        } catch (Exception e) {
            e.printStackTrace();
            retStr = "Failed uploading!";
        }

        return retStr;

    }
//
//    public static MyPlace getMyPlace(String itemName) {
//        MyPlace place = null;
//        try {
//            URL url = new URL("http://10.0.2.2:8080");
//            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            Uri.Builder builder = new Uri.Builder()
//                    .appendQueryParameter("req", GET_MY_PLACE)
//                    .appendQueryParameter("name", itemName);
//            String query = builder.build().getEncodedQuery();
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
//            writer.write(query);
//            writer.flush();
//            writer.close();
//            os.close();
//
//            int responseCode = conn.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                String str = inputStreamToString(conn.getInputStream());
//                JSONObject jsonObject = new JSONObject(str);
//                JSONObject jsonPlace = jsonObject.getJSONObject("myplace");
//                place = new MyPlace(
//                        jsonPlace.getString("name"),
//                        jsonPlace.getString("desc"),
//                        jsonPlace.getString("lat"),
//                        jsonPlace.getString("lon")
//                );
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return place;
//    }
}
