group orm_object_base;

license() ::=<<
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
>>

setAndGetMethods(col) ::= <<

//---------------------------------------------------------------------------
/**
	$col.comment$

 	@return $col.type$
*/
public $col.type$ get$col.methodName$() { return (m_$col.parameterName$.getValue()); }
public $table.className$ set$col.methodName$($col.type$ data)
{
	boolean changed = m_$col.parameterName$.setValue(data);
	
	//Add the now dirty record to the transaction only if it is not previously dirty
	if (changed)
	{
		if (m_dirtyFlags.isEmpty())
			GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
			
		m_dirtyFlags.set($col.nameCaps$_FIELD_META.getDirtyFlag());
		
		if (m_isNewRecord) //Force set the prev value
			m_$col.parameterName$.setPrevValue(data);
	}
		
	return (($table.className$)this);
}
	
$if(col.allowNull)$
public boolean is$col.methodName$Null()
{
	return (m_$col.parameterName$.isNull());
}
	
public $table.className$ set$col.methodName$Null()
{
	boolean changed = m_$col.parameterName$.setNull();
	
	if (changed)
	{
		if (m_dirtyFlags.isEmpty())
			GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
			
		m_dirtyFlags.set($col.nameCaps$_FIELD_META.getDirtyFlag());
	}
	
	return (($table.className$)this);
}
$endif$


>>
	
declarMetaFields(col) ::= <<
public static final GenOrmFieldMeta $col.nameCaps$_FIELD_META = new GenOrmFieldMeta("$col.name$", "$col.customType$", $col.dirtyFlag$, $col.primaryKey$, $col.foreignKey$);

>>

foreignGetAndSetMethods(foreignKeys) ::= <<
//---------------------------------------------------------------------------
public $foreignKeys.table.className$ get$foreignKeys.methodName$()
{
	return ($foreignKeys.table.className$.factory.find($foreignKeys.keys:{key | m_$key.parameterName$.getValue()}; separator=", "$));
}
	
//--------------------------------------------------------------------------
public $table.className$ set$foreignKeys.methodName$($foreignKeys.table.className$ table)
{
	//We add the record to the transaction if one of the key values change
	$foreignKeys.keys:{key | if (m_$key.parameterName$.setValue(table.get$key.foreignTableColumnMethodName$()))
{
	if ((m_dirtyFlags.isEmpty()) && (GenOrmDataSource.getGenOrmConnection() != null))
		GenOrmDataSource.getGenOrmConnection().addToTransaction(this);
	
	m_dirtyFlags.set($key.nameCaps$_FIELD_META.getDirtyFlag());
}

}$
		
	return (($table.className$)this);
}


>>

addQueryInterfaceMethods(query) ::= <<
/**
	$query.comment$

$if(query.noneResult)$
 $[query.inputs,query.replacements]:{ p | @param $p.parameterName$ $p.type$}; separator="\n"$
	@return int Number of rows updated
$else$
	$[query.inputs,query.replacements]:{ p | @param $p.parameterName$ $p.type$}; separator="\n"$
	@return Results
$endif$
*/
$if(query.noneResult)$
public int run$query.className$($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$);
$else$
public $if(query.singleResult)$$table.className$$else$ResultSet$endif$ get$query.className$($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$);
$endif$
>>

addQueryMethods(query) ::= <<
//---------------------------------------------------------------------------
/**
	$query.comment$
*/
$if(query.noneResult)$
public int run$query.className$($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
{
	int rows = 0;
	String update = "$query.sqlQuery$";
	$if(query.replaceQuery)$
	HashMap<String, String> replaceMap = new HashMap<String, String>();
	$query.replacements:{rep | replaceMap.put("$rep.tag$", $rep.parameterName$);}$
	query = QueryHelper.replaceText(query, replaceMap);
	$endif$
	
	java.sql.PreparedStatement genorm_statement = null;
	
	try
	{
		genorm_statement = GenOrmDataSource.prepareStatement(update);
		$query.queryInputs:{in | genorm_statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);
}$
		
		s_logger.debug(genorm_statement.toString());
		
		rows = genorm_statement.executeUpdate();
	}
	catch (java.sql.SQLException sqle)
	{
		if (s_logger.isDebugEnabled())
			sqle.printStackTrace();
		throw new GenOrmException(sqle);
	}
	finally
	{
		try
		{
			if (genorm_statement != null)
				genorm_statement.close();
		}
		catch (java.sql.SQLException sqle2)
		{
			throw new GenOrmException(sqle2);
		}
	}
		
	return (rows);
}
	
$else$
public $if(query.singleResult)$$table.className$$else$ResultSet$endif$ get$query.className$($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
{
	String query = SELECT+"$query.sqlQuery$";
	$if(query.replaceQuery)$
	HashMap<String, String> replaceMap = new HashMap<String, String>();
	$query.replacements:{rep | replaceMap.put("$rep.tag$", $rep.parameterName$);}$
	query = QueryHelper.replaceText(query, replaceMap);
	$endif$
	
	java.sql.PreparedStatement genorm_statement = null;
	
	try
	{
		genorm_statement = GenOrmDataSource.prepareStatement(query);
		$query.queryInputs:{in | genorm_statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);}$
		
		s_logger.debug(genorm_statement.toString());
		
		ResultSet rs = new SQLResultSet(genorm_statement.executeQuery(), query, genorm_statement);
		
		$if(query.singleResult)$
		return (rs.getOnlyRecord());
		$else$
		return (rs);
		$endif$
	}
	catch (java.sql.SQLException sqle)
	{
		try
		{
			if (genorm_statement != null)
				genorm_statement.close();
		}
		catch (java.sql.SQLException sqle2) { }
			
		if (s_logger.isDebugEnabled())
			sqle.printStackTrace();
		throw new GenOrmException(sqle);
	}
}
	
$endif$


>>

baseClass(package,table,columns,primaryKeys,foreignKeys,createSQL) ::= <<
package $package$;

import java.util.*;
import org.agileclick.genorm.runtime.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
	This class has been automatically generated by GenORMous.  This file
	should not be modified.
	
	$table.comment$
*/
public class $table.className$_base extends GenOrmRecord
{
	protected static final Logger s_logger = LoggerFactory.getLogger($table.className$.class.getName());

	$columns:{col | public static final String COL_$col.nameCaps$ = "$col.name$";
}$
	//Change this value to true to turn on warning messages
	private static final boolean WARNINGS = false;
	private static final String SELECT = "SELECT $columns:{col | this.$fieldEscape$$col.name$$fieldEscape$}; separator=", "$ ";
	private static final String FROM = "FROM $table.name$ this ";
	private static final String WHERE = "WHERE ";
	private static final String KEY_WHERE = "WHERE $primaryKeys:{key | $fieldEscape$$key.name$$fieldEscape$ = ?}; separator=" AND "$";
	
	public static final String TABLE_NAME = "$table.name$";
	public static final int NUMBER_OF_COLUMNS = $table.numberOfColumns$;
	
	
	private static final String s_fieldEscapeString = "$fieldEscape$"; 
	
	$columns:declarMetaFields()$
	
	$if(table.generatedKey)$
	//===========================================================================
	public static class $table.className$KeyGenerator
			implements GenOrmKeyGenerator
	{
		private static final String MAX_QUERY = "SELECT MAX($fieldEscape$$table.primaryKey.name$$fieldEscape$) FROM $table.name$";
		
		private volatile $table.primaryKey.type$ m_nextKey;
		
		public $table.className$KeyGenerator(javax.sql.DataSource ds)
		{
			m_nextKey = 0;
			java.sql.Connection con = null;
			java.sql.Statement stmnt = null;
			try
			{
				con = ds.getConnection();
				con.setAutoCommit(true);
				stmnt = con.createStatement();
				java.sql.ResultSet rs = stmnt.executeQuery(MAX_QUERY);
				if (rs.next())
					m_nextKey = rs.get$javaToJDBCMap.(table.primaryKey.type)$(1);
				
				rs.close();
			}
			catch (java.sql.SQLException sqle)
			{
				//The exception may occur if the table does not yet exist
				if (WARNINGS)
					System.out.println(sqle);
			}
			finally
			{
				try
				{
					if (stmnt != null)
						stmnt.close();
						
					if (con != null)
						con.close();
				}
				catch (java.sql.SQLException sqle) {}
			}
		}
			
		/**
		This resets the key generator from the values in the database
		Usefull if the generated key has been modified via some other means
		Connection must be open before calling this
		*/
		public synchronized void reset()
		{
			m_nextKey = 0;
			java.sql.Statement stmnt = null;
			java.sql.ResultSet rs = null;
			try
			{
				stmnt = GenOrmDataSource.createStatement();
				rs = stmnt.executeQuery(MAX_QUERY);
				
				if (rs.next())
					m_nextKey = rs.get$javaToJDBCMap.(table.primaryKey.type)$(1);
			}
			catch (java.sql.SQLException sqle)
			{
				//The exception may occur if the table does not yet exist
				if (WARNINGS)
					System.out.println(sqle);
			}
			finally
			{
				try
				{
					if (rs != null)
						rs.close();
						
					if (stmnt != null)
						stmnt.close();
				}
				catch (java.sql.SQLException sqle2)
				{
					throw new GenOrmException(sqle2);
				}
			}
		}
			
		public synchronized Object generateKey()
		{
			m_nextKey++;
			return (m_nextKey);
		}
	}
	$endif$
		
	//===========================================================================
	public static $table.className$FactoryImpl factory = new $table.className$FactoryImpl();
	
	public interface $table.className$Factory extends GenOrmRecordFactory
	{
		$if(table.hasPrimaryKey)$
		boolean delete($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$);
		$table.className$ find($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$);
		$table.className$ findOrCreate($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$);
		$endif$
		$table.queries:addQueryInterfaceMethods()$
	}
	
	public static class $table.className$FactoryImpl //Inherit interfaces
			implements $table.className$Factory 
	{
		public static final String CREATE_SQL = "$createSQL$";

		private ArrayList<GenOrmFieldMeta> m_fieldMeta;
		private ArrayList<GenOrmConstraint> m_foreignKeyConstraints;
		
		protected $table.className$FactoryImpl()
		{
			m_fieldMeta = new ArrayList<GenOrmFieldMeta>();
			$columns:{col | m_fieldMeta.add($col.nameCaps$_FIELD_META);
}$
			m_foreignKeyConstraints = new ArrayList<GenOrmConstraint>();
			$constraints:{con | m_foreignKeyConstraints.add(new GenOrmConstraint("$con.foreignTable$", "$con.constraintName$", "$con.sql$"));
}$
		}
			
		protected $table.className$ new$table.className$(java.sql.ResultSet rs)
		{
			$table.className$ rec = new $table.className$();
			(($table.className$_base)rec).initialize(rs);
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
		}
	
		//---------------------------------------------------------------------------
		/**
			Returns a list of the feild meta for the class that this is a factory of
			@return List of GenOrmFieldMeta
		*/
		public List<GenOrmFieldMeta> getFields()
		{
			return (m_fieldMeta);
		}

		//---------------------------------------------------------------------------
		/**
			Returns a list of foreign key constraints
			@return List of GenOrmConstraint
		*/
		public List<GenOrmConstraint> getForeignKeyConstraints()
		{
			return (m_foreignKeyConstraints);
		}
			
		//---------------------------------------------------------------------------
		/**
			Returns the SQL create statement for this table
			@return SQL create statement
		*/
		public String getCreateStatement()
		{
			return (CREATE_SQL);
		}
			
		$if(table.hasPrimaryKey)$
		//---------------------------------------------------------------------------
		/**
			Creates a new entry with the specified primary keys.
			$primaryKeys:{key | @param $key.parameterName$ $key.type$}; separator="\n"$
			@return new $table.className$
		*/
		public $table.className$ create($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
		{
			$table.className$ rec = new $table.className$();
			rec.m_isNewRecord = true;
			
			$primaryKeys:{key | (($table.className$_base)rec).set$key.methodName$($key.parameterName$);
}$
			
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
		}
		$endif$
		//---------------------------------------------------------------------------
		/**
			Creates a new entry that is empty
			@return new blank $table.className$
		*/
		public $table.className$ createRecord()
		{
			$table.className$ rec = new $table.className$();
			rec.m_isNewRecord = true;
			
			return (($table.className$)GenOrmDataSource.getGenOrmConnection().getUniqueRecord(rec));
		}
			
		//---------------------------------------------------------------------------
		/**
		If the table has a primary key that has a key generator this method will 
		return a new table entry with a generated primary key.
		@return $table.className$ with generated primary key
		*/
		public $table.className$ createWithGeneratedKey()
		{
			$if(!table.singleKey)$
			throw new UnsupportedOperationException("$table.className$ does not support a generated primary key");
			$else$
			$table.className$ rec = new $table.className$();
			
			rec.m_isNewRecord = true;
			
			GenOrmKeyGenerator keyGen = GenOrmDataSource.getKeyGenerator("$table.name$");
			if (keyGen != null)
			{
				rec.set$table.primaryKey.methodName$(
						($javaToObjectType.(table.primaryKey.type)$)keyGen.generateKey());
			}
			
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
			The point of this api is to prevent a hit on the db to see if the record
			is there.  This call will add a record to the next transaction that is 
			marked for delete. 
			
			@return Returns true if the record was previous created and existed
			either in the transaction cache or the db.
		*/
		public boolean delete($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
		{
			boolean ret = false;
			$table.className$ rec = new $table.className$();
			
			(($table.className$_base)rec).initialize($primaryKeys:{key | $key.parameterName$}; separator=", "$);
			GenOrmConnection con = GenOrmDataSource.getGenOrmConnection();
			$table.className$ cachedRec = ($table.className$)con.getCachedRecord(rec.getRecordKey());
			
			if (cachedRec != null)
			{
				ret = true;
				cachedRec.delete();
			}
			else
			{
				rec = ($table.className$)con.getUniqueRecord(rec);  //This adds the record to the cache
				rec.delete();
				ret = rec.flush();
				rec.setIgnored(true); //So the system does not try to delete it again at commmit
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
			rec = ($table.className$)GenOrmDataSource.getGenOrmConnection().getCachedRecord(rec.getRecordKey());
			
			java.sql.PreparedStatement genorm_statement = null;
			java.sql.ResultSet genorm_rs = null;
			
			if (rec == null)
			{
				try
				{
					//No cached object so look in db
					genorm_statement = GenOrmDataSource.prepareStatement(SELECT+FROM+KEY_WHERE);
					$primaryKeys:{key | genorm_statement.set$javaToJDBCMap.(key.type)$($i$, $key.parameterName$);
}$
					s_logger.debug(genorm_statement.toString());
						
					genorm_rs = genorm_statement.executeQuery();
					if (genorm_rs.next())
						rec = new$table.className$(genorm_rs);
				}
				catch (java.sql.SQLException sqle)
				{
					throw new GenOrmException(sqle);
				}
				finally
				{
					try
					{
						if (genorm_rs != null)
							genorm_rs.close();
							
						if (genorm_statement != null)
							genorm_statement.close();
					}
					catch (java.sql.SQLException sqle2)
					{
						throw new GenOrmException(sqle2);
					}
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
			@return {@link ResultSet}
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
			@return {@link ResultSet}
		*/
		public ResultSet select(String where, String orderBy)
		{
			ResultSet rs = null;
			java.sql.Statement stmnt = null;
			
			try
			{
				stmnt = GenOrmDataSource.createStatement();
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
				rs = new SQLResultSet(stmnt.executeQuery(query), query, stmnt);
			}
			catch (java.sql.SQLException sqle)
			{
				try
				{
					if (stmnt != null)
						stmnt.close();
				}
				catch (java.sql.SQLException sqle2) { }
					
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
			$table.queries:{ query | $if(!query.skipTest)$System.out.println("$table.className$.get$query.className$");
$if(query.noneResult)$run$query.className$($[query.inputs,query.replacements]:{ p | $p.testParam$}; separator=", "$);
$else$
$if(!query.singleResult)$rs = $endif$get$query.className$($[query.inputs,query.replacements]:{ p | $p.testParam$}; separator=", "$);
$if(!query.singleResult)$rs.close();$endif$$endif$$endif$
}$
		}
	}
		
	//===========================================================================
	public interface ResultSet extends GenOrmResultSet
	{
		ArrayList<$table.className$> getArrayList(int maxRows);
		ArrayList<$table.className$> getArrayList();
		$table.className$ getRecord();
		$table.className$ getOnlyRecord();
	}
		
	//===========================================================================
	private static class SQLResultSet 
			implements ResultSet
	{
		private java.sql.ResultSet m_resultSet;
		private java.sql.Statement m_statement;
		private String m_query;
		private boolean m_onFirstResult;
		
		//------------------------------------------------------------------------
		protected SQLResultSet(java.sql.ResultSet resultSet, String query, java.sql.Statement statement)
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
			@param maxRows if the result set contains more than this param
				then an exception is thrown
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
			Returns the reults as an ArrayList of Record objects.
			The Result set is closed within this call
		*/
		public ArrayList<$table.className$> getArrayList()
		{
			ArrayList<$table.className$> results = new ArrayList<$table.className$>();
			
			try
			{
				if (m_onFirstResult)
					results.add(factory.new$table.className$(m_resultSet));
					
				while (m_resultSet.next())
					results.add(factory.new$table.className$(m_resultSet));
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
		
	$columns:{col | protected $javaToGenOrmMap.(col.type)$ m_$col.parameterName$;$\n$}$
	
	private List<GenOrmRecordKey> m_foreignKeys;
	
	public List<GenOrmRecordKey> getForeignKeys() { return (m_foreignKeys); }

	$columns:setAndGetMethods()$	
	
	$foreignKeys:foreignGetAndSetMethods()$
	
	
	//---------------------------------------------------------------------------
	protected void initialize($primaryKeys:{key | $key.type$ $key.parameterName$}; separator=", "$)
	{
		$primaryKeys:{col | m_$col.parameterName$.setValue($col.parameterName$);
m_$col.parameterName$.setPrevValue($col.parameterName$);$\n$}$
	}
		
	//---------------------------------------------------------------------------
	protected void initialize(java.sql.ResultSet rs)
	{
		try
		{
			if (s_logger.isDebugEnabled())
			{
				java.sql.ResultSetMetaData meta = rs.getMetaData();
				for (int I = 1; I <= meta.getColumnCount(); I++)
				{
					s_logger.debug("Reading - "+meta.getColumnName(I) +" : "+rs.getString(I));
				}
			}
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
		m_logger = s_logger;
		m_foreignKeys = new ArrayList<GenOrmRecordKey>();
		m_dirtyFlags = new java.util.BitSet(NUMBER_OF_COLUMNS);
		
		$columns:{col | 
m_$col.parameterName$ = new $javaToGenOrmMap.(col.type)$($col.nameCaps$_FIELD_META);
addField(COL_$col.nameCaps$, m_$col.parameterName$);$\n$}$
		GenOrmRecordKey foreignKey;
		$foreignKeys:{key | foreignKey = new GenOrmRecordKey("$key.tableName$");
$key.keys:{col | foreignKey.addKeyField("$col.foreignTableColumnName$", m_$col.parameterName$);$\n$}$
m_foreignKeys.add(foreignKey);
}; separator="\n"$
	}
	
	//---------------------------------------------------------------------------
	@Override
	public GenOrmConnection getGenOrmConnection()
	{
		return (GenOrmDataSource.getGenOrmConnection());
	}
		
	//---------------------------------------------------------------------------
	@Override
	public String getFieldEscapeString()
	{
		return (s_fieldEscapeString);
	}
		
	//---------------------------------------------------------------------------
	@Override
	public void setMTS()
	{
		$if (table.isMTSet)$
		set$table.mTColumn.methodName$(new java.sql.Timestamp(System.currentTimeMillis()));
		$endif$
	}
		
	//---------------------------------------------------------------------------
	@Override
	public void setCTS()
	{
		$if (table.isCTSet)$
		set$table.cTColumn.methodName$(new java.sql.Timestamp(System.currentTimeMillis()));
		$endif$
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
		
	//===========================================================================

	$plugins$	
	
}
	
	
>>


