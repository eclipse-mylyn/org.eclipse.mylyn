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

package org.eclipse.mylar.internal.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.mylar.provisional.core.AbstractRelationProvider;
import org.eclipse.mylar.provisional.core.IDegreeOfSeparation;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.ITaskListElement;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;

/**
 * @author Mik Kersten
 */
public class TaskStructureBridge implements IMylarStructureBridge {

	public static final String CONTENT_TYPE = "meta/task";
	
	public void setParentBridge(IMylarStructureBridge bridge) {
		// ignore
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public String getHandleIdentifier(Object object) {
		if (object instanceof ITask) {
			return ((ITask)object).getHandleIdentifier();
		} else {
			return null;
		}
	}

	public String getParentHandle(String handle) {
		return null;
	}

	public Object getObjectForHandle(String handle) {
		return MylarTaskListPlugin.getTaskListManager().getTaskList().getTask(handle);
	}

	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}

	public String getName(Object object) {
		if (object instanceof ITask) {
			return ((ITask)object).getDescription();
		} else {
			return null;
		}
	}

	public boolean canBeLandmark(String handle) {
		return false;
	}

	public boolean acceptsObject(Object object) {
		return object instanceof ITaskListElement;
	}

	public boolean canFilter(Object object) {
		return object instanceof ITask || object instanceof AbstractQueryHit;
	}

	public boolean isDocument(String handle) {
		return getObjectForHandle(handle) instanceof ITask;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	public String getContentType(String elementHandle) {
		return getContentType();
	}

	public List<AbstractRelationProvider> getRelationshipProviders() {
		return null;
	}

	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		return null;
	}

}
