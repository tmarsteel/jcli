package com.tmarsteel.jcli.util.formatting.multiline;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WordsplitMultilineStrategyTest
{
    @Test
    public void test() {
        // SETUP
        String testString = "This is a text that, by far, exceeds the line limit and should be split into 3 separate lines.";

        // ACT
        String result = WordsplitMultilineStrategy.getInstance().wrap(
            testString,
            40,
            '\n'
        );

        // ASSERT
        assertEquals(
            "This is a text that, by far, exceeds the\n" +
            "line limit and should be split into 3\n" +
            "separate lines.",
            result
        );
    }
}
