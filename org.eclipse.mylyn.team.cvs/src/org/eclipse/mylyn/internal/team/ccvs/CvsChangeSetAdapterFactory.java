/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.team.ccvs;

import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.team.internal.ccvs.core.mapping.ChangeSetResourceMapping;
import org.eclipse.team.internal.core.subscribers.ActiveChangeSet;

/**
 * @since 3.0
 */
public class CvsChangeSetAdapterFactory implements IAdapterFactory {

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adaptableObject instanceof ActiveChangeSet && adapterType == ResourceMapping.class) {
			ActiveChangeSet cs = (ActiveChangeSet) adaptableObject;
			return new ChangeSetResourceMapping(cs);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Class[] getAdapterList() {
		return new Class[] { ResourceMapping.class };
	}

}
