# Helptext generation

jCLI allows you to generate a help text based on the [validation constraints you have configured](validation/walkthrough.md).

For the scope of this tutorial it is assumed that a `Validator` has been instantiated and configured:

```java
Validator validator = new Validator();
XMLValidatorConfigurator.getInstance(...).configure(validator);
```

## Helptext structure

The format of the generated help text is very similar to that of manpages and of most UNIX programs. It has six sections:

1. Usage examples / example commands
2. Program description: What does this program do?
3. A list of available flags including aliases and description
4. A list of available options including with aliases and description
5. A list of available arguments including description, ordered by index
6. Further notes, e.g. to hint licenses, author contacts, ...

The generator can deduce sections 3 through 5 on itself, provided that you have a description on all your flags, options
and arguments (see next section of this page).

Input for sections 1, 2 and 6 has to be provided at runtime.

### Example helptext:

```
Usage: smapleProgram [-flags] [--options values] arguments...
                     --flag -option value argument
This program does XYZ by utilizing the algorithms A, B and C. For further details
see http://www.example.com.

-- Options --
option1  Some tedious, precise description of the option including format,
o1       default values and requirements...
o 

option2  Some tedious, precise description of the option including format,
o2       default values and requirements...


-- Flags --
flag1  Some tedious, precise description of the flag including format, default
f1     values and requirements...
f 

flag2  Some tedious, precise description of the flag including format, default
f2     values and requirements...



Some more notes on the program: license, author contact information, bugs, ...
```

## Preconditions

To get a helpful help text, all the flags, options and arguments should have a description. You can achieve this either
by putting `<description>` tags into your XML configuration or by invoking `setDescription` on each element.

If you invoke `#useDefaultFilterDescriptors` on the `HelptextGenerator`, descriptions of the filters in use will
 be generated automatically. To learn how to customize this behaviour, see the *advanced* section of this page.

```xml
<cli>
    <flag identifier="verbose">
        <alias>v</alias>
        <description>If set to true, all debug information will be written to STDOUT to help with debugging.</description>
    </flag>
    
    <option identifier="input-encoding">
        <alias>encoding</alias>
        <alias>inputEncoding</alias>
        <alias>ie</alias>
        
        <filter type="set">
            <value>UTF-8</value>
            <value>UTF-16</value>
            <value>ANSII</value>
            <value>UNICODE</value>
        </filter>
        
        <decsripction>The input encoding. Must be one of: UTF-8, UTF-16, ANSII or UNICODE.</decsripction>        
    </option>
    
    <argument identifier="input" index="0">
        <filter type="file">
            <type>FILE</type>
            <permissions>READ_EXIST</permissions>
        </filter>   
        <description>The input CSV file; must exist and be readable.</description>    
    </argument>
</cli>
```

```java
Flag verboseFlag = new Flag("verbose", "v");
verboseFlag.setDescription("If set to true, all debug information will be written to STDOUT to help with debugging.");
validator.add(verboseFlag);

// .. and so forth for the options and arguments

// invoke this method to include automatically generated descriptions of your filters:
helptextGenerator.useDefaultFilterDescriptors();
```

## Basic usage

A help text is modeled by the `Helptext` class. You can obtain an instance of it prefilled with your configurated
validation rules by using `HelptextFactory`:

```java
Helptext helptext = HelptextFactory.getInstance(validator);
```

You then need a bit more information the make the help text more helpful:

```java
helptext.usageExamples().addAll(Arrays.asList(
    "--inputEncoding UTF-8 input.csv out.xml",
    "input.csv out.xml
));

helptext.setProgramDescription("Converts CSV files to an XML version");
helptext.setNotes("Licensed under the WHATEVER license");
```

That helptext then needs to be formatted, preferably for CLI output:

```java
System.out.println(
    (new CLIHelptextFormatter()).format(helptext)
);
```

The resulting output will contain at most 80 characters per line. To increase that limit, use
`CLIHelptextFormatter#setMaxWidth`:

```java
CLIHelptextFormatter formatter = new CLIHelptextFormatter();
formatter.setMaxWidth(120);
System.out.println(formatter.format(helptext));
```

Invoke this method to include automatically generated descriptions of your filters:

```java
helptextGenerator.useDefaultFilterDescriptors();
```

## Examples

```java
class CSV2XMLStarter {
    
    private Validator validator = new Validator();
    
    public CSV2XMLStarter() {}
    
    public static void main(String[] args) {
        (new CSV2XMLStarter()).do_main(args);
    }
    
    private void do_main(String[] args) {
        try {
            (XMLValidatorConfigurator.getInstance(
                CSV2XMLStarter.getResourceAsStream("cli-config.xml")
            )).configure(validator);
        } catch (Exception ex) {
            System.err.println("Startup-Error:");
            System.err.println(ex);
            System.exit(-1);
        }
        
        Validator.ValidatedInput input;
        try {
            input = validator.parse(args);
            if (input.isFlagSet("help")) {
                printHelptext();
                return;
            }
            
            // do the actual work
            
        } catch(ValidationException ex) {
            System.err.println("Invalid input:" + ex.getMessage());
            printHelptext();
            System.exit(-1);
        }
    }
    
    private void printHelptext() {
        Helptext helptext = HelptextFactory.getInstance(validator);
        helptext.usageExamples().addAll(Arrays.asList(
            "--inputEncoding UTF-8 input.csv out.xml",
            "input.csv out.xml
        ));
        
        helptext.setProgramDescription("Converts CSV files to an XML version");
        helptext.setNotes("Licensed under the WHATEVER license");
        
        System.out.println(
            (new CLIHelptextFormatter()).format(helptext)
        );
    }
}
```

## Advanced

### Customizing Filter Descriptions

Filter descriptions are generated by the methods in `com.tmarsteel.jcli.helptext.FilterDescriptionUtil`.
Invoking `useDefaultFilterDescriptors` on any `FilterAwareHelptextGenerator` will cause these methods to be
used.

To override the defaults, implement `com.tmarsteel.jcli.helptext.FilterDescriptor` and register it with your
`HelptextGenerator`:

```java
abstract class MyFilterDescriptionUtil {
    List<String> describeCustomFilter(Filter oFilter) {
        if (!(oFilter) instanceof MyCustomFilter) {
            throw new UnsupportedOperationException("This method supports only objects of type " + MyCustomFilter.class.getName());
        }
        
        ArrayList<String> descr = new ArrayList<>();
        descr.add("Must fit to my super custom filter; will otherwise be rejected.");
        
        // invoke some getters of the filter here to learn its constraints
        // and put them into nice words; each element in the returned list
        // will end up being a separate bullet point in the helptext
        
        return descr;
    }
}

helptextGenerator.setFilterDescriptor(MyCustomFilter.class, MyFilterDescriptionUtil::describeCustomFilter);
```

#### Class hierarchy awareness

The way `HelptextGenerator`s are aware of the class hierarchy and allow for precise control
on what branches of the filter hierarchy are described by what descriptor.

Consider this class hierarchy of filters:
```
      A implements com.tmarsteel.jcli.filter.Filter
                  /        \
                 /          \
                B            C
                            / \
                           D   E
```

and this configuration of a `HelptextGenerator`:

```java
hg.setFilterDescriptor(A.class, descriptor_A);
hg.setFilterDescriptor(C.class, descriptor_C);
hg.setFilterDescriptor(E.class, descriptor_E);
```

This association of filter class to used desriptor results:

```
A -> descriptor_A
B -> descriptor_A
C -> descriptor_C
D -> descriptor_D
E -> descriptor_E
```

---

Consider this when implementing a `FilterAwareHelptextGenerator`. `CLIHelptextGenerator` uses a `SortedMap`
to achieve this behaviour.