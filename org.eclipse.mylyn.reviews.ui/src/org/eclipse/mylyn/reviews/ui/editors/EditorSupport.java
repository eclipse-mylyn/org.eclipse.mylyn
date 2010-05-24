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

import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.patch.IFilePatch2;
import org.eclipse.compare.patch.PatchConfiguration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.core.model.review.Rating;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ReviewResult;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.ReviewDiffModel;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class EditorSupport {

	private ReviewSubmitHandler handler;

	public EditorSupport(ReviewTaskEditorInput input,
			ReviewSubmitHandler handler) {
		this.input = input;
		this.handler = handler;
	}

	private ReviewTaskEditorInput input;

	private ListViewer fileList;
	private TextMergeViewer viewer;
	private Section delegateSection;
	private Composite container;

	public Control createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		return createPartControl(parent, toolkit);
	}

	public Control createPartControl(Composite parent, FormToolkit toolkit) {
		try {
			container = toolkit.createComposite(parent, SWT.NONE);
			container.setLayout(new FillLayout());

			ScrolledForm scrolledForm = toolkit.createScrolledForm(container);
			scrolledForm.setText(Messages.ReviewEditor_New_Patch_based_Review);
			scrolledForm.setDelayedReflow(false);
			toolkit.decorateFormHeading(scrolledForm.getForm());

			Composite body = scrolledForm.getBody();
			body.setLayoutData(getLayoutData(false));
			body.setLayout(new GridLayout());

			createDelegateSection(toolkit, body);

			Section fileSection = toolkit.createSection(body,
					ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
							| ExpandableComposite.EXPANDED);
			fileSection.setText(Messages.ReviewEditor_Files);
			fileSection.setLayoutData(getLayoutData(false));

			fileList = new ListViewer(fileSection);
			fileSection.setClient(fileList.getControl());
			fileList.setContentProvider(new IStructuredContentProvider() {

				public void inputChanged(Viewer viewer, Object oldInput,
						Object newInput) {
				}

				public void dispose() {
				}

				public Object[] getElements(Object inputElement) {
					Patch patch = (Patch) inputElement;
					List<IFilePatch2> patches = patch.parse();
					ReviewDiffModel[] model = new ReviewDiffModel[patches
							.size()];
					int index = 0;
					for (IFilePatch2 currentPatch : patches) {
						final PatchConfiguration configuration = new PatchConfiguration();
						currentPatch.getTargetPath(configuration);
						model[index++] = new ReviewDiffModel(currentPatch,
								configuration);

					}
					return model;
				}
			});
			fileList.addSelectionChangedListener(new ISelectionChangedListener() {

				public void selectionChanged(SelectionChangedEvent event) {
					ISelection selection = event.getSelection();
					if (selection instanceof IStructuredSelection) {
						IStructuredSelection sel = (IStructuredSelection) selection;
						if (sel.getFirstElement() instanceof ReviewDiffModel) {
							viewer.setInput(((ReviewDiffModel) sel
									.getFirstElement()).getCompareInput());
						}
					}
				}
			});
			fileList.setInput((getEditorInput().getReview().getScope().get(0)));

			Section differencesSection = toolkit.createSection(body,
					ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
							| ExpandableComposite.EXPANDED);
			differencesSection.setLayoutData(getLayoutData(true));
			differencesSection.setText(Messages.ReviewEditor_Diff);

			CompareConfiguration configuration = new CompareConfiguration();
			configuration.setLeftEditable(false);
			configuration.setRightEditable(false);
			configuration.setLeftLabel(Messages.EditorSupport_Original);
			configuration.setRightLabel(Messages.EditorSupport_Patched);
			configuration.setProperty(CompareConfiguration.IGNORE_WHITESPACE,
					false);
			configuration.setProperty(CompareConfiguration.USE_OUTLINE_VIEW,
					true);
			viewer = new TextMergeViewer(differencesSection, SWT.BORDER,
					configuration);
			differencesSection.setClient(viewer.getControl());
			viewer.setInput(null);

			Section reviewSection = toolkit.createSection(body,
					ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
							| ExpandableComposite.EXPANDED);

			reviewSection.setLayoutData(getLayoutData(false));
			reviewSection.setLayout(new FillLayout());
			reviewSection.setText(Messages.ReviewEditor_Review);

			Composite reviewComposite = toolkit.createComposite(reviewSection);
			reviewComposite.setLayout(new GridLayout(4, false));
			toolkit.createLabel(reviewComposite, Messages.ReviewEditor_Rating);
			final ComboViewer ratingList = new ComboViewer(reviewComposite,
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
			if (input.getReview().getResult() != null) {
				Rating rating = input.getReview().getResult().getRating();
				ratingList.setSelection(new StructuredSelection(rating));
			}

			toolkit.createLabel(reviewComposite, Messages.ReviewEditor_Comment);
			final Text commentText = toolkit.createText(reviewComposite, "", //$NON-NLS-1$
					SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			GridData gd = getLayoutData(true);// new GridData(SWT.DEFAULT, 60);
			gd.heightHint = 60;
			gd.verticalSpan = 2;
			commentText.setLayoutData(gd);
			if (input.getReview().getResult() != null) {
				commentText.setText(input.getReview().getResult().getText());
			}

			toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$
			toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$
			toolkit.createLabel(reviewComposite, ""); //$NON-NLS-1$
			Button submitButton = toolkit.createButton(reviewComposite,
					Messages.ReviewEditor_Submit, SWT.PUSH);
			submitButton.addSelectionListener(new SelectionListener() {

				public void widgetSelected(SelectionEvent e) {
					try {
						Rating rating = (Rating) ((IStructuredSelection) ratingList
								.getSelection()).getFirstElement();

						ReviewResult reviewResult = ReviewFactory.eINSTANCE
								.createReviewResult();
						reviewResult.setText(commentText.getText());
						reviewResult.setRating(rating);
						getEditorInput().getReview().setResult(reviewResult);

						handler.doSubmit(getEditorInput());

					} catch (Exception ex) {

						ex.printStackTrace();

					}
				}

				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			});
			submitButton
					.setImage(TasksUiImages.REPOSITORY_SUBMIT.createImage());
			reviewSection.setClient(reviewComposite);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return container;
	}

	private ReviewTaskEditorInput getEditorInput() {
		return input;
	}

	private void createDelegateSection(FormToolkit toolkit, Composite body) {
		delegateSection = toolkit.createSection(body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| ExpandableComposite.EXPANDED);
		delegateSection.setText(Messages.ReviewEditor_Review);
		delegateSection.setLayoutData(getLayoutData(false));
		Composite composite = toolkit.createComposite(delegateSection);
		composite.setLayout(new GridLayout(2, false));
		toolkit.createLabel(composite, Messages.ReviewEditor_Assigned_to);

		// TODO change text to task people input
		Text people = toolkit.createText(composite, "", SWT.BORDER); //$NON-NLS-1$
		if (getEditorInput() instanceof NewReviewTaskEditorInput) {
			String currentUserName = ((NewReviewTaskEditorInput) getEditorInput())
					.getModel().getTaskRepository().getUserName();
			people.setText(currentUserName);
		}

		delegateSection.setClient(composite);
	}

	private GridData getLayoutData(boolean growHorizontal) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		return gd;
	}

}
