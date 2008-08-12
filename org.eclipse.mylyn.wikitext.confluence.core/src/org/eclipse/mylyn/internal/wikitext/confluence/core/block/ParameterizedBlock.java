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
package org.eclipse.mylyn.internal.wikitext.confluence.core.block;

import org.eclipse.mylyn.wikitext.core.parser.markup.Block;

/**
 * 
 * 
 * @author David Green
 */
public abstract class ParameterizedBlock extends Block {

	public void setOptions(String options) {
		if (options == null) {
			return;
		}
		String[] opts = options.split("\\s*\\|\\s*");
		for (String optionPair : opts) {
			String[] keyValue = optionPair.split("\\s*=\\s*");
			if (keyValue.length == 2) {
				String key = keyValue[0].trim();
				String value = keyValue[1].trim();
				setOption(key, value);
			}
		}
	}

	protected abstract void setOption(String key, String value);
}
