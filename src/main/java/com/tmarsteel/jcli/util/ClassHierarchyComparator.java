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
package com.tmarsteel.jcli.util;

import java.util.Comparator;

/**
 * Sorts {@link java.lang.Class}es by their hierarchy. More specific &gt; less specific.
 * @param <T> Constraint on the classes passed to the comparator.
 */
public class ClassHierarchyComparator<T> implements Comparator<Class<? extends T>>
{
    @Override
    public int compare(Class<? extends T> cls1, Class<? extends T> cls2) {
        if (cls1.equals(cls2)) {
            return 0;
        }
        if (cls1.isAssignableFrom(cls2)) {
            return 1;
        } else {
            return -1;
        }
    }
}
