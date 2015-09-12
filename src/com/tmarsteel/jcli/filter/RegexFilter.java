package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validator.ValidationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Accepts only Strings matching the given regex.
 * @author tmarsteel
 */
public class RegexFilter implements Filter
{
    protected Pattern pattern;
    protected int returnGroup = 0;

    /**
     * Creates a new filter matching against the given pattern.
     * @param pattern The pattern to compile and then match against
     */
    public RegexFilter(Pattern pattern)
    {
        this.pattern = pattern;
    }

    /**
     * Creates a new filter matching against the given pattern.
     * @param regex The pattern to match against
     */
    public RegexFilter(String regex)
    {
        this(Pattern.compile(regex));
    }

    /**
     * Creates a new filter from a DOM node. <br>
     * Node structure: The regex itself must be contained in a &lt;regex&gt;
     * subtag.
     * The group to return may be specified by the <code>returnGroup</code>
     * attribute in <code>filterNode</code>.
     * @param filterNode
     * @throws ParseException If the return group attribute is not set to an 
     * integer value or any other tag than &lt;regex&gt; is found within
     * <code>filterNode</code>
     */
    public RegexFilter(Node filterNode)
        throws ValidationException
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
                throw new ValidationException("Value of returnGroup attribute needs to be an integer");
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
            throw new ValidationException("regex-filters only allow regex tags");
        }
    }

    @Override
    public Object parse(String value)
        throws ValidationException
    {
        Matcher m = pattern.matcher(value);
        if (m.matches())
        {
            return m.group(returnGroup);
        }
        else
        {
            throw new ValidationException("value needs to match regex " + pattern.pattern());
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
