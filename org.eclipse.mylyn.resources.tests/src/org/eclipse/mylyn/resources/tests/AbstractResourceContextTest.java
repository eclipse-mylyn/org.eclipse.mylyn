/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.resources.tests;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.tests.AbstractContextTest;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.internal.context.core.MylarContext;
import org.eclipse.mylar.internal.context.core.ContextManager;
import org.eclipse.mylar.internal.context.core.ScalingFactors;
import org.eclipse.mylar.internal.ide.MylarIdePlugin;
import org.eclipse.mylar.internal.ide.ui.IdeUiUtil;
import org.eclipse.mylar.internal.resources.ResourceInteractionMonitor;
import org.eclipse.mylar.internal.resources.ResourceStructureBridge;
import org.eclipse.ui.views.navigator.ResourceNavigator;

/**
 * @author Mik Kersten
 */
public abstract class AbstractResourceContextTest extends AbstractContextTest {

	protected ContextManager manager = ContextCorePlugin.getContextManager();

	protected ResourceInteractionMonitor monitor = new ResourceInteractionMonitor();

	protected ResourceStructureBridge structureBridge = new ResourceStructureBridge();

	protected TestProject project;

	protected MylarContext context;

	protected ScalingFactors scaling = new ScalingFactors();

	protected String taskId = this.getClass().getName();

	protected ResourceNavigator navigator;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		assertNotNull(MylarIdePlugin.getDefault());
		project = new TestProject(this.getClass().getName());
		context = new MylarContext(taskId, scaling);
		context.reset();
		manager.activateContext(context);
		ContextUiPlugin.getDefault().getViewerManager().setSyncRefreshMode(true);
		navigator = (ResourceNavigator) openView(IdeUiUtil.ID_NAVIGATOR);
		assertNotNull(navigator);
	}

	@Override
	protected void tearDown() throws Exception {
		context.reset();
		assertTrue(context.getInteresting().isEmpty());
		manager.deactivateContext(taskId);
		manager.deleteContext(taskId);
		manager.getFileForContext(taskId).delete();
		ResourceTestUtil.deleteProject(project.getProject());
		super.tearDown();
	}
}
