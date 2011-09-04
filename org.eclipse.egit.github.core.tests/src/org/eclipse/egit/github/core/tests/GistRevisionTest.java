/*******************************************************************************
 *  Copyright (c) 2011 Christian Trutz
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Christian Trutz - initial API and implementation
 *******************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.GistRevision;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests of {@link GistRevision}
 */
@RunWith(MockitoJUnitRunner.class)
public class GistRevisionTest {

	/**
	 * Test non-mutable committed at date
	 */
	@Test
	public void getCreatedAReferenceMutableObject() {
		GistRevision gistRevision = new GistRevision();
		gistRevision.setCommittedAt(new Date(10000));
		gistRevision.getCommittedAt().setTime(0);
		assertTrue(gistRevision.getCommittedAt().getTime() != 0);
	}
}
