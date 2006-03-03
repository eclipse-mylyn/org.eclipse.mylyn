/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceColors;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.bugzilla.core.Attribute;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaAttributeListener;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaReportSelection;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.internal.ide.IDEInternalWorkbenchImages;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Abstract base implementation for an editor to view a bugzilla report.
 * 
 * @author Mik Kersten (some hardening of prototype)
 */
public abstract class AbstractBugEditor extends EditorPart implements Listener {

	protected TaskRepository repository;

	public static final int WRAP_LENGTH = 90;

	protected Display display;

	public static final Font TITLE_FONT = JFaceResources.getBannerFont();

	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	public static final Font COMMENT_FONT = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);

	public static final Font HEADER_FONT = JFaceResources.getDefaultFont();

	public static final int DESCRIPTION_WIDTH = 79 * 7;

	public static final int DESCRIPTION_HEIGHT = 10 * 14;

	protected Color background;

	protected Color foreground;

	protected AbstractBugEditorInput bugzillaInput;

	private MylarTaskEditor parentEditor = null;
	
	protected BugzillaOutlineNode bugzillaOutlineModel = null;

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * plain-styled, selectable text label.
	 */
	protected final String VALUE = "VALUE";

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * bolded, selectable header. It will also have an arrow image before the
	 * text (simply for decoration).
	 */
	protected final String HEADER = "HEADER";

	/**
	 * Style option for function <code>newLayout</code>. This will create a
	 * bolded, unselectable label.
	 */
	protected final String PROPERTY = "PROPERTY";

	protected final int HORZ_INDENT = 0;

	protected Combo oSCombo;

	protected Combo versionCombo;

	protected Combo platformCombo;

	protected Combo priorityCombo;

	protected Combo severityCombo;

	protected Combo milestoneCombo;

	protected Combo componentCombo;

	protected Text urlText;

	protected Text summaryText;

	protected Text assignedTo;

	protected Button submitButton;

	// protected Button saveButton;

	protected int scrollIncrement;

	protected int scrollVertPageIncrement;

	protected int scrollHorzPageIncrement;

	public boolean isDirty = false;

	/** Manager controlling the context menu */
	protected MenuManager contextMenuManager;

	protected StyledText currentSelectedText;

	protected static final String cutActionDefId = "org.eclipse.ui.edit.cut"; //$NON-NLS-1$

	protected static final String copyActionDefId = "org.eclipse.ui.edit.copy"; //$NON-NLS-1$

	protected static final String pasteActionDefId = "org.eclipse.ui.edit.paste"; //$NON-NLS-1$

	protected RetargetAction cutAction;

	protected BugzillaEditorCopyAction copyAction;

	protected RetargetAction pasteAction;

	protected Composite editorComposite;

//	protected CLabel titleLabel;

	protected ScrolledComposite scrolledComposite;

	protected Composite infoArea;

//	protected Hyperlink linkToBug;

//	protected StyledText generalTitleText;

	private List<IBugzillaAttributeListener> attributesListeners = new ArrayList<IBugzillaAttributeListener>();

	protected final ISelectionProvider selectionProvider = new ISelectionProvider() {
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.add(listener);
		}

		public ISelection getSelection() {
			return null;
		}

		public void removeSelectionChangedListener(ISelectionChangedListener listener) {
			selectionChangedListeners.remove(listener);
		}

		public void setSelection(ISelection selection) {
			// No implementation.
		}
	};

	@SuppressWarnings("deprecation")
	protected ListenerList selectionChangedListeners = new ListenerList();

	protected HashMap<Combo, String> comboListenerMap = new HashMap<Combo, String>();

	private IBugzillaReportSelection lastSelected = null;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof BugzillaOutlineNode) {
					BugzillaOutlineNode n = (BugzillaOutlineNode) select;

					if (n != null && lastSelected != null
							&& BugzillaTools.getHandle(n).equals(BugzillaTools.getHandle(lastSelected))) {
						// we don't need to set the selection if it is alredy
						// set
						return;
					}
					lastSelected = n;

					Object data = n.getData();
					boolean highlight = true;
					if (n.getKey().toLowerCase().equals("comments")) {
						highlight = false;
					}
					if (n.getKey().toLowerCase().equals("new comment")) {
						selectNewComment();
					} else if (n.getKey().toLowerCase().equals("new description")) {
						selectNewDescription();
					} else if (data != null) {
						select(data, highlight);
					}
				}
			}
		}
	};

	/**
	 * Creates a new <code>AbstractBugEditor</code>. Sets up the default
	 * fonts and cut/copy/paste actions.
	 */
	public AbstractBugEditor() {
		// set the scroll increments so the editor scrolls normally with the
		// scroll wheel
		FontData[] fd = TEXT_FONT.getFontData();
		int cushion = 4;
		scrollIncrement = fd[0].getHeight() + cushion;
		scrollVertPageIncrement = 0;
		scrollHorzPageIncrement = 0;

		// set up actions for the context menu
		cutAction = new RetargetAction(ActionFactory.CUT.getId(), WorkbenchMessages.Workbench_cut);
		cutAction.setToolTipText(WorkbenchMessages.Workbench_cutToolTip);// WorkbenchMessages.getString("Workbench.cutToolTip"));
		// //$NON-NLS-1$
		cutAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		cutAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
		cutAction.setAccelerator(SWT.CTRL | 'x');
		cutAction.setActionDefinitionId(cutActionDefId);

		pasteAction = new RetargetAction(ActionFactory.PASTE.getId(), WorkbenchMessages.Workbench_paste);
		pasteAction.setToolTipText(WorkbenchMessages.Workbench_pasteToolTip);// WorkbenchMessages.getString("Workbench.pasteToolTip"));
		// //$NON-NLS-1$
		pasteAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		pasteAction.setDisabledImageDescriptor(WorkbenchImages
				.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		pasteAction.setAccelerator(SWT.CTRL | 'v');
		pasteAction.setActionDefinitionId(pasteActionDefId);

		copyAction = new BugzillaEditorCopyAction(this);
		copyAction.setText(WorkbenchMessages.Workbench_copy);// WorkbenchMessages.getString("Workbench.copy"));
		copyAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		copyAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		copyAction.setAccelerator(SWT.CTRL | 'c');

		copyAction.setEnabled(false);
	}

	/**
	 * @return The bug this editor is displaying.
	 */
	public abstract IBugzillaBug getBug();

	/**
	 * @return Any currently selected text.
	 */
	protected StyledText getCurrentText() {
		return currentSelectedText;
	}

	/**
	 * @return The action used to copy selected text from a bug editor to the
	 *         clipboard.
	 */
	protected BugzillaEditorCopyAction getCopyAction() {
		return copyAction;
	}

	@Override
	public void createPartControl(Composite parent) {
		editorComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		editorComposite.setLayout(layout);
		
		display = parent.getDisplay();
		background = JFaceColors.getBannerBackground(display);
		foreground = JFaceColors.getBannerForeground(display);
		// Create the title for the editor
//		createTitleArea(editorComposite);
//		Label titleBarSeparator = new Label(editorComposite, SWT.HORIZONTAL | SWT.SEPARATOR);

		background = JFaceColors.getBannerBackground(display);
		foreground = JFaceColors.getBannerForeground(display);
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		titleBarSeparator.setLayoutData(gd);

		// Put the bug info onto the editor
		createInfoArea(editorComposite);

		WorkbenchHelpSystem.getInstance().setHelp(editorComposite, IBugzillaConstants.EDITOR_PAGE_CONTEXT);

		infoArea.setMenu(contextMenuManager.createContextMenu(infoArea));

		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

//	protected Composite createTitleArea(Composite parent) {
//		// Get the background color for the title area
//
//		// Create the title area which will contain
//		// a title, message, and image.
//		Composite titleArea = new Composite(parent, SWT.NO_FOCUS);
//		GridLayout layout = new GridLayout();
//		layout.marginHeight = 0;
//		layout.marginWidth = 0;
//		layout.verticalSpacing = 0;
//		layout.horizontalSpacing = 0;
//		layout.numColumns = 2;
//		titleArea.setLayout(layout);
//		titleArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		titleArea.setBackground(background);
//
//		// Message label
//		titleLabel = new CLabel(titleArea, SWT.LEFT);
//		JFaceColors.setColors(titleLabel, foreground, background);
//		titleLabel.setFont(TITLE_FONT);
//
//		final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
//			public void propertyChange(PropertyChangeEvent event) {
//				if (JFaceResources.HEADER_FONT.equals(event.getProperty())) {
//					titleLabel.setFont(TITLE_FONT);
//				}
//			}
//		};
//		titleLabel.addDisposeListener(new DisposeListener() {
//			public void widgetDisposed(DisposeEvent event) {
//				JFaceResources.getFontRegistry().removeListener(fontListener);
//			}
//		});
//		JFaceResources.getFontRegistry().addListener(fontListener);
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		titleLabel.setLayoutData(gd);
//
//		// Title image
//		Label titleImage = new Label(titleArea, SWT.LEFT);
//		titleImage.setBackground(background);
//		titleImage.setImage(WorkbenchImages.getImage(IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_BANNER));
//		gd = new GridData();
//		gd.horizontalAlignment = GridData.END;
//		titleImage.setLayoutData(gd);
//		return titleArea;
//	}

	/**
	 * Creates the part of the editor that contains the information about the
	 * the bug.
	 * 
	 * @param parent
	 *            The composite to put the info area into.
	 * @return The info area composite.
	 */
	protected Composite createInfoArea(Composite parent) {

		createContextMenu();

		scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		infoArea = new Composite(this.scrolledComposite, SWT.NONE);
		scrolledComposite.setMinSize(infoArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		GridLayout infoLayout = new GridLayout();
		infoLayout.numColumns = 1;
		infoLayout.verticalSpacing = 0;
		infoLayout.horizontalSpacing = 0;
		infoLayout.marginWidth = 0;
		infoArea.setLayout(infoLayout);
		infoArea.setBackground(background);
		if (getBug() == null) {
			// close();
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Bugzilla Client Errror",
					"Could not resolve the requested bug, check Bugzilla server and version.");

			Composite composite = new Composite(parent, SWT.NULL);
			Label noBugLabel = new Label(composite, SWT.NULL);
			noBugLabel.setText("Could not resolve bug");
			return composite;
		}
		createLayouts();

		this.scrolledComposite.setContent(infoArea);
		Point p = infoArea.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		this.scrolledComposite.setMinHeight(p.y);
		this.scrolledComposite.setMinWidth(p.x);
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setExpandVertical(true);

		// make the editor scroll properly with a scroll editor
		scrolledComposite.addControlListener(new ControlListener() {
			public void controlMoved(ControlEvent e) {
				// don't care when the control moved
			}

			public void controlResized(ControlEvent e) {
				scrolledComposite.getVerticalBar().setIncrement(scrollIncrement);
				scrolledComposite.getHorizontalBar().setIncrement(scrollIncrement);
				scrollVertPageIncrement = scrolledComposite.getClientArea().height;
				scrollHorzPageIncrement = scrolledComposite.getClientArea().width;
				scrolledComposite.getVerticalBar().setPageIncrement(scrollVertPageIncrement);
				scrolledComposite.getHorizontalBar().setPageIncrement(scrollHorzPageIncrement);
			}
		});

		return infoArea;
	}

	/**
	 * Create a context menu for this editor.
	 */
	protected void createContextMenu() {
		contextMenuManager = new MenuManager("#BugEditor");
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(cutAction);
				manager.add(copyAction);
				manager.add(pasteAction);
				manager.add(new Separator());
				manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
				if (currentSelectedText == null || currentSelectedText.getSelectionText().length() == 0) {

					copyAction.setEnabled(false);
				} else {
					copyAction.setEnabled(true);
				}
			}
		});
		getSite().registerContextMenu("#BugEditor", contextMenuManager, getSite().getSelectionProvider());
	}

	/**
	 * Creates all of the layouts that display the information on the bug.
	 */
	protected void createLayouts() {
		createAttributeLayout();
		createDescriptionLayout();
		createCommentLayout();
		createButtonLayouts();
	}

	/**
	 * Creates the attribute layout, which contains most of the basic attributes
	 * of the bug (some of which are editable).
	 */
	protected void createAttributeLayout() {

		String title = getTitleString();
		String keywords = "";
		String url = "";

		// Attributes Composite- this holds all the combo fiels and text fields
		Composite attributesComposite = new Composite(infoArea, SWT.NONE);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 14;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		attributesComposite.setBackground(background);
		// End Attributes Composite

		// Attributes Title Area
		Composite attributesTitleComposite = new Composite(attributesComposite, SWT.NONE);
		GridLayout attributesTitleLayout = new GridLayout();
		attributesTitleLayout.horizontalSpacing = 0;
		attributesTitleLayout.marginWidth = 0;
		attributesTitleComposite.setLayout(attributesTitleLayout);
		attributesTitleComposite.setBackground(background);
		GridData attributesTitleData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		attributesTitleData.horizontalSpan = 4;
		attributesTitleData.grabExcessVerticalSpace = false;
		attributesTitleComposite.setLayoutData(attributesTitleData);
		// End Attributes Title

		// Set the Attributes Title
//		newAttributesLayout(attributesTitleComposite);
//		titleLabel.setText(title);
		bugzillaInput.setToolTipText(title);
		int currentCol = 1;

		String ccValue = null;

		// Populate Attributes
		for (Iterator<Attribute> it = getBug().getAttributes().iterator(); it.hasNext();) {
			Attribute attribute = it.next();
			String key = attribute.getParameterName();
			String name = attribute.getName();
			String value = checkText(attribute.getValue());
			Map<String, String> values = attribute.getOptionValues();

			// make sure we don't try to display a hidden field
			if (attribute.isHidden() || (key != null && key.equals("status_whiteboard")))
				continue;

			if (values == null)
				values = new HashMap<String, String>();

			if (key == null)
				key = "";

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 1;
			data.horizontalIndent = HORZ_INDENT;

			if (key.equals("short_desc") || key.equals("keywords")) {
				keywords = value;
			} else if (key.equals("newcc")) {
				ccValue = value;
				if (value == null)
					ccValue = "";
			} else if (key.equals("bug_file_loc")) {
				url = value;
			} else if (key.equals("op_sys")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				oSCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);
				oSCombo.setFont(TEXT_FONT);
				oSCombo.setLayoutData(data);
				oSCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					oSCombo.add(a[i]);
				}
				if (oSCombo.indexOf(value) != -1) {
					oSCombo.select(oSCombo.indexOf(value));
				} else {
					oSCombo.select(oSCombo.indexOf("All"));
				}
				oSCombo.addListener(SWT.Modify, this);
				comboListenerMap.put(oSCombo, name);
				oSCombo.addListener(SWT.FocusIn, new GenericListener());
				currentCol += 2;
			} else if (key.equals("version")) {
				newLayout(attributesComposite, 1, name, PROPERTY);

				versionCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);
				versionCombo.setFont(TEXT_FONT);
				versionCombo.setLayoutData(data);
				versionCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					versionCombo.add(a[i]);
				}
				versionCombo.select(versionCombo.indexOf(value));
				versionCombo.addListener(SWT.Modify, this);
				versionCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(versionCombo, name);
				currentCol += 2;
			} else if (key.equals("priority")) {
				newLayout(attributesComposite, 1, name, PROPERTY);

				priorityCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);
				priorityCombo.setFont(TEXT_FONT);
				priorityCombo.setLayoutData(data);
				priorityCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					priorityCombo.add(a[i]);
				}
				priorityCombo.select(priorityCombo.indexOf(value));
				priorityCombo.addListener(SWT.Modify, this);
				priorityCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(priorityCombo, name);
				currentCol += 2;
			} else if (key.equals("bug_severity")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				severityCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);

				severityCombo.setFont(TEXT_FONT);
				severityCombo.setLayoutData(data);
				severityCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					severityCombo.add(a[i]);
				}
				severityCombo.select(severityCombo.indexOf(value));
				severityCombo.addListener(SWT.Modify, this);
				severityCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(severityCombo, name);
				currentCol += 2;
			} else if (key.equals("target_milestone")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				milestoneCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);

				milestoneCombo.setFont(TEXT_FONT);
				milestoneCombo.setLayoutData(data);
				milestoneCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					milestoneCombo.add(a[i]);
				}
				milestoneCombo.select(milestoneCombo.indexOf(value));
				milestoneCombo.addListener(SWT.Modify, this);
				milestoneCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(milestoneCombo, name);
				currentCol += 2;
			} else if (key.equals("rep_platform")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				platformCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);

				platformCombo.setFont(TEXT_FONT);
				platformCombo.setLayoutData(data);
				platformCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					platformCombo.add(a[i]);
				}
				platformCombo.select(platformCombo.indexOf(value));
				platformCombo.addListener(SWT.Modify, this);
				platformCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(platformCombo, name);
				currentCol += 2;
			} else if (key.equals("product")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				newLayout(attributesComposite, 1, value, VALUE).addListener(SWT.FocusIn, new GenericListener());
				currentCol += 2;
			} else if (key.equals("assigned_to")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				assignedTo = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
				assignedTo.setFont(TEXT_FONT);
				assignedTo.setText(value);
				data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				data.horizontalSpan = 1;
				assignedTo.setLayoutData(data);

				assignedTo.addListener(SWT.KeyUp, new Listener() {
					public void handleEvent(Event event) {
						String sel = assignedTo.getText();
						Attribute a = getBug().getAttribute("Assign To");
						if (!(a.getNewValue().equals(sel))) {
							a.setNewValue(sel);
							changeDirtyStatus(true);
						}
					}
				});
				assignedTo.addListener(SWT.FocusIn, new GenericListener());

				currentCol += 2;
			} else if (key.equals("component")) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				componentCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
						| SWT.READ_ONLY);

				componentCombo.setFont(TEXT_FONT);
				componentCombo.setLayoutData(data);
				componentCombo.setBackground(background);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					componentCombo.add(a[i]);
				}
				componentCombo.select(componentCombo.indexOf(value));
				componentCombo.addListener(SWT.Modify, this);
				componentCombo.addListener(SWT.FocusIn, new GenericListener());
				comboListenerMap.put(componentCombo, name);
				currentCol += 2;
			} else if (name.equals("Summary")) {
				// Don't show the summary here.
				continue;
			} else if (values.isEmpty()) {
				newLayout(attributesComposite, 1, name, PROPERTY);
				newLayout(attributesComposite, 1, value, VALUE).addListener(SWT.FocusIn, new GenericListener());
				currentCol += 2;
			}
			if (currentCol > attributesLayout.numColumns) {
				currentCol -= attributesLayout.numColumns;
			}
		}
		// End Populate Attributes

		// make sure that we are in the first column
		if (currentCol > 1) {
			while (currentCol <= attributesLayout.numColumns) {
				newLayout(attributesComposite, 1, "", PROPERTY);
				currentCol++;
			}
		}

		// URL, Keywords, Summary Text Fields
		addUrlText(url, attributesComposite);

		// keywords text field (not editable)
		addKeywordsList(keywords, attributesComposite);
		if (ccValue != null) {
			addCCList(ccValue, attributesComposite);
		}
		addSummaryText(attributesComposite);
		// End URL, Keywords, Summary Text Fields
	}

	/**
	 * Adds a text field to display and edit the bug's URL attribute.
	 * 
	 * @param url
	 *            The URL attribute of the bug.
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addUrlText(String url, Composite attributesComposite) {
		newLayout(attributesComposite, 1, "URL:", PROPERTY);
		urlText = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
		urlText.setFont(TEXT_FONT);
		GridData urlTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		urlTextData.horizontalSpan = 3;
		urlTextData.widthHint = 200;
		urlText.setLayoutData(urlTextData);
		urlText.setText(url);
		urlText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				String sel = urlText.getText();
				Attribute a = getBug().getAttribute("URL");
				if (!(a.getNewValue().equals(sel))) {
					a.setNewValue(sel);
					changeDirtyStatus(true);
				}
			}
		});
		urlText.addListener(SWT.FocusIn, new GenericListener());
	}

	/**
	 * Adds a text field and selection list to display and edit the bug's
	 * keywords.
	 * 
	 * @param keywords
	 *            The current list of keywords for this bug.
	 * @param attributesComposite
	 *            The composite to add the widgets to.
	 */
	protected abstract void addKeywordsList(String keywords, Composite attributesComposite);

	protected abstract void addCCList(String value, Composite attributesComposite);

	/**
	 * Adds a text field to display and edit the bug's summary.
	 * 
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addSummaryText(Composite attributesComposite) {
		newLayout(attributesComposite, 1, "Summary:", PROPERTY);
		summaryText = new Text(attributesComposite, SWT.BORDER | SWT.SINGLE | SWT.WRAP);
		summaryText.setFont(TEXT_FONT);
		GridData summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		summaryTextData.horizontalSpan = 3;
		summaryTextData.widthHint = 200;
		summaryText.setLayoutData(summaryTextData);
		summaryText.setText(getBug().getSummary());
		summaryText.addListener(SWT.KeyUp, new SummaryListener());
		summaryText.addListener(SWT.FocusIn, new GenericListener());
	}

	/**
	 * Creates the description layout, which displays and possibly edits the
	 * bug's description.
	 */
	protected abstract void createDescriptionLayout();

	/**
	 * Creates the comment layout, which displays the bug's comments and
	 * possibly lets the user enter a new one.
	 */
	protected abstract void createCommentLayout();

	/**
	 * Creates the button layout. This displays options and buttons at the
	 * bottom of the editor to allow actions to be performed on the bug.
	 */
	protected void createButtonLayouts() {

		Composite buttonComposite = new Composite(infoArea, SWT.NONE);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 4;
		buttonComposite.setLayout(buttonLayout);
		buttonComposite.setBackground(background);
		GridData buttonData = new GridData(GridData.FILL_BOTH);
		buttonData.horizontalSpan = 1;
		buttonData.grabExcessVerticalSpace = false;
		buttonComposite.setLayoutData(buttonData);

		addRadioButtons(buttonComposite);
		addActionButtons(buttonComposite);
	}

	/**
	 * Adds radio buttons to this composite.
	 * 
	 * @param buttonComposite
	 *            Composite to add the radio buttons to.
	 */
	abstract protected void addRadioButtons(Composite buttonComposite);

	/**
	 * Adds buttons to this composite. Subclasses can override this method to
	 * provide different/additional buttons.
	 * 
	 * @param buttonComposite
	 *            Composite to add the buttons to.
	 */
	protected void addActionButtons(Composite buttonComposite) {
		submitButton = new Button(buttonComposite, SWT.NONE);
		submitButton.setFont(TEXT_FONT);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		submitButtonData.widthHint = AbstractBugEditor.WRAP_LENGTH;
		submitButtonData.heightHint = 20;
		submitButton.setText("Submit");
		submitButton.setLayoutData(submitButtonData);
		submitButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				submitBug();
			}
		});
		submitButton.addListener(SWT.FocusIn, new GenericListener());

		// This is not needed anymore since we have the save working properly
		// with ctrl-s and file->save
		// saveButton = new Button(buttonComposite, SWT.NONE);
		// saveButton.setFont(TEXT_FONT);
		// GridData saveButtonData = new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// saveButtonData.widthHint = 100;
		// saveButtonData.heightHint = 20;
		// saveButton.setText("Save Offline");
		// saveButton.setLayoutData(saveButtonData);
		// saveButton.addListener(SWT.Selection, new Listener() {
		// public void handleEvent(Event e) {
		// saveBug();
		// updateEditor();
		// }
		// });
		// saveButton.addListener(SWT.FocusIn, new GenericListener());
	}

	/**
	 * Make sure that a String that is <code>null</code> is changed to a null
	 * string
	 * 
	 * @param text
	 *            The text to check if it is null or not
	 * @return If the text is <code>null</code>, then return the null string (<code>""</code>).
	 *         Otherwise, return the text.
	 */
	public static String checkText(String text) {
		if (text == null)
			return "";
		else
			return text;
	}

	/**
	 * @return A string to use as a title for this editor.
	 */
	protected abstract String getTitleString();

	/**
	 * Creates an uneditable text field for displaying data.
	 * 
	 * @param composite
	 *            The composite to put this text field into. Its layout style
	 *            should be a grid with columns.
	 * @param colSpan
	 *            The number of columns that this text field should span.
	 * @param text
	 *            The text that for this text field.
	 * @param style
	 *            The style for this text field. See below for valid values
	 *            (default is HEADER).
	 * @return The new styled text.
	 * @see VALUE
	 * @see PROPERTY
	 * @see HEADER
	 */
	protected StyledText newLayout(Composite composite, int colSpan, String text, String style) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = colSpan;

		StyledText stext;
		if (style.equalsIgnoreCase(VALUE)) {
			StyledText styledText = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY);
			styledText.setFont(TEXT_FONT);
			styledText.setText(checkText(text));
			styledText.setBackground(background);
			data.horizontalIndent = HORZ_INDENT;
			styledText.setLayoutData(data);
			styledText.setEditable(false);
			styledText.getCaret().setVisible(false);

			styledText.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					StyledText c = (StyledText) e.widget;
					if (c != null && c.getSelectionCount() > 0) {
						if (currentSelectedText != null) {
							if (!c.equals(currentSelectedText)) {
								currentSelectedText.setSelectionRange(0, 0);
							}
						}
					}
					currentSelectedText = c;
				}
			});

			styledText.setMenu(contextMenuManager.createContextMenu(styledText));
			stext = styledText;
		} else if (style.equalsIgnoreCase(PROPERTY)) {
			StyledText styledText = new StyledText(composite, SWT.MULTI | SWT.READ_ONLY);
			styledText.setFont(TEXT_FONT);
			styledText.setText(checkText(text));
			styledText.setBackground(background);
			data.horizontalIndent = HORZ_INDENT;
			styledText.setLayoutData(data);
			StyleRange sr = new StyleRange(styledText.getOffsetAtLine(0), text.length(), foreground, background,
					SWT.BOLD);
			styledText.setStyleRange(sr);
			styledText.getCaret().setVisible(false);
			styledText.setEnabled(false);

			styledText.setMenu(contextMenuManager.createContextMenu(styledText));
			stext = styledText;
		} else {
			Composite generalTitleGroup = new Composite(composite, SWT.NONE);
			generalTitleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			generalTitleGroup.setLayoutData(data);
			GridLayout generalTitleLayout = new GridLayout();
			generalTitleLayout.numColumns = 2;
			generalTitleLayout.marginWidth = 0;
			generalTitleLayout.marginHeight = 9;
			generalTitleGroup.setLayout(generalTitleLayout);
			generalTitleGroup.setBackground(background);

			Label image = new Label(generalTitleGroup, SWT.NONE);
			image.setBackground(background);
			image.setImage(WorkbenchImages.getImage(IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM));

			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
			image.setLayoutData(gd);
			StyledText titleText = new StyledText(generalTitleGroup, SWT.MULTI | SWT.READ_ONLY);
			titleText.setText(checkText(text));
			titleText.setFont(HEADER_FONT);
			titleText.setBackground(background);
			StyleRange sr = new StyleRange(titleText.getOffsetAtLine(0), text.length(), foreground, background,
					SWT.BOLD);
			titleText.setStyleRange(sr);
			titleText.getCaret().setVisible(false);
			titleText.setEditable(false);
			titleText.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					StyledText c = (StyledText) e.widget;
					if (c != null && c.getSelectionCount() > 0) {
						if (currentSelectedText != null) {
							if (!c.equals(currentSelectedText)) {
								currentSelectedText.setSelectionRange(0, 0);
							}
						}
					}
					currentSelectedText = c;
				}
			});
			// create context menu
			generalTitleGroup.setMenu(contextMenuManager.createContextMenu(generalTitleGroup));
			titleText.setMenu(contextMenuManager.createContextMenu(titleText));
			image.setMenu(contextMenuManager.createContextMenu(image));
			stext = titleText;
		}
		composite.setMenu(contextMenuManager.createContextMenu(composite));
		return stext;
	}

//	/**
//	 * This creates the title header for the info area. Its style is similar to
//	 * one from calling the function <code>newLayout</code> with the style
//	 * <code>HEADER</code>.
//	 * 
//	 * @param composite
//	 *            The composite to put this text field into. Its layout style
//	 *            should be a grid with columns.
//	 */
//	protected void newAttributesLayout(Composite composite) {
//		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
//		data.horizontalSpan = 4;
//		Composite generalTitleGroup = new Composite(composite, SWT.NONE);
//		generalTitleGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		generalTitleGroup.setLayoutData(data);
//		GridLayout generalTitleLayout = new GridLayout();
//		generalTitleLayout.numColumns = 3;
//		generalTitleLayout.marginWidth = 0;
//		generalTitleLayout.marginHeight = 9;
//		generalTitleGroup.setLayout(generalTitleLayout);
//		generalTitleGroup.setBackground(background);
//
//		Label image = new Label(generalTitleGroup, SWT.NONE);
//		image.setBackground(background);
//		image.setImage(WorkbenchImages.getImage(IDEInternalWorkbenchImages.IMG_OBJS_WELCOME_ITEM));
//
//		GridData gd = new GridData(GridData.FILL_BOTH);
//		gd.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
//		image.setLayoutData(gd);

//		generalTitleText = new StyledText(generalTitleGroup, SWT.MULTI | SWT.READ_ONLY);
//		generalTitleText.setBackground(background);
//		generalTitleText.getCaret().setVisible(false);
//		generalTitleText.setEditable(false);
//		generalTitleText.addSelectionListener(new SelectionAdapter() {
//
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				StyledText c = (StyledText) e.widget;
//				if (c != null && c.getSelectionCount() > 0) {
//					if (currentSelectedText != null) {
//						if (!c.equals(currentSelectedText)) {
//							currentSelectedText.setSelectionRange(0, 0);
//						}
//					}
//				}
//				currentSelectedText = c;
//			}
//		});
		// create context menu
//		generalTitleGroup.setMenu(contextMenuManager.createContextMenu(generalTitleGroup));
//		generalTitleText.setMenu(contextMenuManager.createContextMenu(generalTitleText));

//		linkToBug = new Hyperlink(generalTitleGroup, SWT.MULTI | SWT.READ_ONLY);
//		linkToBug.setBackground(background);

//		setGeneralTitleText();

//		image.setMenu(contextMenuManager.createContextMenu(image));
//		composite.setMenu(contextMenuManager.createContextMenu(composite));
//	}

	/**
	 * This refreshes the text in the title label of the info area (it contains
	 * elements which can change).
	 */
	protected void setGeneralTitleText() {
//		String text = "[Open in Internal Browser]";
//		linkToBug.setText(text);
//		linkToBug.setFont(TEXT_FONT);
//		if (this instanceof ExistingBugEditor) {
//			linkToBug.setUnderlined(true);
//			linkToBug.setForeground(JFaceColors.getHyperlinkText(Display.getCurrent()));
//			linkToBug.addMouseListener(new MouseListener() {
//
//				public void mouseDoubleClick(MouseEvent e) {
//				}
//
//				public void mouseUp(MouseEvent e) {
//				}
//
//				public void mouseDown(MouseEvent e) {
//					TaskListUiUtil.openUrl(getTitle(), getTitleToolTip(), BugzillaRepositoryUtil.getBugUrlWithoutLogin(
//							bugzillaInput.getBug().getRepositoryUrl(), bugzillaInput.getBug().getId()));
//					if (e.stateMask == SWT.MOD3) {
//						// XXX come back to look at this ui
//						close();
//					}
//
//				}
//			});
//		} else {
//			linkToBug.setEnabled(false);
//		}
//		linkToBug.addListener(SWT.FocusIn, new GenericListener());
//
//		// Resize the composite, in case the new summary is longer than the
//		// previous one.
//		// Then redraw it to show the changes.
//		linkToBug.getParent().pack(true);
//		linkToBug.redraw();

//		String text = getTitleString();
//		generalTitleText.setText(text);
//		StyleRange sr = new StyleRange(generalTitleText.getOffsetAtLine(0), text.length(), foreground, background,
//				SWT.BOLD);
//		generalTitleText.setStyleRange(sr);
//		generalTitleText.addListener(SWT.FocusIn, new GenericListener());
//
//		// Resize the composite, in case the new summary is longer than the
//		// previous one.
//		// Then redraw it to show the changes.
//		generalTitleText.getParent().pack(true);
//		generalTitleText.redraw();
	}

	/**
	 * Creates some blank space underneath the supplied composite.
	 * 
	 * @param parent
	 *            The composite to add the blank space to.
	 */
	protected void createSeparatorSpace(Composite parent) {
		GridData separatorData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		separatorData.verticalSpan = 1;
		separatorData.grabExcessVerticalSpace = false;

		Composite separatorComposite = new Composite(parent, SWT.NONE);
		GridLayout separatorLayout = new GridLayout();
		separatorLayout.marginHeight = 0;
		separatorLayout.verticalSpacing = 0;
		separatorComposite.setLayout(separatorLayout);
		separatorComposite.setBackground(background);
		separatorComposite.setLayoutData(separatorData);
		newLayout(separatorComposite, 1, "", VALUE);
	}

	/**
	 * Submit the changes to the bug to the bugzilla server.
	 * (Public for testing purposes)
	 */
	protected abstract void submitBug();

	/**
	 * If there is no locally saved copy of the current bug, then it saved
	 * offline. Otherwise, any changes are updated in the file.
	 */
	public void saveBug() {
		try {
			updateBug();
			IBugzillaBug bug = getBug();

//			if (bug.hasChanges()) {
//				BugzillaPlugin.getDefault().fireOfflineStatusChanged(bug,
//						BugzillaOfflineStaus.SAVED_WITH_OUTGOING_CHANGES);
//			} else {
//				BugzillaPlugin.getDefault().fireOfflineStatusChanged(bug, BugzillaOfflineStaus.SAVED);
//			}
			final BugzillaRepositoryConnector bugzillaRepositoryClient = (BugzillaRepositoryConnector)MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
			changeDirtyStatus(false);
			bugzillaRepositoryClient.saveBugReport(bug);//OfflineView.saveOffline(getBug(), true);
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "bug save offline failed", true);
		}
		// OfflineView.checkWindow();
		// OfflineView.refreshView();
	}

	/**
	 * Updates the <code>IBugzillaBug</code> object to contain the latest data
	 * entered in the data fields.
	 */
	protected abstract void updateBug();

	/**
	 * Resets the data fields to contain the data currently in the
	 * <code>IBugzillaBug</code> object.
	 */
	protected abstract void restoreBug();

	/**
	 * Refreshes any text labels in the editor that contain information that
	 * might change.
	 */
	protected void updateEditor() {
		// Reset all summary occurrences, since it might have
		// been edited.
//		String title = getTitleString();
//		titleLabel.setText(title);
		setGeneralTitleText();
	}

//	/**
//	 * Break text up into lines of about 80 characters so that it is displayed
//	 * properly in bugzilla
//	 * 
//	 * @param origText
//	 *            The string to be formatted
//	 * @return The formatted text
//	 */
//	public static String formatText(String origText) {
//		if (BugzillaPlugin.getDefault().isServerCompatability220()) {
//			return origText;
//		}
//
//		String[] textArray = new String[(origText.length() / WRAP_LENGTH + 1) * 2];
//		for (int i = 0; i < textArray.length; i++)
//			textArray[i] = null;
//		int j = 0;
//		while (true) {
//			int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5);
//			if (spaceIndex == origText.length() || spaceIndex == -1) {
//				textArray[j] = origText;
//				break;
//			}
//			textArray[j] = origText.substring(0, spaceIndex);
//			origText = origText.substring(spaceIndex + 1, origText.length());
//			j++;
//		}
//
//		String newText = "";
//
//		for (int i = 0; i < textArray.length; i++) {
//			if (textArray[i] == null)
//				break;
//			newText += textArray[i] + "\n";
//		}
//		return newText;
//	}

//	/**
//	 * function to set the url to post the bug to
//	 * 
//	 * @param form
//	 *            A reference to a BugzillaReportSubmitForm that the bug is going to
//	 *            be posted to
//	 * @param formName
//	 *            The form that we wish to use to submit the bug
//	 */
//	public static void setURL(BugzillaReportSubmitForm form, TaskRepository repository, String formName) {
//		// String baseURL = BugzillaPlugin.getDefault().getServerName();
//		String baseURL = repository.getUrl().toExternalForm();
//		if (!baseURL.endsWith("/"))
//			baseURL += "/";
//		try {
//			form.setURL(baseURL + formName);
//		} catch (MalformedURLException e) {
//			// we should be ok here
//		}
//
//		// add the login information to the bug post
//		form.add("Bugzilla_login", repository.getUserName());
//		form.add("Bugzilla_password", repository.getPassword());
//	}

	@Override
	public void setFocus() {
		scrolledComposite.setFocus();
	}

	@Override
	public boolean isDirty() {
		return isDirty;
	}

	/**
	 * Updates the dirty status of this editor page. The dirty status is true if
	 * the bug report has been modified but not saved. The title of the editor
	 * is also updated to reflect the status.
	 * 
	 * @param newDirtyStatus
	 *            is true when the bug report has been modified but not saved
	 */
	public void changeDirtyStatus(boolean newDirtyStatus) {
		isDirty = newDirtyStatus;
		if (parentEditor == null) {
			firePropertyChange(PROP_DIRTY);
		} else {
			parentEditor.markDirty();
		}

	}

	/**
	 * Updates the title of the editor to reflect dirty status. If the bug
	 * report has been modified but not saved, then an indicator will appear in
	 * the title.
	 */
	protected void updateEditorTitle() {
		setPartName(bugzillaInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		saveBug();
		updateEditor();

		// XXX notify that saved ofline?
	}

	@Override
	public void doSaveAs() {
		// we don't save, so no need to implement
	}

	/**
	 * @return The composite for the whole editor.
	 */
	public Composite getEditorComposite() {
		return editorComposite;
	}

	@Override
	public void dispose() {
		super.dispose();
		isDisposed = true;
		getSite().getPage().removeSelectionListener(selectionListener);
	}

	public void handleEvent(Event event) {
		if (event.widget instanceof Combo) {
			Combo combo = (Combo) event.widget;
			if (comboListenerMap.containsKey(combo)) {
				String sel = combo.getItem(combo.getSelectionIndex());
				Attribute attribute = getBug().getAttribute(comboListenerMap.get(combo));
				if (sel != null && !(sel.equals(attribute.getNewValue()))) {
					attribute.setNewValue(sel);
					for (IBugzillaAttributeListener client : attributesListeners) {
						client.attributeChanged(attribute.getName(), sel);
					}
					changeDirtyStatus(true);
				}
			}
		}
	}

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered
	 * under <code>selectionChangedListeners</code>.
	 * 
	 * @param event
	 *            The selection event.
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			final ISelectionChangedListener l = (ISelectionChangedListener) listeners[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					l.selectionChanged(event);
				}
			});
		}
	}

	/**
	 * A generic listener for selection of unimportant items. The default
	 * selection item sent out is the entire bug object.
	 */
	protected class GenericListener implements Listener {
		public void handleEvent(Event event) {
			IBugzillaBug bug = getBug();
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new BugzillaReportSelection(bug.getId(), bug.getRepositoryUrl(), bug.getLabel(), false, bug
							.getSummary()))));
		}
	}

	/**
	 * A listener to check if the summary field was modified.
	 */
	protected class SummaryListener implements Listener {
		public void handleEvent(Event event) {
			handleSummaryEvent();
		}
	}

	/**
	 * Check if the summary field was modified, and update it if necessary.
	 */
	public abstract void handleSummaryEvent();

	/*----------------------------------------------------------*
	 * CODE TO SCROLL TO A COMMENT OR OTHER PIECE OF TEXT
	 *----------------------------------------------------------*/

	/** List of the StyledText's so that we can get the previous and the next */
	protected ArrayList<StyledText> texts = new ArrayList<StyledText>();

	protected HashMap<Object, StyledText> textHash = new HashMap<Object, StyledText>();

	/** Index into the styled texts */
	protected int textsindex = 0;

	protected Text addCommentsTextBox = null;

	protected Text descriptionTextBox = null;

	private StyledText previousText = null;

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param commentNumber
	 *            The comment number to be selected
	 */
	public void select(int commentNumber) {
		if (commentNumber == -1)
			return;

		for (Object o : textHash.keySet()) {
			if (o instanceof Comment) {
				if (((Comment) o).getNumber() == commentNumber) {
					select(o, true);
				}
			}
		}
	}

	/**
	 * Selects the given object in the editor.
	 * 
	 * @param o
	 *            The object to be selected.
	 * @param highlight
	 *            Whether or not the object should be highlighted.
	 */
	public void select(Object o, boolean highlight) {
		if (textHash.containsKey(o)) {
			StyledText t = textHash.get(o);
			if (t != null) {
				focusOn(t, highlight);
			}
		} else if (o instanceof IBugzillaBug) {
			focusOn(null, highlight);
		}
	}

	public void selectDescription() {
		for (Object o : textHash.keySet()) {
			if (o.equals(bugzillaInput.getBug().getDescription())) {
				select(o, true);
			}
		}
	}

	public void selectNewComment() {
		focusOn(addCommentsTextBox, false);
	}

	public void selectNewDescription() {
		focusOn(descriptionTextBox, false);
	}

	/**
	 * Scroll to a specified piece of text
	 * 
	 * @param selectionComposite
	 *            The StyledText to scroll to
	 */
	private void focusOn(Control selectionComposite, boolean highlight) {

		int pos = 0;
		if (previousText != null && !previousText.isDisposed()) {
			previousText.setSelection(0);
		}

		if (selectionComposite instanceof StyledText)
			previousText = (StyledText) selectionComposite;

		if (selectionComposite != null) {

			if (highlight && selectionComposite instanceof StyledText && !selectionComposite.isDisposed())
				((StyledText) selectionComposite).setSelection(0, ((StyledText) selectionComposite).getText().length());

			// get the position of the text in the composite
			pos = 0;
			Control s = selectionComposite;
			if (s.isDisposed())
				return;
			s.setEnabled(true);
			s.setFocus();
			s.forceFocus();
			while (s != null && s != getEditorComposite()) {
				if (!s.isDisposed()) {
					pos += s.getLocation().y;
					s = s.getParent();
				}
			}

			pos = scrolledComposite.getOrigin().y + pos - 60;
		}
		if (!scrolledComposite.isDisposed())
			scrolledComposite.setOrigin(0, pos);
	}

	private BugzillaOutlinePage outlinePage = null;

	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && bugzillaInput != null) {
				outlinePage = new BugzillaOutlinePage(bugzillaOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public BugzillaOutlineNode getOutlineModel() {
		return bugzillaOutlineModel;
	}

	public BugzillaOutlinePage getOutline() {
		return outlinePage;
	}

	private boolean isDisposed = false;

	public boolean isDisposed() {
		return isDisposed;
	}

	public void close() {
		Display activeDisplay = getSite().getShell().getDisplay();
		activeDisplay.asyncExec(new Runnable() {
			public void run() {
				if (getSite() != null && getSite().getPage() != null && !AbstractBugEditor.this.isDisposed())
					if (parentEditor != null) {
						getSite().getPage().closeEditor(parentEditor, false);
					} else {
						getSite().getPage().closeEditor(AbstractBugEditor.this, false);
					}
			}
		});
	}

	public void addAttributeListener(IBugzillaAttributeListener listener) {
		attributesListeners.add(listener);
	}

	public void removeAttributeListener(IBugzillaAttributeListener listener) {
		attributesListeners.remove(listener);
	}

	public void setParentEditor(MylarTaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	public BugzillaOutlineNode getBugzillaOutlineModel() {
		return bugzillaOutlineModel;
	}

	public void setBugzillaOutlineModel(BugzillaOutlineNode bugzillaOutlineModel) {
		this.bugzillaOutlineModel = bugzillaOutlineModel;
	}
}
