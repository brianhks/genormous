package $package$.genorm;

import java.sql.ResultSet;

public abstract class GenOrmField
	{
	protected GenOrmFieldMeta m_fieldMeta;
	
	public GenOrmField(GenOrmFieldMeta gofm)
		{
		m_fieldMeta = gofm;
		}
		
	public GenOrmFieldMeta getFieldMeta() { return (m_fieldMeta); }
	
	public abstract void setValue(ResultSet rs, int pos) throws java.sql.SQLException;
	public abstract String getSQLValue();
	public abstract String toString();
	}
