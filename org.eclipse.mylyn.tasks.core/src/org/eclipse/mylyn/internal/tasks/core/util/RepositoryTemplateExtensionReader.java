/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core.util;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.internal.tasks.core.RepositoryTemplateManager;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.RepositoryTemplate;
import org.eclipse.osgi.util.NLS;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Rob Elves
 */
public class RepositoryTemplateExtensionReader {

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

	private final IRepositoryManager repositoryManager;

	private final RepositoryTemplateManager templateManager;

	public RepositoryTemplateExtensionReader(IRepositoryManager repositoryManager,
			RepositoryTemplateManager templateManager) {
		this.repositoryManager = repositoryManager;
		this.templateManager = templateManager;

	}

	public void loadExtensions(ContributorBlackList blackList) {
		MultiStatus result = new MultiStatus(ITasksCoreConstants.ID_PLUGIN, 0,
				"Unexpected error while loading repository template extensions", null); //$NON-NLS-1$

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint templatesExtensionPoint = registry.getExtensionPoint(EXTENSION_TEMPLATES);
		IExtension[] templateExtensions = templatesExtensionPoint.getExtensions();
		for (IExtension templateExtension : templateExtensions) {
			IConfigurationElement[] elements = templateExtension.getConfigurationElements();
			for (IConfigurationElement element : elements) {
				if (!blackList.isDisabled(element)) {
					if (element.getName().equals(EXTENSION_TMPL_REPOSITORY)) {
						IStatus status = readRepositoryTemplate(element);
						if (!status.isOK()) {
							result.add(status);
						}
					}
				}
			}
		}

		if (!result.isOK()) {
			StatusHandler.log(result);
		}
	}

	private IStatus readRepositoryTemplate(IConfigurationElement element) {
		String label = element.getAttribute(ELMNT_TMPL_LABEL);
		String serverUrl = element.getAttribute(ELMNT_TMPL_URLREPOSITORY);
		String repKind = element.getAttribute(ELMNT_TMPL_REPOSITORYKIND);
		String version = element.getAttribute(ELMNT_TMPL_VERSION);
		String newTaskUrl = element.getAttribute(ELMNT_TMPL_URLNEWTASK);
		String taskPrefix = element.getAttribute(ELMNT_TMPL_URLTASK);
		String taskQueryUrl = element.getAttribute(ELMNT_TMPL_URLTASKQUERY);
		String newAccountUrl = element.getAttribute(ELMNT_TMPL_NEWACCOUNTURL);
		String encoding = element.getAttribute(ELMNT_TMPL_CHARACTERENCODING);
		boolean addAuto = Boolean.parseBoolean(element.getAttribute(ELMNT_TMPL_ADDAUTO));
		boolean anonymous = Boolean.parseBoolean(element.getAttribute(ELMNT_TMPL_ANONYMOUS));

		if (serverUrl != null && label != null && repKind != null
				&& repositoryManager.getRepositoryConnector(repKind) != null) {
			RepositoryTemplate template = new RepositoryTemplate(label, serverUrl, encoding, version, newTaskUrl,
					taskPrefix, taskQueryUrl, newAccountUrl, anonymous, addAuto);
			for (IConfigurationElement configElement : element.getChildren()) {
				String name = configElement.getAttribute("name"); //$NON-NLS-1$
				String value = configElement.getAttribute("value"); //$NON-NLS-1$
				if (name != null && name.length() > 0 && value != null) {
					template.addAttribute(name, value);
				}
			}
			templateManager.addTemplate(repKind, template);
			return Status.OK_STATUS;
		} else {
			return new Status(
					IStatus.ERROR,
					ITasksCoreConstants.ID_PLUGIN,
					NLS.bind(
							"Could not load repository template extension contributed by ''{0}'' with connectorKind ''{1}''", element.getNamespaceIdentifier(), repKind)); //$NON-NLS-1$
		}
	}

}
