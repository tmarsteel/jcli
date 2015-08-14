package com.wisper.cli;


public class ParseException extends Exception
{
    public ParseException(String message)
    {
        super(message);
    }
    
    public ParseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
