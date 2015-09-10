package com.tmarsteel.jcli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a flag; for easier use with {@link Option}.
 * @author tmarsteel
 */
public class Flag extends Identifiable
{    
    /**
     * Creates a new flag that is identified by the 
     * @param names 
     */
    public Flag(String... names)
    {
        super(names);
    }
    
    @Override
    public String toString()
    {
        return this.getPrimaryIdentifier() + " flag";
    }
}
