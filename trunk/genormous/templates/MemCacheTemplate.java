group mem_cache;


QueryBody() ::= <<

private static class MemCacheResultSet implements ResultSet, Externalizable
	{
	private transient int m_position;
	private List<$query.className$Data> m_memCacheResults;
	
	public MemCacheResultSet(List<$query.className$Data> rsList)
		{
		m_position = -1;
		m_memCacheResults = rsList;
		}
		
	//---------------------------------------------------------------------------
	/** 
		Constructor used by deserialization
	*/
	public MemCacheResultSet()
		{
		m_position = -1;
		}
		
	//---------------------------------------------------------------------------
	/* ResultSet */
	public List<$query.className$Data> getArrayList(int maxRows)
		{
		return (m_memCacheResults);
		}
		
	//---------------------------------------------------------------------------
	/* ResultSet */
	public $query.className$Data getRecord()
		{
		return (m_memCacheResults.get(m_position));
		}
		
	//---------------------------------------------------------------------------
	/* ResultSet */
	public $query.className$Data getOnlyRecord()
		{
		return (m_memCacheResults.get(0));
		}
		
	//---------------------------------------------------------------------------
	/* ResultSet */
	public void close() {}
	
	//---------------------------------------------------------------------------
	/* ResultSet */
	public java.sql.ResultSet getResultSet() { return (null); }
	
	//---------------------------------------------------------------------------
	/* ResultSet */
	public boolean next()
		{
		return ((++m_position) < m_memCacheResults.size());
		}
		
	//---------------------------------------------------------------------------
	/* Externalizable */
	public void writeExternal(ObjectOutput out)
			throws java.io.IOException
		{
		//System.out.println("writeExternal");
		out.writeInt(m_memCacheResults.size());
		for ($query.className$Data data : m_memCacheResults)
			out.writeObject(data);
		}
		
	//---------------------------------------------------------------------------
	/* Externalizable */
	public void readExternal(ObjectInput in)
			throws java.io.IOException, ClassNotFoundException
		{
		//System.out.println("readExtneral");
		m_memCacheResults = new ArrayList<$query.className$Data>();
		int size = in.readInt();
		for (int I = 0; I < size; I++)
			m_memCacheResults.add(($query.className$Data)in.readObject());
		}
	}
	
	
/**
	Generates the key used to store this query in the cache
*/
public String getCacheKey()
	{
	StringBuilder sb = new StringBuilder();
	sb.append("$query.className$");
	$[query.inputs,query.replacements]:{ p | sb.append(" $p.parameterName$=").append(String.valueOf(m_$p.parameterName$));}; separator="\n"$
	
	return (sb.toString().replaceAll("\\\\s", "_"));
	}

/**
	This call first checks the cache for the query results and if the results
	are not there the database is queried and the results are then placed in 
	cache.
	
	<p>
	The <code>exp</code> value s passed along to memcached exactly as given, and
	will be processed per the memcached protocol specification:
	<blockquote>
	The actual value sent may either be Unix time (number of seconds since 
	January 1, 1970, as a 32-bit value), or a number of seconds starting from 
	current time. In the latter case, this number of seconds may not exceed 
	60*60*24*30 (number of seconds in 30 days); if the number sent by a client 
	is larger than that, the server will consider it to be real Unix time value 
	rather than an offset from current time.
	</blockquote>
	</p>
	@param memClient MemCachedClient, if this value is null then the results of
	<code>runQuery</code> are returned.
	@param exp Some future date when the cache is to expire.  Null for no expire.
*/
public ResultSet getCachedQuery(MemCachedClient memClient, int exp)
	{
	if (memClient == null)
		return (runQuery());
		
	//Create key for query
	String key = getCacheKey();
	
	//Check cache for result set
	MemCacheResultSet cacheResultSet = (MemCacheResultSet)memClient.createGet(key).run().getValue();
	
	//if no cache copy then hit db and send data to cache
	if (cacheResultSet == null)
		{
		List<$query.className$Data> dataList = new ArrayList<$query.className$Data>();
		ResultSet rs = runQuery();
		while (rs.next())
			{
			dataList.add(rs.getRecord());
			}
		rs.close();
			
		cacheResultSet = new MemCacheResultSet(dataList);
		memClient.createSet(key, cacheResultSet).setExpiry(exp).setReply(false).run();
		}
	
	return (cacheResultSet);
	}
	
/**
	Removes this query from the cache.
	
	@param memClient MemCachedClient if null this method does nothing
*/
public void removeCachedQuery(MemCachedClient memClient)
	{
	if (memClient == null)
		return;
		
	//Create key for query
	String key = getCacheKey();
	
	//Remove from cache this query
	memClient.createDelete(key).setReply(false).run();
	}

>>
