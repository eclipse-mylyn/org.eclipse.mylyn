/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.core.parser.builder;

/**
 * A strategy for deciding when to split content into multiple files based on headings.
 * 
 * @author David Green
 */
public abstract class SplittingStrategy {

	public abstract void heading(int level, String id, String label);

	public abstract boolean isSplit();

	public abstract String getSplitTarget();

}
