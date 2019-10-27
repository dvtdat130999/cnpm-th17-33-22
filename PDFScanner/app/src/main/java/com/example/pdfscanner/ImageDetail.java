package com.example.pdfscanner;

import java.util.Date;

public class ImageDetail {
    private String location;
    private Date dateTake;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateTake() {
        return dateTake;
    }

    public void setDateTake(Date dateTake) {
        this.dateTake = dateTake;
    }
}
