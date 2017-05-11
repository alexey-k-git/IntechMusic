package ru.alexeykulkov.intechmusic.objects;

import java.io.Serializable;

//Объект песни
public class Song implements Serializable {
    private String artist;
    private String name;
    private String artworkUrl100;
    private String artworkUrl30;

    public Song(String artist, String name, String artworkUrl30, String artworkUrl100) {
        this.artist = artist;
        this.name = name;
        this.artworkUrl30 = artworkUrl30;
        this.artworkUrl100 = artworkUrl100;
    }

    public String getArtworkUrl100() {
        return artworkUrl100;
    }

    public String getArtworkUrl30() {
        return artworkUrl30;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

}
