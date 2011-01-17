package genorm;

import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.dom4j.Element;

public interface QueryPlugin extends GenPlugin
	{
	//Additional code for the Query object
	public Set<String> getQueryImports(Map<String, Object> attributes);
	public String getQueryBody(Map<String, Object> attributes);
	public Set<String> getQueryImplements(Map<String, Object> attributes);
	
	//Additional code for the QueryRecord object
	public String getQueryRecordBody(Map<String, Object> attributes);
	public Set<String> getQueryRecordImplements(Map<String, Object> attributes);
	
	//Additional code for ORMObject_base
	}
