/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist;

/**
 * @author Mik Kersten and Ken Sueda
 */
public class TaskListExternalizerException extends Exception {

	private static final long serialVersionUID = 5804522104992031907L;

	public TaskListExternalizerException() {
		super();
	}

	public TaskListExternalizerException(String detailMessage) {
		super(detailMessage);
	}
}
