/*******************************************************************************
 * Copyright (c) 2010 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
		List<T> items = new ArrayList<T>(response.length);
		for (Object element : response) {
			items.add(itemClass.cast(getMultiCallResult(element)));
		}
		return items;
	}

	private Object getMultiCallResult(Object item) {
		return ((Object[]) item)[0];
	}

}
