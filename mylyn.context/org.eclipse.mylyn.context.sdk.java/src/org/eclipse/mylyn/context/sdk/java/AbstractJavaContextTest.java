/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.sdk.java;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.java.ui.JavaEditingMonitor;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public abstract class AbstractJavaContextTest extends AbstractContextTest {

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected JavaEditingMonitor monitor;

	protected TestJavaProject project;

	protected TestProject nonJavaProject;

	protected IPackageFragment p1;

	protected IType type1;

	protected String contextId = this.getClass().getSimpleName();

	protected InteractionContext context;

	protected InteractionContextScaling scaling = new InteractionContextScaling();

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ContextTestUtil.triggerContextUiLazyStart();
		assertNotNull(JavaPlugin.getDefault());
		assertNotNull(JavaUiBridgePlugin.getDefault());
		assertNotNull(ResourcesUiBridgePlugin.getDefault());
		String bridges = ContextCorePlugin.getDefault().getStructureBridges().toString();
		assertTrue("Expected JavaStructureBridge not available: " + bridges,
				bridges.indexOf(JavaStructureBridge.class.getCanonicalName()) != -1);
		assertTrue("Expected initialized ResourcesUiBridge", ResourcesUiBridgePlugin.getDefault().isStarted());
		monitor = new JavaEditingMonitor();
		project = new TestJavaProject(this.getClass().getSimpleName());
		nonJavaProject = new TestProject(this.getClass().getSimpleName() + "nonJava");
		p1 = project.createPackage("p1");
		type1 = project.createType(p1, "Type1.java", "public class Type1 { }");

		context = new InteractionContext(contextId, scaling);
		context.reset();
		manager.internalActivateContext(context);

		ContextUiPlugin.getViewerManager().setSyncRefreshMode(true);
		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(false);
	}

	@Override
	protected void tearDown() throws Exception {
		ResourcesUiBridgePlugin.getDefault().setResourceMonitoringEnabled(true);
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(contextId);
		manager.deleteContext(contextId);
		ContextCorePlugin.getContextStore().getFileForContext(contextId).delete();

		ResourceTestUtil.deleteProject(project.getProject());
		ResourceTestUtil.deleteProject(nonJavaProject.getProject());

		for (InteractionContext context : manager.getActiveContexts()) {
			manager.deactivateContext(context.getHandleIdentifier());
		}
		assertFalse(manager.isContextActive());
		monitor.dispose();
		waitForAutoBuild();
		super.tearDown();
	}

	public static void waitForAutoBuild() {
		boolean wasInterrupted = false;
		do {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
				wasInterrupted = false;
			} catch (OperationCanceledException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				wasInterrupted = true;
			}
		} while (wasInterrupted);
	}

	public class TestProgressMonitor implements IProgressMonitor {

		boolean done = false;

		@Override
		public void beginTask(String name, int totalWork) {
			// TODO Auto-generated method stub

		}

		@Override
		public void done() {
			done = true;
		}

		@Override
		public void internalWorked(double work) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setCanceled(boolean value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setTaskName(String name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void subTask(String name) {
			// TODO Auto-generated method stub

		}

		@Override
		public void worked(int work) {
			// TODO Auto-generated method stub

		}

		public boolean isDone() {
			return done;
		}
	}
}
