/*******************************************************************************
 * Copyright (c) 2013 Ericsson and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.core.spi.remote.emf;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.spi.remote.AbstractRemoteFactoryProvider;

/**
 * Extends {@link AbstractRemoteFactoryProvider} with methods to open, save and close EMF resources.
 *
 * @author Miles Parker
 */
public abstract class AbstractRemoteEmfFactoryProvider<ERootObject extends EObject, EChildObject>
		extends AbstractRemoteFactoryProvider {

	public abstract ERootObject open();

	public abstract EChildObject open(Object id);

	public abstract void save();

	public abstract void save(EObject child);

	public abstract void close();

	public abstract void close(EObject child);
}
