/* 
 * Copyright (C) 2015 Tobias Marstaller
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
package com.tmarsteel.jcli.examples;

import com.tmarsteel.jcli.validation.Validator;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.ParseException;
import com.tmarsteel.jcli.validation.ValidationException;
import com.tmarsteel.jcli.validation.configuration.XMLValidatorConfigurator;
import java.io.IOException;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

class Examples {
    public static void main(String[] args) {
        Validator inputValidator = new Validator();
        Validator.ValidatedInput input;
    
        try
        {
            (XMLValidatorConfigurator.getInstance(
                Examples.class.getResourceAsStream("example-config.xml"),
                Environment.UNIX
            )).configure(inputValidator);
            

            input = inputValidator.parse(new String[] {
                "--e", "enc",
                "--input", "1",
                "--input", "2"
            });
        }
        catch (SAXException | IOException ex)
        {
            System.err.println("Failed to load internal configuration");
            System.err.println(ex);
            System.exit(1);
            return;
        }
        catch (ParseException | ValidationException ex)
        {
            System.err.println("Please check your input:");
            System.err.println(ex.getMessage());
            System.exit(1);
            return;
        }
        
        assertFalse(input.isFlagSet("verbose"));
        assertEquals(input.getOption("encoding"), "enc");
        assertArrayEquals((Object[]) input.getOption("input"), new Long[]{ 1L, 2L });
    }
}