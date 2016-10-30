# jCLI 
A Java library for parsing and validating CLI arguments (and Strings formatted alike).

[Project on GitHub](http://github.com/tmarsteel/jcli)

![build state](https://api.travis-ci.org/tmarsteel/jcli.svg?branch=develop)

## Installation

Go to the [releases page](http://github.com/tmarsteel/jcli/releases) and download `tmarsteel-jcli-X.X.X.jar` from the latest release. Support for dependency managers is about to come!

Alternatively, you can check out this repo and build jCLI yourself using Ant. JUnit 4.10+ is required if you want to run unit-tests. See `build.xml` for more information.

## Features

* Flags, Options and Arguments
* Validation
  * **entirely** configurable using XML
  * convert/parse input to data types such as `File`, `BigDecimal` and `Pattern`
  * Constraints on inputs, e.g: file must exist and be readable, integer ranges, pattern matching (and group extraction!), ...
* Helptext generation: put `<description>` tags in your XML and the help output is generated for you
* Multi-Command support in the style of `git commit` and `apt-get install`
* Environment-Agnostic: accept *NIX style commands on *NIX systems, DOS style commands on DOS systems

## Documentation

* Input validation
  * [Walkthrough](docs/validation/walkthrough.md): an introduction to XML configuration
  * [detailed Documentation](docs/validation/detailed.md) a full explanation of all XML directives and of how to create custom filters and rules
  * [Programmatical Configuration](docs/validation/programmatical.md) documentation of programmatical configuration
* [Helptext](docs/helptext.md): How to generate a help text based on validation constraints
* [Sub-Commands](docs/multicommand.md): How to use the multi-command support
* [Gotchas](docs/gotchas.md): Avoid pitfalls by reading this page

## Quick Start

jCLI can be configured either via XML documents or programmtaically (modifying the parsed XML configuration is possible, but not convenient).  

The library distinguishes three types of input: flags, options and arguments. They are supposed to be arranged in this order: `<executable> [flags and options] : arguments...`

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
            System.out.println("arg: " + input.getArgument("sampleArgument"));
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
> java SampleProgram -foo "Hello World!" : sampleArgument
foo: Hello World!
baz: false
arg: sampleArgument
```

On UNIX systems (`File.separatorChar == '/'`) flags are recognised by a single-dash prefix and options by a double-dash prefix:

`program -flag --option optionValue : argument`

On DOS systems (`File.separatorChar != '/'`) both flags and options are expected to be prefixed with a forward
slash (`/`); whether something is a flag or an option is derived from context.

When using arguments with a `Validator`, these arguments have to be configured in order to be accessed.

## Roadmap

Version 2.0.0-RC2 contains multiple improvements; the RCs have yet to undergo exhaustive tests before a final version is released (presumably Q1 2017, but no promises!). RCs **will** introduce BC breaks to previous RCs.

### 2.0.0-RC3

* Varargs
* Integrate the helptext and multicommand features