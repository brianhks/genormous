/* 
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
*/

package genorm.unittest;

import org.depunit.*;
import org.dom4j.io.SAXReader;
import org.dom4j.*;
import java.util.*;
import java.io.*;
import genorm.TextReplace;
import genorm.GenUtil;

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
		List<String> testParams = new ArrayList<String>();
		try
			{
			m_queries = new ArrayList<Map<String, ? extends Object>>();
			
			GenUtil genUtil = new GenUtil(file);
			
			SAXReader reader = new SAXReader();
			Document xmldoc = reader.read(new File(file));
			
			Iterator it = xmldoc.selectNodes("//query").iterator();
			while (it.hasNext())
				{
				testParams.clear();
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
				ArrayList<Object> inputList = new ArrayList<Object>();
				
				//Map of real params that references may point to
				Map<String, Element> realParams = new HashMap<String, Element>();
				
				Element inputElement = e.element("input");
				if (inputElement != null)
					{
					Iterator inputs = inputElement.elementIterator("param");
					context = baseContext+"/input/param";
					while (inputs.hasNext())
						{
						Element p = (Element)inputs.next();
						if (p.attributeValue("ref") != null)
							{
							String ref = p.attributeValue("ref");
							p = realParams.get(ref);
							}
							
						String name = p.attributeValue("name");
						realParams.put(name, p);
						String type = genUtil.getJavaType(p.attributeValue("type"));
						String test = p.attributeValue("test");
						if (test == null)
							throw new IllegalArgumentException("No test attribute for "+context+"[@"+p.attributeValue("name")+"]");
							
						testParams.add(test);
						if (type.equals("int"))
							inputList.add(new Integer(test));
						else if (type.equals("String"))
							inputList.add(test);
						else if (type.equals("boolean"))
							inputList.add(new Boolean(test));
						else if (type.equals("java.sql.Timestamp"))
							inputList.add(java.sql.Timestamp.valueOf(test));
						else if (type.equals("java.util.UUID"))
							inputList.add(UUID.fromString(test));
						}
					}
					
				queryData.put(INPUT_PARAMS, inputList);
				
				m_queries.add(queryData);
				}
				
			reset();
			}
		catch (Exception e)
			{
			System.out.println("Test Parameters:");
			for (String t : testParams)
				System.out.println("   '"+t+"'");
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
