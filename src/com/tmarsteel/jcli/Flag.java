package com.tmarsteel.jcli;

/**
 * Represents a flag; for easier use with {@link Option}.
 * @author tmarsteel
 */
public class Flag extends Option
{
    public Flag(String... names)
    {
        super(true, names);
    }
}
