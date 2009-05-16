/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Steffen Pingel
 */
public class ProductRepositoryMapping {

	private final Map<String, String> attributes;

	private final String namespace;

//	private IProduct product;

	public ProductRepositoryMapping(String namespace) {
		this.namespace = namespace;
		this.attributes = new HashMap<String, String>();
	}

	public void addAttributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}

	public Map<String, String> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}

	public String getNamespace() {
		return namespace;
	}

//	public IProduct getProduct() {
//		return product;
//	}
//
//	public void setProduct(IProduct product) {
//		this.product = product;
//	}

}
