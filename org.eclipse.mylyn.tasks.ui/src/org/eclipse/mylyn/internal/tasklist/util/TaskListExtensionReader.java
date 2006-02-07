/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.tasklist.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.AbstractRepositoryClient;
import org.eclipse.mylar.internal.tasklist.ITaskListExternalizer;
import org.eclipse.mylar.internal.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.internal.tasklist.ui.ITaskEditorFactory;
import org.eclipse.mylar.internal.tasklist.ui.IDynamicSubMenuContributor;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class TaskListExtensionReader {

	public static final String EXTENSION_REPOSITORIES = "org.eclipse.mylar.tasklist.repositories";

	public static final String ELMNT_REPOSITORY_CLIENT = "repositoryClient";

	public static final String ELMNT_TYPE = "type";

	public static final String ELMNT_QUERY_PAGE = "queryPage";

	public static final String ELMNT_SETTINGS_PAGE = "settingsPage";

	public static final String EXTENSION_TASK_CONTRIBUTOR = "org.eclipse.mylar.tasklist.providers";

	public static final String ELMNT_TASK_HANDLER = "taskHandler";

	public static final String ATTR_EXTERNALIZER_CLASS = "externalizerClass";

	public static final String ATTR_ACTION_CONTRIBUTOR_CLASS = "taskHandlerClass";

	// public static final String TASK_LISTENER_ELEMENT = "taskListener";

	// public static final String TASK_LISTENER_CLASS_ID = "class";

	public static final String DYNAMIC_POPUP_ELEMENT = "dynamicPopupMenu";

	public static final String ATTR_CLASS = "class";

	public static final String EXTENSION_EDITORS = "org.eclipse.mylar.tasklist.editors";

	public static final String EDITOR_FACTORY = "editorFactory";

	public static final String EDITOR_FACTORY_CLASS = "class";

	private static boolean extensionsRead = false;

	public static void initExtensions(TaskListWriter writer) {
		// code from "contributing to eclipse" with modifications for deprecated
		// code
		List<ITaskListExternalizer> externalizers = new ArrayList<ITaskListExternalizer>();
		if (!extensionsRead) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			
			// HACK: has to be read first
			IExtensionPoint repositoriesExtensionPoint = registry.getExtensionPoint(EXTENSION_REPOSITORIES);
			IExtension[] repositoryExtensions = repositoriesExtensionPoint.getExtensions();
			for (int i = 0; i < repositoryExtensions.length; i++) {
				IConfigurationElement[] elements = repositoryExtensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elements[j].getName().compareTo(ELMNT_REPOSITORY_CLIENT) == 0) {
						readRepositoryClient(elements[j]);
					}
				}
			}
			
			IExtensionPoint extensionPoint = registry.getExtensionPoint(EXTENSION_TASK_CONTRIBUTOR);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elements[j].getName().compareTo(ELMNT_TASK_HANDLER) == 0) {
						readExternalizer(elements[j], externalizers);
					} else if (elements[j].getName().compareTo(DYNAMIC_POPUP_ELEMENT) == 0) {
						readDynamicPopupContributor(elements[j]);
					}
				}
			}
//			for (int i = 0; i < extensions.length; i++) {
//				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
//				for (int j = 0; j < elements.length; j++) {
//					if (elements[j].getName().compareTo(ELMNT_REPOSITORY_CLIENT) == 0) {
//						readRepositoryClient(elements[j]);
//					}
//				}
//			}
			
			IExtensionPoint editorsExtensionPoint = registry.getExtensionPoint(EXTENSION_EDITORS);
			IExtension[] editors = editorsExtensionPoint.getExtensions();
			for (int i = 0; i < editors.length; i++) {
				IConfigurationElement[] elements = editors[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					if (elements[j].getName().compareTo(EDITOR_FACTORY) == 0) {
						readEditorFactory(elements[j]);
					}
				}
			}
			writer.setDelegateExternalizers(externalizers);
			extensionsRead = true;
		}
	}

	private static void readEditorFactory(IConfigurationElement element) {
		try {
			Object editor = element.createExecutableExtension(EDITOR_FACTORY_CLASS);
			if (editor instanceof ITaskEditorFactory) {
				MylarTaskListPlugin.getDefault().addContextEditor((ITaskEditorFactory) editor);
			} else {
				MylarStatusHandler.log("Could not load editor: " + editor.getClass().getCanonicalName()
						+ " must implement " + ITaskEditorFactory.class.getCanonicalName(), null);
			}
		} catch (CoreException e) {
			MylarStatusHandler.log(e, "Could not load tasklist listener extension");
		}
	}

	private static void readRepositoryClient(IConfigurationElement element) {
		try {
			Object type = element.getAttribute(ELMNT_TYPE);
			Object repository = element.createExecutableExtension(ATTR_CLASS);
			if (repository instanceof AbstractRepositoryClient && type != null) {
				MylarTaskListPlugin.getRepositoryManager().addRepositoryClient((AbstractRepositoryClient) repository);
			} else {
				MylarStatusHandler.log("could not not load extension: " + repository, null);
			}
		} catch (CoreException e) {
			MylarStatusHandler.log(e, "Could not load tasklist listener extension");
		}
	}

	private static void readDynamicPopupContributor(IConfigurationElement element) {
		try {
			Object dynamicPopupContributor = element.createExecutableExtension(ATTR_CLASS);
			if (dynamicPopupContributor instanceof IDynamicSubMenuContributor) {
				MylarTaskListPlugin.getDefault().addDynamicPopupContributor(
						(IDynamicSubMenuContributor) dynamicPopupContributor);
			} else {
				MylarStatusHandler.log("Could not load dyanmic popup menu: "
						+ dynamicPopupContributor.getClass().getCanonicalName() + " must implement "
						+ IDynamicSubMenuContributor.class.getCanonicalName(), null);
			}
		} catch (CoreException e) {
			MylarStatusHandler.log(e, "Could not load dynamic popup extension");
		}
	}

	private static void readExternalizer(IConfigurationElement element, List<ITaskListExternalizer> externalizers) {
		try {
			Object externalizerObject = element.createExecutableExtension(ATTR_EXTERNALIZER_CLASS);
			if (externalizerObject instanceof ITaskListExternalizer) {
				ITaskListExternalizer externalizer = (ITaskListExternalizer)externalizerObject;
				externalizers.add((ITaskListExternalizer) externalizer);
			} else {
				MylarStatusHandler.log("Could not load externalizer: " + externalizerObject.getClass().getCanonicalName()
						+ " must implement " + ITaskListExternalizer.class.getCanonicalName(), null);
			}

//			Object taskHandler = element.createExecutableExtension(ATTR_ACTION_CONTRIBUTOR_CLASS);
//			if (taskHandler instanceof ITaskHandler) {
//				MylarTaskListPlugin.getDefault().addTaskHandler((ITaskHandler) taskHandler);
//			} else {
//				MylarStatusHandler.log("Could not load contributor: " + taskHandler.getClass().getCanonicalName()
//						+ " must implement " + ITaskHandler.class.getCanonicalName(), null);
//			}
		} catch (CoreException e) {
			MylarStatusHandler.log(e, "Could not load task handler extension");
		}
	}
}
