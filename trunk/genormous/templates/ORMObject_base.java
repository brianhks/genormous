group orm_object_base;

setAndGetMethods(col) ::= <<
//---------------------------------------------------------------------------
protected $col.type$ get$col.methodName$_base() { return (m_$col.parameterName$.getValue()); }
protected void set$col.methodName$_base($col.type$ data)
	{
	m_$col.parameterName$.setValue(data);
	
	//Add the now dirty record to the transaction only if it is not previously dirty
	if (m_dirtyFlags == 0)
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		
	m_dirtyFlags |= $col.nameCaps$_FIELD_META.getDirtyFlag();
	}


>>
	
declarMetaFields(col) ::= <<
private static final GenOrmFieldMeta $col.nameCaps$_FIELD_META = new GenOrmFieldMeta("$col.name$", $col.dirtyFlag$, $col.primaryKey$, $col.foreignKey$);

>>

foreignGetAndSetMethods(foreignKeys) ::= <<
//---------------------------------------------------------------------------
protected $foreignKeys.table.className$ get$foreignKeys.methodName$_base()
	{
	return ($foreignKeys.table.className$.factory.find($foreignKeys.keys:{key | m_$key.parameterName$.getValue()}; separator=", "$));
	}
	
//--------------------------------------------------------------------------
protected void set$foreignKeys.methodName$_base($foreignKeys.table.className$ table)
	{
	//Add the now dirty record to the transaction only if it is not previously dirty
	if ((!m_isNewRecord) && (m_dirtyFlags == 0))
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		
	$foreignKeys.keys:{key | m_$key.parameterName$.setValue(table.get$key.foreignTableColumnMethodName$());
m_dirtyFlags |= $key.nameCaps$_FIELD_META.getDirtyFlag();
}; separator="\n"$
	
	}


>>

addQueryMethods(query) ::= <<
//---------------------------------------------------------------------------
public $if(query.singleResult)$$table.className$$else$ResultSet$endif$ get$query.className$($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
	{
	String query = SELECT+"$query.sqlQuery$";
	$if(query.replaceQuery)$
	HashMap<String, String> replaceMap = new HashMap<String, String>();
	$query.replacements:{rep | replaceMap.put("$rep.tag$", $rep.parameterName$);}$
	query = replaceText(query, replaceMap);
	$endif$
	
	try
		{
		java.sql.PreparedStatement statement = GenOrmDataSource.prepareStatement(query);
		$query.inputs:{in | statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);}$
		
		if (DEBUG)
			System.out.println(statement);
		
		ResultSet rs = new ResultSet(statement.executeQuery(), query);
		statement.close();
		
		$if(query.singleResult)$
		return (rs.getOnlyRecord());
		$else$
		return (rs);
		$endif$
		}
	catch (java.sql.SQLException sqle)
		{
		if (DEBUG)
			sqle.printStackTrace();
		throw new GenOrmException(sqle);
		}
	}

>>

baseClass(package,table,columns,primaryKeys,foreignKeys,createSQL) ::= <<
package $package$;

import java.util.*;
import genorm.runtime.*;

public class $table.className$_base extends GenOrmRecord
	{
	//Change this value to true to turn on warning messages
	private static final boolean WARNINGS = false;
	private static final String SELECT = "SELECT $columns:{col | this.$col.name$}; separator=", "$ ";
	private static final String FROM = "FROM $table.name$ this ";
	private static final String WHERE = "WHERE ";
	private static final String KEY_WHERE = "WHERE $primaryKeys:{key | $key.name$ = ?}; separator=" AND "$";
	
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
				if (WARNINGS)
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
			
		protected $table.className$ new$table.className$(java.sql.ResultSet rs)
			{
			$table.className$ rec = new $table.className$();
			(($table.className$_base)rec).initialize(rs);
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
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
			
			//GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			}
		$endif$
		//---------------------------------------------------------------------------
		public $table.className$ createRecord()
			{
			$table.className$ rec = new $table.className$();
			rec.m_isNewRecord = true;
			
			//GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			}
			
		//---------------------------------------------------------------------------
		public $table.className$ createWithGeneratedKey()
			{
			$if(!table.generatedKey)$
			throw new UnsupportedOperationException("$table.className$ does not support a generated primary key");
			$else$
			$table.className$ rec = new $table.className$();
			
			rec.m_isNewRecord = true;
			rec.set$table.primaryKey.methodName$_base(
					($table.primaryKey.type$)GenOrmDataSource.getKeyGenerator("$table.name$").generateKey());
			
			//GenOrmDataSource.getGenOrmConnection().addToTransaction(rec);
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			$endif$
			}
			
		//---------------------------------------------------------------------------
		public $table.className$ findRecord(Object keys)
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
			{
			$table.className$ rec = null;
			try
				{
				//TODO: Look for the object on the current transaction first
				java.sql.PreparedStatement ps = GenOrmDataSource.prepareStatement(SELECT+FROM+KEY_WHERE);
				$primaryKeys:{key | ps.set$javaToJDBCMap.(key.type)$($i$, $key.parameterName$);
}$
				if (DEBUG)
					System.out.println(ps);
					
				java.sql.ResultSet rs = ps.executeQuery();
				if (rs.next())
					rec = new$table.className$(rs);
					
				rs.close();
				ps.close();
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (rec);
			}
		
		//---------------------------------------------------------------------------
		public $table.className$ findOrCreate($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			$table.className$ rec = find($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			if (rec == null)
				rec = create($primaryKeys:{key | $key.parameterName$}; separator=", "$);
				
			return (rec);
			}
			
		$endif$
		//---------------------------------------------------------------------------
		public ResultSet select(String where)
			{
			return (select(where, null));
			}
			
		//---------------------------------------------------------------------------
		public ResultSet select(String where, String orderBy)
			{
			ResultSet rs = null;
			
			try
				{
				java.sql.Statement stmnt = GenOrmDataSource.createStatement();
				StringBuilder sb = new StringBuilder();
				sb.append(SELECT);
				sb.append(FROM);
				sb.append(WHERE);
				sb.append(where);
				if (orderBy != null)
					{
					sb.append(" ");
					sb.append(orderBy);
					}
				
				String query = sb.toString();
				rs = new ResultSet(stmnt.executeQuery(query), query);
				stmnt.close();
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (rs);
			}
			
		$table.queries:addQueryMethods()$
		
		//---------------------------------------------------------------------------
		public void testQueryMethods()
			{
			ResultSet rs;
			$table.queries:{ query | $if(!query.singleResult)$rs = $endif$get$query.className$($[query.inputs,query.replacements]:{ p | $p.testParam$}; separator=", "$);
$if(!query.singleResult)$rs.close();$endif$
}$
			}
		}
		
	//===========================================================================
	public static class ResultSet 
			implements GenOrmResultSet
		{
		private java.sql.ResultSet m_resultSet;
		private String m_query;
		
		//------------------------------------------------------------------------
		protected ResultSet(java.sql.ResultSet resultSet, String query)
			{
			m_resultSet = resultSet;
			m_query = query;
			}
		
		//------------------------------------------------------------------------
		public void close()
			{
			try
				{
				m_resultSet.close();
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
			}
			
		//------------------------------------------------------------------------
		public ArrayList<$table.className$> getArrayList(int maxRows)
			{
			ArrayList<$table.className$> results = new ArrayList<$table.className$>();
			int count = 0;
			
			try
				{
				while (m_resultSet.next() && (count < maxRows))
					{
					count ++;
					results.add(factory.new$table.className$(m_resultSet));
					}
					
				if (m_resultSet.next())
					throw new GenOrmException("Bound of "+maxRows+" is too small for query ["+m_query+"]");
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (results);
			}
			
		//------------------------------------------------------------------------
		public java.sql.ResultSet getResultSet()
			{
			return (m_resultSet);
			}
			
		//------------------------------------------------------------------------
		public $table.className$ getRecord()
			{
			return (factory.new$table.className$(m_resultSet));
			}
			
		//------------------------------------------------------------------------
		public $table.className$ getOnlyRecord()
			{
			$table.className$ ret = null;
			
			try
				{
				if (m_resultSet.next())
					ret = factory.new$table.className$(m_resultSet);
					
				if (m_resultSet.next())
					throw new GenOrmException("Multiple rows returned in call from $table.className$.getOnlyRecord");
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (ret);
			}
			
		//------------------------------------------------------------------------
		public boolean hasNext()
			{
			boolean ret = false;
			try
				{
				ret = m_resultSet.next();
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
			
			return (ret);
			}
		}
		
	//===========================================================================
		
	$columns:{col | private $javaToGenOrmMap.(col.type)$ m_$col.parameterName$;$\n$}$

	$columns:setAndGetMethods()$	
	
	$foreignKeys:foreignGetAndSetMethods()$
	
	
	//---------------------------------------------------------------------------
	private void initialize(java.sql.ResultSet rs)
		{
		try
			{
			$columns:{col | m_$col.parameterName$.setValue(rs, $i$);$\n$}$
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
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
	/**
		Returns true of the primary keys have the same value as those in obj
	*/
	public boolean equals(Object obj)
		{
		if (!(obj instanceof $table.className$_base))
			return (false);
			
		$table.className$_base other = ($table.className$_base)obj;
		if ($primaryKeys:{key | m_$key.parameterName$.equals(other.m_$key.parameterName$)}; separator=" &&\n\t\t\t\t "$)
			return (true);
		else
			return (false);
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


