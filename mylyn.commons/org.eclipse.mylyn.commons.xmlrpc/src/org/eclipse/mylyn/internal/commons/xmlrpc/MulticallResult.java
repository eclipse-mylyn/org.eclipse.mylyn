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
import java.util.List;

/**
 * @author Steffen Pingel
 */
public class MulticallResult {

	private final Object[] response;

	public MulticallResult(Object[] response) {
		this.response = response;
	}

	public List<Object> getItems() {
		return getItems(Object.class);
	}

	public <T> List<T> getItems(Class<T> itemClass) {
		List<T> items = new ArrayList<>(response.length);
		for (Object element : response) {
			items.add(itemClass.cast(getMultiCallResult(element)));
		}
		return items;
	}

	private Object getMultiCallResult(Object item) {
		return ((Object[]) item)[0];
	}

}
