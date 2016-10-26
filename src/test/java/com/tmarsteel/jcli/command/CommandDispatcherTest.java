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

package com.tmarsteel.jcli.command;

import com.tmarsteel.jcli.validation.ValidationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CommandDispatcherTest
{
    private CommandDispatcher<Object> subject;
    private Command<Object> testCommand;

    @Before
    public void setUp() {
        subject = new CommandDispatcher<>();
        testCommand = mock(Command.class);
        subject.add("test", testCommand);
    }

    @Test(expected= ValidationException.class)
    public void dispatchShouldErrorOnEmptyArray() throws Exception {
        subject.dispatch(new String[]{});
    }

    @Test(expected= ValidationException.class)
    public void dispatchShouldErrorOnNullFirstEntry() throws Exception {
        subject.dispatch(new String[]{null});
    }

    @Test(expected= ValidationException.class)
    public void dispatchShouldErrorOnUnknownCommand() throws Exception {
        subject.dispatch(new String[]{"unknown"});
    }

    @Test
    public void dispatchShouldCallCommandExecute() throws Exception {
        doReturn(null).when(testCommand).execute(any());

        subject.dispatch(new String[]{"test"});

        verify(testCommand).execute(any());
    }

    @Test
    public void dispatchShouldPassCorrectArguments() throws Exception {
        doReturn(null).when(testCommand).execute(any());

        subject.dispatch(new String[]{"test", "a", "b", "c"});

        verify(testCommand).execute(new String[]{"a", "b", "c"});
    }

    @Test
    public void dispatchShouldPassReturnValue() throws Exception {
        Object expectedReturn = new Object();
        doReturn(expectedReturn).when(testCommand).execute(any());

        Object result = subject.dispatch(new String[]{"test"});

        assertSame(expectedReturn, result);
    }

    @Test
    public void dispatchShouldPassException() throws Exception {
        Exception expected = new Exception("Fake reason");
        doThrow(expected).when(testCommand).execute(any());

        try {
            subject.dispatch(new String[]{"test"});
            fail("Did not pass exception");
        } catch (Exception ex) {
            assertSame(expected, ex);
        }
    }

    @Test
    public void Issue10_dispatchShouldThrowUnambigousNoSuchCommand() throws Exception {
        Command mockCommand = mock(Command.class);
        NoSuchCommandException nestedEx = new NoSuchCommandException("subcommand");
        doThrow(nestedEx).when(mockCommand).execute(any());

        subject.add("topcommand", mockCommand);

        try {
            subject.dispatch(new String[]{"topcommand", "subcommand"});
            fail("NoSuchCommandException was expected but not thrown");
        }
        catch (NoSuchCommandException ex) {
            assertTrue("Invalid exception message, see issue #10", ex.getMessage().indexOf("topcommand subcommand") != -1);
        }
    }
}
