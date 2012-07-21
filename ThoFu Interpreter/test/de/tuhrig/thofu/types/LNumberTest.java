package de.tuhrig.thofu.types;

import java.math.BigDecimal;

import de.tuhrig.thofu.types.LNumber;
import de.tuhrig.thofu.types.LObject;

import junit.framework.Assert;

public class LNumberTest extends LObjectTest {

	@Override
	public void toStringMethod() {

		LNumber number = new LNumber("1");
		Assert.assertEquals("1", number.toString());
		
		number = new LNumber("3");
		Assert.assertEquals("3", number.toString());
		
		number = new LNumber(new BigDecimal(66));
		Assert.assertEquals("66", number.toString());
	}

	@Override
	public void evalMethod() {

		LNumber number = new LNumber("1");
		
		LObject o = number.eval(null, null);
		
		Assert.assertEquals("1", o.toString());
		Assert.assertEquals(number, o);
	}

	@Override
	public void equalsMethod() {
		
		LNumber number1 = new LNumber("1");
		LNumber number2 = new LNumber("1");
		LNumber number3 = new LNumber("2");
		
		Assert.assertTrue(number1.equals(number2));
		Assert.assertFalse(number1.equals(number3));
		Assert.assertFalse(number1.equals(new Object()));
	}
}