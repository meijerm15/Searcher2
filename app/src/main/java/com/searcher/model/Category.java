package com.searcher.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author Maarten Meijer (Maarten.Meijer@hva.nl)
 */
public class Category {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("[id:%s, name:%s]", id, name);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Category) {
            Category category = (Category) o;
            if(category.getId().equals(id) && category.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
