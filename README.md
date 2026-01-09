# GenORMous

Genormous is an ORM.  The twist is that it generates the ORM classes for you in
your project, kind of like protobuf but for SQL.

This project was created a long time ago to solve a problem and has been successfully 
used in several projects since.

## Overview

Genormous generates ORM code in your project and provides a runtime to support
the generated code.

You start with an XML file (referred to as tables.xml) that describes your database.  If you already have a 
database you are trying to connect to you, Genormous provides a way to generate the 
tables.xml from the database schema.

The tables.xml file describes each table and column along with additional queries and
configuration used to generate code.  When Genormous generates code it will create
an SQL file for creating the database from scratch and a Java class for each
table and query you define.

For each table you get two classes.  For example if you have table foo, genormous will
generate a Foo.java and a Foo_base.java.  Foo extends Foo_base.
Foo.java is empty and is only generated once.  Foo.java is a place for you to add custom code to the ORM
object, it will not get overwritten.  Foo_base.java is 100% generated code and 
will get overwritten everytime genormous is run on your project.

Accessing table objects is done via a static factory that is defined in the base class (Foo_base).
By default, the factory comes with methods to find or create the object based on 
its primary keys.  Additional for retrieving the object by some other column can 
be added as table queries in the tables.xml file.

## Benefits 

### Speed
A big complaint I've heard of any ORM is the speed issue.  Genormous generates code
specifically for your tables.  Genormous doesn't need to use reflection or lookups to figure 
out how to map an object to the table when writing it.  It is as effecient as if 
you wrote each ORM object by hand.  What about aggregating data from multiple tables?  
If your project only accesses a single table at a time you can be perfectly happy with using any ORM. 
But let's say you have 30 tables in your database and for the main user page you 
need to aggregate data from 15 of those tables. Collecting this data using strait 
ORM will result in 15 queries to the database returning way more information than 
you probably need.  

GenORMous has a two prong approach to solving ORM The first is to generate ORM 
objects for each table. The second is to create objects from custom SQL queries. 
All database queries are placed in a simple XML file. Here is an example of one 
of those queries: 
```xml
<query name="project_list"> 
    <input> 
        <param name="client_id" type="int" test="1"/> 
    </input> 
    <return> 
        <param name="project_id" type="int"/> 
        <param name="project_name" type="String"/> 
    </return> 
    <sql> 
        select p.project_id, p.name as project_name 
        from project p, project_assignment pa 
        where pa.client_id = ? and pa.project_id = p.project_id 
    </sql> 
</query>
```

From this GenORMous creates an object that represents the query project_list (ProjectListQuery). The SQL is 
unrestricted. You can put in anything the database can handle. Just define what 
the inputs and return types are and that's it.  Now you can take advantage of the 
full query capabilities of your DBMS solution.



### Database Changes
In an ideal world the database would be designed perfect the first time and 
never need to be changes.  Unfortunately requirements change along with customer 
needs that require changes to the database.

Why are database changes so bad?  Database changes break code.  Even worse it breaks 
code in a way that is only runtime detectable. The testing required to find such 
breakages are costly to any organization.  The reason these breakages are so 
insidious is because of SQL strings in the code.  The compiler cannot detect errors 
in SQL queries embedded in the code.  They only fail during runtime when the query 
string is sent to the database and fails.

#### How does GenORMous fix this problem?
By now you should know GenORMous generates ORM objects to access your database.
These objects are generated at pre-compile time.  The objects are generated off 
of your database design.  When you change the database you simply regenerate the 
ORM objects.  Because now the ORM objects reflect the database changes your code 
will break at compile time instead of run time.

What about the queries mentioned above in the Speed section?  Unfortunately breakages 
here cannot be identified at compile time. But because all of your queries are placed 
in a single file GenORMous creates a unit test for you to call from your favorite 
unit test framework to test all the queries against the changed database.
The queries that are affected will fail.

So lets review what steps are required to detect and fix database changes in the code: 
1. Change the databse. 
2. Change the GenORMous XML file to reflect the database changes. 
3. Regenerate ORM objects. 
4. Compile code and fix problems. 
5. Run unit tests to detect query problems. 
6. Fix queries and regenerate query objects. 
7. Again recompile to detect code breakages from query changes.

This is not a short list but, the process is predictable and determinate.

The above steps are mostly automated so the computer tells you where the problems are so you can quickly fix them.

### Simplicity
The idea of GenORMous had its origins in SimpleORM.  SimpleORM is easy to use 
but the setup is a bit tedious. The other drawback of SimpleORM is that it 
used generic parameters and return types that required a lot of casting to use it. 
These are not serous flaws but, it can be done better.

Genormous simplifies the objects so interactions with the database is neat and clean.

Basic ORM interaction.
```java
Foo f = Foo.factory.findOrCreate(keyValue);
f.setName("new name");
f.flush();
```

## Getting Started

Include Genormous in your project pom or your favorite build system.
```xml
<dependency>
    <groupId>org.agileclick.genorm</groupId>
    <artifactId>genormous</artifactId>
    <version>1.6.6.jdbc43</version>
</dependency>
```

Add an exec section to your pom to run the code generation.
```xml
<project>
    <build>
        <plugins>
            <plugin>
                <artifactId>exec-maven-plugin</artifactId>
                <version>1.6.0</version>
                <groupId>org.codehaus.mojo</groupId>
                <executions>
                    <execution><!-- Run our version calculation script -->
                        <id>Generate GenOrmous code</id>
                        <goals>
                            <goal>java</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <mainClass>org.agileclick.genorm.Genormous</mainClass>
                    <arguments>
                        <argument>-s</argument>
                        <argument>src/main/conf/tables.xml</argument>
                    </arguments>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
You can now run `> mvn exec:java` to generate your java code. 



### Configure tables.xml

The tables.xml file genormous uses to generate the ORM code.  The tables.xml
file defines what your tables look like and how to query them.  

#### Exporting Database Schema
If you already have
a database schema created the easiest way to start is to export that schema into 
a tables.xml file.  To do so in the above maven exec plugin replace the configuration 
with the following:
```xml
<configuration>
    <mainClass>org.agileclick.genorm.TableCreator</mainClass>
    <arguments>
        <argument>org.postgresql.Driver</argument> <!-- replace with the driver for your DB -->
        <argument>jdbc:postgresql://127.0.0.1:5432/mydb</argument> <!-- JDBC connection string -->
        <argument>admin</argument> <!-- DB User name -->
        <argument>secret_password</argument> <!-- DB Password -->
        <argument>src/main/conf/tables.xml</argument>
    </arguments>
</configuration>
```
Run `> mvn exec:java` will export your database schema and save it to tables.xml for a starting point.
Remember to change the configuration back to do code generation or you will overwrite your tables.xml file.

#### Creating tables.xml

Lets start with a very simple tables.xml definition file

```xml
<!DOCTYPE tables SYSTEM "https://raw.githubusercontent.com/brianhks/genormous/master/doc/tables_1_4.dtd">

<tables>
    <configuration>
        <option name="genorm.package" value="com.example.orm"/>
        <option name="genorm.destination"
                value="src/main/java/com/example/orm"/>
        
        <type_map>
            <type custom="integer" java="int" db="INT"/>
            <type custom="string" java="String" db="VARCHAR"/>
        </type_map>
        
        <plugin class="org.agileclick.genorm.plugins.dbsupport.MySQL"/>
    </configuration>
    
    <table name="user_info">
        <comment>This table contains user information.</comment>
        <property key="mysql_table_option" value="ENGINE=InnoDB"/>
        <col name="user_id" type="integer" primary_key="true">
        </col>
        <col name="first_name" type="string"/>
        <col name="last_name" type="string"/>
    </table>
    <queries>
    <!-- cross table queries go here -->
    </queries>

</tables>
```

We will get into the specifics of this file in a later section.  For now, you can 
see we have a configuration section that lets you define where to generate the code.
There is a type_map section that lets you map types used in the tables.xml file to
java and database types.  Most importantly is the table sections where you define
each table in your database.  As a side note each `table` and `col` tag lets you set 
a `comment` element.  The `comment` will show up as javadoc comments in the code.


### Generating java code

Using the above tables.xml file with the configuration in your pom.xml you can run
`> mvn exec:java` to generate your java code.  The following files will be generated under 
`src/main/java/com/example/orm`
 * **create.sql** - File you can use to create your database and tables
 * **UserInfo.java** - Empty java class you can make modifications to
 * **UserInfo_base.java** - Base class that has all generated code
 * **DSEnvelope.java** - A wrapper for the DataSource object you get from your driver or connection pool code
 * **GenOrmDataSource.java** - Contains static methods for handling database connections
 * **GenOrmUnitTest.java** - Provides a method that calls unit tests for every table query

## Database Connections and Transactions

Database connections are handled in a very stealthy way.  You will never see or deal
with a connection directly.  First lets look at how to initialize Genormous runtime.

In your main or startup code you will need to create a DataSource from your database driver
and wrap it in whatever pooling library you want to use.  In this example we use c3p0
and have it load the postgres driver, then we pass the DataSource to Genormous
```java
ComboPooledDataSource cpds = new ComboPooledDataSource();
cpds.setDriverClass( "org.postgresql.Driver" ); //loads the jdbc driver
cpds.setJdbcUrl(config.getJdbcUrl());
cpds.setUser(config.getUsername());
cpds.setPassword(config.getPassword());

GenOrmDataSource.setDataSource(new DSEnvelope(cpds));
```
Genormous stores that data source in a static environment variable.

Now you can use your ORM objects.
```java
UserInfo info = UserInfo.factory.create(1234);
info.setFirstName("Bob");
info.setLastName("Dog");
info.flush();
```

In this simple use case no transaction is created and the object is saved to the 
database when flush is called.

If you want to put multiple operations within a transaction you can start it before
manipulating the ORM objects.  Just make sure you commit and close when finished.

```java
try (GenOrmDataSource.attachAndBegin())
{
    UserInfo info = UserInfo.factory.find(1234);
    info.setFirstName("Robert");
    
    GenOrmDataSource.commit();
}
```

In the above example `attachAndBegin()` returns a Closable that calls `GenOrmDataSource.close()`.

The call `attachAndBegin()` acquires a connection, starts a transaction and attaches it 
to the current thread.  Any database interaction like doing lookups or flushes will
happen within that transaction.  Any object you modify will get flushed when `commit()` is called.

By using thread local storage to keep the database connection you don't have to pass it around.
Your code can call into other methods that access or update the database.  In some
applications I started and ended the transaction in a web filter.  If the underlying 
request doesn't throw an exception the transaction is committed and closed.

If you need to connect to more than one database you can name the individual sources and then
use that name when calling into GenOrmDataSource.
```java
//Setting named datasource
GenOrmDataSource.setDataSource("postgres", new DSEnvelope(cpds));

//Getting that datasource
try (GenOrmDataSource.attachAndBegin("postgres"))
{
UserInfo info = UserInfo.factory.find(1234);
    info.setFirstName("Robert");
    
    GenOrmDataSource.commit();
}
```


## Database Compatibility

Genormous was initially written and tested against HSQLDB. With that said the 
only part of Genormous that is specific to the database is the create table SQL statement. 
If creating the tables is something you do not care about, Genormous "should" 
work with most databases. All operations are standard insert update and delete SQL statements.

Genormous currently has specific configurations for the following databases * HSQL * PostgreSQL * MySQL

Support for a particular database is specified in the configuration section of 
your definition file
```xml
<tables> 
    <configuration> 
        ... 
        <plugin class="org.agileclick.genorm.plugins.dbsupport.MySQL"/> 
    </configuration> 
    ... 
</tables>
```
Available plugins are
 * org.agileclick.genorm.plugins.dbsupport.MySQL
 * org.agileclick.genorm.plugins.dbsupport.Postgres
 * org.agileclick.genorm.plugins.dbsupport.HSQLDB


## Unit Testing

When you define a `table_query` or `query` in your tables.xml file you specify a
test attribute for each input parameter.
```xml
<table_query name="by_name" result_type="single">
    <input>
        <param name="name" type="string(255)" test="bob"/>
    </input>
    <sql>
        from customers this
        where
        this."customerName" = ?
    </sql>
</table_query>
```
The test parameter just needs to be valid for the sql in question.  When generating
the code for each table Genormous will create on the factory a method called `testQueryMethods()`, which 
will run each query passing the test parameter.  The purpose of this test is to make sure the 
sql is syntactically correct for the database.  If you rename tables or columns this 
test will verify there are not schema mistakes in the query.

From within a unit test you will want to call `GenOrmUnitTest.performUnitTests()` which 
will in turn call `testQueryMethods()` on each of the tables factory objects.
```java
@Test
public void testDatabaseQueries()
{
	try (GenOrmDataSource.attachAndBegin())
	{
		 GenOrmUnitTest.performUnitTests();
	}
}
```

## Tables.xml Schema

When creating a tables.xml file make sure to reference the current dtd at the top of the file
```xml
<!DOCTYPE tables SYSTEM "https://raw.githubusercontent.com/brianhks/genormous/master/doc/tables_1_4.dtd">
```
Most editors will use this to enable auto-complete when filling out the data.

Under the root `tables` element you can have a `configuration` section, a `global`
section, multiple `table` definitions, multiple `view` definitions and a `queries` section.
We will discuss each section in depth below.

### configuration

The configuration section is made up of `option`, `type_map` and `plugins`.

#### option

The `option` elements let you define properties that are used by genormous durring 
code generation.  Currently there are only 3 options to set.
 * **genorm.package** - Name of the java package for each generated file.
 * **genorm.destination** - Destination to write the generated code to.
 * **genorm.graphvizFile** - Name of the graphviz dot file.  This lets you create a pretty graphic of your tables and their relationships.

```xml
<option name="genorm.package" value="org.example.orm"/>
<option name="genorm.destination" value="src/main/java/org/example/orm"/>
```

#### type_map
The type map is a way of mapping what database type maps to what java type.  You do 
it by specifying more than one `type` element with the following attributes
 * **custom** - The definition you use throughout the tables.xml file.
 * **java** - The primative or object to use in Java. 
 * **db** - The database type to use when generating the create.sql file.

```xml
<type custom="integer" java="int" db="INT"/> 
<type custom="date" java="java.sql.Date" db="DATE"/> 
<type custom="boolean" java="boolean" db="BOOLEAN"/> 
<type custom="string" java="String" db="VARCHAR"/> 
<type custom="timestamp" java="java.sql.Timestamp" db="TIMESTAMP"/>
```
Currently supported java types are
 * **byte**
 * **int**
 * **long**
 * **boolean**
 * **String**
 * **double**
 * **byte[]**
 * **java.math.BigDecimal**
 * **java.sql.Date**
 * **java.sql.Timestamp**
 * **java.util.UUID**



#### plugins

Plugins are used to control/augment the generated code. At least one plugin will 
need to be defined to specify the target database.

```xml
<plugin class="org.agileclick.genorm.plugins.dbsupport.HSQLDB"/>
```

There is a plugin to leverage memcached and one to support setting values
from a http request on an ORM object.  You can find the plugins under
`org.agileclick.genorm.plugins`

### global

Defines columns that are applied to all tables. A good place to put creation and modified timestamps.

```xml
<global>
    <col name="mts" type="timestamp" auto_set="mts"/>
    <col name="cts" type="timestamp" auto_set="cts"/>
</global>
```
Here we add two columns to every table that is used to track modification time (mts) and 
creation time (cts).  These values will be set by genormous code when the entry is created and modified.

### table

The table requires a `name` attribute that is the table name in the database.  If you 
use underscores '_' genormous will convert that to camel case when creating the class names.
ie 'user_data' will be 'UserData'.

#### comment

Many elements can contain a comment.  Comments will show up as java doc comments
with the generated code.

#### property

Properties are just key/values that are made available to the code generator and 
used within the code template.

For HSQL you can set the table type with hsqldb_tableType to MEMORY, CACHED, or TEXT
```xml
<property key="hsqldb_tableType" value="CACHED"/>
```

For Mysql you can set the database engine for the table with mysql_table_option
```xml
<property key="mysql_table_option" value="ENGINE=InnoDB"/>
```

#### col

Defines a column for a table.  May also contain a `commen` element.  The col element
has the following attributes
 * **name** - Name of the column.
 * **type** - This is a custom type from the type_map above.
 * **primary_key** - Either true or false - default false.  You can mark one or more columns as primary keys.
 * **default_value** - Sets a default value if you want.
 * **default_value_no_quote** - This will set the default value without using quotes.
 * **allow_null** - Sets allow null, default is true.
 * **unique** - Set the unique constraint on this column, default is false.
 * **auto_set** - Will be automatically set by genormous code.  If the value is "cts" the creation timestamp is set, if the value is "mts" the modification time is set.

If the column is a foreign key this constraint can be added by adding a `reference` child element and you set the table and column this column points to.

```xml
<col name="metric_id" type="string" primary_key="true">
    <reference table="metric" column="id"/>
</col>
```

#### table_query

By default, genormous provides methods to retrieve data by the primary key.  
The table query is where you can add custom SQL to retrieve table objects.
Table queries return an object that is mutable.  You can make updates and commit
objects that are returned but the update is limited to the single table.
Here is an example of one I used in the KairosDB project:

```xml
<table_query name="for_metric_id" result_type="multi">
    <input>
        <param name="metric_id" type="string" test="foo"/>
        <param name="start_time" type="timestamp" test="new java.sql.Timestamp(0L)"/>
        <param name="end_time" type="timestamp" test="new java.sql.Timestamp(0L)"/>
    </input>
    <replace>
        <param name="order" tag="order" type="string" test="asc"/>
    </replace>
    <sql>
        from data_point this
        where
        this."metric_id" = ?
        and this."timestamp" &gt;= ?
        and this."timestamp" &lt;= ?
        order by this."timestamp" %order%
    </sql>
</table_query>
```
This adds a method `getForMetricId` on the factory object for the table.  

The `input` section lets you define what the prepared statement parameters are (all the ?'s).
The type is the custom type from the `type_map`, the `test` value is what is used
when testing the query against the database.  The `test` value doesn't have to return
anything, it just needs to be valid for the sql query.

The `replace` section is a simple string replace into the sql.  The replace happens
before calling `prepareStatement` so do not use this to pass anything that hasn't been
sanitized.

The last section `sql` is where you place your custom SQL query.  There are two 
things to note.  The first is you do not specify the `SELECT` portion of the query.  
Genormous will insert the appropriate `SELECT` for the table.  The second item to 
note is the need to identify the table with `this`.  The reason for this is you may 
want to do the query based on information from a second table, so all the columns 
in the `SELECT` inserted by genormous are prefixed with this, ie this.metric_id.




### queries

This section is for generic query's that either update or retrieve data from multiple
tables.  Objects that are returned are immutable.

First lets look at a query that just retrieves data
```xml
<query name="count_uelsjb_records">
    <return>
        <param name="count" type="integer"/>
    </return>
    <sql>
        SELECT count(0) AS count
        FROM uelsjb
    </sql>
</query>
```

Here is an example that queries data from joined tables
```xml
<query name="list_history_records">
    <input>
        <param name="first_page" type="integer" test="1"/>
        <param name="rec_per_page" type="integer" test="20"/>
    </input>
    <return>
        <param name="date" type="timestamp"/>
        <param name="description" type="string"/>
        <param name="user" type="string"/>
    </return>
    <sql>
        SELECT h.entry_time AS date, h.description, u.username AS user
        FROM history h LEFT JOIN users u ON u.id = h.user_id
        ORDER BY h.entry_time DESC
        LIMIT ?, ?
    </sql>
</query>
```

Each query can have a `comment`, `input`, `replace`, `return` and `sql` elements.

The `comment` element lets you add a comment that is then added to the generated
Object for this query as a javadoc comment for the class.

The `input` element is for prepared statement input parameters same as the `table_query` 
above.  The order needs to be the same as the '?' in the code they correspond to.

The `replace` element is a set of parameters that are text replaced into the query
string before being processed by the prepared statement call.  Here is an example where
the filter is passed in:
```xml
<query name="count_uelsjb_records_with_filter">
    <replace>
        <param name="filter" tag="filter" type="string" test="`company_name` = 'bob'"/>
    </replace>
    <return>
        <param name="count" type="integer"/>
    </return>
    <sql>
        SELECT count(0) AS count
        FROM uelsjb this
        WHERE %filter%
    </sql>
</query>
```

The tag attribute is used to match up with `%filter%` in the query to know what to replace.
Be cautious as this input is not sanitized like prepared statement input is.  Make sure
input is not retrieved directly from the user for replace parameters.

The `return` element identifies the elements of the result set that is returned
by this query.  The parameters are pulled from the result set in order they are specified.  
These parameters will be available as get methods on the generated object.

The `sql` element contains the SQL for the query.  Unlike the `table_query` you will
specify the select portion of the query.


Here is an example of what using one of the queries would look like in code.  This
example pass the filter into the query:
```java
protected int getRecordCount(String filter)
{
    CountUelsjbRecordsWithFilterQuery countQuery = new CountUelsjbRecordsWithFilterQuery(filter);
    return countQuery.runQuery().getOnlyRecord().getCount();
}
```

The `getOnlyRecord()` method is a shortcut that fetches the first message and then
closes the result set for you.
