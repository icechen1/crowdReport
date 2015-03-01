package com.icechen1.crowdreport.data;

/**
 * Created by YuChen on 2015-02-28.
 */

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

/**
 * Represents an item in a ToDo list
 */
public class Issue {

    @Expose
    private String description;
    @Expose
    private Double lat;
    @Expose
    private Double lon;
    @Expose
    private String picture;
    @Expose
    private String category;
    //@com.google.gson.annotations.SerializedName("userid")
    transient String userId;
    @com.google.gson.annotations.SerializedName("status")
    private int status;
    //@com.google.gson.annotations.SerializedName("createTime")
    //private Date createTime;
    /**
     * Item Id
     */
    @com.google.gson.annotations.SerializedName("id")
    private String mId;

    /**
     * Indicates if the item is completed
     */
    @com.google.gson.annotations.SerializedName("complete")
    private boolean mComplete;
    /**
     * Indicates if the item is marked as completed
     */
    public int getStatus() {
        return status;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setStatus(int s) {
        status = s;
    }
    /**
     * Indicates if the item is marked as completed
     */
    public boolean isComplete() {
        return mComplete;
    }

    /**
     * Marks the item as completed or incompleted
     */
    public void setComplete(boolean complete) {
        mComplete = complete;
    }
    /**
     * Returns the userId id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the userId id
     *
     * @param id
     *            id to set
     */
    public final void setUserId(String id) {
        userId = id;
    }
    /**
     * Returns the item id
     */
    public String getId() {
        return mId;
    }

    /**
     * Sets the item id
     *
     * @param id
     *            id to set
     */
    public final void setId(String id) {
        mId = id;
    }
    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     * The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }


    /**
     *
     * @return
     * The lat
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     * The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     *
     * @param lon
     * The lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     *
     * @return
     * The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     *
     * @param picture
     * The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Issue) == false) {
            return false;
        }
        Issue rhs = ((Issue) other);
        return this == rhs;
    }
}
