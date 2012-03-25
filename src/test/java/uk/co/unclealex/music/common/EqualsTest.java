/**
 * Copyright 2011 Alex Jones
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 *
 * @author unclealex72
 *
 */

package uk.co.unclealex.music.common;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Predicate;

/**
 * @author alex
 *
 */
public class EqualsTest {

	@Test
	public void testStillEqualUsingSupertype() {
		Predicate<Object> equalsPredicate = new Predicate<Object>() {
			@Override
			public boolean apply(Object obj) {
				return "123".equals(obj);
			}
		};
		boolean actualResult = Equals.isEqual(Object.class, equalsPredicate, "1234".substring(0, 3));
		Assert.assertTrue("Two equals strings should still be equal if tested against object.", actualResult);
	}
	
	@Test
	public void testNullNotEqual() {
		testEquals(1, null, false);
	}

	@Test
	public void testWrongClassNotEqual() {
		testEquals(1, "1", false);
	}

	@Test
	public void testEqual() {
		testEquals(1, 1, true);
	}

	@Test
	public void testNotEqual() {
		testEquals(1, 2, false);
	}

	@Test
	public void testCompareNullNotEqual() {
		testCompareEquals(Integer.class, 1, null, false);
	}

	@Test
	public void testCompareWrongClassNotEqual() {
		testCompareEquals(Integer.class, 1, "1", false);
	}


	@Test
	public void testCompareLessThanNotEqual() {
		testCompareEquals(Integer.class, 1, 2, false);
	}

	@Test
	public void testCompareGreaterThanNotEqual() {
		testCompareEquals(Integer.class, 1, 2, false);
	}

	@Test
	public void testCompareSameObjectEquals() {
		Integer one = 1;
		testCompareEquals(Integer.class, one, one, true);
	}

	@Test
	public void testCompareEquals() {
		String oneTwoThree = "123123";
		testCompareEquals(String.class, oneTwoThree.substring(0, 3), oneTwoThree.substring(3, 6), true);
	}

	protected void testEquals(final Integer me, Object you, boolean expectEquals) {
		Predicate<Integer> equalsPredicate = new Predicate<Integer>() {
			@Override
			public boolean apply(Integer you) {
				return me.intValue() == you.intValue();
			}
		};
		boolean actualEquals = Equals.isEqual(Integer.class, equalsPredicate, you);
		if (expectEquals) {
			Assert.assertTrue("Objects " + me + " and " + you + " were not equal when they should be.", actualEquals);
		}
		else {
			Assert.assertFalse("Objects " + me + " and " + you + " were equal when they should not be.", actualEquals);
		}
	}

	protected <C extends Comparable<C>> void testCompareEquals(Class<C> clazz, final C me, Object you, boolean expectEquals) {
		boolean actualEquals = Equals.isEqual(clazz, me, you);
		if (expectEquals) {
			Assert.assertTrue("Objects " + me + " and " + you + " were not equal when they should be.", actualEquals);
		}
		else {
			Assert.assertFalse("Objects " + me + " and " + you + " were equal when they should not be.", actualEquals);
		}
	}
}
