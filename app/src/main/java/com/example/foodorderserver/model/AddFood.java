package com.example.foodorderserver.model;

public class AddFood {
    String foodName,foodPrice,foodDiscount,image;
    String mKey;

    public AddFood() {
    }

    public AddFood(String foodName , String foodPrice , String foodDiscount , String image) {
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodDiscount = foodDiscount;
        this.image = image;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDiscount() {
        return foodDiscount;
    }

    public void setFoodDiscount(String foodDiscount) {
        this.foodDiscount = foodDiscount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }
}
