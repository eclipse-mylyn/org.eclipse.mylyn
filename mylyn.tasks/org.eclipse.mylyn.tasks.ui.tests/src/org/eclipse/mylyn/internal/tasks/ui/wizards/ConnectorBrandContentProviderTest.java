/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.ui.BrandManager;
import org.eclipse.mylyn.internal.tasks.ui.ConnectorBrand;
import org.eclipse.mylyn.internal.tasks.ui.wizards.SelectRepositoryConnectorPage.ConnectorBrandContentProvider;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.junit.Test;

public class ConnectorBrandContentProviderTest {

	@Test
	public void testGetElementsNoConnectors() {
		Collection<MockRepositoryConnector> connectors = List.of();
		BrandManager brandManager = mock(BrandManager.class);
		ConnectorBrandContentProvider provider = new ConnectorBrandContentProvider(brandManager, connectors);

		assertEquals(0, provider.getElements(null).length);
	}

	@Test
	public void testGetElementsMultipleConnectors() {
		MockRepositoryConnector cantCreateRepository = mockConnector("cantCreateRepository");
		when(cantCreateRepository.canCreateRepository()).thenReturn(false);
		MockRepositoryConnector notUserManaged = mockConnector("notUserManaged");
		when(notUserManaged.isUserManaged()).thenReturn(false);
		MockRepositoryConnector c1 = mockConnector("c1");
		MockRepositoryConnector c2 = mockConnector("c2");
		MockRepositoryConnector c3 = mockConnector("c3");
		Collection<MockRepositoryConnector> connectors = List.of(c1, c2, notUserManaged, cantCreateRepository, c3);
		BrandManager brandManager = mock(BrandManager.class);
		ConnectorBrandContentProvider provider = new ConnectorBrandContentProvider(brandManager, connectors);

		assertBrands(provider, new ConnectorBrand(c1, null), new ConnectorBrand(c2, null),
				new ConnectorBrand(c3, null));

		when(brandManager.getBrands(c1.getConnectorKind())).thenReturn(List.of("a", "b", "c"));
		when(brandManager.getBrands(c2.getConnectorKind())).thenReturn(List.<String> of());
		when(brandManager.getBrands(c3.getConnectorKind())).thenReturn(List.of("d"));
		assertBrands(provider, new ConnectorBrand(c1, null), new ConnectorBrand(c1, "a"), new ConnectorBrand(c1, "b"),
				new ConnectorBrand(c1, "c"), new ConnectorBrand(c2, null), new ConnectorBrand(c3, null),
				new ConnectorBrand(c3, "d"));
	}

	protected MockRepositoryConnector mockConnector(String kind) {
		MockRepositoryConnector spy = spy(new MockRepositoryConnector());
		when(spy.getConnectorKind()).thenReturn(kind);
		return spy;
	}

	protected void assertBrands(ConnectorBrandContentProvider provider, ConnectorBrand... brands) {
		assertEquals(Arrays.asList(brands), Arrays.asList(provider.getElements(null)));
	}
}
