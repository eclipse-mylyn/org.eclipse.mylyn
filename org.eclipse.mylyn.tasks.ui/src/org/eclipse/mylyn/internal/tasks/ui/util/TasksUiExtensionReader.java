/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTaskListFactory;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorFactory;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Rob Elves
 */
public class TasksUiExtensionReader {

	public static final String EXTENSION_REPOSITORIES = "org.eclipse.mylyn.tasks.ui.repositories";

	public static final String EXTENSION_REPOSITORY_LINKS_PROVIDERS = "org.eclipse.mylyn.tasks.ui.projectLinkProviders";

	public static final String EXTENSION_TEMPLATES = "org.eclipse.mylyn.tasks.core.templates";

	public static final String EXTENSION_TMPL_REPOSITORY = "repository";

	public static final String ELMNT_TMPL_LABEL = "label";

	public static final String ELMNT_TMPL_URLREPOSITORY = "urlRepository";

	public static final String ELMNT_TMPL_REPOSITORYKIND = "repositoryKind";

	public static final String ELMNT_TMPL_CHARACTERENCODING = "characterEncoding";

	public static final String ELMNT_TMPL_ANONYMOUS = "anonymous";

	public static final String ELMNT_TMPL_VERSION = "version";

	public static final String ELMNT_TMPL_URLNEWTASK = "urlNewTask";

	public static final String ELMNT_TMPL_URLTASK = "urlTask";

	public static final String ELMNT_TMPL_URLTASKQUERY = "urlTaskQuery";

	public static final String ELMNT_TMPL_NEWACCOUNTURL = "urlNewAccount";

	public static final String ELMNT_TMPL_ADDAUTO = "addAutomatically";

	public static final String ELMNT_REPOSITORY_CONNECTOR = "connectorCore";

	public static final String ATTR_USER_MANAGED = "userManaged";

	public static final String ATTR_CUSTOM_NOTIFICATIONS = "customNotifications";

	public static final String ELMNT_REPOSITORY_LINK_PROVIDER = "linkProvider";

	public static final String ELMNT_REPOSITORY_UI = "connectorUi";

	public static final String ELMNT_EXTERNALIZER = "taskListFactory";

	public static final String ATTR_BRANDING_ICON = "brandingIcon";

	public static final String ATTR_OVERLAY_ICON = "overlayIcon";

	public static final String ELMNT_TYPE = "type";

	public static final String ELMNT_QUERY_PAGE = "queryPage";

	public static final String ELMNT_SETTINGS_PAGE = "settingsPage";

	public static final String EXTENSION_TASK_CONTRIBUTOR = "org.eclipse.mylyn.tasks.ui.actions";

	public static final String ATTR_ACTION_CONTRIBUTOR_CLASS = "taskHandlerClass";

	public static final String DYNAMIC_POPUP_ELEMENT = "dynamicPopupMenu";

	public static final String ATTR_CLASS = "class";

	public static final String ATTR_MENU_PATH = "menuPath";

	public static final String EXTENSION_EDITORS = "org.eclipse.mylyn.tasks.ui.editors";

	public static final String ELMNT_EDITOR_FACTORY = "editorFactory";

	public static final String ELMNT_TASK_EDITOR_PAGE_FACTORY = "pageFactory";

	public static final String ELMNT_HYPERLINK_LISTENER = "hyperlinkListener";

	public static final String ELMNT_HYPERLINK_DETECTOR = "hyperlinkDetector";

	public static final String EXTENSION_DUPLICATE_DETECTORS = "org.eclipse.mylyn.tasks.ui.duplicateDetectors";

	public static final String ELMNT_DUPLICATE_DETECTOR = "detector";

	public static final String ATTR_NAME = "name";

	public static final String ATTR_KIND = "kind";

	private static final String EXTENSION_PRESENTATIONS = "org.eclipse.mylyn.tasks.ui.presentations";

	public static final String ELMNT_PRESENTATION = "presentation";

	public static final String ATTR_ICON = "icon";

	public static final String ATTR_PRIMARY = "primary";

	public static final String ATTR_ID = "id";

	private static boolean coreExtensionsRead = false;

	public static void initStartupExtensions(TaskListWriter delegatingExternalizer) {
		List<AbstractTaskListFactory> externalizers = new ArrayList<AbstractTaskListFactory>();
		if (!coreExtensionsRead) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			// NOTE: has to be read first, consider improving
			IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORIES);
			IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
			for (IExtension repositoryExtension : repositoryExtensions) {
				IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(ELMNT_REPOSITORY_CONNECTOR)) {
						readRepositoryConnectorCore(element);
					} else if (element.getName().equals(ELMNT_EXTERNALIZER)) {
						readExternalizer(element, externalizers);
					}
				}
			}
			delegatingExternalizer.setDelegateExternalizers(externalizers);

			IExtensionPoint templatesExtensionPoint = registry.getExtensionPoint(EXTENSION_TEMPLATES);
			IExtension[] templateExtensions = templatesExtensionPoint.getExtensions();
			for (IExtension templateExtension : templateExtensions) {
				IConfigurationElement[] elements = templateExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(EXTENSION_TMPL_REPOSITORY)) {
						readRepositoryTemplate(element);
					}
				}
			}

			IExtensionPoint presentationsExtensionPoint = registry.getExtensionPoint(EXTENSION_PRESENTATIONS);
			IExtension[] presentations = presentationsExtensionPoint.getExtensions();
			for (IExtension presentation : presentations) {
				IConfigurationElement[] elements = presentation.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					readPresentation(element);
				}
			}

			// NOTE: causes ..mylyn.context.ui to load
			IExtensionPoint editorsExtensionPoint = registry.getExtensionPoint(EXTENSION_EDITORS);
			IExtension[] editors = editorsExtensionPoint.getExtensions();
			for (IExtension editor : editors) {
				IConfigurationElement[] elements = editor.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(ELMNT_EDITOR_FACTORY)) {
						readEditorFactory(element);
					} else if (element.getName().equals(ELMNT_TASK_EDITOR_PAGE_FACTORY)) {
						readTaskEditorPageFactory(element);
					}
				}
			}

			// NOTE: causes ..mylyn.java.ui to load
			for (IExtension editor : editors) {
				IConfigurationElement[] elements = editor.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (element.getName().equals(ELMNT_HYPERLINK_DETECTOR)) {
						readHyperlinkDetector(element);
					}
				}
			}

			coreExtensionsRead = true;
		}
	}

	public static void initWorkbenchUiExtensions() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();

		IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORIES);
		IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
		for (IExtension repositoryExtension : repositoryExtensions) {
			IConfigurationElement[] elements = repositoryExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELMNT_REPOSITORY_UI)) {
					readRepositoryConnectorUi(element);
				}
			}
		}

		IExtensionPoint linkProvidersExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORY_LINKS_PROVIDERS);
		IExtension[] linkProvidersExtensions = linkProvidersExtensionPoint.getExtensions();
		for (IExtension linkProvidersExtension : linkProvidersExtensions) {
			IConfigurationElement[] elements = linkProvidersExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELMNT_REPOSITORY_LINK_PROVIDER)) {
					readLinkProvider(element);
				}
			}
		}

		IExtensionPoint duplicateDetectorsExtensionPoint = registry.getExtensionPoint(EXTENSION_DUPLICATE_DETECTORS);
		IExtension[] dulicateDetectorsExtensions = duplicateDetectorsExtensionPoint.getExtensions();
		for (IExtension dulicateDetectorsExtension : dulicateDetectorsExtensions) {
			IConfigurationElement[] elements = dulicateDetectorsExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(ELMNT_DUPLICATE_DETECTOR)) {
					readDuplicateDetector(element);
				}
			}
		}

		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_TASK_CONTRIBUTOR);
		IExtension[] extensions = extensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (element.getName().equals(DYNAMIC_POPUP_ELEMENT)) {
					readDynamicPopupContributor(element);
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
			presentation.setImageDescriptor(imageDescriptor);
			presentation.setName(name);

			String primary = element.getAttribute(ATTR_PRIMARY);
			if (primary != null && primary.equals("true")) {
				presentation.setPrimary(true);
			}

			TaskListView.addPresentation(presentation);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load presentation extension", e));
		}
	}

	private static void readDuplicateDetector(IConfigurationElement element) {
		try {
			Object obj = element.createExecutableExtension(ATTR_CLASS);
			if (obj instanceof AbstractDuplicateDetector) {
				AbstractDuplicateDetector duplicateDetector = (AbstractDuplicateDetector) obj;
				duplicateDetector.setName(element.getAttribute(ATTR_NAME));
				duplicateDetector.setKind(element.getAttribute(ATTR_KIND));
				TasksUiPlugin.getDefault().addDuplicateDetector(duplicateDetector);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load duplicate detector " + obj.getClass().getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load duplicate detector", e));
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
						"Could not load repository link provider "
								+ repositoryLinkProvider.getClass().getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load repository link provider", e));
		}
	}

	private static void readHyperlinkDetector(IConfigurationElement element) {
		try {
			Object hyperlinkDetector = element.createExecutableExtension(ATTR_CLASS);
			if (hyperlinkDetector instanceof IHyperlinkDetector) {
				TasksUiPlugin.getDefault().addTaskHyperlinkDetector((IHyperlinkDetector) hyperlinkDetector);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load hyperlink detector " + hyperlinkDetector.getClass().getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load hyperlink detector", e));
		}
	}

	private static void readEditorFactory(IConfigurationElement element) {
		try {
			Object editor = element.createExecutableExtension(ATTR_CLASS);
			if (editor instanceof AbstractTaskEditorFactory) {
				TasksUiPlugin.getDefault().addContextEditor((AbstractTaskEditorFactory) editor);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load editor "
						+ editor.getClass().getCanonicalName() + " must implement "
						+ AbstractTaskEditorFactory.class.getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load editor", e));
		}
	}

	private static void readTaskEditorPageFactory(IConfigurationElement element) {
		String id = element.getAttribute(ATTR_ID);
		if (id == null) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Editor page factory must specify id"));
			return;
		}

		try {
			Object item = element.createExecutableExtension(ATTR_CLASS);
			if (item instanceof AbstractTaskEditorPageFactory) {
				AbstractTaskEditorPageFactory editorPageFactory = (AbstractTaskEditorPageFactory) item;
				editorPageFactory.setId(id);
				TasksUiPlugin.getDefault().addTaskEditorPageFactory(editorPageFactory);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
						"Could not load editor page factory " + item.getClass().getCanonicalName() + " must implement "
								+ AbstractTaskEditorPageFactory.class.getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load page editor factory",
					e));
		}
	}

	private static void readRepositoryConnectorCore(IConfigurationElement element) {
		try {
			Object type = element.getAttribute(ELMNT_TYPE);
			Object connectorCore = element.createExecutableExtension(ATTR_CLASS);
			if (connectorCore instanceof AbstractRepositoryConnector && type != null) {
				AbstractRepositoryConnector repositoryConnector = (AbstractRepositoryConnector) connectorCore;
				TasksUiPlugin.getRepositoryManager().addRepositoryConnector(repositoryConnector);

				String userManagedString = element.getAttribute(ATTR_USER_MANAGED);
				if (userManagedString != null) {
					boolean userManaged = Boolean.parseBoolean(userManagedString);
					repositoryConnector.setUserManaged(userManaged);
				}
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector core "
						+ connectorCore.getClass().getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector core", e));
		}
	}

	private static void readRepositoryConnectorUi(IConfigurationElement element) {
		try {
			Object connectorUiObject = element.createExecutableExtension(ATTR_CLASS);
			if (connectorUiObject instanceof AbstractRepositoryConnectorUi) {
				AbstractRepositoryConnectorUi connectorUi = (AbstractRepositoryConnectorUi) connectorUiObject;
				TasksUiPlugin.getDefault().addRepositoryConnectorUi(connectorUi);

				String customNotificationsString = element.getAttribute(ATTR_CUSTOM_NOTIFICATIONS);
				if (customNotificationsString != null) {
					boolean customNotifications = Boolean.parseBoolean(customNotificationsString);
					connectorUi.setCustomNotificationHandling(customNotifications);
				}

				String iconPath = element.getAttribute(ATTR_BRANDING_ICON);
				if (iconPath != null) {
					ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor()
							.getName(), iconPath);
					if (descriptor != null) {
						TasksUiPlugin.getDefault().addBrandingIcon(connectorUi.getConnectorKind(),
								TasksUiImages.getImage(descriptor));
					}
				}
				String overlayIconPath = element.getAttribute(ATTR_OVERLAY_ICON);
				if (overlayIconPath != null) {
					ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(element.getContributor()
							.getName(), overlayIconPath);
					if (descriptor != null) {
						TasksUiPlugin.getDefault().addOverlayIcon(connectorUi.getConnectorKind(), descriptor);
					}
				}
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector ui "
						+ connectorUiObject.getClass().getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load connector ui", e));
		}
	}

	private static void readRepositoryTemplate(IConfigurationElement element) {
		boolean anonymous = false;
		boolean addAuto = false;

		String label = element.getAttribute(ELMNT_TMPL_LABEL);
		String serverUrl = element.getAttribute(ELMNT_TMPL_URLREPOSITORY);
		String repKind = element.getAttribute(ELMNT_TMPL_REPOSITORYKIND);
		String version = element.getAttribute(ELMNT_TMPL_VERSION);
		String newTaskUrl = element.getAttribute(ELMNT_TMPL_URLNEWTASK);
		String taskPrefix = element.getAttribute(ELMNT_TMPL_URLTASK);
		String taskQueryUrl = element.getAttribute(ELMNT_TMPL_URLTASKQUERY);
		String newAccountUrl = element.getAttribute(ELMNT_TMPL_NEWACCOUNTURL);
		String encoding = element.getAttribute(ELMNT_TMPL_CHARACTERENCODING);
		addAuto = Boolean.parseBoolean(element.getAttribute(ELMNT_TMPL_ADDAUTO));
		anonymous = Boolean.parseBoolean(element.getAttribute(ELMNT_TMPL_ANONYMOUS));

		if (serverUrl != null && label != null && repKind != null
				&& TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repKind) != null) {
			AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager()
					.getRepositoryConnector(repKind);
			RepositoryTemplate template = new RepositoryTemplate(label, serverUrl, encoding, version, newTaskUrl,
					taskPrefix, taskQueryUrl, newAccountUrl, anonymous, addAuto);
			connector.addTemplate(template);

			for (IConfigurationElement configElement : element.getChildren()) {
				String name = configElement.getAttribute("name");
				String value = configElement.getAttribute("value");
				if (name != null && !name.equals("") && value != null) {
					template.addAttribute(name, value);
				}
			}
		} else {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load repository template extension " + element.getName()));
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
						"Could not load dynamic popup menu: " + dynamicPopupContributor.getClass().getCanonicalName()
								+ " must implement " + IDynamicSubMenuContributor.class.getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load dynamic popup menu extension", e));
		}
	}

	private static void readExternalizer(IConfigurationElement element, List<AbstractTaskListFactory> externalizers) {
		try {
			Object externalizerObject = element.createExecutableExtension(ATTR_CLASS);
			if (externalizerObject instanceof AbstractTaskListFactory) {
				AbstractTaskListFactory externalizer = (AbstractTaskListFactory) externalizerObject;
				externalizers.add(externalizer);
			} else {
				StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Could not load externalizer: "
						+ externalizerObject.getClass().getCanonicalName() + " must implement "
						+ AbstractTaskListFactory.class.getCanonicalName()));
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
					"Could not load task handler extension", e));
		}
	}
}
