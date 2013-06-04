package com.expelabs.karaoke.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.billing.IabHelper;
import com.billing.IabResult;
import com.billing.Inventory;
import com.billing.Purchase;
import com.expelabs.karaoke.app.KaraokeApp;
import com.expelabs.tips.activity.AdditionalSettingsActivity;
import com.expelabs.tips.app.DailyTipsApp;
import com.expelabs.tips.delegate.PurchaseDelegate;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Вадим
 * Date: 22.05.13
 * Time: 14:16
 * To change this template use File | Settings | File Templates.
 */
public class BillingUtils {

    public static final int RC_BUY = 777;
    protected IabHelper mHelper;
    private boolean isBillingSetUp;
    private Context context;

    public BillingUtils(Context context) {
        this.context = context;
        mHelper = createIabHelper();
    }

    private IabHelper createIabHelper() {
        mHelper = new IabHelper(context, KaraokeApp.GOOGLE_PLAY_KEY);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d(KaraokeApp.class.toString(), "Problem setting up In-app Billing: " + result);
                    return;
                }
                isBillingSetUp = true;
                getInApps();
            }
        });
        return mHelper;
    }

    public void getInApps() {
        if (isBillingSetUp) {
            IabHelper.QueryInventoryFinishedListener mGotInventoryListener
                    = new IabHelper.QueryInventoryFinishedListener() {
                public void onQueryInventoryFinished(IabResult result,
                                                     Inventory inventory) {
                    if (!result.isFailure()) {
                        SharedPreferences sharedPreferences = context.getSharedPreferences(KaraokeApp.PREFERENCES_NAME,Context.MODE_PRIVATE);
                        boolean total = false;
                        boolean cooking = false;
                        boolean home = false;
                        boolean work = false;
                        boolean life = false;

                        if(inventory.hasPurchase(DailyTipsApp.SKU_TOTAL)){
                            total = true;

                        }
                        if(inventory.hasPurchase(DailyTipsApp.SKU_HOME)){
                            home = true;
                        }
                        if(inventory.hasPurchase(DailyTipsApp.SKU_COOKING)){
                            cooking = true;
                        }
                        if(inventory.hasPurchase(DailyTipsApp.SKU_LIFESTYLE)){
                            life = true;
                        }
                        if(inventory.hasPurchase(DailyTipsApp.SKU_WORK)){
                            work = true;
                        }
                        sharedPreferences.edit()
                                .putBoolean(DailyTipsApp.SKU_TOTAL,total)
                                .putBoolean(DailyTipsApp.SKU_COOKING,cooking)
                                .putBoolean(DailyTipsApp.SKU_HOME,home)
                                .putBoolean(DailyTipsApp.SKU_WORK,work)
                                .putBoolean(DailyTipsApp.SKU_LIFESTYLE,life)
                                .commit();
                    }
                }
            };
            mHelper.queryInventoryAsync(mGotInventoryListener);
        }
    }

    public void buy(String sku, Activity activity, final PurchaseDelegate purchaseDelegate) {
        if (isBillingSetUp) {
            IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                    = new IabHelper.OnIabPurchaseFinishedListener() {

                public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                    if (result.isFailure()) {
                        return;
                    } else {
                        context.getSharedPreferences(DailyTipsApp.PREFERENCES_NAME,Context.MODE_PRIVATE).edit().putBoolean(purchase.getSku(),true).commit();
                        purchaseDelegate.onSuccess();
                    }
                }
            };
            try {
                mHelper.launchPurchaseFlow(activity, sku, RC_BUY, mPurchaseFinishedListener, "");
            } catch (IllegalStateException e) {
                Log.e(DailyTipsApp.class.getName(), e.getMessage());
            }
        } else {
            Toast toast = Toast.makeText(activity, "Oh no! Something went wrong, can't make purchase.", 1000);
            toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
            toast.show();
        }
    }

    public void dispose(){
        mHelper.dispose();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        mHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
