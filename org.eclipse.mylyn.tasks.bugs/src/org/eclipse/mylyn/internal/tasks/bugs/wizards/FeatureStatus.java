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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProduct;

/**
 * @author Steffen Pingel
 */
public class FeatureStatus extends Status {

	private final IProduct product;

	public FeatureStatus(IProduct product) {
		super(IStatus.INFO, product.getId(), ""); //$NON-NLS-1$
		this.product = product;
	}

	public IProduct getProduct() {
		return product;
	}

}
