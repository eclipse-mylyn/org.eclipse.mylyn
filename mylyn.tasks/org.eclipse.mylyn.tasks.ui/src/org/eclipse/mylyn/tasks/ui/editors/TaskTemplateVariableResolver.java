/*******************************************************************************
 * Copyright (c) 2010 Andreas Hoehmann and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Andreas Hoehmann - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui.editors;

import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * Resolver to resolve variables from the active task.
 * <p>
 * This resolver can handle the following variables:
 * <ul>
 * <li><code>{@link #TYPE_ACTIVE_TASK_PREFIX}<code> - the prefix for the active task (if any) or null</li>
 * <li><code>{@link #TYPE_ACTIVE_TASK_KEY}<code> - the prefix for the active task (if any) or null</li>
 * </ul>
 * </p>
 * <h4>Adding Additional Variables</h4>
 * <p>
 * First add another template-resolver (for detail information see <a href=
 * "http://help.eclipse.org/help32/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/extension-points/org_eclipse_ui_editors_templates.html"
 * >here</a>) for the <code>org.eclipse.mylyn.internal.ide.ui.editors.templates</code>.
 * 
 * <pre>
 * &lt;extension point="org.eclipse.ui.editors.templates"
 *            id="org.eclipse.mylyn.internal.ide.ui.editors.templates"&gt;
 *   &lt;resolver
 *      class="org.eclipse.mylyn.internal.ide.ui.TasksTemplateVariableResolver"
 *      contextTypeId="<b>java</b>"
 *      description="%MylynTemplateVariableResolver.<b>activeTaskId.description</b>"
 *      name="<b>Active Task ID</b>"
 *      type="<b>activeTaskKey</b>"&gt;
 *   &lt;/resolver&gt;
 * &lt;/extension&gt;
 * </pre>
 * 
 * You have to define/change the:
 * <ul>
 * <li><b>contextTypeId</b> - you should define an resolver for the "java" and the "javadoc" context and maybe more
 * contexts.</li>
 * <li><b>type</b> - this is the property which can be used by users in there templates</li>
 * <li><b>description</b> - this is the localized description for the resolver, have a look into
 * <code>plugin.properties</code></li>
 * <li><b>name</b> - the display name of the resolver (for internal usage only)</li>
 * </ul>
 * </p>
 * <p>
 * The you must put your resolver code here.
 * <ol>
 * <li>add a new constant for the resolver-<b>type</b> ("activeTaskKey")</li>
 * <li>check your type in {@link #resolve(TemplateContext)} and resolve the type, return <code>null</code> if nothing
 * can resolved, always <b>trim</b> your result</li>
 * </ol>
 * </p>
 * <p>
 * <i>Each returned variable should be trimmed to avoid avoid unnecessary spaces between the resolved variables, i.e.
 * ("${activeTaskPrefix}${activeTaskKey}" should become "task2" and not "task 2").</i>
 * </p>
 * 
 * @author Andreas Hoehmann
 * @author Steffen Pingel
 * @since 3.7
 */
public class TaskTemplateVariableResolver extends TemplateVariableResolver {

	/**
	 * Resolver type to provide the ID of the active task.
	 */
	public static final String TYPE_ACTIVE_TASK_KEY = "activeTaskKey"; //$NON-NLS-1$

	/**
	 * Resolver type to provide the prefix (i.e. "bug") of the active task.
	 */
	public static final String TYPE_ACTIVE_TASK_PREFIX = "activeTaskPrefix"; //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolve(final TemplateContext context) {
		final String type = getType();
		if (TYPE_ACTIVE_TASK_KEY.equalsIgnoreCase(type)) {
			final ITask activeTask = TasksUi.getTaskActivityManager().getActiveTask();
			if (activeTask != null) {
				String taskKey = activeTask.getTaskKey();
				if (taskKey == null) {
					// use the task-id, i.e. for a local task, such a task doesn't have a task-key
					taskKey = activeTask.getTaskId();
				}
				if (taskKey != null) {
					return taskKey.trim();
				}
			}
		} else if (TYPE_ACTIVE_TASK_PREFIX.equalsIgnoreCase(type)) {
			final ITask activeTask = TasksUi.getTaskActivityManager().getActiveTask();
			if (activeTask != null) {
				String taskPrefix = TasksUiInternal.getTaskPrefix(activeTask.getConnectorKind());
				if (taskPrefix != null) {
					return taskPrefix;
				}
			}
		}
		return null;
	}

}
