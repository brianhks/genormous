package org.agileclick.genorm.test.orm;

public class GenOrmUnitTest
	{
	public static void performUnitTests()
		{
		Author.factory.testQueryMethods();
		Book.factory.testQueryMethods();

		}
	}
