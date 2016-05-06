package com.searcher.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.searcher.R;
import com.searcher.adapter.CategorySpinnerAdapter;
import com.searcher.dao.CategoryDAO;
import com.searcher.dao.ChannelDAO;
import com.searcher.dao.DAOCallback;
import com.searcher.dao.UserDAO;
import com.searcher.model.Category;
import com.searcher.model.Channel;
import com.searcher.model.ImageChannel;
import com.searcher.model.User;
import com.searcher.util.BitmapUtil;
import com.searcher.util.LocationRequester;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class CreateChannelActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PHOTO = 100;

    private User user;

    private LocationRequester locationRequester;
    private Bitmap bitmap;

    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;

    @Bind(R.id.channel_button_form)
    protected Button mButton;

    @Bind(R.id.channel_title_form)
    protected TextView mTitleTextView;

    @Bind(R.id.channel_category_form)
    protected Spinner mCategorySpinner;

    @Bind(R.id.channel_description_form)
    protected TextView mDescriptionTextView;

    @Bind(R.id.channel_size_form)
    protected TextView mMemberSizeTextView;

    @Bind(R.id.channel_image_preview_form)
    protected ImageView mImagePreview;

    @Bind(R.id.channel_image_preview_form_panel)
    protected ScrollView mImagePreviewPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_channel);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton.setOnClickListener(this);

        mButton.setOnLongClickListener((view) -> {
            onClick(view);
            return true;
        });

        locationRequester = new LocationRequester(this);

        String userId = getIntent().getStringExtra("user");

        UserDAO userDAO = new UserDAO();
        userDAO.getUserById(userId, (user, c1) -> {
            this.user = user;

            CategoryDAO categoryDAO = new CategoryDAO();
            categoryDAO.getCategories((cl, c2) -> {
                if (cl != null) {
                    mCategorySpinner.setAdapter(new CategorySpinnerAdapter(this, cl));
                }
            });
        });
    }

    @Override
    public void onClick(View view) {
        // reset errors
        mTitleTextView.setError(null);
        mDescriptionTextView.setError(null);
        mMemberSizeTextView.setError(null);

        // retrieve form data
        String title = mTitleTextView.getText().toString();
        String description = mDescriptionTextView.getText().toString();
        String size = mMemberSizeTextView.getText().toString();

        Category category = (Category) mCategorySpinner.getSelectedItem();

        View focusView = null;
        boolean cancel = false;

        if (size.isEmpty()) {
            cancel = true;
            focusView = mMemberSizeTextView;
            mMemberSizeTextView.setError("Member Size is not mentioned");
        }

        if (description.isEmpty()) {
            cancel = true;
            focusView = mDescriptionTextView;
            mDescriptionTextView.setError("Description is not mentioned");
        }

        if (title.isEmpty()) {
            cancel = true;
            focusView = mTitleTextView;
            mTitleTextView.setError("Title is not mentioned");
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            locationRequester.requestLocationUpdate((location) -> {
                if (location == null) {
                    Snackbar.make(view, "GPS Error, Check your GPS sensor", Snackbar.LENGTH_LONG).show();
                } else {
                    preformChannelCreation(view, title, description, category, size, location.getAltitude(), location.getLongitude(), location.getLatitude());
                }
            });
        }
    }

    private void preformChannelCreation(View view, String title, String description, Category category, String size, double altitude, double longitude, double latitude) {
        Channel channel = new Channel();

        channel.setTitle(title);
        channel.setDescription(description);

        channel.setCategory(category);

        channel.setAltitude(altitude);
        channel.setLongitude(longitude);
        channel.setLatitude(latitude);

        channel.setSize(Integer.parseInt(size));

        channel.setOwner(user);

        ChannelDAO channelDAO = new ChannelDAO();

        channelDAO.createChannel(channel, (x, code) -> {
            if (code == 200) {
                if (bitmap == null) {
                    onBackPressed();
                } else {
                    sendChannelImageByte(view, channelDAO, x);
                }
            } else {
                Snackbar.make(view, "Server Error. Please try again later.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void sendChannelImageByte(View view, ChannelDAO channelDAO, Channel channel) {
        try {
            File file = new File(getCacheDir(), channel.getId());

            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            String base64 = Base64.encodeToString(bitmapdata, Base64.NO_WRAP);

            ImageChannel imageChannel = new ImageChannel();
            imageChannel.setBase64(base64);
            imageChannel.setChannel(channel.getId());

            channelDAO.addChannelImage(imageChannel, (ch, c) -> {
                Timber.i("%s, %s", ch, c);
                onBackPressed();
            });

        } catch (Exception e) {
            Timber.e(e, "Bitmap to File");
            Snackbar.make(view, "Server Error. Please try again later.", Snackbar.LENGTH_LONG).show();

            onBackPressed();
        }
    }

    private void sendChannelImage(View view, ChannelDAO channelDAO, Channel channel) {
        FileOutputStream fos = null;
        try {
            File file = new File(getCacheDir(), channel.getId());
            file.createNewFile();

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            fos = new FileOutputStream(file);
            fos.write(bitmapdata);
            fos.flush();

            channelDAO.addChannelImage(file, channel.getId(), (y, o) -> {
                Timber.i("%s, %s", y, o);

                onBackPressed();
            });
        } catch (Exception e) {
            Timber.e(e, "Bitmap to File");
            Snackbar.make(view, "Server Error. Please try again later.", Snackbar.LENGTH_LONG).show();

            onBackPressed();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.channel_image_pick_form)
    public void onChannelPickClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapUtil.decodeUri(imageReturnedIntent.getData(), getContentResolver());
                        mImagePreviewPanel.setVisibility(View.VISIBLE);
                        mImagePreview.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Timber.e(e, "bitmap decode");
                    }
                }
        }
    }
}
