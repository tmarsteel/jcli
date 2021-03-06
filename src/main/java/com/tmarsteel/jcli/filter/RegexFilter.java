/* 
 * Copyright (C) 2015 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Returns the pattern used to validate input.
     * @return The pattern used to validate input.
     */
    public Pattern getPattern()
    {
        return pattern;
    }

    /**
     * Sets the pattern used to validate input. If a new object is set, the 
     * return group is reset to 0.
     * @param pattern The pattern used to validate input.
     */
    public void setPattern(Pattern pattern)
    {
        if (pattern != this.pattern)
        {
            returnGroup = 0;
        }
        
        this.pattern = pattern;
    }
    
    
}
