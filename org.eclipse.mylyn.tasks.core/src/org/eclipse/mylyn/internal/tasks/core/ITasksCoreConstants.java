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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Rob Elves
 */
public interface ITasksCoreConstants {

	public static final int MAX_SUBTASK_DEPTH = 10;

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.core"; //$NON-NLS-1$

	public static final String OLD_TASK_LIST_FILE = "tasklist.xml"; //$NON-NLS-1$

	public static final String FILENAME_ENCODING = "UTF-8"; //$NON-NLS-1$

	public static final String OLD_PREFIX_TASKLIST = "tasklist"; //$NON-NLS-1$

	public static final String PREFIX_TASKS = "tasks"; //$NON-NLS-1$

	public static final String DEFAULT_BACKUP_FOLDER_NAME = "backup"; //$NON-NLS-1$

	public static final String EXPORT_FILE_NAME = "mylyn-tasks"; //$NON-NLS-1$

	public static final String FILE_EXTENSION = ".xml.zip"; //$NON-NLS-1$

	public static final String OLD_FILENAME_TIMESTAMP_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$

	public static final String FILENAME_TIMESTAMP_FORMAT = "yyyy-MM-dd-HHmmss"; //$NON-NLS-1$

	public static final String OLD_M_2_TASKLIST_FILENAME = OLD_PREFIX_TASKLIST + FILE_EXTENSION;

	public static final String DEFAULT_TASK_LIST_FILE = PREFIX_TASKS + FILE_EXTENSION;

	public static final String CONTEXTS_DIRECTORY = "contexts"; //$NON-NLS-1$

	public static final ISchedulingRule ACTIVITY_SCHEDULING_RULE = new MutexSchedulingRule();

	public static final ISchedulingRule TASKLIST_SCHEDULING_RULE = new MutexSchedulingRule();

	public static final ISchedulingRule ROOT_SCHEDULING_RULE = new RootSchedulingRule();

	public static final String ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL = "outgoingNewRepositoryUrl"; //$NON-NLS-1$

	public static final String ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND = "outgoingNewConnectorKind"; //$NON-NLS-1$

	/**
	 * Jobs that have the same instances of this rule set are mutually exclusive.
	 */
	public static class MutexSchedulingRule extends RootSchedulingRule {

		public MutexSchedulingRule() {
		}

		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return rule == ROOT_SCHEDULING_RULE || rule == this;
		}
	}

	/**
	 * The parent of all scheduling rules that modify task data.
	 * 
	 * @see ITasksCoreConstants#ROOT_SCHEDULING_RULE
	 */
	public static class RootSchedulingRule implements ISchedulingRule {

		protected RootSchedulingRule() {
		}

		public boolean contains(ISchedulingRule rule) {
			return rule instanceof RootSchedulingRule;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule instanceof RootSchedulingRule;
		}
	}

	/**
	 * Jobs that have an instances of this rule set which references the same object are mutually exclusive.
	 */
	public static class ObjectSchedulingRule extends RootSchedulingRule {

		private final Object object;

		public ObjectSchedulingRule(Object object) {
			Assert.isNotNull(object);
			this.object = object;
		}

		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule == this;
		}

		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			if (rule == ROOT_SCHEDULING_RULE) {
				return true;
			}
			if (rule instanceof ObjectSchedulingRule) {
				return object.equals(((ObjectSchedulingRule) rule).object);
			}
			return false;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + " [" + object + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public static final String COMMAND_LINE_NO_ACTIVATE_TASK = "-no-activate-task"; //$NON-NLS-1$

	public static final String PROPERTY_LINK_PROVIDER_TIMEOUT = "org.eclipse.mylyn.linkProviderTimeout"; //$NON-NLS-1$

}
