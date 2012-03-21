group web_setter;

convertMap ::= [
	"byte":"Byte",
	"int":"Int",
	"long":"Long",
	"boolean":"Boolean",
	"String":"String",
	"double":"Double",
	"byte[]":"Bytes",
	"java.math.BigDecimal":"BigDecimal",
	"java.sql.Date":"Date",
	"java.sql.Timestamp":"Timestamp",
	"java.util.UUID":"UUID",
	default:"null"
	]

fkeyConstraint(keyset) ::=<<
CONSTRAINT $keyset.constraintName$ FOREIGN KEY ($keyset.keys:{key | "$key.name$"}; separator=", "$)
	REFERENCES $keyset.tableName$ ($keyset.keys:{key | "$key.foreignTableColumnName$"}; separator=", "$)$if(keyset.hasOnDelete)$ ON DELETE $keyset.onDelete$$endif$$if(keyset.hasOnUpdate)$ ON UPDATE $keyset.onUpdate$$endif$
>>

tableCreate(table) ::= <<
CREATE TABLE $table.name$ (
	$table.columns:{col | "$col.name$" $col.sQLType$ $if(col.defaultSet)$DEFAULT '$col.default$'$endif$ $if(!col.allowNull)$NOT $endif$NULL}; separator=",\n"$$if(table.hasPrimaryKey)$,
	PRIMARY KEY ($table.primaryKeys:{key | "$key.name$"}; separator=", "$)$endif$$if(table.hasForeignKey)$,
	$table.foreignKeys:fkeyConstraint(); separator=",\n"$ 
	$endif$$if(table.hasUniqueColumns)$,
	$table.uniqueColumnSets:{colset | UNIQUE ($colset:{col | "$col.name$"}; separator=", "$)}; separator=",\n"$
	$endif$
	)

>>


class() ::= <<
/**
	Class added by WebFieldSetterPlugin
	This class provides a means to automatically set field data from a web post
*/
public static class WebFieldSetter
	{
	java.util.BitSet m_setFields = new java.util.BitSet();
	java.util.Map<String, String> m_fieldNames = new java.util.HashMap<String, String>();
	genorm.plugins.web.WebFieldConverter m_converter;
	
	protected void init()
		{
		m_converter = new genorm.plugins.web.DefaultWebFieldConverter();
		$columns:{col | m_fieldNames.put(COL_$col.nameCaps$, COL_$col.nameCaps$);
}$
		}
		
	/**
		You can override this method if you want to set default values
	*/
	protected String getFieldValue($table.className$ obj, String col, java.util.Map<String, String[]> data)
		{
		String[] values = data.get(m_fieldNames.get(col));
		String value = null;
		
		if (values != null && values.length > 1)
			value = values[0];
			
		return (value);
		}
	
	public WebFieldSetter()
		{
		m_setFields.set(0, NUMBER_OF_COLUMNS);
		}
		
	/**
		Use this constructor to provide a list of field data meta.  The list of 
		field meta is used as a filter, only meta listed will have their fields
		set.
	*/
	public WebFieldSetter(GenOrmFieldMeta... metaList)
		{
		for (GenOrmFieldMeta meta : metaList)
			m_setFields.set(meta.getDirtyFlag());
		}
		
	/**
		Sets the field converter to be used to convert string values from a post
		into values that can be set on the object.  If no converter is provided
		genorm.plugins.web.DefaultWebFieldConverter is used.
		
		@param converter Converter to use when setting fields.
	*/
	public WebFieldSetter setWebFieldConverter(genorm.plugins.web.WebFieldConverter converter)
		{
		m_converter = converter;
		return (this);
		}
		
	$columns:{col | /**
	Sets the paramter name for the column $col.name$.  This name will be the expected
	key in data map in the call to setFields.
	The default is '$col.name$'
*/
public WebFieldSetter set$col.methodName$Field(String fieldName)
	{
	m_fieldNames.put(COL_$col.nameCaps$, fieldName);
	return (this);
	}
		
}$
	
	/**
		Sets the fields on obj with the values in data according to the filter
		that was specified in the constructor.
	*/
	public $table.className$ setFields($table.className$ obj, java.util.Map<String, String[]> data)
		{
		$columns:{col | if (m_setFields.get($col.nameCaps$_FIELD_META.getDirtyFlag()))
	{
	String value = getFieldValue(obj, COL_$col.nameCaps$, data);
	obj.set$col.methodName$(m_converter.to$convertMap.(col.type)$(COL_$col.nameCaps$, value));
	}
			
}$
		return (obj);
		}
	}


>>