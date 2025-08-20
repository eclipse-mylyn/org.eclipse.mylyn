/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.ui.SelectionProviderAdapter} instead
 */
@Deprecated
public class SelectionProviderAdapter extends EventManager implements ISelectionProvider {

	private ISelection selection;

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		addListenerObject(listener);
	}

	@Override
	public ISelection getSelection() {
		return selection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		removeListenerObject(listener);
	}

	protected void selectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = getListeners();
		for (Object listener2 : listeners) {
			final ISelectionChangedListener listener = (ISelectionChangedListener) listener2;
			SafeRunner.run(new SafeRunnable() {
				@Override
				public void run() {
					listener.selectionChanged(event);
				}
			});
		}
	}

	@Override
	public void setSelection(ISelection selection) {
		this.selection = selection;
		selectionChanged(new SelectionChangedEvent(this, selection));
	}

}
