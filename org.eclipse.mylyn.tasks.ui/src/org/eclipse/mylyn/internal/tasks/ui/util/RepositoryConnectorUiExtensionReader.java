/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.util.ContributorBlackList;
import org.eclipse.mylyn.internal.tasks.ui.BrandManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorBranding;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RepositoryConnectorUiExtensionReader {

	private static final String EXTENSION_REPOSITORIES = "org.eclipse.mylyn.tasks.ui.repositories"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_UI = "connectorUi"; //$NON-NLS-1$

	private static final String ATTR_BRANDING_ICON = "brandingIcon"; //$NON-NLS-1$

	private static final String ATTR_OVERLAY_ICON = "overlayIcon"; //$NON-NLS-1$

	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private final IExtensionRegistry registry;

	/**
	 * Plug-in ids of connector extensions that failed to load.
	 */
	private final ContributorBlackList blackList;

	public RepositoryConnectorUiExtensionReader(IExtensionRegistry registry, ContributorBlackList blackList) {
		Assert.isNotNull(registry);
		Assert.isNotNull(blackList);
		this.registry = registry;
		this.blackList = blackList;
	}

	public void registerConnectorUis() {
		registerFromExtensionPoint();
		registerFromAdaptable();
		registerConnetorToConnectorUi();
	}

	private void registerConnetorToConnectorUi() {
		for (AbstractRepositoryConnector connector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
			AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin.getConnectorUi(connector.getConnectorKind());
			if (connectorUi != null) {
				setConnectorForConnectorUi(connector, connectorUi);
			}
		}
	}

	private void registerFromAdaptable() {
		for (AbstractRepositoryConnector connector : TasksUi.getRepositoryManager().getRepositoryConnectors()) {
			if (TasksUiPlugin.getConnectorUi(connector.getConnectorKind()) == null) {
				registerFromAdaptable(connector);
			}
		}
	}

	private void registerFromAdaptable(final AbstractRepositoryConnector connector) {
		SafeRunner.run(new ISafeRunnable() {
			@Override
			public void run() throws Exception {
				AbstractRepositoryConnectorUi connectorUi = loadAdapter(connector, AbstractRepositoryConnectorUi.class);
				if (connectorUi != null) {
					TasksUiPlugin.getDefault().addRepositoryConnectorUi(connectorUi);
				}

				RepositoryConnectorBranding branding = loadAdapter(connector, RepositoryConnectorBranding.class);
				if (branding != null) {
					addDefaultImageData(connector, branding);
					addBranding(connector.getConnectorKind(), branding);
				}
			}

			protected void addDefaultImageData(final AbstractRepositoryConnector connector,
					RepositoryConnectorBranding branding) throws IOException {
				InputStream brandingImageData = branding.getBrandingImageData();
				if (brandingImageData != null) {
					try {
						((BrandManager) TasksUiPlugin.getDefault().getBrandManager()).addDefaultBrandingIcon(
								connector.getConnectorKind(), getImage(brandingImageData));
					} finally {
						closeQuietly(brandingImageData);
					}
				}
				InputStream overlayImageData = branding.getOverlayImageData();
				if (overlayImageData != null) {
					try {
						((BrandManager) TasksUiPlugin.getDefault().getBrandManager()).addDefaultOverlayIcon(
								connector.getConnectorKind(), getImageDescriptor(overlayImageData));
					} finally {
						closeQuietly(brandingImageData);
					}
				}
			}

			@Override
			public void handleException(Throwable e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
						"Loading of connector ui for kind ''{0}'' failed.", connector.getConnectorKind()), e)); //$NON-NLS-1$
			}
		});
	}

	protected void addBranding(final String connectorKind, RepositoryConnectorBranding branding) throws IOException {
		BrandManager brandManager = (BrandManager) TasksUiPlugin.getDefault().getBrandManager();
		for (String brand : branding.getBrands()) {
			if (brand == null) {
				continue;
			}
			try {
				String connectorLabel = branding.getConnectorLabel(brand);
				if (connectorLabel != null) {
					brandManager.addConnectorLabel(connectorKind, brand, connectorLabel);
				}

				InputStream brandingImageData = branding.getBrandingImageData(brand);
				if (brandingImageData != null) {
					try {
						brandManager.addBrandingIcon(connectorKind, brand, getImage(brandingImageData));
					} finally {
						closeQuietly(brandingImageData);
					}
				}

				InputStream overlayImageData = branding.getOverlayImageData(brand);
				if (overlayImageData != null) {
					try {
						brandManager.addOverlayIcon(connectorKind, brand, getImageDescriptor(overlayImageData));
					} finally {
						closeQuietly(brandingImageData);
					}
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
						"Loading of brand '{0}' for kind '{1}' failed.", brand, connectorKind), e)); //$NON-NLS-1$
			}
		}
	}

	private void closeQuietly(InputStream in) {
		try {
			in.close();
		} catch (IOException e) {
			// ignore
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T loadAdapter(final AbstractRepositoryConnector connector, Class<T> klass) {
		T adapter = null;
		if (connector instanceof IAdaptable) {
			adapter = (T) ((IAdaptable) connector).getAdapter(klass);
		}
		if (adapter == null) {
			adapter = (T) Platform.getAdapterManager().loadAdapter(connector, klass.getName());
		}
		return adapter;
	}

	private ImageDescriptor getImageDescriptor(InputStream in) {
		return ImageDescriptor.createFromImageData(new ImageData(in));
	}

	private Image getImage(InputStream in) {
		return CommonImages.getImage(getImageDescriptor(in));
	}

	private void registerFromExtensionPoint() {
		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORIES);
		IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension repositoryExtension : repositoryExtensions) {
			IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(ELMNT_REPOSITORY_UI)) {
						registerRepositoryConnectorUi(element);
					}
				}
			}
		}
	}

	private void registerRepositoryConnectorUi(IConfigurationElement element) {
		try {
			Object connectorUiObject = element.createExecutableExtension(ATTR_CLASS);
			if (connectorUiObject instanceof AbstractRepositoryConnectorUi) {
				AbstractRepositoryConnectorUi connectorUi = (AbstractRepositoryConnectorUi) connectorUiObject;
				AbstractRepositoryConnector connector = TasksUiPlugin.getConnector(connectorUi.getConnectorKind());
				if (connector != null) {
					TasksUiPlugin.getDefault().addRepositoryConnectorUi(connectorUi);

					String iconPath = element.getAttribute(ATTR_BRANDING_ICON);
					if (iconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
								element.getContributor().getName(), iconPath);
						if (descriptor != null) {
							((BrandManager) TasksUiPlugin.getDefault().getBrandManager()).addDefaultBrandingIcon(
									connectorUi.getConnectorKind(), CommonImages.getImage(descriptor));
						}
					}
					String overlayIconPath = element.getAttribute(ATTR_OVERLAY_ICON);
					if (overlayIconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
								element.getContributor().getName(), overlayIconPath);
						if (descriptor != null) {
							((BrandManager) TasksUiPlugin.getDefault().getBrandManager()).addDefaultOverlayIcon(
									connectorUi.getConnectorKind(), descriptor);
						}
					}

					RepositoryConnectorBranding branding = loadAdapter(connector, RepositoryConnectorBranding.class);
					if (branding != null) {
						addBranding(connector.getConnectorKind(), branding);
					}
				} else {
					StatusHandler.log(new Status(
							IStatus.ERROR,
							TasksUiPlugin.ID_PLUGIN,
							NLS.bind(
									"Ignoring connector ui for kind ''{0}'' without corresponding core contributed by ''{1}''.", connectorUi.getConnectorKind(), element.getContributor().getName()))); //$NON-NLS-1$
				}
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector ui " //$NON-NLS-1$
						+ connectorUiObject.getClass().getCanonicalName()));
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector ui", e)); //$NON-NLS-1$
		}
	}

	private static void setConnectorForConnectorUi(AbstractRepositoryConnector connector,
			AbstractRepositoryConnectorUi connectorUi) {
		// need reflection since the field is private
		try {
			Field field = AbstractRepositoryConnectorUi.class.getDeclaredField("connector"); //$NON-NLS-1$
			field.setAccessible(true);
			field.set(connectorUi, connector);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Unable to call setConnector()", e)); //$NON-NLS-1$
		}
	}

}
