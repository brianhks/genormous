package genorm.test;

import genorm.runtime.GenOrmDataSource;
import genorm.test.orm.*;
import org.hsqldb.jdbc.*;
import java.io.*;
import java.sql.*;
import javax.sql.*;
import java.util.*;

import static java.lang.System.out;

public class Main
	{
	private static final String DATABASE_DIR = "database";
	
	public static void main(String[] args)
			throws Exception
		{
		boolean createDB = false;
		File dataDir = new File(DATABASE_DIR);
		if (!dataDir.exists())
			createDB = true;
			
		jdbcDataSource ds = new jdbcDataSource();
		ds.setDatabase("jdbc:hsqldb:file:"+DATABASE_DIR+"/testdb");
		ds.setUser("sa");
		ds.setPassword("");
		
		if (createDB)
			createDatabase(ds);
			
		GenOrmDataSource.setDataSource(new DSEnvelope(ds));
		
		//Now we are ready to use the orm
		runMenu();
		
		Connection c = ds.getConnection();
		c.createStatement().execute("SHUTDOWN");
		c.commit();
		c.close();
		}
		
	//---------------------------------------------------------------------------
	private static void createDatabase(DataSource ds)
			throws Exception
		{
		System.out.println("Creating DB");
		Connection c = ds.getConnection();
		c.setAutoCommit(false);
		
		StringBuilder sb = new StringBuilder();
		FileReader fr = new FileReader("src/genorm/test/orm/create.sql");
		int ch;
		while ((ch = fr.read()) != -1)
			sb.append((char)ch);
			
		String[] tableCommands = sb.toString().split(";");
		
		Statement s = c.createStatement();
		for (String command : tableCommands)
			s.execute(command);
		
		c.commit();
		c.close();
		}
		
	//---------------------------------------------------------------------------
	private static void runMenu()
			throws Exception
		{
		int option;
		Scanner input = new Scanner(System.in);
		do
			{
			out.println("Select an option");
			out.println("1. Add an author");
			out.println("2. Add a book");
			out.println("3. Display books from author");
			out.println("4. Display all authors");
			out.println("5. Display all books");
			out.println("6. Quit");
			option = input.nextInt();
			//input.nextLine();
			switch (option)
				{
				case 1: addAuthor(input);
					break;
				case 2: addBook(input);
					break;
				case 3: displayBooks(input);
					break;
				case 4: displayAllAuthors();
					break;
				case 5: displayAllBooks();
					break;
				}
			}
		while (option != 6);
		}
		
	//---------------------------------------------------------------------------
	private static void addAuthor(Scanner input)
			throws Exception
		{
		out.println("Enter authors name: ");
		String name = input.next();
		
		GenOrmDataSource.begin();
		
		try
			{
			//Example of using a generated key to create a record
			Author author = Author.factory.createWithGeneratedKey();
			author.setName(name);
			
			out.println("Added author \""+name+"\" id = "+author.getAuthorId());
			
			GenOrmDataSource.commit();
			}
		finally
			{
			//The close will auto roll back if the commit has not been done
			GenOrmDataSource.close();
			}
		}
		
	//---------------------------------------------------------------------------
	private static void addBook(Scanner input)
			throws Exception
		{
		out.println("Enter author id:");
		int id = input.nextInt();
		out.println("Enter book title:");
		String title = input.next();
		out.println("Enter isbn:");
		String isbn = input.next();
		
		try
			{
			GenOrmDataSource.begin();
			
			Author author = Author.factory.find(id);
			if (author == null)
				{
				out.println("Not a valid author id");
				return;
				}
				
			//This table has no primary keys so we use the createRecord method
			Book book = Book.factory.createRecord();
			book.setAuthorRef(author);
			//We can chain set methods
			book.setTitle(title).setIsbn(isbn);
			
			out.println("Added book "+title);
			
			GenOrmDataSource.commit();
			}
		finally
			{
			GenOrmDataSource.close();
			}
		}
		
	//---------------------------------------------------------------------------
	private static void displayAllBooks()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		try
			{
			//Query with no parameters
			AllBooksQuery abq = new AllBooksQuery();
			AllBooksQuery.ResultSet rs = abq.runQuery();
			while (rs.next())
				{
				AllBooksData data = rs.getRecord(); 
				out.println(data);
				}
			rs.close();
		
			GenOrmDataSource.commit();
			}
		finally
			{
			GenOrmDataSource.close();
			}
		}
		
	//---------------------------------------------------------------------------
	private static void displayBooks(Scanner input)
			throws Exception
		{
		out.println("Enter author name:");
		String name = input.next();
		
		GenOrmDataSource.begin();
		
		try
			{
			//Query with one parameters.
			BooksByAuthorQuery bbaq = new BooksByAuthorQuery(name);
			BooksByAuthorQuery.ResultSet rs = bbaq.runQuery();
			while (rs.next())
				{
				BooksByAuthorData data = rs.getRecord(); 
				out.println(data);
				}
			rs.close();
		
			GenOrmDataSource.commit();
			}
		finally
			{
			GenOrmDataSource.close();
			}
		}
		
	//---------------------------------------------------------------------------
	private static void displayAllAuthors()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		try
			{
			AllAuthorsQuery aaq = new AllAuthorsQuery();
			AllAuthorsQuery.ResultSet rs = aaq.runQuery();
			while (rs.next())
				{
				AllAuthorsData data = rs.getRecord();
				out.println(data);
				}
			rs.close();
			
			GenOrmDataSource.commit();
			}
		finally
			{
			GenOrmDataSource.close();
			}
		}
	}
