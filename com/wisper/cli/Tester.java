package com.wisper.cli;

/**
 *
 * @author Tobse
 */
public class Tester {
    public static void main(String... args) throws Exception
    {
        CLIParser p = CLIParser.getInstance(
            Tester.class.getResourceAsStream("config.xml"),
            Environment.UNIX
        );
        CLIParser.ValidatedInput input = p.parse("-v");
        System.out.println(input.isFlagSet("q"));
    }
}
