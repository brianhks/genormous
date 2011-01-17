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
	public String getConstraintSQL(ForeignKeySet keySet);
	/**
		This it he characters to use when escaping field names in select statements
		Postgress uses double quote (") where mysql uses back tick `
		The code must returned the value as it can appear in a java code string
		so a double quote should be returned as "\"" 
	*/
	public String getFieldEscapeString();
	}
