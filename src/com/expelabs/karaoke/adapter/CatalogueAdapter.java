package com.expelabs.karaoke.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.expelabs.karaoke.R;
import com.expelabs.karaoke.data.TrackDao;
import com.expelabs.karaoke.data.TrackEntry;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 23:23
 * To change this template use File | Settings | File Templates.
 */
public class CatalogueAdapter extends BaseAdapter {

    private Context context;
    private List<TrackEntry> trackEntries = new ArrayList<TrackEntry>();

    public CatalogueAdapter(Context context) {
        this.context = context;
        trackEntries.clear();
    }

    public void addTracks(boolean clear, List<TrackEntry> entries) {
        if (clear) {
            trackEntries.clear();
        }
        trackEntries.addAll(entries);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trackEntries.size();
    }

    @Override
    public TrackEntry getItem(int i) {
        return trackEntries.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View row = view;
        ContentHolder holder;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.item, viewGroup, false);
            holder = new ContentHolder();
            holder.itemAuthor = (TextView) row.findViewById(R.id.itemAuthor);
            holder.itemName = (TextView) row.findViewById(R.id.itemName);
            holder.itemMidi = (TextView) row.findViewById(R.id.itemMidi);
            holder.itemMp3 = (TextView) row.findViewById(R.id.itemMP3);
            row.setTag(holder);
        } else {
            holder = (ContentHolder) row.getTag();
        }
        holder.itemAuthor.setText(trackEntries.get(i).getAuthor());
        holder.itemName.setText(trackEntries.get(i).getName());
        holder.itemMidi.setText(trackEntries.get(i).getMidi());
        holder.itemMp3.setText(trackEntries.get(i).getMp3());

        return row;
    }

    static class ContentHolder {
        TextView itemAuthor;
        TextView itemName;
        TextView itemMidi;
        TextView itemMp3;
    }
}
