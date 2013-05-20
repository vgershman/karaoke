package com.expelabs.karaoke.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

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
    public static final String FAVOURITE_TABLE = "favorite";
    public static  List<String> favoriteFields = new ArrayList<String>();
    public static List<String> fields = new ArrayList<String>();
    private static DatabaseUtils.InsertHelper ih;
    static int authorColumn;
    static int nameColumn;
    static int midiColumn;
    static int mp3Column;
    static int locColumn;
    static int favColumn;
    private static final int PAGE_SIZE = 100;
    private static AsyncSearch current;

    static {
        fields.add("author text");
        fields.add("name text");
        fields.add("midi text");
        fields.add("mp3 text");
        fields.add("loc text");
        favoriteFields.add("mp3 text");
        favoriteFields.add("midi text");
        favoriteFields.add("fav int");
    }

    private static SQLiteDatabase db;

    public static void insert(TrackEntry trackEntry) {
        ih.prepareForInsert();
        ih.bind(authorColumn, trackEntry.getAuthor());
        ih.bind(nameColumn, trackEntry.getName());
        ih.bind(midiColumn, trackEntry.getMidi());
        ih.bind(mp3Column, trackEntry.getMp3());
        ih.bind(locColumn, trackEntry.getLocale());
        ih.execute();
    }

    public static void open() {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        ih = new DatabaseUtils.InsertHelper(db, TABLENAME);
        authorColumn = ih.getColumnIndex("author");
        nameColumn = ih.getColumnIndex("name");
        midiColumn = ih.getColumnIndex("midi");
        mp3Column = ih.getColumnIndex("mp3");
        locColumn = ih.getColumnIndex("loc");
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

    public static void setFavourite(TrackEntry trackEntry, boolean favourite){
        db = DataBaseHelper.getInstance().getWritableDatabase();
        if(favourite){
            ContentValues contentValues = new ContentValues();
            contentValues.put("mp3",trackEntry.getMp3());
            contentValues.put("midi",trackEntry.getMidi());
            contentValues.put("fav", 1);
            db.insert(FAVOURITE_TABLE,null,contentValues);
        }else{
            db.delete(FAVOURITE_TABLE,"mp3 = ? and midi = ?", new String[]{trackEntry.getMp3(),trackEntry.getMidi()});
        }
    }


    public static List<TrackEntry> getTrackEntries(Integer pageCount, String query, String sort) {
        db = DataBaseHelper.getInstance().getWritableDatabase();
        List<TrackEntry> trackEntries = new ArrayList<TrackEntry>();
        Cursor cursor = db.rawQuery("select * from " + TABLENAME + " " + query + sort + " limit " + pageCount * PAGE_SIZE + " offset " + (pageCount - 1) * PAGE_SIZE + ";", null);
        while (cursor.moveToNext()) {
            TrackEntry trackEntry = new TrackEntry();
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

    public static boolean isFavourite(TrackEntry trackEntry){
        db = DataBaseHelper.getInstance().getWritableDatabase();
        Cursor cursor = db.query(FAVOURITE_TABLE, new String[]{"fav"},"mp3 = ? and midi = ?",new String[]{trackEntry.getMp3(),trackEntry.getMidi()},null,null,null);
        if(cursor.moveToFirst()){
            db.close();
            return cursor.getInt(cursor.getColumnIndex("fav")) == 1;
        }else{
            db.close();
            return false;
        }
    }

    public static List<TrackEntry> getFavouriteTracks(){
        db = DataBaseHelper.getInstance().getWritableDatabase();
        List<TrackEntry>preResult = new ArrayList<TrackEntry>();
        Cursor cursor = db.query(FAVOURITE_TABLE, new String[]{"mp3","midi"},"fav = 1",null,null,null,null);
        while(cursor.moveToNext()){
            TrackEntry trackEntry = new TrackEntry();
            trackEntry.setMidi(cursor.getString(cursor.getColumnIndex("midi")));
            trackEntry.setMp3(cursor.getString(cursor.getColumnIndex("mp3")));
            preResult.add(trackEntry);
        }
        cursor.close();

        for(TrackEntry trackEntry:preResult){
            db = DataBaseHelper.getInstance().getReadableDatabase();
            Cursor cursor1 = db.query(TABLENAME, new String[]{"author","name"}, "mp3 = ? and midi = ?", new String[]{trackEntry.getMp3(),trackEntry.getMidi()},null,null,null);
            if(cursor1.moveToFirst()){
                trackEntry.setAuthor(cursor1.getString(cursor1.getColumnIndex("author")));
                trackEntry.setName(cursor1.getString(cursor1.getColumnIndex("name")));
            }
            cursor1.close();
            db.close();
        }
        return preResult;
    }

    public static void asyncSearch(Integer pageCount, String query, String sort, SearchCallback callback) {
        if (current != null) {
            current.cancel(true);
        }
        current = new AsyncSearch(pageCount, query, sort, callback);
        current.execute();
    }
}









