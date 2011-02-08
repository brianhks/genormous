package genorm.runtime;

import java.io.Serializable;

public class GenOrmFieldMeta implements Serializable
	{
	private String m_fieldName;
	private String m_fieldType;
	private int m_dirtyFlag;
	private boolean m_primaryKey;
	private boolean m_foreignKey;
	
	public GenOrmFieldMeta(String fieldName, String fieldType, int dirtyFlag, boolean primaryKey, boolean foreignKey)
		{
		m_fieldName = fieldName;
		m_fieldType = fieldType;
		m_dirtyFlag = dirtyFlag;
		m_primaryKey = primaryKey;
		m_foreignKey = foreignKey;
		}
		
	public String getFieldName() { return (m_fieldName); }
	public String getFieldType() { return (m_fieldType); }
	public int getDirtyFlag() { return (m_dirtyFlag); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	public boolean isForeignKey() { return (m_foreignKey); }
	}
