package com.searcher.util;

import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import timber.log.Timber;


/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class LocationRequester {
    private LocationManager locationManager;
    private Context context;

    public LocationRequester(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
    }

    public void requestLocationUpdate(LocationCallback.Callback locationCallback) {
        if (!checkGpsProvider() || !isLocationEnabled()) {
            Timber.e("GPS Disabled");
            locationCallback.onResponse(null);
            return;
        }

        new LocationCallback(locationManager, locationCallback).start();
    }

    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int locationMode = 0;
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            return !TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED));
        }
    }

    private boolean checkGpsProvider() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
