/*******************************************************************************
 * Copyright (c) 2009, 2015 Atlassian and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Sebastien Dubois (Ericsson) - Improvements for bug 400266
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.ui.ReviewBehavior;
import org.eclipse.osgi.util.NLS;

/**
 * @author Steffen Pingel
 * @author Sebastien Dubois
 * @author Miles Parker
 */
public class FileItemCompareEditorInput extends ReviewItemCompareEditorInput {

	private final IFileItem file;

	public FileItemCompareEditorInput(CompareConfiguration configuration, IFileItem file, ReviewBehavior behavior) {
		super(configuration, behavior);
		this.file = file;
		setTitle(NLS.bind(Messages.FileItemCompareEditorInput_Compare_X_Y_and_Z,
				file.getName(), file.getBase().getDescription(), file.getTarget().getDescription()));
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		FileItemNode node = new FileItemNode(behavior, file, monitor);
		getCompareConfiguration().setLabelProvider(node, node.getLabelProvider());
		return node;
	}

	public String getFileItemId() {
		return file.getId();
	}
}