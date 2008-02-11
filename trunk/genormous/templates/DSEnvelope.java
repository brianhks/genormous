package $package$;

import java.util.*;
import javax.sql.*;

import genorm.runtime.*;

public class DSEnvelope implements GenOrmDSEnvelope
	{
	private DataSource m_dataSource;
	private Map<String, GenOrmKeyGenerator> m_keyGenMap;
	
	public DSEnvelope(DataSource ds)
		{
		m_dataSource = ds;
		m_keyGenMap = new HashMap<String, GenOrmKeyGenerator>();
		$tables:{t | $if(t.generatedKey)$m_keyGenMap.put("$t.name$", new $package$.$t.className$_base.$t.className$KeyGenerator(ds));$endif$
}$
		}
		
	public DataSource getDataSource()
		{
		return (m_dataSource);
		}
		
	public Map<String, GenOrmKeyGenerator> getKeyGeneratorMap()
		{
		return (m_keyGenMap);
		}
	}
