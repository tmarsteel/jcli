# jCLI
A Java library for parsing and validating CLI arguments (and Strings formatted alike).

[Project on GitHub](http://github.com/tmarsteel/jcli)

## Installation

Go to the [releases page](realeses) and download `tmarsteel-jcli-X.X.X.jar` from the latest release. Support for dependency managers is about to come!

## Documentation

* [Walkthrough](docs/walkthrough.md): an introduction to XML configuration
* [detailed Documentation](docs/detailed.md) a full explanation of all XML directives and of how to create custom filters and rules
* [Programmatical Configuration](docs/programmatical.md) documentation of programmatical configuration

## Quick Start

jCLI can be configured either via XML documents or programmtaically (modifying the parsed XML configuration is possible, but not convenient).  

The library distinguishes three types of input: flags, options and arguments. They are supposed to be arranged in this order: `<executable> [flags and options] arguments...`

Options and arguments can have arbitrary values, falgs are represented as `boolean`, set or not set.

#### Parse raw input

If you only want to parse input (without support for aliases, validation and OS specifiy syntax), just push the data into an instance of `Input`:

```java
import com.tmarsteel.jcli.Input;
import com.tmarsteel.jcli.Environment;

// ...

public static void main(String[] args) {
	Input input = new Input(Environment.UNIX, args);
	
	boolean verbose = input.isFlagSet("verbose");
}
```

#### Advanced Parsing

To offer aliases (e.g. `-v` and `--verbose` for the same flag), validate the values of options/arguments and to adapt the syntax to the platform your software is being run on use
`Validator`:

##### 1. Get a `Validator` instance and configure it

```java
Validator inputValidator = new Validator();
```

You can then programmatically configure the validator. But most of the time you will want to load an XML configuration, this can be done like this:

```java
ValidatorConfigurator configurator = XMLValidatorConfigurator.getInstance(
	getClass().getResourceAsStream("cli-config.xml")
);

configurator.configure(inputValidator);
```

#### 2. Parse Input and access it

```java
class SampleProgram
{
    public static void main(final String[] args)
    {
        Validator inputValidator = new Validator();
		
		// configure inputValidator

        Validator.ValidatedInput input;
        try
        {
            input = inputValidator.parse(args);
            System.out.println("foo: " + input.getOption("foo"));
            System.out.println("baz: " + input.isFlagSet("baz"));
        }
        catch (ParseException ex)
        {
            System.out.println("Failed to initialize: " + ex.getMessage());
            return;
        }
		catch (ValidationException ex)
        {
			System.out.println("Please check your input: " + ex.getMessage());
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

On UNIX systems (`File.separatorChar == '/'`) flags are recognised by a single-dash prefix and options by a double-dash prefix:

`program -flag --option optionValue argument`

On DOS systems (`File.separatorChar != '/'`) both flags and options are expected to be prefixed with a forward
slash (`/`); whether something is a flag or an option is derived from context.

Arguments are interally handled the same way as options - but they have to be configured to be accessed.

## Planned for Version 1.1

* Help-Text generation (via a `<description>` tag)