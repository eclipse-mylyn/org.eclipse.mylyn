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

package org.eclipse.mylyn.context.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: generalize and coupling to java.io.File
 * 
 * @author Mik Kersten
 */
public abstract class AbstractContextStore {

	private List<IContextStoreListener> listeners = new ArrayList<IContextStoreListener>();
	
	public abstract void init();
		
	/**
	 * @return 	a directory that can be written to.
	 */
	public abstract File getRootDirectory();
	
	public abstract File getContextDirectory();
	
	public void contextStoreMoved() {
		init();
		for (IContextStoreListener listener : listeners) {
			listener.contextStoreMoved();
		}
	}
	
	public void addListener(IContextStoreListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IContextStoreListener listener) {
		listeners.remove(listener);
	}

}
