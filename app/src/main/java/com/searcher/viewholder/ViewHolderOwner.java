package com.searcher.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.searcher.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zanax on 12/1/2015.
 */
public class ViewHolderOwner extends RecyclerView.ViewHolder {
    @Bind(R.id.channel_title)
    public TextView channelTitle;
    @Bind(R.id.channel_description)
    public TextView channelDescription;
    @Bind(R.id.channel_category)
    public TextView channelCategory;
    @Bind(R.id.channel_distance)
    public TextView channelDistance;
    @Bind(R.id.channel_size)
    public TextView channelSize;
    @Bind(R.id.channel_item_stub)
    public ViewStub channelStub;
    @Bind(R.id.channel_image)
    public ImageView channelImage;

    public TextView channelEdit;
    public TextView channelDelete;

    public ViewHolderOwner(View view) {
        super(view);
        ButterKnife.bind(this, view);
        channelStub.setLayoutResource(R.layout.channel_item_owner);
        View viewStub = channelStub.inflate();

        channelEdit = ButterKnife.findById(viewStub, R.id.channel_edit);
        channelDelete = ButterKnife.findById(viewStub, R.id.channel_delete);

    }
}
