group orm_object_base;

setAndGetMethods(col) ::= <<
//---------------------------------------------------------------------------
protected $col.type$ get$col.methodName$_base() { return (m_$col.parameterName$.getValue()); }
protected void set$col.methodName$_base($col.type$ data)
	{
	m_$col.parameterName$.setValue(data);
	m_dirtyFlags |= $col.nameCaps$_FIELD_META.getDirtyFlag();
	}


>>
	
declarMetaFields(col) ::= <<
private static final GenOrmFieldMeta $col.nameCaps$_FIELD_META = new GenOrmFieldMeta("$col.name$", $col.dirtyFlag$, $col.primaryKey$);

>>

foreignGetAndSetMethods(foreignKeys) ::= <<
//---------------------------------------------------------------------------
protected $foreignKeys.table.className$ get$foreignKeys.methodName$_base()
		throws java.sql.SQLException
	{
	return ($foreignKeys.table.className$.factory.find($foreignKeys.keys:{key | m_$key.parameterName$.getValue()}; separator=", "$));
	}
	
protected void set$foreignKeys.methodName$_base($foreignKeys.table.className$ table)
		throws java.sql.SQLException
	{
	$foreignKeys.keys:{key | m_$key.parameterName$.setValue(table.get$key.foreignTableColumnMethodName$());m_dirtyFlags |= $key.nameCaps$_FIELD_META.getDirtyFlag();
}; separator="\n"$
	
	}


>>

baseClass(package,table,columns,primaryKeys,foreignKeys,createSQL) ::= <<
package $package$;

import java.util.*;
import $package$.genorm.*;

public class $table.className$_base extends GenOrmRecord
	{
	private static final String QUERY = "SELECT $columns:{col | $col.name$}; separator=", "$ FROM $table.name$ ";
	private static final String WHERE = "WHERE ";
	private static final String KEY_WHERE = "WHERE $primaryKeys:{key | $key.name$ = ?}; separator=", "$";
	
	private static final String TABLE_NAME = "$table.name$";
	
	$columns:declarMetaFields()$
	
	$if(table.generatedKey)$
	//===========================================================================
	public static class $table.className$KeyGenerator
			implements GenOrmKeyGenerator
		{
		private static final String MAX_QUERY = "SELECT MAX($table.primaryKey.name$) FROM $table.name$";
		
		private volatile long m_nextKey;
		
		public $table.className$KeyGenerator(javax.sql.DataSource ds)
				throws java.sql.SQLException
			{
			m_nextKey = 1;
			try
				{
				java.sql.Connection con = ds.getConnection();
				java.sql.Statement stmnt = con.createStatement();
				java.sql.ResultSet rs = stmnt.executeQuery(MAX_QUERY);
				if (rs.next())
					m_nextKey = rs.getLong(1);
				
				rs.close();
				stmnt.close();
				con.commit();
				con.close();
				}
			catch (java.sql.SQLException sqle)
				{
				//The exception may occur if the table does not yet exist
				System.out.println(sqle);
				}
			}
			
		public synchronized long generateKey()
			{
			m_nextKey++;
			return (m_nextKey);
			}
		}
	$endif$
		
	//===========================================================================
	public static $table.className$Factory factory = new $table.className$Factory();
	
	public static class $table.className$Factory //Inherit interfaces
			implements GenOrmRecordFactory
		{
		public static final String CREATE_SQL = "$createSQL$";
		
		private $table.className$Factory()
			{
			}
			
		private $table.className$ new$table.className$(java.sql.ResultSet rs)
				throws java.sql.SQLException
			{
			$table.className$ rec = new $table.className$();
			(($table.className$_base)rec).initialize(rs);
			GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (rec);
			}
	
		//---------------------------------------------------------------------------
		public String getCreateStatement()
			{
			return (CREATE_SQL);
			}
			
		$if(table.hasPrimaryKey)$
		//---------------------------------------------------------------------------
		public $table.className$ create($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			$table.className$ rec = new $table.className$();
			$primaryKeys:{key | (($table.className$_base)rec).set$key.methodName$_base($key.parameterName$);
}$
			rec.m_isNewRecord = true;
			
			GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (rec);
			}
		$endif$
		//---------------------------------------------------------------------------
		public $table.className$ create()
				throws java.sql.SQLException
			{
			$table.className$ rec = new $table.className$();
			rec.m_isNewRecord = true;
			
			GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (rec);
			}
			
		//---------------------------------------------------------------------------
		public $table.className$ createWithGeneratedKey()
				throws java.sql.SQLException
			{
			$if(!table.generatedKey)$
			throw new UnsupportedOperationException("$table.className$ does not support a generated primary key");
			$else$
			$table.className$ rec = new $table.className$();
			
			rec.m_isNewRecord = true;
			rec.set$table.primaryKey.methodName$_base(
					($table.primaryKey.type$)GenOrmDataSource.getKeyGenerator("$table.name$").generateKey());
			
			GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (rec);
			$endif$
			}
			
		//---------------------------------------------------------------------------
		public $table.className$ find(Object keys)
				throws java.sql.SQLException
			{
			$if(table.hasPrimaryKey)$
			$if(table.multiplePrimaryKeys)$
			Object[] kArr = (Object[])keys;
			return (find($primaryKeys:{key | ($javaToObjectType.(key.type)$)kArr[$i0$]}; separator=", "$));
			$else$
			$primaryKeys:{key | return (find(($javaToObjectType.(key.type)$)keys));}$
			$endif$
			$else$
			return (null);
			$endif$
			}
			
		$if(table.hasPrimaryKey)$
		//---------------------------------------------------------------------------
		public $table.className$ find($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
				throws java.sql.SQLException
			{
			$table.className$ rec = null;
			
			//TODO: Look for the object on the current transaction first
			java.sql.PreparedStatement ps = GenOrmDataSource.prepareStatement(QUERY+KEY_WHERE);
			$primaryKeys:{key | ps.set$javaToJDBCMap.(key.type)$($i$, $key.parameterName$);
}$
			if (DEBUG)
				System.out.println(ps);
				
			java.sql.ResultSet rs = ps.executeQuery();
			if (rs.next())
				rec = new$table.className$(rs);
				
			rs.close();
			ps.close();
				
			return (rec);
			}
		
		//---------------------------------------------------------------------------
		public $table.className$ findOrCreate($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
				throws java.sql.SQLException
			{
			$table.className$ rec = find($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			if (rec == null)
				rec = create($primaryKeys:{key | $key.parameterName$}; separator=", "$);
				
			return (rec);
			}
			
		$endif$
		//---------------------------------------------------------------------------
		public ArrayList<$table.className$> select(String where)
				throws java.sql.SQLException
			{
			return (select(where, null));
			}
			
		//---------------------------------------------------------------------------
		public ArrayList<$table.className$> select(String where, String orderBy)
				throws java.sql.SQLException
			{
			ArrayList<$table.className$> results = new ArrayList<$table.className$>();
			
			java.sql.Statement stmnt = GenOrmDataSource.createStatement();
			StringBuilder sb = new StringBuilder();
			sb.append(QUERY);
			sb.append(WHERE);
			sb.append(where);
			if (orderBy != null)
				{
				sb.append(" ");
				sb.append(orderBy);
				}
			
			java.sql.ResultSet rs = stmnt.executeQuery(sb.toString());
			while (rs.next())
				{
				results.add(new$table.className$(rs));
				}
				
			rs.close();
			stmnt.close();
			return (results);
			}
		}
		
	//===========================================================================
	//This does not allow for multiple database instances
	//TODO: Change it.
	/* private static int s_newClientId = 0;
	
	private static synchronized int getNewClientId()
			throws java.sql.SQLException
		{
		if (s_newClientId == 0)
			{
			GenOrmConnection goc = new GenOrmConnection();
			goc.begin(GenOrmDataSource.s_dataSource);
			java.sql.Connection c = goc.getConnection();
			java.sql.Statement stmnt = c.createStatement();
			
			java.sql.ResultSet rs = stmnt.executeQuery("select max(client_id) as max_id from client");
			rs.first();
			s_newClientId = rs.getInt(1);
			
			rs.close();
			stmnt.close();
			goc.commit();
			goc.close();
			}
			
		return (++s_newClientId);
		} */
		
	//===========================================================================
		
	$columns:{col | private $javaToGenOrmMap.(col.type)$ m_$col.parameterName$;$\n$}$

	$columns:setAndGetMethods()$	
	
	$foreignKeys:foreignGetAndSetMethods()$
	
	
	//---------------------------------------------------------------------------
	private void initialize(java.sql.ResultSet rs)
			throws java.sql.SQLException
		{
		$columns:{col | m_$col.parameterName$.setValue(rs, $i$);$\n$}$
		}
	
	//---------------------------------------------------------------------------
	/*package*/ $table.className$_base()
		{
		super(TABLE_NAME);
		
		$columns:{col | 
m_$col.parameterName$ = new $javaToGenOrmMap.(col.type)$($col.nameCaps$_FIELD_META);
m_fields.add(m_$col.parameterName$);$\n$}$
		}
	
	//---------------------------------------------------------------------------	
	public boolean isDirty()
		{
		return (m_dirtyFlags != 0);
		}
		
	//---------------------------------------------------------------------------
	public void setDirty()
		{
		//This will mark all attributes as dirty
		m_dirtyFlags = -1;
		}
		
	//---------------------------------------------------------------------------
	public void setMTS()
		{
		//setMTS_base(new Timestamp(System.currentTimeMillis()));
		}
		
	//---------------------------------------------------------------------------
	public void setCTS()
		{
		//setCTS_base(new Timestamp(System.currentTimeMillis()));
		}
		
	//---------------------------------------------------------------------------
	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		
		$columns:{col | sb.append("$col.name$=\"");
sb.append(m_$col.parameterName$.getValue());
sb.append("\" ");
}$
		
		return (sb.toString().trim());
		}
	}
>>


