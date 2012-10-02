/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import java.util.List;

import org.eclipse.mylyn.commons.core.ExtensionPointReader;
import org.eclipse.mylyn.versions.tasks.ui.AbstractChangesetMappingProvider;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsContributionAction;

/**
 * @author Kilian Matt
 */
public class InternalExtensionPointLoader {

	private static final String MAPPER_EXTENSION_POINT = "changesetmapping";
	private static final String ACTION_CONTRIBUTION_EXTENSION_POINT = "actionContributions";

	public static List<ITaskVersionsContributionAction> getActionContributions() {
		return loadActionContributions();
	}

	public synchronized static List<AbstractChangesetMappingProvider> loadMappingProviders() {
		return loadClassExtensions(AbstractChangesetMappingProvider.class,
				MAPPER_EXTENSION_POINT,"changesetMapper");
	}

	public synchronized static List<ITaskVersionsContributionAction> loadActionContributions() {
		return loadClassExtensions(ITaskVersionsContributionAction.class,
				ACTION_CONTRIBUTION_EXTENSION_POINT,"actionContribution");
	}

	public synchronized static <T> List<T> loadClassExtensions(Class<T> clazz,
			String extensionPoint, String elementId) {
		ExtensionPointReader<T> extensionPointReader = new ExtensionPointReader<T>(TaskVersionsUiPlugin.PLUGIN_ID, extensionPoint, elementId, clazz);
		extensionPointReader.read();
		return extensionPointReader.getItems();
	}

}
