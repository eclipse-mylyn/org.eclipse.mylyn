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

package org.eclipse.mylyn.internal.tasks.core.externalization;

/**
 * @author Rob Elves
 */
public interface IExternalizationContext {

	public enum Kind {
		SAVE, SNAPSHOT, LOAD;
	}

	public abstract Kind getKind();

	public abstract String getRootPath();
}
