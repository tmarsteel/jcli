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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CLIHelptextFormatterTest
{
    @Test
    public void testWrap() {
        CLIHelptextFormatter formatter = new CLIHelptextFormatter();
        formatter.setMaxWidth(20);
        String result = formatter.wrap(
            "This is a text that exceeds the line limit of this formatter by far."
        );

        assertEquals(result, "This is a text that\n" +
                "exceeds the line\n" +
                "limit of this\n" +
                "formatter by far.");
    }
}
