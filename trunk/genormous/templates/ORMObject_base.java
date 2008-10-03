group orm_object_base;

setAndGetMethods(col) ::= <<
//---------------------------------------------------------------------------
/**
	$col.comment$
*/
public $col.type$ get$col.methodName$() { return (m_$col.parameterName$.getValue()); }
public void set$col.methodName$($col.type$ data)
	{
	m_$col.parameterName$.setValue(data);
	
	//TODO: should check if the value has changed
	//Add the now dirty record to the transaction only if it is not previously dirty
	if (m_dirtyFlags == 0)
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		
	m_dirtyFlags |= $col.nameCaps$_FIELD_META.getDirtyFlag();
	}
	
$if(col.allowNull)$
public void set$col.methodName$Null()
	{
	m_$col.parameterName$.setNull();
	
	if (m_dirtyFlags == 0)
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
		
	m_dirtyFlags |= $col.nameCaps$_FIELD_META.getDirtyFlag();
	}
$endif$


>>
	
declarMetaFields(col) ::= <<
private static final GenOrmFieldMeta $col.nameCaps$_FIELD_META = new GenOrmFieldMeta("$col.name$", $col.dirtyFlag$, $col.primaryKey$, $col.foreignKey$);

>>

foreignGetAndSetMethods(foreignKeys) ::= <<
//---------------------------------------------------------------------------
public $foreignKeys.table.className$ get$foreignKeys.methodName$()
	{
	return ($foreignKeys.table.className$.factory.find($foreignKeys.keys:{key | m_$key.parameterName$.getValue()}; separator=", "$));
	}
	
//--------------------------------------------------------------------------
public void set$foreignKeys.methodName$($foreignKeys.table.className$ table)
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
		
		ResultSet rs = new ResultSet(statement.executeQuery(), query, statement);
		
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

/**
	This class has been automatically generated by GenORMous.  This file
	should not be modified.
	
	$table.comment$
*/
public class $table.className$_base extends GenOrmRecord
	{
	$columns:{col | public static final String COL_$col.nameCaps$ = "$col.name$";
}$
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
		/**
			Returns the SQL create statement for this table
		*/
		public String getCreateStatement()
			{
			return (CREATE_SQL);
			}
			
		$if(table.hasPrimaryKey)$
		//---------------------------------------------------------------------------
		/**
			Creates a new entry with the specified primary keys.
		*/
		public $table.className$ create($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			$table.className$ rec = new $table.className$();
			$primaryKeys:{key | (($table.className$_base)rec).set$key.methodName$($key.parameterName$);
}$
			rec.m_isNewRecord = true;
			
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			}
		$endif$
		//---------------------------------------------------------------------------
		/**
			Creates a new entry that is empty
		*/
		public $table.className$ createRecord()
			{
			$table.className$ rec = new $table.className$();
			rec.m_isNewRecord = true;
			
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			}
			
		//---------------------------------------------------------------------------
		/**
		If the table has a primary key that is auto generated this method will 
		return a new table entry with a generated primary key.
		@return $table.className$ with generated primary key
		*/
		public $table.className$ createWithGeneratedKey()
			{
			$if(!table.generatedKey)$
			throw new UnsupportedOperationException("$table.className$ does not support a generated primary key");
			$else$
			$table.className$ rec = new $table.className$();
			
			rec.m_isNewRecord = true;
			rec.set$table.primaryKey.methodName$(
					($table.primaryKey.type$)GenOrmDataSource.getKeyGenerator("$table.name$").generateKey());
			
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
			$endif$
			}
			
		//---------------------------------------------------------------------------
		/**
		A generic api for finding a record.
		@param keys This must match the primary key for this record.  If the 
		record has multiple primary keys this parameter must be of type Object[] 
		where each element is the corresponding key.
		@return $table.className$ or null if no record is found
		*/
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
		/**
			Deletes the record with the specified primary keys.
			@return Returns true if the record existed false otherwise.
		*/
		public boolean delete($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			boolean ret = true;
			$table.className$ rec = new $table.className$();
			
			(($table.className$_base)rec).initialize($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			$table.className$ cacheRec = ($table.className$)GenOrmDataSource.getGenOrmConnection().getCachedRecord(rec);
			
			if (cacheRec != null)
				cacheRec.delete(); //The record was in the cache so set it to be deleted.
			else
				{
				rec.delete();
				try
					{
					//We will try to flush it to make sure the record is there
					rec.flush();
					}
				catch (java.sql.SQLException sqle)
					{
					//The record did not exist so return false
					ret = false;
					}
				}
				
			return (ret);
			}
			
		//---------------------------------------------------------------------------
		/**
		Find the record with the specified primary keys
		@return $table.className$ or null if no record is found
		*/
		public $table.className$ find($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			$table.className$ rec = new $table.className$();
			
			//Create temp object and look in cache for it
			(($table.className$_base)rec).initialize($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			rec = ($table.className$)GenOrmDataSource.getGenOrmConnection().getCachedRecord(rec);
			
			if (rec == null)
				{
				try
					{
					//No cached object so look in db
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
				}
				
			return (rec);
			}
		
		//---------------------------------------------------------------------------
		/**
		This is the same as find except if the record returned is null a new one 
		is created with the specified primary keys
		@return A new or existing record.  
		*/
		public $table.className$ findOrCreate($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
			{
			$table.className$ rec = find($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			if (rec == null)
				rec = create($primaryKeys:{key | $key.parameterName$}; separator=", "$);
				
			return (rec);
			}
			
		$endif$
		//---------------------------------------------------------------------------
		/**
			Convenience method for selecting records.  Ideally this should not be use, 
			instead a custom query for this table should be used.
			@param where sql where statement.
		*/
		public ResultSet select(String where)
			{
			return (select(where, null));
			}
			
		//---------------------------------------------------------------------------
		/**
			Convenience method for selecting records.  Ideally this should not be use, 
			instead a custom query for this table should be used.
			@param where sql where statement.
			@param orderBy sql order by statement
		*/
		public ResultSet select(String where, String orderBy)
			{
			ResultSet rs = null;
			
			try
				{
				java.sql.Statement stmnt = GenOrmDataSource.createStatement();
				StringBuilder sb = new StringBuilder();
				sb.append(SELECT);
				sb.append(FROM);
				if (where != null)
					{
					sb.append(WHERE);
					sb.append(where);
					}
					
				if (orderBy != null)
					{
					sb.append(" ");
					sb.append(orderBy);
					}
				
				String query = sb.toString();
				rs = new ResultSet(stmnt.executeQuery(query), query, stmnt);
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (rs);
			}
			
		$table.queries:addQueryMethods()$
		
		//---------------------------------------------------------------------------
		/**
			Calls all query methods with test parameters.
		*/
		public void testQueryMethods()
			{
			ResultSet rs;
			$table.queries:{ query | System.out.println("$table.className$.get$query.className$");
$if(!query.singleResult)$rs = $endif$get$query.className$($[query.inputs,query.replacements]:{ p | $p.testParam$}; separator=", "$);
$if(!query.singleResult)$rs.close();$endif$
}$
			}
		}
		
	//===========================================================================
	public static class ResultSet 
			implements GenOrmResultSet
		{
		private java.sql.ResultSet m_resultSet;
		private java.sql.Statement m_statement;
		private String m_query;
		private boolean m_onFirstResult;
		
		//------------------------------------------------------------------------
		protected ResultSet(java.sql.ResultSet resultSet, String query, java.sql.Statement statement)
			{
			m_resultSet = resultSet;
			m_statement = statement;
			m_query = query;
			m_onFirstResult = false;
			}
		
		//------------------------------------------------------------------------
		/**
			Closes any underlying java.sql.Result set and java.sql.Statement 
			that was used to create this results set.
		*/
		public void close()
			{
			try
				{
				m_resultSet.close();
				m_statement.close();
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
			}
			
		//------------------------------------------------------------------------
		/**
			Returns the reults as an ArrayList of Record objects.
			The Result set is closed within this call
		*/
		public ArrayList<$table.className$> getArrayList(int maxRows)
			{
			ArrayList<$table.className$> results = new ArrayList<$table.className$>();
			int count = 0;
			
			try
				{
				if (m_onFirstResult)
					{
					count ++;
					results.add(factory.new$table.className$(m_resultSet));
					}
					
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
				sqle.printStackTrace();
				throw new GenOrmException(sqle);
				}
				
			close();
			return (results);
			}
			
		//------------------------------------------------------------------------
		/**
			Returns the underlying java.sql.ResultSet object
		*/
		public java.sql.ResultSet getResultSet()
			{
			return (m_resultSet);
			}
			
		//------------------------------------------------------------------------
		/**
			Returns the current record in the result set
		*/
		public $table.className$ getRecord()
			{
			return (factory.new$table.className$(m_resultSet));
			}
			
		//------------------------------------------------------------------------
		/**
			This call expects only one record in the result set.  If multiple records
			are found an excpetion is thrown.
			The ResultSet object is automatically closed by this call.
		*/
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
				
			close();
			return (ret);
			}
			
		//------------------------------------------------------------------------
		/**
			Returns true if there is another record in the result set.
		*/
		public boolean next()
			{
			boolean ret = false;
			m_onFirstResult = true;
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
	private void initialize($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
		{
		$primaryKeys:{col | m_$col.parameterName$.setValue($col.parameterName$);$\n$}$
		}
		
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
		$if (table.isMTSet)$
		set$table.mTColumn.methodName$(new java.sql.Timestamp(System.currentTimeMillis()));
		$endif$
		}
		
	//---------------------------------------------------------------------------
	public void setCTS()
		{
		$if (table.isCTSet)$
		set$table.cTColumn.methodName$(new java.sql.Timestamp(System.currentTimeMillis()));
		$endif$
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


