package org.agileclick.genorm.parser;

import java.util.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
	This file is a slickxml generated SAX parser.
	
	The following is the configuration used to create this file
	
	<pre>
	{@code
	<parser name="GenOrmParser">
			<object tag="configuration" name="Configuration">
			
				<object tag="option" name="Option">
					<property name="Name">
						<attribute>name</attribute>
					</property>
					<property name="Value">
						<attribute>value</attribute>
					</property>
				</object>
				
				<object tag="type" name="TypeMap">
					<property name="Custom">
						<attribute>custom</attribute>
					</property>
					<property name="JavaType">
						<attribute>java</attribute>
					</property>
					<property name="DBType">
						<attribute>db</attribute>
					</property>
				</object>
				
				<object tag="plugin" name="Plugin">
					<property name="PluginClass">
						<attribute>class</attribute>
					</property>
				</object>
				
			</object>
			
			<object tag="global" name="Global">
				<object tag="col" name="Column">
					<property name="Comment">
						<element name="comment"/>
					</property>
					<property name="Name">
						<attribute>name</attribute>
					</property>
					<property name="Type">
						<attribute>type</attribute>
					</property>
					<property name="PrimaryKey">
						<attribute>primary_key</attribute>
					</property>
					<property name="DefaultValue">
						<attribute>default_value</attribute>
					</property>
					<property name="DefaultValueNoQuote">
						<attribute>default_value_no_quote</attribute>
					</property>
					<property name="AllowNull">
						<attribute>allow_null</attribute>
					</property>
					<property name="Unique">
						<attribute>unique</attribute>
					</property>
					<property name="AutoSet">
						<attribute>auto_set</attribute>
					</property>
					
					<object tag="property" name="Property">
						<property name="Key">
							<attribute>key</attribute>
						</property>
						<property name="Value">
							<attribute>value</attribute>
						</property>
					</object>
					<object tag="reference" name="Reference">
						<property name="Table">
							<attribute>table</attribute>
						</property>
						<property name="Column">
							<attribute>column</attribute>
						</property>
						<property name="Id">
							<attribute>id</attribute>
						</property>
						<property name="OnDelete">
							<attribute>on_delete</attribute>
						</property>
						<property name="OnUpdate">
							<attribute>on_update</attribute>
						</property>
					</object>
				</object>
			</object>
			
			<object tag="table" name="Table">
				<property name="Name">
					<attribute>name</attribute>
				</property>
				<property name="Comment">
					<element name="comment"/>
				</property>
				<object reference="Property"/>
				<object reference="Column"/>
				<object tag="primary_key" name="PrimaryKey">
					<object tag="colref" name="ColumnRef">
						<property name="ColumnName">
							<attribute>name</attribute>
						</property>
					</object>
				</object>
				<object tag="unique" name="Unique">
					<object reference="ColumnRef"/>
				</object>

				<object tag="table_query" name="TableQuery" implements="org.agileclick.genorm.ParsedQuery">
					<property name="Name">
						<attribute>name</attribute>
					</property>
					<property name="ResultType">
						<attribute>result_type</attribute>
					</property>
					<property name="Comment">
						<element name="comment"/>
					</property>
					<object tag="sql" name="Sql" body="Query">
						<property name="Parse">
							<attribute>parse</attribute>
						</property>
					</object>
					<object tag="input" name="Input">
						<object tag="param" name="Param">
							<property name="Name"><attribute>name</attribute></property>
							<property name="Type"><attribute>type</attribute></property>
							<property name="Test"><attribute>test</attribute></property>
							<property name="Tag"><attribute>tag</attribute></property>
							<property name="Ref"><attribute>ref</attribute></property>
						</object>
					</object>
					<object tag="replace" name="Replace">
						<object reference="Param"/>
					</object>
				</object>
			</object>

			<object tag="query" name="Query" implements="org.agileclick.genorm.ParsedQuery">
				<property name="Name">
					<attribute>name</attribute>
				</property>
				<property name="ResultType">
					<attribute>result_type</attribute>
				</property>
				<property name="Comment">
					<element name="comment"/>
				</property>
				<object reference="Input"/>
				<object reference="Replace"/>
				<object tag="return" name="Return">
					<object reference="Param"/>
				</object>
				<object reference="Sql"/>
			</object>
			
		</parser>
}
	</pre>
*/
public class GenOrmParser extends DefaultHandler
	{
	private SlickHandler _parserHandler;
	private StringBuilder _characterGrabber;
	
	private Configuration m_Configuration;
	private int m_ConfigurationRef = 0;
	private Global m_Global;
	private int m_GlobalRef = 0;
	private Table m_Table;
	private int m_TableRef = 0;
	private Query m_Query;
	private int m_QueryRef = 0;

	
	/**
		Main constructor
		
		@param handler Handler class provided by you to receive the data objects
			created as the XML is parsed.
	*/
	public GenOrmParser(SlickHandler handler)
		{
		_parserHandler = handler;
		}
		
	/**
		This is the interface implemented by you.  As the SAX parser processes the 
		XML data objects are created by the slickXML parser and passed to this 
		interface.
	*/
	public static interface SlickHandler
		{
		public void parsedConfiguration(Configuration entry) throws Exception;
		public void parsedGlobal(Global entry) throws Exception;
		public void parsedTable(Table entry) throws Exception;
		public void parsedQuery(Query entry) throws Exception;

		}
		

	//========================================================================
	public class Replace 
		{
		private boolean _firstCall = true;
		
		private List<Param> m_ParamList = new ArrayList<Param>();
		private Param m_Param;
		private int m_ParamRef = 0;


		public List<Param> getParamList() { return (m_ParamList); }
		/**
			Convenience function for getting a single value
			@return Param
		*/
		public Param getParam() 
			{
			if (m_ParamList.size() == 0)
				return (null);
			else
				return (m_ParamList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_Param != null) 
				{
				if (qName.equals("param"))
					m_ParamRef ++;
					
				m_Param.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("param"))
				{
				m_Param = new Param();
				m_ParamList.add(m_Param);
				m_ParamRef = 1;
				
				m_Param.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_Param != null)
				m_Param.endElement(uri, localName, qName);

			if ((qName.equals("param")) && ((--m_ParamRef) == 0))
				{
				m_Param = null;
				}


			}
		}

	//========================================================================
	public class Table 
		{
		private boolean _firstCall = true;
		
		private String m_Name;
		private StringBuilder m_Comment;

		private List<String> m_CommentList = new ArrayList<String>();

		private List<Property> m_PropertyList = new ArrayList<Property>();
		private Property m_Property;
		private int m_PropertyRef = 0;
		private List<Column> m_ColumnList = new ArrayList<Column>();
		private Column m_Column;
		private int m_ColumnRef = 0;
		private List<PrimaryKey> m_PrimaryKeyList = new ArrayList<PrimaryKey>();
		private PrimaryKey m_PrimaryKey;
		private int m_PrimaryKeyRef = 0;
		private List<Unique> m_UniqueList = new ArrayList<Unique>();
		private Unique m_Unique;
		private int m_UniqueRef = 0;
		private List<TableQuery> m_TableQueryList = new ArrayList<TableQuery>();
		private TableQuery m_TableQuery;
		private int m_TableQueryRef = 0;

		public String getName() { return (m_Name); }

		public String getComment()
			{
			return (m_CommentList.size() == 0 ? null : m_CommentList.get(0));
			}
			
		public List<String> getCommentList()
			{
			return (m_CommentList);
			}


		public List<Property> getPropertyList() { return (m_PropertyList); }
		/**
			Convenience function for getting a single value
			@return Property
		*/
		public Property getProperty() 
			{
			if (m_PropertyList.size() == 0)
				return (null);
			else
				return (m_PropertyList.get(0));
			}
			
		public List<Column> getColumnList() { return (m_ColumnList); }
		/**
			Convenience function for getting a single value
			@return Column
		*/
		public Column getColumn() 
			{
			if (m_ColumnList.size() == 0)
				return (null);
			else
				return (m_ColumnList.get(0));
			}
			
		public List<PrimaryKey> getPrimaryKeyList() { return (m_PrimaryKeyList); }
		/**
			Convenience function for getting a single value
			@return PrimaryKey
		*/
		public PrimaryKey getPrimaryKey() 
			{
			if (m_PrimaryKeyList.size() == 0)
				return (null);
			else
				return (m_PrimaryKeyList.get(0));
			}
			
		public List<Unique> getUniqueList() { return (m_UniqueList); }
		/**
			Convenience function for getting a single value
			@return Unique
		*/
		public Unique getUnique() 
			{
			if (m_UniqueList.size() == 0)
				return (null);
			else
				return (m_UniqueList.get(0));
			}
			
		public List<TableQuery> getTableQueryList() { return (m_TableQueryList); }
		/**
			Convenience function for getting a single value
			@return TableQuery
		*/
		public TableQuery getTableQuery() 
			{
			if (m_TableQueryList.size() == 0)
				return (null);
			else
				return (m_TableQueryList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");

				_firstCall = false;
				return;
				}
				
			if (m_Property != null) 
				{
				if (qName.equals("property"))
					m_PropertyRef ++;
					
				m_Property.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Column != null) 
				{
				if (qName.equals("col"))
					m_ColumnRef ++;
					
				m_Column.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_PrimaryKey != null) 
				{
				if (qName.equals("primary_key"))
					m_PrimaryKeyRef ++;
					
				m_PrimaryKey.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Unique != null) 
				{
				if (qName.equals("unique"))
					m_UniqueRef ++;
					
				m_Unique.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_TableQuery != null) 
				{
				if (qName.equals("table_query"))
					m_TableQueryRef ++;
					
				m_TableQuery.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (qName.equals("comment") )
				{
				m_Comment = new StringBuilder();
				_characterGrabber = m_Comment;
				}

				
			if (qName.equals("property"))
				{
				m_Property = new Property();
				m_PropertyList.add(m_Property);
				m_PropertyRef = 1;
				
				m_Property.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("col"))
				{
				m_Column = new Column();
				m_ColumnList.add(m_Column);
				m_ColumnRef = 1;
				
				m_Column.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("primary_key"))
				{
				m_PrimaryKey = new PrimaryKey();
				m_PrimaryKeyList.add(m_PrimaryKey);
				m_PrimaryKeyRef = 1;
				
				m_PrimaryKey.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("unique"))
				{
				m_Unique = new Unique();
				m_UniqueList.add(m_Unique);
				m_UniqueRef = 1;
				
				m_Unique.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("table_query"))
				{
				m_TableQuery = new TableQuery();
				m_TableQueryList.add(m_TableQuery);
				m_TableQueryRef = 1;
				
				m_TableQuery.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{
			if (m_Comment != null)
				{
				m_CommentList.add(m_Comment.toString());
				m_Comment = null;
				}
				

			if (m_Property != null)
				m_Property.endElement(uri, localName, qName);

			if ((qName.equals("property")) && ((--m_PropertyRef) == 0))
				{
				m_Property = null;
				}

			if (m_Column != null)
				m_Column.endElement(uri, localName, qName);

			if ((qName.equals("col")) && ((--m_ColumnRef) == 0))
				{
				m_Column = null;
				}

			if (m_PrimaryKey != null)
				m_PrimaryKey.endElement(uri, localName, qName);

			if ((qName.equals("primary_key")) && ((--m_PrimaryKeyRef) == 0))
				{
				m_PrimaryKey = null;
				}

			if (m_Unique != null)
				m_Unique.endElement(uri, localName, qName);

			if ((qName.equals("unique")) && ((--m_UniqueRef) == 0))
				{
				m_Unique = null;
				}

			if (m_TableQuery != null)
				m_TableQuery.endElement(uri, localName, qName);

			if ((qName.equals("table_query")) && ((--m_TableQueryRef) == 0))
				{
				m_TableQuery = null;
				}


			}
		}

	//========================================================================
	public class Return 
		{
		private boolean _firstCall = true;
		
		private List<Param> m_ParamList = new ArrayList<Param>();
		private Param m_Param;
		private int m_ParamRef = 0;


		public List<Param> getParamList() { return (m_ParamList); }
		/**
			Convenience function for getting a single value
			@return Param
		*/
		public Param getParam() 
			{
			if (m_ParamList.size() == 0)
				return (null);
			else
				return (m_ParamList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_Param != null) 
				{
				if (qName.equals("param"))
					m_ParamRef ++;
					
				m_Param.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("param"))
				{
				m_Param = new Param();
				m_ParamList.add(m_Param);
				m_ParamRef = 1;
				
				m_Param.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_Param != null)
				m_Param.endElement(uri, localName, qName);

			if ((qName.equals("param")) && ((--m_ParamRef) == 0))
				{
				m_Param = null;
				}


			}
		}

	//========================================================================
	public class Configuration 
		{
		private boolean _firstCall = true;
		
		private List<Option> m_OptionList = new ArrayList<Option>();
		private Option m_Option;
		private int m_OptionRef = 0;
		private List<TypeMap> m_TypeMapList = new ArrayList<TypeMap>();
		private TypeMap m_TypeMap;
		private int m_TypeMapRef = 0;
		private List<Plugin> m_PluginList = new ArrayList<Plugin>();
		private Plugin m_Plugin;
		private int m_PluginRef = 0;


		public List<Option> getOptionList() { return (m_OptionList); }
		/**
			Convenience function for getting a single value
			@return Option
		*/
		public Option getOption() 
			{
			if (m_OptionList.size() == 0)
				return (null);
			else
				return (m_OptionList.get(0));
			}
			
		public List<TypeMap> getTypeMapList() { return (m_TypeMapList); }
		/**
			Convenience function for getting a single value
			@return TypeMap
		*/
		public TypeMap getTypeMap() 
			{
			if (m_TypeMapList.size() == 0)
				return (null);
			else
				return (m_TypeMapList.get(0));
			}
			
		public List<Plugin> getPluginList() { return (m_PluginList); }
		/**
			Convenience function for getting a single value
			@return Plugin
		*/
		public Plugin getPlugin() 
			{
			if (m_PluginList.size() == 0)
				return (null);
			else
				return (m_PluginList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_Option != null) 
				{
				if (qName.equals("option"))
					m_OptionRef ++;
					
				m_Option.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_TypeMap != null) 
				{
				if (qName.equals("type"))
					m_TypeMapRef ++;
					
				m_TypeMap.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Plugin != null) 
				{
				if (qName.equals("plugin"))
					m_PluginRef ++;
					
				m_Plugin.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("option"))
				{
				m_Option = new Option();
				m_OptionList.add(m_Option);
				m_OptionRef = 1;
				
				m_Option.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("type"))
				{
				m_TypeMap = new TypeMap();
				m_TypeMapList.add(m_TypeMap);
				m_TypeMapRef = 1;
				
				m_TypeMap.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("plugin"))
				{
				m_Plugin = new Plugin();
				m_PluginList.add(m_Plugin);
				m_PluginRef = 1;
				
				m_Plugin.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_Option != null)
				m_Option.endElement(uri, localName, qName);

			if ((qName.equals("option")) && ((--m_OptionRef) == 0))
				{
				m_Option = null;
				}

			if (m_TypeMap != null)
				m_TypeMap.endElement(uri, localName, qName);

			if ((qName.equals("type")) && ((--m_TypeMapRef) == 0))
				{
				m_TypeMap = null;
				}

			if (m_Plugin != null)
				m_Plugin.endElement(uri, localName, qName);

			if ((qName.equals("plugin")) && ((--m_PluginRef) == 0))
				{
				m_Plugin = null;
				}


			}
		}

	//========================================================================
	public class Query implements org.agileclick.genorm.ParsedQuery
		{
		private boolean _firstCall = true;
		
		private String m_Name;
		private String m_ResultType;
		private StringBuilder m_Comment;

		private List<String> m_CommentList = new ArrayList<String>();

		private List<Input> m_InputList = new ArrayList<Input>();
		private Input m_Input;
		private int m_InputRef = 0;
		private List<Replace> m_ReplaceList = new ArrayList<Replace>();
		private Replace m_Replace;
		private int m_ReplaceRef = 0;
		private List<Return> m_ReturnList = new ArrayList<Return>();
		private Return m_Return;
		private int m_ReturnRef = 0;
		private List<Sql> m_SqlList = new ArrayList<Sql>();
		private Sql m_Sql;
		private int m_SqlRef = 0;

		public String getName() { return (m_Name); }
		public String getResultType() { return (m_ResultType); }

		public String getComment()
			{
			return (m_CommentList.size() == 0 ? null : m_CommentList.get(0));
			}
			
		public List<String> getCommentList()
			{
			return (m_CommentList);
			}


		public List<Input> getInputList() { return (m_InputList); }
		/**
			Convenience function for getting a single value
			@return Input
		*/
		public Input getInput() 
			{
			if (m_InputList.size() == 0)
				return (null);
			else
				return (m_InputList.get(0));
			}
			
		public List<Replace> getReplaceList() { return (m_ReplaceList); }
		/**
			Convenience function for getting a single value
			@return Replace
		*/
		public Replace getReplace() 
			{
			if (m_ReplaceList.size() == 0)
				return (null);
			else
				return (m_ReplaceList.get(0));
			}
			
		public List<Return> getReturnList() { return (m_ReturnList); }
		/**
			Convenience function for getting a single value
			@return Return
		*/
		public Return getReturn() 
			{
			if (m_ReturnList.size() == 0)
				return (null);
			else
				return (m_ReturnList.get(0));
			}
			
		public List<Sql> getSqlList() { return (m_SqlList); }
		/**
			Convenience function for getting a single value
			@return Sql
		*/
		public Sql getSql() 
			{
			if (m_SqlList.size() == 0)
				return (null);
			else
				return (m_SqlList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");
				m_ResultType = attrs.getValue("result_type");

				_firstCall = false;
				return;
				}
				
			if (m_Input != null) 
				{
				if (qName.equals("input"))
					m_InputRef ++;
					
				m_Input.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Replace != null) 
				{
				if (qName.equals("replace"))
					m_ReplaceRef ++;
					
				m_Replace.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Return != null) 
				{
				if (qName.equals("return"))
					m_ReturnRef ++;
					
				m_Return.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Sql != null) 
				{
				if (qName.equals("sql"))
					m_SqlRef ++;
					
				m_Sql.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (qName.equals("comment") )
				{
				m_Comment = new StringBuilder();
				_characterGrabber = m_Comment;
				}

				
			if (qName.equals("input"))
				{
				m_Input = new Input();
				m_InputList.add(m_Input);
				m_InputRef = 1;
				
				m_Input.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("replace"))
				{
				m_Replace = new Replace();
				m_ReplaceList.add(m_Replace);
				m_ReplaceRef = 1;
				
				m_Replace.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("return"))
				{
				m_Return = new Return();
				m_ReturnList.add(m_Return);
				m_ReturnRef = 1;
				
				m_Return.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("sql"))
				{
				m_Sql = new Sql();
				m_SqlList.add(m_Sql);
				m_SqlRef = 1;
				
				m_Sql.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{
			if (m_Comment != null)
				{
				m_CommentList.add(m_Comment.toString());
				m_Comment = null;
				}
				

			if (m_Input != null)
				m_Input.endElement(uri, localName, qName);

			if ((qName.equals("input")) && ((--m_InputRef) == 0))
				{
				m_Input = null;
				}

			if (m_Replace != null)
				m_Replace.endElement(uri, localName, qName);

			if ((qName.equals("replace")) && ((--m_ReplaceRef) == 0))
				{
				m_Replace = null;
				}

			if (m_Return != null)
				m_Return.endElement(uri, localName, qName);

			if ((qName.equals("return")) && ((--m_ReturnRef) == 0))
				{
				m_Return = null;
				}

			if (m_Sql != null)
				m_Sql.endElement(uri, localName, qName);

			if ((qName.equals("sql")) && ((--m_SqlRef) == 0))
				{
				m_Sql = null;
				}


			}
		}

	//========================================================================
	public class Reference 
		{
		private boolean _firstCall = true;
		
		private String m_Table;
		private String m_Column;
		private String m_Id;
		private String m_OnDelete;
		private String m_OnUpdate;

		public String getTable() { return (m_Table); }
		public String getColumn() { return (m_Column); }
		public String getId() { return (m_Id); }
		public String getOnDelete() { return (m_OnDelete); }
		public String getOnUpdate() { return (m_OnUpdate); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Table = attrs.getValue("table");
				m_Column = attrs.getValue("column");
				m_Id = attrs.getValue("id");
				m_OnDelete = attrs.getValue("on_delete");
				m_OnUpdate = attrs.getValue("on_update");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class PrimaryKey 
		{
		private boolean _firstCall = true;
		
		private List<ColumnRef> m_ColumnRefList = new ArrayList<ColumnRef>();
		private ColumnRef m_ColumnRef;
		private int m_ColumnRefRef = 0;


		public List<ColumnRef> getColumnRefList() { return (m_ColumnRefList); }
		/**
			Convenience function for getting a single value
			@return ColumnRef
		*/
		public ColumnRef getColumnRef() 
			{
			if (m_ColumnRefList.size() == 0)
				return (null);
			else
				return (m_ColumnRefList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_ColumnRef != null) 
				{
				if (qName.equals("colref"))
					m_ColumnRefRef ++;
					
				m_ColumnRef.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("colref"))
				{
				m_ColumnRef = new ColumnRef();
				m_ColumnRefList.add(m_ColumnRef);
				m_ColumnRefRef = 1;
				
				m_ColumnRef.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_ColumnRef != null)
				m_ColumnRef.endElement(uri, localName, qName);

			if ((qName.equals("colref")) && ((--m_ColumnRefRef) == 0))
				{
				m_ColumnRef = null;
				}


			}
		}

	//========================================================================
	public class Global 
		{
		private boolean _firstCall = true;
		
		private List<Column> m_ColumnList = new ArrayList<Column>();
		private Column m_Column;
		private int m_ColumnRef = 0;


		public List<Column> getColumnList() { return (m_ColumnList); }
		/**
			Convenience function for getting a single value
			@return Column
		*/
		public Column getColumn() 
			{
			if (m_ColumnList.size() == 0)
				return (null);
			else
				return (m_ColumnList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_Column != null) 
				{
				if (qName.equals("col"))
					m_ColumnRef ++;
					
				m_Column.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("col"))
				{
				m_Column = new Column();
				m_ColumnList.add(m_Column);
				m_ColumnRef = 1;
				
				m_Column.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_Column != null)
				m_Column.endElement(uri, localName, qName);

			if ((qName.equals("col")) && ((--m_ColumnRef) == 0))
				{
				m_Column = null;
				}


			}
		}

	//========================================================================
	public class Sql 
		{
		private boolean _firstCall = true;
		
		private String m_Parse;
		private StringBuilder m_Query;

		private List<String> m_QueryList = new ArrayList<String>();

		public String getParse() { return (m_Parse); }

		public String getQuery()
			{
			return (m_QueryList.size() == 0 ? null : m_QueryList.get(0));
			}
			
		public List<String> getQueryList()
			{
			return (m_QueryList);
			}



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Parse = attrs.getValue("parse");
				m_Query = new StringBuilder();
				_characterGrabber = m_Query;

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{
			if (m_Query != null)
				{
				m_QueryList.add(m_Query.toString());
				m_Query = null;
				}
				

			}
		}

	//========================================================================
	public class Input 
		{
		private boolean _firstCall = true;
		
		private List<Param> m_ParamList = new ArrayList<Param>();
		private Param m_Param;
		private int m_ParamRef = 0;


		public List<Param> getParamList() { return (m_ParamList); }
		/**
			Convenience function for getting a single value
			@return Param
		*/
		public Param getParam() 
			{
			if (m_ParamList.size() == 0)
				return (null);
			else
				return (m_ParamList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_Param != null) 
				{
				if (qName.equals("param"))
					m_ParamRef ++;
					
				m_Param.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("param"))
				{
				m_Param = new Param();
				m_ParamList.add(m_Param);
				m_ParamRef = 1;
				
				m_Param.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_Param != null)
				m_Param.endElement(uri, localName, qName);

			if ((qName.equals("param")) && ((--m_ParamRef) == 0))
				{
				m_Param = null;
				}


			}
		}

	//========================================================================
	public class Column 
		{
		private boolean _firstCall = true;
		
		private StringBuilder m_Comment;
		private String m_Name;
		private String m_Type;
		private String m_PrimaryKey;
		private String m_DefaultValue;
		private String m_DefaultValueNoQuote;
		private String m_AllowNull;
		private String m_Unique;
		private String m_AutoSet;

		private List<String> m_CommentList = new ArrayList<String>();

		private List<Property> m_PropertyList = new ArrayList<Property>();
		private Property m_Property;
		private int m_PropertyRef = 0;
		private List<Reference> m_ReferenceList = new ArrayList<Reference>();
		private Reference m_Reference;
		private int m_ReferenceRef = 0;


		public String getComment()
			{
			return (m_CommentList.size() == 0 ? null : m_CommentList.get(0));
			}
			
		public List<String> getCommentList()
			{
			return (m_CommentList);
			}
		public String getName() { return (m_Name); }
		public String getType() { return (m_Type); }
		public String getPrimaryKey() { return (m_PrimaryKey); }
		public String getDefaultValue() { return (m_DefaultValue); }
		public String getDefaultValueNoQuote() { return (m_DefaultValueNoQuote); }
		public String getAllowNull() { return (m_AllowNull); }
		public String getUnique() { return (m_Unique); }
		public String getAutoSet() { return (m_AutoSet); }


		public List<Property> getPropertyList() { return (m_PropertyList); }
		/**
			Convenience function for getting a single value
			@return Property
		*/
		public Property getProperty() 
			{
			if (m_PropertyList.size() == 0)
				return (null);
			else
				return (m_PropertyList.get(0));
			}
			
		public List<Reference> getReferenceList() { return (m_ReferenceList); }
		/**
			Convenience function for getting a single value
			@return Reference
		*/
		public Reference getReference() 
			{
			if (m_ReferenceList.size() == 0)
				return (null);
			else
				return (m_ReferenceList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");
				m_Type = attrs.getValue("type");
				m_PrimaryKey = attrs.getValue("primary_key");
				m_DefaultValue = attrs.getValue("default_value");
				m_DefaultValueNoQuote = attrs.getValue("default_value_no_quote");
				m_AllowNull = attrs.getValue("allow_null");
				m_Unique = attrs.getValue("unique");
				m_AutoSet = attrs.getValue("auto_set");

				_firstCall = false;
				return;
				}
				
			if (m_Property != null) 
				{
				if (qName.equals("property"))
					m_PropertyRef ++;
					
				m_Property.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Reference != null) 
				{
				if (qName.equals("reference"))
					m_ReferenceRef ++;
					
				m_Reference.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (qName.equals("comment") )
				{
				m_Comment = new StringBuilder();
				_characterGrabber = m_Comment;
				}

				
			if (qName.equals("property"))
				{
				m_Property = new Property();
				m_PropertyList.add(m_Property);
				m_PropertyRef = 1;
				
				m_Property.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("reference"))
				{
				m_Reference = new Reference();
				m_ReferenceList.add(m_Reference);
				m_ReferenceRef = 1;
				
				m_Reference.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{
			if (m_Comment != null)
				{
				m_CommentList.add(m_Comment.toString());
				m_Comment = null;
				}
				

			if (m_Property != null)
				m_Property.endElement(uri, localName, qName);

			if ((qName.equals("property")) && ((--m_PropertyRef) == 0))
				{
				m_Property = null;
				}

			if (m_Reference != null)
				m_Reference.endElement(uri, localName, qName);

			if ((qName.equals("reference")) && ((--m_ReferenceRef) == 0))
				{
				m_Reference = null;
				}


			}
		}

	//========================================================================
	public class Param 
		{
		private boolean _firstCall = true;
		
		private String m_Name;
		private String m_Type;
		private String m_Test;
		private String m_Tag;
		private String m_Ref;

		public String getName() { return (m_Name); }
		public String getType() { return (m_Type); }
		public String getTest() { return (m_Test); }
		public String getTag() { return (m_Tag); }
		public String getRef() { return (m_Ref); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");
				m_Type = attrs.getValue("type");
				m_Test = attrs.getValue("test");
				m_Tag = attrs.getValue("tag");
				m_Ref = attrs.getValue("ref");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class Option 
		{
		private boolean _firstCall = true;
		
		private String m_Name;
		private String m_Value;

		public String getName() { return (m_Name); }
		public String getValue() { return (m_Value); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");
				m_Value = attrs.getValue("value");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class Unique 
		{
		private boolean _firstCall = true;
		
		private List<ColumnRef> m_ColumnRefList = new ArrayList<ColumnRef>();
		private ColumnRef m_ColumnRef;
		private int m_ColumnRefRef = 0;


		public List<ColumnRef> getColumnRefList() { return (m_ColumnRefList); }
		/**
			Convenience function for getting a single value
			@return ColumnRef
		*/
		public ColumnRef getColumnRef() 
			{
			if (m_ColumnRefList.size() == 0)
				return (null);
			else
				return (m_ColumnRefList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				_firstCall = false;
				return;
				}
				
			if (m_ColumnRef != null) 
				{
				if (qName.equals("colref"))
					m_ColumnRefRef ++;
					
				m_ColumnRef.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (qName.equals("colref"))
				{
				m_ColumnRef = new ColumnRef();
				m_ColumnRefList.add(m_ColumnRef);
				m_ColumnRefRef = 1;
				
				m_ColumnRef.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			if (m_ColumnRef != null)
				m_ColumnRef.endElement(uri, localName, qName);

			if ((qName.equals("colref")) && ((--m_ColumnRefRef) == 0))
				{
				m_ColumnRef = null;
				}


			}
		}

	//========================================================================
	public class TableQuery implements org.agileclick.genorm.ParsedQuery
		{
		private boolean _firstCall = true;
		
		private String m_Name;
		private String m_ResultType;
		private StringBuilder m_Comment;

		private List<String> m_CommentList = new ArrayList<String>();

		private List<Sql> m_SqlList = new ArrayList<Sql>();
		private Sql m_Sql;
		private int m_SqlRef = 0;
		private List<Input> m_InputList = new ArrayList<Input>();
		private Input m_Input;
		private int m_InputRef = 0;
		private List<Replace> m_ReplaceList = new ArrayList<Replace>();
		private Replace m_Replace;
		private int m_ReplaceRef = 0;

		public String getName() { return (m_Name); }
		public String getResultType() { return (m_ResultType); }

		public String getComment()
			{
			return (m_CommentList.size() == 0 ? null : m_CommentList.get(0));
			}
			
		public List<String> getCommentList()
			{
			return (m_CommentList);
			}


		public List<Sql> getSqlList() { return (m_SqlList); }
		/**
			Convenience function for getting a single value
			@return Sql
		*/
		public Sql getSql() 
			{
			if (m_SqlList.size() == 0)
				return (null);
			else
				return (m_SqlList.get(0));
			}
			
		public List<Input> getInputList() { return (m_InputList); }
		/**
			Convenience function for getting a single value
			@return Input
		*/
		public Input getInput() 
			{
			if (m_InputList.size() == 0)
				return (null);
			else
				return (m_InputList.get(0));
			}
			
		public List<Replace> getReplaceList() { return (m_ReplaceList); }
		/**
			Convenience function for getting a single value
			@return Replace
		*/
		public Replace getReplace() 
			{
			if (m_ReplaceList.size() == 0)
				return (null);
			else
				return (m_ReplaceList.get(0));
			}
			


		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Name = attrs.getValue("name");
				m_ResultType = attrs.getValue("result_type");

				_firstCall = false;
				return;
				}
				
			if (m_Sql != null) 
				{
				if (qName.equals("sql"))
					m_SqlRef ++;
					
				m_Sql.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Input != null) 
				{
				if (qName.equals("input"))
					m_InputRef ++;
					
				m_Input.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Replace != null) 
				{
				if (qName.equals("replace"))
					m_ReplaceRef ++;
					
				m_Replace.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (qName.equals("comment") )
				{
				m_Comment = new StringBuilder();
				_characterGrabber = m_Comment;
				}

				
			if (qName.equals("sql"))
				{
				m_Sql = new Sql();
				m_SqlList.add(m_Sql);
				m_SqlRef = 1;
				
				m_Sql.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("input"))
				{
				m_Input = new Input();
				m_InputList.add(m_Input);
				m_InputRef = 1;
				
				m_Input.startElement(uri, localName, qName, attrs);
				}

			if (qName.equals("replace"))
				{
				m_Replace = new Replace();
				m_ReplaceList.add(m_Replace);
				m_ReplaceRef = 1;
				
				m_Replace.startElement(uri, localName, qName, attrs);
				}


			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{
			if (m_Comment != null)
				{
				m_CommentList.add(m_Comment.toString());
				m_Comment = null;
				}
				

			if (m_Sql != null)
				m_Sql.endElement(uri, localName, qName);

			if ((qName.equals("sql")) && ((--m_SqlRef) == 0))
				{
				m_Sql = null;
				}

			if (m_Input != null)
				m_Input.endElement(uri, localName, qName);

			if ((qName.equals("input")) && ((--m_InputRef) == 0))
				{
				m_Input = null;
				}

			if (m_Replace != null)
				m_Replace.endElement(uri, localName, qName);

			if ((qName.equals("replace")) && ((--m_ReplaceRef) == 0))
				{
				m_Replace = null;
				}


			}
		}

	//========================================================================
	public class ColumnRef 
		{
		private boolean _firstCall = true;
		
		private String m_ColumnName;

		public String getColumnName() { return (m_ColumnName); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_ColumnName = attrs.getValue("name");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class TypeMap 
		{
		private boolean _firstCall = true;
		
		private String m_Custom;
		private String m_JavaType;
		private String m_DBType;

		public String getCustom() { return (m_Custom); }
		public String getJavaType() { return (m_JavaType); }
		public String getDBType() { return (m_DBType); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Custom = attrs.getValue("custom");
				m_JavaType = attrs.getValue("java");
				m_DBType = attrs.getValue("db");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class Property 
		{
		private boolean _firstCall = true;
		
		private String m_Key;
		private String m_Value;

		public String getKey() { return (m_Key); }
		public String getValue() { return (m_Value); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Key = attrs.getValue("key");
				m_Value = attrs.getValue("value");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

	//========================================================================
	public class Plugin 
		{
		private boolean _firstCall = true;
		
		private String m_PluginClass;

		public String getPluginClass() { return (m_PluginClass); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_PluginClass = attrs.getValue("class");

				_firstCall = false;
				return;
				}
				

				
			}

		//------------------------------------------------------------------------
		protected void endElement(String uri, String localName, String qName)
				throws SAXException
			{

			}
		}

		
	//========================================================================
	@Override
	public void characters(char[] ch, int start, int length)
		{
		if (_characterGrabber != null)
			_characterGrabber.append(ch, start, length);
		}
		
	//------------------------------------------------------------------------
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attrs)
			throws SAXException
		{
		if (qName.equals("configuration"))
			{
			//This handles recursive nodes
			if (m_Configuration != null)
				m_ConfigurationRef ++;
			else
				{
				m_Configuration = new Configuration();
				m_ConfigurationRef = 1;
				}
			}

		if (m_Configuration != null)
			{
			m_Configuration.startElement(uri, localName, qName, attrs);
			}

		if (qName.equals("global"))
			{
			//This handles recursive nodes
			if (m_Global != null)
				m_GlobalRef ++;
			else
				{
				m_Global = new Global();
				m_GlobalRef = 1;
				}
			}

		if (m_Global != null)
			{
			m_Global.startElement(uri, localName, qName, attrs);
			}

		if (qName.equals("table"))
			{
			//This handles recursive nodes
			if (m_Table != null)
				m_TableRef ++;
			else
				{
				m_Table = new Table();
				m_TableRef = 1;
				}
			}

		if (m_Table != null)
			{
			m_Table.startElement(uri, localName, qName, attrs);
			}

		if (qName.equals("query"))
			{
			//This handles recursive nodes
			if (m_Query != null)
				m_QueryRef ++;
			else
				{
				m_Query = new Query();
				m_QueryRef = 1;
				}
			}

		if (m_Query != null)
			{
			m_Query.startElement(uri, localName, qName, attrs);
			}


		}
	
	//------------------------------------------------------------------------
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
		{
		//Stop grabbing characters for any node.
		_characterGrabber = null;
		
		if ((qName.equals("configuration")) && ((--m_ConfigurationRef) == 0))
			{
			try
				{
				_parserHandler.parsedConfiguration(m_Configuration);
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
				
			m_Configuration = null;
			}
			
		if (m_Configuration != null)
			{
			m_Configuration.endElement(uri, localName, qName);
			}
		if ((qName.equals("global")) && ((--m_GlobalRef) == 0))
			{
			try
				{
				_parserHandler.parsedGlobal(m_Global);
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
				
			m_Global = null;
			}
			
		if (m_Global != null)
			{
			m_Global.endElement(uri, localName, qName);
			}
		if ((qName.equals("table")) && ((--m_TableRef) == 0))
			{
			try
				{
				_parserHandler.parsedTable(m_Table);
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
				
			m_Table = null;
			}
			
		if (m_Table != null)
			{
			m_Table.endElement(uri, localName, qName);
			}
		if ((qName.equals("query")) && ((--m_QueryRef) == 0))
			{
			try
				{
				_parserHandler.parsedQuery(m_Query);
				}
			catch (Exception e)
				{
				throw new SAXException(e);
				}
				
			m_Query = null;
			}
			
		if (m_Query != null)
			{
			m_Query.endElement(uri, localName, qName);
			}

		} 
	}
	