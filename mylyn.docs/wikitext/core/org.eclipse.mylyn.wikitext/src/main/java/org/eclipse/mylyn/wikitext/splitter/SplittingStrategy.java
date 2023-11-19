/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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
package org.eclipse.mylyn.wikitext.splitter;

/**
 * A strategy for deciding when to split content into multiple files based on headings.
 *
 * @author David Green
 * @since 3.0
 */
public abstract class SplittingStrategy {

	public abstract void heading(int level, String id, String label);

	public abstract boolean isSplit();

	public abstract String getSplitTarget();

}
