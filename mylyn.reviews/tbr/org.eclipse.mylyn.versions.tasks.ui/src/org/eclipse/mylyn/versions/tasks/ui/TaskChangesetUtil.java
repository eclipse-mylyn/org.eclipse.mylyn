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
package org.eclipse.mylyn.versions.tasks.ui;

import java.util.List;

import org.eclipse.mylyn.internal.versions.tasks.ui.InternalExtensionPointLoader;

/**
 *
 * @author Kilian Matt
 *
 */
public class TaskChangesetUtil {

	private static List<AbstractChangesetMappingProvider> providers;

	public static List<AbstractChangesetMappingProvider> getMappingProviders() {
		if (providers != null)
			return providers;

		return providers = InternalExtensionPointLoader.loadMappingProviders();
	}

}
