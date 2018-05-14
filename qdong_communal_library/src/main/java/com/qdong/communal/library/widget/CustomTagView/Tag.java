package com.qdong.communal.library.widget.CustomTagView;
/**
 * Tag
 * 标签模型
 * 责任人:  Chuck
 * 修改人： Chuck
 * 创建/修改时间: 2016/2/1  11:51
 **/
public class Tag {
    private int id;
    private String name;
    private boolean isChecked;//是否被选择

    public Tag() {
    }

    public Tag(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(int id, String name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
    }

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

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return name;
    }
}
