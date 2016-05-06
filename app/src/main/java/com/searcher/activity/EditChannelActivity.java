package com.searcher.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.searcher.model.Category;
import com.searcher.model.Channel;
import com.searcher.model.ImageChannel;
import com.searcher.util.BitmapUtil;
import com.searcher.util.LocationRequester;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class EditChannelActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int SELECT_PHOTO = 100;

    private LocationRequester locationRequester;
    private Channel channel;

    private Bitmap bitmap;

    private boolean fakeLocation;

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
    protected TextView mSizeTextView;

    @Bind(R.id.channel_image_preview_form)
    protected ImageView mImagePreview;

    @Bind(R.id.channel_image_preview_form_panel)
    protected ScrollView mImagePreviewPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_channel);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mButton.setOnClickListener(this);

        mButton.setOnLongClickListener((view) -> {
            fakeLocation = true;
            onClick(view);
            return true;
        });

        locationRequester = new LocationRequester(this);

        String channelId = getIntent().getStringExtra("channel");

        ChannelDAO channelDAO = new ChannelDAO();
        channelDAO.getChannelById(channelId, (channel, c1) -> {
            this.channel = channel;

            mTitleTextView.setText(channel.getTitle());
            mDescriptionTextView.setText(channel.getDescription());
            mSizeTextView.setText(Integer.toString(channel.getSize()));


            CategoryDAO categoryDAO = new CategoryDAO();
            categoryDAO.getCategories((cl, c2) -> {
                if (cl != null) {
                    CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(this, cl);
                    mCategorySpinner.setAdapter(adapter);

                    List<Category> categories = adapter.getCategories();
                    for (int i = 0; i < categories.size(); i++) {
                        Category category = categories.get(i);
                        if (category.equals(channel.getCategory())) {
                            mCategorySpinner.setSelection(i);
                            break;
                        }
                    }
                }
            });
        });


    }

    @Override
    public void onClick(View view) {
        // reset errors
        mTitleTextView.setError(null);
        mDescriptionTextView.setError(null);
        mSizeTextView.setError(null);

        // retrieve form data
        String title = mTitleTextView.getText().toString();
        String description = mDescriptionTextView.getText().toString();
        String size = mSizeTextView.getText().toString();

        Category category = (Category) mCategorySpinner.getSelectedItem();

        View focusView = null;
        boolean cancel = false;

        if (size.isEmpty()) {
            cancel = true;
            focusView = mSizeTextView;
            mSizeTextView.setError("Member Size is not mentioned");
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
            // TODO: Temp fake location
            if (fakeLocation) {
                Timber.d("Fake Location, preforming create channel");
                fakeLocation = false;
                preformChannelEdit(view, title, description, category, size, 2.35446525, 4.9076997, 52.36183); // Weesperplein
            } else {
                locationRequester.requestLocationUpdate((location) -> {
                    if (location == null) {
                        Snackbar.make(view, "GPS Error, Check your GPS sensor", Snackbar.LENGTH_LONG).show();
                    } else {
                        preformChannelEdit(view, title, description, category, size, location.getAltitude(), location.getLongitude(), location.getLatitude());
                    }
                });
            }
        }
    }

    private void preformChannelEdit(View view, String title, String description, Category category, String size, double altitude, double longitude, double latitude) {
        channel.setTitle(title);
        channel.setDescription(description);

        channel.setCategory(category);

        channel.setAltitude(altitude);
        channel.setLongitude(longitude);
        channel.setLatitude(latitude);

        channel.setSize(Integer.parseInt(size));

        ChannelDAO channelDAO = new ChannelDAO();
        channelDAO.editChannel(channel, (x, code) -> {
            if (code == 200) {
                if (bitmap == null) {
                    onBackPressed();
                } else {
                    sendChannelImageByte(view, channelDAO, channel);
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
                onBackPressed();
            });

        } catch (Exception e) {
            Timber.e(e, "Bitmap to File");
            Snackbar.make(view, "Server Error. Please try again later.", Snackbar.LENGTH_LONG).show();

            onBackPressed();
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
        finish();
        super.onBackPressed();
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
