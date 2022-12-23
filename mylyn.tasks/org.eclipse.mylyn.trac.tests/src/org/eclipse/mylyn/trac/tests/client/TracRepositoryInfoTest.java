/*******************************************************************************
 * Copyright (c) 2009 Steffen Pingel and others. 
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

package org.eclipse.mylyn.trac.tests.client;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.trac.core.model.TracRepositoryInfo;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryInfoTest extends TestCase {

	public void testIsApiVersion() {
		TracRepositoryInfo info = new TracRepositoryInfo(1, 1, 1);
		assertTrue(info.isApiVersion(1, 1, 1));
		assertFalse(info.isApiVersion(1, 1, 0));
		assertFalse(info.isApiVersion(1, 0, 1));
		assertFalse(info.isApiVersion(0, 1, 1));
		assertFalse(info.isApiVersion(-1, -1, -1));
	}

	public void testIsApiVersionOrHigher() {
		TracRepositoryInfo info = new TracRepositoryInfo(1, 2, 3);
		assertTrue(info.isApiVersionOrHigher(1, 2, 3));
		assertTrue(info.isApiVersionOrHigher(0, 1, 3));
		assertTrue(info.isApiVersionOrHigher(1, 2, -3));
		assertFalse(info.isApiVersionOrHigher(1, 2, 4));
		assertFalse(info.isApiVersionOrHigher(2, 3, 2));
	}

	public void testIsApiVersionOrSmaller() {
		TracRepositoryInfo info = new TracRepositoryInfo(1, 2, 3);
		assertTrue(info.isApiVersionOrSmaller(1, 2, 3));
		assertTrue(info.isApiVersionOrSmaller(2, 1, 3));
		assertTrue(info.isApiVersionOrSmaller(1, 3, -3));
		assertFalse(info.isApiVersionOrSmaller(1, 2, 2));
		assertFalse(info.isApiVersionOrSmaller(0, 3, 2));
	}

}
