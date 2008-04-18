/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.internal.team.ui.ContextChangeSet;
import org.eclipse.team.internal.ccvs.core.mapping.ChangeSetResourceMapping;
import org.eclipse.team.internal.core.subscribers.DiffChangeSet;

/**
 * @author Eugene Kuleshov
 */
public class CvsChangeSetResourceMappingAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	private static final Class[] ADAPTER_TYPES = new Class[] { ResourceMapping.class };

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object object, Class adapterType) {
		// used to bind popup menu actions in Synchronize view 
		if (ResourceMapping.class.equals(adapterType) && object instanceof ContextChangeSet) {
			return new ChangeSetResourceMapping((DiffChangeSet) object);
		}

		return null;
	}

}
