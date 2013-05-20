package com.expelabs.karaoke.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.expelabs.karaoke.R;
import com.expelabs.karaoke.adapter.CatalogueAdapter;
import com.expelabs.karaoke.data.SearchCallback;
import com.expelabs.karaoke.data.TrackDao;
import com.expelabs.karaoke.data.TrackEntry;
import com.slidingmenu.lib.SlidingMenu;

import java.util.List;

public class CatalogActivity extends Activity {

    private ListView list;
    private EditText search;
    private boolean sortAuthor = true;
    private boolean sortAsc;
    private String current_sort = "";
    private String query = "";
    private TextView artistHeader;
    private TextView nameHeader;
    private boolean fav;
    private CatalogueAdapter adapter;
    private int currentPage = 1;
    private boolean searching;
    private SlidingMenu menu;
    private String curLoc = "";
    private boolean showFavs;

    public void initMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        menu.setBehindOffset(width - 310);
        menu.setBehindScrollScale(1);
        menu.setFadeEnabled(false);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenu);
    }

    public void initControls() {
        final TextView natives = (TextView) menu.findViewById(R.id.natives);
        final TextView eng = (TextView) menu.findViewById(R.id.eng);
        final TextView all = (TextView) menu.findViewById(R.id.all);
        final TextView favs = (TextView) menu.findViewById(R.id.favs);
        final TextView deleteQuery = (TextView) findViewById(R.id.delete_query);
        artistHeader = (TextView) findViewById(R.id.headerAuthor);
        nameHeader = (TextView) findViewById(R.id.headerName);
        list = (ListView) findViewById(R.id.list);
        search = (EditText) findViewById(R.id.search);
        natives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFavs){
                    showFavs=false;
                }
                curLoc = "ru";
                natives.setTextColor(Color.parseColor("#f9e8b7"));
                eng.setTextColor(Color.parseColor("#eaeaea"));
                all.setTextColor(Color.parseColor("#eaeaea"));
                favs.setTextColor(Color.parseColor("#eaeaea"));
                doSearch(true, query);
            }
        });
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFavs){
                    showFavs=false;
                }
                curLoc = "en";
                eng.setTextColor(Color.parseColor("#f9e8b7"));
                natives.setTextColor(Color.parseColor("#eaeaea"));
                all.setTextColor(Color.parseColor("#eaeaea"));
                favs.setTextColor(Color.parseColor("#eaeaea"));
                doSearch(true, query);
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFavs){
                    showFavs=false;
                }
                curLoc = "";
                all.setTextColor(Color.parseColor("#f9e8b7"));
                natives.setTextColor(Color.parseColor("#eaeaea"));
                eng.setTextColor(Color.parseColor("#eaeaea"));
                favs.setTextColor(Color.parseColor("#eaeaea"));
                doSearch(true, query);
            }
        });
        favs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curLoc = "";
                favs.setTextColor(Color.parseColor("#f9e8b7"));
                natives.setTextColor(Color.parseColor("#eaeaea"));
                eng.setTextColor(Color.parseColor("#eaeaea"));
                all.setTextColor(Color.parseColor("#eaeaea"));
                adapter.addTracks(true, TrackDao.getFavouriteTracks());
                showFavs = true;
            }
        });
        deleteQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
                query = "";
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);
        initMenu();
        initControls();
        setListeners();
        adapter = new CatalogueAdapter(this);
        list.setAdapter(adapter);
    }

    private void setListeners() {
        artistHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFavs){return;}
                artistHeader.setTextColor(Color.parseColor("#f9e8b7"));
                nameHeader.setTextColor(Color.parseColor("#eaeaea"));
                if (sortAuthor) {
                    sortAsc = !sortAsc;
                } else {
                    sortAuthor = true;
                    sortAsc = true;
                }
                sortChanged();
            }
        });
        nameHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(showFavs){return;}
                nameHeader.setTextColor(Color.parseColor("#f9e8b7"));
                artistHeader.setTextColor(Color.parseColor("#eaeaea"));
                if (!sortAuthor) {
                    sortAsc = !sortAsc;
                } else {
                    sortAuthor = false;
                    sortAsc = true;
                }
                sortChanged();
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final TrackEntry trackEntry = adapter.getItem((int) l);
                final boolean isFavourite = TrackDao.isFavourite(trackEntry);
                AlertDialog.Builder builder = new AlertDialog.Builder(CatalogActivity.this);
                builder.setMessage(isFavourite ? "Удалить из избранных" : "Добавить в избранные?");
                builder.setPositiveButton(isFavourite ? "Удалить" : "Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        TrackDao.setFavourite(trackEntry, !isFavourite);
                        dialogInterface.dismiss();
                        doSearch(true, query);
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
                return false;
            }
        });
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (i + i2 >= i3 && !searching) {
                    currentPage++;
                    doSearch(false, query);
                }
            }
        });
        search.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (view.getRight() - motionEvent.getX() <= 30 && motionEvent.getAction() == (MotionEvent.ACTION_DOWN)) {
                    search.setText("");
                }
                return false;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                query = charSequence.toString();
                currentPage = 1;
                doSearch(true, charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    private void doSearch(final boolean clear, CharSequence query) {
        if(showFavs){return;}
        searching = true;
        TrackDao.asyncSearch(currentPage, "where (author like '%" + query + "%' or name like '%" + query + "%' and loc like '%" + curLoc + "%') collate nocase ", current_sort, new SearchCallback() {
            @Override
            public void onFound(List<TrackEntry> result) {
                adapter.addTracks(clear, result);
                searching = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doSearch(false, query);
    }

    private void sortChanged() {
        if (sortAuthor) {
            if (sortAsc) {
                current_sort = " order by author asc";
            } else {
                current_sort = " order by author desc";
            }
        } else {
            if (sortAsc) {
                current_sort = " order by name asc";
            } else {
                current_sort = " order by name desc";
            }
        }
        currentPage = 1;
        doSearch(true, query);
    }
}
