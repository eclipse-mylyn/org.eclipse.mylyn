/*******************************************************************************
 * Copyright (c) 2004, 2023 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     ArSysOp - adapt to SimRel 2023-06
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import org.eclipse.mylyn.commons.sdk.util.UiTestUtil;
import org.eclipse.mylyn.context.sdk.util.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.mylyn.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourceInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

/**
 * @author Mik Kersten
 * @deprecated use {@link org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest} instead
 */
@Deprecated
public abstract class AbstractResourceContextTest extends AbstractContextTest {

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected ResourceInteractionMonitor monitor;

	protected ResourceStructureBridge structureBridge = new ResourceStructureBridge();

	protected TestProject project;

	protected InteractionContext context;

	protected InteractionContextScaling scaling = new InteractionContextScaling();

	protected String taskId = this.getClass().getName();

	protected ProjectExplorer navigator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(IdeUiBridgePlugin.getDefault());
		monitor = new ResourceInteractionMonitor();
		project = new TestProject(this.getClass().getName());
		context = new InteractionContext(taskId, scaling);
		context.reset();
		manager.internalActivateContext(context);
		ContextUiPlugin.getViewerManager().setSyncRefreshMode(true);
		navigator = (ProjectExplorer) UiTestUtil.openView(IdeUiUtil.ID_NAVIGATOR);
		assertNotNull(navigator);
	}

	@Override
	protected void tearDown() throws Exception {
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(taskId);
		manager.deleteContext(taskId);
		ContextCorePlugin.getContextStore().getFileForContext(taskId).delete();
		monitor.dispose();
		ResourceTestUtil.deleteProject(project.getProject());
		super.tearDown();
	}
}
