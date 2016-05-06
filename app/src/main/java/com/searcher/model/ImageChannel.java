package com.searcher.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class ImageChannel {
    @SerializedName("image")
    private String base64;

    @SerializedName("channel")
    private String channel;

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
