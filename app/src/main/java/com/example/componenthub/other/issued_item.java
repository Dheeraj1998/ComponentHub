package com.example.componenthub.other;

/**
 * Created by dheeraj on 28/08/17.
 */

public class issued_item {
    private String component_name;
    private String issued_date;
    private String return_date;
    private String component_code;

    public issued_item() {

    }

    public issued_item(String component_name, String issued_date, String return_date, String component_code) {
        this.component_name = component_name;
        this.issued_date = issued_date;
        this.return_date = return_date;
        this.component_code = component_code;
    }

    public String getComponent_name() {
        return component_name;
    }

    public void setComponent_name(String component_name) {
        this.component_name = component_name;
    }

    public String getIssued_date() {
        return issued_date;
    }

    public void setIssued_date(String issued_date) {
        this.issued_date = issued_date;
    }

    public String getReturn_date() {
        return return_date;
    }

    public void setReturn_date(String return_date) {
        this.return_date = return_date;
    }

    public String getComponent_code() {
        return component_code;
    }

    public void setComponent_code(String component_code) {
        this.component_code = component_code;
    }
}
