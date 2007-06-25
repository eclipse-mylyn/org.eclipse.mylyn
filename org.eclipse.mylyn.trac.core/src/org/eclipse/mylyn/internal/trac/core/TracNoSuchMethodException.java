/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import org.apache.xmlrpc.XmlRpcException;

public class TracNoSuchMethodException extends TracException {

	private static final long serialVersionUID = 9075003728286406705L;

	public TracNoSuchMethodException(XmlRpcException e) {
		super(e);
	}

}
