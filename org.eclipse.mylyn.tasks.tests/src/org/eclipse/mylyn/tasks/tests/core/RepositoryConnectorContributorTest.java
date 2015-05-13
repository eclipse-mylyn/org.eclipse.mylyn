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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.TaskTask;
import org.eclipse.mylyn.internal.tasks.ui.IBrandManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.tests.support.MockRepositoryConnectorAdapter.DynamicMockRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.tests.support.MockRepositoryConnectorDescriptor.DynamicMockRepositoryConnector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.graphics.Image;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class RepositoryConnectorContributorTest extends TestCase {

	private final IBrandManager manager = TasksUiPlugin.getDefault().getBrandManager();

	private final DynamicMockRepositoryConnector connector = new DynamicMockRepositoryConnector();

	private final Map<String, TaskRepository> repositories = new HashMap<>();

	private final Map<String, ITask> tasks = new HashMap<>();

	private TaskRepository repositoryForConnectorWithNoBrands;

	private TaskTask taskForConnectorWithNoBrands;

	@Before
	@Override
	protected void setUp() throws Exception {
		setUpBrand("org.mylyn");
		setUpBrand("org.eclipse");
		setUpBrand("unknown");
		setUpBrand("exceptional");
		setUpBrand(null);

		String repositoryUrl = "http://mock-connector-with-no-brands";
		repositoryForConnectorWithNoBrands = new TaskRepository("mock-connector-with-no-brands", repositoryUrl);
		repositoryForConnectorWithNoBrands.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, "org.mylyn");
		TasksUi.getRepositoryManager().addRepository(repositoryForConnectorWithNoBrands);
		taskForConnectorWithNoBrands = new TaskTask("mock-connector-with-no-brands", repositoryUrl, "1");
	}

	private void setUpBrand(String brand) {
		String repositoryUrl = "http://mock-" + brand;
		TaskRepository repository = new TaskRepository(connector.getConnectorKind(), repositoryUrl);
		repository.setProperty(ITasksCoreConstants.PROPERTY_BRAND_ID, brand);
		TasksUi.getRepositoryManager().addRepository(repository);
		repositories.put(brand, repository);
		tasks.put(brand, new TaskTask(connector.getConnectorKind(), repositoryUrl, "1"));
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		for (TaskRepository repository : repositories.values()) {
			((TaskRepositoryManager) TasksUi.getRepositoryManager()).removeRepository(repository);
		}
		((TaskRepositoryManager) TasksUi.getRepositoryManager()).removeRepository(repositoryForConnectorWithNoBrands);
	}

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
	public void testDefaultBrandingIcon() {
		Image brandingImage = TasksUiPlugin.getDefault()
				.getBrandManager()
				.getDefaultBrandingIcon(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected branding image contributed by MockRepositoryConnectorAdapter", brandingImage);
		assertEquals(16, brandingImage.getImageData().height);
	}

	@Test
	public void testDefaultOverlayIcon() {
		ImageDescriptor overlay = TasksUiPlugin.getDefault()
				.getBrandManager()
				.getDefaultOverlayIcon(DynamicMockRepositoryConnector.CONNECTOR_KIND);
		assertNotNull("Expected overlay image contributed by MockRepositoryConnectorAdapter", overlay);
		assertEquals(8, overlay.getImageData().height);
	}

	@Test
	public void testGetBrands() throws Exception {
		assertEquals(ImmutableSet.of("org.mylyn", "org.eclipse"), manager.getBrands(connector.getConnectorKind()));
	}

	@Test
	public void testGetConnectorLabel() {
		assertEquals("Label for org.mylyn", manager.getConnectorLabel(connector, "org.mylyn"));
		assertEquals("Label for org.eclipse", manager.getConnectorLabel(connector, "org.eclipse"));
		assertEquals(connector.getLabel(), manager.getConnectorLabel(connector, "unknown"));
		assertEquals(connector.getLabel(), manager.getConnectorLabel(connector, "exceptional"));
		assertEquals(connector.getLabel(), manager.getConnectorLabel(connector, null));
		AbstractRepositoryConnector mockConnector = new DynamicMockRepositoryConnector() {
			@Override
			public String getConnectorKind() {
				return "mock-connector-with-no-brands";
			};
		};
		assertEquals(connector.getLabel(), manager.getConnectorLabel(mockConnector, "org.mylyn"));
	}

	@Test
	public void testGetBrandingIcon() {
		assertBrandingIconHeight(3, "org.mylyn");
		assertBrandingIconHeight(2, "org.eclipse");
		assertBrandingIconHeight(16, "unknown");
		assertBrandingIconHeight(16, "exceptional");
		assertBrandingIconHeight(16, null);

		assertNull(manager.getBrandingIcon(repositoryForConnectorWithNoBrands.getConnectorKind(), "org.mylyn"));
		assertNull(manager.getBrandingIcon(repositoryForConnectorWithNoBrands));
	}

	private void assertBrandingIconHeight(int expectedHeight, String brand) {
		assertIconHeight(expectedHeight, manager.getBrandingIcon(connector.getConnectorKind(), brand));
		assertIconHeight(expectedHeight, manager.getBrandingIcon(repositories.get(brand)));
	}

	@Test
	public void testGetOverlayIcon() {
		assertOverlayIconHeight(5, "org.mylyn");
		assertOverlayIconHeight(4, "org.eclipse");
		assertOverlayIconHeight(8, "unknown");
		assertOverlayIconHeight(8, "exceptional");
		assertOverlayIconHeight(8, null);

		assertNull(manager.getOverlayIcon(repositoryForConnectorWithNoBrands.getConnectorKind(), "org.mylyn"));
		assertNull(manager.getOverlayIcon(repositoryForConnectorWithNoBrands));
		assertNull(manager.getOverlayIcon(taskForConnectorWithNoBrands));
	}

	private void assertOverlayIconHeight(int expectedHeight, String brand) {
		assertIconHeight(expectedHeight, manager.getOverlayIcon(connector.getConnectorKind(), brand));
		assertIconHeight(expectedHeight, manager.getOverlayIcon(repositories.get(brand)));
		assertIconHeight(expectedHeight, manager.getOverlayIcon(tasks.get(brand)));
	}

	private void assertIconHeight(int expectedHeight, Image image) {
		assertEquals(expectedHeight, image.getImageData().height);
	}

	private void assertIconHeight(int expectedHeight, ImageDescriptor descriptor) {
		assertEquals(expectedHeight, CommonImages.getImage(descriptor).getImageData().height);
	}
}
