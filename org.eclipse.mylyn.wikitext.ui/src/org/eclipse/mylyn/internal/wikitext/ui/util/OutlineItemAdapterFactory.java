/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An adapter factory for {@link OutlineItem} that can adapt it to {@link IWorkbenchAdapter}.
 * 
 * @author dgreen
 */
public class OutlineItemAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = { IWorkbenchAdapter.class };

	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof OutlineItem) {
			return OutlineItemWorkbenchAdapter.instance();
		}
		return null;
	}

	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
