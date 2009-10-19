package genorm.plugins.memcached;

import genorm.QueryPlugin;
import org.dom4j.Element;
import java.util.Map;

public class MemCachePlugin implements QueryPlugin
	{
	public MemCachePlugin()
		{
		}
		
	public void init(Element e)
		{
		System.out.println("Initialized Plugin");
		}
		
	public String getIncludes(Map<String, Object> attributes)
		{
		return ("import com.danga.MemCached.MemCachedClient;");
		}
		
	public String getMethods(Map<String, Object> attributes)
		{
		return ("");
		}
	}
