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
package org.eclipse.mylyn.reviews.ui.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.patch.IFilePatch2;
import org.eclipse.compare.patch.PatchConfiguration;
import org.eclipse.compare.patch.PatchParser;
import org.eclipse.compare.patch.ReaderCreator;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.ReviewDiffModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/*
 * @author Kilian Matt
 */
public class ReviewTaskEditorInput implements IEditorInput {

	private Review review;

	public ReviewTaskEditorInput(Review review) {
		this.review = review;
	}

	public Review getReview() {
		return review;
	}

	public boolean exists() {
		return false;
	}

	public ImageDescriptor getImageDescriptor() {
		return Images.SMALL_ICON;
	}

	public String getName() {
		// TODO
		return Messages.ReviewTaskEditorInput_New_Review;//"Review of" + model.getTask().getTaskKey() + " " +
							// model.getTask().toString();
	}

	public IPersistableElement getPersistable() {
		return null;
	}

	public String getToolTipText() {
		return Messages.NewReviewTaskEditorInput_Tooltip;
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		return null;
	}

	public List<ReviewDiffModel> getScope() {
		try {

			IFilePatch2[] patches = PatchParser.parsePatch(new ReaderCreator() {

				@Override
				public Reader createReader() throws CoreException {
					return new InputStreamReader(new ByteArrayInputStream(
							((Patch) review.getScope().get(0)).getContents()
									.getBytes()));
				}
			});
			List<ReviewDiffModel> model = new ArrayList<ReviewDiffModel>();
			for (int i = 0; i < patches.length; i++) {
				final PatchConfiguration configuration = new PatchConfiguration();

				final IFilePatch2 currentPatch = patches[i];
				model.add(new ReviewDiffModel(currentPatch, configuration));
			}
			return model;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

}
