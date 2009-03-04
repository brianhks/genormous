package genorm.unittest;

import org.depunit.*;
import org.dom4j.io.SAXReader;
import org.dom4j.*;
import java.util.*;
import java.io.*;
import genorm.TextReplace;

public class QueryDataDriver extends DataDriver
	{
	private static final String QUERY_NAME = "QueryName";
	private static final String QUERY = "Query";
	private static final String INPUT_PARAMS = "InputParams";
	
	private List<Map<String, ? extends Object>> m_queries;
	private Iterator<Map<String, ? extends Object>> m_iterator;
	
	public QueryDataDriver()
		{
		//System.out.println("QeuryDataDriver loaded");
		}
	
	public void setQueryFile(String file)
			throws Exception
		{
		String baseContext = "";
		String context = "";
		try
			{
			m_queries = new ArrayList<Map<String, ? extends Object>>();
			
			SAXReader reader = new SAXReader();
			Document xmldoc = reader.read(new File(file));
			
			Iterator it = xmldoc.getRootElement().elementIterator("query");
			while (it.hasNext())
				{
				Map<String, Object> queryData = new HashMap<String, Object>();
				Element e = (Element)it.next();
				
				queryData.put(QUERY_NAME, e.attributeValue("name"));
				baseContext = "/queries/query[@"+e.attributeValue("name")+"]";
				context = baseContext;
				String sql = e.elementText("sql");
				
				
				//Get the replace params
				Element rep = e.element("replace");
				if (rep != null)
					{
					context = baseContext+"/replace/param";
					Iterator rparams = rep.elementIterator("param");
					Map<String, String> replaceMap = new HashMap<String, String>();
					while (rparams.hasNext())
						{
						Element p = (Element)rparams.next();
						
						replaceMap.put(p.attributeValue("tag"), p.attributeValue("test"));
						}
						
					TextReplace tr = new TextReplace(sql, "%", false);
					queryData.put(QUERY, tr.replaceTextWith(replaceMap));
					}
				else
					queryData.put(QUERY, sql);
				
					
				//Get the input params
				Iterator inputs = e.element("input").elementIterator("param");
				context = baseContext+"/input/param";
				ArrayList<Object> inputList = new ArrayList<Object>();
				while (inputs.hasNext())
					{
					Element p = (Element)inputs.next();
					String type = p.attributeValue("type");
					String test = p.attributeValue("test");
					if (test == null)
						throw new IllegalArgumentException("No test attribute for "+context+"[@"+p.attributeValue("name")+"]");
					if (type.equals("int"))
						inputList.add(new Integer(test));
					else if (type.equals("String"))
						inputList.add(test);
					else if (type.equals("java.sql.Timestamp"))
						inputList.add(java.sql.Timestamp.valueOf(test));
					}
					
				queryData.put(INPUT_PARAMS, inputList);
				
				m_queries.add(queryData);
				}
				
			reset();
			}
		catch (Exception e)
			{
			System.out.println(context);
			throw e;
			}
		}
		
	
	/*DataDriver*/
	public void reset()
		{
		m_iterator = m_queries.iterator();
		}
	
	/*DataDriver*/
	public boolean hasNextDataSet()
		{
		return (m_iterator.hasNext());
		}
		
	/*DataDriver*/
	public Map<String, ? extends Object> getNextDataSet()
		{
		return (m_iterator.next());
		}
	}
