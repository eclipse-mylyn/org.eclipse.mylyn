/*******************************************************************************
 * Copyright (c) 2013, 2014 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     (Much of original functionality has been factored out, see new classes for 
 *     relevant contributor credits.) 
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import org.eclipse.mylyn.internal.gerrit.ui.factories.PatchSetUiFactoryProvider;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewSetSection;

/**
 * Displays patch sets within sub-sections, supporting lazy loading of each patch set.
 * 
 * @author Miles Parker
 */
public class PatchSetSection extends ReviewSetSection {

	public PatchSetSection() {
		setPartName(Messages.PatchSetSection_Patch_Sets);
	}

	@Override
	protected PatchSetUiFactoryProvider getUiFactoryProvider() {
		return new PatchSetUiFactoryProvider();
	}
}
