/*******************************************************************************
 * Copyright (c) 2009, 2021 David Green and others .
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
package org.eclipse.mylyn.wikitext.ui.viewer;

import org.eclipse.mylyn.wikitext.ui.viewer.AbstractTextSourceViewerConfiguration.HyperlinkDetectorDescriptorFilter;
import org.eclipse.ui.texteditor.HyperlinkDetectorDescriptor;

/**
 * A filter that filters hyperlink detectors by their descriptor {@link HyperlinkDetectorDescriptor#getId() id}.
 *
 * @author David Green
 * @since 1.1
 */
public class DefaultHyperlinkDetectorDescriptorFilter implements HyperlinkDetectorDescriptorFilter {
	private final String filteredId;

	public DefaultHyperlinkDetectorDescriptorFilter(String filteredId) {
		if (filteredId == null) {
			throw new IllegalArgumentException();
		}
		this.filteredId = filteredId;
	}

	@Override
	public boolean filter(HyperlinkDetectorDescriptor descriptor) {
		String id = descriptor.getId();
		if (filteredId.equals(id)) {
			return true;
		}
		return false;
	}
}
