/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.swt.graphics.Image;

/**
 * @author Mik Kersten
 */
public class PersonContentProposal implements IContentProposal {

	private final String address;

	private boolean isCurrentUser = false;

	public PersonContentProposal(String address, boolean isCurrentUser) {
		this.address = address;
		this.isCurrentUser = isCurrentUser;
	}

	public String getLabel() {
		return address;
	}

	public String getDescription() {
		return null;
	}

	public int getCursorPosition() {
		return address.length();
	}

	public String getContent() {
		return address;
	}

	public Image getImage() {
		if (isCurrentUser) {
			return TasksUiImages.getImage(TasksUiImages.PERSON_ME);
		} else {
			return TasksUiImages.getImage(TasksUiImages.PERSON);
		}
	}

}
