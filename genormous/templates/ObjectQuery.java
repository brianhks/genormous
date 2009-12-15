group object_query;


objectQuery() ::= <<
/**
This file is automatically generated.  Do not modify
*/
package $package$;

import java.util.Locale;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.sql.Timestamp;
import org.xml.sax.ContentHandler;
import org.xml.sax.Attributes;
import genorm.runtime.*;

$importList:{imp | import $imp$;}; separator="\n"$

/**
	$query.comment$
*/
public class $query.className$Query extends genorm.runtime.SQLQuery
		$if(queryImplementSetNotEmpty)$implements $queryImplementSet:{imp | $imp$}; separator=", "$$endif$
	{
	private static final Logger s_logger = LoggerFactory.getLogger($query.className$Query.class.getName());
	
	public static final String QUERY_NAME = "$query.queryName$";
	public static final String QUERY = "$query.sqlQuery$";
	private static final int ATTRIBUTE_COUNT = $query.outputsCount$;
	private static Map<String, Integer> s_attributeIndex;
	private static String[] s_attributeNames = {
			$query.outputs:{out | "$out.xmlName$"}; separator=",\n"$ };
			
	static
		{
		s_attributeIndex = new HashMap<String, Integer>();
		for (int I = 0; I < ATTRIBUTE_COUNT; I++)
			s_attributeIndex.put(s_attributeNames[I], I);
		}
	
	private boolean m_serializable;
	
	$[query.inputs,query.replacements]:{ p | private $p.type$ m_$p.parameterName$;}; separator="\n"$

$if(query.hasParameters)$
	//Deprecated
	public $query.className$Query()
		{
		super();
		}
$endif$
		
	//---------------------------------------------------------------------------
	public $query.className$Query($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
		{
		super();
		$[query.inputs,query.replacements]:{ p | m_$p.parameterName$ = $p.parameterName$;}; separator="\n"$
		}
		
	//---------------------------------------------------------------------------
	public String getQueryName() { return (QUERY_NAME); }
	
	//---------------------------------------------------------------------------
	public String getQuery() { return (QUERY); }
		
	//---------------------------------------------------------------------------
	public void setSerializable(boolean serializable)
		{
		m_serializable = serializable;
		}
	
	//---------------------------------------------------------------------------
	public String toString()
		{
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		$[query.inputs,query.replacements]:{ p | sb.append(" $p.parameterName$=").append(String.valueOf(m_$p.parameterName$));}; separator="\n"$
		
		return (sb.toString());
		}
		
	
$if(query.update)$
$if(query.hasParameters)$
	//---------------------------------------------------------------------------
	//Deprecated
	public int runUpdate($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
		{
		try
			{
			String query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | replaceMap.put("$rep.tag$", String.valueOf($rep.parameterName$));}$
			query = QueryHelper.replaceText(query, replaceMap);
			$endif$
			
			java.sql.PreparedStatement statement = GenOrmDataSource.prepareStatement(query);
			$query.inputs:{in | statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);
}$
			
			int ret = statement.executeUpdate();
			
			statement.close();
			return (ret);
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
$endif$
	
	//---------------------------------------------------------------------------
	public int runUpdate()
		{
		try
			{
			String query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | replaceMap.put("$rep.tag$", String.valueOf(m_$rep.parameterName$));}$
			query = QueryHelper.replaceText(query, replaceMap);
			$endif$
			
			java.sql.PreparedStatement statement = GenOrmDataSource.prepareStatement(query);
			$query.inputs:{in | statement.set$javaToJDBCMap.(in.type)$($i$, m_$in.parameterName$);
}$
			
			int ret = statement.executeUpdate();
			
			statement.close();
			return (ret);
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
$else$

	//---------------------------------------------------------------------------
	public void serializeQuery(ContentHandler ch, String tagName)
			throws org.xml.sax.SAXException
		{
		boolean prevSerializeState = m_serializable;
		m_serializable = true;
		ResultSet rs = runQuery();
		
		while (rs.next())
			{
			$query.className$Data rec = rs.getRecord();
			ch.startElement("", tagName, tagName, rec);
			ch.endElement("", tagName, tagName);
			}
			
		m_serializable = prevSerializeState;
		}
	
$if(query.hasParameters)$
	//---------------------------------------------------------------------------
	//Deprecated
	public ResultSet runQuery($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
		{
		try
			{
			String genorm_query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> genorm_replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | genorm_replaceMap.put("$rep.tag$", String.valueOf($rep.parameterName$));}$
			genorm_query = QueryHelper.replaceText(genorm_query, genorm_replaceMap);
			$endif$
			
			java.sql.PreparedStatement genorm_statement = GenOrmDataSource.prepareStatement(genorm_query);
			$query.inputs:{in | genorm_statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);
}$
			long genorm_queryTimeStart = 0L;
			if (s_logger.isInfo())
				{
				genorm_queryTimeStart = System.currentTimeMillis();
				}
				
			java.sql.ResultSet genorm_resultSet = genorm_statement.executeQuery();
			
			if (genorm_queryTimeStart != 0L)
				{
				long genorm_quryTime = System.currentTimeMillis() - genorm_queryTimeStart;
				s_logger.info(genorm_quryTime);
				}
			
			ResultSet genorm_ret = new SQLResultSet(genorm_resultSet, genorm_statement, genorm_query);
			
			return (genorm_ret);
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
$endif$
		
	//---------------------------------------------------------------------------
	public ResultSet runQuery()
		{
		try
			{
			String genorm_query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> genorm_replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | genorm_replaceMap.put("$rep.tag$", String.valueOf(m_$rep.parameterName$));}$
			genorm_query = QueryHelper.replaceText(genorm_query, genorm_replaceMap);
			$endif$
			
			java.sql.PreparedStatement genorm_statement = GenOrmDataSource.prepareStatement(genorm_query);
			$query.inputs:{in | genorm_statement.set$javaToJDBCMap.(in.type)$($i$, m_$in.parameterName$);
}$
			long genorm_queryTimeStart = 0L;
			if (s_logger.isInfo())
				{
				genorm_queryTimeStart = System.currentTimeMillis();
				}
				
			java.sql.ResultSet genorm_resultSet = genorm_statement.executeQuery();
			
			if (genorm_queryTimeStart != 0L)
				{
				long genorm_quryTime = System.currentTimeMillis() - genorm_queryTimeStart;
				s_logger.info(genorm_quryTime);
				}
			
			ResultSet genorm_ret = new SQLResultSet(genorm_resultSet, genorm_statement, genorm_query);
			
			return (genorm_ret);
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	//===========================================================================
	//Plugin Classes and Methods
	$pluginQueryBody$
		
	//===========================================================================
	public interface ResultSet extends GenOrmQueryResultSet
		{
		public List<$query.className$Data> getArrayList(int maxRows);
		public $query.className$Data getRecord();
		public $query.className$Data getOnlyRecord();
		}
		
	//===========================================================================
	private class SQLResultSet 
			implements ResultSet
		{
		private java.sql.ResultSet m_resultSet;
		private java.sql.Statement m_statement;
		private String m_query;
		private boolean m_onFirstResult;
		
		//------------------------------------------------------------------------
		//need to pass in the statement so it can be closed after the result set
		protected SQLResultSet(java.sql.ResultSet resultSet, java.sql.Statement statement, 
				String query)
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
		public List<$query.className$Data> getArrayList(int maxRows)
			{
			ArrayList<$query.className$Data> results = new ArrayList<$query.className$Data>();
			int count = 0;
			
			try
				{
				if (m_onFirstResult)
					{
					count ++;
					results.add(new $query.className$Data($query.className$Query.this, m_resultSet));
					}
					
				while (m_resultSet.next() && (count < maxRows))
					{
					count ++;
					results.add(new $query.className$Data($query.className$Query.this, m_resultSet));
					}
					
				if (m_resultSet.next())
					throw new GenOrmException("Bound of "+maxRows+" is too small for query ["+m_query+"]");
				}
			catch (java.sql.SQLException sqle)
				{
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
		public $query.className$Data getRecord()
			{
			$query.className$Data ret = null;
			try
				{
				ret = new $query.className$Data($query.className$Query.this, m_resultSet);
				}
			catch (java.sql.SQLException sqle)
				{
				throw new GenOrmException(sqle);
				}
				
			return (ret);
			}
			
		//------------------------------------------------------------------------
		/**
			This call expects only one record in the result set.  If multiple records
			are found an excpetion is thrown.
			The ResultSet object is automatically closed by this call.
		*/
		public $query.className$Data getOnlyRecord()
			{
			$query.className$Data ret = null;
			
			try
				{
				if (m_resultSet.next())
					ret = new $query.className$Data($query.className$Query.this, m_resultSet);
					
				if (m_resultSet.next())
					throw new GenOrmException("Multiple rows returned in call from $query.className$Query.ResultSet.getOnlyRecord");
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
	public class Record extends GenOrmQueryRecord implements Attributes$recordImplementSet:{imp | , $imp$}$
		{
		$query.outputs:{ o | protected $o.type$ m_$o.parameterName$;
}$
		protected String[] m_attrValues;
		
		protected Record(java.sql.ResultSet rs)
				throws java.sql.SQLException
			{
			$query.outputs:{ o | m_$o.parameterName$ = rs.get$javaToJDBCMap.(o.type)$($i$);
}$
			if (m_serializable)
				{
				m_attrValues = new String[ATTRIBUTE_COUNT];
				
				$query.outputs:{ o | m_attrValues[$i0$] = $query.className$Query.this.m_formatter.toString(s_attributeNames[$i0$], m_$o.parameterName$);
}$
				}
			}
			
		$query.outputs:{ param | public $param.type$ $if(param.booleanType)$is$else$get$endif$$param.methodName$() { return (m_$param.parameterName$); }
}$ 
		
		//------------------------------------------------------------------------
		public String toString()
			{
			StringBuilder sb = new StringBuilder();
			$query.outputs:{ o | sb.append(" $o.name$=\"");
sb.append(m_$o.parameterName$);
sb.append("\"");
}$
			return (sb.toString().trim());
			}
			
		//------------------------------------------------------------------------
		/*Attributes*/
		public int getIndex(String qName)
			{
			Integer index = s_attributeIndex.get(qName);
			if (index == null)
				return (-1);
			else
				return (index);
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public int getIndex(String uri, String localName)
			{
			if (uri == null || uri.equals(""))
				return (getIndex(localName));
			else
				return (-1);
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public int getLength() { return (ATTRIBUTE_COUNT); }
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getLocalName(int index)
			{
			if (index < 0 || index >= ATTRIBUTE_COUNT)
				return (null);
			else
				return (s_attributeNames[index]);
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getQName(int index)
			{
			if (index < 0 || index >= ATTRIBUTE_COUNT)
				return (null);
			else
				return (s_attributeNames[index]);
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getType(int index) { return ("CDATA"); }
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getType(String qName) { return ("CDATA"); }
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getType(String uri, String localName) { return ("CDATA"); }
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getURI(int index)
			{
			if (index < 0 || index >= ATTRIBUTE_COUNT)
				return (null);
			else
				return ("");
			}
			
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getValue(int index)
			{
			if (index < 0 || index >= ATTRIBUTE_COUNT)
				return (null);
			else
				return (m_attrValues[index]);
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getValue(String qName)
			{
			return (getValue(getIndex(qName)));
			}
		
		//------------------------------------------------------------------------
		/*Attributes*/
		public String getValue(String uri, String localName)
			{
			return (getValue(getIndex(uri, localName)));
			}
			
		}
		
$endif$

	$pluginMethods$
	}
>>


objectQueryData() ::= <<
package $package$;

/**
	This class has been automatically generated by GenORMous.  This file is for
	adding custom code to.  This file will not be regenerated once it exists.
	
	$query.comment$
*/
public class $query.className$Data extends $query.className$Query.Record
	{
	public $query.className$Data($query.className$Query query, java.sql.ResultSet resultSet)
			throws java.sql.SQLException
		{
		query.super(resultSet);
		}
	}
>>
