package com.tmarsteel.jcli.util.formatting;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IndentationTest
{
    private Indentation subject;

    @Before
    public void setUp() {
        subject = new Indentation(' ', 2);
    }

    @Test
    public void test_NoIndentation() {
        // SETUP
        String testString = "I am some string and i should not be altered.";

        // ACT
        String result = subject.indent(testString, Indentation.Strategy.NO_INDENTATION, '\n');

        // ASSERT
        assertEquals(testString, result);
    }

    @Test
    public void test_All() {
        // SETUP
        String testString = "I am some string that\ncontains multiple lines.";

        // ACT
        String result = subject.indent(testString, Indentation.Strategy.INDENT_ALL, '\n');

        // ASSERT
        assertEquals(
            "  I am some string that\n  contains multiple lines.",
            result
        );
    }

    @Test
    public void test_SecondToLast() {
        // SETUP
        String testString = "I am some string that\ncontains multiple\nlines.";

        // ACT
        String result = subject.indent(testString, Indentation.Strategy.INDENT_SECOND_TO_LAST, '\n');

        // ASSERT
        assertEquals(
            "I am some string that\n  contains multiple\n  lines.",
            result
        );
    }
}
