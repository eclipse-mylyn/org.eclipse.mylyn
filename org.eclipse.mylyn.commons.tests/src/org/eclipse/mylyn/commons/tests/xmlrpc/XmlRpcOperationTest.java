/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.tests.xmlrpc;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.commons.net.WebLocation;
import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcOperation;

/**
 * @author Steffen Pingel
 */
public class XmlRpcOperationTest extends TestCase {

	private CommonXmlRpcClient client;

	private int port;

	@Override
	protected void setUp() throws Exception {
		port = XmlRpcTestServer.start();
		client = new CommonXmlRpcClient(new WebLocation("http://localhost:" + port + "/xmlrpc"));
	}

	public void testExecute() throws Exception {
		Integer response = (new XmlRpcOperation<Integer>(client) {
			@Override
			public Integer execute() throws XmlRpcException {
				return (Integer) call(new NullProgressMonitor(), "Test.identity", 5);
			}
		}).execute();
		assertEquals(5, (int) response);
	}

//	public void testExecuteMulticall() throws Exception {
//		MulticallResult result = (new XmlRpcOperation<MulticallResult>(client) {
//			@Override
//			public MulticallResult execute() throws XmlRpcException {
//				Multicall call = new Multicall();
//				call.add("Test.identity", 1);
//				call.add("Test.identity", 5);
//				return call(new NullProgressMonitor(), call);
//			}
//		}).execute();
//		List<Integer> response = result.getItems(Integer.class);
//		assertEquals(Arrays.asList(1, 5), response);
//	}

}
