/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * @author Rob Elves
 */
public interface ITasksCoreConstants {

	public static final int MAX_SUBTASK_DEPTH = 10;

	public static final String ID_PLUGIN = "org.eclipse.mylyn.tasks.core";

	public static final String OLD_TASK_LIST_FILE = "tasklist.xml";

	public static final String FILENAME_ENCODING = "UTF-8";

	public static final String PREFIX_TASKLIST = "tasklist";

	public static final String DEFAULT_BACKUP_FOLDER_NAME = "backup";

	public static final String FILE_EXTENSION = ".xml.zip";

	public static final String DEFAULT_TASK_LIST_FILE = PREFIX_TASKLIST + FILE_EXTENSION;

	public static final String CONTEXTS_DIRECTORY = "contexts";

	public static final TaskListSchedulingRule TASKLIST_SCHEDULING_RULE = new TaskListSchedulingRule();

	public static final ISchedulingRule ROOT_SCHEDULING_RULE = new RootSchedulingRule();

	static class TaskListSchedulingRule extends RootSchedulingRule {

		@Override
		public boolean contains(ISchedulingRule rule) {
			return rule instanceof TaskListSchedulingRule;
		}

		@Override
		public boolean isConflicting(ISchedulingRule rule) {
			return rule instanceof TaskListSchedulingRule;
		}
	}

	static class RootSchedulingRule implements ISchedulingRule {

		public boolean contains(ISchedulingRule rule) {
			return rule instanceof RootSchedulingRule;
		}

		public boolean isConflicting(ISchedulingRule rule) {
			return rule instanceof RootSchedulingRule;
		}
	}

}
