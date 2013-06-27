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

package org.eclipse.mylyn.tasks.tests.core;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.support.MockRepositoryConnectorAdapter.DynamicMockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.tests.support.MockRepositoryConnectorDescriptor.DynamicMockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Image;
import org.junit.Test;

public class RepositoryConnectorContributorTest extends TestCase {

	@Test
	public void testConnectorContributed() {
		AbstractRepositoryConnector connector = TasksUi.getRepositoryConnector(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected dynamically contributed mock connector", connector);
		assertEquals(DynamicMockRepositoryConnector.class, connector.getClass());
		assertEquals(DynamicMockRepositoryConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	@Test
	public void testConnectorUiContributed() {
		AbstractRepositoryConnectorUi connector = TasksUi.getRepositoryConnectorUi(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected connector UI contributed by MockRepositoryConnectorAdapter", connector);
		assertEquals(DynamicMockRepositoryConnectorUi.class, connector.getClass());
		assertEquals(DynamicMockRepositoryConnector.CONNECTOR_KIND, connector.getConnectorKind());
	}

	@Test
	public void testMockBrandingIcon() {
		Image brandingImage = TasksUiPlugin.getDefault().getBrandingIcon(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected branding image contributed by MockRepositoryConnectorAdapter", brandingImage);
	}

	@Test
	public void testMockOverlayIcon() {
		ImageDescriptor overlay = TasksUiPlugin.getDefault().getOverlayIcon(
				DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected overlay image contributed by MockRepositoryConnectorAdapter", overlay);
		assertEquals(8, overlay.getImageData().height);
	}

}
