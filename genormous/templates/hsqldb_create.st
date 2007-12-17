CREATE $hsqldb_tableType$ TABLE $table.name$ (
	$columns:{col | $col.name$ $typeToSQLTypeMap.(col.type)$ $col.default$ $if(!col.allowNull)$NOT $endif$NULL}; separator=",\n"$$if(table.hasPrimaryKey)$,
	PRIMARY KEY ($primaryKeys:{key | $key.name$}; separator=", "$)$endif$$if(table.hasForeignKey)$,
	$foreignKeys:{keyset | FOREIGN KEY ($keyset.keys:{key | $key.name$}; separator=", "$)
	REFERENCES $keyset.tableName$ ($keyset.keys:{key | $key.foreignTableColumnName$}; separator=", "$)}; separator=",\n"$
	$endif$$if(table.hasUniqueColumns)$,
	$uniqueColumns:{colset | UNIQUE ($colset:{col | $col.name$}; separator=", "$)}; separator=",\n"$
	$endif$
	)
