package com.expelabs.karaoke.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.expelabs.karaoke.ImportTask;
import com.expelabs.karaoke.R;
import com.expelabs.karaoke.data.Callback;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 15:29
 * To change this template use File | Settings | File Templates.
 */
public class SplashActivity extends Activity {
    private static final String PREFERENCES_NAME = "karaoke_app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

    }

    private void importData(Callback callback) {
        new ImportTask(this,callback).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkLoaded()){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, CatalogActivity.class));
                    finish();
                }
            },1000);

        }else{
            findViewById(R.id.loading).setVisibility(View.VISIBLE);
            importData(new Callback() {
                @Override
                public void onSuccess() {
                    findViewById(R.id.loading).setVisibility(View.GONE);
                    getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit().putBoolean("loaded", true).commit();
                    startActivity(new Intent(SplashActivity.this, CatalogActivity.class));
                    finish();
                }

                @Override
                public void onFailure() {
                    findViewById(R.id.loading).setVisibility(View.GONE);
                    Toast.makeText(SplashActivity.this, "Sorry, can't import data", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean checkLoaded(){
        return getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean("loaded",false);
    }
}
