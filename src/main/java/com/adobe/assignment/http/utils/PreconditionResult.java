package com.adobe.assignment.http.utils;

import com.adobe.assignment.http.HttpResponse;

public class PreconditionResult {

    private boolean value = true;
    private int statusCode = HttpResponse.NOT_AN_SC;

    private void checkInvariant( ) {
        if (value == false && statusCode == HttpResponse.NOT_AN_SC)
            throw new RuntimeException();
        if(value == true && statusCode != HttpResponse.NOT_AN_SC)
            throw new RuntimeException();
    }

    public void clear( ) {
        this.value = true;
        this.statusCode = HttpResponse.NOT_AN_SC;
    }

    public int getStatusCode( ) {
        checkInvariant();
        return statusCode;
    }

    public boolean getValue( ) {
        checkInvariant();
        return value;
    }

    public void setValue( boolean value ) {
        this.value = value;
    }

    public void setStatusCode( int statusCode ) {
        this.statusCode = statusCode;
    }
}
