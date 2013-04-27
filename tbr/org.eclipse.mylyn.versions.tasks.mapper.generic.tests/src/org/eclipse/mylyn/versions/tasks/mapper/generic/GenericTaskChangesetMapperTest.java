/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.mapper.generic;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
/**
 *
 * @author Kilian Matt
 *
 */
public class GenericTaskChangesetMapperTest {

	private GenericTaskChangesetMapper mapper;
	private IChangeSetIndexSearcher indexSearcher;
	private IConfiguration configuration;
	@Before
	public void setUp() throws Exception {
		configuration = mock(IConfiguration.class);
		indexSearcher = mock(IChangeSetIndexSearcher.class);
		mapper = new GenericTaskChangesetMapper(configuration, indexSearcher);

		ScmConnector connector = mock(ScmConnector.class);
		when(configuration.getRepositoriesFor(any(ITask.class))).thenReturn(Collections.singletonList(new ScmRepository(connector,"test", "http://localhost")));
	}

	@Test
	public void testGetChangesetsForTask_TaskNullNotAllowed()
			throws CoreException {
		IChangeSetMapping mapping = new TestChangeSetMapping(null);
		try {
			mapper.getChangesetsForTask(mapping, new NullProgressMonitor());
			fail();
		} catch (IllegalArgumentException ex) {

		}
	}

	@Test
	public void testGetChangesetsForTask() throws CoreException {

		ITask task = mock(ITask.class);
		TestChangeSetMapping mapping = new TestChangeSetMapping(task);
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				IChangeSetCollector collector = (IChangeSetCollector) invocation.getArguments()[3];
				collector.collect("123", (String) invocation.getArguments()[1]);
				return null;
			}

		}).when(indexSearcher).search(eq(task), anyString(), anyInt(),
				any(IChangeSetCollector.class));
		mapper.getChangesetsForTask(mapping, new NullProgressMonitor());
		assertEquals(1, mapping.getChangeSets().size());
	}

	private static class TestChangeSetMapping implements IChangeSetMapping {
		private List<ChangeSet> changes = new ArrayList<ChangeSet>();
		private ITask task;

		TestChangeSetMapping(ITask task) {
			this.task = task;
		}

		@Override
		public ITask getTask() {
			return task;
		}

		@Override
		public void addChangeSet(ChangeSet changeset) {
			changes.add(changeset);
		}

		public List<ChangeSet> getChangeSets() {
			return changes;
		}

	}

}
