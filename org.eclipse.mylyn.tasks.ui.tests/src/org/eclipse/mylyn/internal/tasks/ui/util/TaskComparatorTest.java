/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.CategorizedPresentation;
import org.eclipse.mylyn.internal.tasks.ui.ScheduledPresentation;
import org.eclipse.mylyn.internal.tasks.ui.util.SortCriterion.SortKey;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("restriction")
public class TaskComparatorTest {

	private TaskComparator taskComparator;

	@Before
	public void setup() {
		taskComparator = new TaskComparator();
	}

	@Test
	public void compareRank() {
		assertCompare(taskWithRank("5"), taskWithRank("7"));
		assertCompare(taskWithRank("5"), taskWithRank("17"));
		assertCompare(taskWithRank("5"), taskWithRank("17.1"));
		assertCompare(taskWithRank("5.3"), taskWithRank("17.1"));
		assertCompare(taskWithRank("#$d"), taskWithRank("#$e"));
		assertCompare(taskWithRank("gjp"), taskWithRank("gkp"));
		assertCompare(taskWithRank("A"), taskWithRank("a"));
		assertCompare(taskWithRank("dsfhgSd"), taskWithRank("dsfhgsd"));
		assertCompare(taskWithRank("dsfhgS"), taskWithRank("dsfhgsd"));
		assertCompare(taskWithRank("dsfhgs"), taskWithRank("dsfhgsd"));

		assertEquals(0, taskComparator.compare(taskWithRank("5"), taskWithRank("5")));
		assertEquals(0, taskComparator.compare(taskWithRank("17.1"), taskWithRank("17.1")));
		assertEquals(0, taskComparator.compare(taskWithRank("dsfhgs"), taskWithRank("dsfhgs")));
		assertEquals(0, taskComparator.compare(taskWithRank("ds#fHgs"), taskWithRank("ds#fHgs")));
	}

	@Test
	public void readLegacyMemento() {
		XMLMemento memento = XMLMemento.createWriteRoot("sorter");
		IMemento child1 = memento.createChild("sort0");
		IMemento child2 = memento.createChild("sort1");
		SortCriterion criterion1 = new SortCriterion(SortKey.PRIORITY, SortCriterion.ASCENDING);
		SortCriterion criterion2 = new SortCriterion(SortKey.TASK_ID, SortCriterion.DESCENDING);
		criterion1.saveState(child1);
		criterion2.saveState(child2);
		taskComparator.restoreState(memento);
		assertCriterionEquals(criterion1, taskComparator.getSortCriterion(0));
		assertCriterionEquals(criterion2, taskComparator.getSortCriterion(1));
	}

	@Test
	public void readPerspectiveMemento() {
		XMLMemento memento = XMLMemento.createWriteRoot("sorter");
		IMemento categorized1 = memento.createChild("sortorg.eclipse.mylyn.tasks.ui.categorized0");
		IMemento categorized2 = memento.createChild("sortorg.eclipse.mylyn.tasks.ui.categorized1");
		IMemento scheduled1 = memento.createChild("sortorg.eclipse.mylyn.tasks.ui.scheduled0");
		IMemento scheduled2 = memento.createChild("sortorg.eclipse.mylyn.tasks.ui.scheduled1");
		SortCriterion criterion1c = new SortCriterion(SortKey.PRIORITY, SortCriterion.ASCENDING);
		SortCriterion criterion2c = new SortCriterion(SortKey.TASK_ID, SortCriterion.DESCENDING);
		SortCriterion criterion1s = new SortCriterion(SortKey.DUE_DATE, SortCriterion.ASCENDING);
		SortCriterion criterion2s = new SortCriterion(SortKey.SCHEDULED_DATE, SortCriterion.DESCENDING);
		criterion1c.saveState(categorized1);
		criterion2c.saveState(categorized2);
		criterion1s.saveState(scheduled1);
		criterion2s.saveState(scheduled2);

		taskComparator.restoreState(memento);

		assertCriterionEquals(criterion1c, taskComparator.getSortCriterion(0));
		assertCriterionEquals(criterion2c, taskComparator.getSortCriterion(1));
		taskComparator.presentationChanged(new ScheduledPresentation());
		assertCriterionEquals(criterion1s, taskComparator.getSortCriterion(0));
		assertCriterionEquals(criterion2s, taskComparator.getSortCriterion(1));
		taskComparator.presentationChanged(new CategorizedPresentation());
		assertCriterionEquals(criterion1c, taskComparator.getSortCriterion(0));
		assertCriterionEquals(criterion2c, taskComparator.getSortCriterion(1));
	}

	private void assertCriterionEquals(SortCriterion expected, SortCriterion actual) {
		assertEquals(expected.getDirection(), actual.getDirection());
		assertEquals(expected.getKey(), actual.getKey());
	}

	private void assertCompare(ITask task1, ITask task2) {
		assertTrue(taskComparator.compare(task1, task2) < 0);
		assertTrue(taskComparator.compare(task2, task1) > 0);
	}

	private ITask taskWithRank(String rank) {
		ITask task = new TaskTask("kind", "http://mock", "1");
		task.setPriority("");
		task.setAttribute(TaskAttribute.RANK, rank);
		return task;
	}
}
