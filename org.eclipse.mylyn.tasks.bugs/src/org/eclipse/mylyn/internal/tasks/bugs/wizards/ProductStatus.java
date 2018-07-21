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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.tasks.bugs.IProduct;

/**
 * @author Steffen Pingel
 */
public class ProductStatus extends Status {

	private final IProduct product;

	public ProductStatus(IProduct product) {
		super(IStatus.INFO, product.getId(), ""); //$NON-NLS-1$
		this.product = product;
	}

	public IProduct getProduct() {
		return product;
	}

}
