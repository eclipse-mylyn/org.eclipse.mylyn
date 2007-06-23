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

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;


/**
 * @author Rob Elves
 */
public class UnrecognizedReponseException extends IOException {

	private static final long serialVersionUID = 3937060773477757464L;

	public UnrecognizedReponseException(String message) {
		super(message);
	}
}
