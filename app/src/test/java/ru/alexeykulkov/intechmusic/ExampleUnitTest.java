package ru.alexeykulkov.intechmusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.alexeykulkov.intechmusic.objects.Song;

import static org.junit.Assert.*;

public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        StringBuilder builder = new StringBuilder();
        String line;
        URL url;
        HttpURLConnection connection;
        BufferedReader in=null;
        try {
            url = new URL("https://itunes.apple.com/search?term=Boulevard+of+broken+dreams");
            connection = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            System.out.println(in.toString());
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }
        }
        catch (IOException e) {
            System.out.println("Exception!");
        }
        finally {
            if (in!=null) {
                in.close();
            }
        }
        System.out.println(builder);
        tryToParseJSONResponse(builder.toString());

    }


    private boolean tryToParseJSONResponse(String jsonResponse) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonResponse);
        } catch (JSONException e) {
            return false;
        }
        try {
            Integer resultCount = jsonObject.getInt("resultCount");
            if (resultCount>0)
            {
                JSONArray resultArray = jsonObject.getJSONArray("results");
                String currentSongName;
                String currentSongArtist;
                Song currentSong;
                Bitmap currentImage;
                for (int i=0;i<resultArray.length();i++) {
                    currentSongName = resultArray.getJSONObject(i).getString("trackName");
                    currentSongArtist = resultArray.getJSONObject(i).getString("artistName");
                    currentImage = downloadPicture(resultArray.getJSONObject(i).getString("artworkUrl100"));
                }
            }
        } catch (JSONException e) {

        }
        return true;
    }

    private Bitmap downloadPicture(String urlToPicture) {

        URL url;
        HttpURLConnection connection;
        Bitmap bitmap = null;
        InputStream inputStream=null;
        try {
            url = new URL(urlToPicture);
            connection = (HttpURLConnection) url.openConnection();
            inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            bitmap=null;
        }
        finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                bitmap=null;
            }
        }
        return bitmap;
    }
}