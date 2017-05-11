package ru.alexeykulkov.intechmusic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ru.alexeykulkov.intechmusic.adapters.CardViewAdapter;
import ru.alexeykulkov.intechmusic.adapters.SongTableAdapter;
import ru.alexeykulkov.intechmusic.interfaces.Listener;
import ru.alexeykulkov.intechmusic.objects.Song;

public class MainActivity extends Activity implements Listener {

    private QueryTask queryTask;

    private EditText keywordEditText;
    private LinearLayout headerProgress;

    private List<Song> songs;

    private RecyclerView songsRecyclerCardView;
    private RecyclerView songsRecyclerTableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("listTag");
        tabSpec.setContent(R.id.listTab);
        tabSpec.setIndicator("Список");
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tableTag");
        tabSpec.setContent(R.id.tableTab);
        tabSpec.setIndicator("Таблица");
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTab(0);
        keywordEditText = (EditText) findViewById(R.id.keyWordEditText);
        keywordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (keywordEditText.getText().toString().length()>4)
                {
                    if (!isOnline()) {
                        Toast.makeText(MainActivity.this, "Отсутсвует соединение с интернетом.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    findSongs();
                }
            }
        });

        SongTableAdapter.setListener(this);
        CardViewAdapter.setListener(this);

        songsRecyclerCardView = (RecyclerView) findViewById(R.id.recyclerView);
        songsRecyclerCardView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


        songsRecyclerTableView = (RecyclerView) findViewById(R.id.recyclerTableView);
        songsRecyclerTableView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        headerProgress = (LinearLayout) findViewById(R.id.headerProgress);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
        outState.putParcelableArrayList("songs", (ArrayList<Song>) songs);
        outState.putBoolean("currentResultViewTableFlag", currentResultViewTableFlag);
        outState.putString("currentKeyWord", keywordEditText.getText().toString());
        */
    }

    //проверка доступа к интернету
    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void findSongs() {
        String keyword = keywordEditText.getText().toString();
        if (queryTask!=null && queryTask.getStatus()==AsyncTask.Status.RUNNING) {
            queryTask.cancel(false);
        }
        queryTask = new QueryTask();
        queryTask.execute(keyword);
    }


    //открытие активности плеера
    public void startPlaySongActivity(Song song) {
        Intent intent = new Intent(MainActivity.this, PlayMusicActivity.class);
        intent.putExtra("Song", song);
        startActivity(intent);
    }


    //AsyncTask для загрузки предварительного списка данных с ITunes
    class QueryTask extends AsyncTask<String, Integer, List<Song>> {

        boolean errorFlag;

        @Override
        protected void onPreExecute() {
            headerProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Song> doInBackground(String... strings) {
            List<Song> songs= new ArrayList<>();
            try {
                errorFlag = false;
                String resultOfQuery = makeRequestToITunes(strings[0]);
                if (isCancelled()) return songs;
                songs = parseJSONResponse(resultOfQuery);
            } catch (Exception e) {
                errorFlag = true;
            }
            return songs;
        }

        @Override
        protected void onPostExecute(List<Song> result) {
            super.onPostExecute(result);
            if (errorFlag) {
                Toast.makeText(MainActivity.this, "Произошла ошибка во время запроса", Toast.LENGTH_SHORT).show();
            }
            songs = result;
            songsRecyclerCardView.setAdapter(new CardViewAdapter(songs));
            songsRecyclerTableView.setAdapter(new SongTableAdapter(songs));
            headerProgress.setVisibility(View.GONE);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            headerProgress.setVisibility(View.GONE);
        }

        //преобразование JSON объекта в виде строки в лист объектов Song
        private List<Song> parseJSONResponse(String jsonResponse) throws JSONException {
            List<Song> songs = new ArrayList<Song>();
            JSONObject jsonObject;
            jsonObject = new JSONObject(jsonResponse);
            Integer resultCount = jsonObject.getInt("resultCount");
            if (resultCount > 0) {
                JSONArray resultArray = jsonObject.getJSONArray("results");
                String currentSongName;
                String currentSongArtist;
                String artworkUrl30;
                String artworkUrl100;
                Song currentSong;
                for (int i = 0; i < resultArray.length(); i++) {
                    currentSongName = resultArray.getJSONObject(i).getString("trackName");
                    currentSongArtist = resultArray.getJSONObject(i).getString("artistName");
                    artworkUrl30 = resultArray.getJSONObject(i).getString("artworkUrl30");
                    artworkUrl100 = resultArray.getJSONObject(i).getString("artworkUrl100");
                    currentSong = new Song(currentSongArtist, currentSongName, artworkUrl30, artworkUrl100);
                    songs.add(currentSong);
                }
            }
            return songs;
        }

        //запрос к ITunes
        private String makeRequestToITunes(String text) {
            StringBuilder builder = new StringBuilder();
            String queryParam = text.trim().replaceAll("\\s+", "+");
            HttpsURLConnection connection = null;
            BufferedReader in = null;
            try {
                URL url = new URL("https://itunes.apple.com/search?term=" + queryParam);
                connection = (HttpsURLConnection) url.openConnection();
                if (connection.getResponseCode()==HttpsURLConnection.HTTP_OK) {
                    String line;
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = in.readLine()) != null) {
                        if (isCancelled()) return null;
                        builder.append(line);
                    }
                }
            } catch (IOException e) {
                return null;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        return null;
                    }
                }
            }
            return builder.toString();
        }
    }
}
