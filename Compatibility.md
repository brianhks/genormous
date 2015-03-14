# Database compatibility #

Genormous was initially written and tested against HSQLDB.  With that said the only part of Genormous that is specific to the database is the create SQL statement.  If creating the tables is something you do not care about, Genormous "should" work with most databases.  All operations are standard insert update and delete SQL statements.

Genormous currently has specific configurations for the following databases
  * HSQL
  * PostgreSQL
  * MySQL

Support for a particular database is specified in the `configuration` section of your definition file
```
<tables>
  <configuration>
    ...
    <plugin class="genorm.plugins.dbsupport.MySQL"/>
  </configuration>
  ...
</tables>
```