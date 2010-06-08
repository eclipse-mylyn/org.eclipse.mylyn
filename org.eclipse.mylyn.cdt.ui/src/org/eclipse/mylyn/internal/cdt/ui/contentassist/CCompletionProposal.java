/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui.contentassist;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shawn Minto
 */
public class CCompletionProposal extends org.eclipse.cdt.internal.ui.text.contentassist.CCompletionProposal {

	private final ICElement cElement;

	// only needed until we can actually get the ICElement
	private final String bindingName;

	public CCompletionProposal(String replacementString, int replacementOffset, int replacementLength, Image image,
			String displayString, String idString, int relevance, ITextViewer viewer, ICElement element,
			String bindingName) {
		super(replacementString, replacementOffset, replacementLength, image, displayString, idString, relevance,
				viewer);
		this.cElement = element;
		this.bindingName = bindingName;
	}

	public ICElement getCElement() {
		return cElement;
	}

	public String getBindingName() {
		return bindingName;
	}

}
