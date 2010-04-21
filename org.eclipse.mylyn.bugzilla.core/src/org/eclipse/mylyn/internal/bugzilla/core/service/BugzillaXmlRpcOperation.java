/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
