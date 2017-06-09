package com.tmarsteel.jcli.util.formatting;

import com.tmarsteel.jcli.util.formatting.multiline.WordsplitMultilineStrategy;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ListFormatterTest
{
    private ListFormatter subject;
    private Collection<String> items = Stream.of("Item 1", "Item 2", "Item 3").collect(Collectors.toList());

    @Before
    public void setUp() {
        subject = new ListFormatter("-", WordsplitMultilineStrategy.getInstance());
    }

    @Test
    public void test_simple() {
        // ACT
        String result = subject.format(items, 20, '\n');

        // ASSERT
        assertEquals(
            "- Item 1\n- Item 2\n- Item 3",
            result
        );
    }

    @Test
    public void test_bulletpoint() {
        // SETUP
        subject.setBulletPoint("->");

        // ACT
        String result = subject.format(items, 20, '\n');

        // ASSERT
        assertEquals(
            "-> Item 1\n-> Item 2\n-> Item 3",
            result
        );
    }

    @Test
    public void test_Wrap() {
        // SETUP
        items.add("This item exceeds the line limit");

        // ACT
        String result = subject.format(items, 20, '\n');

        // ASSERT
        assertEquals(
            "- Item 1\n- Item 2\n- Item 3\n- This item exceeds\n  the line limit",
            result
        );
    }
}
