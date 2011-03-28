import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import ultramc.MemCachedClient;
import java.net.InetSocketAddress;

import genorm.runtime.*;
import test.*;

import static org.junit.Assert.*;

public class MemCacheTests
	{
	public static final String MEMCACHED_CON_POOL = "test connection pool";
	
	private MemCachedClient m_cacheClient;
	
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void loadData()
		{
		GenOrmDataSource.attachAndBegin();
		
		Language lang = Language.factory.createWithGeneratedKey();
		lang.setLanguageCode("en");
		lang.setCountryCode("US");
		
		lang = Language.factory.createWithGeneratedKey();
		lang.setLanguageCode("en");
		lang.setCountryCode("GB");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(cleanupMethod = "closeMemCacheClient")
	public void openMemCacheClient()
			throws IOException
		{
		String memServer = System.getenv().get("memcached_server");
		if (memServer == null)
			memServer = "localhost";
		m_cacheClient = new MemCachedClient(new InetSocketAddress(memServer, 11211));
				
		m_cacheClient.flushAll();
		}
	
	//---------------------------------------------------------------------------
	@Test
	public void closeMemCacheClient()
		{
		m_cacheClient.close();
		}
		
		
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "loadData", "openMemCacheClient" })
	public void firstCacheQuery()
		{
		GenOrmDataSource.attachAndBegin();
		
		SimilarLanguagesQuery slq = new SimilarLanguagesQuery("en");
		SimilarLanguagesQuery.ResultSet rs = slq.getCachedQuery(m_cacheClient, 0);
		while (rs.next())
			System.out.println(rs.getRecord());
		rs.close();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "firstCacheQuery" } )
	public void modifyDB()
		{
		GenOrmDataSource.attachAndBegin();
		
		Language lang = Language.factory.createWithGeneratedKey();
		lang.setLanguageCode("en");
		lang.setCountryCode("AU");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "modifyDB" })
	public void secondCacheQuery()
		{
		/*
			This should pull from cache and not see the en_AU language
		*/
		GenOrmDataSource.attachAndBegin();
		
		SimilarLanguagesQuery slq = new SimilarLanguagesQuery("en");
		SimilarLanguagesQuery.ResultSet rs = slq.getCachedQuery(m_cacheClient, 0);
		while (rs.next())
			{
			SimilarLanguagesData sld = rs.getRecord();
			System.out.println(sld);
			assertFalse(sld.getCountryCode().equals("AU"));
			}
		rs.close();
		
		slq.removeCachedQuery(m_cacheClient);
		
		//Now the AU should be in the list so the size should be 3
		assertEquals(3, slq.getCachedQuery(m_cacheClient, 0).getArrayList(100).size());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	}
