/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.ITask.IPriorityValue;
import org.eclipse.mylyn.tasks.core.ITask.PriorityLevel;

/**
 * @author Steffen Pingel
 */
public class PriorityLevelTest extends TestCase {

	private static class Priority implements IPriorityValue {

		private final int value;

		public Priority(int value) {
			this.value = value;
		}

		public int getPriorityValue() {
			return value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + value;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Priority other = (Priority) obj;
			if (value != other.value) {
				return false;
			}
			return true;
		}

	}

	public void testFromValueThreePriorities() {
		Priority p1 = new Priority(1);
		Priority p2 = new Priority(2);
		Priority p3 = new Priority(3);
		Priority[] priorities = new Priority[] { p1, p2, p3 };
		assertEquals("P1", PriorityLevel.fromValue(priorities, new Priority(1)).toString());
		assertEquals("P3", PriorityLevel.fromValue(priorities, new Priority(2)).toString());
		assertEquals("P5", PriorityLevel.fromValue(priorities, new Priority(3)).toString());
		assertEquals("P3", PriorityLevel.fromValue(priorities, new Priority(10)).toString());
		assertEquals("P3", PriorityLevel.fromValue(priorities, null).toString());
	}

	public void testFromValueSinglePriority() {
		Priority p1 = new Priority(10);
		Priority[] priorities = new Priority[] { p1 };
		assertEquals("P1", PriorityLevel.fromValue(priorities, new Priority(10)).toString());
		assertEquals("P3", PriorityLevel.fromValue(priorities, new Priority(11)).toString());
		assertEquals("P3", PriorityLevel.fromValue(priorities, null).toString());
	}

	public void testFromValueSixPriorites() {
		Priority p1 = new Priority(10);
		Priority p2 = new Priority(20);
		Priority p3 = new Priority(30);
		Priority p4 = new Priority(40);
		Priority p5 = new Priority(70);
		Priority p6 = new Priority(100);
		Priority[] priorities = new Priority[] { p1, p2, p3, p4, p5, p6 };
		assertEquals("P1", PriorityLevel.fromValue(priorities, new Priority(10)).toString());
		assertEquals("P1", PriorityLevel.fromValue(priorities, new Priority(20)).toString());
		assertEquals("P2", PriorityLevel.fromValue(priorities, new Priority(30)).toString());
		assertEquals("P2", PriorityLevel.fromValue(priorities, new Priority(40)).toString());
		assertEquals("P4", PriorityLevel.fromValue(priorities, new Priority(70)).toString());
		assertEquals("P5", PriorityLevel.fromValue(priorities, new Priority(100)).toString());
	}

}
