package genorm.runtime;

import java.util.*;
import javax.sql.*;

public interface GenOrmDSEnvelope
	{
	public DataSource getDataSource();
		
	public Map<String, GenOrmKeyGenerator> getKeyGeneratorMap();
	}
