package org.eclipse.mylyn.gitlab.ui;

import org.eclipse.mylyn.gitlab.core.GitlabTaskSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema.Field;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskSchema.Flag;

public class GitlabSearchQueryPageSchema extends AbstractQueryPageSchema {

	private static final GitlabSearchQueryPageSchema instance = new GitlabSearchQueryPageSchema();
	private final GitlabTaskSchema parent = GitlabTaskSchema.getDefault();

	public static GitlabSearchQueryPageSchema getInstance() {
		return instance;
	}

	public GitlabSearchQueryPageSchema() {
	}

	public final Field product = copyFrom(parent.PRODUCT).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();
	public final Field group = copyFrom(parent.GROUP).type(TaskAttribute.TYPE_MULTI_SELECT)
			.layoutPriority(11)
			.create();
	public final Field state = createField("STATE", "State", TaskAttribute.TYPE_SINGLE_SELECT,
			null, 1);
}
