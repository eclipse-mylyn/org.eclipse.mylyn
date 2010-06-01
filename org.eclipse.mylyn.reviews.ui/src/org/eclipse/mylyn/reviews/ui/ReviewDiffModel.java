/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui;

import java.io.CharArrayReader;
import java.io.Reader;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.IFilePatch2;
import org.eclipse.compare.patch.IFilePatchResult;
import org.eclipse.compare.patch.IHunk;
import org.eclipse.compare.patch.PatchConfiguration;
import org.eclipse.compare.patch.ReaderCreator;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.Differencer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.reviews.core.ITargetPathStrategy;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;

/**
 * @author Kilian Matt
 */
public class ReviewDiffModel {

	private IFilePatch2 patch;
	private PatchConfiguration configuration;
	private ICompareInput compareInput;
	private IFilePatchResult compareEditorInput = null;

	public ReviewDiffModel(IFilePatch2 currentPatch,
			PatchConfiguration configuration) {
		patch = currentPatch;
		this.configuration = configuration;
	}

	@Override
	public String toString() {
		return getFileName();
	}

	private String getFileName() {
		String string = patch.getTargetPath(configuration).lastSegment();
		return string;
	}

	public ICompareInput getCompareInput() {
		if (compareInput == null) {
			IFilePatchResult patchResult = getCompareEditorInput();
			ICompareInput ci = new DiffNode(Differencer.CHANGE, null,
					new CompareItem(patchResult, CompareItem.Kind.ORIGINAL,
							toString()), new CompareItem(patchResult,
							CompareItem.Kind.PATCHED, toString()));
			compareInput = ci;
		}
		return compareInput;

	}

	public IFilePatchResult getCompareEditorInput() {
		if (compareEditorInput == null) {
			IPath targetPath = patch.getTargetPath(configuration);

			for (ITargetPathStrategy strategy : ReviewsUtil
					.getPathFindingStrategies()) {

				if (strategy.matches(targetPath)) {
					CompareConfiguration config = new CompareConfiguration();
					config.setRightEditable(false);
					config.setLeftEditable(false);

					ReaderCreator rc = strategy.get(targetPath);
					NullProgressMonitor monitor = new NullProgressMonitor();
					IFilePatchResult result = patch.apply(rc, configuration,
							monitor);

					return compareEditorInput = result;
				}
			}
			if (patchAddsFile(patch)) {
				ReaderCreator rc = new ReaderCreator() {

					@Override
					public Reader createReader() throws CoreException {
						return new CharArrayReader(new char[0]);
					}
				};
				NullProgressMonitor monitor = new NullProgressMonitor();
				compareEditorInput = patch.apply(rc, configuration, monitor);
			}
		}
		return compareEditorInput;
	}

	private boolean patchAddsFile(IFilePatch2 patch2) {
		for (IHunk hunk : patch2.getHunks()) {
			for (String line : hunk.getUnifiedLines()) {
				if (!line.startsWith("+")) {
					return false;
				}
			}
		}
		return true;
	}

}
