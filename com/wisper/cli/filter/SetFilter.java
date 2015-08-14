package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accepts only values from a given set of possible values.
 * @author Tobias Marstaller
 */
public class SetFilter implements ValueFilter
{
    protected Collection<String> options;
    protected boolean caseSensitive = false;

    public SetFilter(String... options)
    {
        this(Arrays.asList(options));
    }

    public SetFilter(boolean caseSensitive, String... options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    public SetFilter(Collection<String> options)
    {
        if (options.isEmpty())
        {
            throw new IllegalArgumentException("Need to specify at least one value");
        }
        this.options = options;
    }

    public SetFilter(boolean caseSensitive, Collection<String> options)
    {
        this(options);
        this.caseSensitive = caseSensitive;
    }

    public SetFilter(Node filterNode) throws ParseException
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
            if (cNode.getTextContent().equals("true"))
            {
                caseSensitive = true;
            }
            else if (cNode.getTextContent().equals("false"))
            {
                caseSensitive = false;
            }
            else
            {
                throw new ParseException("Invalid value for caseSensitive attribute on set-filter");
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
                throw new ParseException("set-filters only allow value tags");
            }
        }
    }

    @Override
    public Object parse(String value)
        throws ParseException
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
        throw new ParseException(value + " is not a possible value.");
    }

}
