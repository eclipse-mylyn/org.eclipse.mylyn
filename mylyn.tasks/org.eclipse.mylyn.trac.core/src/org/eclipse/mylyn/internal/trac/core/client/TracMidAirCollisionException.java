/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core.client;

/**
 * Indicates that an edit conflict has occurred due to a ticket changing in the repository since it was last updated.
 * 
 * @author Steffen Pingel
 */
public class TracMidAirCollisionException extends TracRemoteException {

	private static final long serialVersionUID = -5505542112062812372L;

	public TracMidAirCollisionException() {
	}

	public TracMidAirCollisionException(String message) {
		super(message);
	}

	public TracMidAirCollisionException(Throwable cause) {
		super(cause);
	}

	public TracMidAirCollisionException(String message, Throwable cause) {
		super(message, cause);
	}

}
