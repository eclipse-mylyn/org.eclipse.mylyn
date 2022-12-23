/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.connector;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.wizards.ITaskRepositoryPage;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class MockRepositoryConnectorUi extends AbstractRepositoryConnectorUi {

	private static Pattern HYPERLINK_PATTERN = Pattern.compile("(\\d+)");

	public MockRepositoryConnectorUi() {
		super();
	}

	public MockRepositoryConnectorUi(AbstractRepositoryConnector connector) {
		super(connector);
	}

	@Override
	public String getConnectorKind() {
		return "mock";
	}

	@Override
	public ITaskRepositoryPage getSettingsPage(TaskRepository taskRepository) {
		return new MockRepositorySettingsPage(taskRepository);
	}

	@Override
	public boolean hasSearchPage() {
		return false;
	}

	@Override
	public IWizard getQueryWizard(TaskRepository repository, IRepositoryQuery query) {
		// ignore
		return null;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, ITaskMapping selection) {
		// ignore
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();
		Matcher m = HYPERLINK_PATTERN.matcher(text);
		while (m.find()) {
			links.add(
					new TaskHyperlink(new Region(textOffset + m.start(), m.end() - m.start()), repository, m.group()));
		}
		return links.toArray(new IHyperlink[0]);
	}
}
