package com.tmarsteel.jcli.parser;

/**
 * Thrown when a configuration is to be applied that is invalid/falwed.
 * @author tmarsteel
 */
public class MisconfigurationException extends RuntimeException
{
    public MisconfigurationException(String message)
    {
        super(message);
    }
    
    public MisconfigurationException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
