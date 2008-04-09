/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.java.tests;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.java.ui.JavaEditingMonitor;
import org.eclipse.mylyn.internal.java.ui.JavaStructureBridge;
import org.eclipse.mylyn.internal.java.ui.JavaUiBridgePlugin;
import org.eclipse.mylyn.resources.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.resources.tests.ResourceTestUtil;

/**
 * @author Mik Kersten
 */
public abstract class AbstractJavaContextTest extends AbstractContextTest {

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected JavaEditingMonitor monitor = new JavaEditingMonitor();

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
		assertNotNull(JavaPlugin.getDefault());
		assertNotNull(JavaUiBridgePlugin.getDefault());
		project = new TestJavaProject(this.getClass().getSimpleName());
		nonJavaProject = new TestProject(this.getClass().getSimpleName() + "nonJava");
		p1 = project.createPackage("p1");
		type1 = project.createType(p1, "Type1.java", "public class Type1 { }");
		context = new InteractionContext(contextId, scaling);
		context.reset();
		manager.internalActivateContext(context);
		assertNotNull(JavaUiBridgePlugin.getDefault());
		assertTrue(ContextCorePlugin.getDefault().getStructureBridges().toString().indexOf(
				JavaStructureBridge.class.getCanonicalName()) != -1);

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
		manager.getFileForContext(contextId).delete();

		ResourceTestUtil.deleteProject(project.getProject());
		ResourceTestUtil.deleteProject(nonJavaProject.getProject());

		for (InteractionContext context : manager.getActiveContexts()) {
			manager.deactivateContext(context.getHandleIdentifier());
		}
		assertFalse(manager.isContextActive());
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

	class TestProgressMonitor implements IProgressMonitor {

		boolean done = false;

		public void beginTask(String name, int totalWork) {
			// TODO Auto-generated method stub

		}

		public void done() {
			done = true;
		}

		public void internalWorked(double work) {
			// TODO Auto-generated method stub

		}

		public boolean isCanceled() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setCanceled(boolean value) {
			// TODO Auto-generated method stub

		}

		public void setTaskName(String name) {
			// TODO Auto-generated method stub

		}

		public void subTask(String name) {
			// TODO Auto-generated method stub

		}

		public void worked(int work) {
			// TODO Auto-generated method stub

		}

		public boolean isDone() {
			return done;
		}
	}
}
