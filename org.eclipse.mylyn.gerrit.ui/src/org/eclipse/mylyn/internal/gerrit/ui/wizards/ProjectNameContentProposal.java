/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.wizards;

import org.eclipse.jface.fieldassist.IContentProposal;

/**
 * @author Sascha Scholz
 */
public class ProjectNameContentProposal implements IContentProposal {

	private final String projectName;

	public ProjectNameContentProposal(String projectName) {
		this.projectName = projectName;
	}

	@Override
	public String getContent() {
		return projectName;
	}

	@Override
	public int getCursorPosition() {
		return projectName.length();
	}

	@Override
	public String getLabel() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
