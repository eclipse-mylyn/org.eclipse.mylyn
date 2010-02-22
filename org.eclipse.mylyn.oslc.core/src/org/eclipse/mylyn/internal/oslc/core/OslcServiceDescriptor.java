/*******************************************************************************
 * Copyright (c) 2009, 2010 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.core;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.eclipse.mylyn.internal.oslc.core.cm.Messages;

/**
 * Could be considered equivalent to a repository configuration in Mylyn terminology. Model object for the OSLC Service
 * Descriptor document.
 * 
 * @see http://open-services.net/bin/view/Main/CmServiceDescriptionV1
 * @author Robert Elves
 */
public class OslcServiceDescriptor implements Serializable {

	private static final long serialVersionUID = -5981264972265788764L;

	private final Set<OslcCreationDialogDescriptor> creationDialogs;

	private final Set<OslcServiceFactory> serviceFactories;

	private final Set<OslcSelectionDialogDescriptor> selectionDialogs;

	private ServiceContributor contributor;

	private ServiceHome home;

	private String aboutUrl;

	private String simpleQueryUrl;

	private OslcServiceFactory defaultFactory;

	private OslcCreationDialogDescriptor defaultDialog;

	private String title;

	private String description;

	public OslcServiceDescriptor(String aboutUrl) {
		this.aboutUrl = aboutUrl;
		this.creationDialogs = new CopyOnWriteArraySet<OslcCreationDialogDescriptor>();
		this.serviceFactories = new CopyOnWriteArraySet<OslcServiceFactory>();
		this.selectionDialogs = new CopyOnWriteArraySet<OslcSelectionDialogDescriptor>();
	}

	public void clear() {
		this.creationDialogs.clear();
		this.serviceFactories.clear();
		this.selectionDialogs.clear();
		this.contributor = null;
		this.title = null;
		this.description = null;
		this.defaultFactory = null;
		this.simpleQueryUrl = null;
	}

	public Set<OslcCreationDialogDescriptor> getCreationDialogs() {
		return Collections.unmodifiableSet(creationDialogs);
	}

	public void addCreationDialog(OslcCreationDialogDescriptor descriptor) {
		creationDialogs.add(descriptor);
		if (descriptor.isDefault()) {
			defaultDialog = descriptor;
		}
	}

	public String getAboutUrl() {
		return aboutUrl;
	}

	public void setAboutUrl(String url) {
		this.aboutUrl = url;
	}

	public void setDefaultCreationDialog(OslcCreationDialogDescriptor defaultDialog) {
		this.defaultDialog = defaultDialog;
	}

	public OslcCreationDialogDescriptor getDefaultCreationDialog() {
		return defaultDialog;
	}

	public void setDefaultFactory(OslcServiceFactory factory) {
		this.defaultFactory = factory;
	}

	public OslcServiceFactory getDefaultFactory() {
		if (defaultFactory == null && !serviceFactories.isEmpty()) {
			return serviceFactories.iterator().next();
		}
		return defaultFactory;
	}

	public String getTitle() {
		if (title != null) {
			return title;
		} else {
			return Messages.OslcServiceDescriptor_Service_Available;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		if (description != null) {
			return description;
		} else {
			return Messages.OslcServiceDescriptor_Service_Available;
		}
	}

	public String getSimpleQueryUrl() {
		return simpleQueryUrl;
	}

	public void setSimpleQueryUrl(String simpleQueryUrl) {
		this.simpleQueryUrl = simpleQueryUrl;
	}

	public Set<OslcSelectionDialogDescriptor> getSelectionDialogs() {
		return Collections.unmodifiableSet(selectionDialogs);
	}

	public void addSelectionDialog(OslcSelectionDialogDescriptor dialog) {
		selectionDialogs.add(dialog);
	}

	public OslcSelectionDialogDescriptor getDefaultSelectionDialog() {
		for (OslcSelectionDialogDescriptor dialog : selectionDialogs) {
			if (dialog.isDefault()) {
				return dialog;
			}
		}
		return null;
	}

	public Set<OslcServiceFactory> getFactories() {
		return Collections.unmodifiableSet(serviceFactories);
	}

	public void addServiceFactory(OslcServiceFactory factory) {
		serviceFactories.add(factory);
	}

	public void setContributor(ServiceContributor contributor) {
		this.contributor = contributor;
	}

	public ServiceContributor getContributor() {
		return this.contributor;
	}

	public void setHome(ServiceHome home) {
		this.home = home;
	}

	public ServiceHome getHome() {
		return home;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aboutUrl == null) ? 0 : aboutUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof OslcServiceDescriptor)) {
			return false;
		}
		OslcServiceDescriptor other = (OslcServiceDescriptor) obj;
		if (aboutUrl == null) {
			if (other.aboutUrl != null) {
				return false;
			}
		} else if (!aboutUrl.equals(other.aboutUrl)) {
			return false;
		}
		return true;
	}

}
