/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.core;

/**
 * NOTE: not used in current Mylyn distribution, likely to change for 3.0.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public interface IDegreeOfSeparation {

	String getLabel();

	int getDegree();

}
