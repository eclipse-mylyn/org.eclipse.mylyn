/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.Date;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;

/**
 * @author Shawn Minto
 */
public class ResourceModifiedDateExclusionStrategy implements IResourceExclusionStrategy {

	// used so we can compare last modified dates with the last time that the context was activated
	private final AbstractContextListener contextActivationListener = new AbstractContextListener() {

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
			case ACTIVATED:
				lastActivatedDate = new Date();
				break;
			case DEACTIVATED:
				lastActivatedDate = null;
				break;
			}
		};
	};

	private transient Date lastActivatedDate = null;

	public void dispose() {
		ContextCore.getContextManager().removeListener(contextActivationListener);
	}

	public void init() {
		ContextCore.getContextManager().addListener(contextActivationListener);
		if (ContextCore.getContextManager().isContextActive()) {
			lastActivatedDate = new Date();
		}
	}

	public void update() {
		// ignore
	}

	public boolean isEnabled() {
		//XXX add a preference for this!
		return false;
	}

	public boolean isExcluded(IResource resource) {
		return isEnabled() && !wasModifiedAfter(resource, lastActivatedDate);
	}

	/**
	 * Public for testing
	 */
	public static boolean wasModifiedAfter(IResource resource, Date date) {
		if (date == null) {
			return true;
		}
		long modificationStamp = resource.getLocalTimeStamp();
		if (modificationStamp > 0 && modificationStamp != IResource.NULL_STAMP) {
			Date resourceDate = new Date(modificationStamp);
			return resourceDate.after(date);
		}

		return false;
	}

}
