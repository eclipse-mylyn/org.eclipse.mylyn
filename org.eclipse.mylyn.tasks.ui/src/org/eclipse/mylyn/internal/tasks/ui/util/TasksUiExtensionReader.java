/*******************************************************************************
 * Copyright (c) 2004, 2014 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.util.ContributorBlackList;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TasksUiExtensionReader {

	public static final String EXTENSION_REPOSITORY_LINKS_PROVIDERS = "org.eclipse.mylyn.tasks.ui.projectLinkProviders"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_LINK_PROVIDER = "linkProvider"; //$NON-NLS-1$

	public static final String EXTENSION_TASK_CONTRIBUTOR = "org.eclipse.mylyn.tasks.ui.actions"; //$NON-NLS-1$

	public static final String DYNAMIC_POPUP_ELEMENT = "dynamicPopupMenu"; //$NON-NLS-1$

	public static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	public static final String ATTR_MENU_PATH = "menuPath"; //$NON-NLS-1$

	public static final String EXTENSION_EDITORS = "org.eclipse.mylyn.tasks.ui.editors"; //$NON-NLS-1$

	public static final String ELMNT_TASK_EDITOR_PAGE_FACTORY = "pageFactory"; //$NON-NLS-1$

	public static final String EXTENSION_DUPLICATE_DETECTORS = "org.eclipse.mylyn.tasks.ui.duplicateDetectors"; //$NON-NLS-1$

	public static final String ELMNT_DUPLICATE_DETECTOR = "detector"; //$NON-NLS-1$

	public static final String ATTR_NAME = "name"; //$NON-NLS-1$

	public static final String ATTR_KIND = "kind"; //$NON-NLS-1$

	private static final String EXTENSION_PRESENTATIONS = "org.eclipse.mylyn.tasks.ui.presentations"; //$NON-NLS-1$

	public static final String ATTR_ICON = "icon"; //$NON-NLS-1$

	public static final String ATTR_PRIMARY = "primary"; //$NON-NLS-1$

	public static final String ATTR_ID = "id"; //$NON-NLS-1$

	public static void initStartupExtensions(ContributorBlackList blackList) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint presentationsExtensionPoint = registry.getExtensionPoint(EXTENSION_PRESENTATIONS);
		IExtension[] presentations = presentationsExtensionPoint.getExtensions();
		for (IExtension presentation : presentations) {
			IConfigurationElement[] elements = presentation.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					readPresentation(element);
				}
			}
		}

		// NOTE: causes ..mylyn.context.ui to load
		IExtensionPoint editorsExtensionPoint = registry.getExtensionPoint(EXTENSION_EDITORS);
		IExtension[] editors = editorsExtensionPoint.getExtensions();
		for (IExtension editor : editors) {
			IConfigurationElement[] elements = editor.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(ELMNT_TASK_EDITOR_PAGE_FACTORY)) {
						readTaskEditorPageFactory(element);
					}
				}
			}
		}
	}

	public static void initWorkbenchUiExtensions(ContributorBlackList blackList) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		RepositoryConnectorUiExtensionReader reader = new RepositoryConnectorUiExtensionReader(registry, blackList);
		reader.registerConnectorUis();

		IExtensionPoint linkProvidersExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORY_LINKS_PROVIDERS);
		IExtension[] linkProvidersExtensions = linkProvidersExtensionPoint.getExtensions();
		for (IExtension linkProvidersExtension : linkProvidersExtensions) {
			IConfigurationElement[] elements = linkProvidersExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(ELMNT_REPOSITORY_LINK_PROVIDER)) {
						readLinkProvider(element);
					}
				}
			}
		}

		IExtensionPoint duplicateDetectorsExtensionPoint = registry.getExtensionPoint(EXTENSION_DUPLICATE_DETECTORS);
		IExtension[] dulicateDetectorsExtensions = duplicateDetectorsExtensionPoint.getExtensions();
		for (IExtension dulicateDetectorsExtension : dulicateDetectorsExtensions) {
			IConfigurationElement[] elements = dulicateDetectorsExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(ELMNT_DUPLICATE_DETECTOR)) {
						readDuplicateDetector(element);
					}
				}
			}
		}

		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_TASK_CONTRIBUTOR);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(DYNAMIC_POPUP_ELEMENT)) {
						readDynamicPopupContributor(element);
					}
				}
			}
		}
	}

	private static void readPresentation(IConfigurationElement element) {
		try {
			String name = element.getAttribute(ATTR_NAME);

			String iconPath = element.getAttribute(ATTR_ICON);
			ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin( //
					element.getContributor().getName(), iconPath);
			AbstractTaskListPresentation presentation = (AbstractTaskListPresentation) element.createExecutableExtension(ATTR_CLASS);
			presentation.setPluginId(element.getNamespaceIdentifier());
			presentation.setImageDescriptor(imageDescriptor);
			presentation.setName(name);

			String primary = element.getAttribute(ATTR_PRIMARY);
			if (primary != null && primary.equals("true")) { //$NON-NLS-1$
				presentation.setPrimary(true);
			}

			TaskListView.addPresentation(presentation);
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load presentation extension", e)); //$NON-NLS-1$
		}
	}

	private static void readDuplicateDetector(IConfigurationElement element) {
		try {
			Object obj = element.createExecutableExtension(ATTR_CLASS);
			if (obj instanceof AbstractDuplicateDetector) {
				AbstractDuplicateDetector duplicateDetector = (AbstractDuplicateDetector) obj;
				duplicateDetector.setName(element.getAttribute(ATTR_NAME));
				duplicateDetector.setConnectorKind(element.getAttribute(ATTR_KIND));
				TasksUiPlugin.getDefault().addDuplicateDetector(duplicateDetector);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load duplicate detector " + obj.getClass().getCanonicalName())); //$NON-NLS-1$
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load duplicate detector", e)); //$NON-NLS-1$
		}
	}

	private static void readLinkProvider(IConfigurationElement element) {
		try {
			Object repositoryLinkProvider = element.createExecutableExtension(ATTR_CLASS);
			if (repositoryLinkProvider instanceof AbstractTaskRepositoryLinkProvider) {
				TasksUiPlugin.getDefault().addRepositoryLinkProvider(
						(AbstractTaskRepositoryLinkProvider) repositoryLinkProvider);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load repository link provider " //$NON-NLS-1$
						+ repositoryLinkProvider.getClass().getCanonicalName()));
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load repository link provider", e)); //$NON-NLS-1$
		}
	}

	private static void readTaskEditorPageFactory(IConfigurationElement element) {
		String id = element.getAttribute(ATTR_ID);
		if (id == null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Editor page factory must specify id")); //$NON-NLS-1$
			return;
		}

		try {
			Object item = element.createExecutableExtension(ATTR_CLASS);
			if (item instanceof AbstractTaskEditorPageFactory) {
				AbstractTaskEditorPageFactory editorPageFactory = (AbstractTaskEditorPageFactory) item;
				editorPageFactory.setId(id);
				editorPageFactory.setPluginId(element.getNamespaceIdentifier());
				TasksUiPlugin.getDefault().addTaskEditorPageFactory(editorPageFactory);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load editor page factory " + item.getClass().getCanonicalName() + " must implement " //$NON-NLS-1$ //$NON-NLS-2$
						+ AbstractTaskEditorPageFactory.class.getCanonicalName()));
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load page editor factory", //$NON-NLS-1$
					e));
		}
	}

	private static void readDynamicPopupContributor(IConfigurationElement element) {
		try {
			Object dynamicPopupContributor = element.createExecutableExtension(ATTR_CLASS);
			String menuPath = element.getAttribute(ATTR_MENU_PATH);
			if (dynamicPopupContributor instanceof IDynamicSubMenuContributor) {
				TasksUiPlugin.getDefault().addDynamicPopupContributor(menuPath,
						(IDynamicSubMenuContributor) dynamicPopupContributor);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load dynamic popup menu: " + dynamicPopupContributor.getClass().getCanonicalName() //$NON-NLS-1$
						+ " must implement " + IDynamicSubMenuContributor.class.getCanonicalName())); //$NON-NLS-1$
			}
		} catch (Throwable e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load dynamic popup menu extension", e)); //$NON-NLS-1$
		}
	}

}
