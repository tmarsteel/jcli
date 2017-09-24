package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.Renderable;
import com.tmarsteel.jcli.util.formatting.table.builder.ColumnHeading;
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

        String tableAsString = table.render(55, '\n');
        assertEquals(
            "+-------------+-------------+------------+------------+\n" +
                "| Baaar       | Foo         | Number     | Renderable |\n" +
                "+-------------+-------------+------------+------------+\n" +
                "| bar 1       | Foo1        | 1          | 10 10      |\n" +
                "+-------------+-------------+------------+------------+\n" +
                "| bar 2       | Foo2        | 2          | 10 10      |\n" +
                "+-------------+-------------+------------+------------+\n" +
                "| bar 3       | Foo3        | 3          | 10 10      |\n" +
                "+-------------+-------------+------------+------------+",
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

        @ColumnHeading("Baaar")
        public String getBar()
        {
            return bar;
        }

        public int getNumber()
        {
            return number;
        }

        public Object getRenderable() { return (Renderable) (maxWidth, lineSeparator) -> maxWidth + " " + (short) lineSeparator; }
    }
}
