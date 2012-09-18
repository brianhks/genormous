group mysql_db;

license() ::=<<
$! 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
!$
>>

fkeyConstraint(keyset) ::=<<
CONSTRAINT $keyset.constraintName$ FOREIGN KEY ($keyset.keys:{key | `$key.name$`}; separator=", "$)
	REFERENCES $keyset.tableName$ ($keyset.keys:{key | `$key.foreignTableColumnName$`}; separator=", "$)$if(keyset.hasOnDelete)$ ON DELETE $keyset.onDelete$$endif$$if(keyset.hasOnUpdate)$ ON UPDATE $keyset.onUpdate$$endif$
>>

tableCreate(table) ::= <<
CREATE TABLE $table.name$ (
	$table.columns:{col | `$col.name$` $col.sQLType$ $if(col.defaultSet)$DEFAULT $if(col.quoteDefault)$'$col.default$'$else$$col.default$$endif$$endif$ $if(!col.allowNull)$NOT $endif$NULL}; separator=",\n"$$if(table.hasPrimaryKey)$,
	PRIMARY KEY ($table.primaryKeys:{key | `$key.name$`}; separator=", "$)$endif$$if(table.hasForeignKey)$,
	$table.foreignKeys:fkeyConstraint(); separator=",\n"$ 
	$endif$$if(table.hasUniqueColumns)$,
	$table.uniqueColumnSets:{colset | UNIQUE ($colset:{col | `$col.name$`}; separator=", "$)}; separator=",\n"$
	$endif$
	)

>>
