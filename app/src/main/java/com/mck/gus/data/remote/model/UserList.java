package com.mck.gus.data.remote.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class UserList {

    @SerializedName("total_count")
    @Expose
    public Integer totalCount;
    @SerializedName("items")
    @Expose
    public List<User> items = new ArrayList<User>();

    /**
     *
     * @return
     * The totalCount
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     *
     * @param totalCount
     * The total_count
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     *
     * @return
     * The items
     */
    public List<User> getItems() {
        return items;
    }

    /**
     *
     * @param items
     * The items
     */
    public void setItems(List<User> items) {
        this.items = items;
    }

}
