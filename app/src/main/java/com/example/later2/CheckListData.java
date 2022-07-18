package com.example.later2;

public class CheckListData {

    private int id;
    private int FId;
    private String title;
    private String color;
    private String description;
    private String images;
    private boolean urgent;
    private boolean ticked;
    private String rating;
    private int xp;
    private String dateDue;
    private String dateCreated;
    private String dateEdited;
    //private int numberInList;
    private String order;
    private String location;
    private String iconImage;
    private String note;
    private String other;
    private String drawing;

    public CheckListData(int id, int FId, String title, String color, String description, String images, boolean urgent, boolean ticked, String rating, int xp, String dateDue, String dateCreated, String dateEdited, String order, String location, String iconImage, String note, String other, String drawing) {
        this.id = id;
        this.FId = FId;
        this.title = title;
        this.color = color;
        this.description = description;
        this.images = images;
        this.urgent = urgent;
        this.ticked = ticked;
        this.rating = rating;
        this.xp = xp;
        this.dateDue = dateDue;
        this.dateCreated = dateCreated;
        this.dateEdited = dateEdited;
        //this.numberInList = numberInList;
        this.order = order;
        this.location = location;
        this.iconImage = iconImage;
        this.note = note;
        this.other = other;
        this.drawing = drawing;
    }

    @Override
    public String toString() {
        return "CheckListData{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color='" + color + '\'' +
                ", description='" + description + '\'' +
                ", images='" + images + '\'' +
                ", urgent=" + urgent +
                ", ticked=" + ticked +
                ", rating='" + rating + '\'' +
                ", xp=" + xp +
                ", dateDue='" + dateDue + '\'' +
                ", dateCreated='" + dateCreated + '\'' +
                ", dateEdited='" + dateEdited + '\'' +
                ", order='" + order + '\'' +
                ", location='" + location + '\'' +
                ", iconImage='" + iconImage + '\'' +
                ", note='" + note + '\'' +
                ", other='" + other + '\'' +
                ", drawing='" + drawing + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFId() {
        return FId;
    }

    public void setFId(int checkListId) { this.FId = FId; }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isTicked() {
        return ticked;
    }

    public void setTicked(boolean ticked) {
        this.ticked = ticked;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public String getDateDue() {
        return dateDue;
    }

    public void setDateDue(String dateDue) {
        this.dateDue = dateDue;
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

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getDrawing() {
        return drawing;
    }

    public void setDrawing(String drawing) {
        this.drawing = drawing;
    }
}
