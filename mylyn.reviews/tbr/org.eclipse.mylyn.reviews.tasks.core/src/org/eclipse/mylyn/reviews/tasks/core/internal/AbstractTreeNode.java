/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;

/**
 * @author Kilian Matt
 *
 */
public abstract class AbstractTreeNode implements ITreeNode {
	private ITreeNode parent;
	private List<ITreeNode> children=new ArrayList<ITreeNode>();
	private ITaskProperties task;

	protected AbstractTreeNode(ITaskProperties task) {
		this.task=task;
	}

	public List<ITreeNode> getChildren() {
		return Collections.unmodifiableList(children);
	}

	public ITreeNode getParent() {
		return parent;
	}

	public void addChildren(ITreeNode child) {
		this.children.add(child);
		child.setParent(this);
	}

	public void setParent(ITreeNode parent) {
		this.parent = parent;
	}

	public String getTaskId() {
		return task!=null? task.getTaskId():null;
	}

	public ITaskProperties getTask() {
		return task;
	}
}
