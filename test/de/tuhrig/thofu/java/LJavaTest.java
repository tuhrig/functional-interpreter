package de.tuhrig.thofu.java;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class LJavaTest {

	@Test
	public void createInterface() {
		
		Object object = LJava.createInterface(List.class, null, null);
		
		Assert.assertEquals("<Proxy: java.util.List>", object.toString());
		Assert.assertEquals(true, ((LJava) object).getJObject() instanceof List);
	}
}