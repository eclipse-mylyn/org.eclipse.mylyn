/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class PersonContentProposal implements IContentProposal, Comparable<PersonContentProposal> {

	private final String address;

	private final boolean isCurrentUser;

	private final String replacementText;

	private final int cursorPosition;

	public PersonContentProposal(String address, boolean isCurrentUser, String replacementText, int cursorPosition) {
		Assert.isNotNull(address);
		Assert.isNotNull(replacementText);
		this.address = address;
		this.isCurrentUser = isCurrentUser;
		this.replacementText = replacementText;
		this.cursorPosition = cursorPosition;
	}

	public PersonContentProposal(String address, boolean isCurrentUser) {
		this(address, isCurrentUser, address, address.length());
	}

	public String getLabel() {
		return address;
	}

	public String getDescription() {
		return null;
	}

	public int getCursorPosition() {
		return cursorPosition;
	}

	public String getContent() {
		return replacementText;
	}

	public Image getImage() {
		if (isCurrentUser) {
			return CommonImages.getImage(CommonImages.PERSON_ME);
		} else {
			return CommonImages.getImage(CommonImages.PERSON);
		}
	}

	public int compareTo(PersonContentProposal otherContentProposal) {
		if (isCurrentUser) {
			return -1;
		} else if (otherContentProposal.isCurrentUser) {
			return 1;
		}
		return address.compareToIgnoreCase(otherContentProposal.address);
	}

	public boolean isCurrentUser() {
		return isCurrentUser;
	}

}
