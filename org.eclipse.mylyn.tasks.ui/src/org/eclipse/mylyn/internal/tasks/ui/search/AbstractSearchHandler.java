/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
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

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * An abstract search handler, that enables implementors to augment the default task list filtering capabilities.
 * 
 * @author David Green
 */
public abstract class AbstractSearchHandler {

	public interface IFilterChangeListener {
		public void filterChanged();
	}

	private final List<IFilterChangeListener> listeners = new ArrayList<IFilterChangeListener>();

	public void addFilterChangeListener(IFilterChangeListener listener) {
		listeners.add(listener);
	}

	public void removeFilterChangeListener(IFilterChangeListener listener) {
		listeners.remove(listener);
	}

	protected void fireFilterChanged() {
		if (listeners.isEmpty()) {
			return;
		}
		for (IFilterChangeListener listener : listeners.toArray(new IFilterChangeListener[listeners.size()])) {
			listener.filterChanged();
		}
	}

	/**
	 * Called when UI is being created for the tasks list, gives the search handler an opportunity to contribute
	 * additional search controls.
	 * 
	 * @param container
	 *            the container in which the search controls should be created
	 * @return the composite, or null if there are no additional controls
	 */
	public abstract Composite createSearchComposite(Composite container);

	/**
	 * Creates a filter for use with the task list.
	 * 
	 * @return the filter, must not return null.
	 */
	public abstract PatternFilter createFilter();

	/**
	 * Disposes of any resources that should be explicitly released when no longer needed. After calling this method,
	 * the search handler is no longer usable and its behaviour is undefined.
	 */
	public abstract void dispose();

	/**
	 * Adapts the text search control. The default implementation does nothing.
	 */
	public void adaptTextSearchControl(Text textControl) {

	}
}
