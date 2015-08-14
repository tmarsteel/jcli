package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        this.pattern = Pattern.compile(regex);
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
