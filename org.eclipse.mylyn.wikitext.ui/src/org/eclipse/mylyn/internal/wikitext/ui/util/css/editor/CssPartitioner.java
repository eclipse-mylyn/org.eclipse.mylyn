/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css.editor;

import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * 
 * @author David Green
 */
public class CssPartitioner extends FastPartitioner {

	public CssPartitioner() {
		super(new CssPartitionScanner(), new String[] { CssPartitionScanner.CONTENT_TYPE_BLOCK,
				CssPartitionScanner.CONTENT_TYPE_COMMENT });
	}

}
