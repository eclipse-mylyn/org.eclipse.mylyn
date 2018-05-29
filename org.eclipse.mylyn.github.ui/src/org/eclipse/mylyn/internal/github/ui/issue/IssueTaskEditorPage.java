/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *     Kaloyan Raev <kaloyan.raev@sap.com> - bug 390757
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.core.issue.IssueAttribute;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

/**
 * Editor page for GitHub.
 */
public class IssueTaskEditorPage extends AbstractTaskEditorPage {

	/**
	 * Constructor for the GitHubTaskEditorPage
	 *
	 * @param editor
	 *            The task editor to create for GitHub
	 */
	public IssueTaskEditorPage(final TaskEditor editor) {
		super(editor, GitHub.CONNECTOR_KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> partDescriptors = super
				.createPartDescriptors();
		Iterator<TaskEditorPartDescriptor> descriptorIt = partDescriptors
				.iterator();
		while (descriptorIt.hasNext()) {
			TaskEditorPartDescriptor partDescriptor = descriptorIt.next();
			String id = partDescriptor.getId();
			if (id.equals(ID_PART_ATTRIBUTES) || id.equals(ID_PART_SUMMARY))
				descriptorIt.remove();
		}
		partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_SUMMARY) {

			public AbstractTaskEditorPart createPart() {
				return new IssueSummaryPart(IssueAttribute.REPORTER_GRAVATAR
						.getMetadata().getId(),
						IssueAttribute.ASSIGNEE_GRAVATAR.getMetadata().getId());
			}
		}.setPath(PATH_HEADER));
		partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {

			public AbstractTaskEditorPart createPart() {
				return new IssueAttributePart();
			}
		}.setPath(PATH_ATTRIBUTES));
		return partDescriptors;
	}

	@Override
	protected void createParts() {
		super.createParts();
		checkCanSubmit(IMessageProvider.INFORMATION);
	}

	@Override
	public void refresh() {
		super.refresh();
		checkCanSubmit(IMessageProvider.INFORMATION);
	}

	@Override
	public void doSubmit() {
		if (!checkCanSubmit(IMessageProvider.ERROR))
			return;
		super.doSubmit();
	}

	private boolean checkCanSubmit(final int type) {
		final TaskRepository taskRepository = getModel().getTaskRepository();
		AuthenticationCredentials cred = taskRepository.getCredentials(AuthenticationType.REPOSITORY);
		if (cred == null || cred.getUserName() == null || cred.getUserName().equals("")) { //$NON-NLS-1$
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					getTaskEditor().setMessage(Messages.IssueTaskEditorPage_MessageAnonymousCannotSubmit, type,
							new HyperlinkAdapter() {
								@Override
								public void linkActivated(HyperlinkEvent e) {
									TasksUiUtil.openEditRepositoryWizard(taskRepository);
									refresh();
								}
							});
				}
			});
			return false;
		}
		return true;
	}
}
