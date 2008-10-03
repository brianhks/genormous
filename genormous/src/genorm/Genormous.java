package genorm;

import java.io.*;
import org.dom4j.io.SAXReader;
import org.dom4j.*;
import java.util.*;
import org.jargp.*;
import org.antlr.stringtemplate.*;

import static java.lang.System.out;

public class Genormous extends TemplateHelper
	{
	//XML elements and attribute names
	public static final String NAME = "name";
	public static final String COMMENT = "comment";
	public static final String TYPE = "type";
	public static final String PRIMARY_KEY = "primary_key";
	public static final String UNIQUE = "unique";
	public static final String COL = "col";
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
	
	
	private String m_source;
	//private String m_destDir;
	private PropertiesFile m_typeMap;
	private Format m_formatter;
	private String m_packageName;
	private boolean m_includeStringSets;
	private String m_graphVizFile;
	
	
	//===========================================================================
	private static class CommandLine
		{
		public String source;
		public String destination;
		public String targetPackage;
		public boolean includeStringSets;
		public String customTypeProperties;
		public String customDBTypeProperties;
		public String graphVizFile;
		
		public CommandLine()
			{
			source = null;
			destination = null;
			targetPackage = null;
			includeStringSets = false;
			customTypeProperties = null;
			customDBTypeProperties = null;
			graphVizFile = null;
			}
		}
		
	//===========================================================================
	private static final ParameterDef[] PARAMETERS = 
		{
		new StringDef('o', "source"),
		new StringDef('d', "destination"),
		new StringDef('p', "targetPackage"),
		new BoolDef('s', "includeStringSets"),
		new StringDef('c', "customTypeProperties"),
		new StringDef('b', "customDBTypeProperties"),
		new StringDef('g', "graphVizFile")
		};
		
	
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
		
		Genormous gen = new Genormous(cl.source, cl.destination, cl.targetPackage,
				cl.includeStringSets, cl.graphVizFile);
				
		QueryGen qgen = new QueryGen(cl.source, cl.destination, cl.targetPackage);
		
		try
			{
			System.out.println("Generating classes");
			gen.generateClasses();
			System.out.println("Generating queries");
			qgen.generateClasses();
			}
		catch (Exception e)
			{
			System.out.println(e);
			}
		}
		
//------------------------------------------------------------------------------
	public Genormous(String source, String destDir, String packageName, boolean includeStringSets, String graphVizFile)
		{
		super(destDir);
		
		m_source = source;
		m_typeMap = new PropertiesFile("types.properties");
		m_formatter = new DefaultFormat();
		m_packageName = packageName;
		m_includeStringSets = includeStringSets;
		m_graphVizFile = graphVizFile;
		}
		
//------------------------------------------------------------------------------
	public void setTypesFile(String typeFile)
		{
		m_typeMap = new PropertiesFile(typeFile);
		}
		
//------------------------------------------------------------------------------
	public void setFormat(Format formatter)
		{
		m_formatter = formatter;
		}
		
		
//------------------------------------------------------------------------------
	public void generateClasses()
			throws Exception
		{
		//TextReplace baseClass = null, derivedClass = null;
		Queue<Table> tables = new LinkedList<Table>();
		//Table table;
		Map<String, Table> tableNames = new HashMap<String, Table>();
		
		new File(m_destDir).mkdirs();
		
		try
			{
			StringTemplateGroup dataTypeMapGroup = loadTemplateGroup("templates/data_type_maps.st");
			
			//Switch on the type of database
			dataTypeMapGroup.defineMap("typeToSQLTypeMap", new PropertiesFile("templates/hsqldb_types.properties"));
			dataTypeMapGroup.defineTemplate("dbCreate", readResource("templates/hsqldb_create.st"));
			
			StringTemplateGroup ormBaseObjectTG = loadTemplateGroup("templates/ORMObject_base.java");
			ormBaseObjectTG.setSuperGroup(dataTypeMapGroup);
			StringTemplateGroup ormObjectTG = loadTemplateGroup("templates/ORMObject.java");
			ormObjectTG.setSuperGroup(dataTypeMapGroup);
			
			SAXReader reader = new SAXReader();
			reader.setValidation(false);
			reader.setIncludeExternalDTDDeclarations(false);
			/* reader.setProperty(org.apache.xerces.parsers.XML11Configuration.LOAD_EXTERNAL_DTD,
					new ); */
			Document xmldoc = reader.read(new File(m_source));
			
			//Read in global column definitions
			ArrayList<Column> globalColumns = new ArrayList<Column>();
			Iterator globColIt = xmldoc.selectNodes("/tables/global/col").iterator();
			while (globColIt.hasNext())
				{
				Element cole = (Element)globColIt.next();
				String colName = cole.attribute(NAME).getValue();
				String type = cole.attribute(TYPE).getValue();
				Column col = new Column(colName, m_typeMap.getString(type), type, m_formatter);
				
				if ((cole.attribute(ALLOW_NULL) != null)  && (cole.attribute(ALLOW_NULL).getValue().equals("false")))
					col.setAllowNull(false);
					
				if (cole.attribute(DEFAULT_VALUE) != null)
					col.setDefault(cole.attribute(DEFAULT_VALUE).getValue());
				
				if ((cole.attribute(UNIQUE) != null)  && (cole.attribute(UNIQUE).getValue().equals("true")))
					col.setUnique();
				
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
					}
					
				col.setComment(cole.elementText(COMMENT));
				
				globalColumns.add(col);
				}
			
			Iterator tableit = xmldoc.selectNodes("/tables/table").iterator();
			while (tableit.hasNext())
				{
				int dirtyFlag = 1;
				Element e = (Element) tableit.next();
				String tableName = e.attribute(NAME).getValue();
				//System.out.println("Table "+tableName);
				Table table = new Table(tableName, m_formatter);
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
					Column col = new Column(colName, m_typeMap.getString(type), type, m_formatter);
					col.setDirtyFlag(dirtyFlag);
					dirtyFlag <<= 1;
					
					if ((cole.attribute(ALLOW_NULL) != null)  && (cole.attribute(ALLOW_NULL).getValue().equals("false")))
						col.setAllowNull(false);
						
					if (cole.attribute(DEFAULT_VALUE) != null)
						col.setDefault(cole.attribute(DEFAULT_VALUE).getValue());
					
					if ((cole.attribute(UNIQUE) != null)  && (cole.attribute(UNIQUE).getValue().equals("true")))
						col.setUnique();
					
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
						}
						
					col.setComment(cole.elementText(COMMENT));
					
					if ("mts".equals(col.getAutoSet()))
						table.setMTColumn(col);
						
					if ("cts".equals(col.getAutoSet()))
						table.setCTColumn(col);
						
					table.addColumn(col);
					}
					
				//Add global columns to table
				Iterator<Column> globCols = globalColumns.iterator();
				while (globCols.hasNext())
					{
					Column col = globCols.next();
					col.setDirtyFlag(dirtyFlag);
					dirtyFlag <<= 1;
					
					if ("mts".equals(col.getAutoSet()))
						table.setMTColumn(col);
						
					if ("cts".equals(col.getAutoSet()))
						table.setCTColumn(col);
					
					table.addColumn(col);
					}
					
				Iterator queries = e.elementIterator(QUERY);
				while (queries.hasNext())
					{
					Query q = new Query((Element)queries.next(), m_formatter, m_typeMap);
					table.addQuery(q);
					}
					
				tableNames.put(tableName, table);
				tables.offer(table);
				}
			
			PrintWriter dotFile = new PrintWriter(new FileWriter("tables.dot"));
			
			Iterator<Table> it = tables.iterator();
			while (it.hasNext())
				{
				Table t = it.next();

				Map<String, Object> attributes = new HashMap<String, Object>();
				
				String className = m_formatter.formatClassName(t.getName());
				StringTemplate baseTemplate = ormBaseObjectTG.getInstanceOf("baseClass");
				StringTemplate derivedTemplate = ormObjectTG.getInstanceOf("derivedClass");
				
				ArrayList<ForeignKeySet> foreignKeySetList = new ArrayList<ForeignKeySet>();
				Map<String, ForeignKeySet> foreignKeySetMap = new HashMap<String, ForeignKeySet>();
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
				
				attributes.put("package", m_packageName);
				attributes.put("table", t);
				attributes.put("columns", columns);
				attributes.put("primaryKeys", t.getPrimaryKeys());
				attributes.put("foreignKeys", t.getForeignKeys());
				attributes.put("uniqueColumns", t.getUniqueColumnSets());
				attributes.putAll(t.getProperties());
				
				/* replaceMap.put("TableName", t.getName());
				replaceMap.put("ClassName", className);
				replaceMap.put("Comment", t.getComment());
				replaceMap.put("PackageName", m_packageName); */
				
				StringTemplate createTemplate = ormBaseObjectTG.getInstanceOf("dbCreate");
				createTemplate.setAttributes(attributes);
				String sql = createTemplate.toString().trim();
				t.setCreateSQL(sql);
				attributes.put("createSQL", sql.replaceAll("\\n+", "\\\\n"));
				
				baseTemplate.setAttributes(attributes);
				
				m_generatedFileCount ++;
				FileWriter fw = new FileWriter(m_destDir+"/"+className+"_base.java");
				fw.write(baseTemplate.toString());
				fw.close();
				
				derivedTemplate.setAttributes(attributes);
				
				m_generatedFileCount ++;
				File derivedFile = new File(m_destDir+"/"+className+".java");
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
			FileWriter fw = new FileWriter(m_destDir+"/create.sql");
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
			attributes.put("package", m_packageName);
			attributes.put("tables", savedTableList);
			
			writeTemplate("DSEnvelope.java", attributes);
			writeTemplate("GenOrmUnitTest.java", attributes);
			
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
			
			if (m_graphVizFile != null)
				writeTemplate(m_graphVizFile, "templates/tables.dot", attributes);
			}
		catch (Exception e)
			{
			System.out.println("Dude an exception");
			e.printStackTrace();
			return;
			}
			
		System.out.println("Generated "+m_generatedFileCount+" files.");
		}
	}
