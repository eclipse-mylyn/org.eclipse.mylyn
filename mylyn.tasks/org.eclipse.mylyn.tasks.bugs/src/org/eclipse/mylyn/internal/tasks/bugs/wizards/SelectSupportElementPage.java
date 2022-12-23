/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.ControlListItem;
import org.eclipse.mylyn.commons.ui.ControlListViewer;
import org.eclipse.mylyn.commons.ui.GradientCanvas;
import org.eclipse.mylyn.commons.ui.compatibility.CommonThemes;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.tasks.bugs.AbstractSupportElement;
import org.eclipse.mylyn.internal.tasks.bugs.SupportCategory;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProduct;
import org.eclipse.mylyn.internal.tasks.bugs.SupportProvider;
import org.eclipse.mylyn.tasks.bugs.IProvider;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.themes.IThemeManager;

/**
 * @author Steffen Pingel
 */
public class SelectSupportElementPage extends WizardPage {

	private class SupportElementItem extends ControlListItem {

		private static final int ICON_GAP = 10;

		private ToolBar toolBar;

		private Label titleLabel;

		private Label iconLabel;

		private Label descriptionLabel;

		private ToolBarManager toolBarManager;

		private boolean gradientBackground;

		private GradientCanvas canvas;

		public SupportElementItem(Composite parent, int style, Object element) {
			super(parent, style, element);
			registerChild(titleLabel);
			registerChild(iconLabel);
			registerChild(descriptionLabel);
			registerChild(toolBar);
		}

		public void setGradientBackground(boolean gradientBackground) {
			this.gradientBackground = gradientBackground;

			if (gradientBackground) {
				IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
				Color colorCategoryGradientStart = themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(CommonThemes.COLOR_CATEGORY_GRADIENT_START);
				Color colorCategoryGradientEnd = themeManager.getCurrentTheme()
						.getColorRegistry()
						.get(CommonThemes.COLOR_CATEGORY_GRADIENT_END);

				canvas.setSeparatorVisible(true);
				canvas.setSeparatorAlignment(SWT.TOP);
				canvas.setBackgroundGradient(new Color[] { colorCategoryGradientStart, colorCategoryGradientEnd },
						new int[] { 100 }, true);
				canvas.putColor(GradientCanvas.H_BOTTOM_KEYLINE1, colorCategoryGradientStart);
				canvas.putColor(GradientCanvas.H_BOTTOM_KEYLINE2, colorCategoryGradientEnd);

			}
		}

		public boolean isGradientBackground() {
			return gradientBackground;
		}

		@Override
		protected void createContent() {
			setLayout(new FillLayout());

			canvas = new GradientCanvas(this, SWT.NONE);

			FormLayout layout = new FormLayout();
			layout.marginHeight = 3;
			layout.marginWidth = 3;
			canvas.setLayout(layout);

			iconLabel = new Label(canvas, SWT.NONE);
			FormData fd = new FormData();
			fd.left = new FormAttachment(0);
			iconLabel.setLayoutData(fd);

			titleLabel = new Label(canvas, SWT.NONE);
			titleLabel.setFont(JFaceResources.getBannerFont());
			fd = new FormData();
			fd.left = new FormAttachment(iconLabel, ICON_GAP);
			titleLabel.setLayoutData(fd);

			descriptionLabel = new Label(canvas, SWT.WRAP);

			toolBarManager = new ToolBarManager(SWT.FLAT);
			toolBar = toolBarManager.createControl(canvas);

			fd = new FormData();
			fd.top = new FormAttachment(titleLabel, 5);
			fd.left = new FormAttachment(iconLabel, 10);
			fd.right = new FormAttachment(toolBar, -5);
			descriptionLabel.setLayoutData(fd);

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
		public void setBackground(Color color) {
			if (isGradientBackground()) {
				return;
			}
			super.setBackground(color);
		}

		@Override
		public void setForeground(Color color) {
			if (isGradientBackground()) {
				// ignore
				return;
			}
			super.setForeground(color);
			if (isSelected()) {
				titleLabel.setForeground(color);
				descriptionLabel.setForeground(color);
			} else {
				titleLabel.setForeground(color);
				descriptionLabel.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
			}
		}

		@Override
		protected void refresh() {
			AbstractSupportElement data = (AbstractSupportElement) getData();
			Image image = getImage(data);
			if (image == null) {
				// left align with column
				((FormData) titleLabel.getLayoutData()).left = new FormAttachment(0);
				((FormData) descriptionLabel.getLayoutData()).left = new FormAttachment(0);
			} else {
				// leave space between icon and text
				((FormData) titleLabel.getLayoutData()).left = new FormAttachment(iconLabel, ICON_GAP);
				((FormData) descriptionLabel.getLayoutData()).left = new FormAttachment(iconLabel, ICON_GAP);
			}
			iconLabel.setImage(image);
			titleLabel.setText(data.getName());
			descriptionLabel.setText((data.getDescription() != null) ? data.getDescription() : ""); //$NON-NLS-1$

			toolBarManager.removeAll();
			final String url = data.getUrl();
			if (url != null) {
				Action action = new Action() {
					@Override
					public void run() {
						BrowserUtil.openUrl(url, IWorkbenchBrowserSupport.AS_EXTERNAL);
					}
				};
				action.setImageDescriptor(CommonImages.INFORMATION);
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
			canvas.redraw();
		}

		private void updateToolBar() {
			if (toolBar != null) {
				toolBar.setVisible(isHot() || isSelected());
			}
		}

	}

	public class SupportElementViewer extends ControlListViewer {

		public SupportElementViewer(Composite parent, int style) {
			super(parent, style);
			// ignore
		}

		@Override
		protected ControlListItem doCreateItem(Composite parent, Object element) {
			if (element instanceof SupportCategory) {
				SupportElementItem item = new SupportElementItem(parent, SWT.NONE, element);
				item.setGradientBackground(true);
				return item;
			}
			return new SupportElementItem(parent, SWT.NONE, element);
		}

		@Override
		protected void doCreateNoEntryArea(Composite parent) {
			parent.setLayout(new FillLayout());

			Label label = new Label(parent, SWT.WRAP);
			label.setText(Messages.SelectSupportElementPage_No_support_providers_Error0);
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
			setTitle(Messages.SelectSupportElementPage_Supported_Product_Title);
			setMessage(Messages.SelectSupportElementPage_Support_Product_Description);
		} else {
			setTitle(Messages.SelectSupportElementPage_Support_Provider_Title);
			setMessage(Messages.SelectSupportElementPage_Support_Provider_Description);
		}
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, true);
		container.setLayout(layout);

		ControlListViewer viewer = new SupportElementViewer(container, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL);
		GridDataFactory.fillDefaults().grab(true, true).hint(500, TABLE_HEIGHT).applyTo(viewer.getControl());
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
				if (getWizard().canFinish()) {
					if (getWizard().performFinish()) {
						((WizardDialog) getContainer()).close();
					}
				} else {
					IWizardPage nextPage = getNextPage();
					if (nextPage != null) {
						((WizardDialog) getContainer()).showPage(nextPage);
					}
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
					if (((SupportProvider) element).getCategory() != null) {
						return ((SupportProvider) element).getCategory().getWeight() * 2 + 1;
					}
				}
				return super.category(element);
			}
		});
		viewer.setInput(input);

		Object[] elements = contentProvider.getElements(input);
		if (elements.length == 1) {
			viewer.setSelection(new StructuredSelection(elements[0]));
		} else {
			setPageComplete(false);
		}

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
				setErrorMessage(Messages.SelectSupportElementPage_No_products_Message);
				setPageComplete(false);
			}
		} else if (selectedElement instanceof SupportProduct) {
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			setErrorMessage(null);
			setPageComplete(false);
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
		return super.getNextPage();
	}
}