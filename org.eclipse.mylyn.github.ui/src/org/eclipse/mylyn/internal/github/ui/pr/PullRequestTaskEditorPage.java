/******************************************************************************
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
 *****************************************************************************/
package org.eclipse.mylyn.internal.github.ui.pr;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.mylyn.internal.github.core.pr.PullRequestAttribute;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestComposite;
import org.eclipse.mylyn.internal.github.core.pr.PullRequestConnector;
import org.eclipse.mylyn.internal.github.ui.issue.IssueSummaryPart;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

/**
 * Editor page for GitHub pull requests.
 */
public class PullRequestTaskEditorPage extends AbstractTaskEditorPage {

	private PullRequestComposite prComp;

	/**
	 * Constructor for the GitHubTaskEditorPage
	 * 
	 * @param editor
	 *            The task editor to create for GitHub
	 */
	public PullRequestTaskEditorPage(final TaskEditor editor) {
		super(editor, PullRequestConnector.KIND);
		setNeedsPrivateSection(true);
		setNeedsSubmitButton(true);
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		prComp = PullRequestConnector.getPullRequest(getModel().getTaskData());
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
				return new IssueSummaryPart(
						PullRequestAttribute.REPORTER_GRAVATAR.getMetadata()
								.getId(), null);
			}
		}.setPath(PATH_HEADER));
		partDescriptors.add(new TaskEditorPartDescriptor(ID_PART_ATTRIBUTES) {

			public AbstractTaskEditorPart createPart() {
				return new CommitAttributePart(prComp);
			}
		}.setPath(PATH_ATTACHMENTS));
		return partDescriptors;
	}
}
