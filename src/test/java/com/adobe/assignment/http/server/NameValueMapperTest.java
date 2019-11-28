package com.adobe.assignment.http.server;

import junit.framework.TestCase;
import org.junit.Test;
import com.adobe.assignment.http.NameValueMapper;

/**
 * Test class for testing the NameValueMapper class implementation.
 * 
 * @author Alfusainey Jallow, University of the Gambia 
 *
 */
public class NameValueMapperTest extends TestCase {

	@Override
	public void setUp() throws Exception {
		super.setUp();
	}
	
	@Test
	public void testNameValueMapping() {		
		NameValueMapper headers = NameValueMapper.createNameValueMap();
		headers.put("Content Type", "text/plain");
		headers.put("Content Length", "10");
		headers.put("Connection: Close", ":");
		headers.putPair("Host: localhost", ":");
		assertEquals(4, headers.size());
		assertEquals(4, headers.size());		
	}
	
	@Override
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	
}
