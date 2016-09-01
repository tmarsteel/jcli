# Multi-Command support

jCLI offers support for sub-commands of the kind like `git commit`, `apt-get install`, `composer update`, etc. These
commands can be nested to an arbitrary depth, allowing for constructs like AWSs `aws ec2 allocate-hosts`.

Every command can have a return value in case it does not just write to STDOUT.
Every command can have a return value in case it does not just write to STDOUT.

## Simple usage

Create a class implementing `com.tmarsteel.jcli.command.Command` for every command your CLI should offer. The validation
of input given to that command is now a responsibility of each command:

```java
class MyCommand implements Command<Void> {
    public Void execute(String[] args) throws ValidationException, Exception {
        // put args through a Validator
    }
}
```

Instantiate a new `com.tmarsteel.jcli.command.CommandDispatcher` (presumably main() is the correct place for this).
You can then register your commands with that dispatcher.

```java
CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();

dispatcher.add("my", new MyCommand());
```

Once all of your commands have been registered, invoke `dispatch` on your dispatcher:

```java
public static void main(String[] args) {
    CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();
    
    // ... register your commands
    
    dispatcher.dispatch(args);
}
```

## Nesting Commands

`CommandDispatcher`s are `Command`s themselves. This example of the `aws ec2 allocate-hosts` command should say it all:

```java
CommandDispatcher<Void> dispatcher = new CommandDispatcher<>();
CommandDispatcher<Void> ec2Command = new CommandDispatcher<>();
ec2Command.add("allocate-hosts", new EC2_AllocateHostsCommand());
dispatcher.add("ec2", "ec2Command");
```