package genorm.runtime;


public class GenOrmConstraint
	{
	private String m_foreignTable;
	private String m_constraintName;
	private String m_sql;
	
	public GenOrmConstraint(String foreignTable, String constraintName, String sql)
		{
		m_foreignTable = foreignTable;
		m_constraintName = constraintName;
		m_sql = sql;
		}
		
	public String getForeignTable()
		{
		return (m_foreignTable);
		}
		
	public String getConstraintName()
		{
		return (m_constraintName);
		}
		
	public String getSql()
		{
		return (m_sql);
		}
	}
