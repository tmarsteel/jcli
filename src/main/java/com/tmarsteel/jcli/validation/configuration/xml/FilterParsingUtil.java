package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.IntegerFilter;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import org.w3c.dom.Node;

/**
 * Utility methods used to parse the XML configurations of filters provided by the library
 * @see com.tmarsteel.jcli.filter
 */
abstract class FilterParsingUtil
{
    public static IntegerFilter parseIntegerFilter(XMLValidatorConfigurator context, Node node)
            throws MisconfigurationException, ParseException
    {
        String[] minMaxRadix = XMLValidatorConfigurator.XMLUtils.getMinMaxRadix(node);
        int radix;
        long min;
        long max;

        try
        {
            if (minMaxRadix[2] == null)
            {
                radix = 10;
            }
            else
            {
                radix = Integer.parseInt(minMaxRadix[2]);
            }
        }
        catch (NumberFormatException ex)
        {
            throw new ParseException("Invalid radix: " + minMaxRadix[2]);
        }

        min = minMaxRadix[0] == null? Long.MIN_VALUE : XMLValidatorConfigurator.XMLUtils.asLong(minMaxRadix[0], radix);
        max = minMaxRadix[1] == null? Long.MAX_VALUE : XMLValidatorConfigurator.XMLUtils.asLong(minMaxRadix[1], radix);

        return new IntegerFilter(min, max, radix);
    }
}
