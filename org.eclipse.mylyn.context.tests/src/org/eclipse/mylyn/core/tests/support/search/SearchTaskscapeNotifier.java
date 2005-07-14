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
import org.eclipse.mylar.core.internal.ContextManager;
import org.eclipse.mylar.core.internal.Context;
import org.eclipse.mylar.core.tests.AbstractTaskscapeTest;
import org.eclipse.mylar.core.tests.support.WorkspaceSetupHelper;


public class SearchTaskscapeNotifier extends AbstractTaskscapeTest {

	private CompositeContext taskscape; 
	private String source;
	
	public SearchTaskscapeNotifier(CompositeContext taskscape, String source){
		this.taskscape = taskscape;
		this.source = source;
	}
	
	public IMylarContextNode mockLowerInterest(IMylarContextNode node) {
		return taskscape.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, -3));
    }
	
	public IMylarContextNode mockRaiseInterest(IMylarContextNode node) {
		return taskscape.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, 2));
    }
	
	public IMylarContextNode mockLowerInterest(String handle, String kind) {
		return mockLowerInterest(mockEditorSelection(handle, kind));
    }
	
	public IMylarContextNode mockRaiseInterest(String handle, String kind) {
		return mockRaiseInterest(mockEditorSelection(handle, kind));
    }

	public IMylarContextNode mockEditorSelection(String handle, String kind) {
		taskscape.addEvent(mockSelection(handle, kind, source));
		return taskscape.addEvent(mockSelection(handle, kind, source));
	}
	
	public IMylarContextNode getElement(String handle, String kind) {
		IMylarContextNode node = taskscape.addEvent(mockSelection(handle, kind, source));
		taskscape.addEvent(mockUserEvent(handle, kind, source, (1/ContextManager.getScalingFactors().getLandmark()) * -2));
		return node;
	}
	
	public void clearTaskscape() throws IOException, CoreException{
		WorkspaceSetupHelper.clearDoiModel();
		Context task = WorkspaceSetupHelper.getTaskscape();
    	MylarPlugin.getContextManager().taskActivated(task.getId(), task.getId());
    	taskscape = MylarPlugin.getContextManager().getActiveContext();
	}
	
	private InteractionEvent mockSelection(String handle, String kind, String origin) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, kind, handle, origin);
    }
	
	private InteractionEvent mockUserEvent(String handle, String kind, String origin, float scale) {
        InteractionEvent e = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, origin, scale * ContextManager.getScalingFactors().getLandmark());
        e.getInterestContribution();
        return e;
	}
}
