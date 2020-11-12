package com.bill.zografos;

import com.google.gson.GsonBuilder;
import java.util.Date;

/**
 * Created by vasilis on 01/12/2019.
 */
public class Product {
    private String name;
    private String barcode;
    private float price;
    private String category;
    private String description;

    public Product(String name, String barcode, float price, String category, String description) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.category = category;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public float getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String toString() {
        String text = "com.bill.zografos.Product #"+barcode+"\n";
        text += "   name: "+name+"\n";
        text += "   price: "+price+"\n";
        text += "   category: "+category+"\n";
        text += "   description: "+description+"\n";
        return text;
    }

    public String toJson() {
        String productJson = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        return productJson;
    }
}
