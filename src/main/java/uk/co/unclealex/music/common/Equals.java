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

import com.google.common.base.Predicate;

/**
 * A small convenience class that allows equality testing either by a
 * {@link Predicate} or, for {@link Comparable} classes, testing by checking
 * that {@link Comparable#compareTo(Object)} is zero.
 * 
 * @author alex
 * 
 */
public class Equals {

	private Equals() {
		// This class cannot be instantiated.
	}

	/**
	 * Test that two objects are equal using a predicate.
	 * 
	 * @param clazz
	 *          The required class of the other object.
	 * @param equalsPredicate
	 *          A {@link Predicate} to use for checking equality.
	 * @param other
	 *          The other object being checked for equality.
	 * @return True if other is an instance of the required class and applying it
	 *         to the equals predicate returns true, false otherwise.
	 */
	@SuppressWarnings("unchecked")
	public static <C> boolean isEqual(Class<? extends C> clazz, Predicate<C> equalsPredicate, Object other) {
		if (other == null) {
			return false;
		}
		else if (clazz.isAssignableFrom(other.getClass())) {
			return equalsPredicate.apply((C) other);
		}
		else {
			return false;
		}
	}

	/**
	 * Test that two {@link Comparable} objects are equal.
	 * 
	 * @param clazz
	 *          The required class of the other object.
	 * @param me
	 *          The first object being checked for equality.
	 * @param other
	 *          The other object being checked for equality.
	 * @return True if other is an instance of the required class and comparing me
	 *         and other returns 0, false otherwise.
	 */
	public static <C extends Comparable<C>> boolean isEqual(Class<? extends C> clazz, final C me, Object other) {
		if (me == other) {
			return true;
		}
		else {
			Predicate<C> equalsPredicate = new Predicate<C>() {
				public boolean apply(C other) {
					return me.compareTo(other) == 0;
				}
			};
			return isEqual(clazz, equalsPredicate, other);
		}
	}
}
