/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
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

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * @author David Green
 */
public class CssPartitioner extends FastPartitioner {

	public CssPartitioner() {
		super(new CssPartitionScanner(),
				new String[] { CssPartitionScanner.CONTENT_TYPE_BLOCK, CssPartitionScanner.CONTENT_TYPE_COMMENT });
	}

}
