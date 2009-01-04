/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.MutexSchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants.ObjectSchedulingRule;

/**
 * @author Steffen Pingel
 */
public class ITasksCoreConstantsTest extends TestCase {

	public void testRootRuleConflicts() {
		assertTrue(ITasksCoreConstants.ROOT_SCHEDULING_RULE.isConflicting(ITasksCoreConstants.ROOT_SCHEDULING_RULE));
		assertTrue(ITasksCoreConstants.ROOT_SCHEDULING_RULE.isConflicting(ITasksCoreConstants.TASKLIST_SCHEDULING_RULE));
		assertTrue(ITasksCoreConstants.ROOT_SCHEDULING_RULE.isConflicting(new MutexSchedulingRule()));
		assertTrue(ITasksCoreConstants.ROOT_SCHEDULING_RULE.isConflicting(new ObjectSchedulingRule(this)));
		assertTrue(ITasksCoreConstants.TASKLIST_SCHEDULING_RULE.isConflicting(ITasksCoreConstants.ROOT_SCHEDULING_RULE));
		assertTrue(new MutexSchedulingRule().isConflicting(ITasksCoreConstants.ROOT_SCHEDULING_RULE));
		assertTrue(new ObjectSchedulingRule(this).isConflicting(ITasksCoreConstants.ROOT_SCHEDULING_RULE));
	}

	public void testObjectSchdedulingRuleConflicts() {
		assertTrue(new ObjectSchedulingRule(this).isConflicting(new ObjectSchedulingRule(this)));
		assertFalse(new ObjectSchedulingRule(new Object()).isConflicting(new ObjectSchedulingRule(this)));
		ObjectSchedulingRule rule = new ObjectSchedulingRule(this);
		assertTrue(rule.isConflicting(rule));
	}
}
