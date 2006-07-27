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

package org.eclipse.mylar.trac.tests;

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.Trac09Client;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.model.TracVersion;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * @author Steffen Pingel
 */
public class Trac09ClientTest extends AbstractTracClientTest {

	public Trac09ClientTest() {
		super(new AbstractTracRepositoryFactory() {
			protected ITracClient createRepository(String url, String username, String password) throws Exception {
				return new Trac09Client(new URL(url), Version.TRAC_0_9, username, password);
			}
		});
	}

	public void testValidateAnonymousLogin() throws Exception {
		factory.connect(Constants.TEST_REPOSITORY1_URL, "", "");
		factory.repository.validate();
	}

	public void testUpdateAttributes() throws Exception {
		factory.connectRepository1();
		assertNull(factory.repository.getMilestones());
		factory.repository.updateAttributes(new NullProgressMonitor());
		TracVersion[] versions = factory.repository.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("v1", versions[0].getName());
		assertEquals("v2", versions[1].getName());
	}


}
