package genorm;

public class Parameter
	{
	private String m_name;
	private String m_type;
	private String m_tag;
	private Format m_formatter;
	
	public Parameter(String name, String type, Format formatter)
		{
		this(name, type, null, formatter);
		}
		
	public Parameter(String name, String type, String tag, Format formatter)
		{
		m_formatter = formatter;
		m_name = name;
		m_type = type;
		m_tag = tag;
		}
	
	public String getName() { return (m_name); }
	public String getXmlName() { return (m_formatter.formatParameterName(m_name)); }
	public String getParameterName() { return (m_formatter.formatParameterName(m_name)); }
	public String getMethodName() { return (m_formatter.formatMethodName(m_name)); }
	public String getType() { return (m_type); }
	public String getTag() { return (m_tag); }
	public boolean isBooleanType() { return (m_type.equals("boolean")); }
	}
