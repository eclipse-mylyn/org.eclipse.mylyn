/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Utility to create {@link XMLReader} instances. Uses Xerces if available to ensure XML 1.1 parsing works correctly.
 *
 * @author Steffen Pingel
 */
public class XmlReaderUtil {

	public static XMLReader createXmlReader() throws SAXException {
		// use Xerces to ensure XML 1.1 is handled correctly
		return new SAXParser();
	}

	private XmlReaderUtil() {
		// clients must not instantiate class
	}

}
