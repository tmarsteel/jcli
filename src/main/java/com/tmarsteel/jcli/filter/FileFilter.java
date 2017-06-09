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
package com.tmarsteel.jcli.filter;

import com.tmarsteel.jcli.validation.ValidationException;
import java.io.File;

/**
 * Accepts values that to a file or directory meeting the specified requirements.
 */
public class FileFilter implements java.io.FileFilter, Filter
{
    public enum EXISTENCE
    {
        MUST_EXIST,
        MUST_NOT_EXIST,
        IRRELEVANT;

        /**
         * Checks whether the given file fulfills the requirement and throws
         * and exception if that is not the case.
         * @param file The file to test
         * @throws ValidationException If the given file does not fulfill
         */
        public void test(File file)
            throws ValidationException
        {
            switch (this)
            {
                case MUST_EXIST:
                    if (!file.exists())
                    {
                        throw new ValidationException("file " + file.getPath() +
                            " not found");
                    }
                    break;
                case MUST_NOT_EXIST:
                    if (file.exists())
                    {
                        throw new ValidationException("file must not exist.");
                    }
                    break;
            }
        }
    }

    public enum PERMISSION
    {
        EXECUTE             (false, false, true ),
        READ                (true,  false, false),
        WRITE               (false, true,  false),
        READ_EXECUTE        (true,  false, true ),
        WRITE_EXECUTE       (false, true,  true ),
        READ_WRITE          (true,  true,  false),
        READ_WRITE_EXECUTE  (true,  true,  true ),
        IRRELEVANT          (false, false, false);

        /**
         * Whether read permission is required
         */
        public final boolean readR;

        /**
         * Whether write permission is required
         */
        public final boolean writeR;

        /**
         * Whether execute/list permission is required
         */
        public final boolean execR;

        PERMISSION(boolean readR, boolean writeR, boolean execR)
        {
            this.readR = readR;
            this.writeR = writeR;
            this.execR = execR;
        }

        /**
         * Checks whether the given file fulfills the requirement and throws
         * and exception if that is not the case.
         * @param f The file to test
         * @throws ValidationException If the given file does not fulfill
         */
        public void test(File f)
            throws ValidationException
        {
            if (readR && !f.canRead())
            {
                throw new ValidationException("not readable");
            }
            if (writeR && !f.canWrite())
            {
                throw new ValidationException("not writeable");
            }
            // non-existant files will always return false on this
            if (execR && !f.canExecute() && f.exists())
            {
                throw new ValidationException("cannot execute / list");
            }
        }
    }

    public enum TYPE
    {
        FILE,
        DIRECTORY,
        IRRELEVANT;

        /**
         * Checks whether the given file fulfills the requirement and throws
         * and exception if that is not the case.
         * @param file The file to test
         * @throws ValidationException If the given file does not fulfill
         */
        public void test(File file)
            throws ValidationException
        {
            if (this != IRRELEVANT)
            {
                if (!file.exists())
                {
                    return;
                }
                switch (this)
                {
                    case FILE:
                        if (!file.isFile())
                        {
                            throw new ValidationException("needs to be a file");
                        }
                        break;
                    case DIRECTORY:
                        if (!file.isDirectory())
                        {
                            throw new ValidationException("needs to be a directory");
                        }
                        break;
                }
            }
        }
    }

    protected java.io.FileFilter filter = null;
    protected EXISTENCE existenceState = EXISTENCE.IRRELEVANT;
    protected PERMISSION permissionState = PERMISSION.IRRELEVANT;
    protected TYPE fileType = TYPE.IRRELEVANT;
    protected String extension = null;

    /**
     * Creates a new filter that validates using the given {@link java.io.FileFilter}.
     * @param filter The filter to validate with
     */
    public FileFilter(java.io.FileFilter filter)
    {
        this.filter = filter;
    }

    public FileFilter() {}

    @Override
    public boolean accept(File pathname)
    {
        try
        {
            assertSuffices(pathname);
            return true;
        }
        catch (ValidationException ex)
        {
            return false;
        }
    }

    @Override
    public Object parse(String value)
        throws ValidationException
    {
        File f = new File(value);
        assertSuffices(f);
        
        return f;
    }

    /**
     * Checks whether the given file fulfills all the requirements of this filter
     * and throws an exception if that is not the case.
     * @param file The file to check
     * @throws ValidationException If the given file does not fulfill the requirements
     * of this filter.
     */
    public void assertSuffices(File file)
        throws ValidationException
    {
        if (filter == null)
        {
            existenceState.test(file);
            fileType.test(file);
            permissionState.test(file);
            if (extension != null)
            {
                String fName = file.getName();
                
                // if the name ends with a dot, the substring will fail
                if (fName.lastIndexOf('.') == -1 ||
                    fName.endsWith(".") ||
                    !(fName.substring(fName.lastIndexOf('.') + 1).equalsIgnoreCase(extension)))
                {
                    throw new ValidationException("extension needs to be " + extension);
                }
            }
        }
        else
        {
            if (!filter.accept(file))
            {
                throw new ValidationException("invalid file");
            }
        }
    }

    /**
     * Returns the name extension / suffix this filter requires or null if none is set.
     */
    public String getExtension()
    {
        return extension;
    }

    /**
     * Sets the name extension / suffix to require. Set to null to remove the constraint.
     */
    public void setExtension(String extension)
    {
        // cut leading .
        if (extension.charAt(0) == '0')
        {
            extension = extension.substring(1);
        }
        
        this.extension = extension;
    }

    public PERMISSION getPermissions()
    {
        return permissionState;
    }

    public void setPermissions(PERMISSION permissionStatus)
    {
        this.permissionState = permissionStatus;
    }

    public EXISTENCE getExistenceState()
    {
        return existenceState;
    }

    public void setExistenceState(EXISTENCE EXISTENCEStatus)
    {
        this.existenceState = EXISTENCEStatus;
    }

    public TYPE getFileType()
    {
        return fileType;
    }

    public void setFileType(TYPE fileType)
    {
        this.fileType = fileType;
    }
}
