## Detailed Walkthrough

### Environment

The library will adapt to the DOS or UNIX standards for flags and options depending on the `java.io.File.separatorChar`
variable. `CLIParser` contains various constructors and factory methods that support specifing your own configuration.

The environment affects three values:

* the escape character (which is irrelevant if you want to parse `String[]` only).
    * DOS: `^`, UNIX: `\`
* the prefix for options
    * DOS: `/`, UNIX: `-`
* the prefix for flags
    * DOS: `/`, UNIX: `--`

To simply enforce UNIX or DOS syntax, use `com.tmarsteel.jcli.Environment.UNIX` or `com.tmarsteel.jcli.Environment.DOS` constants:

```java
CLIParser parser = CLIParser.getInstance(
    getClass().getResourceAsStream("cli-config.xml"),
    Environment.UNIX
);
```

To define your own settings, use the `(String escapeChar, String optionPrefix, String flagPrefix)` constructor of
`Environment`:

```java
CLIParser parser = CLIParser.getInstance(
    getClass().getResourceAsStream("cli-config.xml"),
    new Environment("\\", "-", "/")
);
```

### XML configuration

In this example we will create a configuration for a .csv to .xls converter:

First of all, create the `cli` root-node:

```xml
<xml version="1.0">
<cli>
    
</cli>
```

#### Flags

We want our converter to be optionally verbose about anything it does. To allow this, we use a `--verbose` flag
that could also be abbreviated as `-v`:

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>
</cli>
```

**Note:** `v` is an alias but when querying the `CLIParser.ValidatedInput` for flags and options, the `identifier` must be used.

#### Options

Because .csv files can have various encodings and might lack a BOM, we need an optional option to specify the input
encoding but the converter will default to UTF-8:

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>
    
    <option identifier="encoding">
        <alias>e</alias>
        <default>UTF8</default>
    </option>
</cli>
```

**Note:** Just dont specify a `default` value if you want to enforce an option to be specified by users.

But wait... not every string is a valid encoding and our program does by far not support all encodings.
Lets add some limitations:

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>
    
    <option identifier="encoding">
        <alias>e</alias>
        <default>UTF8</default>
        <filter type="set">
            <value>UTF8</value>
            <value>UTF16</value>
            <value>UTF32</value>
            <value>ANSII</value>
        </filter>
    </option>
</cli>
```

**Note:** When using filters, always make sure the default value validates, too. There will be more on filters, later.

### Arguments

The input and output files should be specified as arguments of the program. However, only the input file is mandatory;
when the output file is not given our program derives the output filename by exchanging the .csv with the .xls extension.

Additionally, providing non-readable files or directories as input for this conversion is pointless; we will add
validation for that, too.

```xml
<xml version="1.0">
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
    </flag>
    
    <option identifier="encoding">
        <alias>e</alias>
        <default>UTF8</default>
        <filter type="set">
            <value>UTF8</value>
            <value>UTF16</value>
            <value>UTF32</value>
            <value>ANSII</value>
        </filter>
    </option>
    
    <argument index="0" identifier="input" required="true">
        <filter type="file">
            <permissions>READ</permissions>
            <existance>MUST_EXIST</existance>
            <type>FILE</type>
            <extension>csv</extension>
        </filter>
    </argument>
    
    <argument index="1" identifier"output">
        <filter type="file">
            <permissions>WRITE</permissions>
            <extension>xls</extension>
        </filter>
    </argument>
</cli>
```

And we are done. Now, users can call our program like this:

```
> csv2xls --v myFile.csv
> csv2xls -encoding UTF16 myFile.csv out.xls
```

And this is the java-sourceode (except for the conversion, of course) that *could* make use of that XML configuration:

```java
import com.tmarsteel.jcli.CLIParser;
import com.tmarsteel.jcli.Environment;
import com.tmarsteel.jcli.ParseException;
import java.io.IOException;

class CSV2XLSStarter {
    public static void main(String[] args) {
        CLIParser.ValidatedInput input;
    
        try
        {
            CLIParser parser = CLIParser.getInstance(
                CSVV2XLSStarter.class.getResourceAsStream("cli-config.xml"),
                Environment.UNIX
            );
            input = parser.parse(args);
        }
        catch (IOException ex)
        {
            System.err.println("Failed to load internal configuration");
            System.err.println(ex);
            System.exit(1);
        }
        catch (ParseException ex)
        {
            System.err.println("Please check your input:");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        
        File inputFile = (File) input.getOption("input");
        File outputFile = (File) input.getOption("output");
        String inputEncoding = input.getOption("encoding");
        boolean verbose = input.isFlagSet("verbose");
        
        if (outputFile == null)
        {
            outputFile = new File(/* exchange .csv for .xls in inputFile here */);
        }
        
        // do the conversion!
    }
}
```
