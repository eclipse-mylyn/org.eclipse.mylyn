/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Eike Stepper - initial API and implementation
 *     Tasktop Technologies - improvements
 *******************************************************************************/

package org.eclipse.mylyn.team.ui;

import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Eike Stepper
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractCommitTemplateVariable {

	protected String description;

	protected String recognizedKeyword;

	/**
	 * @since 3.10
	 */
	protected String[] arguments;

	public String getDescription() {
		return description != null ? description : "Handler for '" + recognizedKeyword + "'"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRecognizedKeyword() {
		return recognizedKeyword;
	}

	/**
	 * @since 3.0
	 */
	public abstract String getValue(ITask task);

	public void setRecognizedKeyword(String recognizedKeyword) {
		if (recognizedKeyword == null) {
			throw new IllegalArgumentException("Keyword to recognize must not be null"); //$NON-NLS-1$
		}

		this.recognizedKeyword = recognizedKeyword;
	}

	/**
	 * @since 3.10
	 */
	public void setArguments(String[] arguments) {
		this.arguments = arguments;
	}
}