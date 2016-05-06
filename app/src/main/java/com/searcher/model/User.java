package com.searcher.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("phone")
    private String phone;

    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("[id:%s, phone:%s]", id, phone);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof User) {
            User user = (User) o;
            if(user.getId().equals(id) && user.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }
}
