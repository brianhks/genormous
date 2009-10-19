package genorm;


public class DefaultFormat
		implements Format
	{
	/*package*/ static String toCamelCase(String str, boolean upcaseFirstChar)
		{
		StringBuffer sb = new StringBuffer();
		char[] cArr = str.toLowerCase().toCharArray();
		
		if (upcaseFirstChar)
			{
			cArr[0] = Character.toUpperCase(cArr[0]);
			}
			
		boolean upcaseNext = false;
		for (int I = 0; I < cArr.length; I++)
			{
			if (cArr[I] == '_')
				{
				upcaseNext = true;
				}
			else
				{
				if (upcaseNext)
					{
					sb.append(Character.toUpperCase(cArr[I]));
					upcaseNext = false;
					}
				else
					sb.append(cArr[I]);
				}
			}
		
		return (sb.toString());
		}
		
	public String formatClassName(String tableName)
		{
		return (toCamelCase(tableName, true));
		}
		
	public String formatStaticName(String columnName)
		{
		return (columnName.toUpperCase());
		}
		
	/* public String formatStaticNameRef(String columnName)
		{
		//This assumes that columns that referece end with _id
		//It will therefore remove them before generating the name
		if (columnName.endsWith("_id"))
			columnName = columnName.substring(0, (columnName.length()-3));
		else
			columnName += "_ref";
			
		return (formatStaticName(columnName));
		} */
		
	public String formatMethodName(String columnName)
		{
		return (toCamelCase(columnName, true));
		}
		
	public String formatForeignKeyMethod(ForeignKeySet fks)
		{
		//This assumes that columns that referece end with _id
		//It will therefore remove them before generating the name
		String name = "";
		if (fks.getKeys().size() > 1)
			name = fks.getTableName();
		else
			name = fks.getKeys().get(0).getName();
		
		if (name.endsWith("_id"))
			name = name.substring(0, (name.length()-3));
			
		name += "_ref";
			
		return (formatMethodName(name));
		}
		
	public String formatParameterName(String columnName)
		{
		return (toCamelCase(columnName, false));
		}
	}
