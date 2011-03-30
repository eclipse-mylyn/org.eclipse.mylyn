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

import org.eclipse.mylyn.reviews.tasks.core.Attachment;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
/**
 * 
 * @author mattk
 *
 */
public class TaskNode extends AbstractTreeNode {

	public TaskNode(ITaskProperties task) {
		super(task);
	}

	public String getDescription() {
		int patchCount=0;
		int otherCount=0;
		for(Attachment att : getTask().getAttachments()) {
			if(att.isPatch()) {
				patchCount++;
			} else {
				otherCount++;
			}
		}
		StringBuilder sb = new StringBuilder();
		if(patchCount >0) {
			sb.append(patchCount);
			sb.append(" patch");
			if(patchCount>1) {
				sb.append("es");
			}
		}
		if(patchCount>0&& otherCount>0) {
			sb.append(", ");
		}
		if(otherCount>0) {
			sb.append(otherCount);
			sb.append(" other attachment");
			if(otherCount>1) {
				sb.append("s");
			}
		}
		return sb.toString();
	}

	public Rating getResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getPerson() {
		//TODO
		return "FIXME";
	}
	public boolean hasReviewSubTasks() {
		for (ITreeNode node :getChildren())  {
			if(node instanceof ReviewScopeNode)
				return true;
			else if (node instanceof TaskNode) 
				if(((TaskNode)node).hasReviewSubTasks())
					return true;
		}
		return false;
	}

}