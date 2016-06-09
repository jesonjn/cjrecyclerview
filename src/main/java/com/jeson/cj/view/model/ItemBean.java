package com.jeson.cj.view.model;

import java.util.Objects;

/**
 * Created by jiangneng on 6/8/16.
 */
public class ItemBean implements  Comparable<ItemBean>{
    private int type;
    private  String icon;
    private  String text;
    private Objects objects;
    private  String id;
    private   String pID="";
    private Integer   sec;

    public Integer getSec() {
        return sec;
    }

    public void setSec(Integer sec) {
        this.sec = sec;
    }

    public String getId() {
        return id==null?"":id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getpID() {
        return pID==null?"":pID;
    }

    public void setpID(String pID) {
        this.pID = pID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getText() {
        return text==null?"":text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Objects getObjects() {
        return objects;
    }

    public void setObjects(Objects objects) {
        this.objects = objects;
    }

    @Override
    public int compareTo(ItemBean another) {
        return this.getSec().compareTo(another.getSec());
    }
}
