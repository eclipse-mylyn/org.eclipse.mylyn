/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.core.tests.support.search;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.core.IMylarContextNode;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.internal.CompositeContext;
import org.eclipse.mylar.core.internal.MylarContext;
import org.eclipse.mylar.core.internal.MylarContextManager;
import org.eclipse.mylar.core.tests.AbstractContextTest;
import org.eclipse.mylar.core.tests.support.WorkspaceSetupHelper;

/**
 * @author Shawn Minto
 */
public class SearchTaskscapeNotifier extends AbstractContextTest {

	private CompositeContext context; 
	private String source;
	
	public SearchTaskscapeNotifier(CompositeContext context, String source){
		this.context = context;
		this.source = source;
	}
	
	public IMylarContextNode mockLowerInterest(IMylarContextNode node) {
		return context.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, -3));
    }
	
	public IMylarContextNode mockRaiseInterest(IMylarContextNode node) {
		return context.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, 2));
    }
	
	public IMylarContextNode mockLowerInterest(String handle, String kind) {
		return mockLowerInterest(mockEditorSelection(handle, kind));
    }
	
	public IMylarContextNode mockRaiseInterest(String handle, String kind) {
		return mockRaiseInterest(mockEditorSelection(handle, kind));
    }

	public IMylarContextNode mockEditorSelection(String handle, String kind) {
		context.addEvent(mockSelection(handle, kind, source));
		return context.addEvent(mockSelection(handle, kind, source));
	}
	
	public IMylarContextNode getElement(String handle, String kind) {
		IMylarContextNode node = context.addEvent(mockSelection(handle, kind, source));
		context.addEvent(mockUserEvent(handle, kind, source, (1/MylarContextManager.getScalingFactors().getLandmark()) * -2));
		return node;
	}
	
	public void clearTaskscape() throws IOException, CoreException{
		WorkspaceSetupHelper.clearDoiModel();
		MylarContext task = WorkspaceSetupHelper.getTaskscape();
    	MylarPlugin.getContextManager().contextActivated(task.getId(), task.getId());
    	context = (CompositeContext)MylarPlugin.getContextManager().getActiveContext();
	}
	
	private InteractionEvent mockSelection(String handle, String kind, String origin) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, kind, handle, origin);
    }
	
	private InteractionEvent mockUserEvent(String handle, String kind, String origin, float scale) {
        InteractionEvent e = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, origin, scale * MylarContextManager.getScalingFactors().getLandmark());
        e.getInterestContribution();
        return e;
	}
}
