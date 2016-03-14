package com.example.arpaul.rideit.GPSUtilities;



public interface GPSCallback 
{
    public abstract void gotGpsValidationResponse(Object response, GPSErrorCode code);
}
