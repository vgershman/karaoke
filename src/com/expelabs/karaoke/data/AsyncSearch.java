package com.expelabs.karaoke.data;

import android.os.AsyncTask;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 13.04.13
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class AsyncSearch extends AsyncTask<Object, Void,Void> {

    private Integer pageCount;
    private String query;
    private String sort;
    private SearchCallback callback;
    private List<TrackEntry> result;

    public AsyncSearch(Integer pageCount, String query, String sort, SearchCallback callback) {
        this.pageCount = pageCount;
        this.query = query;
        this.sort = sort;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Object... objects) {
        result = TrackDao.getTrackEntries(pageCount, query, sort);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(result!=null){
            callback.onFound(result);
        }
    }
}