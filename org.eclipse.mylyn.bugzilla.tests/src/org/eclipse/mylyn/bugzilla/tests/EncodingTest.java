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

package org.eclipse.mylar.bugzilla.tests;

import java.io.IOException;
import java.text.ParseException;

import javax.security.auth.login.LoginException;

import junit.framework.TestCase;

import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;

/**
 * @author Mik Kersten
 */
public class EncodingTest extends TestCase {

	public void testEncodingSetting() throws LoginException, IOException, ParseException {

		String charset = BugzillaRepositoryUtil.getCharsetFromString("text/html; charset=UTF-8");
		assertEquals("UTF-8", charset);

		charset = BugzillaRepositoryUtil.getCharsetFromString("text/html");
		assertEquals(null, charset);

		charset = BugzillaRepositoryUtil
				.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-2\">>");
		assertEquals("iso-8859-2", charset);

		charset = BugzillaRepositoryUtil.getCharsetFromString("<<meta http-equiv=\"Content-Type\" content=\"text/html\">>");
		assertEquals(null, charset);
	}

}
