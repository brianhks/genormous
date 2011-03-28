import java.sql.*;
import javax.sql.*;
import org.hsqldb.jdbc.*;
import org.depunit.annotations.*;
import org.depunit.RunContext;
import java.io.*;
import java.util.ArrayList;

import genorm.runtime.*;
import test.*;

import static org.junit.Assert.*;

public class Create
	{
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void ormUnitTests()
		{
		GenOrmDataSource.attachAndBegin();
		
		GenOrmUnitTest.performUnitTests();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(
		hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void createTest()
			throws Exception
		{
		//Logger.getLogger("test").setLevel(Level.FINE);
		
		/* Handler[] handlers = Logger.getLogger( "" ).getHandlers();
		for ( int index = 0; index < handlers.length; index++ )
			{
			System.out.println("handler");
			handlers[index].setLevel( Level.FINE );
			} */
			
		GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		seg.setSource("Hello");
		int key = seg.getSegmentId();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		GenOrmDataSource.attachAndBegin();
		
		seg = Segment.factory.find(key);
		
		
		assertEquals("Hello", seg.getSource());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void findMissingTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.find(1000);
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		assertNull(seg);
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void foreignKeyTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg1 = Segment.factory.createWithGeneratedKey();
		Segment seg2 = Segment.factory.createWithGeneratedKey();
		int key1 = seg1.getSegmentId();
		int key2 = seg2.getSegmentId();
		/* seg1.flush();
		seg2.flush(); */
		seg1.setNextSegmentRef(seg2);
		seg2.setPrevSegmentRef(seg1);
		
		seg1.setSource("segment1");
		seg2.setSource("segment2");
		
		//System.out.println(seg1);
		
		/* seg1.flush();
		seg2.flush(); */
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		//GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.find(key1);
		//System.out.println(seg);
		seg = seg.getNextSegmentRef();
		
		assertNotNull(seg);
		assertEquals("segment2", seg.getSource());
		
		/* GenOrmDataSource.commit();
		GenOrmDataSource.close(); */
		
		//GenOrmDataSource.attachAndBegin();
		
		//Todo: why is the phase getting set on this?
		Note note = Note.factory.createWithGeneratedKey();
		note.isDirty();
		note.setTranslationRef(seg1);
		note.isDirty();
		
		/* GenOrmDataSource.commit();
		GenOrmDataSource.close(); */
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void selectTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		int key = seg.getSegmentId();
		seg.setSource("Segment5");
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		
		GenOrmDataSource.attachAndBegin();
		
		ArrayList<Segment> list = Segment.factory.select("\"source\" = 'Segment5'").getArrayList(10);
		
		assertEquals(1, list.size());
		assertEquals(key, seg.getSegmentId());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void deleteBeforeCreateTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		int key = seg.getSegmentId();
		
		seg.delete();
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		//Verify the segment was not added
		GenOrmDataSource.attachAndBegin();
		
		seg = Segment.factory.find(key);
		
		assertNull(seg);
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void circularDependencyTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg1 = Segment.factory.createWithGeneratedKey();
		Segment seg2 = Segment.factory.createWithGeneratedKey();
		
		seg1.setNextSegmentRef(seg2);
		seg2.setPrevSegmentRef(seg1);
		
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	/**
		This test is for updating a table where the primary key is also a foriegn key
	*/
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void foreignPrimaryKeyTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Segment seg = Segment.factory.createWithGeneratedKey();
		Language lang = Language.factory.createWithGeneratedKey();
		lang.setLanguageCode("en");
		lang.setCountryCode("US");
		
		//Target target = Target.factory.create(1, seg.getSegmentId(), lang.getLanguageId());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	/**
		This tests creating two of the same entries in a single transaction
	*/
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void duplicateCreateTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Language lang1 = Language.factory.create(42);
		Language lang2 = Language.factory.create(42);
		
		assertTrue(lang1 == lang2);  //They should be the same object
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	/**
		This tests creating two of the same entries in a single transaction
	*/
	@Test(hardDependencyOn = { "duplicateCreateTest" })
	public void duplicateFindTest()
			throws Exception
		{
		GenOrmDataSource.attachAndBegin();
		
		Language lang1 = Language.factory.find(42);
		lang1.setLanguageCode("xx");
		Language lang2 = Language.factory.find(42);
		
		assertTrue(lang1 == lang2);  //They should be the same object
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	//---------------------------------------------------------------------------
	/**
		Tests modifying the primary key
	*/
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void modifyPrimaryKeyTest()
		{
		Keys key = Keys.factory.create(1, 1);
		key.setNote("Hello");
		key.flush();
		
		key = Keys.factory.find(1, 1);
		assertEquals("Hello", key.getNote());
		key.setKey1(2);
		key.flush();
		
		assertNotNull(Keys.factory.find(2, 1));
		}
		
	//---------------------------------------------------------------------------
	/**
	
	*/
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void testFactoryDelete()
		{
		Keys key = Keys.factory.create(5, 5);
		key.flush();
		
		assertNotNull(Keys.factory.find(5, 5));
		
		GenOrmDataSource.attachAndBegin();
		
		Keys.factory.delete(5, 5);
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		
		assertNull(Keys.factory.find(5, 5));
		}
		
	//---------------------------------------------------------------------------
	/**
		doing the delete without a transaction
	*/
	@Test(hardDependencyOn = { "HSQLDatabase.createDatabase" })
	public void testFactoryDelete2()
		{
		Keys key = Keys.factory.create(6, 6);
		key.flush();
		
		assertNotNull(Keys.factory.find(6, 6));
		
		Keys.factory.delete(6, 6);
		
		assertNull(Keys.factory.find(6, 6));
		}
	
	}
