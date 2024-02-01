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

package org.eclipse.mylyn.context.tests.support.search;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * @deprecated use {@link org.eclipse.mylyn.context.sdk.util.search.ISearchPluginTest} instead
 * @author Mik Kersten
 */
@Deprecated
public interface ISearchPluginTest {
	List<?> search(int dos, IInteractionElement node) throws IOException, CoreException;
}
