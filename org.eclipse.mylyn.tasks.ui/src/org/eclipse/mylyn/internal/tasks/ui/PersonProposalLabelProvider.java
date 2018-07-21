/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Shawn Minto
 */
public class PersonProposalLabelProvider extends LabelProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof PersonContentProposal) {
			return ((PersonContentProposal) element).getImage();
		}
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PersonContentProposal) {
			return ((PersonContentProposal) element).getLabel();
		}
		return super.getText(element);
	}
}
