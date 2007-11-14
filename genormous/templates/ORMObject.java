group orm_object;

derivedSetAndGetMethods(col) ::= <<
//------------------------------------------------------------------------------
/**
	$col.comment$
*/
public $col.type$ get$col.methodName$() 
	{ 
	return (get$col.methodName$_base()); 
	}
	
public void set$col.methodName$($col.type$ id)
	{
	set$col.methodName$_base(id);
	}

	
>>

foreignGetAndSetMethods(foreignKeys) ::= <<
//------------------------------------------------------------------------------
public $foreignKeys.table.className$ get$foreignKeys.methodName$()
	{
	return (get$foreignKeys.methodName$_base());
	}
	
public void set$foreignKeys.methodName$($foreignKeys.table.className$ table)
	{
	set$foreignKeys.methodName$_base(table);
	}
	
	
>>

derivedClass(package,table,columns,foreignKeys) ::= <<
package $package$;

/**
	$table.comment$
*/
public class $table.className$ extends $table.className$_base
	{
	$columns:derivedSetAndGetMethods()$
	
	$foreignKeys:foreignGetAndSetMethods()$
	}
>>
