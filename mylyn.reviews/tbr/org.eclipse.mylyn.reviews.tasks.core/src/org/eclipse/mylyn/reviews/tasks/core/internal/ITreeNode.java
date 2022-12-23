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

import java.util.List;

import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.Rating;

/**
 * @author mattk
 *
 */
public interface ITreeNode {
	List<ITreeNode> getChildren();

	ITreeNode getParent();

	void setParent(ITreeNode parent);

	String getDescription();

	Rating getResult();

	String getTaskId();

	ITaskProperties getTask();

	String getPerson() ;
}