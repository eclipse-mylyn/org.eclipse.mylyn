/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.ui;

import java.text.MessageFormat;

import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestCore;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.widgets.Composite;

public class BugzillaRestRepositorySettingsPage extends AbstractRepositorySettingsPage {
	private static final String LABEL_VERSION_NUMBER = "5.0"; //$NON-NLS-1$

	private static final String DESCRIPTION = MessageFormat.format(
			Messages.BugzillaRestRepositorySettingsPage_SupportsVersionMessage,
			LABEL_VERSION_NUMBER);

	public BugzillaRestRepositorySettingsPage(TaskRepository taskRepository, AbstractRepositoryConnector connector,
			AbstractRepositoryConnectorUi connectorUi) {
		super(Messages.BugzillaRestRepositorySettingsPage_RestRepositorySetting, DESCRIPTION, taskRepository, connector, connectorUi);
		setNeedsAnonymousLogin(true);
		setNeedsEncoding(false);
		setNeedsAdvanced(false);
		setNeedsValidateOnFinish(true);
	}

	@Override
	public String getConnectorKind() {
		return BugzillaRestCore.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore

	}
}
