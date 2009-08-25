package com.redcareditor.mate;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BundleTest {

	@Before
	public void setUp() {
		Bundle.loadBundles("input/");
	}
	
	@Test
	public void shouldHaveCreatedCorrectBundles() {
		assertTrue(Bundle.getBundleByName("Apache") != null);
		assertTrue(Bundle.getBundleByName("Ruby") != null);
		assertTrue(Bundle.getBundleByName("HTML") == null);
	}
	
	@Test
	public void shouldHaveCreatedCorrectGrammars() {
		assertEquals(1, Bundle.getBundleByName("Apache").grammars.size());
		assertEquals(1, Bundle.getBundleByName("Ruby").grammars.size());
	}
}
