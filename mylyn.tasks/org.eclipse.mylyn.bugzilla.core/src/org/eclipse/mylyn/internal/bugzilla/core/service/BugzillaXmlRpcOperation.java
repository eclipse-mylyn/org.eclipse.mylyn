/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.service;

import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.internal.commons.xmlrpc.XmlRpcOperation;

@SuppressWarnings("restriction")
public abstract class BugzillaXmlRpcOperation<T> extends XmlRpcOperation<T> {

	public BugzillaXmlRpcOperation(CommonXmlRpcClient client) {
		super(client);
		// ignore
	}

}
