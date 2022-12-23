/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * Provides an simple implementation of {@link ISelectionProvider} that propagates selection events to registered
 * listeners.
 * 
 * @author Steffen Pingel
 * @since 3.7
 */
public class SelectionProviderAdapter extends EventManager implements ISelectionProvider, ISelectionChangedListener {

	private ISelection selection;

	/**
	 * Constructs a <code>SelectionProviderAdapter</code> and initializes the selection to <code>selection</code>.
	 * 
	 * @param selection
	 *            the initial selection
	 * @see #setSelection(ISelection)
	 */
	public SelectionProviderAdapter(ISelection selection) {
		setSelection(selection);
	}

	/**
	 * Constructs a <code>SelectionProviderAdapter</code> with a <code>null</code> selection.
	 */
	public SelectionProviderAdapter() {
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		addListenerObject(listener);
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		removeListenerObject(listener);
	}

	public void selectionChanged(final SelectionChangedEvent event) {
		this.selection = event.getSelection();
		Object[] listeners = getListeners();
		for (Object listener2 : listeners) {
			final ISelectionChangedListener listener = (ISelectionChangedListener) listener2;
			SafeRunner.run(new SafeRunnable() {
				public void run() {
					listener.selectionChanged(event);
				}
			});
		}
	}

	public void setSelection(ISelection selection) {
		selectionChanged(new SelectionChangedEvent(this, selection));
	}

}
