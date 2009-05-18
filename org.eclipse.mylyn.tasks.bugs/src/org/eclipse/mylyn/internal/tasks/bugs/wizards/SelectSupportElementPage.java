/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.commons.ui.ControlListItem;
import org.eclipse.mylyn.internal.commons.ui.ControlListViewer;
import org.eclipse.mylyn.internal.commons.ui.NotificationPopupColors;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonFonts;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientCanvas;
import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.mylyn.internal.provisional.tasks.bugs.IProvider;
import org.eclipse.mylyn.internal.tasks.bugs.AbstractSupportElement;
import org.eclipse.mylyn.internal.tasks.bugs.SupportCategory;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProduct;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public class SelectSupportElementPage extends WizardPage {

	public class SupportElementViewer extends ControlListViewer {

		public SupportElementViewer(Composite parent, int style) {
			super(parent, style);
			// ignore
		}

		@Override
		protected ControlListItem doCreateItem(Composite parent, Object element) {
			if (element instanceof SupportCategory) {
				return new CategoryItem(parent, SWT.NONE, element);
			}
			return new SupportElementItem(parent, SWT.NONE, element);
		}

	}

	private class CategoryItem extends ControlListItem {

		private Label label;

		public CategoryItem(Composite parent, int style, Object element) {
			super(parent, style, element);
		}

		@Override
		protected void createContent() {
			FillLayout layout = new FillLayout();
			setLayout(layout);

			GradientCanvas canvas = new GradientCanvas(this, SWT.NONE);
			NotificationPopupColors color = new NotificationPopupColors(getDisplay(), JFaceResources.getResources());
			canvas.setBackgroundGradient(new Color[] { color.getGradientBegin(), color.getGradientEnd() },
					new int[] { 100 }, true);
			canvas.setLayout(new GridLayout(1, false));

			label = new Label(canvas, SWT.NONE);
			label.setFont(JFaceResources.getHeaderFont());
			label.setBackground(null);

			canvas.setSize(canvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			refresh();
		}

		@Override
		protected void refresh() {
			AbstractSupportElement data = (AbstractSupportElement) getData();
			label.setText(data.getName());
		}

		@Override
		public void setForeground(Color color) {
			// ignore
		}

		@Override
		public void setBackground(Color color) {
			// ignore
		}

	}

	private class SupportElementItem extends ControlListItem {

		private ToolBar toolBar;

		private Label titleLabel;

		private Label iconLabel;

		private Label descriptionLabel;

		private ToolBarManager toolBarManager;

		public SupportElementItem(Composite parent, int style, Object element) {
			super(parent, style, element);
		}

		@Override
		protected void createContent() {
			FormLayout layout = new FormLayout();
			layout.marginHeight = 3;
			layout.marginWidth = 3;
			setLayout(layout);

			iconLabel = new Label(this, SWT.NONE);
			FormData fd = new FormData();
			fd.left = new FormAttachment(0);
			iconLabel.setLayoutData(fd);

			titleLabel = new Label(this, SWT.NONE);
			titleLabel.setFont(CommonFonts.BOLD);
			fd = new FormData();
			fd.left = new FormAttachment(iconLabel, 5);
			titleLabel.setLayoutData(fd);

			descriptionLabel = new Label(this, SWT.WRAP);
			fd = new FormData();
			fd.top = new FormAttachment(titleLabel, 5);
			fd.left = new FormAttachment(iconLabel, 5);
			descriptionLabel.setLayoutData(fd);

			toolBarManager = new ToolBarManager(SWT.FLAT);
			toolBar = toolBarManager.createControl(this);
			fd = new FormData();
			fd.right = new FormAttachment(100);
			toolBar.setLayoutData(fd);

			refresh();
		}

		@Override
		public void dispose() {
			super.dispose();
			toolBarManager.dispose();
		}

		@Override
		public void setForeground(Color color) {
			super.setForeground(color);
			if (isSelected()) {
				descriptionLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
			} else {
				descriptionLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
			}

		}

		@Override
		protected void refresh() {
			AbstractSupportElement data = (AbstractSupportElement) getData();
			iconLabel.setImage(getImage(data));
			titleLabel.setText(data.getName());
			descriptionLabel.setText(data.getDescription());

			toolBarManager.removeAll();
			final String url = data.getUrl();
			if (url != null) {
				Action action = new Action() {
					@Override
					public void run() {
						WorkbenchUtil.openUrl(url, IWorkbenchBrowserSupport.AS_EXTERNAL);
					}
				};
				action.setImageDescriptor(CommonImages.QUESTION);
				toolBarManager.add(action);
			}
			toolBarManager.update(false);
		}

		@Override
		public void setHot(boolean hot) {
			super.setHot(hot);
			updateToolBar();
		}

		@Override
		public void setSelected(boolean select) {
			super.setSelected(select);
			updateToolBar();
		}

		private void updateToolBar() {
			if (toolBar != null) {
				toolBar.setVisible(isHot() || isSelected());
			}
		}

	}

	private static final int TABLE_HEIGHT = 200;

	private AbstractSupportElement selectedElement;

	private ImageRegistry imageRegistry;

	private final IStructuredContentProvider contentProvider;

	private Object input;

	public SelectSupportElementPage(String pageName, IStructuredContentProvider contentProvider) {
		super(pageName);
		this.contentProvider = contentProvider;
	}

	public Image getImage(AbstractSupportElement data) {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry(getShell().getDisplay());
		}
		Image image = imageRegistry.get(data.getId());
		if (image == null && data.getIcon() != null) {
			imageRegistry.put(data.getId(), data.getIcon());
			image = imageRegistry.get(data.getId());
		}
		return image;
	}

	public void setInput(Object input) {
		this.input = input;

		if (input instanceof IProvider) {
			setTitle("Support Provider");
			setMessage("Select a support provider from the list.");
		} else {
			setTitle("Supported Product");
			setMessage("Select a supported product from the list.");
		}
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		ControlListViewer viewer = new SupportElementViewer(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, TABLE_HEIGHT).applyTo(viewer.getControl());
		viewer.setContentProvider(contentProvider);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object object = selection.getFirstElement();
				if (object instanceof AbstractSupportElement) {
					selectedElement = (AbstractSupportElement) object;
				} else {
					selectedElement = null;
				}
				updatePageStatus();
			}
		});
		viewer.addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				if (getWizard().performFinish()) {
					((WizardDialog) getContainer()).close();
				}
			}
		});
		viewer.setSorter(new ViewerSorter() {
			@SuppressWarnings("unchecked")
			@Override
			public int compare(Viewer viewer, Object o1, Object o2) {
				int cat1 = category(o1);
				int cat2 = category(o2);
				if (cat1 != cat2) {
					return cat1 - cat2;
				}
				if (o1 instanceof AbstractSupportElement && o2 instanceof AbstractSupportElement) {
					return getComparator().compare(((AbstractSupportElement) o1).getName(),
							((AbstractSupportElement) o2).getName());
				}
				return super.compare(viewer, o1, o2);
			}

			@Override
			public int category(Object element) {
				if (element instanceof SupportCategory) {
					return ((SupportCategory) element).getWeight() * 2;
				} else if (element instanceof SupportProvider) {
					return ((SupportProvider) element).getCategory().getWeight() * 2 + 1;
				}
				return super.category(element);
			}
		});
		viewer.setInput(input);

		setPageComplete(false);
		setControl(container);
		Dialog.applyDialogFont(container);
	}

	@Override
	public void dispose() {
		if (imageRegistry != null) {
			imageRegistry.dispose();
		}
		super.dispose();
	}

	public AbstractSupportElement getSelectedElement() {
		return selectedElement;
	}

	private void updatePageStatus() {
		if (selectedElement instanceof SupportProvider) {
			if (contentProvider.getElements(selectedElement).length > 0) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("The selected provider does not specify supported products.");
				setPageComplete(false);
			}
		} else if (selectedElement instanceof SupportProduct) {
			setErrorMessage(null);
			setPageComplete(true);
		}
	}

	@Override
	public IWizardPage getNextPage() {
		if (selectedElement instanceof SupportProvider) {
			SelectSupportElementPage page = new SelectSupportElementPage(selectedElement.getId(), contentProvider);
			page.setInput(selectedElement);
			page.setWizard(getWizard());
			return page;
		}
		return null;
	}
}