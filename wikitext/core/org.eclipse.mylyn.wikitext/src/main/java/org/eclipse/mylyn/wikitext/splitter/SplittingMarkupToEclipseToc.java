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
package org.eclipse.mylyn.wikitext.splitter;

import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.parser.util.MarkupToEclipseToc;

/**
 * @author David Green
 * @since 3.0
 */
public class SplittingMarkupToEclipseToc extends MarkupToEclipseToc {
	@Override
	protected String computeFile(OutlineItem item) {
		if (item instanceof SplitOutlineItem) {
			String target = ((SplitOutlineItem) item).getSplitTarget();
			if (target != null) {
				return target;
			}
		}
		return super.computeFile(item);
	}
}
