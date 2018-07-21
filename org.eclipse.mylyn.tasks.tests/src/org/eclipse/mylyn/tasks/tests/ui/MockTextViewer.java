/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.tests.ui;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;

/**
 * @author Steffen Pingel
 */
public class MockTextViewer extends TextViewer {

	private String text;

	public MockTextViewer() {
	}

	public MockTextViewer(String text) {
		this.text = text;
	}

	@Override
	public IDocument getDocument() {
		return new Document(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
