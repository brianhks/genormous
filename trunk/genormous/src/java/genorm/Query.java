package genorm;

import org.dom4j.*;
import java.util.*;
import java.util.regex.*;

public class Query
	{
	//XML elements and attribute names
	public static final String NAME = "name";
	public static final String QUERY = "query";
	public static final String TABLE_QUERY = "table_query";
	public static final String INPUT = "input";
	public static final String REPLACE = "replace";
	public static final String RETURN = "return";
	public static final String PARAM = "param";
	public static final String RESULT_TYPE = "result_type";
	public static final String RESULT_NONE = "none";
	public static final String RESULT_SINGLE = "single";
	public static final String RESULT_MULTI = "multi";
	public static final String COMMENT = "comment";
	
	
	private Format m_formatter;
	private String m_queryName;
	private ArrayList<Parameter> m_inputs;
	private ArrayList<Parameter> m_queryInputs;
	private ArrayList<Parameter> m_replacements;
	private ArrayList<Parameter> m_outputs;
	private String m_sqlQuery;
	private String m_comment;
	private boolean m_resultTypeNone;
	private boolean m_resultTypeSingle;
	private boolean m_resultTypeMulti;
	private Map<String, String> m_typeMap;
	private boolean m_skipTest;
	private boolean m_escape = true;;
	
	public Query(Format formatter, String name, ArrayList<Parameter> params, String sql)
		{
		m_formatter = formatter;
		m_queryName = name;
		m_inputs = params;
		m_queryInputs = params;
		m_replacements = new ArrayList<Parameter>();
		m_outputs = new ArrayList<Parameter>();
		m_sqlQuery = sql;
		m_comment = "";
		m_resultTypeSingle = false;
		m_skipTest = true;
		}
	
	public Query(Element queryRoot, Format formatter)
			throws QueryConfigException
		{
		this(queryRoot, formatter, null);
		}
	
	public Query(Element queryRoot, Format formatter, Map<String, String> typeMap)
			throws QueryConfigException
		{
		m_skipTest = false;
		m_inputs = new ArrayList<Parameter>();
		m_comment = "";
		m_typeMap = typeMap;
		m_formatter = formatter;
		m_queryName = queryRoot.attributeValue(NAME);
		String resultType = queryRoot.attributeValue(RESULT_TYPE);
		if ((resultType != null) && (!resultType.equals(RESULT_SINGLE)) &&
				(!resultType.equals(RESULT_MULTI)) && (!resultType.equals(RESULT_NONE)))
			{
			System.out.println("result_type value \""+resultType+"\" must be \"none\", \"single\" or \"multi\"");
			}
			
		m_resultTypeNone = RESULT_NONE.equals(resultType);
		m_resultTypeSingle = RESULT_SINGLE.equals(resultType);
		m_resultTypeMulti = RESULT_MULTI.equals(resultType);
		
		try
			{
			m_queryInputs = getParameters(queryRoot.element(INPUT));
			for (Parameter p : m_queryInputs)
				{
				if (!p.isReference())
					m_inputs.add(p);
				}
			m_replacements = getParameters(queryRoot.element(REPLACE));
			m_outputs = getParameters(queryRoot.element(RETURN));
			
			//Validate the outputs with what is in the select
			Pattern selectPattern = Pattern.compile("select(.+?)from.*", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
			Pattern paramPattern = Pattern.compile(".+as\\s(.+)|.+\\.(.+)|(.+)", Pattern.CASE_INSENSITIVE);
			
			m_sqlQuery = queryRoot.elementTextTrim("sql");
			m_comment = queryRoot.elementTextTrim(COMMENT);
			
			//Option to not parse sql
			boolean parse = queryRoot.element("sql").attributeValue("parse", "yes").equals("yes");
			//The rest of this is just to sanity check the query to make sure 
			//the parameters match up with the query
			Matcher m = selectPattern.matcher(m_sqlQuery);
			if (parse && m.matches())
				{
				String select = m.group(1).trim();
				
				String split[] = splitSelect(select);
				
				// Check split length with m_outputs length to make sure they match
				if (split.length != m_outputs.size())
					{
					System.out.println("Warning, in query \""+m_queryName+"\" output parameter count does not match the select statement");
					
					System.out.println("Select parameters:");
					for (int I = 0; I < split.length; I++)
						System.out.println("  "+split[I].trim());
						
					System.out.println("Declared parameters:");
					for (Parameter par : m_outputs)
						System.out.println("  "+par.getName());
					}
				else
					{
					for (int I = 0; I < split.length; I++)
						{
						String param = split[I];
						m = paramPattern.matcher(param.trim());
						if (m.matches())
							{
							int group;
							for (group = 1; group <= 3; group++)
								if (m.group(group) != null)
									break;
							
							String paramName = m_outputs.get(I).getName();
							String selectParam = m.group(group);
							if ((selectParam.startsWith("\"") && selectParam.endsWith("\"")))
								selectParam = selectParam.substring(1, selectParam.length() -1);
								
							if (!paramName.equals(selectParam))
								{
								System.out.println("Query "+m_queryName+": Param "+paramName+" does not match select statement "+selectParam);
								}
							}
						else
							System.out.println("Query "+m_queryName+": Select param \""+param+"\" does not match regular expression");
						}
					}
				}
			}
		catch (QueryConfigException qce)
			{
			qce.setQuery(m_queryName);
			throw qce;
			}
		}
		
	private Pattern m_tickPattern = Pattern.compile("\\'.*?\\'", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	//private Pattern m_parenPattern = Pattern.compile("\\(.*?,[^\\(]*?\\)", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	
	private static final String TICK_SEC = "@%tick%@";
	private static final String PAREN_SEC = "@%paren%@";
		
	private String[] splitSelect(String select)
		{
		//System.out.println("ORIGINAL SELECT: "+select);
		//System.out.println();
		ArrayList<String> ticks = new ArrayList<String>();
		ArrayList<String> parens = new ArrayList<String>();
		
		Matcher matcher = m_tickPattern.matcher(select);
		while (matcher.find())
			{
			ticks.add(matcher.group());
			}
		select = matcher.replaceAll(TICK_SEC);
		
		int openParen;
		while ((openParen = select.indexOf("(")) != -1)
			{
			int closeParen = 0;
			int parenCount = 1;
			
			for (int I = openParen+1; I < select.length(); I++)
				{
				char c = select.charAt(I);
				
				if (c == '(')
					parenCount++;
				else if (c == ')')
					parenCount--;
					
				if (parenCount == 0)
					{
					closeParen = I;
					break;
					}
				}
				
			if (closeParen != 0)
				{
				parens.add(select.substring(openParen, closeParen+1));
				
				//System.out.println("PAREN SECTION: "+select.substring(openParen, closeParen+1));
				
				select = select.substring(0, openParen) + PAREN_SEC + select.substring(closeParen+1);
				//System.out.println("NEW SELECT: "+select);
				//System.out.println();
				}
			}
			
		//System.out.println(select);
		String[] split = select.split(",");
		
		int tickPos = 0;
		int parenPos = 0;
		
		//Now put back the tick and paren sections
		for (int I = 0; I < split.length; I++)
			{
			while (split[I].indexOf(PAREN_SEC) != -1)
				{
				split[I] = split[I].replaceFirst(PAREN_SEC, parens.get(parenPos++));
				}
			
			while (split[I].indexOf(TICK_SEC) != -1)
				{
				split[I] = split[I].replaceFirst(TICK_SEC, ticks.get(tickPos++));
				}
			}
			
		return (split);
		}
		
		
	//---------------------------------------------------------------------------
	private ArrayList<Parameter> getParameters(Element e)
			throws QueryConfigException
		{
		ArrayList<Parameter> params = new ArrayList<Parameter>();
		if (e != null)
			{
			Map<String, Parameter> paramMap = new HashMap<String, Parameter>();
			Iterator it = e.elementIterator(PARAM);
			while (it.hasNext())
				{
				Element p = (Element)it.next();
				Parameter param;
				if (p.attributeValue(Parameter.REF) != null)
					{
					String refName = p.attributeValue(Parameter.REF);
					if (paramMap.get(refName) != null)
						param = new Parameter(paramMap.get(refName));
					else
						throw new QueryConfigException(refName, "A reference must reference a parameter previously declared");
					}
				else
					{
					param = new Parameter(p, m_formatter, m_typeMap);
					paramMap.put(param.getName(), param);
					}
					
				params.add(param);
				/* String type = p.attributeValue(TYPE);
				if (m_typeMap != null)
					type = (String)m_typeMap.get(type);
				if (p.attribute(TAG) != null)
					params.add(new Parameter(p.attributeValue(NAME), type, p.attributeValue(TAG), m_formatter));
				else
					params.add(new Parameter(p.attributeValue(NAME), type, m_formatter)); */
				}
			}
			
		return (params);
		}
		
	public boolean isSkipTest() { return (m_skipTest); }
	public void setEscape(boolean escape) {m_escape = escape; }
		
	public String getQueryName() { return (m_queryName); }
	public ArrayList<Parameter> getInputs() { return (m_inputs); }
	public ArrayList<Parameter> getQueryInputs() { return (m_queryInputs); }
	public ArrayList<Parameter> getReplacements() { return (m_replacements); }
	public ArrayList<Parameter> getOutputs() { return (m_outputs); }
	public boolean isHasParameters() { return (m_inputs.size() + m_replacements.size() > 0); }
	public boolean isUpdate() { return (m_outputs.size() == 0); }
	public int getOutputsCount() { return (m_outputs.size()); }
	public boolean isReplaceQuery() { return (m_replacements.size() > 0); }
	public String getSqlQuery() 
		{
		if (m_escape)
			return (m_sqlQuery.replaceAll("\\n+", "\\\\n").replace("\"", "\\\""));
		else
			return (m_sqlQuery);
		}
	public String getComment() { return (m_comment); }
	
	public boolean isNoneResult() { return (m_resultTypeNone); }
	public boolean isSingleResult() { return (m_resultTypeSingle); }
	
	public boolean isParamQuery()
		{
		return (m_replacements.size() > 0 || m_inputs.size() > 0);
		}
	
	
	public String getClassName() { return (m_formatter.formatClassName(m_queryName)); }
	
	@Override
	public int hashCode()
		{
		return (m_queryName.hashCode());
		}
		
	@Override
	public boolean equals(Object obj)
		{
		Query other = (Query)obj;
		
		return (other.m_queryName.equals(m_queryName) && other.m_queryInputs.equals(m_queryInputs) &&
				other.m_replacements.equals(m_replacements));
		}
	}
