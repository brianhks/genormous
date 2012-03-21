package genorm.runtime;

import java.util.*;
import javax.sql.*;
import javax.servlet.ServletContext;

public interface GenOrmServletDSEnvelope extends GenOrmDSEnvelope
	{
	/**
		Called before initialize to set the ServletContext
	*/
	public void setServletContext(ServletContext context);
	}
