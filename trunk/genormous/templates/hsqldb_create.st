CREATE $hsqldb_tableType$ TABLE $table.name$ (
	$columns:{col | "$col.name$" $typeToSQLTypeMap.(col.type)$ $if(col.defaultSet)$DEFAULT $col.default$$endif$ $if(!col.allowNull)$NOT $endif$NULL}; separator=",\n"$$if(table.hasPrimaryKey)$,
	PRIMARY KEY ($primaryKeys:{key | "$key.name$"}; separator=", "$)$endif$$if(table.hasForeignKey)$,
	$foreignKeys:{keyset | FOREIGN KEY ($keyset.keys:{key | "$key.name$"}; separator=", "$)
	REFERENCES $keyset.tableName$ ($keyset.keys:{key | "$key.foreignTableColumnName$"}; separator=", "$)$if(keyset.hasOnDelete)$ ON DELETE $keyset.onDelete$$endif$$if(keyset.hasOnUpdate)$ ON UPDATE $keyset.onUpdate$$endif$}; separator=",\n"$
	$endif$$if(table.hasUniqueColumns)$,
	$uniqueColumns:{colset | UNIQUE ($colset:{col | "$col.name$"}; separator=", "$)}; separator=",\n"$
	$endif$
	)