/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.java.tests.search;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.tests.AbstractContextTest;
import org.eclipse.mylyn.internal.context.core.CompositeInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.InteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Shawn Minto
 */
public class ActiveSearchNotifier extends AbstractContextTest {

	private CompositeInteractionContext context;

	private final String source;

	public ActiveSearchNotifier(CompositeInteractionContext context, String source) {
		this.context = context;
		this.source = source;
	}

	public IInteractionElement mockLowerInterest(IInteractionElement node) {
		return context.addEvent(mockUserEvent(node.getHandleIdentifier(), node.getContentType(), source, -3));
	}

	public IInteractionElement mockRaiseInterest(IInteractionElement node) {
		return context.addEvent(mockUserEvent(node.getHandleIdentifier(), node.getContentType(), source, 2));
	}

	public IInteractionElement mockLowerInterest(String handle, String kind) {
		return mockLowerInterest(mockEditorSelection(handle, kind));
	}

	public IInteractionElement mockRaiseInterest(String handle, String kind) {
		return mockRaiseInterest(mockEditorSelection(handle, kind));
	}

	public IInteractionElement mockEditorSelection(String handle, String kind) {
		context.addEvent(mockSelection(handle, kind, source));
		return context.addEvent(mockSelection(handle, kind, source));
	}

	public IInteractionElement getElement(String handle, String kind) {
		IInteractionElement node = context.addEvent(mockSelection(handle, kind, source));
		ContextCorePlugin.getContextManager().processInteractionEvent(
				mockUserEvent(handle, kind, source, (1 / ContextCore.getCommonContextScaling().getLandmark()) * -2),
				true);
		return node;
	}

	public void clearContext() throws IOException, CoreException {
		WorkspaceSetupHelper.clearDoiModel();
		try {
			InteractionContext workspaceContext = WorkspaceSetupHelper.getContext();
			ContextCore.getContextManager().activateContext(workspaceContext.getHandleIdentifier());
			context = (CompositeInteractionContext) ContextCore.getContextManager().getActiveContext();
		} catch (Exception e) {
			fail();
		}
	}

	private InteractionEvent mockSelection(String handle, String kind, String origin) {
		return new InteractionEvent(InteractionEvent.Kind.SELECTION, kind, handle, origin);
	}

	private InteractionEvent mockUserEvent(String handle, String kind, String origin, float scale) {
		InteractionEvent e = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, origin, scale
				* ContextCore.getCommonContextScaling().getLandmark());
		e.getInterestContribution();
		return e;
	}
}
