package org.eclipse.mylyn.internal.bugzilla.rest.core;

import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

public class SingleTaskDataCollector extends TaskDataCollector {
	final TaskData[] retrievedData = new TaskData[1];

	@Override
	public void accept(TaskData taskData) {
		retrievedData[0] = taskData;
	}

	public TaskData getTaskData() {
		return retrievedData[0];
	}

}