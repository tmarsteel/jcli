/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tmarsteel.jcli.examples;

import com.tmarsteel.jcli.Validator;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.ParserBuilder;
import com.tmarsteel.jcli.validator.XMLParserBuilder;

/**
 *
 * @author tobias.marstaller
 */
public class Examples
{    
    public static void main(String[] args) throws Exception
    {
        ParserBuilder builder = XMLParserBuilder.getInstance(
            Examples.class.getResourceAsStream("example-config.xml"),
            Environment.UNIX
        );
        
        Validator parser = new Validator();
        builder.configure(parser);
        
        String[] inputAR = new String[] { "-v", "--encoding", "UTF-8" };
        
        Validator.ValidatedInput input = parser.parse(inputAR);
        
        System.out.println(input.getOption("encoding"));
    }
}
