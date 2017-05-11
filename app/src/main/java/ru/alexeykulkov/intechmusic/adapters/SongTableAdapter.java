package ru.alexeykulkov.intechmusic.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.alexeykulkov.intechmusic.R;
import ru.alexeykulkov.intechmusic.interfaces.Listener;
import ru.alexeykulkov.intechmusic.objects.Song;

public class SongTableAdapter extends RecyclerView.Adapter<SongTableAdapter.SongHolder> {


    private List<Song> songs;
    private static Listener listener;

    public SongTableAdapter(List<Song> songs) {
        this.songs = songs;
    }

    public static void setListener(Listener activity)
    {
        listener = activity;
    }

    @Override
    public SongTableAdapter.SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.row_element, parent, false);
        return new SongTableAdapter.SongHolder(view);
    }

    @Override
    public void onBindViewHolder(SongTableAdapter.SongHolder songHolder, int position) {
        final Song song = songs.get(position);
        songHolder.bindSong(song);
        songHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.startPlaySongActivity(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public static  class SongHolder extends RecyclerView.ViewHolder  {

        private ImageView imageView;
        private TextView nameTextView;
        private TextView artistTextView;

        public SongHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.songImage);
            artistTextView = (TextView)  itemView.findViewById(R.id.songArtist);
            nameTextView = (TextView) itemView.findViewById(R.id.songName);
        }

        public void bindSong(Song song) {
            artistTextView.setText(song.getArtist());
            nameTextView.setText(song.getName());
            if (imageView!=null) {
                Picasso.with(this.itemView.getContext())
                        .load(song.getArtworkUrl30())
                        .into(imageView);
            }
        }

    }

}