/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
