package com.tmarsteel.jcli;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Superclass for flags and options that handles aliasing.
 * @author Tobias Marstaller
 */
public abstract class Identifiable
{
    private List<String> names;
    
    /**
     * Creates new identifiable; the first element in <code>names</code> becomes
     * the primary identifier (see {@link #getPrimaryIdentifier()}.
     * @param names The names of this identifiable
     * @throws IllegalArgumentException If names is empty.
     */
    public Identifiable(String... names)
    {
        this(Arrays.asList(names));
    }
    
    /**
     * Creates new identifiable; the first element in <code>names</code> becomes
     * the primary identifier (see {@link #getPrimaryIdentifier()}.
     * @param names The names of this identifiable
     * @throws IllegalArgumentException If names is empty.
     */
    public Identifiable(List<String> names)
    {
        if (names.isEmpty())
        {
            throw new IllegalArgumentException("At least one name must be given.");
        }
        
        this.names = names;
    }
    
    /**
     * Returns whether the given name identifies this flag/option.
     * @return Whether the given name identifies this flag/option.
     */
    public boolean isIdentifiedBy(String name)
    {
        return names.contains(name);
    }
    
    /**
     * Returns the primary identifier for this option.
     * @return The primary identifier for this option.
     */
    public String getPrimaryIdentifier()
    {
        return names.get(0);
    }
    
    /**
     * Returns whether any of this' identifiers is ambigous with any
     * of the identifiers of the given {@link Identifiable}
     * @return Whether any of this' identifiers is ambigous with any
     * of the identifiers of the given {@link Identifiable}
     */
    public boolean isAmbigousWith(Identifiable o)
    {
        Iterator<String> myIt = names.iterator();
        
        while (myIt.hasNext())
        {
            if (o.names.contains(myIt.next()))
            {
                return true;
            }
        }
        
        return false;
    }
}
