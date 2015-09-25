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
package com.tmarsteel.jcli;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility-Class for loading 
 * @author tobse-local
 */
public abstract class XMLTest
{
    public NodeList testNodes;
    
    public String testXML;
    public String testNodesName;
    
    @Before
    public void loadTestXML()
        throws ParserConfigurationException, SAXException, IOException
    {
        if (testXML == null)
        {
            fail("Cannot load test-document beacuse the testXML variable has not been initialized");
        }
        
        if (testNodesName == null)
        {
            fail("Cannot load test-document beacuse the testNodesName variable has not been initialized");
        }
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        
        DocumentBuilder builder = dbf.newDocumentBuilder();
        
        Document testDocument = builder.parse(this.getClass().getResourceAsStream(testXML));
        
        testNodes = testDocument.getElementsByTagName(testNodesName);
    }
}
