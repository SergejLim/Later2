package com.example.later2;

import java.util.Date;

public class CheckLists {

    private int id;
    private String title;
    private String color;
    private String icon;
    private String password;
    private boolean favourite;
    private String key;
    private String shared;
    private String layout; //full card/ small card
    private String type; //watch list
    private String dateCreated;
    private String dateEdited;
    private int numberInList;
    private String order;


    public CheckLists(int id, String title, String color, String icon, String password, boolean favourite, String key, String shared, String layout, String type, String dateCreated, String dateEdited, int numberInList, String order) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.icon = icon;
        this.password = password;
        this.favourite = favourite;
        this.key = key;
        this.shared = shared;
        this.layout = layout;
        this.type = type;
        this.dateCreated = dateCreated;
        this.dateEdited = dateEdited;
        this.numberInList = numberInList;
        this.order = order;
    }

    @Override
    public String toString() {
        return "CheckLists{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                ", password='" + password + '\'' +
                ", favourite=" + favourite +
                ", key='" + key + '\'' +
                ", shared=" + shared +
                ", layout='" + layout + '\'' +
                ", type='" + type + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateEdited=" + dateEdited +
                ", numberInList=" + numberInList +
                ", order='" + order + '\'' +
                '}';
    }

    //Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateEdited() {
        return dateEdited;
    }

    public void setDateEdited(String dateEdited) {
        this.dateEdited = dateEdited;
    }

    public int getNumberInList() {
        return numberInList;
    }

    public void setNumberInList(int numberInList) {
        this.numberInList = numberInList;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
