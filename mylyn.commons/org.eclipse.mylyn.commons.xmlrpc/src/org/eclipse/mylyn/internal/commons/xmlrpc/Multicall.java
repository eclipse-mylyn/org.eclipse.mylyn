/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.xmlrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Represents a multicall.
 * 
 * @author Steffen Pingel
 */
public class Multicall {

	List<Map<String, Object>> calls = new ArrayList<>();

	public Multicall() {
	}

	public Multicall add(String methodName, Object... parameters) throws XmlRpcException {
		Map<String, Object> table = new HashMap<>();
		table.put("methodName", methodName); //$NON-NLS-1$
		table.put("params", parameters); //$NON-NLS-1$
		calls.add(table);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object>[] getCalls() {
		return calls.toArray(new Map[0]);
	}

}
