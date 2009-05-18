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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;

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
