/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.resources.ui;

import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Mik Kersten
 */
public final class ResourcesUi {

	public static void addResourceToContext(final Set<IResource> resources, final InteractionEvent.Kind interactionKind) {
		ResourcesUiBridgePlugin.getInterestUpdater().addResourceToContext(resources, interactionKind);
	}

}
