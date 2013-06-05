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
                        SharedPreferences sharedPreferences = context.getSharedPreferences(KaraokeApp.PREFERENCES_NAME, Context.MODE_PRIVATE);
                        if (inventory.hasPurchase(KaraokeApp.SKU_FAVS)) {
                            sharedPreferences.edit().putBoolean(KaraokeApp.SKU_FAVS, true).commit();
                        }else{
                            sharedPreferences.edit().putBoolean(KaraokeApp.SKU_FAVS, false).commit();
                        }
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
                        context.getSharedPreferences(KaraokeApp.PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putBoolean(purchase.getSku(), true).commit();
                        purchaseDelegate.onSuccess();
                    }
                }
            };
            try {
                mHelper.launchPurchaseFlow(activity, sku, RC_BUY, mPurchaseFinishedListener, "");
            } catch (IllegalStateException e) {
                Log.e(KaraokeApp.class.getName(), e.getMessage());
            }
        } else {
            Toast toast = Toast.makeText(activity, "Oh no! Something went wrong, can't make purchase.", 1000);
            toast.setGravity(Gravity.CENTER, Gravity.CENTER, Gravity.CENTER);
            toast.show();
        }
    }

    public void dispose() {
        mHelper.dispose();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        mHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
