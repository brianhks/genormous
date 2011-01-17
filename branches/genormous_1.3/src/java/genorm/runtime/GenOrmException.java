package genorm.runtime;


public class GenOrmException extends RuntimeException
	{
	public GenOrmException()
		{
		super();
		}
		
	public GenOrmException(String message)
		{
		super(message);
		}
		
	public GenOrmException(String message, Throwable cause)
		{
		super(message, cause);
		}
		
	public GenOrmException(GenOrmRecord rec, Throwable cause)
		{
		super("Exception in table "+rec.getTableName()+" {"+rec.toString()+"}", cause);
		}
		
	public GenOrmException(Throwable cause)
		{
		super(cause);
		}
	}
