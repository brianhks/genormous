import genorm.runtime.*;
import org.depunit.annotations.*;
import java.util.*;

import static org.junit.Assert.*;

public class SQLQueryTest extends SQLQuery
	{
	@Test
	public void testReplaceText()
		{
		HashMap<String, String> replace = new HashMap<String, String>();
		String testString = "to be or %not% to be that %%is%% the question";
		
		String repString = QueryHelper.replaceText(testString, replace);
		assertEquals(testString, repString);
		
		replace.put("not", "will");
		repString = QueryHelper.replaceText(testString, replace);
		assertEquals("to be or will to be that %%is%% the question", repString);
		
		replace.put("is", "isn't");
		repString = QueryHelper.replaceText(testString, replace);
		assertEquals("to be or will to be that %isn't% the question", repString);
		}
		
	public String getQueryName() { return (""); }
	public String getQuery() { return (""); }
	}
