import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;
import java.util.ArrayList;

import test.genorm.*;
import test.*;

import static org.junit.Assert.*;

public class Create
	{
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "Database.createDatabase" })
	public void createTest()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		seg.setSource("Hello");
		int key = seg.getSegmentId();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		GenOrmDataSource.begin();
		
		seg = Segment.factory.find(key);
		
		
		assertEquals("Hello", seg.getSource());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "Database.createDatabase" })
	public void findMissingTest()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		Segment seg = Segment.factory.find(1000);
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		assertNull(seg);
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "Database.createDatabase" })
	public void foreignKeyTest()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		Segment seg1 = Segment.factory.createWithGeneratedKey();
		Segment seg2 = Segment.factory.createWithGeneratedKey();
		int key1 = seg1.getSegmentId();
		int key2 = seg2.getSegmentId();
		seg1.flush();
		seg2.flush();
		seg1.setNextSegmentRef(seg2);
		seg2.setPrevSegmentRef(seg1);
		
		seg1.setSource("segment1");
		seg2.setSource("segment2");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		GenOrmDataSource.begin();
		
		Segment seg = Segment.factory.find(key1);
		seg = seg.getNextSegmentRef();
		
		assertNotNull(seg);
		assertEquals("segment2", seg.getSource());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "Database.createDatabase" })
	public void selectTest()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		int key = seg.getSegmentId();
		seg.setSource("Segment5");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		
		GenOrmDataSource.begin();
		
		ArrayList<Segment> list = Segment.factory.select("source = 'Segment5'");
		
		assertEquals(1, list.size());
		assertEquals(key, seg.getSegmentId());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "Database.createDatabase" })
	public void deleteBeforeCreateTest()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		int key = seg.getSegmentId();
		
		seg.delete();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		//Verify the segment was not added
		GenOrmDataSource.begin();
		
		seg = Segment.factory.find(key);
		
		assertNull(seg);
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	//---------------------------------------------------------------------------
	}
