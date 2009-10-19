package genorm;

import java.util.Map;
import java.util.Properties;
import org.dom4j.Element;

public interface QueryPlugin
	{
	public void init(Element pluginElement);
	public String getIncludes(Map<String, Object> attributes);
	public String getMethods(Map<String, Object> attributes);
	}
