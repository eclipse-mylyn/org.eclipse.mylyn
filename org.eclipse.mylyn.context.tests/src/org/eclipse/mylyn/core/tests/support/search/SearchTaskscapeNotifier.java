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
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.model.ITaskscapeNode;
import org.eclipse.mylar.core.model.InteractionEvent;
import org.eclipse.mylar.core.model.TaskscapeManager;
import org.eclipse.mylar.core.model.internal.CompositeTaskscape;
import org.eclipse.mylar.core.model.internal.Taskscape;
import org.eclipse.mylar.core.tests.AbstractTaskscapeTest;
import org.eclipse.mylar.core.tests.support.WorkspaceSetupHelper;


public class SearchTaskscapeNotifier extends AbstractTaskscapeTest {

	private CompositeTaskscape taskscape; 
	private String source;
	
	public SearchTaskscapeNotifier(CompositeTaskscape taskscape, String source){
		this.taskscape = taskscape;
		this.source = source;
	}
	
	public ITaskscapeNode mockLowerInterest(ITaskscapeNode node) {
		return taskscape.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, -3));
    }
	
	public ITaskscapeNode mockRaiseInterest(ITaskscapeNode node) {
		return taskscape.addEvent(mockUserEvent(node.getElementHandle(), node.getStructureKind(), source, 2));
    }
	
	public ITaskscapeNode mockLowerInterest(String handle, String kind) {
		return mockLowerInterest(mockEditorSelection(handle, kind));
    }
	
	public ITaskscapeNode mockRaiseInterest(String handle, String kind) {
		return mockRaiseInterest(mockEditorSelection(handle, kind));
    }

	public ITaskscapeNode mockEditorSelection(String handle, String kind) {
		taskscape.addEvent(mockSelection(handle, kind, source));
		return taskscape.addEvent(mockSelection(handle, kind, source));
	}
	
	public ITaskscapeNode getElement(String handle, String kind) {
		ITaskscapeNode node = taskscape.addEvent(mockSelection(handle, kind, source));
		taskscape.addEvent(mockUserEvent(handle, kind, source, (1/TaskscapeManager.getScalingFactors().getLandmark()) * -2));
		return node;
	}
	
	public void clearTaskscape() throws IOException, CoreException{
		WorkspaceSetupHelper.clearDoiModel();
		Taskscape task = WorkspaceSetupHelper.getTaskscape();
    	MylarPlugin.getTaskscapeManager().taskActivated(task.getId(), task.getId());
    	taskscape = MylarPlugin.getTaskscapeManager().getActiveTaskscape();
	}
	
	private InteractionEvent mockSelection(String handle, String kind, String origin) {
        return new InteractionEvent(InteractionEvent.Kind.SELECTION, kind, handle, origin);
    }
	
	private InteractionEvent mockUserEvent(String handle, String kind, String origin, float scale) {
        InteractionEvent e = new InteractionEvent(InteractionEvent.Kind.MANIPULATION, kind, handle, origin, scale * TaskscapeManager.getScalingFactors().getLandmark());
        e.getInterestContribution();
        return e;
	}
}
