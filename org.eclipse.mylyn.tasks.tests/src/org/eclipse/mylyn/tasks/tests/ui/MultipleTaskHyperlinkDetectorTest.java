/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylyn.internal.tasks.ui.editors.MultipleTaskHyperlinkDetector;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskHyperlinkDetector;
import org.eclipse.mylyn.tasks.ui.TaskHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/**
 * @author Sam Davis
 * @author Steffen Pingel
 */
public class MultipleTaskHyperlinkDetectorTest extends TestCase {

	protected MockRepositoryConnectorUi connectorUi1;

	protected MockRepositoryConnectorUi connectorUi2;

	protected TaskRepository repository1a;

	protected TaskRepository repository1b;

	protected TaskRepository repository2;

	@Override
	protected void setUp() throws Exception {
		// define 2 repositories using "test xxx" as task id
		repository1a = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL + "1a");
		repository1b = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND,
				MockRepositoryConnector.REPOSITORY_URL + "1b");
		connectorUi1 = new MockRepositoryConnectorUi() {
			private final Pattern HYPERLINK_PATTERN = Pattern.compile("(test \\d+)");

			@Override
			public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int index, int textOffset) {
				List<IHyperlink> links = new ArrayList<IHyperlink>();
				Matcher m = HYPERLINK_PATTERN.matcher(text);
				while (m.find()) {
					links.add(new TaskHyperlink(new Region(textOffset + m.start(), m.end() - m.start()), repository,
							m.group()));
				}
				return links.toArray(new IHyperlink[0]);
			};
		};
		// define 1 repository using "xxx" as task id
		repository2 = new TaskRepository(MockRepositoryConnector.CONNECTOR_KIND, MockRepositoryConnector.REPOSITORY_URL
				+ "2");
		connectorUi2 = new MockRepositoryConnectorUi();
		TasksUi.getRepositoryManager().addRepository(repository1a);
		TasksUi.getRepositoryManager().addRepository(repository1b);
		TasksUi.getRepositoryManager().addRepository(repository2);
	}

	protected IHyperlink[] detect(final String text, int start, int length, TaskRepository textViewerRepository) {
		AbstractTaskHyperlinkDetector detector = createHyperlinkDetector(textViewerRepository);
		return detector.detectHyperlinks(new MockTextViewer(text), new Region(start, length), true);
	}

	protected AbstractTaskHyperlinkDetector createHyperlinkDetector(final TaskRepository textViewerRepository) {
		TaskHyperlinkDetector detector = new MultipleTaskHyperlinkDetector() {
			@Override
			protected AbstractRepositoryConnectorUi getConnectorUi(TaskRepository repository) {
				if (repository.equals(repository1a) || repository.equals(repository1b)) {
					return connectorUi1;
				} else if (repository.equals(repository2)) {
					return connectorUi2;
				}
				return super.getConnectorUi(repository);
			}

			@Override
			protected TaskRepository getTaskRepository(ITextViewer textViewer) {
				return textViewerRepository;
			}
		};
		return detector;
	}

	public void testSingleRepositoryMatches() {
		IHyperlink[] links = detect("test 123 test 456 test 789", 9, 15, repository2);
		assertNotNull(links);
		assertEquals(2, links.length);
		assertEquals(new Region(14, 3), links[0].getHyperlinkRegion());
		assertEquals(new Region(23, 1), links[1].getHyperlinkRegion());
		assertEquals(repository2, ((TaskHyperlink) links[0]).getRepository());
	}

	public void testMultipleRepositoriesMatch() {
		IHyperlink[] links = detect("test 123 test 456 test 789", 9, 15, repository1a);
		assertNotNull(links);
		assertEquals(4, links.length);
		assertEquals(new Region(9, 8), links[0].getHyperlinkRegion());
		assertEquals(new Region(9, 8), links[2].getHyperlinkRegion());
		assertEquals(Arrays.asList(repository1a, repository1b),
				Arrays.asList(((TaskHyperlink) links[0]).getRepository(), ((TaskHyperlink) links[2]).getRepository()));
		assertEquals(new Region(18, 6), links[1].getHyperlinkRegion());
		assertEquals(new Region(18, 6), links[3].getHyperlinkRegion());
		assertEquals(Arrays.asList(repository1a, repository1b),
				Arrays.asList(((TaskHyperlink) links[1]).getRepository(), ((TaskHyperlink) links[3]).getRepository()));
	}

}
