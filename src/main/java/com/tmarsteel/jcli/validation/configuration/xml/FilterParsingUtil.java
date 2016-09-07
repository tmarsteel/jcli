package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.*;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.Validator;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Pattern;

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

    public static DecimalFilter parseDecimalFilter(Node filterNode)
        throws ValidationException
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
        double minValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[0]);
        double maxValue = XMLValidatorConfigurator.XMLUtils.asDouble(minMax[1]);
        return new DecimalFilter(minValue, maxValue);
    }

    public BigIntegerFilter parseBigInteger(Node filterNode)
        throws ParseException
    {
        final String[] minMaxRadix = XMLValidatorConfigurator.XMLUtils.getMinMaxRadix(filterNode);
        final int radix;

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

        BigInteger minValue = minMaxRadix[0] == null? null : new BigInteger(minMaxRadix[0], radix);
        BigInteger maxValue = minMaxRadix[1] == null? null : new BigInteger(minMaxRadix[1], radix);
        return new BigIntegerFilter(minValue, maxValue, radix);
    }

    public static BigDecimalFilter parseBigDecimalFilter(Node filterNode)
    {
        String[] minMax = XMLValidatorConfigurator.XMLUtils.getMinMax(filterNode);
        BigDecimal minValue = minMax[0] == null? null : new BigDecimal(minMax[0]);
        BigDecimal maxValue = minMax[1] == null? null : new BigDecimal(minMax[1]);
        return new BigDecimalFilter(minValue, maxValue);
    }

    public static FileFilter parseFileFilter(Node filterNode)
            throws ParseException
    {
        FileFilter filter = new FileFilter();

        NodeList children = filterNode.getChildNodes();
        for (int i = 0;i < children.getLength();i++)
        {
            Node cNode = children.item(i);

            if (cNode.getNodeName().equals("#text"))
            {
                continue;
            }

            switch (cNode.getNodeName())
            {
                case "extension":
                    filter.setExtension(cNode.getTextContent());
                    break;
                case "type":
                    try
                    {
                        filter.setFileType(FileFilter.TYPE.valueOf(cNode.getTextContent()));
                    }
                    catch (IllegalArgumentException ex)
                    {
                        throw new ParseException("Unknown file type " + cNode.getTextContent());
                    }
                    break;
                case "permissions":
                    try
                    {
                        filter.setPermissions(FileFilter.PERMISSION.valueOf(cNode.getTextContent()));
                    }
                    catch (IllegalArgumentException ex)
                    {
                        throw new ParseException("Unknown permissions combination " + cNode.getTextContent());
                    }
                    break;
                case "existence":
                    try
                    {
                        filter.setExistenceState(FileFilter.EXISTENCE.valueOf(cNode.getTextContent()));
                    }
                    catch (IllegalArgumentException ex)
                    {
                        throw new ParseException("Unknown existence state " + cNode.getTextContent());
                    }
                    break;
                default:
                    throw new ParseException("Unknown tag " + cNode.getNodeName()
                            + " in file-filter");
            }
        }

        return filter;
    }

    public static RegexFilter parseRegexFilter(Node filterNode)
        throws MisconfigurationException
    {
        NamedNodeMap attrs = filterNode.getAttributes();
        // look for the return-group attribute
        Node node = attrs.getNamedItem("returnGroup");

        final int returnGroup;
        final Pattern pattern;

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
                throw new MisconfigurationException("Value of returnGroup attribute needs to be an integer");
            }
        }

        // look for the regex tag, has to be the only one
        Node regexNode = filterNode.getFirstChild();
        while (regexNode.getNodeName().equals("#text"))
            regexNode = regexNode.getNextSibling();

        if (regexNode.getNodeName().equals("regex"))
        {
            pattern = Pattern.compile(regexNode.getTextContent());
        }
        else
        {
            throw new MisconfigurationException("Unknown tag " + regexNode.getNodeName() + "in regex filter");
        }

        RegexFilter filter = new RegexFilter(pattern);
        filter.setReturnGroup(returnGroup);
        return filter;
    }
}
