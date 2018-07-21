/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.compare;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.compare.IStreamContentAccessor;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.data.TaskAttributeDiff;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataDiff;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.mylyn.tasks.core.data.ITaskAttributeDiff;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.swt.graphics.Image;

/**
 * A model element for comparing two {@link TaskData} objects.
 * 
 * @author Steffen Pingel
 */
public class TaskDataDiffNode extends DiffNode {

	static class ByteArrayInput implements ITypedElement, IStreamContentAccessor {

		String content;

		private final String name;

		public ByteArrayInput(String content, String name) {
			this.content = content;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public Image getImage() {
			return null;
		}

		public String getType() {
			return ITypedElement.TEXT_TYPE;
		}

		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream(content.getBytes());
		}

	}

	public TaskDataDiffNode(int change, TaskData oldData, TaskData newData) {
		super(change);
		TaskDataDiff diff = new TaskDataDiff(TasksUiPlugin.getRepositoryModel(), newData, oldData);
		for (ITaskAttributeDiff attribute : diff.getChangedAttributes()) {
			TaskAttributeDiff attr = (TaskAttributeDiff) attribute;
			String label = attr.getLabel();
			if (label.endsWith(":")) { //$NON-NLS-1$
				label = label.substring(0, label.length() - 1);
			}
			DiffNode node = new DiffNode(Differencer.CHANGE, this, new ByteArrayInput(attr.getOldValues().toString(),
					null), new ByteArrayInput(attr.getNewValues().toString(), label));
			add(node);
		}
		for (ITaskComment attribute : diff.getNewComments()) {
			DiffNode node = new DiffNode(Differencer.CHANGE, this, new ByteArrayInput("", null), new ByteArrayInput( //$NON-NLS-1$
					attribute.getText(), Messages.TaskDataDiffNode_New_Comment_Label));
			add(node);
		}
	}

}
