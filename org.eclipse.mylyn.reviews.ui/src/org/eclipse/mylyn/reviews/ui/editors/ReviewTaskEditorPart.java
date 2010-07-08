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
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.reviews.core.model.review.Patch;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.ReviewDiffModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/*
 * @author Kilian Matt
 */
public class ReviewTaskEditorPart extends AbstractTaskEditorPart {
	public static final String ID_PART_REVIEW = "org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorPart"; //$NON-NLS-1$
	private TableViewer fileList;
	private TextMergeViewer viewer;

	public ReviewTaskEditorPart() {
		setPartName("Scope");
		setExpandVertically(true);
	}
	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section section = createSection(parent, toolkit, true);
		GridLayout gl = new GridLayout(1,false);
		gl.marginBottom=8;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		section.setLayout(gl);
		section.setLayoutData(gd);

		SashForm composite =   new SashForm(section, SWT.HORIZONTAL);
		sashComposite=composite;
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(createSectionClientLayout());

		fileList = new TableViewer(composite);
		fileList.getControl().setLayoutData(new GridData(SWT.DEFAULT,SWT.FILL,false,true));

		TableViewerColumn column = new TableViewerColumn(fileList, SWT.LEFT);
		column.getColumn().setText("");
		column.getColumn().setWidth(25);
		column.getColumn().setResizable(false);
		column = new TableViewerColumn(fileList, SWT.LEFT);
		column.getColumn().setText("Filename");
		column.getColumn().setWidth(100);
		column.getColumn().setResizable(true);
		fileList.setLabelProvider(new TableLabelProvider() {
			private final int COLUMN_ICON = 0;
			private final int COLUMN_FILE = 1;

			@Override
			public String getColumnText(Object element, int columnIndex) {
				if (columnIndex == COLUMN_FILE) {
					if (element instanceof ReviewDiffModel) {
						ReviewDiffModel diffModel = ((ReviewDiffModel) element);

						return diffModel.getFileName();
					}
				}
				return null;
			}

			@Override
			public Image getColumnImage(Object element, int columnIndex) {
				if (columnIndex == COLUMN_ICON) {
					ISharedImages sharedImages = PlatformUI.getWorkbench()
							.getSharedImages();
					if (element instanceof ReviewDiffModel) {
						ReviewDiffModel diffModel = ((ReviewDiffModel) element);
						if (diffModel.isNewFile()) {
							return new NewFile().createImage();
						}
						if (!diffModel.canReview()) {
							return new MissingFile().createImage();
						}
					}

					return sharedImages.getImage(ISharedImages.IMG_OBJ_FILE);
				}
				return null;
			}
		});

		fileList.setContentProvider(new IStructuredContentProvider() {

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				Patch patch = (Patch) inputElement;
				List<IFilePatch2> patches = patch.parse();
				ReviewDiffModel[] model = new ReviewDiffModel[patches.size()];
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
						ReviewDiffModel diffModel = ((ReviewDiffModel) sel
								.getFirstElement());
						if (diffModel.canReview()) {
							viewer.setInput(diffModel.getCompareInput());
						}
					}
				}
			}
		});

		fileList.setInput((getTaskEditorPage().getReview().getScope().get(0)));

		CompareConfiguration configuration = new CompareConfiguration();
		configuration.setLeftEditable(false);
		configuration.setRightEditable(false);
		configuration.setLeftLabel(Messages.EditorSupport_Original);
		configuration.setRightLabel(Messages.EditorSupport_Patched);
		configuration
				.setProperty(CompareConfiguration.IGNORE_WHITESPACE, false);
		configuration.setProperty(CompareConfiguration.USE_OUTLINE_VIEW, true);
		viewer = new TextMergeViewer(composite, SWT.BORDER, configuration);
		viewer.getControl().setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		viewer.setInput(getDiffEditorNullInput());
		
		composite.setWeights(new int[]{1,3});
		section.setClient(composite);


		// Depends on 288171
//		getSashComposite().setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION, getMaximizePartAction());
//		if (getSashComposite() instanceof Composite) {
//			for (Control control : ((Composite) getSashComposite()).getChildren()) {
//				control.setData(EditorUtil.KEY_TOGGLE_TO_MAXIMIZE_ACTION, getMaximizePartAction());
//			}
//		}

		setSection(toolkit, section);

	}

	private DiffNode getDiffEditorNullInput() {
		return  new DiffNode(new DiffNode(SWT.LEFT),new DiffNode(SWT.RIGHT));
	}
	
	@Override
	public ReviewTaskEditorPage getTaskEditorPage() {
		return (ReviewTaskEditorPage)super.getTaskEditorPage();
	}

	private GridLayout createSectionClientLayout() {
		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight = 0;
		// leave 1px for borders
		layout.marginTop = 2;
		// spacing if a section is expanded
		layout.marginBottom = 8;
		return layout;
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

	public Control getSashComposite() {
		return sashComposite;
	}
	private Control sashComposite;

	@Override
	protected void fillToolBar(ToolBarManager manager) {
		// Depends on 288171
//		manager.add(getMaximizePartAction());
		super.fillToolBar(manager);
	}

}
