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

package org.eclipse.mylyn.internal.tasks.ui;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("restriction")
public class BrandManagerTest {
	private final BrandManager brandManager = new BrandManager();

	@Test
	public void testGetConnectorLabel() {
		MockRepositoryConnector mock1 = connector("mock1");
		MockRepositoryConnector mock2 = connector("mock2");
		brandManager.addConnectorLabel("mock1", "org.mylyn", "Mylyn");
		brandManager.addConnectorLabel("mock1", "org.eclipse", "Eclipse");
		brandManager.addConnectorLabel("mock2", "org.mylyn", "Mylyn");
		brandManager.addConnectorLabel("mock2", "com.acme", "ACME");

		assertBrands();
		assertEquals("Mylyn", brandManager.getConnectorLabel(mock1, "org.mylyn"));
		assertEquals("Eclipse", brandManager.getConnectorLabel(mock1, "org.eclipse"));
		assertEquals("Mylyn", brandManager.getConnectorLabel(mock2, "org.mylyn"));
		assertEquals("ACME", brandManager.getConnectorLabel(mock2, "com.acme"));
		assertEquals(mock2.getLabel(), brandManager.getConnectorLabel(mock2, "org.eclipse"));
		assertEquals(mock2.getLabel(), brandManager.getConnectorLabel(mock2, null));
	}

	@Test
	public void testGetBrandingIcon() {
		brandManager.addBrandingIcon("mock1", "org.mylyn", image(1));
		brandManager.addBrandingIcon("mock1", "org.eclipse", new Image(Display.getCurrent(), 2, 2));
		brandManager.addBrandingIcon("mock2", "org.mylyn", new Image(Display.getCurrent(), 3, 3));
		brandManager.addBrandingIcon("mock2", "com.acme", new Image(Display.getCurrent(), 4, 4));

		assertBrands();
		assertBrandingIconHeight(1, "mock1", "org.mylyn");
		assertBrandingIconHeight(2, "mock1", "org.eclipse");
		assertBrandingIconHeight(3, "mock2", "org.mylyn");
		assertBrandingIconHeight(4, "mock2", "com.acme");

		assertEquals(null, brandManager.getBrandingIcon("mock2", "org.eclipse"));
		assertEquals(null, brandManager.getBrandingIcon(repository("mock2", "org.eclipse")));
		assertEquals(null, brandManager.getBrandingIcon("mock2", null));
		assertEquals(null, brandManager.getBrandingIcon(repository("mock2", null)));

		brandManager.addDefaultBrandingIcon("mock2", new Image(Display.getCurrent(), 5, 5));
		assertEquals(5, brandManager.getBrandingIcon("mock2", "org.eclipse").getImageData().height);
		assertEquals(5, brandManager.getBrandingIcon(repository("mock2", "org.eclipse")).getImageData().height);
		assertEquals(5, brandManager.getBrandingIcon("mock2", null).getImageData().height);
		assertEquals(5, brandManager.getBrandingIcon(repository("mock2", null)).getImageData().height);
	}

	private void assertBrandingIconHeight(int expectedHeight, String connectorKind, String brand) {
		assertEquals(expectedHeight, brandManager.getBrandingIcon(connectorKind, brand).getImageData().height);
		assertEquals(expectedHeight,
				brandManager.getBrandingIcon(repository(connectorKind, brand)).getImageData().height);
	}

	private TaskRepository repository(String connectorKind, String brand) {
		TaskRepository repository = new TaskRepository(connectorKind, "http://mock");
		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, brand);
		return repository;
	}

	@Test
	public void testGetOverlayIcon() {
		brandManager.addOverlayIcon("mock1", "org.mylyn", imageDescriptor(1));
		brandManager.addOverlayIcon("mock1", "org.eclipse", imageDescriptor(2));
		brandManager.addOverlayIcon("mock2", "org.mylyn", imageDescriptor(3));
		brandManager.addOverlayIcon("mock2", "com.acme", imageDescriptor(4));

		assertBrands();
		assertOverlayIconHeight(1, "mock1", "org.mylyn");
		assertOverlayIconHeight(2, "mock1", "org.eclipse");
		assertOverlayIconHeight(3, "mock2", "org.mylyn");
		assertOverlayIconHeight(4, "mock2", "com.acme");

		assertEquals(null, brandManager.getOverlayIcon("mock2", "org.eclipse"));
		assertEquals(null, brandManager.getOverlayIcon(repository("mock2", "org.eclipse")));
		assertEquals(null, brandManager.getOverlayIcon("mock2", null));
		assertEquals(null, brandManager.getOverlayIcon(repository("mock2", null)));

		brandManager.addDefaultOverlayIcon("mock2", imageDescriptor(5));
		assertEquals(5, brandManager.getOverlayIcon("mock2", "org.eclipse").getImageData().height);
		assertEquals(5, brandManager.getOverlayIcon(repository("mock2", "org.eclipse")).getImageData().height);
		assertEquals(5, brandManager.getOverlayIcon("mock2", null).getImageData().height);
		assertEquals(5, brandManager.getOverlayIcon(repository("mock2", null)).getImageData().height);
	}

	private void assertOverlayIconHeight(int expectedHeight, String connectorKind, String brand) {
		assertEquals(expectedHeight, brandManager.getOverlayIcon(connectorKind, brand).getImageData().height);
		assertEquals(expectedHeight,
				brandManager.getOverlayIcon(repository(connectorKind, brand)).getImageData().height);
	}

	private void assertBrands() {
		assertEquals(ImmutableSet.of("org.mylyn", "org.eclipse"), brandManager.getBrands("mock1"));
		assertEquals(ImmutableSet.of("org.mylyn", "com.acme"), brandManager.getBrands("mock2"));
		assertEquals(ImmutableSet.of(), brandManager.getBrands("unknown"));
	}

	private Image image(int size) {
		return new Image(Display.getCurrent(), size, size);
	}

	private ImageDescriptor imageDescriptor(int size) {
		ImageDescriptor mock = mock(ImageDescriptor.class);
		ImageData data = new ImageData(size, size, 1, new PaletteData(1, 1, 1));
		when(mock.getImageData()).thenReturn(data);
		return mock;
	}

	private MockRepositoryConnector connector(final String kind) {
		return new MockRepositoryConnector() {
			@Override
			public String getConnectorKind() {
				return kind;
			}
		};
	}
}
