/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.mylyn.commons.ui.CommonImages;
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

	@Override
	public String getLabel() {
		return address;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public int getCursorPosition() {
		return cursorPosition;
	}

	@Override
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

	@Override
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
