package com.expelabs.karaoke.app;

import android.app.Application;
import android.content.Context;
import com.expelabs.karaoke.util.BillingUtils;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 06.04.13
 * Time: 22:59
 * To change this template use File | Settings | File Templates.
 */
public class KaraokeApp extends Application {

    public static final String GOOGLE_PLAY_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiozmeWC1KxhxxvehLSNNan5HuZs1TXXAAQ6+cQio61p3lRIwYbh6uxya1WPYN44ByvA0UgrpWLRUzBWEyNtcTXgIRjhF3WOs5dy6KAbE3vcGt33QCyfuVo4rG+Cid7wTU2R2DdRzA9Q/bUjvOblILwuMOi0hayLbHRT+y6Prk2M0teOU6zc83qIwAEvA7PawpYibAw80xZmsAiRSwSRJa3gIl8+u7POSp5VYZAipcepeVcgGD3fxA/opDldDlQvlWY2EjCiM5wefNoZZEZd94+T/3ND3u7MNdbmRvzWMQArrQi9kYOsyv3xJCNHeWNTDag4IVKyFPXIu0nR0dUaQ0QIDAQAB";
    public static final String SKU_FAVS = "com.expelabs.karaoke.favs";
    //public static final String SKU_FAVS = "android.test.purchased";
    private static Context context;
    public static final String PREFERENCES_NAME = "karaoke";
    private static BillingUtils billingUtils;
    public static Context getContext(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        billingUtils = new BillingUtils(context);
    }

    public static BillingUtils getBillingUtils() {
        return billingUtils;
    }

    public static boolean isPro(){
        return context.getSharedPreferences(PREFERENCES_NAME,MODE_PRIVATE).getBoolean(SKU_FAVS,false);
    }
}
