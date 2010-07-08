/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class DelegateReviewTaskEditorPart extends AbstractTaskEditorPart {
	public static final String ID_PART_REVIEW = "org.eclipse.mylyn.reviews.ui.editors.DelegateReviewTaskEditorPart"; //$NON-NLS-1$
	
	public DelegateReviewTaskEditorPart() {
		setPartName("Review");
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {

		Section section = createSection(parent, toolkit, true);
		EditorUtil.setTitleBarForeground(section,	
				toolkit.getColors().getColor(IFormColors.TITLE));

		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		section.setLayout(gl);
		section.setLayoutData(gd);

		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 5;
		composite.setLayout(layout);

		Composite resultComposite = toolkit.createComposite(composite);
		resultComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
		resultComposite.setLayout(new GridLayout(2, false));
		final ComboViewer ratingList = new ComboViewer(resultComposite,
				SWT.READ_ONLY | SWT.BORDER | SWT.FLAT);
		ratingList.setContentProvider(ArrayContentProvider.getInstance());

		ratingList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				// TODO externalize string
				return ((Rating) element).getName();
			}

			@Override
			public Image getImage(Object element) {
				Rating rating = ((Rating) element);
				switch (rating) {
				case FAILED:
					return Images.REVIEW_RESULT_FAILED.createImage();
				case NONE:
					return Images.REVIEW_RESULT_NONE.createImage();
				case PASSED:
					return Images.REVIEW_RESULT_PASSED.createImage();
				case WARNING:
					return Images.REVIEW_RESULT_WARNING.createImage();
				}
				return super.getImage(element);
			}
		});
		ratingList.setInput(Rating.VALUES);
		ratingList.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Text commentText = toolkit.createText(resultComposite, "",
				SWT.BORDER|SWT.MULTI);
		gd=new GridData(SWT.FILL, SWT.DEFAULT, true,
				false);
		gd.heightHint=30;
		commentText.setLayoutData(gd);

		Review review = getTaskEditorPage().getReview();
		if (review.getResult() != null) {
			Rating rating = review.getResult().getRating();
			ratingList.setSelection(new StructuredSelection(rating));
			commentText.setText(review.getResult().getText());
		}
		commentText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				getTaskEditorPage().setDirty();
				Review review = getTaskEditorPage().getReview();
				if (review.getResult() == null) {
					review.setResult(ReviewFactory.eINSTANCE
							.createReviewResult());
				}
				review.getResult().setText(commentText.getText());
			}
		});
		ratingList.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				getTaskEditorPage().setDirty();
				Rating rating = (Rating) ((IStructuredSelection) event
						.getSelection()).getFirstElement();
				Review review = getTaskEditorPage().getReview();
				if (review.getResult() == null) {
					review.setResult(ReviewFactory.eINSTANCE
							.createReviewResult());
				}
				review.getResult().setRating(rating);
			}
		});

		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		setSection(toolkit, section);
	}
	
	@Override
	public ReviewTaskEditorPage getTaskEditorPage() {
		return (ReviewTaskEditorPage) super.getTaskEditorPage();
	}
}
