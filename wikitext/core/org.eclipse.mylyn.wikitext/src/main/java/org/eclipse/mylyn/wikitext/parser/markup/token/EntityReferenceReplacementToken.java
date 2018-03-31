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
package org.eclipse.mylyn.wikitext.parser.markup.token;

import java.util.regex.Pattern;

/**
 * @author David Green
 * @since 3.0
 */
public class EntityReferenceReplacementToken extends PatternEntityReferenceReplacementToken {

	public EntityReferenceReplacementToken(String token, String replacement) {
		super("(" + Pattern.quote(token) + ")", replacement); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
