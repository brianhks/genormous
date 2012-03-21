package genorm;

import java.util.Map;
import java.util.Set;
import java.io.IOException;

public interface ORMPlugin extends GenPlugin
	{
	public Set<String> getImplements(Map<String, Object> attributes);
	public String getBody(Map<String, Object> attributes) throws IOException;
	}
