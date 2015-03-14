# Tags #

The following is a layout of the tags that are and can be present in the definition xml.  The definition xml is used by Genormous to generate the ORM files as well as the create.sql file.

  * [tables](TableDefinition#tables.md)

  * [configuration?](TableDefinition#configuration.md)
    * [option\*](TableDefinition#option.md)
    * [type\_map?](TableDefinition#type_map.md)
      * [type+](TableDefinition#type.md)
    * [plugin\*](TableDefinition#plugin.md)

  * [global?](TableDefinition#global.md)
    * [col+](TableDefinition#col.md)

  * [table+](TableDefinition#table.md)
    * [comment?](TableDefinition#comment.md)
    * [property\*](TableDefinition#property.md)
    * [col+](TableDefinition#col.md)
      * [comment?](TableDefinition#comment.md)
      * [property\*](TableDefinition#property.md)
      * [reference?](TableDefinition#reference.md)
    * [primary\_key?](TableDefinition#primary_key.md)
      * [colref+](TableDefinition#colref.md)
    * [unique\*](TableDefinition#unique.md)
      * [colref+](TableDefinition#colref.md)
    * [table\_query\*](TableDefinition#table_query.md)
      * [comment?](TableDefinition#comment.md)
      * [input?](TableDefinition#input.md)
      * [replace?](TableDefinition#replace.md)
      * [sql](TableDefinition#sql.md)

  * [query\*](TableDefinition#query.md)
    * [comment?](TableDefinition#comment.md)
    * [input?](TableDefinition#input.md)
      * [param\*](TableDefinition#param.md)
    * [replace?](TableDefinition#replace.md)
      * [param\*](TableDefinition#param.md)
    * [return?](TableDefinition#return.md)
      * [param\*](TableDefinition#param.md)
    * [sql](TableDefinition#sql.md)




---

## tables ##

This is the root tag in the definitions file.

| **sub tags** |
|:-------------|
| [configuration?](TableDefinition#configuration.md) |
| [TableDefinition#global global?} |
| [table+](TableDefinition#table.md) |
| [query\*](TableDefinition#query.md) |


---

## configuration ##

| **sub tags** |
|:-------------|
| [option\*](TableDefinition#option.md) |
| [type\_map?](TableDefinition#type_map.md) |
| [plugin\*](TableDefinition#plugin.md) |


---

## option ##

Specifies Genormous options used during code generations

| **attributes** | **description** |
|:---------------|:----------------|
| name | Name of the property to set |
| value | Value of the property |

### Possible options ###

_genorm.package_:  defines the package name to be placed in the generated code

_genorm.destination_:  destination folder to place generated orm files into.

_genorm.graphvizFile_:  name of the graphviz dot file to create

```
<option name="genorm.package" value="test.orm"/>
<option name="genorm.destination" value="src/java/test/orm"/>
<option name="genorm.graphvizFile" value="build/tables.dot"/>
```


---

## type\_map ##

The type\_map tag contains type definitions used to translate types used in the
table definitions to java and database typtes

| **sub tags** |
|:-------------|
| [type](TableDefinition#type.md) |


---

## type ##

Defines a type used in the definition file and its equivalent java and database types

| **attributes** |
|:---------------|
| custom |
| java |
| db |

```
<type custom="integer" java="int" db="INT"/>
<type custom="date" java="java.sql.Date" db="DATE"/>
<type custom="boolean" java="boolean" db="BOOLEAN"/>
<type custom="string" java="String" db="VARCHAR"/>
<type custom="timestamp" java="java.sql.Timestamp" db="TIMESTAMP"/>
```


---

## plugin ##

Plugins are used to control/augment the generated code.  At least one plugin will need to be defined to specify the target database.

| **attributes** |
|:---------------|
| class |

```
<plugin class="genorm.plugins.dbsupport.HSQLDB"/>
```


---

## global ##

Defines columns that are applied to all tables.  A good place to put creation timestamps and such.

| **sub tags** |
|:-------------|
| [col+](TableDefinition#col.md) |

```
<global>
	<col name="MTS" type="timestamp" auto_set="mts"/>
	<col name="CST" type="timestamp" auto_set="cts"/>
</global>
```


---

## table ##

| **attributes** |
|:---------------|
| name |

| **sub tags** |
|:-------------|
| [col+](TableDefinition#col.md) |


```
<table name="database_info">
	<comment></comment>
	<property key="hsqldb_tableType" value="CACHED"/>
	<col name="INFO_ID" type="integer" primary_key="true">
		<comment></comment>
	</col>
	<col name="VERSION" type="integer">
		<comment></comment>
	</col>
</table>
```


---

## comment ##

Used for adding comments to a column or table.  The comments show up in the javadoc for the class as well.


---

## property ##

Used for adding tables specific properties.  For HSQL databases it is used to define the table type.

| **attributes** |
|:---------------|
| key |
| value |

See [table](TableDefinition#table.md) for an example.


---

## col ##

| **attributes** | **default** | **description** |
|:---------------|:------------|:----------------|
| name |  | Name of the table |
| type |  | Type as defined in the type\_map |
| primary\_key | false | (true/false) defines the column as the primary key |
| default\_value |  | Sets a default value for the column |
| allow\_null | true | (true/false) defines if the column can be null or not |
| unique | false | Specifies if a unique constraint is placed on this column |
| auto\_set |  | Can set to either "mts" or "cts".  This defines the table to be automatically set with the modification time or creation time. |

| **sub tags** |
|:-------------|
| [comment?](TableDefinition#comment.md) |
| [property\*](TableDefinition#property.md) |
| [reference?](TableDefinition#reference.md) |


---

## reference ##

Used to define a column as a foriegn key to another column.

| **attributes** | **default** | **description** |
|:---------------|:------------|:----------------|
| table |  | Foreign table this column refers to |
| column |  | Name of column on foreign table this column refers to |
| id |  |  |
| on\_delete | default | (cascade, null, default) |
| on\_update | default | (cascade, null, default) |

```
<col name="community" type="string">
	<reference table="community" column="name" on_update="cascade"/>
</col>
```


---

## table\_query ##

Defines queries specific to this table.

| **attributes** |
|:---------------|
| name |
| result\_type |


---

## input ##




---

## replace ##


---

## return ##


---

## param ##

| **attributes** |
|:---------------|
| name |
| type |
| test |


---

## sql ##

Contains the sql query or update.


---

## query ##

| **attributes** |
|:---------------|
| name |


---

## primary\_key ##

Used to set a set of columns as the primary key

| **sub tags** |
|:-------------|
| [colref+](TableDefinition#colref.md) |


---

## unique ##

Used to define a set of columns as unique

| **sub tags** |
|:-------------|
| [colref+](TableDefinition#colref.md) |

```
<table name="role">
	<comment></comment>
	<col name="id" type="integer" primary_key="true"/>
	<col name="name" type="string"/>
	<col name="type" type="string"/>
	<col name="owner" type="integer">
		<reference column="client_id" table="client"/>
	</col>
	<col name="community" type="string">
		<reference column="name" table="community"/>
	</col>
	<table_query name="role" result_type="single">
		<input>
			<param name="name" type="string" test="test"/>
			<param name="type" type="string" test="user"/>
			<param name="owner" type="integer" test="2"/>
			<param name="community" type="string" test="Lingotek"/>
		</input>
		<sql>
			from role this
			where this.name = ? and
			this.type = ? and
			this.owner = ? and
			this.community = ?
		</sql>
	</table_query>
	<unique>
		<colref name="name"/>
		<colref name="type"/>
		<colref name="owner"/>
		<colref name="community"/>
	</unique>
</table>
```


---

## colref ##

| **attributes** |
|:---------------|
| name |