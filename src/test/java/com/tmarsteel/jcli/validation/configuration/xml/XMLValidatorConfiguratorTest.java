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

import com.tmarsteel.jcli.Flag;
import com.tmarsteel.jcli.Option;
import com.tmarsteel.jcli.validation.Validator;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;
import java.util.function.Predicate;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Tobias Marstaller
 */
@Ignore
public class XMLValidatorConfiguratorTest
{
    private Validator spyValidator;
    private XMLValidatorConfigurator subject;

    @Before
    public void setUp() throws Exception {
        spyValidator = spy(new Validator());
        subject = XMLValidatorConfigurator.getInstance(
            getClass().getResourceAsStream("testconfig.xml")
        );
    }

    @Test
    public void shouldFindFlags() {
        // ACT
        subject.configure(spyValidator);

        // ASSERT
        verify(spyValidator, times(2)).add(notNull(Flag.class));
    }

    @Test
    public void validateParsedFlag() {
        // ACT
        subject.configure(spyValidator);

        // ASSERT
        assertTrue(
            "flag1 missing",
            containsMatching(spyValidator.flags(), f -> f.getPrimaryIdentifier().equals("flag1"))
        );
        assertTrue(
            "flag2 missing or incorrect",
            containsMatching(spyValidator.flags(), f -> f.getPrimaryIdentifier().equals("flag2") && f.isIdentifiedBy("f2"))
        );
    }

    @Test
    public void shouldFindOptions() {
        // ACT
        subject.configure(spyValidator);

        // ASSERT
        verify(spyValidator, times(4)).add(notNull(Option.class));
    }

    @Test
    public void validateParsedOption() {
        // ACT
        subject.configure(spyValidator);

        // ASSERT
        // option 1
        assertTrue(
            "option1 missing",
            containsMatching(spyValidator.options(), o -> o.getPrimaryIdentifier().equals("option1") && o.isRequired())
        );
        // option 2
        assertTrue(
            "option2 missing or incorrect",
            containsMatching(spyValidator.options(), o -> o.getPrimaryIdentifier().equals("option2") && o.isIdentifiedBy("o2"))
        );
        // option 3
        assertTrue(
            "option3 missing or incorrect",
            containsMatching(spyValidator.options(), o ->
                o.getPrimaryIdentifier().equals("option3") &&
                o.allowsMultipleValues() &&
                !o.isRequired()
        ));
        // option 4
        assertTrue(
            "option4 is missing or incorrect",
            containsMatching(spyValidator.options(), o ->
                o.getPrimaryIdentifier().equals("option4") &&
                o.isIdentifiedBy("o4")
            )
        );
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

    private <E> boolean containsMatching(Iterator<E> it, Predicate<? super E> predicate) {
        while (it.hasNext()) {
            if (predicate.test(it.next())) {
                return true;
            }
        }

        return false;
    }
}
