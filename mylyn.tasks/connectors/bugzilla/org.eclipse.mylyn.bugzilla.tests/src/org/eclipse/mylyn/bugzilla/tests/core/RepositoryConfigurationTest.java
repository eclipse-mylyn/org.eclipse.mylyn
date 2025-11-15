/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttributeMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("nls")
public class RepositoryConfigurationTest {

	private final static String PRODUCT = "product";

	RepositoryConfiguration cfg;

	@BeforeEach
	void setUp() throws Exception {
		cfg = new RepositoryConfiguration();
		cfg.addProduct(PRODUCT);
	}

	@Test
	public void testGetUnconfirmedAllowed_product() throws Exception {
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	@Test
	public void testGetUnconfirmedAllowed_productFalse() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, Boolean.FALSE);
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	@Test
	public void testGetUnconfirmedAllowed_productNull() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, null);
		assertFalse(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	@Test
	public void testGetUnconfirmedAllowed_productTrue() throws Exception {
		cfg.addUnconfirmedAllowed(PRODUCT, Boolean.TRUE);
		assertTrue(cfg.getUnconfirmedAllowed(PRODUCT));
	}

	@Test
	public void testGetUnconfirmedAllowed_noProduct() throws Exception {
		assertFalse(cfg.getUnconfirmedAllowed("no-product"));
	}

	@Test
	public void testGetAttributeOptions() throws Exception {
		TaskRepository repository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, "http://repository");
		BugzillaAttributeMapper mapper = new BugzillaAttributeMapper(repository, new BugzillaRepositoryConnector());
		TaskData taskData = new TaskData(mapper, repository.getConnectorKind(), repository.getRepositoryUrl(), "");

		cfg.addItem(BugzillaAttribute.REP_PLATFORM, "3");
		cfg.addItem(BugzillaAttribute.REP_PLATFORM, "2");
		cfg.addItem(BugzillaAttribute.REP_PLATFORM, "1");
		List<String> options = cfg.getAttributeOptions(PRODUCT,
				taskData.getRoot().createAttribute(BugzillaAttribute.REP_PLATFORM.getKey()));
		assertEquals(3, options.size());
		assertEquals("1", options.get(0));
		assertEquals("2", options.get(1));
		assertEquals("3", options.get(2));
	}
}
