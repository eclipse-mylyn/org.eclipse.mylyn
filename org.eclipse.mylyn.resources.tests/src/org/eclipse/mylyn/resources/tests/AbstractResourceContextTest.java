/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.resources.tests;

import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;
import org.eclipse.mylyn.internal.context.core.InteractionContextScaling;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.mylyn.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylyn.internal.resources.ui.ResourceInteractionMonitor;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public abstract class AbstractResourceContextTest extends AbstractContextTest {

	protected InteractionContextManager manager = ContextCorePlugin.getContextManager();

	protected ResourceInteractionMonitor monitor = new ResourceInteractionMonitor();

	protected ResourceStructureBridge structureBridge = new ResourceStructureBridge();

	protected TestProject project;

	protected InteractionContext context;

	protected InteractionContextScaling scaling = new InteractionContextScaling();

	protected String taskId = this.getClass().getName();

	protected ResourceNavigator navigator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(IdeUiBridgePlugin.getDefault());
		project = new TestProject(this.getClass().getName());
		context = new InteractionContext(taskId, scaling);
		context.reset();
		manager.internalActivateContext(context);
		ContextUiPlugin.getViewerManager().setSyncRefreshMode(true);
		navigator = (ResourceNavigator) TestUtil.openView(IdeUiUtil.ID_NAVIGATOR);
		assertNotNull(navigator);
	}

	@Override
	protected void tearDown() throws Exception {
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(taskId);
		manager.deleteContext(taskId);
		ContextCorePlugin.getContextStore().getFileForContext(taskId).delete();
		ResourceTestUtil.deleteProject(project.getProject());
		super.tearDown();
	}
}
