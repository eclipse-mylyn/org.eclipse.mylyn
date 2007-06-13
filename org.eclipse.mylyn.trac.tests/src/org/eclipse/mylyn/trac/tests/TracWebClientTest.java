/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.TracException;
import org.eclipse.mylyn.internal.trac.core.ITracClient.Version;
import org.eclipse.mylyn.internal.trac.core.model.TracVersion;

/**
 * @author Steffen Pingel
 */
public class TracWebClientTest extends AbstractTracClientRepositoryTest {

	public TracWebClientTest() {
		super(Version.TRAC_0_9);
	}

	public void testValidate096() throws Exception {
		validate(TracTestConstants.TEST_TRAC_096_URL);
	}

	public void testValidate011() throws Exception {
		try {
			validate(TracTestConstants.TEST_TRAC_011_URL);
		} catch (TracException e) {
		}
	}

	public void testValidateAnyPage() throws Exception {
		connect("http://mylyn.eclipse.org/");
		try {
			repository.validate();
			fail("Expected TracException");
		} catch (TracException e) {
		}
	}

	public void testValidateAnonymousLogin() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		repository.validate();
		
		connect(TracTestConstants.TEST_TRAC_096_URL, "", "");
		repository.validate();
	}

	public void testUpdateAttributesAnonymous096() throws Exception {
		connect(TracTestConstants.TEST_TRAC_096_URL, "", "");
		updateAttributes();
	}
	
	public void testUpdateAttributesAnonymous010() throws Exception {
		connect(TracTestConstants.TEST_TRAC_010_URL, "", "");
		updateAttributes();
	}

	private void updateAttributes() throws TracException {
		assertNull(repository.getMilestones());
		repository.updateAttributes(new NullProgressMonitor(), true);
		TracVersion[] versions = repository.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("2.0", versions[1].getName());
	}

}
