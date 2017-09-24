package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.table.builder.CamelCaseGetterMethodToHeadingConverter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CamelCaseGetterMethodToHeadingConverterTest
{
    private CamelCaseGetterMethodToHeadingConverter subject;

    @Before
    public void setUp() {
        subject = CamelCaseGetterMethodToHeadingConverter.getInstance();
    }

    @Test
    public void test() throws NoSuchMethodException {
        assertEquals("A Property With A Long Name", subject.toHeading(getClass().getMethod("getAPropertyWithALongName")));
        assertEquals("Foobar", subject.toHeading(getClass().getMethod("getFoobar")));
        assertEquals("Foo Bar", subject.toHeading(getClass().getMethod("getFooBar")));
        assertEquals("Property 2", subject.toHeading(getClass().getMethod("getProperty2")));
        assertEquals("Property 2 And 3", subject.toHeading(getClass().getMethod("getProperty2And3")));
    }

    public int getAPropertyWithALongName() {
        throw new UnsupportedOperationException("This method is just a dummy so that a reference to it can be passed around.");
    }

    public int getFoobar() {
        throw new UnsupportedOperationException("This method is just a dummy so that a reference to it can be passed around.");
    }

    public int getFooBar() {
        throw new UnsupportedOperationException("This method is just a dummy so that a reference to it can be passed around.");
    }

    public int getProperty2() {
        throw new UnsupportedOperationException("This method is just a dummy so that a reference to it can be passed around.");
    }
    public int getProperty2And3() {
        throw new UnsupportedOperationException("This method is just a dummy so that a reference to it can be passed around.");
    }
}
