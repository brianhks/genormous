package $package$;

public class GenOrmUnitTest
	{
	public static void performUnitTests()
		{
		$tables:{ table | $table.className$.factory.testQueryMethods();
}$
		}
	}
