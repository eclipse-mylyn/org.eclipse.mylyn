/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.confluence.internal.block;

import org.eclipse.mylyn.wikitext.confluence.internal.util.Options;
import org.eclipse.mylyn.wikitext.parser.markup.Block;

/**
 * @author David Green
 */
public abstract class ParameterizedBlock extends Block {

	public void setOptions(String options) {
		if (options == null) {
			return;
		}
		Options.parseOptions(options, new Options.Handler() {
			@Override
			public void setOption(String key, String value) {
				ParameterizedBlock.this.setOption(key, value);
			}

			@Override
			public void setOption(String key) {
				ParameterizedBlock.this.setOption(key);
			}
		});
	}

	/**
	 * Set an option that is specified without a value. The default implementation does nothing.
	 * 
	 * @param option
	 *            the option to set
	 * @see #setOption(String, String)
	 */
	protected void setOption(String option) {
	}

	/**
	 * Set an option with key and value pair
	 * 
	 * @param key
	 *            the key of the option
	 * @param value
	 *            the value of the option
	 * @see #setOption(String)
	 */
	protected abstract void setOption(String key, String value);
}
