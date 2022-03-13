package com.jxntv.account.model;

import java.util.List;

/**
 * 地区模型
 */
public final class RegionModel {

    private int id;
    private String name;
    private String level;
    private String upid;
    private String sort;
    private List<RegionModel> child;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getUpid() {
        return upid;
    }

    public void setUpid(String upid) {
        this.upid = upid;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<RegionModel> getChild() {
        return child;
    }

    public void setChild(List<RegionModel> child) {
        this.child = child;
    }

    @Override
    public String toString() {
        return name;
    }
}
