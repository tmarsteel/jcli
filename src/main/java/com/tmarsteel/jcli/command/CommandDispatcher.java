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

import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Dispatches full {@code String[] args} values to instances of {@link Command}.
 */
public class CommandDispatcher<R> implements Command<R> {

    private Map<String,Command<R>> commands = new HashMap<>();

    /**
     * Aliased commands
     */
    private Map<String,String> aliases = new HashMap<>();

    public void add(String name, Command<R> command) {
        if (commands.get(name) != null) {
            throw new IllegalStateException("A command with the name " + name + " has already been added.");
        }

        commands.put(name, command);
    }

    /**
     * Treats the first entry in {@code args} as the command name. Looks up to command with the given name, parses
     * the rest of the arguments (excluding the first entry) and attempts to execute the command.
     * @param args The arguments as passed to {@code main(String[])}.
     * @return Whatever the command returns.
     * @throws Exception Passes every exception that arises during the dispatch and execution
     */
    public R dispatch(String[] args) throws CommandDispatchException, NoSuchCommandException, ParseException, ValidationException, Exception {
        if (args.length == 0 || args[0] == null) {
            throw new CommandDispatchException("No command specified.");
        }

        Command<R> command = getCommand(args[0]);

        String[] newArgs = new String[args.length - 1];
        if (newArgs.length > 0) {
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
        }

        return command.execute(newArgs);
    }

    public R execute(String[] args) throws ValidationException, Exception {
        return dispatch(args);
    }

    /**
     * Add a command alias
     * @param command The target command; must have been added previously to the call to this method
     * @param alias The alias command name
     * @throws NoSuchCommandException If the given command name is not known
     */
    public void alias(String command, String alias) throws NoSuchCommandException
    {
        add(alias, getCommand(command));
    }

    private Command<R> getCommand(String name) throws NoSuchCommandException {
        if (commands.containsKey(name)) {
            return commands.get(name);
        } else {
            throw new NoSuchCommandException(name);
        }
    }
}
