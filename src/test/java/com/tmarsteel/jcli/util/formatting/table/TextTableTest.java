package com.tmarsteel.jcli.util.formatting.table;

import com.tmarsteel.jcli.util.formatting.OutOfRenderingSpaceException;
import com.tmarsteel.jcli.util.formatting.multiline.MultilineTextStrategy;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TextTableTest
{
    private TextTable textTable = new TextTable();

    private MultilineTextStrategy multilineStrategyMock = mock(MultilineTextStrategy.class);

    @Before
    public void setUp() {
        textTable.setMultilineTextStrategy(multilineStrategyMock);

        doCallRealMethod().when(multilineStrategyMock).renderableOf(any());
        when(multilineStrategyMock.wrap(anyString(), anyInt(), anyChar())).thenAnswer(args -> {
            if (((String) args.getArgument(0)).length() <= (int) args.getArgument(1)) {
                return args.getArgument(0);
            }
            else throw new UnsupportedOperationException("Define wrapped text for width " + args.getArgument(1) + ", text = " + args.getArgument(0));
        });

        // common definitions
        doReturn("I exceed the\nassigned cell\nwidth and will\nhave to break")
            .when(multilineStrategyMock).wrap(
            "I exceed the assigned cell width and will have to break",
            15,
            '\n'
        );

        doReturn("Lorem ipsum\ndolor")
            .when(multilineStrategyMock).wrap("Lorem ipsum dolor", 15, '\n');
    }

    @Test
    public void testTableRendering_allColumnsDynamic_unevenSpaceDivision() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.addRow("I exceed the assigned cell width and will have to break", "Foooooobar", "trololo", "Lorem ipsum dolor");

        assertEquals(
            "+-----------------+-----------------+-----------------+-----------------+\n" +
            "| Spalte 1        | Spalte 2        | Spalte 3        | Spalte 4        |\n" +
            "+-----------------+-----------------+-----------------+-----------------+\n" +
            "| Foo             | Bar             | Bla             | Blub            |\n" +
            "+-----------------+-----------------+-----------------+-----------------+\n" +
            "| I exceed the    | Foooooobar      | trololo         | Lorem ipsum     |\n" +
            "| assigned cell   |                 |                 | dolor           |\n" +
            "| width and will  |                 |                 |                 |\n" +
            "| have to break   |                 |                 |                 |\n" +
            "+-----------------+-----------------+-----------------+-----------------+",
            textTable.render(73, '\n')
        );
    }

    @Test
    public void testTableRendering_allColumnsDynamic_evenSpaceDivision() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.addRow("I exceed the assigned cell width and will have to break", "Foooooobar", "trololo", "Lorem ipsum dolor");

        doReturn("Lorem ipsum\ndolor")
            .when(multilineStrategyMock).wrap("Lorem ipsum dolor", 14, '\n');

        assertEquals(
            "+-----------------+----------------+----------------+----------------+\n" +
                    "| Spalte 1        | Spalte 2       | Spalte 3       | Spalte 4       |\n" +
                    "+-----------------+----------------+----------------+----------------+\n" +
                    "| Foo             | Bar            | Bla            | Blub           |\n" +
                    "+-----------------+----------------+----------------+----------------+\n" +
                    "| I exceed the    | Foooooobar     | trololo        | Lorem ipsum    |\n" +
                    "| assigned cell   |                |                | dolor          |\n" +
                    "| width and will  |                |                |                |\n" +
                    "| have to break   |                |                |                |\n" +
                    "+-----------------+----------------+----------------+----------------+",
            textTable.render(70, '\n')
        );
    }

    @Test
    public void testTableRendering_oneDynamicColumn() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.addRow("I exceed the assigned cell width and will have to break", "Foooooobar", "trololo", "Lorem ipsum dolor");
        textTable.setColumnWidthCalculator((int index, int nColuns) -> {
            switch(index) {
                case 0: return -1;
                case 1: return 15;
                case 2: return 20;
                case 3: return 15;
                default: throw new IllegalArgumentException();
            }
        });

        doReturn("I exceed the assigned\ncell width and will\nhave to break")
            .when(multilineStrategyMock).wrap(
            "I exceed the assigned cell width and will have to break",
            22,
            '\n'
        );

        assertEquals(
            "+------------------------+-----------------+----------------------+-----------------+\n" +
                    "| Spalte 1               | Spalte 2        | Spalte 3             | Spalte 4        |\n" +
                    "+------------------------+-----------------+----------------------+-----------------+\n" +
                    "| Foo                    | Bar             | Bla                  | Blub            |\n" +
                    "+------------------------+-----------------+----------------------+-----------------+\n" +
                    "| I exceed the assigned  | Foooooobar      | trololo              | Lorem ipsum     |\n" +
                    "| cell width and will    |                 |                      | dolor           |\n" +
                    "| have to break          |                 |                      |                 |\n" +
                    "+------------------------+-----------------+----------------------+-----------------+",
            textTable.render(85, '\n')
        );
    }

    @Test
    public void testTableRendering_fixedAndMultipleDynamicColumns_evenSpaceDivision() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.addRow("I exceed the assigned cell width and will have to break", "Foooooobar", "trololo", "Lorem ipsum dolor");
        textTable.setColumnWidthCalculator((int index, int nColuns) -> {
            switch(index) {
                case 0: return -1;
                case 1: return -1;
                case 2: return 19;
                case 3: return 15;
                default: throw new IllegalArgumentException();
            }
        });

        doReturn("I exceed the\nassigned cell width\nand will have to\nbreak")
            .when(multilineStrategyMock).wrap(
            "I exceed the assigned cell width and will have to break",
            19,
            '\n'
        );

        assertEquals(
            "+---------------------+---------------------+---------------------+-----------------+\n" +
                    "| Spalte 1            | Spalte 2            | Spalte 3            | Spalte 4        |\n" +
                    "+---------------------+---------------------+---------------------+-----------------+\n" +
                    "| Foo                 | Bar                 | Bla                 | Blub            |\n" +
                    "+---------------------+---------------------+---------------------+-----------------+\n" +
                    "| I exceed the        | Foooooobar          | trololo             | Lorem ipsum     |\n" +
                    "| assigned cell width |                     |                     | dolor           |\n" +
                    "| and will have to    |                     |                     |                 |\n" +
                    "| break               |                     |                     |                 |\n" +
                    "+---------------------+---------------------+---------------------+-----------------+",
            textTable.render(85, '\n')
        );
    }

    @Test
    public void testTableRendering_fixedAndMultipleDynamicColumns_unevenSpaceDivision() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.addRow("I exceed the assigned cell width and will have to break", "Foooooobar", "trololo", "Lorem ipsum dolor");
        textTable.setColumnWidthCalculator((int index, int nColuns) -> {
            switch(index) {
                case 0: return -1;
                case 1: return -1;
                case 2: return 18;
                case 3: return 15;
                default: throw new IllegalArgumentException();
            }
        });

        doReturn("I exceed the\nassigned cell width\nand will have to\nbreak")
            .when(multilineStrategyMock).wrap(
            "I exceed the assigned cell width and will have to break",
            20,
            '\n'
        );

        assertEquals(
            "+----------------------+---------------------+--------------------+-----------------+\n" +
                    "| Spalte 1             | Spalte 2            | Spalte 3           | Spalte 4        |\n" +
                    "+----------------------+---------------------+--------------------+-----------------+\n" +
                    "| Foo                  | Bar                 | Bla                | Blub            |\n" +
                    "+----------------------+---------------------+--------------------+-----------------+\n" +
                    "| I exceed the         | Foooooobar          | trololo            | Lorem ipsum     |\n" +
                    "| assigned cell width  |                     |                    | dolor           |\n" +
                    "| and will have to     |                     |                    |                 |\n" +
                    "| break                |                     |                    |                 |\n" +
                    "+----------------------+---------------------+--------------------+-----------------+",
            textTable.render(85, '\n')
        );
    }

    @Test(expected = OutOfRenderingSpaceException.class)
    public void testTableRendering_shouldRefuseToRenderWihtoutSufficientSpace() {
        textTable.setHeadings("Spalte 1", "Spalte 2", "Spalte 3", "Spalte 4");
        textTable.addRow("Foo", "Bar", "Bla", "Blub");
        textTable.setColumnWidthCalculator((int index, int nColuns) -> {
            switch(index) {
                case 0: return 5;
                case 1: return 5;
                case 2: return 5;
                case 3: return 5;
                default: throw new IllegalArgumentException();
            }
        });

        // all the columns add up to 20; if the user forgets about padding and borders there is not enough space
        textTable.render(20, '\n');
    }
}
