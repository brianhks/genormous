package $package$.genorm;


public class GenOrmFieldMeta
	{
	private String m_fieldName;
	private int m_dirtyFlag;
	private boolean m_primaryKey;
	
	public GenOrmFieldMeta(String fieldName, int dirtyFlag, boolean primaryKey)
		{
		m_fieldName = fieldName;
		m_dirtyFlag = dirtyFlag;
		m_primaryKey = primaryKey;
		}
		
	public String getFieldName() { return (m_fieldName); }
	public int getDirtyFlag() { return (m_dirtyFlag); }
	public boolean isPrimaryKey() { return (m_primaryKey); }
	}
