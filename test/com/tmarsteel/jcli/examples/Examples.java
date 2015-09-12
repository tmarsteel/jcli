/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tmarsteel.jcli.examples;

import com.tmarsteel.jcli.validator.Validator;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.validator.builder.ValidatorBuilder;
import com.tmarsteel.jcli.validator.builder.XMLValidatorBuilder;

/**
 *
 * @author tobias.marstaller
 */
public class Examples
{    
    public static void main(String[] args) throws Exception
    {
        ValidatorBuilder builder = XMLValidatorBuilder.getInstance(
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
