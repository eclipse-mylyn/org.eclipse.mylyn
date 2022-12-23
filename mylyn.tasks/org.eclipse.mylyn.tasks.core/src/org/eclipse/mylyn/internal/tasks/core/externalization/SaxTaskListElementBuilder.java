/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.externalization;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.xml.sax.Attributes;

public abstract class SaxTaskListElementBuilder<T extends IRepositoryElement> {

	private String currentAttributeKey;

	private StringBuilder currentAttributeValue;

	private final MultiStatus errors;

	protected SaxTaskListElementBuilder() {
		this.errors = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, IStatus.OK, null, null);
	}

	public abstract void beginItem(Attributes elementAttributes);

	protected abstract void applyAttribute(String attributeKey, String attributeValue);

	public abstract T getItem();

	public abstract void addToTaskList(ITransferList taskList);

	public void startAttribute(Attributes elementAttributes) {
		currentAttributeKey = elementAttributes.getValue(TaskListExternalizationConstants.KEY_KEY);
		currentAttributeValue = new StringBuilder();
	}

	public void acceptAttributeValueContent(char[] content, int start, int length) {
		if (isAcceptingAttributeValues()) {
			currentAttributeValue.append(content, start, length);
		}
	}

	public void endAttribute() {
		if (isAcceptingAttributeValues()) {
			applyAttribute(currentAttributeKey, currentAttributeValue.toString());
		}
		currentAttributeKey = null;
		currentAttributeValue = null;
	}

	public boolean isAcceptingAttributeValues() {
		return currentAttributeKey != null && currentAttributeValue != null;
	}

	protected void addError(IStatus status) {
		errors.add(status);
	}

	public IStatus getErrors() {
		return errors;
	}

}
