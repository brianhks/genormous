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

public class $query.className$Query extends genorm.runtime.SQLQuery
	{
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
	
	public $query.className$Query()
		{
		super();
		}
		
	//---------------------------------------------------------------------------
	public void setSerializable(boolean serializable)
		{
		m_serializable = serializable;
		}

$if(query.update)$
	public int runUpdate($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
		{
		try
			{
			String query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | replaceMap.put("$rep.tag$", $rep.parameterName$);}$
			query = replaceText(query, replaceMap);
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
$else$

	//---------------------------------------------------------------------------
	public void serializeQuery(ContentHandler ch, String tagName$if(query.paramQuery)$, $endif$$[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
			throws org.xml.sax.SAXException
		{
		boolean prevSerializeState = m_serializable;
		m_serializable = true;
		ResultSet rs = runQuery($[query.inputs,query.replacements]:{ p | $p.parameterName$}; separator=", "$);
		
		while (rs.next())
			{
			Record rec = rs.getRecord();
			ch.startElement("", tagName, tagName, rec);
			ch.endElement("", tagName, tagName);
			}
			
		m_serializable = prevSerializeState;
		}
	
	//---------------------------------------------------------------------------
	public ResultSet runQuery($[query.inputs,query.replacements]:{ p | $p.type$ $p.parameterName$}; separator=", "$)
		{
		try
			{
			String query = QUERY;
			$if(query.replaceQuery)$
			HashMap<String, String> replaceMap = new HashMap<String, String>();
			$query.replacements:{rep | replaceMap.put("$rep.tag$", $rep.parameterName$);}$
			query = replaceText(query, replaceMap);
			$endif$
			
			java.sql.PreparedStatement statement = GenOrmDataSource.prepareStatement(query);
			$query.inputs:{in | statement.set$javaToJDBCMap.(in.type)$($i$, $in.parameterName$);
}$
			
			java.sql.ResultSet resultSet = statement.executeQuery();
			
			ResultSet ret = new ResultSet(resultSet, statement, query);
			
			return (ret);
			}
		catch (java.sql.SQLException sqle)
			{
			throw new GenOrmException(sqle);
			}
		}
		
	//===========================================================================
	public class ResultSet 
			implements GenOrmQueryResultSet
		{
		private java.sql.ResultSet m_resultSet;
		private java.sql.Statement m_statement;
		private String m_query;
		private boolean m_onFirstResult;
		
		//------------------------------------------------------------------------
		//need to pass in the statement so it can be closed after the result set
		protected ResultSet(java.sql.ResultSet resultSet, java.sql.Statement statement, 
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
		public ArrayList<Record> getArrayList(int maxRows)
			{
			ArrayList<Record> results = new ArrayList<Record>();
			int count = 0;
			
			try
				{
				if (m_onFirstResult)
					{
					count ++;
					results.add(new Record(m_resultSet));
					}
					
				while (m_resultSet.next() && (count < maxRows))
					{
					count ++;
					results.add(new Record(m_resultSet));
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
		public Record getRecord()
			{
			Record ret = null;
			try
				{
				ret = new Record(m_resultSet);
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
		public Record getOnlyRecord()
			{
			Record ret = null;
			
			try
				{
				if (m_resultSet.next())
					ret = new Record(m_resultSet);
					
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
	public class Record implements Attributes, GenOrmQueryRecord
		{
		$query.outputs:{ o | private $o.type$ m_$o.parameterName$;
}$
		private String[] m_attrValues;
		
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

	}
>>