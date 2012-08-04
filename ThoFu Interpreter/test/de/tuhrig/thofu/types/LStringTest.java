package de.tuhrig.thofu.types;

import junit.framework.Assert;

import org.junit.Test;

public class LStringTest extends LObjectTest {

	@Test
	public void toStringMethod() {
		
		LString string = new LString("aaa");
		
		Assert.assertEquals(new LString("aaa"), string);
		Assert.assertEquals("aaa", string.toString());
	}
	
	@Test
	public void evaluateMethod() {
		
		LString string = new LString("aaa");
		
		Assert.assertEquals(new LString("aaa"), string.run(null, null));
		Assert.assertEquals("aaa", string.run(null, null).toString());
	}
	
	@Test
	public void equalsMethod() {
		
		LString string1 = new LString("aaa");
		LString string2 = new LString("aaa");
		LString string3 = new LString("bbb");
		Object object = new Object();
		
		Assert.assertTrue(string1.equals(string2));
		Assert.assertFalse(string1.equals(string3));
		Assert.assertFalse(string1.equals(object));
	}
}