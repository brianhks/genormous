package genorm.runtime;


public class GenOrmFieldMeta
	{
	private String m_fieldName;
	private int m_dirtyFlag;
	private boolean m_primaryKey;
	private boolean m_foreignKey;
	
	public GenOrmFieldMeta(String fieldName, int dirtyFlag, boolean primaryKey, boolean foreignKey)
		{
		m_fieldName = fieldName;
		m_dirtyFlag = dirtyFlag;
		m_primaryKey = primaryKey;
		m_foreignKey = foreignKey;
		}
		
	public String getFieldName() { return (m_fieldName); }
	public int getDirtyFlag() { return (m_dirtyFlag); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	public boolean isForeignKey() { return (m_foreignKey); }
	}
