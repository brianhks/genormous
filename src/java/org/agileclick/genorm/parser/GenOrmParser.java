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
						<element>comment</element>
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
							<attribute>Value</attribute>
						</property>
					</object>
					<object tag="reference" name="Reference">
						<property name="Table">
							<attribute>table</attribute>
						</property>
						<property name="Column">
							<attribute>column</attribute>
						</property>
					</object>
				</object>
			</object>
			
			<object tag="table" name="Table">
				<property name="Type">
					<element>comment</element>
				</property>
				<object reference="Property"/>
				<object reference="Column"/>
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

		}
		

	//========================================================================
	public class Table
		{
		private boolean _firstCall = true;
		
		private StringBuilder m_Type;

		private List<String> m_TypeList = new ArrayList<String>();

		private List<Property> m_PropertyList = new ArrayList<Property>();
		private Property m_Property;
		private int m_PropertyRef = 0;
		private List<Column> m_ColumnList = new ArrayList<Column>();
		private Column m_Column;
		private int m_ColumnRef = 0;


		public String getType()
			{
			return (m_TypeList.size() == 0 ? null : m_TypeList.get(0));
			}
			
		public List<String> getTypeList()
			{
			return (m_TypeList);
			}


		public List<Property> getPropertyList() { return (m_PropertyList); }
		/**
			Convenience function for getting a single value
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
				
			if (m_Property != null) 
				{
				if (localName.equals("property"))
					m_PropertyRef ++;
					
				m_Property.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Column != null) 
				{
				if (localName.equals("col"))
					m_ColumnRef ++;
					
				m_Column.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (localName.equals("") )
				{
				m_Type = new StringBuilder();
				_characterGrabber = m_Type;
				}

				
			if (localName.equals("property"))
				{
				m_Property = new Property();
				m_PropertyList.add(m_Property);
				m_PropertyRef = 1;
				
				m_Property.startElement(uri, localName, qName, attrs);
				}

			if (localName.equals("col"))
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
			if (m_Type != null)
				{
				m_TypeList.add(m_Type.toString());
				m_Type = null;
				}
				

			if ((localName.equals("property")) && ((--m_PropertyRef) == 0))
				{
				m_Property = null;
				}
					
			if (m_Property != null)
				m_Property.endElement(uri, localName, qName);

			if ((localName.equals("col")) && ((--m_ColumnRef) == 0))
				{
				m_Column = null;
				}
					
			if (m_Column != null)
				m_Column.endElement(uri, localName, qName);


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
				if (localName.equals("option"))
					m_OptionRef ++;
					
				m_Option.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_TypeMap != null) 
				{
				if (localName.equals("type"))
					m_TypeMapRef ++;
					
				m_TypeMap.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Plugin != null) 
				{
				if (localName.equals("plugin"))
					m_PluginRef ++;
					
				m_Plugin.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (localName.equals("option"))
				{
				m_Option = new Option();
				m_OptionList.add(m_Option);
				m_OptionRef = 1;
				
				m_Option.startElement(uri, localName, qName, attrs);
				}

			if (localName.equals("type"))
				{
				m_TypeMap = new TypeMap();
				m_TypeMapList.add(m_TypeMap);
				m_TypeMapRef = 1;
				
				m_TypeMap.startElement(uri, localName, qName, attrs);
				}

			if (localName.equals("plugin"))
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

			if ((localName.equals("option")) && ((--m_OptionRef) == 0))
				{
				m_Option = null;
				}
					
			if (m_Option != null)
				m_Option.endElement(uri, localName, qName);

			if ((localName.equals("type")) && ((--m_TypeMapRef) == 0))
				{
				m_TypeMap = null;
				}
					
			if (m_TypeMap != null)
				m_TypeMap.endElement(uri, localName, qName);

			if ((localName.equals("plugin")) && ((--m_PluginRef) == 0))
				{
				m_Plugin = null;
				}
					
			if (m_Plugin != null)
				m_Plugin.endElement(uri, localName, qName);


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
		public String getAllowNull() { return (m_AllowNull); }
		public String getUnique() { return (m_Unique); }
		public String getAutoSet() { return (m_AutoSet); }


		public List<Property> getPropertyList() { return (m_PropertyList); }
		/**
			Convenience function for getting a single value
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
				m_AllowNull = attrs.getValue("allow_null");
				m_Unique = attrs.getValue("unique");
				m_AutoSet = attrs.getValue("auto_set");

				_firstCall = false;
				return;
				}
				
			if (m_Property != null) 
				{
				if (localName.equals("property"))
					m_PropertyRef ++;
					
				m_Property.startElement(uri, localName, qName, attrs);
				return;
				}
				
			if (m_Reference != null) 
				{
				if (localName.equals("reference"))
					m_ReferenceRef ++;
					
				m_Reference.startElement(uri, localName, qName, attrs);
				return;
				}
				


			if (localName.equals("") )
				{
				m_Comment = new StringBuilder();
				_characterGrabber = m_Comment;
				}

				
			if (localName.equals("property"))
				{
				m_Property = new Property();
				m_PropertyList.add(m_Property);
				m_PropertyRef = 1;
				
				m_Property.startElement(uri, localName, qName, attrs);
				}

			if (localName.equals("reference"))
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
				

			if ((localName.equals("property")) && ((--m_PropertyRef) == 0))
				{
				m_Property = null;
				}
					
			if (m_Property != null)
				m_Property.endElement(uri, localName, qName);

			if ((localName.equals("reference")) && ((--m_ReferenceRef) == 0))
				{
				m_Reference = null;
				}
					
			if (m_Reference != null)
				m_Reference.endElement(uri, localName, qName);


			}
		}

	//========================================================================
	public class Reference
		{
		private boolean _firstCall = true;
		
		private String m_Table;
		private String m_Column;

		public String getTable() { return (m_Table); }
		public String getColumn() { return (m_Column); }



		//------------------------------------------------------------------------
		protected void startElement(String uri, String localName, String qName, Attributes attrs)
			{
			if (_firstCall)
				{
				m_Table = attrs.getValue("table");
				m_Column = attrs.getValue("column");

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
				m_Value = attrs.getValue("Value");

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
	public class Global
		{
		private boolean _firstCall = true;
		
		private List<Column> m_ColumnList = new ArrayList<Column>();
		private Column m_Column;
		private int m_ColumnRef = 0;


		public List<Column> getColumnList() { return (m_ColumnList); }
		/**
			Convenience function for getting a single value
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
				if (localName.equals("col"))
					m_ColumnRef ++;
					
				m_Column.startElement(uri, localName, qName, attrs);
				return;
				}
				


				
			if (localName.equals("col"))
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

			if ((localName.equals("col")) && ((--m_ColumnRef) == 0))
				{
				m_Column = null;
				}
					
			if (m_Column != null)
				m_Column.endElement(uri, localName, qName);


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
		if (localName.equals("configuration"))
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

		if (localName.equals("global"))
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

		if (localName.equals("table"))
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


		}
	
	//------------------------------------------------------------------------
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
		{
		//Stop grabbing characters for any node.
		_characterGrabber = null;
		
		if ((localName.equals("configuration")) && ((--m_ConfigurationRef) == 0))
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
		if ((localName.equals("global")) && ((--m_GlobalRef) == 0))
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
		if ((localName.equals("table")) && ((--m_TableRef) == 0))
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

		} 
	}
	