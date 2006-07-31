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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.internal.trac.core.TracRemoteException;
import org.eclipse.mylar.internal.trac.core.TracXmlRpcClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.model.TracVersion;

/**
 * @author Steffen Pingel
 */
public class TracXmlRpcClientTest extends AbstractTracClientRepositoryTest {

	public TracXmlRpcClientTest() {
		super(Version.XML_RPC);
	}

	public void testValidateFailNoAuth() throws Exception {
		connect(Constants.TEST_TRAC_010_URL, "", "");
		try {
			repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testMulticallExceptions() throws Exception {
		connect010();
		try {
			((TracXmlRpcClient) repository).getTickets(new int[] { 1, Integer.MAX_VALUE });
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

	public void testUpdateAttributes() throws Exception {
		connect010();
		assertNull(repository.getMilestones());
		repository.updateAttributes(new NullProgressMonitor());
		TracVersion[] versions = repository.getVersions();
		assertEquals(2, versions.length);
		Arrays.sort(versions, new Comparator<TracVersion>() {
			public int compare(TracVersion o1, TracVersion o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		assertEquals("1.0", versions[0].getName());
		assertEquals("", versions[0].getDescription());
		assertEquals(new Date(0), versions[0].getTime());
		assertEquals("2.0", versions[1].getName());
		assertEquals("", versions[1].getDescription());
		assertEquals(new Date(0), versions[1].getTime());
	}

}
