/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * An adapter factory for {@link OutlineItem} that can adapt it to {@link IWorkbenchAdapter}.
 *
 * @author dgreen
 */
public class OutlineItemAdapterFactory implements IAdapterFactory {

	private static final Class<?>[] ADAPTER_LIST = { IWorkbenchAdapter.class };

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IWorkbenchAdapter.class && adaptableObject instanceof OutlineItem) {
			return (T) OutlineItemWorkbenchAdapter.instance();
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return ADAPTER_LIST;
	}

}
