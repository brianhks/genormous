package genorm;

import java.io.*;
import org.dom4j.*;
import java.util.*;
import org.jargp.*;
import org.antlr.stringtemplate.*;
import genorm.runtime.GenOrmException;
import genorm.runtime.GenOrmConstraint;

import static java.lang.System.out;

public class Genormous extends GenUtil
	{
	//XML elements and attribute names
	public static final String NAME = "name";
	public static final String COMMENT = "comment";
	public static final String TYPE = "type";
	public static final String PRIMARY_KEY = "primary_key";
	public static final String UNIQUE = "unique";
	public static final String UNIQUE_SET = "unique_set";
	public static final String COL = "col";
	public static final String COL_REF = "colref";
	public static final String TABLE = "table";
	public static final String GLOBAL = "global";
	public static final String COLUMN = "column";
	public static final String REF_TABLE = "reftable";
	public static final String REFERENCE = "reference";
	public static final String PROPERTY = "property";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	public static final String QUERY = "query";
	public static final String DEFAULT_VALUE = "default_value";
	public static final String ALLOW_NULL = "allow_null";
	public static final String AUTO_SET = "auto_set";
	public static final String ON_UPDATE = "on_update";
	public static final String ON_DELETE = "on_delete";
	
	
	//private String m_destDir;
	//private Map<String, String> m_typeMap;
	//private Format m_formatter;
	//private String m_packageName;
	//private boolean m_includeStringSets;
	//private String m_graphVizFile;
	//private String m_databaseType;
	
	
	//===========================================================================
	private static class CommandLine
		{
		public String source;
		public String destination;
		public String targetPackage;
		public String customTypeProperties;
		public String customDBTypeProperties;
		public String databaseType;
		public String graphVizFile;
		public boolean help;
		public boolean verbose;
		
		public CommandLine()
			{
			source = null;
			destination = null;
			targetPackage = null;
			customTypeProperties = null;
			customDBTypeProperties = null;
			graphVizFile = null;
			databaseType = null;
			help = false;
			verbose = false;
			}
		}
		
	//===========================================================================
	private static final ParameterDef[] PARAMETERS = 
		{
		new StringDef('d', "destination"),
		new StringDef('p', "targetPackage"),
		new StringDef('s', "source"),
		new StringDef('c', "customTypeProperties"),
		new StringDef('b', "customDBTypeProperties"),
		//new StringDef('t', "databaseType"),
		new StringDef('g', "graphVizFile"),
		new BoolDef('?', "help"),
		new BoolDef('v', "verbose")
		};
		
		
	//---------------------------------------------------------------------------
	private static void printHelp()
		{
		out.println("GenORMous version X");
		out.println("Usage: java -jar genormous.jar -o <source xml> -[d <dest dir>] [-p <package>]");
		out.println("      [-s] [-c <custom type properties>] [-b <custom db properties>]");
		out.println("      [-t <database type>] [-g <graphViz file>] [-?]");
		out.println("  -o: Source xml containing table definitions.");
		out.println("  -d: Destination dir to write generated files to.");
		out.println("  -p: Package name of generated files.");
		//out.println("  -s: .");
		out.println("  -c: Custom java type properties file.");
		out.println("  -b: Custom DB type properties file.");
		//out.println("  -t: Database type.");
		out.println("  -g: Name of graphViz dot file to generate.");
		out.println("  -v: Verbose output.");
		}
		
	
//==============================================================================
	
		
/* //------------------------------------------------------------------------------
	private String getCreateCommands(Iterator<String> it)
		{
		StringBuffer sb = new StringBuffer();
		
		while (it.hasNext())
			{
			sb.append("\t\tsb.append("+m_formatter.formatClassName(it.next())+".s_meta.createTableSQL());\n");
			sb.append("\t\tsb.append(\";\");\n");
			}
			
		return (sb.toString());
		} */

//------------------------------------------------------------------------------
	public static void main(String[] args)
		{
		CommandLine cl = new CommandLine();
		ArgumentProcessor proc = new ArgumentProcessor(PARAMETERS);
		
		proc.processArgs(args, cl);
		
		if (cl.help || (args.length == 0))
			{
			printHelp();
			return;
			}
			
		try
			{
			Genormous gen = new Genormous(cl.source); /* , cl.destination, cl.targetPackage,
					cl.includeStringSets, cl.graphVizFile); */
					
			if (cl.destination != null)
				gen.setDestination(cl.destination);
				
			if (cl.targetPackage != null)
				gen.setPackage(cl.targetPackage);
				
			if (cl.graphVizFile != null)
				gen.setGraphvizFile(cl.graphVizFile);
					
			if (cl.customTypeProperties != null)
				gen.setTypesFile(cl.customTypeProperties);
				
					
			QueryGen qgen = new QueryGen(cl.source);
			
			if (cl.destination != null)
				qgen.setDestination(cl.destination);
				
			if (cl.targetPackage != null)
				qgen.setPackage(cl.targetPackage);
			
			if (cl.customTypeProperties != null)
				qgen.setTypesFile(cl.customTypeProperties);
		
			System.out.println("Generating classes");
			gen.generateClasses();
			System.out.println("Generating queries");
			qgen.generateClasses();
			}
		catch (Exception e)
			{
			e.printStackTrace();
			}
		}

		
//==============================================================================
	public Genormous(String source)
			throws ConfigurationException
		{
		super(source);
		
		/* super.setDestinationDir(destDir);
		m_source = source;
		m_typeMap = readPropertiesFile(new PropertiesFile("types.properties"));
		m_formatter = new DefaultFormat();
		m_packageName = packageName;
		m_includeStringSets = includeStringSets;
		m_graphVizFile = graphVizFile;
		m_databaseType = "hsqldb"; */
		}
		
		
		
//------------------------------------------------------------------------------
	public void setGraphvizFile(String graphvizFile)
		{
		m_config.setProperty(PROP_GRAPHVIZ_FILE, graphvizFile);
		}
		
		
//------------------------------------------------------------------------------
	public void generateClasses()
			throws Exception
		{
		//TextReplace baseClass = null, derivedClass = null;
		Queue<Table> tables = new LinkedList<Table>();
		//Table table;
		Map<String, Table> tableNames = new HashMap<String, Table>();
		
		String destDir = getRequiredProperty(PROP_DESTINATION);
		super.setDestinationDir(destDir);
		new File(destDir).mkdirs();
		
		Format formatter = super.getFormat();
		
		//Look for the CreatePlugin for the db type
		CreatePlugin createPlugin = null;
		for (GenPlugin gp : m_pluginList)
			{
			if (gp instanceof CreatePlugin)
				{
				createPlugin = (CreatePlugin)gp;
				break;
				}
			}
		
		try
			{
			StringTemplateGroup dataTypeMapGroup = loadTemplateGroup("templates/data_type_maps.st");
			
			//Switch on the type of database
			//String propFile = "templates/"+m_databaseType+"_types.properties";
			dataTypeMapGroup.defineMap("typeToSQLTypeMap", m_dbTypeMap);
			
			StringTemplateGroup ormBaseObjectTG = loadTemplateGroup("templates/ORMObject_base.java");
			ormBaseObjectTG.setSuperGroup(dataTypeMapGroup);
			StringTemplateGroup ormObjectTG = loadTemplateGroup("templates/ORMObject.java");
			ormObjectTG.setSuperGroup(dataTypeMapGroup);
			
			
			//Read in global column definitions
			ArrayList<Column> globalColumns = new ArrayList<Column>();
			Iterator globColIt = m_source.selectNodes("/tables/global/col").iterator();
			while (globColIt.hasNext())
				{
				Element cole = (Element)globColIt.next();
				String colName = cole.attribute(NAME).getValue();
				String type = cole.attribute(TYPE).getValue();
				Column col = new Column(colName, m_javaTypeMap.get(type), type, formatter,
						m_dbTypeMap.get(type));
				
				if ((cole.attribute(ALLOW_NULL) != null)  && (cole.attribute(ALLOW_NULL).getValue().equals("false")))
					col.setAllowNull(false);
					
				if (cole.attribute(DEFAULT_VALUE) != null)
					col.setDefault(cole.attribute(DEFAULT_VALUE).getValue());
				
				if ((cole.attribute(UNIQUE) != null) && (cole.attribute(UNIQUE).getValue().equals("true")))
					col.setUnique();
					
				/* if (cole.attribute(UNIQUE_SET) != null)
					col.setUniqueSet(cole.attribute(UNIQUE_SET).getValue()); */
				
				if ((cole.attribute(PRIMARY_KEY) != null) && (cole.attribute(PRIMARY_KEY).getValue().equals("true")))
					col.setPrimaryKey();
					
				if (cole.attribute(AUTO_SET) != null)
					col.setAutoSet(cole.attribute(AUTO_SET).getValue());
					
				//Attribute refTable = cole.attribute(REF_TABLE);
				Element refTable = cole.element(REFERENCE);
				if (refTable != null)
					{
					col.setForeignKey();
					col.setForeignTableName(refTable.attributeValue(TABLE));
					col.setForeignTableColumnName(refTable.attributeValue(COLUMN));
					
					if (refTable.attribute(ON_DELETE) != null)
						col.setOnDelete(refTable.attribute(ON_DELETE).getValue());
						
					if (refTable.attribute(ON_UPDATE) != null)
						col.setOnUpdate(refTable.attribute(ON_UPDATE).getValue());
					}
					
				col.setComment(cole.elementText(COMMENT));
				
				globalColumns.add(col);
				}
			
			Iterator tableit = m_source.selectNodes("/tables/table").iterator();
			while (tableit.hasNext())
				{
				int dirtyFlag = 0;
				Element e = (Element) tableit.next();
				String tableName = e.attribute(NAME).getValue();
				//System.out.println("Table "+tableName);
				Table table = new Table(tableName, formatter);
				table.setComment(e.elementText(COMMENT));
				
				Iterator props = e.elementIterator(PROPERTY);
				while (props.hasNext())
					{
					Element prop = (Element)props.next();
					table.addProperty(prop.attributeValue(KEY), prop.attributeValue(VALUE));
					}
				
				Iterator cols = e.elementIterator(COL);
				while (cols.hasNext())
					{
					Element cole = (Element)cols.next();
					String colName = cole.attribute(NAME).getValue();
					String type = cole.attribute(TYPE).getValue();
					Column col = new Column(colName, m_javaTypeMap.get(type), type, 
							formatter, m_dbTypeMap.get(type));
					col.setDirtyFlag(dirtyFlag);
					dirtyFlag ++;
					
					
					if ((cole.attribute(ALLOW_NULL) != null)  && (cole.attribute(ALLOW_NULL).getValue().equals("false")))
						col.setAllowNull(false);
						
					if (cole.attribute(DEFAULT_VALUE) != null)
						col.setDefault(cole.attribute(DEFAULT_VALUE).getValue());
					
					if ((cole.attribute(UNIQUE) != null)  && (cole.attribute(UNIQUE).getValue().equals("true")))
						col.setUnique();
						
					/* if (cole.attribute(UNIQUE_SET) != null)
						col.setUniqueSet(cole.attribute(UNIQUE_SET).getValue()); */
					
					if ((cole.attribute(PRIMARY_KEY) != null) && (cole.attribute(PRIMARY_KEY).getValue().equals("true")))
						col.setPrimaryKey();
						
					if (cole.attribute(AUTO_SET) != null)
						col.setAutoSet(cole.attribute(AUTO_SET).getValue());
						
					//Attribute refTable = cole.attribute(REF_TABLE);
					Element refTable = cole.element(REFERENCE);
					if (refTable != null)
						{
						col.setForeignKey();
						col.setForeignTableName(refTable.attributeValue(TABLE));
						col.setForeignTableColumnName(refTable.attributeValue(COLUMN));
						
						if (refTable.attribute(ON_DELETE) != null)
							col.setOnDelete(refTable.attribute(ON_DELETE).getValue());
						
						if (refTable.attribute(ON_UPDATE) != null)
							col.setOnUpdate(refTable.attribute(ON_UPDATE).getValue());
						}
						
					col.setComment(cole.elementText(COMMENT));
					
					if ("mts".equals(col.getAutoSet()))
						table.setMTColumn(col);
						
					if ("cts".equals(col.getAutoSet()))
						table.setCTColumn(col);
						
					table.addColumn(col);
					}
					
				//Add global columns to table
				if (!"false".equals(table.getProperties().get(PROP_INHERIT)))
					{
					Iterator<Column> globCols = globalColumns.iterator();
					while (globCols.hasNext())
						{
						Column col = globCols.next().getCopy();
						col.setDirtyFlag(dirtyFlag);
						dirtyFlag ++;
						
						if ("mts".equals(col.getAutoSet()))
							table.setMTColumn(col);
							
						if ("cts".equals(col.getAutoSet()))
							table.setCTColumn(col);
						
						table.addColumn(col);
						}
					}
					
				//Add Queries for foreign keys
				for (ForeignKeySet fkeySet : table.getForeignKeys())
					{
					StringBuilder sqlQuery = new StringBuilder();
					ArrayList<Parameter> params = new ArrayList<Parameter>();
					
					sqlQuery.append("FROM ");
					sqlQuery.append(table.getName());
					sqlQuery.append(" this WHERE ");
					boolean first = true;
					for (Column c : fkeySet.getKeys())
						{
						if (!first)
							sqlQuery.append(" AND ");
						sqlQuery.append("this.").append(createPlugin.getFieldEscapeString());
						sqlQuery.append(c.getName());
						sqlQuery.append(createPlugin.getFieldEscapeString()).append(" = ?");
						first = false;
						
						params.add(new Parameter(c.getName(), c.getType(), formatter));
						}
						
					String queryName = "by_"+fkeySet.getTableName();
						
					Query query = new Query(formatter, queryName, params, sqlQuery.toString());
					query.setEscape(false);
					table.addQuery(query);
					}
					
				//Get Unique definitions
				Iterator uniques = e.elementIterator(UNIQUE);
				while (uniques.hasNext())
					{
					Iterator uniqueRefs = ((Element)uniques.next()).elementIterator(COL_REF);
					Set<Column> uniqueSet = new HashSet<Column>();
					while (uniqueRefs.hasNext())
						{
						uniqueSet.add(table.getColumn(
								((Element)uniqueRefs.next()).attribute(NAME).getValue()));
						}
						
					table.getUniqueColumnSets().add(uniqueSet);
					}
					
				Iterator queries = e.elementIterator(Query.TABLE_QUERY);
				while (queries.hasNext())
					{
					Query q = new Query((Element)queries.next(), formatter, m_javaTypeMap);
					table.addQuery(q);
					}
				
				//This is grandfather in older table setups
				queries = e.elementIterator(Query.QUERY);
				while (queries.hasNext())
					{
					Query q = new Query((Element)queries.next(), formatter, m_javaTypeMap);
					table.addQuery(q);
					}
					
				tableNames.put(tableName, table);
				tables.offer(table);
				}
			
			//PrintWriter dotFile = new PrintWriter(new FileWriter("tables.dot"));
			
			Iterator<Table> it = tables.iterator();
			while (it.hasNext())
				{
				Table t = it.next();

				Map<String, Object> attributes = new HashMap<String, Object>();
				
				String className = formatter.formatClassName(t.getName());
				StringTemplate baseTemplate = ormBaseObjectTG.getInstanceOf("baseClass");
				StringTemplate derivedTemplate = ormObjectTG.getInstanceOf("derivedClass");
				
				ArrayList<Column> columns = t.getColumns();
				Iterator<Column> colit = columns.iterator();
				while (colit.hasNext())
					{
					Column col = colit.next();
						
					if (col.isForeignKey())
						{
						String ftable = col.getForeignTableName();
						
						//Need to check if the table exists at all
						if (!tableNames.containsKey(ftable))
							throw new Exception("Table "+ftable+" does not exist");
						col.setForeignTable(tableNames.get(ftable));
						}
					}
				
				attributes.put("package", m_config.getProperty(PROP_PACKAGE));
				attributes.put("table", t);
				attributes.put("columns", columns);
				attributes.put("primaryKeys", t.getPrimaryKeys());
				attributes.put("foreignKeys", t.getForeignKeys());
				attributes.put("uniqueColumns", t.getUniqueColumnSets());
				attributes.putAll(t.getProperties());
				
				List<GenOrmConstraint> constraints = new ArrayList<GenOrmConstraint>();
								
				//Get the table create statement
				if (createPlugin != null)
					{
					String sql = createPlugin.getCreateSQL(t);
					t.setCreateSQL(sql);
					attributes.put("createSQL", sql.replaceAll("\\n+", "\\\\n").replace("\"", "\\\""));
					
					for (ForeignKeySet keySet : t.getForeignKeys())
						{
						String csql = createPlugin.getConstraintSQL(keySet); 
						constraints.add(new GenOrmConstraint(keySet.getTableName(), 
								keySet.getConstraintName(),
								csql.replaceAll("\\n+", "\\\\n").replace("\"", "\\\"")));
						}
					}
				else
					{
					t.setCreateSQL("");
					attributes.put("createSQL", "");
					}
					
				attributes.put("constraints", constraints);
				attributes.put("fieldEscape", createPlugin.getFieldEscapeString());
				attributes.put("createPlugin", createPlugin.getClass().getName());
					
				baseTemplate.setAttributes(attributes);
				
				m_generatedFileCount ++;
				FileWriter fw = new FileWriter(destDir+"/"+className+"_base.java");
				fw.write(baseTemplate.toString());
				fw.close();
				
				derivedTemplate.setAttributes(attributes);
				
				m_generatedFileCount ++;
				File derivedFile = new File(destDir+"/"+className+".java");
				if (!derivedFile.exists())
					{
					fw = new FileWriter(derivedFile);
					fw.write(derivedTemplate.toString());
					fw.close();
					}
				}
			
			//Sort the tables first
			List<String> createList = new LinkedList<String>();
			
			ArrayList<Table> savedTableList = new ArrayList<Table>(tables);
			//Sort list in order of dependency
			int tableSz = tables.size();
			int loopCounter = 0;
			Table table;
			while ((table = tables.poll()) != null)
				{
				boolean canAdd = true;
				
				loopCounter++;
				Iterator<String> strit = table.getForeignIterator();
				while (strit.hasNext())
					{
					String name = strit.next();
					if ((!name.equals(table.getName())) && (!createList.contains(name)))
						{						
						canAdd = false;
						break;
						}
					}
				
				if (canAdd)
					createList.add(table.getName());
				else
					tables.offer(table); //put it on the end of the queue
					
				//This checks for circular dependencies
				if (loopCounter == tableSz)
					{
					if (tableSz == tables.size())
						{
						System.out.println("Circular dependency hit");
						while ((table = tables.poll()) != null)
							{
							System.out.println(table.getName());
							strit = table.getForeignIterator();
							while (strit.hasNext())
								{
								System.out.println("   "+strit.next());
								System.out.flush();
								}
							}
						throw new Exception("Circular dependency hit");
						}
					else
						{
						tableSz = tables.size();
						loopCounter = 0;
						}
					}
				}
				
			//Write out the create.sql file
			m_generatedFileCount ++;
			FileWriter fw = new FileWriter(destDir+"/create.sql");
			Iterator<String> crIt = createList.iterator();
			while (crIt.hasNext())
				{
				String sql = tableNames.get(crIt.next()).getCreateSQL();
				fw.write(sql);
				fw.write(";\n\n");
				}
			
			fw.close();
				
			//new File(m_destDir+"/genorm").mkdir();
				
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("package", m_config.getProperty(PROP_PACKAGE));
			attributes.put("tables", savedTableList);
			
			writeTemplate("DSEnvelope.java", attributes);
			writeTemplate("GenOrmUnitTest.java", attributes);
			
			writeTemplate("GenOrmDataSource.java", attributes);
			
			
			/* writeTemplate("GenOrmKeyGenerator.java", attributes);
			writeTemplate("GenOrmConnection.java", attributes);
			writeTemplate("GenOrmDataSource.java", attributes);
			writeTemplate("GenOrmField.java", attributes);
			writeTemplate("GenOrmFieldMeta.java", attributes);
			writeTemplate("GenOrmInt.java", attributes);
			writeTemplate("GenOrmRecord.java", attributes);
			writeTemplate("GenOrmString.java", attributes);
			writeTemplate("GenOrmTimestamp.java", attributes);
			writeTemplate("GenOrmRecordFactory.java", attributes);
			writeTemplate("GenOrmDate.java", attributes);
			writeTemplate("GenOrmBoolean.java", attributes);
			writeTemplate("GenOrmBinary.java", attributes);
			writeTemplate("GenOrmBigDecimal.java", attributes);
			writeTemplate("GenOrmDouble.java", attributes);
			writeTemplate("Pair.java", attributes);
			
			writeTemplate("GenOrmException.java", attributes);
			writeTemplate("GenOrmResultSet.java", attributes);
			writeTemplate("GenOrmQueryResultSet.java", attributes);
			writeTemplate("GenOrmQueryRecord.java", attributes);
			 */
			
			if (m_config.getProperty(PROP_GRAPHVIZ_FILE) != null)
				writeTemplate(m_config.getProperty(PROP_GRAPHVIZ_FILE), "templates/tables.dot", attributes);
			}
		catch (Exception e)
			{
			e.printStackTrace();
			return;
			}
			
		System.out.println("Generated "+m_generatedFileCount+" files.");
		}
	}
