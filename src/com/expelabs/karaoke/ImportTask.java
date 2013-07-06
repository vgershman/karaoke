package com.expelabs.karaoke;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;
import com.expelabs.karaoke.data.Callback;
import com.expelabs.karaoke.data.TrackDao;
import com.expelabs.karaoke.data.TrackEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: vgershman
 * Date: 06.01.13
 * Time: 0:15
 * To change this template use File | Settings | File Templates.
 */
public class ImportTask extends AsyncTask {

    boolean fail = false;
    Callback callback;
    Context context;

    public ImportTask(Context context, Callback dbLoadCallback) {
        this.callback = dbLoadCallback;
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        try {
            TrackDao.clear();
            TrackDao.open();
            CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(context.getAssets().open("ru.csv"))));
            String[] nextLine;
            int i = 0;
            while ((nextLine = reader.readNext()) != null) {
                TrackEntry trackEntry = new TrackEntry();
                trackEntry.setAuthor(nextLine[0]);
                trackEntry.setName(nextLine[1]);
                trackEntry.setMidi(nextLine[2].replace("?", "d"));
                trackEntry.setMp3(nextLine[3].replace("?", "d"));
                trackEntry.setLocale("ru");
                TrackDao.insert(trackEntry);
                i++;
                Log.i("karaoke", i + "");
            }
            reader.close();
            CSVReader reader1 = new CSVReader(new BufferedReader(new InputStreamReader(context.getAssets().open("en.csv"))));
            String[] nextLine1;
            int i1 = 0;
            while ((nextLine1 = reader1.readNext()) != null) {
                TrackEntry trackEntry = new TrackEntry();
                trackEntry.setAuthor(nextLine1[0]);
                trackEntry.setName(nextLine1[1]);
                trackEntry.setMidi(nextLine1[2].replace("?", "d"));
                trackEntry.setMp3(nextLine1[3].replace("?", "d"));
                trackEntry.setLocale("en");
                TrackDao.insert(trackEntry);
                i++;
                Log.i("karaoke", i1 + "");
            }
            TrackDao.close();
            reader1.close();

        } catch (Exception ex) {
            fail = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (!fail) {
            callback.onSuccess();
        } else {
            callback.onFailure();
        }
    }
}
