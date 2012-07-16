package de.tuhrig.fujas.java;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import de.tuhrig.fujas.java.LJava;

public class LJavaTest {

	@Test
	public void createInterface() {
		
		Object object = LJava.createInterface(List.class, null, null);
		
		Assert.assertEquals("<Proxy: java.util.List>", object.toString());
		Assert.assertEquals(true, ((LJava) object).getJObject() instanceof List);
	}
}