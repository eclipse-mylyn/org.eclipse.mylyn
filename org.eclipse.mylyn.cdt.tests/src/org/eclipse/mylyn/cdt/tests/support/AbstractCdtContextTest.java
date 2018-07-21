/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.cdt.tests.support;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.sdk.util.ResourceTestUtil;
import org.eclipse.mylyn.commons.sdk.util.TestProject;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.cdt.ui.CDTStructureBridge;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;

/**
 * @author Mik Kersten
 */
public abstract class AbstractCdtContextTest extends AbstractContextTest {

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected CdtProject project;

	protected TestProject nonJavaProject;

	protected String contextId = this.getClass().getSimpleName();

	protected InteractionContext context;

	protected InteractionContextScaling scaling = new InteractionContextScaling();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		ContextTestUtil.triggerContextUiLazyStart();
		project = new CdtProject(getClass().getSimpleName());
		context = new InteractionContext(contextId, scaling);
		context.reset();
		manager.internalActivateContext(context);
		assertTrue(ContextCorePlugin.getDefault()
				.getStructureBridges()
				.toString()
				.indexOf(CDTStructureBridge.class.getCanonicalName()) != -1);

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

}
