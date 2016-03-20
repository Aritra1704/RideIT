package com.example.arpaul.rideit.Utilities;

/**
 * Created by ARPaul on 20-03-2016.
 */
public class CustomException extends Exception {
    private static final long serialVersionUID = 1997753363232807009L;

    public CustomException()
    {
    }

    public CustomException(String message)
    {
        super(message);
    }

    public CustomException(Throwable cause)
    {
        super(cause);
    }

    public CustomException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
