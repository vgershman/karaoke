package com.expelabs.karaoke.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.expelabs.karaoke.app.KaraokeApp;



import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 05.04.13
 * Time: 23:47
 * To change this template use File | Settings | File Templates.
 */
public class DataBaseHelper extends SQLiteOpenHelper {


   private DataBaseHelper(){
       super(KaraokeApp.getContext(), "karaoke_db", null, 1);
   }

    public static DataBaseHelper getInstance(){
         return SingletonHolder.instance;
    }

    private static class SingletonHolder{
        static DataBaseHelper instance = new DataBaseHelper();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
       createTable(TrackDao.TABLENAME, TrackDao.fields, sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        ;
    }

    private void createTable(String tableName, List<String>fields, SQLiteDatabase db){
        StringBuilder builder=new StringBuilder();
        builder.append("create table ");
        builder.append(tableName);
        builder.append("(id integer primary key autoincrement,");
        for(String field:fields){
            builder.append(field);
            builder.append(",");
        }
        builder.deleteCharAt(builder.length()-1);
        builder.append(");");
        db.execSQL(builder.toString());
    }
}
