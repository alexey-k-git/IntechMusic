package ru.alexeykulkov.intechmusic.adapters;

import android.support.v7.widget.CardView;
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

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private List<Song> songs;
    private static Listener listener;


    public CardViewAdapter(List<Song> songs) {
        this.songs = songs;
    }

    public static void setListener(Listener activity)
    {
        listener=activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_card,parent,false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        final Song song = songs.get(position);
        ImageView imageView = (ImageView) cardView.findViewById(R.id.songImage);
        TextView artistTextView = (TextView)  cardView.findViewById(R.id.songArtist);
        TextView nameTextView = (TextView) cardView.findViewById(R.id.songName);
        artistTextView.setText(song.getArtist());
        nameTextView.setText(song.getName());
        Picasso.with(holder.itemView.getContext())
                .load(song.getArtworkUrl30())
                .into(imageView);
        cardView.setOnClickListener(new View.OnClickListener() {
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

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        private CardView cardView;

        public ViewHolder(CardView itemView) {
            super(itemView);
            cardView = itemView;
        }
    }

}
