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

/**
 * Reselbmes a command as used by the {@link CommandDispatcher}
 */
public interface Command<R> {
    /**
     * Executes this command with the given arguments
     * @param args
     * @return The result of this command, if any. Otherwise null.
     * @throws ValidationException If validating the given arguments fails.
     * @throws Exception Any exception that occurs during execution.
     */
    R execute(String[] args) throws ValidationException, Exception;
}
