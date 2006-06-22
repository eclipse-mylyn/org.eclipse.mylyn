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

import org.eclipse.mylar.internal.trac.core.ITracRepository;
import org.eclipse.mylar.internal.trac.core.TracLoginException;
import org.eclipse.mylar.internal.trac.core.TracRemoteException;
import org.eclipse.mylar.internal.trac.core.TracXmlRpcRepository;
import org.eclipse.mylar.internal.trac.core.ITracRepository.Version;
import org.eclipse.mylar.trac.tests.support.AbstractTracRepositoryFactory;

/**
 * @author Steffen Pingel
 */
public class TracXmlRpcRepositoryTest extends AbstractTracRepositoryTest {

	public TracXmlRpcRepositoryTest() {
		super(new AbstractTracRepositoryFactory() {
			protected ITracRepository createRepository(String url, String username, String password) throws Exception {
				return new TracXmlRpcRepository(new URL(url), Version.XML_RPC, username, password);
			}
		});
	}

	public void testValidateFailNoAuth() throws Exception {
		factory.connect(Constants.TEST_REPOSITORY1_URL, "", "");
		try {
			factory.repository.validate();
			fail("Expected TracLoginException");
		} catch (TracLoginException e) {
		}
	}

	public void testMulticallExceptions() throws Exception {
		factory.connectRepository1();
		try {
			((TracXmlRpcRepository) factory.repository).getTickets(new int[] { 1, Integer.MAX_VALUE });
			fail("Expected TracRemoteException");
		} catch (TracRemoteException e) {
		}
	}

}
