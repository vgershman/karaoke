package com.expelabs.karaoke.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 22:25
 * To change this template use File | Settings | File Templates.
 */
public class TrackDao {

    public static final String TABLENAME = "tracks";
    public static List<String> fields = new ArrayList<String>();
    private static DatabaseUtils.InsertHelper ih;
    static int authorColumn;
    static int nameColumn;
    static int midiColumn;
    static int mp3Column;
    static int favColumn;
    private static final int PAGE_SIZE = 50;
    private static AsyncSearch current;

    static {
        fields.add("author text");
        fields.add("name text");
        fields.add("midi text");
        fields.add("mp3 text");
        fields.add("fav integer");
    }

    private static SQLiteDatabase db;

    public static void insert(TrackEntry trackEntry) {
        ih.prepareForInsert();
        ih.bind(authorColumn, trackEntry.getAuthor());
        ih.bind(nameColumn, trackEntry.getName());
        ih.bind(midiColumn, trackEntry.getMidi());
        ih.bind(mp3Column, trackEntry.getMp3());
        ih.bind(favColumn, 0);
        ih.execute();
    }

    public static void open() {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        ih = new DatabaseUtils.InsertHelper(db, TABLENAME);
        authorColumn = ih.getColumnIndex("author");
        nameColumn = ih.getColumnIndex("name");
        midiColumn = ih.getColumnIndex("midi");
        mp3Column = ih.getColumnIndex("mp3");
        favColumn = ih.getColumnIndex("fav");
        db.execSQL("PRAGMA synchronous=OFF");
        db.setLockingEnabled(false);
        db.beginTransaction();
    }

    public static void close() {
        db.setTransactionSuccessful();
        db.endTransaction();
        db.setLockingEnabled(true);
        db.execSQL("PRAGMA synchronous=NORMAL");
        ih.close();
        db.close();
    }

    public static void updateFavourite(TrackEntry trackEntry) {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLENAME, "id = " + trackEntry.getId(), null);
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", trackEntry.getId());
        contentValues.put("author", trackEntry.getAuthor());
        contentValues.put("name", trackEntry.getName());
        contentValues.put("midi", trackEntry.getMidi());
        contentValues.put("mp3", trackEntry.getMp3());
        contentValues.put("fav", trackEntry.isFavourite() ? 1 : 0);
        db.insert(TABLENAME, null, contentValues);
        db.close();
    }

    public static List<TrackEntry> getTrackEntries(Integer pageCount, String query, String sort) {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        List<TrackEntry> trackEntries = new ArrayList<TrackEntry>();
        Cursor cursor = db.rawQuery("select * from " + TABLENAME + " " + query + sort + " limit " + pageCount * PAGE_SIZE + " offset "+ (pageCount-1)*PAGE_SIZE +";", null);
        while (cursor.moveToNext()) {
            TrackEntry trackEntry = new TrackEntry();
            trackEntry.setFavourite(cursor.getInt(cursor.getColumnIndex("fav")) == 1);
            trackEntry.setId(cursor.getInt(cursor.getColumnIndex("id")));
            trackEntry.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            trackEntry.setName(cursor.getString(cursor.getColumnIndex("name")));
            trackEntry.setMidi(cursor.getString(cursor.getColumnIndex("midi")));
            trackEntry.setMp3(cursor.getString(cursor.getColumnIndex("mp3")));
            trackEntries.add(trackEntry);
        }
        return trackEntries;
    }

    public static void clear() {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        db.delete(TABLENAME, null, null);
        db.close();
    }

    public static void asyncSearch(Integer pageCount, String query, String sort, SearchCallback callback) {
        if(current!=null){
            current.cancel(true);
        }
        current = new AsyncSearch(pageCount, query, sort, callback);
        current.execute();
    }
}









