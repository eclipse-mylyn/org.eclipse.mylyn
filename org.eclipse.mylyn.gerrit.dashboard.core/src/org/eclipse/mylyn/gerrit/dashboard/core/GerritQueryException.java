/*******************************************************************************
 * Copyright (c) 2013, 2014 Ericsson
 * 
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * Contributors:
 *   Francois Chouinard - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.core;

import org.eclipse.core.runtime.IStatus;

/**
 * An Gerrit Dashboard exception. Same as a QueryException but with an IStatus.
 * 
 * @author Francois Chouinard
 * @version 0.1
 */
public class GerritQueryException extends Exception {

	private static final long serialVersionUID = 1L;

	private final IStatus fStatus;

	public GerritQueryException(IStatus status, String message) {
		super(message);
		fStatus = status;
	}

	public IStatus getStatus() {
		return fStatus;
	}

}
