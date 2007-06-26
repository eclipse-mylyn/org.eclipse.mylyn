/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.TaskRepositoriesExternalizer;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @author Erik Ramfelt (bug 168782)
 */
public class TaskRepositoriesExternalizerTest extends TestCase {

	private Set<TaskRepository> taskRepositories = new HashSet<TaskRepository>();

	private static final String REP_TYPE = "bugzilla";

	private static final String REPURL1 = "http://somewhere1";

	private static final String REPURL2 = "http://somewhere2";

	private static final String TIMEZONE = "test time zone";

	private static final String VERSION = "test version";

	private static final String ENCODING = "test encoding";

	private static final String TIMESTAMP = "test time stamp";

	private static final String SUFFIX = "2";

	private TaskRepository repository1;

	private TaskRepository repository2;

	@Override
	protected void setUp() throws Exception {
		repository1 = new TaskRepository(REP_TYPE, REPURL1);
		repository2 = new TaskRepository("bugzilla", REPURL2);

		repository1.setTimeZoneId(TIMEZONE);
		repository1.setVersion(VERSION);
		repository1.setCharacterEncoding(ENCODING);
		repository1.setSynchronizationTimeStamp(TIMESTAMP);
		taskRepositories.add(repository1);

		repository2.setTimeZoneId(TIMEZONE + SUFFIX);
		repository2.setVersion(VERSION + SUFFIX);
		repository2.setCharacterEncoding(ENCODING + SUFFIX);
		repository2.setSynchronizationTimeStamp(TIMESTAMP + SUFFIX);
		taskRepositories.add(repository2);
	}

	public void testExternalization() {
		TaskRepositoriesExternalizer externalizer = new TaskRepositoriesExternalizer();
		String path = "repositories.xml";
		File file = new File(path);
		file.deleteOnExit();
		externalizer.writeRepositoriesToXML(taskRepositories, file);
		taskRepositories.clear();
		assertEquals(0, taskRepositories.size());
		taskRepositories = externalizer.readRepositoriesFromXML(file);
		assertEquals(2, taskRepositories.size());
		taskRepositories.contains(repository1);
		taskRepositories.contains(repository2);
		for (TaskRepository repository : taskRepositories) {
			if (repository.getUrl().equals(REPURL1)) {
				assertEquals(TIMEZONE, repository.getTimeZoneId());
				assertEquals(VERSION, repository.getVersion());
				assertEquals(ENCODING, repository.getCharacterEncoding());
				assertEquals(TIMESTAMP, repository.getSynchronizationTimeStamp());
			} else if (repository.getUrl().equals(REPURL2)) {
				assertEquals(TIMEZONE + SUFFIX, repository.getTimeZoneId());
				assertEquals(VERSION + SUFFIX, repository.getVersion());
				assertEquals(ENCODING + SUFFIX, repository.getCharacterEncoding());
				assertEquals(TIMESTAMP + SUFFIX, repository.getSynchronizationTimeStamp());
			}
		}

	}

	public void testExternalizationEmptyRepository() {
		TaskRepositoriesExternalizer externalizer = new TaskRepositoriesExternalizer();
		String path = "repositories.xml";
		File file = new File(path);
		file.deleteOnExit();
		externalizer.writeRepositoriesToXML(taskRepositories, file);
		taskRepositories = externalizer.readRepositoriesFromXML(file);
		assertEquals(2, taskRepositories.size());
		taskRepositories.clear();
		externalizer.writeRepositoriesToXML(taskRepositories, file);
		taskRepositories = externalizer.readRepositoriesFromXML(file);
		assertEquals(0, taskRepositories.size());
	}

}
