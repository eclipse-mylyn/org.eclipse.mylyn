/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs;

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
		return attributes;
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
