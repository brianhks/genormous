package genorm.test;

import genorm.test.orm.genorm.GenOrmDataSource;
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
			
		GenOrmDataSource.setDataSource(ds);
		
		//Now we are ready to use the orm
		runMenu();
		
		Connection c = ds.getConnection();
		c.createStatement().execute("SHUTDOWN");
		c.commit();
		c.close();
		}
		
	private static void createDatabase(DataSource ds)
			throws Exception
		{
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
			out.println("5. Quit");
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
				}
			}
		while (option != 5);
		}
		
	private static void addAuthor(Scanner input)
			throws Exception
		{
		out.println("Enter authors name: ");
		String name = input.next();
		
		GenOrmDataSource.begin();
		
		Author author = Author.factory.createWithGeneratedKey();
		author.setName(name);
		
		out.println("Added author \""+name+"\" id = "+author.getAuthorId());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
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
				
			Book book = Book.factory.create();
			book.setAuthorRef(author);
			book.setTitle(title);
			book.setIsbn(isbn);
			
			out.println("Added book "+title);
			}
		finally
			{
			GenOrmDataSource.commit();
			GenOrmDataSource.close();
			}
		}
		
	private static void displayBooks(Scanner input)
			throws Exception
		{
		out.println("Enter author name:");
		String name = input.next();
		
		GenOrmDataSource.begin();
		
		BooksByAuthorQuery bbaq = new BooksByAuthorQuery();
		Iterator<BooksByAuthorQuery.Data> it = bbaq.runQuery(name).iterator();
		while (it.hasNext())
			out.println(it.next());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
		
	private static void displayAllAuthors()
			throws Exception
		{
		GenOrmDataSource.begin();
		
		AllAuthorsQuery aaq = new AllAuthorsQuery();
		Iterator<AllAuthorsQuery.Data> it = aaq.runQuery().iterator();
		while (it.hasNext())
			out.println(it.next());
		
		GenOrmDataSource.commit();
		GenOrmDataSource.close();
		}
	}
