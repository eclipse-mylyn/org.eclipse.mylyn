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
package org.eclipse.mylyn.reviews.tasks.ui.internal.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
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
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeNodeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.reviews.tasks.core.IReviewFile;
import org.eclipse.mylyn.reviews.tasks.core.IReviewMapper;
import org.eclipse.mylyn.reviews.tasks.core.IReviewScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
import org.eclipse.mylyn.reviews.tasks.core.internal.TaskProperties;
import org.eclipse.mylyn.reviews.tasks.ui.internal.Images;
import org.eclipse.mylyn.reviews.tasks.ui.internal.ReviewsUiPlugin;
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
	private TreeViewer fileList;
	private Composite composite;
	private ITaskProperties taskProperties;
	private ComboViewer ratingList;
	private Section section;

	public ReviewTaskEditorPart() {
		setPartName("Review ");
		setExpandVertically(true);
	}

	private enum Column implements IColumnSpec<TreeNode> {
		GROUP("Group") {
			@Override
			public String getText(TreeNode node) {
				Object value = node.getValue();
				if (value instanceof IReviewScopeItem) {
					return ((IReviewScopeItem) value).getDescription();
				}
				return null;
			}
		},
		FILES("Filename") {
			@Override
			public String getText(TreeNode node) {
				Object value = node.getValue();
				if (value instanceof IReviewFile) {
					return ((IReviewFile) value).getFileName();
				}
				return null;
			}

			@Override
			public Image getImage(TreeNode node) {
				Object element = node.getValue();
				if (element instanceof IReviewFile) {
					ISharedImages sharedImages = PlatformUI.getWorkbench()
							.getSharedImages();
					IReviewFile file = ((IReviewFile) element);
					if (file.isNewFile()) {
						return new NewFile().createImage();
					}
					if (!file.canReview()) {
						return new MissingFile().createImage();
					}

					return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
				}
				return null;
			}
		};

		/*
		 * Object element = ((TreeNode) node).getValue(); if (columnIndex ==
		 * COLUMN_FILE) {
		 * 
		 * } } return null;
		 */
		private String title;

		private Column(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public String getText(TreeNode value) {
			return value != null ? value.toString() : "";
		}

		public Image getImage(TreeNode value) {
			return null;
		}

	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		section = createSection(parent, toolkit, true);
		GridLayout gl = new GridLayout(1, false);
		gl.marginBottom = 16;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		section.setLayout(gl);
		section.setLayoutData(gd);
		setSection(toolkit, section);

		composite = toolkit.createComposite(section);

		composite.setLayout(new GridLayout(1, true));

		fileList = new TreeViewer(composite);

		fileList.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		TreeHelper.createColumns(fileList, Column.values());
		fileList.getTree().setLinesVisible(true);
		fileList.getTree().setHeaderVisible(true);

		fileList.setLabelProvider(new ColumnLabelProvider<TreeNode>(Column
				.values()));

		fileList.setContentProvider(new TreeNodeContentProvider());
		fileList.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection sel = (IStructuredSelection) selection;
					Object value = ((TreeNode) sel.getFirstElement())
							.getValue();
					if (value instanceof IReviewFile) {
						final IReviewFile file = (IReviewFile) value;
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
							CompareUI.openCompareEditor(new CompareEditorInput(
									configuration) {

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

			public void run() throws Exception {

				ReviewScope reviewScope = getReviewScope();
				if (reviewScope == null) {
					section.setExpanded(false);
					return;
				}
				List<IReviewScopeItem> files = reviewScope.getItems();

				final TreeNode[] rootNodes = new TreeNode[files.size()];
				int index = 0;
				for (IReviewScopeItem item : files) {
					TreeNode node = new TreeNode(item);
					List<IReviewFile> reviewFiles = item
							.getReviewFiles(new NullProgressMonitor());
					TreeNode[] children = new TreeNode[reviewFiles.size()];
					for (int i = 0; i < reviewFiles.size(); i++) {
						children[i] = new TreeNode(reviewFiles.get(i));
						children[i].setParent(node);
					}
					node.setChildren(children);

					rootNodes[index++] = node;
				}

				Display.getCurrent().asyncExec(new Runnable() {
					public void run() {
						fileList.setInput(rootNodes);
						if (rootNodes.length == 0) {
							section.setExpanded(false);
						}
					}
				});

			}

			public void handleException(Throwable exception) {
				exception.printStackTrace();
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
