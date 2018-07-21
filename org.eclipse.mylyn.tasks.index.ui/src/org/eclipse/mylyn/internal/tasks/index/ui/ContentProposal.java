/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.index.ui;

import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * @author David Green
 */
class ContentProposal implements IContentProposal {

	private final String content;

	private final int cursorPosition;

	private final String label;

	private final String description;

	public ContentProposal(String content, String label, String description) {
		this(content, label, description, content.length());
	}

	public ContentProposal(String content, String contentSuffix, String label, String description) {
		this(content + contentSuffix, label, description, content.length());
	}

	public ContentProposal(String content, String label, String description, int cursorPosition) {
		this.content = content;
		this.cursorPosition = cursorPosition;
		this.label = label;
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

}
