package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.filter.*;
import com.tmarsteel.jcli.validation.MisconfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class FilterParsingUtilTest
{
    NodeList testNodes;
    XMLValidatorConfigurator dummyContext = mock(XMLValidatorConfigurator.class);

    @Before
    public void loadTestXML()
        throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder builder = dbf.newDocumentBuilder();

        Document testDocument = builder.parse(this.getClass().getResourceAsStream("filters.xml"));

        testNodes = testDocument.getElementsByTagName("filter");
    }

    // -- big decimal --
    @Test
    public void bigDecimal_testNodeParser()
    {
        BigDecimalFilter filter = FilterParsingUtil.parseBigDecimalFilter(dummyContext, testNodes.item(0));

        assertEquals(filter.getMinValue().toPlainString(), "10.1");
        assertEquals(filter.getMaxValue().toPlainString(), "2000.978765487");
    }

    @Test(expected = NumberFormatException.class)
    public void bigDecimal_nodeParserShouldFailOnNonNumerical()
    {
        BigDecimalFilter filter = FilterParsingUtil.parseBigDecimalFilter(dummyContext, testNodes.item(1));
    }

    // -- big integer --
    @Test
    public void bigInteger_testNodeParser()
        throws ParseException
    {
        BigIntegerFilter filter = FilterParsingUtil.parseBigInteger(dummyContext, testNodes.item(2));

        assertEquals(filter.getMinValue().toString(), "10");
        assertEquals(filter.getMaxValue().toString(), "2000");
    }

    @Test
    public void bigInteger_testNodeParserWithRadix()
        throws ParseException
    {
        BigIntegerFilter filter = FilterParsingUtil.parseBigInteger(dummyContext, testNodes.item(3));

        assertEquals(filter.getMinValue().toString(10), "10");
        assertEquals(filter.getMaxValue().toString(10), "2000");
        assertEquals(filter.getRadix(), 16);
    }

    @Test(expected = NumberFormatException.class)
    public void bigInteger_nodeParserShouldFailOnNonNumerical()
        throws ParseException
    {
        BigIntegerFilter filter = FilterParsingUtil.parseBigInteger(dummyContext, testNodes.item(4));
    }

    // -- decimal --
    @Test
    public void decimal_testNodeParser()
        throws ParseException
    {
        DecimalFilter filter = FilterParsingUtil.parseDecimalFilter(dummyContext, testNodes.item(5));

        assertEquals(10.1D, filter.getMinValue(), 0.1D);
        assertEquals(2000.978765487D, filter.getMaxValue(), 0.1D);
    }

    @Test(expected = ParseException.class)
    public void decimal_nodeParserShouldFailOnNonNumerical()
        throws ParseException
    {
        DecimalFilter filter = FilterParsingUtil.parseDecimalFilter(dummyContext, testNodes.item(6));
    }

    // -- integer --
    @Test
    public void integer_testNodeParser()
        throws ParseException
    {
        IntegerFilter filter = FilterParsingUtil.parseIntegerFilter(dummyContext, testNodes.item(7));

        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
    }

    @Test
    public void integer_testNodeParserWithRadix()
        throws ParseException
    {
        IntegerFilter filter = FilterParsingUtil.parseIntegerFilter(dummyContext, testNodes.item(8));

        assertEquals(filter.getMinValue(), 10);
        assertEquals(filter.getMaxValue(), 2000);
        assertEquals(filter.getRadix(), 16);
    }

    @Test(expected = ParseException.class)
    public void integer_nodeParserShouldFailOnNonNumerical()
        throws ParseException
    {
        IntegerFilter filter = FilterParsingUtil.parseIntegerFilter(dummyContext, testNodes.item(9));
    }

    // -- regex --
    @Test
    public void regex_testNodeParser()
    {
        RegexFilter f = FilterParsingUtil.parseRegexFilter(dummyContext, testNodes.item(10));

        assertEquals(f.getPattern().pattern(), "^a(.+)f$");
        assertEquals(f.getReturnGroup(), 1);
    }

    @Test(expected = MisconfigurationException.class)
    public void regex_nodeParserShouldFailOnNonNumericReturnGroup()
    {
        RegexFilter f = FilterParsingUtil.parseRegexFilter(dummyContext, testNodes.item(11));
    }

    // -- set --
    @Test
    public void set_testNodeParser_A()
        throws ParseException
    {
        SetFilter filter = FilterParsingUtil.parseSetFilter(dummyContext, testNodes.item(12));

        Collection<String> expectedOptions = Arrays.asList(new String[]{ "foo", "bar" });

        assertEquals(filter.options(), expectedOptions);
        assertFalse(filter.isCaseSensitive());
    }

    @Test
    public void set_testNodeParser_B()
        throws ParseException
    {
        SetFilter filter = FilterParsingUtil.parseSetFilter(dummyContext, testNodes.item(13));

        Collection<String> expectedOptions = Arrays.asList(new String[]{ "foo", "bar" });

        assertEquals(filter.options(), expectedOptions);
        assertTrue(filter.isCaseSensitive());
    }

    @Test(expected = MisconfigurationException.class)
    public void set_nodeParserShouldFailOnEmptyOptionSet()
        throws ParseException
    {
        SetFilter filter = FilterParsingUtil.parseSetFilter(dummyContext, testNodes.item(14));
    }
}
