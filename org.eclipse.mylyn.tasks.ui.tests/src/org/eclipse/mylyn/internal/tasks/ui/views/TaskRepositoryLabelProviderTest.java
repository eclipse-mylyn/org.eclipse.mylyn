/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.ui.ConnectorBrand;
import org.eclipse.mylyn.internal.tasks.ui.IBrandManager;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;
import org.mockito.Matchers;

public class TaskRepositoryLabelProviderTest {
	private final IBrandManager manager = mock(IBrandManager.class);;

	private final TaskRepositoryLabelProvider labelProvider = new TaskRepositoryLabelProvider() {
		@Override
		protected org.eclipse.mylyn.internal.tasks.ui.IBrandManager getBrandManager() {
			return manager;
		};
	};

	@Test
	public void testGetImage() {
		Image brandingImage = new Image(Display.getCurrent(), 1, 1);
		when(manager.getBrandingIcon("mock", "a")).thenReturn(brandingImage);
		assertEquals(brandingImage, labelProvider.getImage(new ConnectorBrand(new MockRepositoryConnector(), "a")));
		assertEquals(CommonImages.getImage(TasksUiImages.REPOSITORY),
				labelProvider.getImage(new ConnectorBrand(new MockRepositoryConnector(), "b")));
	}

	@Test
	public void testGetText() {
		when(manager.getConnectorLabel(any(AbstractRepositoryConnector.class), Matchers.eq("a"))).thenReturn(
				"Mock Brand");
		when(manager.getConnectorLabel(any(AbstractRepositoryConnector.class), Matchers.eq("b"))).thenReturn(
				"Mock Brand B");
		assertEquals("Mock Brand", labelProvider.getText(new ConnectorBrand(new MockRepositoryConnector(), "a")));
		assertEquals("Mock Brand B", labelProvider.getText(new ConnectorBrand(new MockRepositoryConnector(), "b")));
	}
}
