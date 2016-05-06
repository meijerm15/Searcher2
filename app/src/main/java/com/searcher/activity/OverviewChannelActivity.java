package com.searcher.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

import com.searcher.R;
import com.searcher.adapter.ChannelAdapter;
import com.searcher.dao.ChannelDAO;
import com.searcher.dao.UserDAO;
import com.searcher.model.Channel;
import com.searcher.model.User;
import com.searcher.util.GeoUtil;
import com.searcher.util.LocationRequester;

import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class OverviewChannelActivity extends AppCompatActivity {
    private LocationRequester locationRequester;
    private ChannelAdapter adapter;
    private User user;
    private UserDAO userDAO;
    private ChannelDAO channelDAO;

    @Bind(R.id.channel_listview)
    protected ListView mListview;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.fab)
    protected FloatingActionButton mChannelCreateButton;

    @Bind(R.id.main_swipe)
    protected SwipeRefreshLayout mSwipe;

    public OverviewChannelActivity() {
        userDAO = new UserDAO();
        channelDAO = new ChannelDAO();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview_channel);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        adapter = new ChannelAdapter(this);
        mListview.setAdapter(adapter);

        locationRequester = new LocationRequester(this);

        onChannelCreateButton();

        mSwipe.setOnRefreshListener(() -> loadChannels());
    }


    private void setRefreshing() {
        // Workaround to apply refreshing progressview
        mSwipe.setProgressViewOffset(false, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        mSwipe.setRefreshing(true);
    }

    private void onChannelCreateButton() {
        mChannelCreateButton.setOnClickListener((view) -> {
            Intent intent = new Intent(OverviewChannelActivity.this, CreateChannelActivity.class);
            intent.putExtra("user", user.getId());
            startActivity(intent);
        });
    }

    private void loadChannels() {
        adapter.setUser(user);
        channelDAO.getChannels((List<Channel> channels, int code) -> {
            if (code == 200 && channels != null) {
                findLocation(channels);
            } else {
                Snackbar.make(mChannelCreateButton, "Network Error, Channels not found", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public void setCreateChannelVisability() {
//        if(adapter.isMember()) {
        if (adapter.isMember()) {
            mChannelCreateButton.setVisibility(View.GONE);
        } else {
            mChannelCreateButton.setVisibility(View.VISIBLE);
        }
    }

    private void findLocation(final List<Channel> channels) {
        locationRequester.requestLocationUpdate((location) -> {
            if (location != null) {
                setChannelDistance(channels, location);
            } else {
                Snackbar.make(mChannelCreateButton, "GPS Error, Location not found", Snackbar.LENGTH_LONG).setAction("GPS Settings", (view) -> startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))).show();
            }

            setUserChannel(channels);

            adapter.set(channels);
            adapter.checkIsMember();
            adapter.notifyDataSetChanged();

            setCreateChannelVisability();

            mSwipe.setRefreshing(false);
        });
    }

    private void setChannelDistance(List<Channel> channels, Location location) {
        for (Channel channel : channels) {
            channel.setDistance(GeoUtil.distance(channel.getLatitude(), location.getLatitude(), channel.getLongitude(), location.getLongitude(), channel.getAltitude(), location.getAltitude()));
        }
    }

    private void setUserChannel(List<Channel> channels) {
        for (Channel channel : channels) {
            if (channel.getMembers().contains(user)) {
                channels.remove(channel);
                channels.add(0, channel);
                break;
            }
        }
    }

    public void loadUser() {
        String deviceId = getDeviceId();
        userDAO.getUserByPhone(deviceId, (user, code) -> {
            if (user == null) {
                createUser(deviceId);
            } else {
                this.user = user;
                loadChannels();
            }
        });
    }

    private void createUser(String phoneNumber) {
        User user = new User();
        user.setPhone(phoneNumber);
        userDAO.createUser(user, (newUser, code) -> {
            if (code == 200 && newUser != null) {
                this.user = newUser;
                loadChannels();
            } else {
                Snackbar.make(mChannelCreateButton, "Network Error, User could not be created", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String getDeviceId() {
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public void reload() {
        setRefreshing();
        adapter.clear();
        if (user == null) {
            loadUser();
        } else {
            loadChannels();
        }
    }

    @Override
    protected void onResume() {
        reload();
        super.onResume();
    }
}
