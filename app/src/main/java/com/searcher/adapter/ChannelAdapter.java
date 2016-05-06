package com.searcher.adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.searcher.R;
import com.searcher.activity.EditChannelActivity;
import com.searcher.activity.OverviewChannelActivity;
import com.searcher.dao.ChannelDAO;
import com.searcher.model.Channel;
import com.searcher.model.User;
import com.searcher.util.Base64ImageDownloader;
import com.searcher.util.Constants;
import com.searcher.viewholder.ViewHolder;
import com.searcher.viewholder.ViewHolderFree;
import com.searcher.viewholder.ViewHolderMember;
import com.searcher.viewholder.ViewHolderOwner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ChannelAdapter extends BaseAdapter {
    private List<Channel> channels = new ArrayList<>();
    private User user;

    private boolean isMember;

    private OverviewChannelActivity activity;
    private LayoutInflater inflater;

    private ChannelDAO channelDAO;

    public ChannelAdapter(OverviewChannelActivity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);

        this.channelDAO = new ChannelDAO();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        Channel channel = getItem(position);

        if (channel.getMembers().contains(user)) {
            if (channel.getOwner().equals(user)) {
                view = inflater.inflate(R.layout.channel_item, viewGroup, false);

                ViewHolderOwner holder = new ViewHolderOwner(view);

                holder.channelTitle.setText(channel.getTitle());
                holder.channelDescription.setText(channel.getDescription());
                holder.channelCategory.setText(channel.getCategory().getName());
                holder.channelDistance.setText(String.format("%s %s", channel.getDistance(), "km"));
                holder.channelSize.setText(String.format("%s/%s", channel.getMembers().size(), channel.getSize()));
                loadImage(channel, holder.channelImage);

                onChannelEditClick(channel, holder);
                onChannelDeleteClick(channel, holder);
                onLocationClick(channel, holder.channelDistance);
            } else {
                view = inflater.inflate(R.layout.channel_item, viewGroup, false);

                ViewHolderMember holder = new ViewHolderMember(view);

                holder.channelTitle.setText(channel.getTitle());
                holder.channelDescription.setText(channel.getDescription());
                holder.channelCategory.setText(channel.getCategory().getName());
                holder.channelDistance.setText(String.format("%s %s", channel.getDistance(), "km"));
                holder.channelSize.setText(String.format("%s/%s", channel.getMembers().size(), channel.getSize()));
                loadImage(channel, holder.channelImage);

                onChannelLeaveClick(channel, holder);
                onLocationClick(channel, holder.channelDistance);
            }
        } else if (isMember) {
            view = inflater.inflate(R.layout.channel_item, viewGroup, false);

            ViewHolder holder = new ViewHolder(view);

            holder.channelTitle.setText(channel.getTitle());
            holder.channelDescription.setText(channel.getDescription());
            holder.channelCategory.setText(channel.getCategory().getName());
            holder.channelDistance.setText(String.format("%s %s", channel.getDistance(), "km"));
            holder.channelSize.setText(String.format("%s/%s", channel.getMembers().size(), channel.getSize()));
            loadImage(channel, holder.channelImage);

            onLocationClick(channel, holder.channelDistance);
        } else {
            view = inflater.inflate(R.layout.channel_item, viewGroup, false);

            ViewHolderFree holder = new ViewHolderFree(view);

            holder.channelTitle.setText(channel.getTitle());
            holder.channelDescription.setText(channel.getDescription());
            holder.channelCategory.setText(channel.getCategory().getName());
            holder.channelDistance.setText(String.format("%s %s", channel.getDistance(), "km"));
            holder.channelSize.setText(String.format("%s/%s", channel.getMembers().size(), channel.getSize()));
            loadImage(channel, holder.channelImage);

            onChannelJoinClick(channel, holder);
            onLocationClick(channel, holder.channelDistance);
        }

        return view;
    }

    private void loadImage(Channel channel, ImageView imageView) {
        String path = Constants.BASE_URL + "channel/image/" + channel.getId();

        new Picasso.Builder(activity)
                .downloader(new Base64ImageDownloader())
                .build()
                .load(path)
                .placeholder(R.drawable.placeholder_image)
                .fit()
                .into(imageView);
    }

    // Cardview Interactions \\

    private void onLocationClick(Channel channel, View view) {
        view.setOnClickListener((view2) -> {
            String url = String.format(Constants.GOOGLE_MAP_URL, channel.getLatitude(), channel.getLongitude());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            activity.startActivity(browserIntent);
        });
    }

    private void onChannelLeaveClick(Channel channel, ViewHolderMember holder) {
        holder.channelLeave.setOnClickListener((view) -> {
            if (user != null) {
                channelDAO.removeMember(channel.getId(), user, (v, code) -> {
                    if (code == 200) {
                        activity.reload();
                        Snackbar.make(view, "Successful remove User from Channel", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, "Failed to remove User from Channel", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                Snackbar.make(view, "No User Found", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onChannelJoinClick(Channel channel, ViewHolderFree holder) {
        holder.channelJoin.setOnClickListener((view) -> {
            if (user != null) {
                ChannelDAO channelDAO = new ChannelDAO();
                channelDAO.addMember(channel, user, (c, code) -> {
                    if (code == 200) {
                        activity.reload();
                        Snackbar.make(view, "Successful added User to Channel", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, "Failed to add User to Channel", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                Snackbar.make(view, "No User Found", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onChannelEditClick(Channel channel, ViewHolderOwner holder1) {
        holder1.channelEdit.setOnClickListener((view) -> {
            if (user != null) {
                Intent intent = new Intent(activity.getApplication(), EditChannelActivity.class);
                intent.putExtra("channel", channel.getId());
                activity.startActivity(intent);
            } else {
                Snackbar.make(view, "No User Found", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onChannelDeleteClick(Channel channel, ViewHolderOwner holder1) {
        holder1.channelDelete.setOnClickListener((view) -> {
            if (user != null) {
                channelDAO.removeChannel(channel.getId(), (v, code) -> {
                    if (code == 200) {
                        activity.reload();
                        Snackbar.make(view, "Successful removed Channel", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, "Failed to remove Channel", Snackbar.LENGTH_LONG).show();
                    }
                });
            } else {
                Snackbar.make(view, "No User Found", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Getter Setters \\
    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Channel getItem(int position) {
        return channels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isMember() {
        return isMember;
    }


    public void set(List<Channel> channels) {
        if (channels != null) {
            this.channels = channels;
        }
    }

    public void clear() {
        channels.clear();
        notifyDataSetChanged();
    }

    public void checkIsMember() {
        for (int i = 0; i < channels.size(); i++) {
            Channel channel = channels.get(i);
            if (channel.getMembers().contains(user)) {
                isMember = true;
                return;
            }
        }
        isMember = false;
    }
}
