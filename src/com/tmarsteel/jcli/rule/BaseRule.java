package com.tmarsteel.jcli.rule;

/**
 * Represents a rule regarding the precence and values of options and flags.
 * @author tmarsteel
 */
public abstract class BaseRule implements Rule
{
    protected String errorMessage;
    
    @Override
    public void setErrorMessage(String msg)
    {
        this.errorMessage = msg;
    }
}
