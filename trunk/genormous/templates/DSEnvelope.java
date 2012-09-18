$! 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
!$
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
		
	public GenOrmKeyGenerator getKeyGenerator(String table)
		{
		return (m_keyGenMap.get(table));
		}
	
	public void initialize()
		{
		GenOrmDataSource.setDataSource(this);
		}
		
	/**
		Method for overriding the standard key generator
	*/
	public void setKeyGenerator(String table, GenOrmKeyGenerator generator)
		{
		m_keyGenMap.put(table, generator);
		}
	}
