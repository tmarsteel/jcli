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
package com.tmarsteel.jcli.validation.configuration.xml;

import com.tmarsteel.jcli.validation.Validator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * @author Tobias Marstaller
 */
@Ignore
public class XMLValidatorConfiguratorTest
{
    private Validator mockValidator;

    @Before
    public void setUp() {
        mockValidator = mock(Validator.class);
    }

    @Test
    public void shouldFindFlags() {
        // TODO: implement
    }

    @Test
    public void validateParsedFlag() {
        // TODO: implement
    }

    @Test
    public void shouldFindOptions() {
        // TODO: implement
    }

    @Test
    public void validateParsedOption() {
        // TODO: implement
    }

    @Test
    public void shouldFindOptionFilter() {
        // TODO: implement
    }

    @Test
    public void shouldFindArguments() {
        // TODO: implement
    }

    @Test
    public void validateParsedArgument() {
        // TODO: implement
    }

    @Test
    public void souldFindArgumentFilter() {
        // TODO: implement
    }

    @Test
    public void shouldFindRules() {
        // TODO: implement
    }
}
