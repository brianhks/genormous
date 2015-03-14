# Create a definition file #

The definition file is used to generate the Genormous source code.  The definition file can be anywhere in your project and is only used when regenerating the source code.

The basic outline of a definition file looks like this
```
<tables>
  <configuration>
    ...
  </configuration>

  <table ...>
    ...
  </table>

  <table ...>
    ...
  </table>

  <queries>
    ...
  </queries>
</tables>
```

## Set Genormous options ##

First off we need to specify some values specific to Genormous so it knows where to put the code and in what package to use.

In the `configuration` section we will use the `option` element to define two parameters for Genormous
```
<configuration>
  <option name="genorm.package" value="com.xyz.genorm"/>
  <option name="genorm.destination" value="src/java/com/xyz/genorm"/>
  ...
</configuration>
```
  * `genorm.package` Defines the package to place at the top of the generated files.
  * `genorm.destination` Defines where to put the generated files.

## Define your types ##

The `typeMap` allows us to abstract the types used in the definition file from those used in the database.  It also lets us specify what java type to use for a given database type.
```
<configuration>
  ...
  <typeMap>
    <type custom="integer" java="int" db="INT"/>
    ...
  </typeMap>
  ...
</configuration>
```
  * `custom` Defines the types used withing the definition file when defining a table.
  * `java` Defines what java native type or class to use in the code.  This must be one of the JDBC data types.
  * `db` Defines the type used in the database to represent this value.  This is only used in the generating the create.sql file.

Create as many `type` tags as you need to define all of your types you use.

## Add database support ##

Database support is added via a plugin.  The plugin tells Genormous how to format SQL for the particular DBMS.  The plugin bellow adds support for MySQL.
```
<configuration>
  ...
  <plugin class="genorm.plugins.dbsupport.MySQL"/>
  ...
</configuration>
```
We can only define one database support plugin.  This does not restrict other types of plugins from being defined.

## Define tables ##



## Define queries ##


# Generate code #



# Define your data source #


# Accessing tables #


# Accessing queries #