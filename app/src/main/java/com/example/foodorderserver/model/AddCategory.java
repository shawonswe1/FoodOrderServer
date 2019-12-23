package com.example.foodorderserver.model;

public class AddCategory
{
    private String ImageName,ImageUrl,mKey;

    public AddCategory() {
    }

    public AddCategory(String imageName , String imageUrl) {
        ImageName = imageName;
        ImageUrl = imageUrl;
    }

    public String getImageName() {
        return ImageName;
    }

    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        this.mKey = key;
    }
}
