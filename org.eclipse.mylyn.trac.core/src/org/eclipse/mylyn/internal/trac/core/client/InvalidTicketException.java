/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

/**
 * Indicates an error while parsing a ticket retrieved from a repository.
 * 
 * @author Steffen Pingel
 */
public class InvalidTicketException extends TracException {

	private static final long serialVersionUID = 7716941243394876876L;

	public InvalidTicketException(String message) {
		super(message);
	}

	public InvalidTicketException() {
	}

}
