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

package org.eclipse.mylar.internal.context.ui.actions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.ui.InterestFilter;
import org.eclipse.mylar.internal.tasks.ui.TasksUiImages;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.internal.navigator.NavigatorContentServiceContentProvider;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * 
 * @author Mik Kersten
 */
public class RemoveFromContextAction extends BaseSelectionListenerAction {

	public static final String ID = "org.eclipse.mylar.context.ui.actions.delete";

	private CommonViewer commonViewer;
	
	private InterestFilter interestFilter;
	
	public RemoveFromContextAction(CommonViewer commonViewer, InterestFilter interestFilter) {
		super("Remove from Context");
		setId(ID);
		setImageDescriptor(TasksUiImages.REMOVE);
		this.commonViewer = commonViewer;
		this.interestFilter = interestFilter;
	}

	@Override
	public void run() {
		Set<IMylarElement> toRemove = new HashSet<IMylarElement>();
		
		for (Iterator<?> iterator = super.getStructuredSelection().iterator(); iterator.hasNext();) {
			Object object = iterator.next();
			collectChildren(toRemove, object);
		}
		
		for (IMylarElement element : toRemove) {
			ContextCorePlugin.getContextManager().delete(element);
		}
		commonViewer.refresh();
 	}

	private void collectChildren(Set<IMylarElement> toRemove, Object object) {
		IMylarElement element = resolveElement(object);
		if (element != null) {
			toRemove.add(element);
		}

		Object[] children = ((NavigatorContentServiceContentProvider)commonViewer.getContentProvider()).getChildren(object);
		for (int i = 0; i < children.length; i++) {
			Object child = children[i];
			if (interestFilter.select(commonViewer, object, child)) {
				collectChildren(toRemove, child);
			}
		}
	}

	private IMylarElement resolveElement(Object object) {
		IMylarElement element = null;
		if (object instanceof IMylarElement) {
			element = (IMylarElement)object;
		} else {
			AbstractContextStructureBridge bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
			if (bridge.getContentType() == null) {
				// try to resolve the resource
				if (object instanceof IAdaptable) {
					Object adapted = ((IAdaptable) object).getAdapter(IResource.class);
					if (adapted instanceof IResource) {
						object = adapted;
					}
					bridge = ContextCorePlugin.getDefault().getStructureBridge(object);
				} 
			}

			if (!object.getClass().getName().equals(Object.class.getCanonicalName())) {
				String handle = bridge.getHandleIdentifier(object);
				element = ContextCorePlugin.getContextManager().getElement(handle);
			} 
		}
		return element;
	}
}
