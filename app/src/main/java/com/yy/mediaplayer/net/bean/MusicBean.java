package com.yy.mediaplayer.net.bean;

public class MusicBean {
    private String title;
    private String album;
    private String artist;
    private String genre;
    private String source;
    private String image;
    private int trackNumber;
    private int totalTrackCount;
    private long duration;
    private String site;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    public void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public int getTotalTrackCount() {
        return totalTrackCount;
    }

    public void setTotalTrackCount(int totalTrackCount) {
        this.totalTrackCount = totalTrackCount;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "MusicBean{" +
                "title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", artist='" + artist + '\'' +
                ", genre='" + genre + '\'' +
                ", source='" + source + '\'' +
                ", image='" + image + '\'' +
                ", trackNumber=" + trackNumber +
                ", totalTrackCount=" + totalTrackCount +
                ", duration=" + duration +
                ", site='" + site + '\'' +
                '}';
    }
}
