package com.example.phuocdat.mp3;



public class Song {

    private int songID;
    private String songName;
    private String songArtist;
    private int songLong;
    private boolean isCurrentSong;

    public Song(int songID, String songName, String songArtist, boolean isCurrentSong, int songLong) {
        this.songID = songID;
        this.songName = songName;
        this.songArtist = songArtist;
        this.isCurrentSong = isCurrentSong;
        this.songLong = songLong;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public void setSongArtist(String songArtist) {
        this.songArtist = songArtist;
    }

    public boolean isCurrentSong() {
        return isCurrentSong;
    }

    public void setCurrentSong(boolean currentSong) {
        isCurrentSong = currentSong;
    }

    public int getSongLong() {
        return songLong;
    }

    public void setSongLong(int songLong) {
        this.songLong = songLong;
    }
}
