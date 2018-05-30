/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.internal.github.core.gist.GistAttribute;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.internal.github.ui.issue.IssueSummaryPart;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskEditorActionPart;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * Gist task editor page class
 */
public class GistTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * @param editor
	 * @param connectorKind
	 */
	public GistTaskEditorPage(TaskEditor editor, String connectorKind) {
		super(editor, connectorKind);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	private CommandContributionItem createCommandContributionItem(
			String commandId) {
		CommandContributionItemParameter parameter = new CommandContributionItemParameter(
				getSite(), commandId, commandId,
				CommandContributionItem.STYLE_PUSH);
		return new CommandContributionItem(parameter);
	}

	private void addCloneAction(IToolBarManager manager) {
		if (TasksUiUtil.isOutgoingNewTask(getTask(), GistConnector.KIND))
			return;
		manager.prependToGroup(
				"open", createCommandContributionItem(CloneGistHandler.ID)); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage#fillToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	public void fillToolBar(IToolBarManager toolBarManager) {
		super.fillToolBar(toolBarManager);
		addCloneAction(toolBarManager);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage#createPartDescriptors()
	 */
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> partDescriptors = super
				.createPartDescriptors();
		Iterator<TaskEditorPartDescriptor> descriptorIt = partDescriptors
				.iterator();
		while (descriptorIt.hasNext()) {
			TaskEditorPartDescriptor partDescriptor = descriptorIt.next();
			String id = partDescriptor.getId();
			if (id.equals(ID_PART_ATTRIBUTES) || id.equals(ID_PART_SUMMARY)
					|| id.equals(ID_PART_ATTACHMENTS)
					|| id.equals(ID_PART_ACTIONS))
				descriptorIt.remove();
		}
		if (!getModel().getTaskData().isNew()) {
			partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {

				public AbstractTaskEditorPart createPart() {
					return new IssueSummaryPart(GistAttribute.AUTHOR_GRAVATAR
							.getMetadata().getId(), null);
				}
			}.setPath(PATH_HEADER));
			partDescriptors.add(new TaskEditorPartDescriptor(
					ID_PART_ATTACHMENTS) {

				public AbstractTaskEditorPart createPart() {
					return new GistAttachmentPart();
				}
			}.setPath(PATH_ATTACHMENTS));
		}
		partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_ACTIONS) {

			public AbstractTaskEditorPart createPart() {
				return new TaskEditorActionPart() {

					protected void addAttachContextButton(
							Composite buttonComposite, FormToolkit toolkit) {
						// Prohibit context button since Gists don't support
						// binary attachments
					}

				};
			}
		}.setPath(PATH_ACTIONS));

		return partDescriptors;
	}
}
