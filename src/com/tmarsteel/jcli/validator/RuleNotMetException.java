package com.tmarsteel.jcli.validator;

public class RuleNotMetException extends ValidationException
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
