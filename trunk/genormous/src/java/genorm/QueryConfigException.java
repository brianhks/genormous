package genorm;

public class QueryConfigException extends Exception
	{
	private String m_option;
	private String m_query;
	private String m_message;
	
	public QueryConfigException(String option, String message)
		{
		super();
		m_option = option;
		m_message = message;
		}
		
	public void setQuery(String query) { m_query = query; }
		
	@Override
	public String getMessage()
		{
		return ("Configuration problem with option: \""+m_option+"\" in query \""+
				m_query+"\": "+m_message);
		}
	}
