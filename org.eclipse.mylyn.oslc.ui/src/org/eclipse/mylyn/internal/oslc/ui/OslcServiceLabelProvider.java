/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.ui;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.internal.oslc.core.OslcCreationDialogDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProviderCatalog;
import org.eclipse.mylyn.internal.oslc.ui.OslcServiceDiscoveryProvider.ServiceProviderCatalogWrapper;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Robert Elves
 */
@SuppressWarnings("restriction")
public class OslcServiceLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof OslcServiceProvider) {
			return ((OslcServiceProvider) element).getName();
		} else if (element instanceof OslcCreationDialogDescriptor) {
			return ((OslcCreationDialogDescriptor) element).getTitle();
		} else if (element instanceof OslcServiceDescriptor) {
			return ((OslcServiceDescriptor) element).getDescription();
		} else if (element instanceof ServiceProviderCatalogWrapper) {
			return this.getText(((ServiceProviderCatalogWrapper) element).getServiceObject());
		} else {
			return Messages.OslcServiceLabelProvider_Loading;
		}
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof OslcServiceProviderCatalog) {
			return CommonImages.getImage(TasksUiImages.REPOSITORIES_VIEW);
		} else if (element instanceof OslcServiceProvider) {
			return CommonImages.getImage(TasksUiImages.REPOSITORY);
		} else if (element instanceof ServiceProviderCatalogWrapper) {
			return this.getImage(((ServiceProviderCatalogWrapper) element).getServiceObject());
		}
		return null;
	}
}
