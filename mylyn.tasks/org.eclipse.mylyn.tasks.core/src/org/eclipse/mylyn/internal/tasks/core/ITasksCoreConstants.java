/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Rob Elves
 */
public interface ITasksCoreConstants {

	int MAX_SUBTASK_DEPTH = 10;

	String ID_PLUGIN = "org.eclipse.mylyn.tasks.core"; //$NON-NLS-1$

	String OLD_TASK_LIST_FILE = "tasklist.xml"; //$NON-NLS-1$

	String FILENAME_ENCODING = "UTF-8"; //$NON-NLS-1$

	String OLD_PREFIX_TASKLIST = "tasklist"; //$NON-NLS-1$

	String PREFIX_TASKS = "tasks"; //$NON-NLS-1$

	String DEFAULT_BACKUP_FOLDER_NAME = "backup"; //$NON-NLS-1$

	String EXPORT_FILE_NAME = "mylyn-tasks"; //$NON-NLS-1$

	String FILE_EXTENSION = ".xml.zip"; //$NON-NLS-1$

	String OLD_FILENAME_TIMESTAMP_FORMAT = "yyyy-MM-dd"; //$NON-NLS-1$

	String FILENAME_TIMESTAMP_FORMAT = "yyyy-MM-dd-HHmmss"; //$NON-NLS-1$

	String OLD_M_2_TASKLIST_FILENAME = OLD_PREFIX_TASKLIST + FILE_EXTENSION;

	String DEFAULT_TASK_LIST_FILE = PREFIX_TASKS + FILE_EXTENSION;

	String CONTEXTS_DIRECTORY = "contexts"; //$NON-NLS-1$

	ISchedulingRule ACTIVITY_SCHEDULING_RULE = new MutexSchedulingRule();

	ISchedulingRule ACTIVE_CONTEXT_SCHEDULING_RULE = new MutexSchedulingRule();

	ISchedulingRule TASKLIST_SCHEDULING_RULE = new MutexSchedulingRule();

	ISchedulingRule ROOT_SCHEDULING_RULE = new RootSchedulingRule();

	String ATTRIBUTE_OUTGOING_NEW_REPOSITORY_URL = "outgoingNewRepositoryUrl"; //$NON-NLS-1$

	String ATTRIBUTE_OUTGOING_NEW_CONNECTOR_KIND = "outgoingNewConnectorKind"; //$NON-NLS-1$

	String ATTRIBUTE_HIDDEN = "task.common.hidden"; //$NON-NLS-1$

	String ATTRIBUTE_TASK_EXTENDED_TOOLTIP = "task.common.extended.tooltip"; //$NON-NLS-1$

	String ATTRIBUTE_TASK_SUPPRESS_INCOMING = "task.common.suppress.incoming"; //$NON-NLS-1$

	String ATTRIBUTE_ARTIFACT = "org.eclipse.mylyn.is.artifact"; //$NON-NLS-1$

	String ATTRIBUTE_PRIORITY_LABEL = "task.common.priority.label"; //$NON-NLS-1$

	/**
	 * @deprecated Since Mylyn 3.11, all TaskRepositories store their credentials in the secure store.
	 */
	@Deprecated
	String PROPERTY_USE_SECURE_STORAGE = "org.eclipse.mylyn.tasklist.repositories.configuration.securestorage"; //$NON-NLS-1$

	String PROPERTY_BRAND_ID = "org.eclipse.mylyn.brand.id"; //$NON-NLS-1$

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

		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule instanceof RootSchedulingRule;
		}

		@Override
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

	String COMMAND_LINE_NO_ACTIVATE_TASK = "-no-activate-task"; //$NON-NLS-1$

	String PROPERTY_LINK_PROVIDER_TIMEOUT = "org.eclipse.mylyn.linkProviderTimeout"; //$NON-NLS-1$

	/**
	 * Boolean value that indicates that queries participate in automatic synchronizations.
	 */
	String ATTRIBUTE_AUTO_UPDATE = "org.eclipse.mylyn.tasks.core.synchronization.auto"; //$NON-NLS-1$

	Object JOB_FAMILY_SYNCHRONIZATION = new Object();

	/**
	 * A property to mark an {@link ITask} as having been newly created and not yet saved or submitted.
	 */
	String PROPERTY_NEW_UNSAVED_TASK = "org.eclipse.mylyn.tasks.ui.new.unsaved.task"; //$NON-NLS-1$

}
