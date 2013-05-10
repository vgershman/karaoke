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
    private boolean sortAuthor;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catalogue);
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        menu.setBehindOffset(width - 310);
        menu.setBehindScrollScale(1);
        menu.setFadeEnabled(false);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.slidingmenu);
        menu.findViewById(R.id.natives).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curLoc = "ru";
                doSearch(true, query);
            }
        });
        menu.findViewById(R.id.eng).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curLoc = "en";
                doSearch(true, query);
            }
        });
        menu.findViewById(R.id.all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curLoc = "";
                doSearch(true, query);
            }
        });
        adapter = new CatalogueAdapter(this);
        artistHeader = (TextView) findViewById(R.id.headerAuthor);
        artistHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(Color.parseColor("#17AAFA"));
                //((TextView) view).setTextColor(Color.BLACK);
                //nameHeader.setBackgroundColor(Color.BLACK);
                ///nameHeader.setTextColor(Color.parseColor("#17AAFA"));
                if (sortAuthor) {
                    sortAsc = !sortAsc;
                } else {
                    sortAuthor = true;
                    sortAsc = true;
                }
                sortChanged();
            }
        });
        nameHeader = (TextView) findViewById(R.id.headerName);
        nameHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //view.setBackgroundColor(Color.parseColor("#17AAFA"));
                //((TextView) view).setTextColor(Color.BLACK);
                //artistHeader.setBackgroundColor(Color.BLACK);
                //artistHeader.setTextColor(Color.parseColor("#17AAFA"));
                if (!sortAuthor) {
                    sortAsc = !sortAsc;
                } else {
                    sortAuthor = false;
                    sortAsc = true;
                }
                sortChanged();
            }
        });
        list = (ListView) findViewById(R.id.list);
        //final ImageView openMenu = (ImageView) findViewById(R.id.open_menu);
        //openMenu.setOnClickListener(new View.OnClickListener() {
         //   @Override
         //   public void onClick(View view) {
         //       menu.toggle();
         //   }
        //});
        list.setAdapter(adapter);
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
        search = (EditText) findViewById(R.id.search);
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
