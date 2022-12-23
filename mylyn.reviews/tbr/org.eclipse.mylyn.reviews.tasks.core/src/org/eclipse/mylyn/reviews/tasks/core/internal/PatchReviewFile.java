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
package org.eclipse.mylyn.reviews.tasks.core.internal;

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
import org.eclipse.mylyn.reviews.tasks.core.IReviewFile;
import org.eclipse.mylyn.reviews.tasks.core.patch.ITargetPathStrategy;
/**
 * @author mattk
 *
 */
public class PatchReviewFile implements IReviewFile {

	private ICompareInput compareInput;
	private IFilePatch2 patch;
	private IFilePatchResult compareEditorInput;
	private PatchConfiguration configuration;

	public PatchReviewFile(IFilePatch2 filePatch) {
		this.patch = filePatch;
		configuration = new PatchConfiguration();
	}

	public ICompareInput getCompareInput() {
		if (compareInput == null) {
			IFilePatchResult patchResult = getCompareEditorInput();
			ICompareInput ci = new DiffNode(Differencer.CHANGE, null,
					new PatchCompareItem(patchResult, PatchCompareItem.Kind.ORIGINAL,
							toString()), new PatchCompareItem(patchResult,
							PatchCompareItem.Kind.PATCHED, toString()));
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
			if (patchAddsFile()) {
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

	private boolean patchAddsFile() {
		for (IHunk hunk : patch.getHunks()) {
			for (String line : hunk.getUnifiedLines()) {
				if (!line.startsWith("+")) {
					return false;
				}
			}
		}
		return true;
	}

	public String getFileName() {
		return patch.getTargetPath(configuration).lastSegment();
	}

	public boolean isNewFile() {
		return patchAddsFile();
	}

	public boolean canReview() {
		return sourceFileExists() || patchAddsFile();

	}

	public boolean sourceFileExists() {
		IPath targetPath = patch.getTargetPath(configuration);

		for (ITargetPathStrategy strategy : ReviewsUtil
				.getPathFindingStrategies()) {

			if (strategy.matches(targetPath)) {
				return true;
			}
		}
		return false;
	}

}
