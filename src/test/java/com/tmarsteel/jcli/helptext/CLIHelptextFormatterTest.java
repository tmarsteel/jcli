/*
 * Copyright (C) 2016 Tobias Marstaller
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.tmarsteel.jcli.helptext;

import com.tmarsteel.jcli.Argument;
import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.filter.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLIHelptextFormatterTest
{
    private CLIHelptextFormatter subject;
    private Helptext helptext;

    @Before
    public void setUpSubject()
    {
        subject = new CLIHelptextFormatter();
    }

    @Before
    public void setUpHelptext()
    {
        helptext = new Helptext();
        helptext.usageExamples().add("--flag -option value argument");
        helptext.setExecutableName("sampleProgram");
        helptext.setProgramDescription("This is a description of the program. It is intended to exceed the 80 character line mimit in order to test the text wrapping functionality.");
        helptext.setNotes("Some more notes on the program. This, too, should exceed the line length to test wrapping.");

        Option o1 = new Option(new BigIntegerFilter(), null, "option1", "o1", "o");
        o1.setDescription("Some tedious, precise description of the option including format, default values and requirements...");
        Option o2 = new Option("option2", "o2");
        o2.setDescription("Some tedious, precise description of the option including format, default values and requirements...");
        helptext.options().add(o1);
        helptext.options().add(o2);

        Flag f1 = new Flag("flag1", "f1", "f");
        f1.setDescription("Some tedious, precise description of the flag including format, default values and requirements...");
        Flag f2 = new Flag("flag2", "f2");
        f2.setDescription("Some tedious, precise description of the flag including format, default values and requirements...");
        helptext.flags().add(f1);
        helptext.flags().add(f2);

        Argument a1 = new Argument("arg1", 0, new IntegerFilter(16));
        a1.setDescription("Some tedious, precise description of the argument including format, default values and requirements...");
        Argument a2 = new Argument("arg2", 1, new DecimalFilter(0.0, Math.PI));
        a2.setDescription("Some tedious, precise description of the argument including format, default values and requirements...");
        helptext.arguments().add(a1);
        helptext.arguments().add(a2);

        // these arguments are merely here to test filter descriptors
        Argument a3 = new Argument("arg3", 2, new SetFilter(true, "aasdfa", "bsss", "f", "h"));
        a3.setDescription("SetFilterTest");
        helptext.arguments().add(a3);

        RegexFilter regexFilter = new RegexFilter("a(.+)");
        regexFilter.setReturnGroup(1);
        Argument a4 = new Argument("arg4", 3, regexFilter);
        a4.setDescription("RegexFilterTest");
        helptext.arguments().add(a4);

        Argument a5 = new Argument("arg5", 4, new MetaRegexFilter());
        a5.setDescription("MetaRegexFilterTest");
        helptext.arguments().add(a5);
    }

    @Test
    public void testA()
    {
        subject.useDefaultFilterDescriptors();
        String text = subject.format(helptext);

        assertEquals(
            "Usage: sampleProgram [-flags] [--options values] : arguments...\n" +
                "                     --flag -option value argument\n" +
                "This is a description of the program. It is intended to exceed the 80 character\n" +
                "line mimit in order to test the text wrapping functionality.\n" +
                "\n" +
                "-- Options --\n" +
                "option1  Some tedious, precise description of the option including format,\n" +
                "o1       default values and requirements...\n" +
                "o        \n" +
                "         Constraints:\n" +
                "         - must be an integer number\n" +
                "\n" +
                "option2  Some tedious, precise description of the option including format,\n" +
                "o2       default values and requirements...\n" +
                "\n" +
                "\n" +
                "-- Flags --\n" +
                "flag1  Some tedious, precise description of the flag including format, default\n" +
                "f1     values and requirements...\n" +
                "f \n" +
                "\n" +
                "flag2  Some tedious, precise description of the flag including format, default\n" +
                "f2     values and requirements...\n" +
                "\n" +
                "\n" +
                "-- Arguments --\n" +
                "#0  Some tedious, precise description of the argument including format, default\n" +
                "    values and requirements...\n" +
                "    \n" +
                "    Constraints:\n" +
                "    - must be an integer number\n" +
                "    - must be specified in base 16\n" +
                "    - must be between -8000000000000000 and 7fffffffffffffff inclusive\n" +
                "\n" +
                "#1  Some tedious, precise description of the argument including format, default\n" +
                "    values and requirements...\n" +
                "    \n" +
                "    Constraints:\n" +
                "    - must be a number\n" +
                "    - must be between 0.0 and 3.141592653589793 inclusive\n" +
                "\n" +
                "#2  SetFilterTest\n" +
                "    \n" +
                "    Constraints:\n" +
                "    - must be one of the following options (case sensitive):\n" +
                "    - aasdfa\n" +
                "    - bsss\n" +
                "    - f\n" +
                "    - h\n" +
                "\n" +
                "#3  RegexFilterTest\n" +
                "    \n" +
                "    Constraints:\n" +
                "    - must match this regular expression: a(.+)\n" +
                "    - group 1 is relevant\n" +
                "\n" +
                "#4  MetaRegexFilterTest\n" +
                "    \n" +
                "    Constraints:\n" +
                "    - must be a valid regular expression\n" +
                "\n" +
                "\n" +
                "\n" +
                "Some more notes on the program. This, too, should exceed the line length to test\n" +
                "wrapping.\n",
            text
        );
    }
}
