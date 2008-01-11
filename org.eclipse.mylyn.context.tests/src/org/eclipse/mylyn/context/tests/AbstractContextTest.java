/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on May 26, 2005
 */
package org.eclipse.mylyn.context.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public abstract class AbstractContextTest extends TestCase {

	private static final String MOCK_PROVIDER = "<mock-provider>";

	protected static final String MOCK_ORIGIN = "<mock-origin>";

	protected static final String MOCK_KIND = "java";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		if (ContextCorePlugin.getContextManager() != null) {
			assertFalse("" + ContextCorePlugin.getContextManager().getActiveContexts(),
					ContextCorePlugin.getContextManager().isContextActive());
		}
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (ContextCorePlugin.getContextManager() != null) {
			assertFalse("" + ContextCorePlugin.getContextManager().getActiveContexts(),
					ContextCorePlugin.getContextManager().isContextActive());
		}
	}

	protected InteractionEvent mockSelection(String handle) {
		return new InteractionEvent(InteractionEvent.Kind.SELECTION, MOCK_KIND, handle, MOCK_ORIGIN);
	}

	protected InteractionEvent mockPropagation(String handle) {
		return new InteractionEvent(InteractionEvent.Kind.PROPAGATION, MOCK_KIND, handle, MOCK_ORIGIN);
	}

	protected InteractionEvent mockSelection() {
		return mockSelection("<mock-handle>");
	}

	protected InteractionEvent mockNavigation(String toHandle) {
		return new InteractionEvent(InteractionEvent.Kind.SELECTION, MOCK_KIND, toHandle, MOCK_ORIGIN, MOCK_PROVIDER);
	}

	protected InteractionEvent mockInterestContribution(String handle, String kind, float value) {
		InteractionEvent event = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, MOCK_ORIGIN,
				value);
		return event;
	}

	protected InteractionEvent mockInterestContribution(String handle, float value) {
		return mockInterestContribution(handle, MOCK_KIND, value);
	}

	protected InteractionEvent mockPreferenceChange(String handle) {
		return new InteractionEvent(InteractionEvent.Kind.PREFERENCE, MOCK_KIND, handle, MOCK_ORIGIN);
	}

	protected boolean compareTaskscapeEquality(IInteractionContext t1, IInteractionContext t2) {
		return false;
	}

	protected IViewPart openView(String id) throws PartInitException {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(id);
	}
}
