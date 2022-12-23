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
 * Indicates insufficient permissions to execute an operation.
 * 
 * @author Steffen Pingel
 */
public class TracPermissionDeniedException extends TracException {

	private static final long serialVersionUID = -6128773690643367414L;

	public TracPermissionDeniedException() {
	}

	public TracPermissionDeniedException(String message) {
		super(message);
	}

}
