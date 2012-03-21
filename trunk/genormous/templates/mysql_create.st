group mysql_db;

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
