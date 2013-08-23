/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;

public class RepositoryConfigurationTest extends TestCase {

	private final static String PRODUCT = "product";

	RepositoryConfiguration cfg;

	@Override
	protected void setUp() throws Exception {
		cfg = new RepositoryConfiguration();
		cfg.addProduct(PRODUCT);
	}

	public void testGetUnconfirmedAllowed_product() throws Exception {
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	public void testGetUnconfirmedAllowed_productFalse() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, Boolean.FALSE);
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	public void testGetUnconfirmedAllowed_productNull() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, null);
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	public void testGetUnconfirmedAllowed_productTrue() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, Boolean.TRUE);
		assertTrue(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	public void testGetUnconfirmedAllowed_noProduct() throws Exception {
		assertFalse(cfg.getUnconfirmedAllowed("no-product"));
	}
}
