/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.ui;

public class E4CssParseException extends Exception {
	private static final long serialVersionUID = 6799939105221602854L;

	public E4CssParseException(String type, String value) {
		super("Cannot parse " + type + " value from :" + value); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
