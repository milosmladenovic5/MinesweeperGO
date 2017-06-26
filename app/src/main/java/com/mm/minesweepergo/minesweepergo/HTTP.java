package com.mm.minesweepergo.minesweepergo;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.mm.minesweepergo.minesweepergo.Constants;
import com.mm.minesweepergo.minesweepergo.DomainModel.Arena;
import com.mm.minesweepergo.minesweepergo.DomainModel.Game;
import com.mm.minesweepergo.minesweepergo.DomainModel.Mine;
import com.mm.minesweepergo.minesweepergo.DomainModel.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Milan Nikolić on 31-May-17.
 */

public class HTTP {

    private static String inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        try
        {
            while ((line = bf.readLine()) != null)
            {
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
            body.put("btDevice", user.btDevice);

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
                //retStr = String.valueOf("Error: " + responseCode);
                retStr = "Error";

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




    public static List<User> getOnlineUsers() {
        List<User> users = new ArrayList<User>();
        String retStr = null;
        try {
            URL url = new URL(Constants.URL + "/api/getOnlineUsers");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


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
                    JSONObject jsonObjectOrg = new JSONObject(jsonArray.getString(i));

                    JSONObject jsonObject = jsonObjectOrg.getJSONObject("properties");

                    User user = new User();

                    user.username = jsonObject.getString("Username");
                    user.password = jsonObject.getString("Password");
                    user.firstName = jsonObject.getString("FirstName");
                    user.lastName = jsonObject.getString("LastName");
                    user.btDevice = jsonObject.getString("BtDevice");
                    user.email = jsonObject.getString("Email");
                    user.imagePath = jsonObject.getString("ImageURL");
                    user.phoneNumber = jsonObject.getString("PhoneNumber");
                    user.latitude = jsonObject.getDouble("Latitude");
                    user.longitude = jsonObject.getDouble("Longitude");

                    users.add(user);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return users;
    }

    public static User login(String username, String password)
    {
        User retUser = null;

        try {
            URL url = new URL(Constants.URL + "/api/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("username", username);
            body.put("password", password);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONObject jsonObject = new JSONObject(str);

                if(jsonObject!=null){
                    retUser = new User();
                    JSONObject properties = jsonObject.getJSONObject("properties");

                    retUser.username = properties.getString("Username");
                    retUser.password = properties.getString("Password");
                    retUser.email   = properties.getString("Email");
                    retUser.firstName = properties.getString("FirstName");
                    retUser.lastName = properties.getString("LastName");
                    retUser.phoneNumber = properties.getString("PhoneNumber");
                    retUser.imagePath = properties.getString("ImageURL");


                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }

        return retUser;
    }

    public static void logout(String username){

        try {
            URL url = new URL(Constants.URL + "/api/logout");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("username", username);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String uploadPicture(String username, String path) {
        try {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            @SuppressWarnings("PointlessArithmeticExpression")
            int maxBufferSize = 1 * 1024 * 1024;


            java.net.URL url = new URL(Constants.URL + "/api/imageUpload");
            //Log.d(ApplicationConstant.TAG, "url " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Allow Inputs &amp; Outputs.
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            // Set HTTP method to POST.
            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

            FileInputStream fileInputStream;
            DataOutputStream outputStream;
            {
                outputStream = new DataOutputStream(connection.getOutputStream());

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                String filename = path;
                outputStream.writeBytes("Content-Disposition: form-data; name=\"pic\"; filename=\"" + filename+ "\""  + lineEnd);
                outputStream.writeBytes(lineEnd);
                //Log.d(ApplicationConstant.TAG, "filename " + filename);

                fileInputStream = new FileInputStream(filename);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);

                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"username\"" + lineEnd);
                outputStream.writeBytes("Content-Type: text/plain" + lineEnd);
                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(username);
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            }

            int serverResponseCode = connection.getResponseCode();
            String serverResponseMessage = connection.getResponseMessage();
            Log.d("serverResponseCode", "" + serverResponseCode);
            Log.d("serverResponseMessage", "" + serverResponseMessage);

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            if (serverResponseCode == 200) {
                return "true";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    public static List<User> getAllFriends(String username){
        List<User> users = new ArrayList<User>();
        try {
            URL url = new URL(Constants.URL + "/api/getFriends");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("username", username);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectOrg = new JSONObject(jsonArray.getString(i));

                    JSONObject jsonObject = jsonObjectOrg.getJSONObject("properties");

                    User user = new User();

                    user.username = jsonObject.getString("Username");
                    user.password = jsonObject.getString("Password");
                    user.firstName = jsonObject.getString("FirstName");
                    user.lastName = jsonObject.getString("LastName");
                    user.btDevice = jsonObject.getString("BtDevice");
                    user.email = jsonObject.getString("Email");
                    user.imagePath = jsonObject.getString("ImageURL");
                    user.phoneNumber = jsonObject.getString("PhoneNumber");
                    user.latitude = jsonObject.getDouble("Latitude");
                    user.longitude = jsonObject.getDouble("Longitude");

                    users.add(user);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return users;
    }

    public static boolean startOrEndFriendship(String username, String address, boolean starting)
    {
        try {
            URL url = null;
            if(starting)
                url = new URL(Constants.URL + "/api/startFriendship");
            else
                url = new URL(Constants.URL + "/api/endFriendship");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("username", username);
            body.put("address", address);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
              return true;
            } else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> sendLocationInfo(String username, Location location)
    {
        try {
            URL url = new URL(Constants.URL + "/api/locationMonitor");


            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("latitude", location.getLatitude());
            body.put("longitude", location.getLongitude());
            body.put("username", username);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                List<String> usernamesDistance = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    usernamesDistance.add(jsonObject.getString("Username") + " | " + jsonObject.getDouble("Distance"));
                }
                return usernamesDistance;

            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User getUser(String username)
    {
        User retUser = null;

        try {
            URL url = new URL(Constants.URL + "/api/getUser");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("username", username);


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONObject jsonObject = new JSONObject(str);

                if(jsonObject!=null){
                    retUser = new User();
                    JSONObject properties = jsonObject.getJSONObject("properties");

                    retUser.username = properties.getString("Username");
                    retUser.password = properties.getString("Password");
                    retUser.email   = properties.getString("Email");
                    retUser.firstName = properties.getString("FirstName");
                    retUser.lastName = properties.getString("LastName");
                    retUser.phoneNumber = properties.getString("PhoneNumber");
                    retUser.imagePath = properties.getString("ImageURL");


                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }

        return retUser;
    }

    public static Arena getArena(String name)
    {
        Arena retArena = null;

        try {
            URL url = new URL(Constants.URL + "/api/getArena");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("name", name);


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONObject properties = new JSONObject(str);

                if(properties!=null){
                    retArena = new Arena();

                    retArena.name = properties.getString("Name");
                    retArena.radius = properties.getDouble("Radius");
                    retArena.centerLat = properties.getDouble("CenterLatitude");
                    retArena.centerLon = properties.getDouble("CenterLongitude");
                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }

        return retArena;
    }

    public static List<Arena> getArenasByDistance(double latitude, double longitude, double radius) {
        List<Arena> arenas = new ArrayList<Arena>();

        try {
            URL url = new URL(Constants.URL + "/api/getArenasByDistance");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("latitude", latitude);
            body.put("longitude", longitude);
            body.put("radius", radius);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObjectOrg = new JSONObject(jsonArray.getString(i));

                    Arena ar = new Arena();

                    ar.name = jsonObjectOrg.getString("Name");
                    ar.radius = jsonObjectOrg.getDouble("Radius");
                    ar.centerLat = jsonObjectOrg.getDouble("CenterLatitude");
                    ar.centerLon = jsonObjectOrg.getDouble("CenterLongitude");

                    arenas.add(ar);
                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return arenas;
    }



    public static List<Game> getArenaGames(String arenaName)
    {
        List<Game> games = new ArrayList<Game>();
        try {
            URL url = new URL(Constants.URL + "/api/getArenaGames");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("arenaName", arenaName);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    //JSONObject jsonObject = jsonObjectOrg.getJSONObject("properties");

                    Game game = new Game();


                    game.setId(Integer.parseInt(jsonObject.getString("GameId"))); // SUMLJAM da ce j* int da radi
                    game.setCreatorUsername(jsonObject.getString("CreatorUsername"));

                    games.add(game);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return games;
    }

    public static List<Mine> getMines(int gameId)
    {
        List<Mine> mines = new ArrayList<Mine>();
        try {
            URL url = new URL(Constants.URL + "/api/getMines");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("gameId", gameId);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(i));

                    //JSONObject jsonObject = jsonObjectOrg.getJSONObject("properties");


                    LatLng location = new LatLng(jsonObject.getDouble("Latitude"), jsonObject.getDouble("Longitude"));
                    Mine mine = new Mine(location, jsonObject.getDouble("BlastRadius"));


                    mines.add(mine);

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return mines;
    }


    //  nije testirana!
    public static int createGame(Game game, String arenaName) { // ceo Game objekat u slucaju da joj dodamo jos neki prop. kasnije

        int gameId = -1;

        try {
            URL url = new URL(Constants.URL + "/api/createGame");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);



            JSONObject body = new JSONObject();

            body.put("creatorUsername", game.getCreatorUsername());
            body.put("arenaName", arenaName);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();



            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {
                String temp = inputStreamToString(conn.getInputStream());
                JSONObject pair = new JSONObject(temp);
                gameId = Integer.parseInt(pair.getString("gameId"));
            }

        } catch (Exception e) {
            Log.e("http", "error");
            gameId = -1; // za svaki slucaj !
        }
        return gameId;
    }

    public static String addMines(int gameId, List<Mine> mines)
    {
        String retStr = "Error";

        try {
            URL url = new URL(Constants.URL + "/api/addMines");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            JSONObject body = new JSONObject();
            body.put("gameId", gameId);


            JSONArray minesJSONrray = new JSONArray();

            for (Iterator<Mine> mineIterator = mines.iterator(); mineIterator.hasNext();)
            {
                Mine mine = mineIterator.next();
                JSONObject jMine = new JSONObject();
                jMine.put("blastRadius", mine.getBlastRadius());
                jMine.put("Latitude", mine.getLocation().latitude);
                jMine.put("Longitude", mine.getLocation().longitude);
                minesJSONrray.put(jMine);

            }

            body.put("minesArray", minesJSONrray);



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
            }

            Log.e("http", retStr);

        } catch (Exception e) {
            Log.e("http", "error");
        }
        return retStr;
    }


    public static Game getGame(int gameId)
    {
        Game retGame = null;

        try {
            URL url = new URL(Constants.URL + "/api/getGame");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            JSONObject body = new JSONObject();

            body.put("gameId", gameId);


            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();

            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);

            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String str = inputStreamToString(conn.getInputStream());
                JSONArray jsonArray = new JSONArray(str);
                JSONObject object = (JSONObject) jsonArray.get(0);

                if(object!=null){
                    retGame = new Game();

                    retGame.setCreatorUsername(object.getString("CreatorUsername"));
                    retGame.setId(Integer.parseInt(object.getString("GameId")));

                    JSONArray mines = new JSONArray(object.getString("Mines"));

                    for (int i=0; i<mines.length(); i++)
                    {
                        Mine mine = new Mine();
                        JSONObject mn = mines.getJSONObject(i);
                        mine.setBlastRadius(mn.getDouble("blastRadius"));
                        mine.setLocation(new LatLng(mn.getDouble("Latitude"),mn.getDouble("Longitude")));

                        retGame.addMine(mine);
                    }




//                    retGame.setCreatorUsername(jsonArray.get()getString("CreatorUsername"));
//                    retGame.setId(gameId);
//
//                    JSONObject minesObject = jsonArray.getJSONObject("Mines");
//                    //JSONArray mines = minesObject.getJSONArray("")

                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }

        return retGame;
    }

    public static String deleteGameAndUpdateScore(int gameId, String winner, String loser, float winnerAward, float loserPenalty) { // ceo Game objekat u slucaju da joj dodamo jos neki prop. kasnije

        String retStr= "Error";

        try {
            URL url = new URL(Constants.URL + "/api/deleteGameAndUpdateScore");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);



            JSONObject body = new JSONObject();

            body.put("gameId", gameId);
            body.put("winner", winner);
            body.put("loser", loser);
            body.put("pointsWon", winnerAward);
            body.put("penaltyPoints", loserPenalty);

            Uri.Builder builder = new Uri.Builder().appendQueryParameter("action", body.toString());
            String query = builder.build().getEncodedQuery();



            OutputStream os = conn.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(query);
            bw.flush();
            bw.close();
            os.close();
            int responseCode = conn.getResponseCode();


            if (responseCode == HttpURLConnection.HTTP_OK) {
                retStr = inputStreamToString(conn.getInputStream());

            }

        } catch (Exception e) {
            Log.e("http", "error");

        }
        return retStr;
    }


    public static ArrayList<String> getScoreboard() {
        ArrayList<String> scores = new ArrayList<String>();
        try {
            URL url = new URL(Constants.URL + "/api/getScoreboard");
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
                    JSONObject obj  = jsonArray.getJSONObject(i);

                    String username =  obj.getString("Username");
                    double points  = obj.getDouble("Points");

                    scores.add(username + "\t\t\t\t" + points);
                }
            } else
                Log.e("HTTPCOde_Error", String.valueOf(responseCode));


        } catch (Exception e) {
            e.printStackTrace();

        }
        return scores;
    }


}
