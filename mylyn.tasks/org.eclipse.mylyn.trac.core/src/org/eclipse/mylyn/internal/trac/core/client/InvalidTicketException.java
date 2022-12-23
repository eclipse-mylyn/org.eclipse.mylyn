/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
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
