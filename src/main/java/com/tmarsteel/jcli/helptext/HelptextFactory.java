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

import com.tmarsteel.jcli.validation.Validator;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;

/**
 * Helper methods for {@link Helptext} construction
 */
public class HelptextFactory
{
    public static Helptext getInstance(Validator validator) {
        Helptext helptext = new Helptext();
        validator.options().forEachRemaining(helptext.options()::add);
        validator.flags().forEachRemaining(helptext.flags()::add);
        validator.arguments().forEachRemaining(helptext.arguments()::add);
        return helptext;
    }
}
