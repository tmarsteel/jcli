package com.wisper.cli.filter;

import com.wisper.cli.ParseException;
import java.io.File;

/**
 * Accepts values that to a file or directory meeting the specified requirements.
 * @author Tobias Marstaller
 */
public class FileFilter implements java.io.FileFilter, ValueFilter
{
    public enum EXISTANCE
    {
        MUST_EXIST,
        MUST_NOT_EXIST,
        IRRELEVANT;
        
        public void test(File file)
            throws ParseException
        {
            switch (this)
            {
                case MUST_EXIST:
                    if (!file.exists())
                    {
                        throw new ParseException("file " + file.getPath() +
                            " not found");
                    }
                    break;
                case MUST_NOT_EXIST:
                    if (file.exists())
                    {
                        throw new ParseException("file must not exist.");
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
        
        protected boolean readR;
        protected boolean writeR;
        protected boolean execR;
        
        PERMISSION(boolean readR, boolean writeR, boolean execR)
        {
            this.readR = readR;
            this.writeR = writeR;
            this.execR = execR;
        }
        
        public void test(File f)
            throws ParseException
        {
            if (readR && !f.canRead())
            {
                throw new ParseException("not readable");
            }
            if (writeR && !f.canWrite())
            {
                throw new ParseException("not writeable");
            }
            // non-existant files will always return false on this
            if (execR && !f.canExecute() && f.exists())
            {
                throw new ParseException("cannot execute / list");
            }
        }
    }
    
    public enum TYPE
    {
        FILE,
        DIRECTORY,
        IRRELEVANT;
        
        public void test(File file)
            throws ParseException
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
                            throw new ParseException("needs to be a file");
                        }
                        break;
                    case DIRECTORY:
                        if (!file.isDirectory())
                        {
                            throw new ParseException("needs to be a directory");
                        }
                        break;
                }
            }
        }
    }
    
    protected java.io.FileFilter filter = null;
    protected EXISTANCE existanceStatus = EXISTANCE.IRRELEVANT;
    protected PERMISSION permissionStatus = PERMISSION.IRRELEVANT;
    protected TYPE fileType = TYPE.IRRELEVANT;
    protected String extension = null;
    
    
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
            parse(pathname);
            return true;
        }
        catch (ParseException ex)
        {
            return false;
        }
    }

    @Override
    public Object parse(String value)
        throws ParseException
    {
        return parse(new File(value));
    }
    
    public File parse(File file)
        throws ParseException
    {
        if (filter == null)
        {
            existanceStatus.test(file);
            fileType.test(file);
            permissionStatus.test(file);
            if (extension != null)
            {
                if (!(file.getName().substring(file.getName().length() - extension.length()).equalsIgnoreCase(extension)))
                {
                    throw new ParseException("extension needs to be " + extension);
                }
            }
            return file;
        }
        else
        {
            if (filter.accept(file))
            {
                return file;
            }
            else
            {
                throw new ParseException("invalid file");
            }
        }
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public PERMISSION getPermissions()
    {
        return permissionStatus;
    }

    public void setPermissions(PERMISSION permissionStatus)
    {
        this.permissionStatus = permissionStatus;
    }

    public EXISTANCE getExistanceStatus()
    {
        return existanceStatus;
    }

    public void setExistanceStatus(EXISTANCE existanceStatus)
    {
        this.existanceStatus = existanceStatus;
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
