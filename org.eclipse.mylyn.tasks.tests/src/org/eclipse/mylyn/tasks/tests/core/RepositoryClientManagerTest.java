/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import java.io.File;
import java.io.Serializable;

import junit.framework.TestCase;

import org.eclipse.mylyn.tasks.core.RepositoryClientManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Benjamin Muskalla
 */
public class RepositoryClientManagerTest extends TestCase {

	public static class MyConfig implements Serializable {

		private static final long serialVersionUID = 5105526708474366441L;

		public String someString = "mylyn";

		public ConfigSubObject[] anArray = new ConfigSubObject[] { new ConfigSubObject() };

		public TaskRepository repository = null;
	}

	public static class ConfigSubObject implements Serializable {
		private static final long serialVersionUID = -8730054324154087433L;
	}

	private class MockRepositoryClientManager extends RepositoryClientManager<Object, MyConfig> {

		private Throwable throwable;

		public MockRepositoryClientManager(File cacheFile) {
			super(cacheFile, MyConfig.class);
		}

		@Override
		protected Object createClient(TaskRepository taskRepository, MyConfig data) {
			return new Object();
		}

		@Override
		protected void handleError(String message, Throwable e) {
			throwable = e;
		}

		public Throwable getThrowable() {
			return throwable;
		}

	}

	public void testClassloadingSerialize() throws Exception {
		File cacheFile = File.createTempFile("config", "");
		cacheFile.delete();
		MockRepositoryClientManager manager = new MockRepositoryClientManager(cacheFile);
		TaskRepository repository = new TaskRepository("kind", "url");
		manager.getClient(repository);
		manager.writeCache();
		assertNull(manager.getThrowable());
		manager.readCache();
		assertNull(manager.getThrowable());
	}
}
