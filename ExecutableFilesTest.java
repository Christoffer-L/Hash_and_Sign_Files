import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

public class ExecutableFilesTest 
{
	private ExecutableFiles ef;
	
	@Before
	public void init()
	{
		ef = new ExecutableFiles();
	}
	
	@Test
	public void test1()
	{
		assertEquals("1", "1");
	}
}
