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
package org.eclipse.mylar.java.tests.search;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.tests.AbstractContextTest;
import org.eclipse.mylar.internal.context.core.CompositeContext;
import org.eclipse.mylar.internal.context.core.MylarContext;
import org.eclipse.mylar.internal.context.core.ContextManager;
import org.eclipse.mylar.monitor.core.InteractionEvent;

/**
 * @author Shawn Minto
 */
public class ActiveSearchNotifier extends AbstractContextTest {

	private CompositeContext context;

	private String source;

	public ActiveSearchNotifier(CompositeContext context, String source) {
		this.context = context;
		this.source = source;
	}

	public IMylarElement mockLowerInterest(IMylarElement node) {
		return context.addEvent(mockUserEvent(node.getHandleIdentifier(), node.getContentType(), source, -3));
	}

	public IMylarElement mockRaiseInterest(IMylarElement node) {
		return context.addEvent(mockUserEvent(node.getHandleIdentifier(), node.getContentType(), source, 2));
	}

	public IMylarElement mockLowerInterest(String handle, String kind) {
		return mockLowerInterest(mockEditorSelection(handle, kind));
	}

	public IMylarElement mockRaiseInterest(String handle, String kind) {
		return mockRaiseInterest(mockEditorSelection(handle, kind));
	}

	public IMylarElement mockEditorSelection(String handle, String kind) {
		context.addEvent(mockSelection(handle, kind, source));
		return context.addEvent(mockSelection(handle, kind, source));
	}

	public IMylarElement getElement(String handle, String kind) {
		IMylarElement node = context.addEvent(mockSelection(handle, kind, source));
		ContextCorePlugin.getContextManager().handleInteractionEvent(
				mockUserEvent(handle, kind, source, (1 / ContextManager.getScalingFactors().getLandmark()) * -2),
				true);
		// context.addEvent(mockUserEvent(handle, kind, source,
		// (1/MylarContextManager.getScalingFactors().getLandmark()) * -2));
		return node;
	}

	public void clearContext() throws IOException, CoreException {
		WorkspaceSetupHelper.clearDoiModel();
		try {
			MylarContext workspaceContext = WorkspaceSetupHelper.getContext();
			ContextCorePlugin.getContextManager().activateContext(workspaceContext.getHandleIdentifier());
			context = (CompositeContext) ContextCorePlugin.getContextManager().getActiveContext();
		} catch (Exception e) {
			fail();
		}
	}

	private InteractionEvent mockSelection(String handle, String kind, String origin) {
		return new InteractionEvent(InteractionEvent.Kind.SELECTION, kind, handle, origin);
	}

	private InteractionEvent mockUserEvent(String handle, String kind, String origin, float scale) {
		InteractionEvent e = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, origin, scale
				* ContextManager.getScalingFactors().getLandmark());
		e.getInterestContribution();
		return e;
	}
}
