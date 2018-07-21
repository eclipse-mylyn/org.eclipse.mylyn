/*******************************************************************************
 * Copyright (c) 2010 Red Hat Inc. and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat Inc. - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.Serializable;

import org.eclipse.mylyn.tasks.core.data.TaskAttribute;

/**
 * @author Charley Wang
 */
public abstract class AbstractBugzillaOperation implements Serializable {

	private static final long serialVersionUID = 6966730655963098650L;

	public static final String DEFAULT_LABEL_PREFIX = "Set as "; //$NON-NLS-1$

	private final String label;

	private final String inputId;

	private final String inputType;

	AbstractBugzillaOperation(String label) {
		this(label, null, TaskAttribute.TYPE_SHORT_TEXT);
	}

	AbstractBugzillaOperation(String label, String inputId, String type) {
		this.label = label;
		this.inputId = inputId;
		this.inputType = type;
	}

	public String getLabel() {
		return label;
	}

	public String getInputId() {
		return inputId;
	}

	public String getInputType() {
		return inputType;
	}

	@Override
	/**
	 * Return the bug_status string to be sent to the server.
	 * 
	 */
	public abstract String toString();
}
