package genorm;

public class ConfigurationException extends Exception
	{
	public ConfigurationException(String option, String message)
		{
		super("Configuration problem with option: \""+option+"\" - "+message);
		}
	}
