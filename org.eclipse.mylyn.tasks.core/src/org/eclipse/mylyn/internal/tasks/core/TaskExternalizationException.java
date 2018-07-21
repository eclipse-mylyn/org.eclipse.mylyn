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
 *     Ken Sueda - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * @author Mik Kersten
 * @author Ken Sueda
 */
public class TaskExternalizationException extends Exception {

	private static final long serialVersionUID = 5804522104992031907L;

	public TaskExternalizationException() {
		super();
	}

	public TaskExternalizationException(String detailMessage) {
		super(detailMessage);
	}
}
