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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.core;

import java.io.File;
import java.io.Serializable;

import org.eclipse.mylyn.tasks.core.RepositoryClientManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;

import junit.framework.TestCase;

/**
 * @author Benjamin Muskalla
 */
@SuppressWarnings("nls")
public class RepositoryClientManagerTest extends TestCase {

	public static class MyConfig implements Serializable {

		private static final long serialVersionUID = 5105526708474366441L;

		public String someString = "mylyn";

		public ConfigSubObject[] anArray = { new ConfigSubObject() };

		public TaskRepository repository = null;
	}

	public static class ConfigSubObject implements Serializable {
		private static final long serialVersionUID = -8730054324154087433L;
	}

	private static class MockRepositoryClientManager extends RepositoryClientManager<Object, MyConfig> {

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
