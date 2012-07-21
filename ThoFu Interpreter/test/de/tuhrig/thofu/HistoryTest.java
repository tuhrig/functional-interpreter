package de.tuhrig.thofu;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.tuhrig.thofu.gui.History;

public class HistoryTest {

	@Test
	public void storageLimit() {
		
		History history = History.instance;
		
		for(int i = 0; i < History.limit * 2; i++) {
			
			history.add(null, new Date(), new Date());

			Assert.assertTrue(history.size() < History.limit);
		}
	}
}