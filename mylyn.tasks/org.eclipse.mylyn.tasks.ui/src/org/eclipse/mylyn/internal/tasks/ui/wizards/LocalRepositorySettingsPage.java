/*******************************************************************************
 * Copyright (c) 2004, 2011 David Green and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.internal.tasks.core.LocalRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractTaskRepositoryPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * A settings page for the local repository properties dialog. Local repositories have no settings, however they may have settings
 * contributed via the taskRepositoryPageContribution.
 *
 * @author David Green
 */
public class LocalRepositorySettingsPage extends AbstractTaskRepositoryPage {

	public LocalRepositorySettingsPage(TaskRepository taskRepository) {
		super(Messages.LocalRepositorySettingsPage_Local_Repository_Settings,
				Messages.LocalRepositorySettingsPage_Configure_the_local_repository, taskRepository);
	}

	@Override
	public String getConnectorKind() {
		return LocalRepositoryConnector.CONNECTOR_KIND;
	}

	@Override
	public String getRepositoryUrl() {
		return LocalRepositoryConnector.REPOSITORY_URL;
	}

	@Override
	protected void createSettingControls(Composite parent) {
		// nothing to do, since the local repository has no settings
	}

	@Override
	protected IStatus validate() {
		// nothing to do
		return null;
	}

	@Override
	protected void createContributionControls(Composite parentControl) {
		super.createContributionControls(parentControl);
		// expand the first contribution since we have no other settings
		Control[] children = parentControl.getChildren();
		if (children.length > 0) {
			if (children[0] instanceof ExpandableComposite) {
				((ExpandableComposite) children[0]).setExpanded(true);
			}
		}
	}

}
