# jCLI
A Java library for parsing and validating CLI arguments (and Strings formatted alike) into Key/Value maps.

## Installation

Download jCLI-X.X.jar from the dist/ directory or add this dependency to your POM file:

```
<dependency>
  <groupId>tmasteel</groupId>
  <artifactId>jcli</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Quick Start

jCLI can be configured either via XML documents or programmtaically (modifying the parsed XML configuration is
possible, but not convenient).  

The library distinguishes three types of input: flags, options and arguments. They are supposed to be arranged in
this order: `<executable> [flags and options] arguments...`

Options and arguments can have arbitrary values, falgs are represented as `boolean`, set or not set.

#### 1. Get a `CLIParser` instance

Most of the time you will want to load a XML configuration, this can be done like this:

```java
CLIParser parser = CLIParser.getInstance(
    getClass().getResourceAsStream("cli-config.xml")
);

CLIParser parser = CLIParser.getInstance(
    new File("./../cli-config.xml")
)
```

To programmatially configure the parsers, just create an instance of `CLIParser`:

```java
CLIParser parser = new CLIParser();
```

#### 2. Parse Input and access it

```java
class SampleProgram
{
    public static void main(final String[] args)
    {
        CLIParser cliParser = /* load the parser however you like */;
        CLIParser.ValidatedInput input;
        try
        {
            input = cliParser.parse(args);
            System.out.println("foo: " + input.getOption("foo"));
            System.out.println("baz: " + input.isFlagSet("baz"));
        }
        catch (ParseException ex)
        {
            System.out.println("Failed to initialize: " + ex.getMessage());
            return;
        }
    }
}
```

```
> java SampleProgram -foo "Hello World!"
foo: Hello World!
baz: false
```

On UNIX systems (`File.separatorChar == '/'`) flags are recognised by a doublde-dash prefix and options by a
single-dash prefix:

`program --flag -option optionValue argument`

On DOS systems (`File.separatorChar != '/'`) both flags and options are expected to be prefixed with a forward
slash (`/`); whether something is a flag or an option is derived from context.

Arguments are interally handled the same way as options - but they have to be configured to be accessed.

## Further reading

* [Walkthrough](docs/walkthrough.md): an introduction to XML configuration
* [detailed Documentation](docs/detailed.md) a full explanation of all XML directives and of how to create custom   filters and rules
* [Programmatical Configuration](docs/programmatical.md) documentation of programmatical configuration
