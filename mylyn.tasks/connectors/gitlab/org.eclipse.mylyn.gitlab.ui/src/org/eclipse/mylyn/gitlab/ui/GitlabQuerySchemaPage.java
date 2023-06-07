package org.eclipse.mylyn.gitlab.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.mylyn.gitlab.core.GitlabTaskSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.QueryPageDetails;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.RepositoryQuerySchemaPage;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultiSelectionAttributeEditor;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;

public class GitlabQuerySchemaPage extends RepositoryQuerySchemaPage {

	public GitlabQuerySchemaPage(String pageName, TaskRepository repository, IRepositoryQuery query,
			AbstractQueryPageSchema schema, TaskData data, QueryPageDetails pageDetails) {
		super(pageName, repository, query, schema, data, pageDetails);
	}

	private void updateTaskDataFromGroup(TaskData taskData, IRepositoryQuery query, String key) {

		TaskAttribute productAttribute = taskData.getRoot().getAttribute(key);
		productAttribute.setValues(Arrays.asList(
				Optional.ofNullable(query.getAttribute(key)).map(st -> st.split(",")).orElse(new String[] { "" })));

	}

	@Override
	protected boolean restoreState(@NonNull IRepositoryQuery query) {
		TaskData taskData = getTargetTaskData();
		for (String entry : editorMap.keySet()) {
			updateTaskDataFromGroup(taskData, query, entry);
		}

		doRefreshControls();
		return true;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setSummary(this.getQueryTitle());
		query.setUrl(getQueryUrl(getTaskRepository().getRepositoryUrl()));
		for (Entry<String, AbstractAttributeEditor> entry : editorMap.entrySet()) {
			if (entry.getValue() instanceof MultiSelectionAttributeEditor)
				query.setAttribute(entry.getKey(),
						String.join(",", ((MultiSelectionAttributeEditor) entry.getValue()).getValues()));
			else
				query.setAttribute(entry.getKey(), entry.getValue().getTaskAttribute().getValue());
		}
	}

	@Override
	protected void doRefreshControls() {
		for (Entry<String, AbstractAttributeEditor> entry : editorMap.entrySet()) {
			entry.getValue().refresh();
		}
	}
}
