package com.example.componenthub.other;

/**
 * Created by dheeraj on 29/08/17.
 */

public class inventory_item {
    private String color;
    private String component_name;
    private String component_count;

    public inventory_item() {
    }

    public inventory_item(String color, String component_name, String component_count) {
        this.color = color;
        this.component_name = component_name;
        this.component_count = component_count;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getComponent_name() {
        return component_name;
    }

    public void setComponent_name(String component_name) {
        this.component_name = component_name;
    }

    public String getComponent_count() {
        return component_count;
    }

    public void setComponent_count(String component_count) {
        this.component_count = component_count;
    }
}
