/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

	public abstract String getLabel();

	public abstract int getDegree();

}
