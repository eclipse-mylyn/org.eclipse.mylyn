/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.tasks.bugs.IProduct;
import org.eclipse.mylyn.tasks.bugs.IProvider;

/**
 * @author Steffen Pingel
 */
public class SupportProvider extends AbstractSupportElement implements IProvider {

	private SupportCategory category;

	private List<IProduct> products;

	public SupportProvider() {
	}

	public SupportCategory getCategory() {
		return category;
	}

	public void setCategory(SupportCategory category) {
		this.category = category;
	}

	public void add(IProduct provider) {
		if (products == null) {
			products = new ArrayList<IProduct>();
		}
		products.add(provider);
	}

	public void remove(IProduct provider) {
		if (products != null) {
			products.remove(provider);
		}
	}

	public List<IProduct> getProducts() {
		return new ArrayList<IProduct>(products);
	}

}
