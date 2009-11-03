package genorm;

import java.io.IOException;
import org.dom4j.Element;
import java.util.Map;

/**
	Defines the interface for plugins that are used to generate the create.sql file
*/
public interface CreatePlugin extends GenPlugin
	{
	public String getCreateSQL(Table table);
	}
