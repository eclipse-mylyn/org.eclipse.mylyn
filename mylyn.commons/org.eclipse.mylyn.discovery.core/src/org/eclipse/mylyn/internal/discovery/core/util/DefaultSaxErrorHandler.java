/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A default implementation of an error handler that throws exceptions on all errors.
 * 
 * @author David Green
 */
public class DefaultSaxErrorHandler implements ErrorHandler {
	public void warning(SAXParseException exception) throws SAXException {
		// ignore
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		throw exception;
	}

	public void error(SAXParseException exception) throws SAXException {
		throw exception;
	}
}