/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tmarsteel.jcli.examples;

import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Environment;

/**
 *
 * @author tobias.marstaller
 */
public class Examples
{    
    public static void main(String[] args) throws Exception
    {
        CLIParser parser = CLIParser.getInstance(
            Examples.class.getResourceAsStream("example-config.xml"),
            Environment.UNIX
        );
        
        String[] inputAR = new String[] { "--v" };
        
        CLIParser.ValidatedInput input = parser.parse(inputAR);
        
        System.out.println(input.getOption("encoding"));
    }
}
