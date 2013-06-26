/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.internal.tasks.core.activity.DefaultTaskActivityMonitor;
import org.eclipse.mylyn.internal.tasks.core.context.DefaultTaskContextStore;
import org.eclipse.mylyn.internal.tasks.core.externalization.TaskListExternalizer;
import org.eclipse.mylyn.internal.tasks.core.util.RepositoryConnectorExtensionReader;
import org.eclipse.mylyn.internal.tasks.ui.IDynamicSubMenuContributor;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.AbstractTaskListPresentation;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskListView;
import org.eclipse.mylyn.tasks.core.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.mylyn.tasks.core.activity.AbstractTaskActivityMonitor;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.AbstractTaskRepositoryLinkProvider;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Rob Elves
 * @author Steffen Pingel
 */
public class TasksUiExtensionReader {

	public static final String EXTENSION_REPOSITORIES = "org.eclipse.mylyn.tasks.ui.repositories"; //$NON-NLS-1$

	public static final String EXTENSION_REPOSITORY_LINKS_PROVIDERS = "org.eclipse.mylyn.tasks.ui.projectLinkProviders"; //$NON-NLS-1$

	public static final String EXTENSION_TEMPLATES = "org.eclipse.mylyn.tasks.core.templates"; //$NON-NLS-1$

	public static final String EXTENSION_TMPL_REPOSITORY = "repository"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_LABEL = "label"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_URLREPOSITORY = "urlRepository"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_REPOSITORYKIND = "repositoryKind"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_CHARACTERENCODING = "characterEncoding"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_ANONYMOUS = "anonymous"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_VERSION = "version"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_URLNEWTASK = "urlNewTask"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_URLTASK = "urlTask"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_URLTASKQUERY = "urlTaskQuery"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_NEWACCOUNTURL = "urlNewAccount"; //$NON-NLS-1$

	public static final String ELMNT_TMPL_ADDAUTO = "addAutomatically"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_LINK_PROVIDER = "linkProvider"; //$NON-NLS-1$

	public static final String ELMNT_REPOSITORY_UI = "connectorUi"; //$NON-NLS-1$

	public static final String ATTR_BRANDING_ICON = "brandingIcon"; //$NON-NLS-1$

	public static final String ATTR_OVERLAY_ICON = "overlayIcon"; //$NON-NLS-1$

	public static final String ELMNT_TYPE = "type"; //$NON-NLS-1$

	public static final String ELMNT_QUERY_PAGE = "queryPage"; //$NON-NLS-1$

	public static final String ELMNT_SETTINGS_PAGE = "settingsPage"; //$NON-NLS-1$

	public static final String EXTENSION_TASK_CONTRIBUTOR = "org.eclipse.mylyn.tasks.ui.actions"; //$NON-NLS-1$

	public static final String ATTR_ACTION_CONTRIBUTOR_CLASS = "taskHandlerClass"; //$NON-NLS-1$

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

	public static final String ELMNT_PRESENTATION = "presentation"; //$NON-NLS-1$

	public static final String ATTR_ICON = "icon"; //$NON-NLS-1$

	public static final String ATTR_PRIMARY = "primary"; //$NON-NLS-1$

	public static final String ATTR_ID = "id"; //$NON-NLS-1$

	private static boolean coreExtensionsRead = false;

	/**
	 * Plug-in ids of connector extensions that failed to load.
	 */
	private static Set<String> disabledContributors = new HashSet<String>();

	public static void initStartupExtensions(TaskListExternalizer taskListExternalizer,
			TaskRepositoryManager repositoryManager) {
		if (!coreExtensionsRead) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();

			// NOTE: has to be read first, consider improving
			IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORIES);
			RepositoryConnectorExtensionReader reader = new RepositoryConnectorExtensionReader(taskListExternalizer,
					repositoryManager);
			reader.registerConnectors(repositoriesExtensionPoint);
			disabledContributors.addAll(reader.getDisabledContributors());

			IExtensionPoint templatesExtensionPoint = registry.getExtensionPoint(EXTENSION_TEMPLATES);
			IExtension[] templateExtensions = templatesExtensionPoint.getExtensions();
			for (IExtension templateExtension : templateExtensions) {
				IConfigurationElement[] elements = templateExtension.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (!isDisabled(element)) {
						if (element.getName().equals(EXTENSION_TMPL_REPOSITORY)) {
							readRepositoryTemplate(element);
						}
					}
				}
			}

			IExtensionPoint presentationsExtensionPoint = registry.getExtensionPoint(EXTENSION_PRESENTATIONS);
			IExtension[] presentations = presentationsExtensionPoint.getExtensions();
			for (IExtension presentation : presentations) {
				IConfigurationElement[] elements = presentation.getConfigurationElements();
				for (IConfigurationElement element : elements) {
					if (!isDisabled(element)) {
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
					if (!isDisabled(element)) {
						if (element.getName().equals(ELMNT_TASK_EDITOR_PAGE_FACTORY)) {
							readTaskEditorPageFactory(element);
						}
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
				if (!isDisabled(element)) {
					if (element.getName().equals(ELMNT_REPOSITORY_UI)) {
						readRepositoryConnectorUi(element);
					}
				}
			}
		}

		IExtensionPoint linkProvidersExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORY_LINKS_PROVIDERS);
		IExtension[] linkProvidersExtensions = linkProvidersExtensionPoint.getExtensions();
		for (IExtension linkProvidersExtension : linkProvidersExtensions) {
			IConfigurationElement[] elements = linkProvidersExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!isDisabled(element)) {
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
				if (!isDisabled(element)) {
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
				if (!isDisabled(element)) {
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

	private static void readRepositoryConnectorUi(IConfigurationElement element) {
		try {
			Object connectorUiObject = element.createExecutableExtension(ATTR_CLASS);
			if (connectorUiObject instanceof AbstractRepositoryConnectorUi) {
				AbstractRepositoryConnectorUi connectorUi = (AbstractRepositoryConnectorUi) connectorUiObject;
				if (TasksUiPlugin.getConnector(connectorUi.getConnectorKind()) != null) {
					TasksUiPlugin.getDefault().addRepositoryConnectorUi(connectorUi);

					String iconPath = element.getAttribute(ATTR_BRANDING_ICON);
					if (iconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
								element.getContributor().getName(), iconPath);
						if (descriptor != null) {
							TasksUiPlugin.getDefault().addBrandingIcon(connectorUi.getConnectorKind(),
									CommonImages.getImage(descriptor));
						}
					}
					String overlayIconPath = element.getAttribute(ATTR_OVERLAY_ICON);
					if (overlayIconPath != null) {
						ImageDescriptor descriptor = AbstractUIPlugin.imageDescriptorFromPlugin(
								element.getContributor().getName(), overlayIconPath);
						if (descriptor != null) {
							TasksUiPlugin.getDefault().addOverlayIcon(connectorUi.getConnectorKind(), descriptor);
						}
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
				&& TasksUi.getRepositoryManager().getRepositoryConnector(repKind) != null) {
			RepositoryTemplate template = new RepositoryTemplate(label, serverUrl, encoding, version, newTaskUrl,
					taskPrefix, taskQueryUrl, newAccountUrl, anonymous, addAuto);
			TasksUiPlugin.getRepositoryTemplateManager().addTemplate(repKind, template);

			for (IConfigurationElement configElement : element.getChildren()) {
				String name = configElement.getAttribute("name"); //$NON-NLS-1$
				String value = configElement.getAttribute("value"); //$NON-NLS-1$
				if (name != null && !name.equals("") && value != null) { //$NON-NLS-1$
					template.addAttribute(name, value);
				}
			}
		} else {
			// TODO change error message to include hints about the cause of the error 
			StatusHandler.log(new Status(
					IStatus.ERROR,
					TasksUiPlugin.ID_PLUGIN,
					"Could not load repository template extension contributed by " + element.getNamespaceIdentifier() + " with connectorKind " + repKind)); //$NON-NLS-1$ //$NON-NLS-2$
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

	public static AbstractTaskActivityMonitor loadTaskActivityMonitor() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry.getExtensionPoint(ITasksCoreConstants.ID_PLUGIN
				+ ".activityMonitor"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("activityMonitor".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (object instanceof AbstractTaskActivityMonitor) {
							return (AbstractTaskActivityMonitor) object;
						} else {
							StatusHandler.log(new Status(
									IStatus.ERROR,
									ITasksCoreConstants.ID_PLUGIN,
									NLS.bind(
											"Task activity monitor ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
											object.getClass().getCanonicalName(), element.getContributor().getName())));
						}
					} catch (Throwable e) {
						StatusHandler.log(new Status(
								IStatus.ERROR,
								ITasksCoreConstants.ID_PLUGIN,
								NLS.bind(
										"Task activity monitor failed to load for extension contributed by {0}", element.getContributor().getName()), e)); //$NON-NLS-1$
					}
				}
			}
		}
		return new DefaultTaskActivityMonitor();
	}

	public static AbstractTaskContextStore loadTaskContextStore() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint connectorsExtensionPoint = registry.getExtensionPoint(ITasksCoreConstants.ID_PLUGIN
				+ ".contextStore"); //$NON-NLS-1$
		IExtension[] extensions = connectorsExtensionPoint.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] elements = extension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if ("contextStore".equals(element.getName())) { //$NON-NLS-1$
					try {
						Object object = element.createExecutableExtension("class"); //$NON-NLS-1$
						if (object instanceof AbstractTaskContextStore) {
							return (AbstractTaskContextStore) object;
						} else {
							StatusHandler.log(new Status(
									IStatus.ERROR,
									ITasksCoreConstants.ID_PLUGIN,
									NLS.bind(
											"Task context store ''{0}'' does not extend expected class for extension contributed by {1}", //$NON-NLS-1$
											object.getClass().getCanonicalName(), element.getContributor().getName())));
						}
					} catch (Throwable e) {
						StatusHandler.log(new Status(
								IStatus.ERROR,
								ITasksCoreConstants.ID_PLUGIN,
								NLS.bind(
										"Task context store failed to load for extension contributed by {0}", element.getContributor().getName()), e)); //$NON-NLS-1$
					}
				}
			}
		}
		return new DefaultTaskContextStore();
	}

	private static boolean isDisabled(IConfigurationElement element) {
		return disabledContributors.contains(element.getContributor().getName());
	}

}
