package genorm.plugins.memcached;

import genorm.QueryPlugin;
import org.dom4j.Element;
import java.util.*;
import org.antlr.stringtemplate.*;
import genorm.TemplateHelper;
import genorm.Query;

public class MemCachePlugin extends TemplateHelper implements QueryPlugin
	{
	public static final String MEMCACHED_CLIENT_PROPERTY = "genorm.plugins.memcached.ClientProperaty";
	
	StringTemplateGroup m_templateGroup;
	
	public MemCachePlugin()
			throws java.io.IOException
		{
		super();
		m_templateGroup = loadTemplateGroup("templates/MemCacheTemplate.java");
		}
		
	public void init(Element e, Properties config)
		{
		System.out.println("Initialized Plugin");
		}
		
	public Set<String> getQueryImports(Map<String, Object> attributes)
		{
		Set<String> imports = new HashSet<String>();
		imports.add("ultramc.MemCachedClient");
		imports.add("java.io.Externalizable");
		imports.add("java.io.Serializable");
		imports.add("java.io.ObjectInput");
		imports.add("java.io.ObjectOutput");
		return (imports);
		}
		
	public Set<String> getQueryImplements(Map<String, Object> attributes)
		{
		Set<String> implementList = new HashSet<String>();
		implementList.add("Serializable");
		
		/* Query q = (Query)attributes.get("query");
		if (!q.isUpdate())
			{
			implementList.add("Externalizable");
			} */
		
		return (implementList);
		}
		
	public Set<String> getQueryRecordImplements(Map<String, Object> attributes)
		{
		Set<String> implementList = new HashSet<String>();
		//implementList.add("Externalizable");
		implementList.add("Serializable");
		return (implementList);
		}
		
	public String getQueryBody(Map<String, Object> attributes)
		{
		StringTemplate st = m_templateGroup.getInstanceOf("QueryBody");
		st.setAttributes(attributes);
		
		return (st.toString());
		}
		
	public String getQueryRecordBody(Map<String, Object> attributes)
		{
		return ("");
		}
		
	
	}
