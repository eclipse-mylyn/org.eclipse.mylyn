/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
