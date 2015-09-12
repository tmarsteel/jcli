package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accepts only values from a given set of possible values.
 * @author tmarsteel
 */
public class SetFilter implements Filter
{
    protected final Collection<String> options;
    protected boolean caseSensitive = false;

    /**
     * Creates a new case insensitive filter treating the given strings as valid.
     * @param options The strings to accept.
     */
    public SetFilter(String... options)
    {
        this(Arrays.asList(options));
    }

    /**
     * Creates a new filter treating the given strings as valid.
     * @param caseSensitive Whether the filter should be case sensitive
     * @param options The strings to accept.
     */
    public SetFilter(boolean caseSensitive, String... options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    /**
     * Creates a new case insensitive filter treating the given strings as valid.
     * @param options The strings to accept.
     */
    public SetFilter(Collection<String> options)
    {
        if (options.isEmpty())
        {
            throw new IllegalArgumentException("Need to specify at least one value");
        }
        this.options = options;
    }

    /**
     * Creates a new filter treating the given strings as valid.
     * @param caseSensitive Whether the filter should be case sensitive
     * @param options The strings to accept.
     */
    public SetFilter(boolean caseSensitive, Collection<String> options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    /**
     * Creates a new filter from a DOM node. <br>
     * Node format: the node itself may have a <code>caseSensitive</code> attribute.
     * If this attribute is not present or its value is <code>false</code> the
     * filter will be case insensitive. On any other value the filter will be
     * case sensitive.<br>
     * The strings this filter should accept must be declared with <code>&lt;value&gt;</code>
     * subtags.
     * @param filterNode
     * @throws ParseException If any other subtag than &lt;value&gt; is found.
     */
    public SetFilter(Node filterNode)
        throws ValidationException
    {
        NamedNodeMap attrs = filterNode.getAttributes();
        // look for caseSensitive attribute
        Node cNode = attrs.getNamedItem("caseSensitive");

        if (cNode == null)
        {
            caseSensitive = false;
        }
        else
        {
            caseSensitive = true;
            
            if (cNode.getTextContent().equals("false"))
            {
                caseSensitive = false;
            }
        }

        // look for all possible values
        this.options = new ArrayList<String>();
        NodeList children = filterNode.getChildNodes();
        for (int i = 0;i < children.getLength();i++)
        {
            cNode = children.item(i);
            if (cNode.getNodeName().equals("value"))
            {
                options.add(cNode.getTextContent());
            }
            else if (!cNode.getNodeName().equals("#text"))
            {
                throw new ValidationException("set-filters only allow value tags");
            }
        }
    }

    @Override
    public Object parse(String value)
        throws ValidationException
    {
        final Iterator<String> it = options.iterator();
        while (it.hasNext())
        {
            final String cur = it.next();
            if (caseSensitive)
            {
                if (cur.equals(value))
                {
                    return cur;
                }
            }
            else
            {
                if (cur.equalsIgnoreCase(value))
                {
                    return cur;
                }
            }
        }
        throw new ValidationException(value + " is not a possible value.");
    }

    /**
     * Returns whether this filter is case sensitive.
     * @return Whether this filter is case sensitive.
     */
    public boolean isCaseSensitive() 
    {
        return caseSensitive;
    }

    /**
     * Sets whether this filter is case sensitive.
     * @param caseSensitive Whether this filter is case sensitive.
     */
    public void setCaseSensitive(boolean caseSensitive)
    {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Returns a collection of the options this filter treats as valid. Elements
     * can be added/removed from this collection at will and the filter will
     * adapt to the changes.
     * @return A collection of the options this filter treats as valid.
     */
    public Collection<String> options()
    {
        return this.options;
    }
}
