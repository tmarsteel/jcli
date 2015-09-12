package com.tmarsteel.jcli.validator;

import com.tmarsteel.jcli.ParseException;

public class RuleNotMetException extends ParseException
{
    public RuleNotMetException(String message)
    {
        super(message);
    }
    
    public RuleNotMetException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
