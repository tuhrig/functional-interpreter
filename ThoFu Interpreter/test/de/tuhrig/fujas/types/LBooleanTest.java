package de.tuhrig.fujas.types;

import junit.framework.Assert;

import org.junit.Test;

import de.tuhrig.fujas.types.LBoolean;

public class LBooleanTest extends LObjectTest {

	@Test
	public void getTrue() {
		
		Assert.assertEquals("true", LBoolean.get(true).toString());
		Assert.assertEquals(true, LBoolean.get(true) == LBoolean.TRUE);
	}
	
	@Test
	public void getFalse() {
		
		Assert.assertEquals("false", LBoolean.get(false).toString());
		Assert.assertEquals(true, LBoolean.get(false) == LBoolean.FALSE);
	}
	
	@Override
	public void toStringMethod() {

		Assert.assertEquals("false", LBoolean.FALSE.toString());
		Assert.assertEquals("true", LBoolean.TRUE.toString());
	}

	@Override
	public void evalMethod() {
		
		Assert.assertEquals("false", LBoolean.FALSE.eval(null, null).toString());
		Assert.assertEquals(true, LBoolean.FALSE.eval(null, null) == LBoolean.FALSE);
		
		Assert.assertEquals("true", LBoolean.TRUE.eval(null, null).toString());
		Assert.assertEquals(true, LBoolean.TRUE.eval(null, null) == LBoolean.TRUE);
	}

	@Override
	public void equalsMethod() {
		
		Assert.assertEquals(true, LBoolean.FALSE.equals(LBoolean.FALSE));
		Assert.assertEquals(false, LBoolean.FALSE.equals(LBoolean.TRUE));
		Assert.assertEquals(false, LBoolean.FALSE.equals(new Object()));
		
		Assert.assertEquals(true, LBoolean.TRUE.equals(LBoolean.TRUE));
		Assert.assertEquals(false, LBoolean.TRUE.equals(LBoolean.FALSE));
		Assert.assertEquals(false, LBoolean.TRUE.equals(new Object()));
	}
}