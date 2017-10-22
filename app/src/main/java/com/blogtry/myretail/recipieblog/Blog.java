package com.blogtry.myretail.recipieblog;

/**
 * Created by Taruni on 10/3/2017.
 */

public class Blog {
  private String Title;
    private String Description;
    private String Image;

    private String time;
    private String people;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }


    public String getYOUTUBEid() {
        return YOUTUBEid;
    }

    public void setYOUTUBEid(String YOUTUBEid) {
        this.YOUTUBEid = YOUTUBEid;
    }

    private String YOUTUBEid;

    public String getRecipe() {
        return Recipe;
    }

    public void setRecipe(String recipe) {
        Recipe = recipe;
    }

    private String Recipe;


    private String username;

    public Blog(){

    }

    public Blog(String title, String description, String image,String recipe) {

        Title = title;
        Description = description;
        Recipe= recipe;
        Image = image;
        username=username;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
