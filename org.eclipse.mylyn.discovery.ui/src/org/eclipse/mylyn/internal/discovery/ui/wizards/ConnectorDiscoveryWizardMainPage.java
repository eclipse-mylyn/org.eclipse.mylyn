/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green
 *     Shawn Minto bug 275513
 * 	   Steffen Pingel bug 276012 code review, bug 277191 gradient canvas
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.discovery.core.model.AbstractDiscoverySource;
import org.eclipse.mylyn.internal.discovery.core.model.BundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptorKind;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDiscovery;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Icon;
import org.eclipse.mylyn.internal.discovery.core.model.RemoteBundleDiscoveryStrategy;
import org.eclipse.mylyn.internal.discovery.core.util.DiscoveryCategoryComparator;
import org.eclipse.mylyn.internal.discovery.core.util.DiscoveryConnectorComparator;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryImages;
import org.eclipse.mylyn.internal.discovery.ui.DiscoveryUi;
import org.eclipse.mylyn.internal.discovery.ui.util.DiscoveryUiUtil;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientCanvas;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.themes.IThemeManager;

/**
 * The main wizard page that allows users to select connectors that they wish to install.
 * 
 * @author David Green
 */
public class ConnectorDiscoveryWizardMainPage extends WizardPage {

	private static final String DEFAULT_DIRECTORY_URL = "http://www.eclipse.org/mylyn/discovery/directory.xml"; //$NON-NLS-1$

	private static final String SYSTEM_PROPERTY_DIRECTORY_URL = "mylyn.discovery.directory"; //$NON-NLS-1$

	private static final String COLOR_WHITE = "white"; //$NON-NLS-1$

	private static final String COLOR_DARK_GRAY = "DarkGray"; //$NON-NLS-1$

	private static Boolean useNativeSearchField;

	private final List<ConnectorDescriptor> installableConnectors = new ArrayList<ConnectorDescriptor>();

	private ConnectorDiscovery discovery;

	private Composite body;

	private final List<Resource> disposables = new ArrayList<Resource>();

	private Font h2Font;

	private Font h1Font;

	private Color colorWhite;

	private Text filterText;

	private WorkbenchJob refreshJob;

	private String previousFilterText = ""; //$NON-NLS-1$

	private Pattern filterPattern;

	private Label clearFilterTextControl;

	private Set<String> installedFeatures;

	private Image infoImage;

	private Cursor handCursor;

	private Color colorCategoryGradientStart;

	private Color colorCategoryGradientEnd;

	private Color colorDisabled;

	private ScrolledComposite bodyScrolledComposite;

	public ConnectorDiscoveryWizardMainPage() {
		super(ConnectorDiscoveryWizardMainPage.class.getSimpleName());
		setTitle(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_connectorDiscovery);
		// setImageDescriptor(image);
		setDescription(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_pageDescription);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		createRefreshJob();

		Composite container = new Composite(parent, SWT.NULL);
		container.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				refreshJob.cancel();
			}
		});
		container.setLayout(new GridLayout(1, false));
		//		
		{ // header
			Composite header = new Composite(container, SWT.NULL);
			GridLayoutFactory.fillDefaults().applyTo(header);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(header);

//			 TODO: refresh button?
			if (getWizard().isShowConnectorDescriptorKindFilter() || getWizard().isShowConnectorDescriptorTextFilter()) {
				Composite filterContainer = new Composite(header, SWT.NULL);
				GridDataFactory.fillDefaults().grab(true, false).applyTo(filterContainer);

				int numColumns = 1; // 1 for label
				if (getWizard().isShowConnectorDescriptorKindFilter()) {
					numColumns += ConnectorDescriptorKind.values().length;
				}
				if (getWizard().isShowConnectorDescriptorTextFilter()) {
					++numColumns;
				}
				GridLayoutFactory.fillDefaults().numColumns(numColumns).applyTo(filterContainer);
				Label label = new Label(filterContainer, SWT.NULL);
				label.setText(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_filterLabel);

				if (getWizard().isShowConnectorDescriptorTextFilter()) {
					Composite textFilterContainer;
					boolean nativeSearch = useNativeSearchField(header);
					if (nativeSearch) {
						textFilterContainer = new Composite(filterContainer, SWT.NULL);
					} else {
						textFilterContainer = new Composite(filterContainer, SWT.BORDER);
						textFilterContainer.setBackground(header.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
					}
					GridDataFactory.fillDefaults().grab(true, false).applyTo(textFilterContainer);
					GridLayoutFactory.fillDefaults().numColumns(2).applyTo(textFilterContainer);

					if (nativeSearch) {
						filterText = new Text(textFilterContainer, SWT.SINGLE | SWT.BORDER | SWT.SEARCH
								| SWT.ICON_CANCEL);
					} else {
						filterText = new Text(textFilterContainer, SWT.SINGLE);
					}

					filterText.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							filterTextChanged();
						}
					});
					if (nativeSearch) {
						filterText.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetDefaultSelected(SelectionEvent e) {
								if (e.detail == SWT.ICON_CANCEL) {
									clearFilterText();
								}
							}
						});
						GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(filterText);
					} else {
						GridDataFactory.fillDefaults().grab(true, false).applyTo(filterText);
						clearFilterTextControl = createClearFilterTextControl(textFilterContainer, filterText);
						clearFilterTextControl.setVisible(false);
					}
				}

				if (getWizard().isShowConnectorDescriptorKindFilter()) { // filter buttons

					for (final ConnectorDescriptorKind kind : ConnectorDescriptorKind.values()) {
						final Button checkbox = new Button(filterContainer, SWT.CHECK);
						checkbox.setSelection(getWizard().isVisible(kind));
						checkbox.setText(getFilterLabel(kind));
						checkbox.addSelectionListener(new SelectionListener() {
							public void widgetSelected(SelectionEvent e) {
								boolean selection = checkbox.getSelection();
								getWizard().setVisibility(kind, selection);
								connectorDescriptorKindVisibilityUpdated();
							}

							public void widgetDefaultSelected(SelectionEvent e) {
								widgetSelected(e);
							}
						});
					}
				}

			}

		}
		{ // container
			body = new Composite(container, SWT.NULL);
			GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 480).applyTo(body);
		}
		Dialog.applyDialogFont(container);
		setControl(container);
	}

	private static boolean useNativeSearchField(Composite composite) {
		if (useNativeSearchField == null) {
			useNativeSearchField = Boolean.FALSE;
			Text testText = null;
			try {
				testText = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL);
				useNativeSearchField = new Boolean((testText.getStyle() & SWT.ICON_CANCEL) != 0);
			} finally {
				if (testText != null) {
					testText.dispose();
				}
			}

		}
		return useNativeSearchField;
	}

	private void createRefreshJob() {
		refreshJob = new WorkbenchJob("filter") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (filterText.isDisposed()) {
					return Status.CANCEL_STATUS;
				}
				String text = filterText.getText();
				text = text.trim();

				if (!previousFilterText.equals(text)) {
					previousFilterText = text;
					filterPattern = createPattern(previousFilterText);
					if (clearFilterTextControl != null) {
						clearFilterTextControl.setVisible(filterPattern != null);
					}
					createBodyContents();
				}
				return Status.OK_STATUS;
			}
		};
		refreshJob.setSystem(true);
	}

	protected Pattern createPattern(String filterText) {
		if (filterText == null || filterText.length() == 0) {
			return null;
		}
		String regex = filterText;
		regex.replace("\\", "\\\\").replace("?", ".").replace("*", ".*?"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
		return Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	}

	private Label createClearFilterTextControl(Composite filterContainer, final Text filterText) {
		final Image inactiveImage = CommonImages.FIND_CLEAR_DISABLED.createImage();
		final Image activeImage = CommonImages.FIND_CLEAR.createImage();
		final Image pressedImage = new Image(filterContainer.getDisplay(), activeImage, SWT.IMAGE_GRAY);

		final Label clearButton = new Label(filterContainer, SWT.NONE);
		clearButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		clearButton.setImage(inactiveImage);
		clearButton.setToolTipText(Messages.ConnectorDiscoveryWizardMainPage_clearButton_toolTip);
		clearButton.setBackground(filterContainer.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		clearButton.addMouseListener(new MouseAdapter() {
			private MouseMoveListener fMoveListener;

			@Override
			public void mouseDown(MouseEvent e) {
				clearButton.setImage(pressedImage);
				fMoveListener = new MouseMoveListener() {
					private boolean fMouseInButton = true;

					public void mouseMove(MouseEvent e) {
						boolean mouseInButton = isMouseInButton(e);
						if (mouseInButton != fMouseInButton) {
							fMouseInButton = mouseInButton;
							clearButton.setImage(mouseInButton ? pressedImage : inactiveImage);
						}
					}
				};
				clearButton.addMouseMoveListener(fMoveListener);
			}

			@Override
			public void mouseUp(MouseEvent e) {
				if (fMoveListener != null) {
					clearButton.removeMouseMoveListener(fMoveListener);
					fMoveListener = null;
					boolean mouseInButton = isMouseInButton(e);
					clearButton.setImage(mouseInButton ? activeImage : inactiveImage);
					if (mouseInButton) {
						clearFilterText();
						filterText.setFocus();
					}
				}
			}

			private boolean isMouseInButton(MouseEvent e) {
				Point buttonSize = clearButton.getSize();
				return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y && e.y < buttonSize.y;
			}
		});
		clearButton.addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				clearButton.setImage(activeImage);
			}

			public void mouseExit(MouseEvent e) {
				clearButton.setImage(inactiveImage);
			}

			public void mouseHover(MouseEvent e) {
			}
		});
		clearButton.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				inactiveImage.dispose();
				activeImage.dispose();
				pressedImage.dispose();
			}
		});
		clearButton.getAccessible().addAccessibleListener(new AccessibleAdapter() {
			@Override
			public void getName(AccessibleEvent e) {
				e.result = Messages.ConnectorDiscoveryWizardMainPage_clearButton_accessibleListener;
			}
		});
		clearButton.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			@Override
			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_PUSHBUTTON;
			}
		});
		return clearButton;
	}

	@Override
	public ConnectorDiscoveryWizard getWizard() {
		return (ConnectorDiscoveryWizard) super.getWizard();
	}

	private void clearFilterText() {
		filterText.setText(""); //$NON-NLS-1$
		filterTextChanged();
	}

	private void filterTextChanged() {
		refreshJob.cancel();
		refreshJob.schedule(200L);
	}

	private String getFilterLabel(ConnectorDescriptorKind kind) {
		switch (kind) {
		case DOCUMENT:
			return org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_filter_documents;
		case TASK:
			return org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_filter_tasks;
		case VCS:
			return org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_filter_vcs;
		default:
			throw new IllegalStateException(kind.name());
		}
	}

	/**
	 * cause the UI to respond to a change in visibility filters
	 * 
	 * @see #setVisibility(ConnectorDescriptorKind, boolean)
	 */
	public void connectorDescriptorKindVisibilityUpdated() {
		createBodyContents();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Resource resource : disposables) {
			resource.dispose();
		}
		clearDisposables();
		if (discovery != null) {
			discovery.dispose();
		}
	}

	private void clearDisposables() {
		disposables.clear();
		h1Font = null;
		h2Font = null;
		infoImage = null;
		handCursor = null;
		colorCategoryGradientStart = null;
		colorCategoryGradientEnd = null;
	}

	public void createBodyContents() {
		// remove any existing contents
		for (Control child : body.getChildren()) {
			child.dispose();
		}
		clearDisposables();
		initializeCursors();
		initializeImages();
		initializeFonts();
		initializeColors();

		GridLayoutFactory.fillDefaults().applyTo(body);

		bodyScrolledComposite = new ScrolledComposite(body, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		configureLook(bodyScrolledComposite, colorWhite);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(bodyScrolledComposite);

		// FIXME 3.2 does white work for any desktop theme, e.g. an inverse theme?
		final Composite scrolledContents = new Composite(bodyScrolledComposite, SWT.NONE);
		configureLook(scrolledContents, colorWhite);
		scrolledContents.setRedraw(false);
		try {
			createDiscoveryContents(scrolledContents);
		} finally {
			scrolledContents.layout(true);
			scrolledContents.setRedraw(true);
		}
		Point size = scrolledContents.computeSize(body.getSize().x, SWT.DEFAULT, true);
		scrolledContents.setSize(size);

		bodyScrolledComposite.setExpandHorizontal(true);
		bodyScrolledComposite.setMinWidth(100);
		bodyScrolledComposite.setExpandVertical(true);
		bodyScrolledComposite.setMinHeight(1);

		bodyScrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				Point size = scrolledContents.computeSize(body.getSize().x, SWT.DEFAULT, true);
				scrolledContents.setSize(size);
				bodyScrolledComposite.setMinHeight(size.y);
			}
		});

		bodyScrolledComposite.setContent(scrolledContents);

		Dialog.applyDialogFont(body);
		// we've changed it so it needs to know
		body.layout(true);
	}

	private void initializeCursors() {
		if (handCursor == null) {
			handCursor = new Cursor(getShell().getDisplay(), SWT.CURSOR_HAND);
			disposables.add(handCursor);
		}
	}

	private void initializeImages() {
		if (infoImage == null) {
			infoImage = DiscoveryImages.MESSAGE_INFO.createImage();
			disposables.add(infoImage);
		}
	}

	private void initializeColors() {
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		if (colorWhite == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_WHITE)) {
				colorRegistry.put(COLOR_WHITE, new RGB(255, 255, 255));
			}
			colorWhite = colorRegistry.get(COLOR_WHITE);
		}
		if (colorDisabled == null) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			if (!colorRegistry.hasValueFor(COLOR_DARK_GRAY)) {
				colorRegistry.put(COLOR_DARK_GRAY, new RGB(0x69, 0x69, 0x69));
			}
			colorDisabled = colorRegistry.get(COLOR_DARK_GRAY);
		}
		if (colorCategoryGradientStart == null) {
			colorCategoryGradientStart = themeManager.getCurrentTheme().getColorRegistry().get(
					CommonThemes.COLOR_CATEGORY_GRADIENT_START);
			colorCategoryGradientEnd = themeManager.getCurrentTheme().getColorRegistry().get(
					CommonThemes.COLOR_CATEGORY_GRADIENT_END);
		}
	}

	private void initializeFonts() {
		// create a level-2 heading font
		if (h2Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.setStyle(data.getStyle() | SWT.BOLD);
				data.height = data.height * 1.25f;
			}
			h2Font = new Font(Display.getCurrent(), fontData);
			disposables.add(h2Font);
		}
		// create a level-1 heading font
		if (h1Font == null) {
			Font baseFont = JFaceResources.getDialogFont();
			FontData[] fontData = baseFont.getFontData();
			for (FontData data : fontData) {
				data.setStyle(data.getStyle() | SWT.BOLD);
				data.height = data.height * 1.35f;
			}
			h1Font = new Font(Display.getCurrent(), fontData);
			disposables.add(h1Font);
		}
	}

	private class ConnectorDescriptorItemUi implements PropertyChangeListener, Runnable {
		private final DiscoveryConnector connector;

		private final Button checkbox;

		private final Label iconLabel;

		private final Label nameLabel;

		private final Label infoLabel;

		private final Label providerLabel;

		private final Label description;

		private final Composite checkboxContainer;

		private final Composite connectorContainer;

		private final Display display;

		private Image iconImage;

		private Image warningIconImage;

		public ConnectorDescriptorItemUi(DiscoveryConnector connector, Composite categoryChildrenContainer,
				Color background) {
			display = categoryChildrenContainer.getDisplay();
			this.connector = connector;
			connector.addPropertyChangeListener(this);

			connectorContainer = new Composite(categoryChildrenContainer, SWT.NULL);

			configureLook(connectorContainer, background);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(connectorContainer);
			GridLayout layout = new GridLayout(4, false);
			layout.marginLeft = 7;
			layout.marginTop = 2;
			layout.marginBottom = 2;
			connectorContainer.setLayout(layout);

			checkboxContainer = new Composite(connectorContainer, SWT.NULL);
			configureLook(checkboxContainer, background);
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(checkboxContainer);
			GridLayoutFactory.fillDefaults().numColumns(2).applyTo(checkboxContainer);

			checkbox = new Button(checkboxContainer, SWT.CHECK);
			// help UI tests
			checkbox.setData("connectorId", connector.getId()); //$NON-NLS-1$
			configureLook(checkbox, background);
			checkbox.setSelection(installableConnectors.contains(connector));
			checkbox.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					bodyScrolledComposite.showControl(connectorContainer);
				}
			});

			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(checkbox);

			iconLabel = new Label(checkboxContainer, SWT.NULL);
			configureLook(iconLabel, background);
			GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.CENTER).applyTo(iconLabel);

			if (connector.getIcon() != null) {
				iconImage = computeIconImage(connector.getSource(), connector.getIcon(), 32, false);
				if (iconImage != null) {
					iconLabel.setImage(iconImage);
				}
			}

			nameLabel = new Label(connectorContainer, SWT.NULL);
			configureLook(nameLabel, background);
			GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);
			nameLabel.setFont(h2Font);
			nameLabel.setText(connector.getName());

			providerLabel = new Label(connectorContainer, SWT.NULL);
			configureLook(providerLabel, background);
			GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(providerLabel);
			providerLabel.setText(NLS.bind(Messages.ConnectorDiscoveryWizardMainPage_provider_and_license,
					connector.getProvider(), connector.getLicense()));

			infoLabel = new Label(connectorContainer, SWT.NULL);
			configureLook(infoLabel, background);
			if (hasTooltip(connector)) {
				infoLabel.setImage(infoImage);
				infoLabel.setCursor(handCursor);
				infoLabel.setToolTipText(Messages.ConnectorDiscoveryWizardMainPage_tooltip_showOverview);
				hookTooltip(infoLabel, connectorContainer, nameLabel, connector, true);
			}
			GridDataFactory.fillDefaults().align(SWT.END, SWT.FILL).grab(false, true).applyTo(infoLabel);

			description = new Label(connectorContainer, SWT.NULL | SWT.WRAP);
			configureLook(description, background);

			GridDataFactory.fillDefaults().grab(true, false).span(3, 1).hint(100, SWT.DEFAULT).applyTo(description);
			String descriptionText = connector.getDescription();
			int maxDescriptionLength = 162;
			if (descriptionText.length() > maxDescriptionLength) {
				descriptionText = descriptionText.substring(0, maxDescriptionLength);
			}
			description.setText(descriptionText.replaceAll("(\\r\\n)|\\n|\\r", " ")); //$NON-NLS-1$ //$NON-NLS-2$

			// always disabled color to make it less prominent
			providerLabel.setForeground(colorDisabled);

			checkbox.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}

				public void widgetSelected(SelectionEvent e) {
					boolean selected = checkbox.getSelection();
					maybeModifySelection(selected);
				}
			});
			MouseListener connectorItemMouseListener = new MouseAdapter() {
				@Override
				public void mouseUp(MouseEvent e) {
					boolean selected = !checkbox.getSelection();
					if (maybeModifySelection(selected)) {
						checkbox.setSelection(selected);
					}
				}
			};
			checkboxContainer.addMouseListener(connectorItemMouseListener);
			connectorContainer.addMouseListener(connectorItemMouseListener);
			iconLabel.addMouseListener(connectorItemMouseListener);
			nameLabel.addMouseListener(connectorItemMouseListener);
			providerLabel.addMouseListener(connectorItemMouseListener);
			description.addMouseListener(connectorItemMouseListener);
		}

		protected boolean maybeModifySelection(boolean selected) {
			if (selected) {
				if (connector.getAvailable() == null) {
					return false;
				}
				if (!connector.getAvailable()) {
					MessageDialog.openWarning(getShell(),
							Messages.ConnectorDiscoveryWizardMainPage_warningTitleConnectorUnavailable, NLS.bind(
									Messages.ConnectorDiscoveryWizardMainPage_warningMessageConnectorUnavailable,
									connector.getName()));
					return false;
				}
			}
			ConnectorDiscoveryWizardMainPage.this.modifySelection(connector, selected);
			return true;
		}

		public void updateAvailability() {
			boolean enabled = connector.getAvailable() != null && connector.getAvailable();

			checkbox.setEnabled(enabled);
			nameLabel.setEnabled(enabled);
			iconLabel.setEnabled(enabled);
			providerLabel.setEnabled(enabled);
			description.setEnabled(enabled);
			Color foreground;
			if (enabled) {
				foreground = connectorContainer.getForeground();
			} else {
				foreground = colorDisabled;
			}
			nameLabel.setForeground(foreground);
			description.setForeground(foreground);

			if (iconImage != null) {
				boolean unavailable = !enabled && connector.getAvailable() != null;
				if (unavailable) {
					if (warningIconImage == null) {
						warningIconImage = new DecorationOverlayIcon(iconImage, DiscoveryImages.OVERLAY_WARNING_32,
								IDecoration.TOP_LEFT).createImage();
						disposables.add(warningIconImage);
					}
					iconLabel.setImage(warningIconImage);
				} else if (warningIconImage != null) {
					iconLabel.setImage(iconImage);
				}
			}
		}

		public void propertyChange(PropertyChangeEvent evt) {
			display.asyncExec(this);
		}

		public void run() {
			if (!connectorContainer.isDisposed()) {
				updateAvailability();
			}
		}
	}

	private void createDiscoveryContents(Composite container) {

		Color background = container.getBackground();

		if (discovery == null || isEmpty(discovery)) {
			GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(container);

			boolean atLeastOneKindFiltered = false;
			for (ConnectorDescriptorKind kind : ConnectorDescriptorKind.values()) {
				if (!getWizard().isVisible(kind)) {
					atLeastOneKindFiltered = true;
					break;
				}
			}
			Control helpTextControl;
			if (filterPattern != null) {
				Link link = new Link(container, SWT.WRAP);

				link.setFont(container.getFont());
				link.setText(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_noMatchingItems_withFilterText);
				link.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event event) {
						clearFilterText();
						filterText.setFocus();
					}
				});
				helpTextControl = link;
			} else {
				Label helpText = new Label(container, SWT.WRAP);
				helpText.setFont(container.getFont());
				if (atLeastOneKindFiltered) {
					helpText.setText(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_noMatchingItems_filteredType);
				} else {
					helpText.setText(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_noMatchingItems_noFilter);
				}
				helpTextControl = helpText;
			}
			configureLook(helpTextControl, background);
			GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(helpTextControl);
		} else {
			GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).spacing(0, 0) //
					// hack for layout issue where bottom one gets cropped
					.extendedMargins(0, 0, 0, 15)
					.applyTo(container);

			List<DiscoveryCategory> categories = new ArrayList<DiscoveryCategory>(discovery.getCategories());
			Collections.sort(categories, new DiscoveryCategoryComparator());

			Composite categoryChildrenContainer = null;
			for (DiscoveryCategory category : categories) {
				if (isEmpty(category)) {
					// don't add empty categories
					continue;
				}
				{ // category header
					GradientCanvas categoryHeaderContainer = new GradientCanvas(container, SWT.NONE);
					categoryHeaderContainer.setSeparatorVisible(true);
					categoryHeaderContainer.setSeparatorAlignment(SWT.TOP);
					categoryHeaderContainer.setBackgroundGradient(new Color[] { colorCategoryGradientStart,
							colorCategoryGradientEnd }, new int[] { 100 }, true);
					categoryHeaderContainer.putColor(IFormColors.H_BOTTOM_KEYLINE1, colorCategoryGradientStart);
					categoryHeaderContainer.putColor(IFormColors.H_BOTTOM_KEYLINE2, colorCategoryGradientEnd);

					GridDataFactory.fillDefaults().span(2, 1).applyTo(categoryHeaderContainer);
					GridLayoutFactory.fillDefaults().numColumns(2).margins(5, 5).equalWidth(false).applyTo(
							categoryHeaderContainer);

					Label iconLabel = new Label(categoryHeaderContainer, SWT.NULL);
					if (category.getIcon() != null) {
						Image image = computeIconImage(category.getSource(), category.getIcon(), 48, true);
						if (image != null) {
							iconLabel.setImage(image);
						}
					}
					iconLabel.setBackground(null);
					GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).span(1, 2).applyTo(iconLabel);

					Label nameLabel = new Label(categoryHeaderContainer, SWT.NULL);
					nameLabel.setFont(h1Font);
					nameLabel.setText(category.getName());
					nameLabel.setBackground(null);
					GridDataFactory.fillDefaults().grab(true, false).applyTo(nameLabel);

					Label description = new Label(categoryHeaderContainer, SWT.WRAP);
					GridDataFactory.fillDefaults().grab(true, false).hint(100, SWT.DEFAULT).applyTo(description);
					description.setBackground(null);
					description.setText(category.getDescription());
				}

				categoryChildrenContainer = new Composite(container, SWT.NULL);
				configureLook(categoryChildrenContainer, background);
				GridDataFactory.fillDefaults().span(2, 1).grab(true, false).applyTo(categoryChildrenContainer);
				GridLayoutFactory.fillDefaults().spacing(0, 0).applyTo(categoryChildrenContainer);

				int numChildren = 0;
				List<DiscoveryConnector> connectors = new ArrayList<DiscoveryConnector>(category.getConnectors());
				Collections.sort(connectors, new DiscoveryConnectorComparator(category));
				for (final DiscoveryConnector connector : connectors) {
					if (isFiltered(connector)) {
						continue;
					}

					if (++numChildren > 1) {
						// a separator between connector descriptors
						Composite border = new Composite(categoryChildrenContainer, SWT.NULL);
						GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1).applyTo(border);
						GridLayoutFactory.fillDefaults().applyTo(border);
						border.addPaintListener(new ConnectorBorderPaintListener());
					}

					ConnectorDescriptorItemUi itemUi = new ConnectorDescriptorItemUi(connector,
							categoryChildrenContainer, background);
					itemUi.updateAvailability();
				}
			}
			// last one gets a border
			Composite border = new Composite(categoryChildrenContainer, SWT.NULL);
			GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 1).applyTo(border);
			GridLayoutFactory.fillDefaults().applyTo(border);
			border.addPaintListener(new ConnectorBorderPaintListener());
		}
		container.layout(true);
		container.redraw();
	}

	private void configureLook(Control control, Color background) {
		control.setBackground(background);
	}

	private void hookTooltip(final Control tooltipControl, final Control exitControl, final Control titleControl,
			DiscoveryConnector connector, final boolean asButton) {
		final ConnectorDescriptorToolTip toolTip = new ConnectorDescriptorToolTip(tooltipControl, connector);
		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.Dispose:
				case SWT.MouseWheel:
					toolTip.hide();
					break;
				case SWT.MouseDown:
					if (!asButton) {
						toolTip.hide();
						break;
					}
				case SWT.MouseHover:
					if (asButton && event.type == SWT.MouseHover) {
						break;
					}
					Point titleAbsLocation = titleControl.getParent().toDisplay(titleControl.getLocation());
					Point containerAbsLocation = tooltipControl.getParent().toDisplay(tooltipControl.getLocation());
					Rectangle bounds = titleControl.getBounds();
					int relativeX = titleAbsLocation.x - containerAbsLocation.x;
					int relativeY = titleAbsLocation.y - containerAbsLocation.y;

					relativeY += bounds.height + 3;
					toolTip.show(new Point(relativeX, relativeY));
					break;
				}

			}
		};
		hookRecursively(tooltipControl, listener);
		Listener exitListener = new Listener() {
			public void handleEvent(Event event) {
				switch (event.type) {
				case SWT.MouseWheel:
					toolTip.hide();
					break;
				case SWT.MouseExit:
					/*
					 * Check if the mouse exit happened because we move over the
					 * tooltip
					 */
					Rectangle containerBounds = exitControl.getBounds();
					Point displayLocation = exitControl.getParent().toDisplay(containerBounds.x, containerBounds.y);
					containerBounds.x = displayLocation.x;
					containerBounds.y = displayLocation.y;
					if (containerBounds.contains(Display.getCurrent().getCursorLocation())) {
						break;
					}
					toolTip.hide();
					break;
				}
			}
		};
		hookRecursively(exitControl, exitListener);
	}

	private void hookRecursively(Control control, Listener listener) {
		control.addListener(SWT.Dispose, listener);
		control.addListener(SWT.MouseHover, listener);
		control.addListener(SWT.MouseMove, listener);
		control.addListener(SWT.MouseExit, listener);
		control.addListener(SWT.MouseDown, listener);
		control.addListener(SWT.MouseWheel, listener);
		if (control instanceof Composite) {
			for (Control child : ((Composite) control).getChildren()) {
				hookRecursively(child, listener);
			}
		}
	}

	private boolean hasTooltip(final DiscoveryConnector connector) {
		return connector.getOverview() != null && connector.getOverview().getSummary() != null
				&& connector.getOverview().getSummary().length() > 0;
	}

	/**
	 * indicate if there is nothing to display in the UI, given the current state of
	 * {@link ConnectorDiscoveryWizard#isVisible(ConnectorDescriptorKind) filters}.
	 */
	private boolean isEmpty(ConnectorDiscovery discovery) {
		for (DiscoveryCategory category : discovery.getCategories()) {
			if (!isEmpty(category)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * indicate if the category has nothing to display in the UI, given the current state of
	 * {@link ConnectorDiscoveryWizard#isVisible(ConnectorDescriptorKind) filters}.
	 */
	private boolean isEmpty(DiscoveryCategory category) {
		if (category.getConnectors().isEmpty()) {
			return true;
		}
		for (ConnectorDescriptor descriptor : category.getConnectors()) {
			if (!isFiltered(descriptor)) {
				return false;
			}
		}
		return true;
	}

	private boolean isFiltered(ConnectorDescriptor descriptor) {
		boolean kindFiltered = true;
		for (ConnectorDescriptorKind kind : descriptor.getKind()) {
			if (getWizard().isVisible(kind)) {
				kindFiltered = false;
				break;
			}
		}
		if (kindFiltered) {
			return true;
		}
		if (installedFeatures != null && installedFeatures.contains(descriptor.getId())) {
			// always filter installed features per bug 275777
			return true;
		}
		if (filterPattern != null) {
			if (!(filterMatches(descriptor.getName()) || filterMatches(descriptor.getDescription())
					|| filterMatches(descriptor.getProvider()) || filterMatches(descriptor.getLicense()))) {
				return true;
			}
		}
		return false;
	}

	private boolean filterMatches(String text) {
		return text != null && filterPattern.matcher(text).find();
	}

	private Image computeIconImage(AbstractDiscoverySource discoverySource, Icon icon, int dimension, boolean fallback) {
		String imagePath;
		switch (dimension) {
		case 64:
			imagePath = icon.getImage64();
			if (imagePath != null || !fallback) {
				break;
			}
		case 48:
			imagePath = icon.getImage48();
			if (imagePath != null || !fallback) {
				break;
			}
		case 32:
			imagePath = icon.getImage32();
			break;
		default:
			throw new IllegalArgumentException();
		}
		if (imagePath != null && imagePath.length() > 0) {
			URL resource = discoverySource.getResource(imagePath);
			if (resource != null) {
				ImageDescriptor descriptor = ImageDescriptor.createFromURL(resource);
				Image image = descriptor.createImage();
				if (image != null) {
					disposables.add(image);
					return image;
				}
			}
		}
		return null;
	}

	private void maybeUpdateDiscovery() {
		if (!getControl().isDisposed() && isCurrentPage() && discovery == null) {
			final Dictionary<Object, Object> environment = getWizard().getEnvironment();
			boolean wasCancelled = false;
			try {
				getContainer().run(true, true, new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						if (ConnectorDiscoveryWizardMainPage.this.installedFeatures == null) {
							Set<String> installedFeatures = new HashSet<String>();
							IBundleGroupProvider[] bundleGroupProviders = Platform.getBundleGroupProviders();
							for (IBundleGroupProvider provider : bundleGroupProviders) {
								if (monitor.isCanceled()) {
									throw new InterruptedException();
								}
								IBundleGroup[] bundleGroups = provider.getBundleGroups();
								for (IBundleGroup group : bundleGroups) {
									installedFeatures.add(group.getIdentifier());
								}
							}
							ConnectorDiscoveryWizardMainPage.this.installedFeatures = installedFeatures;
						}

						ConnectorDiscovery connectorDiscovery = new ConnectorDiscovery();
						connectorDiscovery.getDiscoveryStrategies().add(new BundleDiscoveryStrategy());
						RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();

						remoteDiscoveryStrategy.setDirectoryUrl(System.getProperty(SYSTEM_PROPERTY_DIRECTORY_URL,
								DEFAULT_DIRECTORY_URL));
						connectorDiscovery.getDiscoveryStrategies().add(remoteDiscoveryStrategy);
						connectorDiscovery.setEnvironment(environment);
						connectorDiscovery.setVerifyUpdateSiteAvailability(false);
						try {
							connectorDiscovery.performDiscovery(monitor);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
						ConnectorDiscoveryWizardMainPage.this.discovery = connectorDiscovery;
						if (monitor.isCanceled()) {
							throw new InterruptedException();
						}
					}
				});
			} catch (InvocationTargetException e) {
				Throwable cause = e.getCause();
				IStatus status;
				if (!(cause instanceof CoreException)) {
					status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN,
							Messages.ConnectorDiscoveryWizardMainPage_unexpectedException, cause);
				} else {
					status = ((CoreException) cause).getStatus();
				}
				DiscoveryUiUtil.logAndDisplayStatus(Messages.ConnectorDiscoveryWizardMainPage_errorTitle, status);
			} catch (InterruptedException e) {
				// cancelled by user so nothing to do here.
				wasCancelled = true;
			}
			if (discovery != null) {
				discoveryUpdated(wasCancelled);
				try {
					getContainer().run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							discovery.verifySiteAvailability(monitor);
						}
					});
				} catch (InvocationTargetException e) {
					Throwable cause = e.getCause();
					IStatus status;
					if (!(cause instanceof CoreException)) {
						status = new Status(IStatus.ERROR, DiscoveryUi.ID_PLUGIN,
								Messages.ConnectorDiscoveryWizardMainPage_unexpectedException, cause);
					} else {
						status = ((CoreException) cause).getStatus();
					}
					DiscoveryUiUtil.logAndDisplayStatus(Messages.ConnectorDiscoveryWizardMainPage_errorTitle, status);
				} catch (InterruptedException e) {
					// cancelled by user so nothing to do here.
					wasCancelled = true;
				}
				// createBodyContents() shouldn't be necessary but for some reason checkboxes don't 
				// regain their enabled state
				createBodyContents();
			}
			// help UI tests
			body.setData("discoveryComplete", "true"); //$NON-NLS-1$//$NON-NLS-2$
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && discovery == null) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					maybeUpdateDiscovery();
				}
			});
		}
	}

	private void discoveryUpdated(boolean wasCancelled) {
		createBodyContents();
		if (discovery != null && !wasCancelled) {
			int categoryWithConnectorCount = 0;
			for (DiscoveryCategory category : discovery.getCategories()) {
				categoryWithConnectorCount += category.getConnectors().size();
			}
			if (categoryWithConnectorCount == 0) {
				// nothing was discovered: notify the user
				MessageDialog.openWarning(
						getShell(),
						org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_noConnectorsFound,
						org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_noConnectorsFound_description);
			}
		}
	}

	public List<ConnectorDescriptor> getInstallableConnectors() {
		return installableConnectors;
	}

	private void modifySelection(final DiscoveryConnector connector, boolean selected) {
		connector.setSelected(selected);

		if (selected) {
			installableConnectors.add(connector);
		} else {
			installableConnectors.remove(connector);
		}
		setPageComplete(!installableConnectors.isEmpty());
	}

	public class ConnectorBorderPaintListener implements PaintListener {
		public void paintControl(PaintEvent e) {
			Composite composite = (Composite) e.widget;
			Rectangle bounds = composite.getBounds();
			GC gc = e.gc;
			gc.setLineStyle(SWT.LINE_DOT);
			gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
		}
	}

}
