package de.tuhrig.fujas.types;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import de.tuhrig.fujas.Environment;
import de.tuhrig.fujas.Interpreter;
import de.tuhrig.fujas.Parser;
import de.tuhrig.fujas.types.LList;
import de.tuhrig.fujas.types.LNull;
import de.tuhrig.fujas.types.LNumber;
import de.tuhrig.fujas.types.LObject;
import de.tuhrig.fujas.types.LOperation;
import de.tuhrig.fujas.types.LSymbol;
import de.tuhrig.fujas.types.LTupel;

public class LTupelTest extends LObjectTest {

	private LTupel tupel;

	private LOperation cons;

	private Environment environment;

	@Before
	public void reset() {

		this.environment = new Interpreter().getEnvironment();

		this.cons = (LOperation) environment.get(LSymbol.get("cons"));
	}

	@Test
	public void fullTupel() {

		tupel = new LTupel(new LNumber("1"), new LNumber("2"));

		Assert.assertEquals("'(1 . 2)", tupel.toString());
	}

	@Test
	public void listTupel() {

		tupel = new LTupel(new LNumber("1"), new LTupel(new LNumber("2"), new LTupel(new LNumber("3"), LNull.NULL)));

		Assert.assertEquals("'(1 2 3)", tupel.toString());
	}

	@Test
	public void emptyTupel() {

		tupel = new LTupel();

		Assert.assertEquals("'()", tupel.toString());
	}

	@Test
	public void firstTupel() {

		tupel = new LTupel(new LNumber("1"), LNull.NULL);

		Assert.assertEquals("'(1)", tupel.toString());
	}

	@Test
	public void secondTupel() {

		tupel = new LTupel(LNull.NULL, new LNumber("1"));

		Assert.assertEquals("'(() . 1)", tupel.toString());
	}

	@Test
	public void tupelTupel1() {

		tupel = new LTupel(new LNumber("1"), new LTupel(new LNumber("1"), LNull.NULL));

		Assert.assertEquals("'(1 1)", tupel.toString());
	}

	@Test
	public void tupelTupel2() {

		tupel = new LTupel(new LNumber("1"), new LTupel(new LNumber("2"), new LNumber("3")));

		Assert.assertEquals("'(1 2 . 3)", tupel.toString());
	}

	@Test
	public void tupelTupel3() {

		tupel = new LTupel(new LNumber("1"), new LTupel(LNull.NULL, new LNumber("3")));

		Assert.assertEquals("'(1 () . 3)", tupel.toString());
	}

	@Test
	@Ignore
	public void tupelTupel4() {

		tupel = new LTupel(new LNumber("1"), new LTupel(LNull.NULL, LNull.NULL));

		Assert.assertEquals("'(1 ())", tupel.toString());
	}

	@Test
	public void tupelTupel5() {

		tupel = new LTupel(LNull.NULL, new LTupel(new LNumber("1"), new LNumber("2")));

		Assert.assertEquals("'(() 1 . 2)", tupel.toString());
	}

	@Test
	public void tupelTupel6() {

		tupel = new LTupel(new LTupel(new LNumber("1"), new LNumber("2")), LNull.NULL);

		Assert.assertEquals("'((1 . 2))", tupel.toString());
	}

	@Test
	public void tupelTupel7() {

		tupel = new LTupel(new LTupel(new LNumber("1"), new LNumber("2")), new LNumber("3"));

		Assert.assertEquals("'((1 . 2) . 3)", tupel.toString());
	}

	@Test
	public void cons1() {

		LList tokens = new LList();
		tokens.add(new LNumber("1"));
		tokens.add(new LNumber("2"));

		LTupel tupel = (LTupel) cons.eval(environment, tokens);

		Assert.assertEquals("'(1 . 2)", tupel.toString());
		Assert.assertEquals(new LNumber("1"), tupel.getFirst());
		Assert.assertEquals(new LNumber("2"), tupel.getRest());
	}

	@Test
	public void cons2() {

		LList tokens = new LList();
		tokens.add(new LNumber("1"));
		tokens.add(LNull.NULL);

		LTupel tupel = (LTupel) cons.eval(environment, tokens);

		Assert.assertEquals("'(1)", tupel.toString());
		Assert.assertEquals(new LNumber("1"), tupel.getFirst());
		Assert.assertEquals(LNull.NULL, tupel.getRest());
	}

	@Test
	public void cons3() {

		LList tokens = new LList();
		tokens.add(LNull.NULL);
		tokens.add(LNull.NULL);

		LTupel tupel = (LTupel) cons.eval(environment, tokens);

		Assert.assertEquals(LNull.NULL, tupel.getFirst());
		Assert.assertEquals(LNull.NULL, tupel.getRest());
	}

	@Test
	public void cons4() {

		LList tokens = new LList();
		tokens.add(new LNumber("1"));
		tokens.add(new LTupel(new LNumber("2"), new LNumber("3")));

		LTupel tupel = (LTupel) cons.eval(environment, tokens);

		Assert.assertEquals("'(1 2 . 3)", tupel.toString());

		Assert.assertEquals(new LNumber("1"), tupel.getFirst());
		Assert.assertEquals(LTupel.class, tupel.getRest().getClass());
		Assert.assertEquals(new LNumber("2"), ((LTupel) tupel.getRest()).getFirst());
		Assert.assertEquals(new LNumber("3"), ((LTupel) tupel.getRest()).getRest());
	}

	@Test
	public void cons() {

		LObject o;

		o = cons.eval(environment, new Parser().parse("(1 2)"));
		Assert.assertEquals("'(1 . 2)", o.toString());
		Assert.assertTrue(o instanceof LTupel);
		Assert.assertTrue(((LTupel) o).getFirst() instanceof LNumber);
		Assert.assertTrue(((LTupel) o).getRest() instanceof LNumber);

		o = cons.eval(environment, new Parser().parse("(1 null)"));
		Assert.assertEquals("'(1)", o.toString());
		Assert.assertTrue(o instanceof LTupel);
		Assert.assertTrue(((LTupel) o).getFirst() instanceof LNumber);
		Assert.assertTrue(((LTupel) o).getRest() instanceof LNull);

		o = cons.eval(environment, new Parser().parse("(null 1)"));
		Assert.assertEquals("'(() . 1)", o.toString());
		Assert.assertTrue(o instanceof LTupel);
		Assert.assertTrue(((LTupel) o).getFirst() instanceof LNull);
		Assert.assertTrue(((LTupel) o).getRest() instanceof LNumber);

		o = cons.eval(environment, new Parser().parse("((cons 1 2) 3)"));
		Assert.assertEquals("'((1 . 2) . 3)", o.toString());
		Assert.assertTrue(o instanceof LTupel);
		Assert.assertTrue(((LTupel) o).getFirst() instanceof LTupel);
		Assert.assertTrue(((LTupel) o).getRest() instanceof LNumber);
	}

	@Override
	public void toStringMethod() {

		// this is tested in the methods above
		Assert.assertTrue(true);
	}

	@Override
	public void evalMethod() {

		tupel = new LTupel(new LNumber("1"), new LNumber("2"));

		LObject o = tupel.eval(null, null);

		Assert.assertTrue(o instanceof LTupel);
	}

	@Override
	public void equalsMethod() {

		LTupel tupel1 = new LTupel(new LNumber("1"), new LNumber("2"));
		LTupel tupel2 = new LTupel(new LNumber("1"), new LNumber("2"));
		LTupel tupel3 = new LTupel(new LNumber("3"), new LNumber("2"));

		Assert.assertTrue(tupel1.equals(tupel2));
		Assert.assertFalse(tupel1.equals(tupel3));
		Assert.assertFalse(tupel1.equals(new Object()));
	}
}