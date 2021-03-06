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
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;

import java.io.File;

/**
 * Accepts values and maps it to a {@link java.nio.file.Path}. Optionally,
 * validates (non-)existence and access permissions against the local filesystem.
 */
public class PathFilter implements Filter {

    private FileFilter delegate;

    public PathFilter(FileFilter delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object parse(String value)
            throws ValidationException
    {
        return ((File) delegate.parse(value)).toPath();
    }

    /**
     * Returns the underlying {@link FileFilter} this filter uses to validate inputs.
     */
    public FileFilter getDelegate() {
        return this.delegate;
    }
}
