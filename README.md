# GenORMous

Genormous is an ORM.  The twist is that it generates the ORM classes for you in
your project, kind of like protobuf but for SQL.

This project was created a long time ago to solve a problem and has been successfully 
used in several projects since.

## Overview

The Genormous project contains code to generate java files and some runtime generic
runtime code.

You start with an XML file (refered to as tables.xml) that describes your database.  If you already have a 
database you are trying to connect to you, Genormous provides a way to generate the 
tables.xml from the database schema.

The tables.xml file describes each table and column along with additional queries and
configuration used to generate code.  When Genormous generates code it will create
an SQL file for creating the database from scratch and a Java class for each
table and query you define.

For each table you get two classes.  Lets say you have a table foo, genormous will
generate a Foo.java and a Foo_base.java.  Foo.java inherits from Foo_base.java.
Foo.java is only generated once and is a place for you to add custom code to the ORM
object, it will not get overwritten.  Foo_base.java is 100% generated code and 
will get overwritten everytime genormous is ran on your project.


Basic ORM interaction.
```java
GenOrmDataSource.attachAndBegin();
try
{
    Foo f = Foo.factory.findOrCreate(keyValue);
    f.setName("new name");

    Bar b = Bar.factory.findOrCreate(barKey);
    b.setBob("something");
	 
    GenOrmDataSource.commit();
}
finally
{
    GenOrmDataSource.close();
}
```



## Getting Started

### Generateing tables.xml


### Generating java code