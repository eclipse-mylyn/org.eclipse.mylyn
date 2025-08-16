/*******************************************************************************
 * Copyright (c) 2011, 2012 SAP and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
