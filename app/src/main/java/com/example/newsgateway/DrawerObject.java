package com.example.newsgateway;

import android.graphics.Color;

import java.io.Serializable;

public class DrawerObject implements Serializable {

    private String text;
    private Integer color;

    public DrawerObject(String text, Integer color) {
        this.text = text;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}
