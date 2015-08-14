package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Accepts only Strings matching the given regex.
 * @author Tobias Marstaller
 */
public class RegexFilter implements ValueFilter
{
    protected Pattern pattern;
    protected int returnGroup = 0;

    public RegexFilter(Pattern pattern)
    {
        this.pattern = pattern;
    }

    public RegexFilter(String regex)
    {
        this(Pattern.compile(regex));
    }

    public RegexFilter(Node filterNode)
        throws ParseException
    {
        NamedNodeMap attrs = filterNode.getAttributes();
        // look for the return-group attribute
        Node node = attrs.getNamedItem("returnGroup");
        if (node == null)
        {
            returnGroup = 0;
        }
        else
        {
            try
            {
                returnGroup = Integer.parseInt(node.getTextContent());
            }
            catch (NumberFormatException ex)
            {
                throw new ParseException("Value of returnGroup attribute needs to be an integer");
            }
        }

        // look for the regex tag, has to be the only one
        Node regexNode = filterNode.getFirstChild();
        while (regexNode.getNodeName().equals("#text"))
            regexNode = regexNode.getNextSibling();

        if (regexNode.getNodeName().equals("regex"))
        {
            this.pattern = Pattern.compile(regexNode.getTextContent());
        }
        else
        {
            throw new ParseException("regex-filters only allow regex tags");
        }
    }

    @Override
    public Object parse(String value)
        throws ParseException
    {
        Matcher m = pattern.matcher(value);
        if (m.matches())
        {
            return m.group(returnGroup);
        }
        else
        {
            throw new ParseException("value needs to match regex " + pattern.pattern());
        }
    }

    /**
     * Returns the regex-group that will be returned when parsing inputs via
     * {@link #parse(java.lang.String)}
     * @return The regex-group that will be returned when parsing inputs.
     */
    public int getReturnGroup()
    {
        return returnGroup;
    }

    /**
     * Sets the regex-group that will be returned when parsing inputs via
     * {@link #parse(java.lang.String)}.
     * <br>
     * For example:<br>
     * Regex: <code>(\d+)KG</code>, <code>returnGroup = 1</code><br>
     * parse("15KG"): Output is <code>"15"</code>.
     * @param returnGroup The regex-group that will be returned when parsing inputs.
     */
    public void setReturnGroup(int returnGroup)
    {
        this.returnGroup = returnGroup;
    }
}
