package com.searcher.util;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import timber.log.Timber;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class LocationCallback implements LocationListener {
    private static final int TIME_OUT = 5000;

    private LocationManager manager;
    private Callback callback;
    private boolean handled;

    private Runnable runnable;
    private Handler handler;

    public LocationCallback(LocationManager manager, Callback callback) {
        this.callback = callback;
        this.manager = manager;
    }

    public void start() {
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
            Timber.e(e, "Security Exception");
        }

        scheduleTimeout();
    }

    @Override
    public void onLocationChanged(Location location) {
        if(!handled) {
            handled = true;
            Timber.d("onLocationChanged: %s", location);
            stopTimeout();
            stopListener();
            callback.onResponse(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    private void scheduleTimeout() {
        runnable = () -> {
            if(!handled) {
                handled = true;
                Timber.d("Timeout");
                stopListener();

                callback.onResponse(null);
            }
        };
        handler = new Handler();
        handler.postDelayed(runnable, TIME_OUT);
    }

    private void stopTimeout() {
        handler.removeCallbacks(runnable);
    }

    private void stopListener() {
        try {
            manager.removeUpdates(this);
        } catch (SecurityException e) {
            Timber.e(e, "Security Exception");
        }
    }

    public interface Callback {
        void onResponse(Location location);
    }
}
