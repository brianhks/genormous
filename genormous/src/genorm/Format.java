package genorm;


public interface Format
	{
	public String formatClassName(String tableName);  //used
	public String formatStaticName(String columnName); //used
	public String formatMethodName(String columnName); //used
	//public String formatStaticNameRef(String columnName);
	public String formatForeignKeyMethod(ForeignKeySet fks);
	public String formatParameterName(String columnName);
	}
