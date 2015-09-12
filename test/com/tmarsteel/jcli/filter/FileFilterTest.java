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
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * @author Tobias Marstaller
 */
public class FileFilterTest
{
    public static class EXISTANCETest
    {
        @Test
        public void MUST_EXISTshouldSucceedOnExistingFile()
            throws ValidationException
        {
            File f = mock(File.class);
            
            when(f.exists()).thenReturn(true);
            
            FileFilter.EXISTANCE.MUST_EXIST.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void MUST_EXISTshouldFailOnNonExistingFile()
            throws ValidationException
        {
            File f = mock(File.class);
            
            when(f.exists()).thenReturn(false);
            
            FileFilter.EXISTANCE.MUST_EXIST.test(f);
        }
    }
    
    // The permissions in the method names are noted in unix-like fashion:
    // RWX = Read, Write, Execute
    // r_x = Read, Execute
    // _w_ = Write
    // and so on...
    public static class PERMISSIONTest
    {
        @Test
        public void EXECUTEshouldSucceedOn___X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.EXECUTE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void EXECUTEshouldFailOn_RW_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.EXECUTE.test(f);
        }
        
        
        @Test
        public void READshouldSucceedOn_R__()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READshouldFailOn____()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ.test(f);
        }
        
        @Test
        public void WRITEshouldSucceedOn__W_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.WRITE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void WRITEshouldFailOn____()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.WRITE.test(f);
        }
        
        @Test
        public void READ_EXECUTEshouldSucceedOn_R_X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_EXECUTE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void READ_EXCEUTEshouldFailOn_R__()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_EXECUTE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void READ_EXCEUTEshouldFailOn___X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_EXECUTE.test(f);
        }
        
        @Test
        public void WRITE_EXECUTEshouldSucceedOn__WX()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.WRITE_EXECUTE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void WRITE_EXCEUTEshouldFailOn__W_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.WRITE_EXECUTE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void WRITE_EXCEUTEshouldFailOn___X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.WRITE_EXECUTE.test(f);
        }
        
        @Test
        public void READ_WRITEshouldSucceedOn_RW_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void READ_WRITEshouldFailOn_R__()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE.test(f);
        }
        
        @Test(expected = ValidationException.class)
        public void READ_WRITEshouldFailOn__W_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE.test(f);
        }
        
        @Test
        public void READ_WRITE_EXECUTEshouldSuccedOn_RWX()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn_R__()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn__W_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn___X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn_R_X()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(false);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn_RW_()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(true);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(false);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void READ_WRITE_EXECUTEshouldFailOn__WX()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.canRead()).thenReturn(false);
            when(f.canWrite()).thenReturn(true);
            when(f.canExecute()).thenReturn(true);
            
            FileFilter.PERMISSION.READ_WRITE_EXECUTE.test(f);
        }
    }
    
    public static class TYPETest
    {   
        @Test
        public void FILEshouldSucceedOnFile()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.isDirectory()).thenReturn(false);
            when(f.isFile()).thenReturn(true);
            
            FileFilter.TYPE.FILE.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void FILEshouldFailOnDirectory()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.isDirectory()).thenReturn(true);
            when(f.isFile()).thenReturn(false);
            
            FileFilter.TYPE.FILE.test(f);
        }
        
        @Test
        public void DIRECTORYshouldSucceedOnDirectory()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.isDirectory()).thenReturn(true);
            when(f.isFile()).thenReturn(false);
            
            FileFilter.TYPE.DIRECTORY.test(f);
        }
        
        @Test(expected=ValidationException.class)
        public void DIRECTORYshouldFailOnFile()
            throws ValidationException
        {
            File f = mock(File.class);
            when(f.exists()).thenReturn(true);
            when(f.isDirectory()).thenReturn(false);
            when(f.isFile()).thenReturn(true);
            
            FileFilter.TYPE.DIRECTORY.test(f);
        }
    }
    
    private FileFilter filter;
    
    @Before
    public void setUp()
    {
        filter = new FileFilter();
        filter.setExtension("ext");
    }
    
    @Test
    public void assertSufficesShouldSucceedOnCorrectExtension()
        throws ValidationException
    {
        File f = new File("f.ext");
        
        filter.assertSuffices(f);
    }
    
    @Test(expected=ValidationException.class)
    public void assertSufficesShouldFailOnNoExtension()
        throws ValidationException
    {
        File f = new File("f");
        
        filter.assertSuffices(f);
    }
    
    @Test(expected=ValidationException.class)
    public void assertSufficesShouldFailOnWrongExtension()
        throws ValidationException
    {
        File f = new File("f.ext2");
        
        filter.assertSuffices(f);
    }
    
    @Test
    public void assertSufficesShouldCallFileFilter()
        throws ValidationException
    {
        File f = new File("f.ext");
        java.io.FileFilter myFilter = mock(java.io.FileFilter.class);
        when(myFilter.accept(f)).thenReturn(true);
        FileFilter filter = new FileFilter(myFilter);
        
        filter.assertSuffices(f);
        
        verify(myFilter).accept(f);
    }
}
