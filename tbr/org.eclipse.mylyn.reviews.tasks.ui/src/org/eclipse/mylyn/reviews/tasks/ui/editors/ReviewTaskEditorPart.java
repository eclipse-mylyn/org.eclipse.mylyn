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
package org.eclipse.mylyn.reviews.tasks.ui.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.reviews.tasks.core.IReviewFile;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskProperties;
import org.eclipse.mylyn.reviews.tasks.ui.Images;
import org.eclipse.mylyn.reviews.tasks.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class ReviewTaskEditorPart extends AbstractReviewTaskEditorPart {
	public static final String ID_PART_REVIEW = "org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorPart"; //$NON-NLS-1$
	private TableViewer fileList;
	private Composite composite;
	private ITaskProperties taskProperties;
	private ComboViewer ratingList;

	public ReviewTaskEditorPart() {
		setPartName("Review ");
		setExpandVertically(true);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		GridLayout gl = new GridLayout(1, false);
		gl.marginBottom = 16;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		section.setLayout(gl);
		section.setLayoutData(gd);

		composite = toolkit.createComposite(section);

		composite.setLayout(new GridLayout(1, true));

		fileList = new TableViewer(composite);

		fileList.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		TableViewerColumn column = new TableViewerColumn(fileList, SWT.LEFT);
		column.getColumn().setText("Filename");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnWeightData(100, true));
		fileList.getTable().setLayout(tableLayout);

		fileList.setLabelProvider(new TableLabelProvider() {
			private final int COLUMN_FILE = 0;

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == COLUMN_FILE) {
					if (element instanceof IReviewFile) {
						IReviewFile file = ((IReviewFile) element);

						return file.getFileName();
					}
				}
				return null;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == COLUMN_FILE) {
					ISharedImages sharedImages = PlatformUI.getWorkbench()
							.getSharedImages();
					if (element instanceof IReviewFile) {
						IReviewFile file = ((IReviewFile) element);
						if (file.isNewFile()) {
							return new NewFile().createImage();
						}
						if (!file.canReview()) {
							return new MissingFile().createImage();
						}
					}

					return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
				}
				return null;
			}
		});

		fileList.setContentProvider(ArrayContentProvider.getInstance());
		fileList.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					if (sel.getFirstElement() instanceof IReviewFile) {
						final IReviewFile file = ((IReviewFile) sel
								.getFirstElement());
						if (file.canReview()) {
							CompareConfiguration configuration = new CompareConfiguration();
							configuration.setLeftEditable(false);
							configuration.setRightEditable(false);
							configuration
									.setLeftLabel(Messages.EditorSupport_Original);
							configuration
									.setRightLabel(Messages.EditorSupport_Patched);
							configuration.setProperty(
									CompareConfiguration.IGNORE_WHITESPACE,
									false);
							configuration
									.setProperty(
											CompareConfiguration.USE_OUTLINE_VIEW,
											true);
							CompareUI.openCompareEditor(new CompareEditorInput(configuration) {

								@Override
								protected Object prepareInput(
										IProgressMonitor monitor)
										throws InvocationTargetException,
										InterruptedException {
									return file.getCompareInput();
								}
							}, true);
						}
					}
				}
			}
		});

		createResultFields(composite, toolkit);

		section.setClient(composite);

		// Depends on 288171
		// getSashComposite().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
		// getMaximizePartAction());
		// if (getSashComposite() instanceof Composite) {
		// for (Control control : ((Composite)
		// getSashComposite()).getChildren()) {
		// control.setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION,
		// getMaximizePartAction());
		// }
		// }

		setSection(toolkit, section);

		SafeRunner.run(new ISafeRunnable() {
			
			@Override
			public void run() throws Exception {
				final List<IReviewFile> files = getInput();
				Display.getCurrent().asyncExec(new Runnable() {
					@Override
					public void run() {
						fileList.setInput(files);	
					}
				});
				
			}
			
			@Override
			public void handleException(Throwable exception) {
				exception.printStackTrace();
			}
			private List<IReviewFile> getInput() throws CoreException {
				List<IReviewFile> files = new ArrayList<IReviewFile>();
				ReviewScope reviewScope = getReviewScope();
				if (reviewScope != null) {
					// FIXME parse all scopes
					for (ReviewScopeItem scopeItem : reviewScope.getItems()) {
						files.addAll(scopeItem
								.getReviewFiles(new NullProgressMonitor()));
					}
				}
				return files;
			}

		});
	}

	private void createResultFields(Composite composite, FormToolkit toolkit) {
		Composite resultComposite = toolkit.createComposite(composite);
		toolkit.paintBordersFor(resultComposite);
		resultComposite.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true,
				false));
		resultComposite.setLayout(new GridLayout(2, false));

		toolkit.createLabel(resultComposite, "Rating:").setForeground(
				toolkit.getColors().getColor(IFormColors.TITLE));
		CCombo ratingsCombo = new CCombo(resultComposite, SWT.READ_ONLY
				| SWT.FLAT);
		ratingsCombo.setData(FormToolkit.KEY_DRAW_BORDER,
				FormToolkit.TREE_BORDER);
		toolkit.adapt(ratingsCombo, false, false);
		ratingList = new ComboViewer(ratingsCombo);
		ratingList.setContentProvider(ArrayContentProvider.getInstance());
		ratingList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				// TODO externalize string
				return ((Rating) element).name();
			}

			@Override
			public Image getImage(Object element) {
				Rating rating = ((Rating) element);
				switch (rating) {
				case FAIL:
					return Images.REVIEW_RESULT_FAILED.createImage();
				case TODO:
					return Images.REVIEW_RESULT_NONE.createImage();
				case PASSED:
					return Images.REVIEW_RESULT_PASSED.createImage();
				case WARNING:
					return Images.REVIEW_RESULT_WARNING.createImage();
				}
				return super.getImage(element);
			}
		});
		ratingList.setInput(Rating.values());
		ratingList.getControl().setLayoutData(
				new GridData(SWT.LEFT, SWT.TOP, false, false));

		toolkit.createLabel(resultComposite, "Rating comment:").setForeground(
				toolkit.getColors().getColor(IFormColors.TITLE));
		final Text commentText = toolkit.createText(resultComposite, "",
				SWT.MULTI);

		GridData gd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		gd.heightHint = 100;
		commentText.setLayoutData(gd);
		final ReviewResult result = getCurrentResultOrNew();

		// FIXME selection for rating
		if (result.getRating() != null)
			ratingList.getCCombo().select(result.getRating().ordinal());
		if (result.getComment() != null)
			commentText.setText(result.getComment());
		commentText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				result.setComment(commentText.getText());

			}
		});
		ratingList.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				Rating rating = (Rating) ((IStructuredSelection) event
						.getSelection()).getFirstElement();

				result.setRating(rating);
			}
		});
		registerEditOperations(result);
	}

	private ReviewResult getCurrentResultOrNew() {
		ReviewResult result = getCurrentResult();
		if (result == null) {
			result = new ReviewResult();
		}
		return result;
	}

	void registerEditOperations(ReviewResult result) {
		final IReviewMapper mapper = ReviewsUiPlugin.getMapper();
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				ReviewResult result = (ReviewResult) arg0.getSource();

				mapper.mapResultToTask(result, getTaskProperties());
				// FIXME
				getModel().attributeChanged(
						getModel().getTaskData().getRoot()
								.getMappedAttribute(TaskAttribute.COMMENT_NEW));
			}

		};
		result.addPropertyChangeListener("comment", listener);
		result.addPropertyChangeListener("rating", listener);
	}

	private ITaskProperties getTaskProperties() {
		if (taskProperties == null) {
			taskProperties = TaskProperties.fromTaskData(
					TasksUi.getTaskDataManager(), getTaskData());

		}
		return taskProperties;
	}

	/**
	 * Retrieves the review from the review data manager and fills the left
	 * table with the files.
	 * 
	 * @return
	 */
	private ReviewResult getCurrentResult() {
		ITaskProperties taskProperties = getTaskProperties();

		final IReviewMapper mapper = ReviewsUiPlugin.getMapper();
		ReviewResult res = mapper.mapCurrentReviewResult(taskProperties);
		return res;
	}


	private static class MissingFile extends CompositeImageDescriptor {
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();

		@Override
		protected void drawCompositeImage(int width, int height) {
			drawImage(getBaseImageData(), 0, 0);
			drawImage(Images.OVERLAY_OBSTRUCTED.getImageData(), 7, 3);
		}

		@Override
		protected Point getSize() {
			ImageData img = getBaseImageData();
			return new Point(img.width, img.height);
		}

		private ImageData baseImage;

		private ImageData getBaseImageData() {
			if (baseImage == null) {
				baseImage = sharedImages.getImageDescriptor(
						ISharedImages.IMG_OBJ_FILE).getImageData();
			}
			return baseImage;
		}

	}

	private static class NewFile extends CompositeImageDescriptor {
		ISharedImages sharedImages = PlatformUI.getWorkbench()
				.getSharedImages();

		@Override
		protected void drawCompositeImage(int width, int height) {
			drawImage(getBaseImageData(), 0, 0);
			drawImage(Images.OVERLAY_ADDITION.getImageData(), 7, 5);
		}

		@Override
		protected Point getSize() {

			ImageData img = getBaseImageData();
			return new Point(img.width, img.height);
		}

		private ImageData baseImage;

		private ImageData getBaseImageData() {
			if (baseImage == null) {
				baseImage = sharedImages.getImageDescriptor(
						ISharedImages.IMG_OBJ_FILE).getImageData();
			}
			return baseImage;
		}

	}

	private ReviewScope getReviewScope() throws CoreException {
		return getReviewPage().getReviewScope();
	}

	@Override
	protected void fillToolBar(ToolBarManager manager) {
		// Depends on 288171
		// manager.add(getMaximizePartAction());
		super.fillToolBar(manager);
	}

}
