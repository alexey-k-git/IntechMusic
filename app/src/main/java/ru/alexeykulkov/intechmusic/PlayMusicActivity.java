package ru.alexeykulkov.intechmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.alexeykulkov.intechmusic.objects.Song;

public class PlayMusicActivity extends Activity {

    private Song song;
    private TextView nameTextView;
    private TextView artistTextView;
    private ImageView imageView;
    private Button startBtn;
    private Button pauseBtn;
    private boolean playFlag;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_music_activity);
        Intent intent = getIntent();
        song =  (Song) intent.getSerializableExtra("Song");
        nameTextView = (TextView) findViewById(R.id.songName);
        artistTextView = (TextView) findViewById(R.id.songArtist);
        imageView = (ImageView) findViewById(R.id.songImage);

        nameTextView.setText(song.getName());
        artistTextView.setText(song.getArtist());

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setProgress(0);
                playFlag = true;
            }
        });
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playFlag = !playFlag;
            }
        });
        if (savedInstanceState!=null)
        {
            seekBar.setProgress(savedInstanceState.getInt("progress"));
            playFlag = savedInstanceState.getBoolean("playFlag");
        }
        Picasso.with(this)
                .load(song.getArtworkUrl100())
                .into(imageView);
        playMusic();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("progress", seekBar.getProgress());
        outState.putBoolean("playFlag", playFlag);
    }

    // метод запускающий вечный Handler, для имитации проигрыша мелодии
    private void playMusic() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (playFlag) {
                    seekBar.setProgress(seekBar.getProgress() + 1);
                    if (seekBar.getProgress() == seekBar.getMax()) {
                        playFlag = false;
                        seekBar.setProgress(0);
                    }
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
