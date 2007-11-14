package genorm;

import java.util.*;

public class Column
	{
	private String m_name;
	private boolean m_primaryKey;
	private String m_type;
	private String m_customType;
	private boolean m_foreignKey;
	private String m_foreignTableName;
	private String m_foreignTableColumnName;
	private Table m_foreignTable;
	private String m_comment;
	private int m_dirtyFlag;
	private String m_default;
	private boolean m_allowNull;
	private Format m_formatter;
	
	
	public Column(String name, String type, String customType, Format format)
		{
		m_formatter = format;
		m_name = name;
		m_type = type;
		m_customType = customType;
		m_primaryKey = false;
		m_foreignKey = false;
		m_foreignTable = null;
		m_comment = "";
		m_dirtyFlag = 0;
		m_default = "";
		m_allowNull = true;
		}
		
		
	public String getDefault() { return (m_default); }
	public String getName() { return (m_name); }
	public String getNameCaps() { return (m_formatter.formatStaticName(m_name)); }
	public String getParameterName() { return (m_formatter.formatParameterName(m_name)); }
	public String getMethodName() { return (m_formatter.formatMethodName(m_name)); }
	public String getType() { return (m_type); }
	public String getCustomType() { return (m_customType); }
	public String getDirtyFlag() { return ("0x"+Integer.toHexString(m_dirtyFlag)); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	public boolean isForeignKey() { return (m_foreignKey); }
	public boolean isKey() { return (m_primaryKey || m_foreignKey); }
	public Table getForeignTable() { return (m_foreignTable); }
	public String getForeignTableName() { return (m_foreignTableName); }
	public String getForeignTableColumnName() { return (m_foreignTableColumnName); }
	public String getForeignTableColumnMethodName() { return (m_formatter.formatMethodName(m_foreignTableColumnName)); }
	public String getComment() { return (m_comment); }
	public boolean getAllowNull() { return (m_allowNull); }
	
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
	}
