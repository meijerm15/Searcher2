package com.searcher.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class Channel {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("category")
    private Category category;

    @SerializedName("user")
    private User owner;

    @SerializedName("members")
    private List<User> members;

    @SerializedName("size")
    private int size;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("altitude")
    private double altitude;

    @Expose
    private double distance;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Category getCategory() {
        return category;
    }

    public User getOwner() {
        return owner;
    }

    public List<User> getMembers() {
        return members;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public int getSize() {
        return size;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return String.format("[id:%s, title:%s, category:%s, description:%s, owner:%s, size:%s, distance:%s]", id, title, category, description, owner, size, distance);
    }
}
