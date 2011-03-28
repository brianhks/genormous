package genorm;

import java.util.*;

public class Column implements Cloneable
	{
	private String m_name;
	private boolean m_primaryKey;
	private String m_type;
	private String m_customType;
	private String m_sqlType;
	private boolean m_foreignKey;
	private String m_foreignTableName;
	private String m_foreignTableColumnName;
	private Table m_foreignTable;
	private String m_comment;
	private int m_dirtyFlag;
	private String m_default;
	private boolean m_isDefaultSet;
	private boolean m_allowNull;
	private Format m_formatter;
	private boolean m_unique;
	private String m_uniqueSet;
	private String m_autoSet;
	private String m_onDelete;
	private String m_onUpdate;
	
	public Column(String name)
		{
		m_name = name;
		}
	
	public Column(String name, String type, String customType, Format format, String sqlType)
		{
		m_formatter = format;
		m_name = name;
		m_type = type;
		m_customType = customType;
		m_sqlType = sqlType;
		m_primaryKey = false;
		m_foreignKey = false;
		m_foreignTable = null;
		m_comment = "";
		m_dirtyFlag = 0;
		m_default = "";
		m_isDefaultSet = false;
		m_allowNull = true;
		m_unique = false;
		m_onDelete = null;
		m_onUpdate = null;
		m_uniqueSet = null;
		}
		
	public Column getCopy()
		{
		Column copy  = null;
		try
			{
			copy = (Column)clone();
			}
		catch (CloneNotSupportedException e)
			{
			}
			
		return (copy);
		}
		
	@Override
	public int hashCode()
		{
		return (m_name.hashCode());
		}
		
	@Override
	public boolean equals(Object obj)
		{
		if (obj instanceof Column)
			return (((Column)obj).m_name.equals(m_name));
		else
			return (false);
		}
		
	public boolean isDefaultSet() { return (m_isDefaultSet); }
	public void setDefault(String def) 
		{
		m_isDefaultSet = true;
		m_default = def; 
		}
	
	public String getDefault() { return (m_default); }
	public String getName() { return (m_name); }
	public String getNameCaps() { return (m_formatter.formatStaticName(m_name)); }
	public String getParameterName() { return (m_formatter.formatParameterName(m_name)); }
	public String getMethodName() { return (m_formatter.formatMethodName(m_name)); }
	public String getType() { return (m_type); }
	public String getCustomType() { return (m_customType); }
	public String getSQLType() { return (m_sqlType); }
	public int getDirtyFlag() { return (m_dirtyFlag); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	public boolean isForeignKey() { return (m_foreignKey); }
	public boolean isKey() { return (m_primaryKey || m_foreignKey); }
	public boolean isUnique() { return (m_unique); }
	public String getUniqueSet() { return m_uniqueSet; }
	public Table getForeignTable() { return (m_foreignTable); }
	public String getForeignTableName() { return (m_foreignTableName); }
	public String getForeignTableColumnName() { return (m_foreignTableColumnName); }
	public String getForeignTableColumnMethodName() { return (m_formatter.formatMethodName(m_foreignTableColumnName)); }
	public String getComment() { return (m_comment); }
	public boolean getAllowNull() { return (m_allowNull); }
	public boolean isAllowNull() { return (m_allowNull); }
	public void setAllowNull(boolean allowNull) { m_allowNull = allowNull; }
	
	public void setPrimaryKey() 
		{ 
		m_primaryKey = true;
		m_allowNull = false;
		}
	public void setForeignKey() { m_foreignKey = true; }
	public void setForeignTable(Table table) { m_foreignTable = table; }
	public void setForeignTableName(String table) { m_foreignTableName = table; }
	public void setForeignTableColumnName(String column) { m_foreignTableColumnName = column; }
	public void setComment(String comment) { m_comment = comment; }
	public void setDirtyFlag(int flag) { m_dirtyFlag = flag; }
	public void setUnique() { m_unique = true; }
	public void setUniqueSet(String set) { System.out.println("Unique "+set); m_uniqueSet = set; }
	public void setCustomType(String type) { m_customType = type; }
	public void setAutoSet(String autoSet) { m_autoSet = autoSet; }
	public String getAutoSet() { return (m_autoSet); }

	
	public String getXML()
		{
		StringBuilder sb = new StringBuilder();
		
		sb.append("\t\t<col name=\""+m_name.toLowerCase()+"\" type=\""+m_customType+"\" ");
		if (m_primaryKey)
			sb.append("primary_key=\""+m_primaryKey+"\" ");
		
		if (isDefaultSet())
			sb.append("default_value=\""+m_default+"\" ");
			
		sb.append("allow_null=\""+m_allowNull+"\">\n");
		sb.append("\t\t\t<comment></comment>\n");
		if (isForeignKey())
			{
			sb.append("\t\t\t<reference table=\""+m_foreignTableName.toLowerCase()+"\" column=\""+
					m_foreignTableColumnName.toLowerCase()+"\"/>\n");
			}
			
		sb.append("\t\t</col>\n");
		
		return (sb.toString());
		}
		
	public void setOnDelete(String onDelete) { m_onDelete = onDelete; }
	public String getOnDelete() { return (m_onDelete); }

	public void setOnUpdate(String onUpdate) { m_onUpdate = onUpdate; }
	public String getOnUpdate() { return (m_onUpdate); }
	}
