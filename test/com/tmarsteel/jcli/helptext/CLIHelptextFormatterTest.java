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
    public void setUpHelptext() {
        helptext = new Helptext();
        helptext.usageExamples().add("--flag -option value argument");
        helptext.setExecutableName("smapleProgram");
        helptext.setProgramDescription("This is a description of the program. It is intended to exceed the 80 character line mimit in order to test the text wrapping functionality.");
        helptext.setNotes("Some more notes on the program. This, too, should exceed the line length to test wrapping.");

        Option o1 = new Option("option1", "o1", "o");
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

        Argument a1 = new Argument("arg1", 0);
        a1.setDescription("Some tedious, precise description of the argument including format, default values and requirements...");
        Argument a2 = new Argument("arg2", 1);
        a2.setDescription("Some tedious, precise description of the argument including format, default values and requirements...");
    }

    @Test
    public void testA() {
        String text = subject.format(helptext);

        assertEquals(text,
            "Usage: smapleProgram [-flags] [--options values] arguments...\n" +
                    "                     --flag -option value argument\n" +
                    "This is a description of the program. It is intended to exceed the 80 character\n" +
                    "line mimit in order to test the text wrapping functionality.\n" +
                    "\n" +
                    "-- Options --\n" +
                    "option1  Some tedious, precise description of the option including format,\n" +
                    "o1       default values and requirements...\n" +
                    "o \n" +
                    "\n" +
                    "option2  Some tedious, precise description of the option including format,\n" +
                    "o2       default values and requirements...\n" +
                    "\n" +
                    "\n" +
                    "-- Flags --\n" +
                    "flag2  Some tedious, precise description of the flag including format, default\n" +
                    "f2     values and requirements...\n" +
                    "\n" +
                    "flag1  Some tedious, precise description of the flag including format, default\n" +
                    "f1     values and requirements...\n" +
                    "f \n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "Some more notes on the program. This, too, should exceed the line length to test\n" +
                    "wrapping.\n"
        );
    }

    public static void main(String... args) {
        CLIHelptextFormatterTest x = new CLIHelptextFormatterTest();
        x.setUpSubject();
        x.setUpHelptext();
        x.testA();
    }
}
