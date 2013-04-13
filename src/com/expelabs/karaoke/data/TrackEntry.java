package com.expelabs.karaoke.data;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 22:08
 * To change this template use File | Settings | File Templates.
 */
public class TrackEntry {

    private Integer id;
    private String author="";
    private String name="";
    private String midi="";
    private String mp3="";
    private boolean favourite;

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMidi() {
        return midi;
    }

    public void setMidi(String midi) {
        this.midi = midi;
    }

    public String getMp3() {
        return mp3;
    }

    public void setMp3(String mp3) {
        this.mp3 = mp3;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
