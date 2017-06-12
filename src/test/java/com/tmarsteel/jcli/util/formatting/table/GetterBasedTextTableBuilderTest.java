package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.table.builder.TextTableBuilder;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class GetterBasedTextTableBuilderTest
{
    @Test
    public void test() {
        TextTable table = TextTableBuilder.byGettersOf(TestPojo.class)
            .build(asList(
               new TestPojo("Foo1", "bar 1", 1),
                new TestPojo("Foo2", "bar 2", 2),
                new TestPojo("Foo3", "bar 3", 3)
            ));

        String tableAsString = table.render(30, '\n');
        assertEquals(
            "+---------+---------+--------+\n" +
                "| Bar     | Foo     | Number |\n" +
                "+---------+---------+--------+\n" +
                "| bar 1   | Foo1    | 1      |\n" +
                "+---------+---------+--------+\n" +
                "| bar 2   | Foo2    | 2      |\n" +
                "+---------+---------+--------+\n" +
                "| bar 3   | Foo3    | 3      |\n" +
                "+---------+---------+--------+",
            tableAsString
        );
    }

    /**
     * The test table is build on this class
     */
    public static class TestPojo {
        private String foo;
        private String bar;
        private int number;

        public TestPojo(String foo, String bar, int number)
        {
            this.foo = foo;
            this.bar = bar;
            this.number = number;
        }

        public String getFoo()
        {
            return foo;
        }

        public String getBar()
        {
            return bar;
        }

        public int getNumber()
        {
            return number;
        }
    }
}
