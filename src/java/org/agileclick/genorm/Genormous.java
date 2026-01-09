/* 
Copyright 2012 Brian Hawkins
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.agileclick.genorm;

import java.io.*;

import org.agileclick.genorm.parser.GenOrmParser;
import org.agileclick.genorm.runtime.Formatter;
import org.dom4j.*;
import java.util.*;
import java.util.function.Consumer;

import org.jargp.*;
import org.antlr.stringtemplate.*;
import org.agileclick.genorm.runtime.GenOrmConstraint;

import static java.lang.System.getProperty;
import static java.lang.System.out;

public class Genormous extends GenUtil
	{

	public static final String DEFAULT_VALUE = "default_value";
	public static final String DEFAULT_VALUE_NO_QUOTE = "default_value_no_quote";

	
	private List<ORMPlugin> m_ormPlugins = new ArrayList<ORMPlugin>();
	
	
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
		out.println("Usage: java -jar genormous.jar -s <source xml> -[d <dest dir>] [-p <package>]");
		out.println("      [-s] [-c <custom type properties>] [-b <custom db properties>]");
		out.println("      [-t <database type>] [-g <graphViz file>] [-?]");
		out.println("  -s: Source xml containing table definitions.");
		out.println("  -d: Destination dir to write generated files to.");
		out.println("  -p: Package name of generated files.");
		//out.println("  -s: .");
		out.println("  -c: Custom java type properties file.");
		out.println("  -b: Custom DB type properties file.");
		//out.println("  -t: Database type.");
		out.println("  -g: Name of graphViz dot file to generate.");
		out.println("  -v: Verbose output.");
		}
		
	
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
			Genormous gen = new Genormous(cl.source, cl.verbose); /* , cl.destination, cl.targetPackage,
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
	public Genormous(String source, boolean verbose)
			throws ConfigurationException
		{
		super(source, verbose);
		}
		
		
		
//------------------------------------------------------------------------------
	public void setGraphvizFile(String graphvizFile)
		{
		m_config.setProperty(PROP_GRAPHVIZ_FILE, graphvizFile);
		}

	public static void setValueIfNotNull(String value, Consumer<String> setter)
		{
		if (value != null)
			setter.accept(value);
		}

	private Column parseColumn(GenOrmParser.Column column, Format formatter) throws ConfigurationException
		{
		String type = column.getType();
		Column col = new Column(column.getName(), m_javaTypeMap.get(type), type,
				formatter, m_dbTypeMap.get(type));

		col.setAllowNull(!"false".equals(column.getAllowNull()));

		if (column.getDefaultValue() != null && column.getDefaultValueNoQuote() != null)
			throw new ConfigurationException(column.getName(), "On column definition: "+column.getName()+", do not set "+DEFAULT_VALUE+" and "+DEFAULT_VALUE_NO_QUOTE+" on the same column definition");

		setValueIfNotNull(column.getDefaultValue(), col::setDefault);

		setValueIfNotNull(column.getDefaultValueNoQuote(), s -> {
			col.setDefault(s);
			col.setQuoteDefault(false);
			});

		if ("true".equals(column.getUnique()))
			col.setUnique();

		if ("true".equals(column.getPrimaryKey()))
			col.setPrimaryKey();

		setValueIfNotNull(column.getAutoSet(), col::setAutoSet);

		GenOrmParser.Reference reference = column.getReference();
		if (reference != null)
			{
			col.setForeignKey();
			col.setForeignTableName(reference.getTable());
			col.setForeignTableColumnName(reference.getColumn());
			setValueIfNotNull(reference.getOnDelete(), col::setOnDelete);
			setValueIfNotNull(reference.getOnUpdate(), col::setOnUpdate);
			}

		setValueIfNotNull(column.getComment(), col::setComment);

		return col;
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
				}
				
			if (gp instanceof ORMPlugin)
				{
				m_ormPlugins.add((ORMPlugin)gp);
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
			//Iterator globColIt = m_source.selectNodes("/tables/global/col").iterator();
			if (m_globalColumns != null)
				{
				for (GenOrmParser.Column column : m_globalColumns.getColumnList())
					{
					Column col = parseColumn(column, formatter);

					globalColumns.add(col);
					}
				}


			if (m_verbose)
				System.out.println("Reading table definitions");

			for (GenOrmParser.Table parseTable: m_tables)
				{
				int dirtyFlag = 0;
				if (m_verbose)
					System.out.println("  Table "+parseTable.getName());

				Table table = new Table(parseTable.getName(), formatter);
				setValueIfNotNull(parseTable.getComment(), table::setComment);

				for (GenOrmParser.Property property : parseTable.getPropertyList())
					table.addProperty(property.getKey(), property.getValue());

				for (GenOrmParser.Column parseColumn : parseTable.getColumnList())
					{
					Column col = parseColumn(parseColumn, formatter);

					col.setDirtyFlag(dirtyFlag);
					dirtyFlag ++;

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
				for (GenOrmParser.Unique unique : parseTable.getUniqueList())
					{
					Set<Column> uniqueSet = new HashSet<>();
					for (GenOrmParser.ColumnRef columnRef : unique.getColumnRefList())
						{
						uniqueSet.add(table.getColumn(columnRef.getColumnName()));
						}

					table.getUniqueColumnSets().add(uniqueSet);
					}

				for (GenOrmParser.TableQuery tableQuery : parseTable.getTableQueryList())
					{
					Query q = new Query(tableQuery, formatter, m_javaTypeMap);
					table.addQuery(q);
					}

				tableNames.put(parseTable.getName(), table);
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
				
				//Add plugin code
				StringBuilder pluginCode = new StringBuilder();
				for (ORMPlugin op : m_ormPlugins)
					{
					pluginCode.append(op.getBody(attributes));
					}
					
				attributes.put("plugins", pluginCode.toString());
					
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
			String sqlFolder = getProperty(PROP_SQL_DESTINATION, destDir);
			FileWriter fw = new FileWriter(sqlFolder+"/create.sql");
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
