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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.bugzilla.core.AbstractRepositoryReport;
import org.eclipse.mylar.bugzilla.core.AbstractRepositoryReportAttribute;
import org.eclipse.mylar.bugzilla.core.BugzillaReport;
import org.eclipse.mylar.bugzilla.core.Comment;
import org.eclipse.mylar.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaTools;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaAttributeListener;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaReportSelection;
import org.eclipse.mylar.internal.bugzilla.core.internal.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.ui.editors.MylarTaskEditor;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * Abstract base implementation for an editor to view a bugzilla report.
 * 
 * @author Mik Kersten (some hardening of prototype)
 * @author Rob Elves (Conversion to Eclipse Forms)
 */
public abstract class AbstractBugEditor extends EditorPart {

	public static final String REPOSITORY_TEXT_ID = "org.eclipse.mylar.tasklist.ui.fonts.task.editor.comment";

	public static final String HYPERLINK_TYPE_TASK = "task";

	public static final String HYPERLINK_TYPE_JAVA = "java";

	private static final String LABEL_BUTTON_SUBMIT = "Submit to Repository";

	private static final String LABEL_SECTION_ACTIONS = "Actions";

	private static final String LABEL_SECTION_ATTRIBUTES = "Attributes";

	protected static final String LABEL_SECTION_DESCRIPTION = "Description";

	protected static final String LABEL_SECTION_COMMENTS = "Comments";

	protected static final String LABEL_SECTION_NEW_COMMENT = "New Comment";

	private FormToolkit toolkit;

	private ScrolledForm form;

	protected TaskRepository repository;

	public static final int WRAP_LENGTH = 90;

	protected Display display;

	public static final Font TITLE_FONT = JFaceResources.getBannerFont();

	public static final Font TEXT_FONT = JFaceResources.getDefaultFont();

	public static final Font COMMENT_FONT = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);

	public static final Font HEADER_FONT = JFaceResources.getDefaultFont();

	public static final int DESCRIPTION_WIDTH = 500;//79 * 7;

	public static final int DESCRIPTION_HEIGHT = 10 * 14;

	// protected Color background;
	//
	// protected Color foreground;

	protected AbstractBugEditorInput bugzillaInput;

	private MylarTaskEditor parentEditor = null;

	protected BugzillaOutlineNode bugzillaOutlineModel = null;

	// private static int MARGIN = 0;// 5

	protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd, yyyy hh:mm aa");// "yyyy-MM-dd

	// HH:mm"

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

	protected CCombo attributeCombo;

	protected CCombo versionCombo;

	protected CCombo platformCombo;

	protected CCombo priorityCombo;

	protected CCombo severityCombo;

	protected CCombo milestoneCombo;

	protected CCombo componentCombo;

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

	// private Action revealAllAction;

	protected RetargetAction pasteAction;

	protected Composite editorComposite;

	// protected CLabel titleLabel;

	// protected ScrolledComposite scrolledComposite;

	// protected Composite scrolledComposite;

	// protected Composite infoArea;

	// protected Hyperlink linkToBug;

	// protected StyledText generalTitleText;

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

	protected List<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	protected HashMap<CCombo, AbstractRepositoryReportAttribute> comboListenerMap = new HashMap<CCombo, AbstractRepositoryReportAttribute>();

	private IBugzillaReportSelection lastSelected = null;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof BugzillaOutlineNode) {
					BugzillaOutlineNode n = (BugzillaOutlineNode) select;

					if (n != null && lastSelected != null
							&& BugzillaTools.getHandle(n).equals(BugzillaTools.getHandle(lastSelected))) {
						// we don't need to set the selection if it is already
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

	private class ComboSelectionListener extends SelectionAdapter {

		private CCombo combo;

		public ComboSelectionListener(CCombo combo) {
			this.combo = combo;
		}

		public void widgetDefaultSelected(SelectionEvent event) {
			// ignore
		}

		public void widgetSelected(SelectionEvent event) {
			if (comboListenerMap.containsKey(combo)) {
				if (combo.getSelectionIndex() > -1) {
					String sel = combo.getItem(combo.getSelectionIndex());
					AbstractRepositoryReportAttribute attribute = comboListenerMap.get(combo);
					if (sel != null && !(sel.equals(attribute.getValue()))) {
						attribute.setValue(sel);
						for (IBugzillaAttributeListener client : attributesListeners) {
							client.attributeChanged(attribute.getName(), sel);
						}
						changeDirtyStatus(true);
					}
				}
			}
		}
	}

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

		//		
		// revealAllAction = new ExpandCommentsAction(this);
		// revealAllAction.setText("Reveal Comments");//
		// WorkbenchMessages.getString("Workbench.copy"));
		// revealAllAction.setImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		// revealAllAction.setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		// revealAllAction.setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		// revealAllAction.setAccelerator(SWT.CTRL | 'r');
		//
		// revealAllAction.setEnabled(true);

	}

	/**
	 * @return The bug this editor is displaying.
	 */
	public abstract IBugzillaBug getBug();

	// TODO: temporary as part of conversion to xml
	public AbstractRepositoryReport getReport() {
		return (AbstractRepositoryReport) getBug();
	}

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

		if (getBug() == null) {
			// close();
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Bugzilla Client Errror",
					"Could not resolve the requested bug, check Bugzilla server and version.");

			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout());
			Label noBugLabel = new Label(composite, SWT.NULL);
			noBugLabel.setText("Could not resolve bug");
			return;
		}

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
//		String truncatedSummary = getBug().getSummary();
//		int maxLength = 50;
//		if (truncatedSummary.length() > maxLength) {
//			truncatedSummary = truncatedSummary.substring(0, maxLength) + "...";
//		}
		form.setText("Bugzilla Bug: " + getBug().getSummary());

		editorComposite = form.getBody();
		editorComposite.setLayout(new GridLayout());
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Header information
		Composite headerInfoComposite = toolkit.createComposite(editorComposite);
		headerInfoComposite.setLayout(new GridLayout(6, false));
		toolkit.createLabel(headerInfoComposite, "Bug# ").setFont(TITLE_FONT);
		toolkit.createText(headerInfoComposite, "" + getReport().getId());

		toolkit.createLabel(headerInfoComposite, " Opened: ").setFont(TITLE_FONT);
		String openedDateString = "";
		if (getBug().getCreated() != null) {
			openedDateString = simpleDateFormat.format(getBug().getCreated());
		}
		toolkit.createText(headerInfoComposite, openedDateString);

		toolkit.createLabel(headerInfoComposite, " Modified: ").setFont(TITLE_FONT);
		String lastModifiedDateString = "";
		if (getBug().getLastModified() != null) {
			lastModifiedDateString = simpleDateFormat.format(getBug().getLastModified());
		}
		toolkit.createText(headerInfoComposite, lastModifiedDateString);

		// openedText.setFont(TITLE_FONT);
		// display = parent.getDisplay();
		// background = JFaceColors.getBannerBackground(display);
		// foreground = JFaceColors.getBannerForeground(display);

		// createInfoArea(editorComposite);
		createContextMenu();
		createAttributeLayout();
		createDescriptionLayout(toolkit, form);
		createCommentLayout(toolkit, form);
		createButtonLayouts(toolkit, form.getBody());

		WorkbenchHelpSystem.getInstance().setHelp(parent, IBugzillaConstants.EDITOR_PAGE_CONTEXT);

		editorComposite.setMenu(contextMenuManager.createContextMenu(editorComposite));
		form.reflow(true);
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
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
				// manager.add(revealAllAction);
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

	// /**
	// * Creates the attribute layout, which contains most of the basic
	// attributes
	// * of the bug (some of which are editable).
	// */
	// protected void createAttributeLayout() {
	//
	// String title = getTitleString();
	// String keywords = "";
	// String url = "";
	//
	// Section section = toolkit.createSection(form.getBody(),
	// ExpandableComposite.TITLE_BAR | Section.TWISTIE);
	// section.setText(LABEL_SECTION_ATTRIBUTES);
	// section.setExpanded(true);
	// section.setLayout(new GridLayout());
	// section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	//
	// section.addExpansionListener(new IExpansionListener() {
	// public void expansionStateChanging(ExpansionEvent e) {
	// form.reflow(true);
	// }
	//
	// public void expansionStateChanged(ExpansionEvent e) {
	// form.reflow(true);
	// }
	// });
	//
	// // Attributes Composite- this holds all the combo fiels and text fields
	// Composite attributesComposite = toolkit.createComposite(section);
	// GridLayout attributesLayout = new GridLayout();
	// attributesLayout.numColumns = 4;
	// attributesLayout.horizontalSpacing = 14;
	// attributesLayout.verticalSpacing = 6;
	// attributesComposite.setLayout(attributesLayout);
	// GridData attributesData = new GridData(GridData.FILL_BOTH);
	// attributesData.horizontalSpan = 1;
	// attributesData.grabExcessVerticalSpace = false;
	// attributesComposite.setLayoutData(attributesData);
	// // attributesComposite.setBackground(background);
	// // End Attributes Composite
	//
	// section.setClient(attributesComposite);
	//
	// // Attributes Title Area
	// // Composite attributesTitleComposite = new
	// // Composite(attributesComposite, SWT.NONE);
	// // GridLayout attributesTitleLayout = new GridLayout();
	// // attributesTitleLayout.horizontalSpacing = 0;
	// // attributesTitleLayout.marginWidth = 0;
	// // attributesTitleComposite.setLayout(attributesTitleLayout);
	// // attributesTitleComposite.setBackground(background);
	// // GridData attributesTitleData = new
	// // GridData(GridData.HORIZONTAL_ALIGN_FILL);
	// // attributesTitleData.horizontalSpan = 4;
	// // attributesTitleData.grabExcessVerticalSpace = false;
	// // attributesTitleComposite.setLayoutData(attributesTitleData);
	// // End Attributes Title
	//
	// // Set the Attributes Title
	// // newAttributesLayout(attributesTitleComposite);
	// // titleLabel.setText(title);
	// bugzillaInput.setToolTipText(title);
	// int currentCol = 1;
	//
	// // String ccValue = null;
	//
	// // Populate Attributes
	// for (Iterator<AbstractRepositoryReportAttribute> it =
	// getReport().getAttributes().iterator(); it.hasNext();) {
	// AbstractRepositoryReportAttribute attribute = it.next();
	// String key = attribute.getID();
	// String name = attribute.getName();
	// String value = checkText(attribute.getValue());
	// System.err.println(">>> AbstractBugEditor>> name: "+name+" key: "+key+"
	// value:"+value);
	// Map<String, String> values = attribute.getOptionValues();
	//
	// // make sure we don't try to display a hidden field
	// if (attribute.isHidden() || (key != null &&
	// key.equals("status_whiteboard")))
	// continue;
	//
	// if (values == null)
	// values = new HashMap<String, String>();
	//
	// if (key == null)
	// key = "";
	//
	// GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	// data.horizontalSpan = 1;
	// data.horizontalIndent = HORZ_INDENT;
	//
	// if (key.equals(BugzillaReportElement.KEYWORDS.getKeyString())) {
	// keywords = attribute.getValue();
	// } else if (key.equals(BugzillaReportElement.CC.getKeyString())) {
	// continue;
	// } else if (key.equals(BugzillaReportElement.NEWCC.getKeyString())) {
	// // force move to first column
	// if (currentCol > 1) {
	// while (currentCol <= attributesLayout.numColumns) {
	// newLayout(attributesComposite, 1, "", PROPERTY);
	// currentCol++;
	// }
	// }
	// addCCList(toolkit, "", attributesComposite);
	// } else if (key.equals(BugzillaReportElement.DEPENDSON.getKeyString())) {
	// // Dependson and blocked are multi valued so need to explicitly
	// // be parsed and shown in the AbstractBugEditor
	// continue;
	// } else if (key.equals(BugzillaReportElement.BLOCKED.getKeyString())) {
	// // Dependson and blocked are multi valued so need to explicitly
	// // be parsed and shown in the AbstractBugEditor
	// continue;
	// } else if (key.equals("bug_file_loc")) {
	// url = value;
	// } else if (key.equals("op_sys")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// // oSCombo = new Combo(attributesComposite, SWT.NO_BACKGROUND |
	// // SWT.MULTI | SWT.V_SCROLL | SWT.READ_ONLY);//SWT.NONE
	// oSCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
	// // oSCombo = new Combo(attributesComposite, SWT.FLAT |
	// // SWT.READ_ONLY);
	// toolkit.adapt(oSCombo, true, true);
	// oSCombo.setFont(TEXT_FONT);
	// oSCombo.setLayoutData(data);
	// // oSCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// oSCombo.add(a[i]);
	// }
	// if (oSCombo.indexOf(value) != -1) {
	// oSCombo.select(oSCombo.indexOf(value));
	// } else {
	// oSCombo.select(oSCombo.indexOf("All"));
	// }
	// // oSCombo.addListener(SWT.Modify, this);
	// oSCombo.addSelectionListener(new ComboSelectionListener(oSCombo));
	// comboListenerMap.put(oSCombo, attribute);
	// oSCombo.addListener(SWT.FocusIn, new GenericListener());
	// currentCol += 2;
	// } else if (key.equals("version")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// versionCombo = new CCombo(attributesComposite, SWT.FLAT |
	// SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
	// | SWT.READ_ONLY);
	// toolkit.adapt(versionCombo, true, true);
	// versionCombo.setFont(TEXT_FONT);
	// versionCombo.setLayoutData(data);
	// // versionCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// versionCombo.add(a[i]);
	// }
	// versionCombo.select(versionCombo.indexOf(value));
	// // versionCombo.addListener(SWT.Modify, this);
	// versionCombo.addSelectionListener(new
	// ComboSelectionListener(versionCombo));
	// versionCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(versionCombo, attribute);
	// currentCol += 2;
	// } else if (key.equals("priority")) {
	// // newLayout(attributesComposite, 1, "Priority", PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// priorityCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.V_SCROLL |
	// SWT.READ_ONLY);
	// toolkit.adapt(priorityCombo, true, true);
	// priorityCombo.setFont(TEXT_FONT);
	// priorityCombo.setLayoutData(data);
	// // priorityCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// priorityCombo.add(a[i]);
	// }
	// priorityCombo.select(priorityCombo.indexOf(value));
	// // priorityCombo.addListener(SWT.Modify, this);
	// priorityCombo.addSelectionListener(new
	// ComboSelectionListener(priorityCombo));
	// priorityCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(priorityCombo, attribute);
	// currentCol += 2;
	// } else if (key.equals("bug_severity")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// severityCombo = new CCombo(attributesComposite, SWT.FLAT |
	// SWT.READ_ONLY);
	// toolkit.adapt(severityCombo, true, true);
	// severityCombo.setFont(TEXT_FONT);
	// severityCombo.setLayoutData(data);
	// // severityCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// severityCombo.add(a[i]);
	// }
	// severityCombo.select(severityCombo.indexOf(value));
	// severityCombo.addSelectionListener(new
	// ComboSelectionListener(severityCombo));
	// // severityCombo.addListener(SWT.Modify, this);
	// severityCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(severityCombo, attribute);
	// currentCol += 2;
	// } else if (key.equals("target_milestone")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// milestoneCombo = new CCombo(attributesComposite, SWT.FLAT |
	// SWT.NO_BACKGROUND | SWT.MULTI
	// | SWT.V_SCROLL | SWT.READ_ONLY);
	// toolkit.adapt(milestoneCombo, true, true);
	// milestoneCombo.setFont(TEXT_FONT);
	// milestoneCombo.setLayoutData(data);
	// // milestoneCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// milestoneCombo.add(a[i]);
	// }
	// milestoneCombo.select(milestoneCombo.indexOf(value));
	// // milestoneCombo.addListener(SWT.Modify, this);
	// milestoneCombo.addSelectionListener(new
	// ComboSelectionListener(milestoneCombo));
	// milestoneCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(milestoneCombo, attribute);
	// currentCol += 2;
	// } else if (key.equals("rep_platform")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// platformCombo = new CCombo(attributesComposite, SWT.FLAT |
	// SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
	// | SWT.READ_ONLY);
	// toolkit.adapt(platformCombo, true, true);
	// platformCombo.setFont(TEXT_FONT);
	// platformCombo.setLayoutData(data);
	// // platformCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// platformCombo.add(a[i]);
	// }
	// platformCombo.select(platformCombo.indexOf(value));
	// // platformCombo.addListener(SWT.Modify, this);
	// platformCombo.addSelectionListener(new
	// ComboSelectionListener(platformCombo));
	// platformCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(platformCombo, attribute);
	// currentCol += 2;
	// } else if (key.equals("product")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// // toolkit.createLabel(attributesComposite, value);
	// Composite uneditableComp = toolkit.createComposite(attributesComposite);
	// GridLayout textLayout = new GridLayout();
	// textLayout.marginWidth = 1;
	// uneditableComp.setLayout(textLayout);
	// toolkit.createText(uneditableComp, value, SWT.READ_ONLY);//
	// Label(attributesComposite,
	// // value);
	// // newLayout(attributesComposite, 1, value,
	// // VALUE).addListener(SWT.FocusIn, new GenericListener());
	// currentCol += 2;
	// } else if (key.equals("assigned_to")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// assignedTo = new Text(attributesComposite, SWT.SINGLE | SWT.WRAP);
	// assignedTo.setFont(TEXT_FONT);
	// assignedTo.setText(value);
	// data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
	// data.horizontalSpan = 1;
	// assignedTo.setLayoutData(data);
	//
	// assignedTo.addListener(SWT.KeyUp, new Listener() {
	// public void handleEvent(Event event) {
	// String sel = assignedTo.getText();
	// AbstractRepositoryReportAttribute a = getReport().getAttribute(
	// BugzillaReportElement.ASSIGNED_TO);
	// if (!(a.getValue().equals(sel))) {
	// a.setValue(sel);
	// changeDirtyStatus(true);
	// }
	// }
	// });
	// assignedTo.addListener(SWT.FocusIn, new GenericListener());
	//
	// currentCol += 2;
	// } else if (key.equals("component")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// componentCombo = new CCombo(attributesComposite, SWT.FLAT |
	// SWT.NO_BACKGROUND | SWT.MULTI
	// | SWT.V_SCROLL | SWT.READ_ONLY);
	// toolkit.adapt(componentCombo, true, true);
	// componentCombo.setFont(TEXT_FONT);
	// componentCombo.setLayoutData(data);
	// // componentCombo.setBackground(background);
	// Set<String> s = values.keySet();
	// String[] a = s.toArray(new String[s.size()]);
	// Arrays.sort(a);
	// for (int i = 0; i < a.length; i++) {
	// componentCombo.add(a[i]);
	// }
	// componentCombo.select(componentCombo.indexOf(value));
	// // componentCombo.addListener(SWT.Modify, this);
	// componentCombo.addSelectionListener(new
	// ComboSelectionListener(componentCombo));
	// componentCombo.addListener(SWT.FocusIn, new GenericListener());
	// comboListenerMap.put(componentCombo, attribute);
	// currentCol += 2;
	// } else if (name.equals("Summary")) {
	// // Don't show the summary here.
	// continue;
	// } else if (name.equals("Last Modified")) {
	// // Don't show last modified here.
	// continue;
	// } else if (name.equals("Bug#")) {
	// // Don't show bug number here
	// continue;
	// } else if (key.equals("bug_status")) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// Composite uneditableComp = toolkit.createComposite(attributesComposite);
	// GridLayout textLayout = new GridLayout();
	// textLayout.marginWidth = 1;
	// uneditableComp.setLayout(textLayout);
	// toolkit.createText(uneditableComp, value, SWT.READ_ONLY);//
	// Label(attributesComposite,
	// // value);
	// // newLayout(attributesComposite, 1, value,
	// // VALUE).addListener(SWT.FocusIn, new GenericListener());
	// currentCol += 2;
	// } else if (values.isEmpty()) {
	// // newLayout(attributesComposite, 1, name, PROPERTY);
	// toolkit.createLabel(attributesComposite, name);
	// Composite uneditableComp = toolkit.createComposite(attributesComposite);
	// GridLayout textLayout = new GridLayout();
	// textLayout.marginWidth = 1;
	// uneditableComp.setLayout(textLayout);
	// toolkit.createText(uneditableComp, value, SWT.READ_ONLY);//
	// Label(attributesComposite,
	// // value);
	// // newLayout(attributesComposite, 1, value,
	// // VALUE).addListener(SWT.FocusIn, new GenericListener());
	// currentCol += 2;
	// }
	// if (currentCol > attributesLayout.numColumns) {
	// currentCol -= attributesLayout.numColumns;
	// }
	// }
	// // End Populate Attributes
	//
	// // make sure that we are in the first column
	// if (currentCol > 1) {
	// while (currentCol <= attributesLayout.numColumns) {
	// newLayout(attributesComposite, 1, "", PROPERTY);
	// currentCol++;
	// }
	// }
	//
	// // URL field
	// addUrlText(url, attributesComposite);
	//
	// // keywords text field (not editable)
	// addKeywordsList(toolkit, keywords, attributesComposite);
	// // if (ccValue != null) {
	// // addCCList(toolkit, ccValue, attributesComposite);
	// // }
	// addSummaryText(attributesComposite);
	// // End URL, Keywords, Summary Text Fields
	// toolkit.paintBordersFor(attributesComposite);
	// }

	/**
	 * Creates the attribute layout, which contains most of the basic attributes
	 * of the bug (some of which are editable).
	 */
	protected void createAttributeLayout() {

		String title = getTitleString();
		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_ATTRIBUTES);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		// Attributes Composite- this holds all the combo fields and text fields
		Composite attributesComposite = toolkit.createComposite(section);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 14;
		attributesLayout.verticalSpacing = 6;
		attributesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		attributesComposite.setLayoutData(attributesData);
		section.setClient(attributesComposite);
		bugzillaInput.setToolTipText(title);

		int currentCol = 1;

		for (AbstractRepositoryReportAttribute attribute : getReport().getAttributes()) {

			// String key = attribute.getID();
			String name = attribute.getName();
			String value = checkText(attribute.getValue());
			// System.err.println(">>> AbstractBugEditor>> name: "+name+"
			// key:"+key+" value:"+value+" is hidden"+attribute.isHidden());
			if (attribute.isHidden())
				continue;
			Map<String, String> values = attribute.getOptionValues();
			if (values == null)
				values = new HashMap<String, String>();

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.horizontalSpan = 1;
			data.horizontalIndent = HORZ_INDENT;

			if (attribute.hasOptions() && !attribute.isReadOnly()) {
				toolkit.createLabel(attributesComposite, name);
				attributeCombo = new CCombo(attributesComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(attributeCombo, true, true);
				attributeCombo.setFont(TEXT_FONT);
				attributeCombo.setLayoutData(data);
				Set<String> s = values.keySet();
				String[] a = s.toArray(new String[s.size()]);
				Arrays.sort(a);
				for (int i = 0; i < a.length; i++) {
					attributeCombo.add(a[i]);
				}
				if (attributeCombo.indexOf(value) != -1) {
					attributeCombo.select(attributeCombo.indexOf(value));
				}
				attributeCombo.addSelectionListener(new ComboSelectionListener(attributeCombo));
				comboListenerMap.put(attributeCombo, attribute);
				attributeCombo.addListener(SWT.FocusIn, new GenericListener());
				currentCol += 2;
			} else {
				toolkit.createLabel(attributesComposite, name);
				Composite textFieldComposite = toolkit.createComposite(attributesComposite);
				GridLayout textLayout = new GridLayout();
				textLayout.marginWidth = 1;
				textFieldComposite.setLayout(textLayout);
				GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
				textData.horizontalSpan = 1;
				textData.widthHint = 135;

				if (attribute.isReadOnly()) {
					final Text text = toolkit.createText(textFieldComposite, value, SWT.FLAT | SWT.READ_ONLY);
					text.setLayoutData(textData);
				} else {
					final Text text = toolkit.createText(textFieldComposite, value, SWT.FLAT);
					text.setLayoutData(textData);
					toolkit.paintBordersFor(textFieldComposite);
					text.setData(attribute);
					text.addListener(SWT.KeyUp, new Listener() {
						public void handleEvent(Event event) {
							String sel = text.getText();
							AbstractRepositoryReportAttribute a = (AbstractRepositoryReportAttribute) text.getData();
							if (!(a.getValue().equals(sel))) {
								a.setValue(sel);
								changeDirtyStatus(true);
							}
						}
					});
					text.addListener(SWT.FocusIn, new GenericListener());
				}

				currentCol += 2;
			}
			if (currentCol > attributesLayout.numColumns) {
				currentCol -= attributesLayout.numColumns;
			}

		}

		// make sure that we are in the first column
		if (currentCol > 1) {
			while (currentCol <= attributesLayout.numColumns) {
				toolkit.createLabel(attributesComposite, "");
				// newLayout(attributesComposite, 1, "", PROPERTY);
				currentCol++;
			}
		}

		// Perhaps these should be performed in subclass eventually

		addCCList(toolkit, "", attributesComposite);

		// URL field
		addUrlText(getReport().getAttributeValue(BugzillaReportElement.BUG_FILE_LOC), attributesComposite);

		// keywords text field (not editable)
		addKeywordsList(toolkit, getReport().getAttributeValue(BugzillaReportElement.KEYWORDS), attributesComposite);

		addSummaryText(attributesComposite);
		// End URL, Keywords, Summary Text Fields
		toolkit.paintBordersFor(attributesComposite);
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
		// newLayout(attributesComposite, 1, "URL:", PROPERTY);
		toolkit.createLabel(attributesComposite, "URL:");
		urlText = toolkit.createText(attributesComposite, url);
		urlText.setFont(TEXT_FONT);
		GridData urlTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		urlTextData.horizontalSpan = 3;
		urlTextData.widthHint = 200;
		urlText.setLayoutData(urlTextData);
		// urlText.setText(url);
		urlText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				String sel = urlText.getText();
				AbstractRepositoryReportAttribute a = getReport().getAttribute(BugzillaReportElement.BUG_FILE_LOC);
				if (!(a.getValue().equals(sel))) {
					a.setValue(sel);
					changeDirtyStatus(true);
				}
			}
		});
		urlText.addListener(SWT.FocusIn, new GenericListener());
	}

	protected abstract void addKeywordsList(FormToolkit toolkit, String keywords, Composite attributesComposite);

	protected abstract void addCCList(FormToolkit toolkit, String value, Composite attributesComposite);

	/**
	 * Adds a text field to display and edit the bug's summary.
	 * 
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addSummaryText(Composite attributesComposite) {
		// newLayout(attributesComposite, 1, "Summary:", PROPERTY);
		toolkit.createLabel(attributesComposite, "Summary:");
		summaryText = toolkit.createText(attributesComposite, getBug().getSummary());// SWT.BORDER
		// |
		// SWT.SINGLE
		// |
		// SWT.WRAP
		summaryText.setFont(TEXT_FONT);
		GridData summaryTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		summaryTextData.horizontalSpan = 3;
		summaryTextData.widthHint = 200;
		summaryText.setLayoutData(summaryTextData);
		// summaryText.setText(getBug().getSummary());
		summaryText.addListener(SWT.KeyUp, new SummaryListener());
		summaryText.addListener(SWT.FocusIn, new GenericListener());
	}

	/**
	 * Creates the description layout, which displays and possibly edits the
	 * bug's description.
	 */
	protected abstract void createDescriptionLayout(FormToolkit toolkit, final ScrolledForm form);

	/**
	 * Creates the comment layout, which displays the bug's comments and
	 * possibly lets the user enter a new one.
	 */
	protected abstract void createCommentLayout(FormToolkit toolkit, final ScrolledForm form);

	/**
	 * Creates the button layout. This displays options and buttons at the
	 * bottom of the editor to allow actions to be performed on the bug.
	 */
	protected void createButtonLayouts(FormToolkit toolkit, Composite formComposite) {

		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_ACTIONS);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		Composite buttonComposite = toolkit.createComposite(section);
		GridLayout buttonLayout = new GridLayout();
		buttonLayout.numColumns = 4;
		buttonComposite.setLayout(buttonLayout);
		// buttonComposite.setBackground(background);
		GridData buttonData = new GridData(GridData.FILL_BOTH);
		buttonData.horizontalSpan = 1;
		buttonData.grabExcessVerticalSpace = false;
		buttonComposite.setLayoutData(buttonData);
		section.setClient(buttonComposite);
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
		submitButton = toolkit.createButton(buttonComposite, LABEL_BUTTON_SUBMIT, SWT.NONE);
		// submitButton.setFont(TEXT_FONT);
		GridData submitButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// submitButtonData.widthHint = AbstractBugEditor.WRAP_LENGTH;
		// submitButtonData.heightHint = 20;

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
	 */
	protected StyledText newLayout(Composite composite, int colSpan, String text, String style) {
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = colSpan;

		StyledText resultText;
		if (style.equalsIgnoreCase(VALUE)) {
			resultText = new StyledText(composite, SWT.READ_ONLY);
			resultText.setText(checkText(text));
			resultText.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					StyledText c = (StyledText) e.widget;
					if (c != null && !c.getSelectionText().equals("")) {
						if (currentSelectedText != null && !currentSelectedText.equals(c)) {
							currentSelectedText.setSelectionRange(0, 0);
						}
						currentSelectedText = c;
					}

				}
			});
			resultText.setLayoutData(data);
		} else if (style.equalsIgnoreCase(PROPERTY)) {
			resultText = new StyledText(composite, SWT.READ_ONLY);
			resultText.setText(checkText(text));
			resultText.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					StyledText c = (StyledText) e.widget;
					if (c != null && !c.getSelectionText().equals("")) {
						if (currentSelectedText != null && !currentSelectedText.equals(c)) {
							currentSelectedText.setSelectionRange(0, 0);
						}
						currentSelectedText = c;
					}

				}
			});
			resultText.setLayoutData(data);
		} else {
			resultText = new StyledText(composite, SWT.READ_ONLY);
			resultText.setText(checkText(text));
			resultText.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					StyledText c = (StyledText) e.widget;
					if (c != null && !c.getSelectionText().equals("")) {
						if (currentSelectedText != null && !currentSelectedText.equals(c)) {
							currentSelectedText.setSelectionRange(0, 0);
						}
						currentSelectedText = c;
					}

				}
			});
			resultText.setLayoutData(data);
		}

		// composite.setMenu(contextMenuManager.createContextMenu(composite));
		return resultText;
	}

	protected TextViewer addRepositoryText(TaskRepository repository, Composite composite, String text) {
		RepositoryTextViewer commentViewer = new RepositoryTextViewer(repository, composite, SWT.WRAP);
		
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();

		commentViewer.getTextWidget().setFont(themeManager.getCurrentTheme().getFontRegistry().get(REPOSITORY_TEXT_ID));

		commentViewer.setEditable(false);
		commentViewer.getTextWidget().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				StyledText c = (StyledText) e.widget;
				if (c != null && !c.getSelectionText().equals("")) {
					if (currentSelectedText != null && !currentSelectedText.equals(c)) {
						currentSelectedText.setSelectionRange(0, 0);
					}
					currentSelectedText = c;
				}

			}
		});

		commentViewer.getTextWidget().setMenu(contextMenuManager.createContextMenu(commentViewer.getTextWidget()));

		// textViewer.getControl().setFont(COMMENT_FONT);
		commentViewer.setDocument(new Document(text));
		// commentViewer.activatePlugins();
		// textViewer.refresh();
		return commentViewer;
	}

	/**
	 * This refreshes the text in the title label of the info area (it contains
	 * elements which can change).
	 */
	protected void setGeneralTitleText() {
		// String text = "[Open in Internal Browser]";
		// linkToBug.setText(text);
		// linkToBug.setFont(TEXT_FONT);
		// if (this instanceof ExistingBugEditor) {
		// linkToBug.setUnderlined(true);
		// linkToBug.setForeground(JFaceColors.getHyperlinkText(Display.getCurrent()));
		// linkToBug.addMouseListener(new MouseListener() {
		//
		// public void mouseDoubleClick(MouseEvent e) {
		// }
		//
		// public void mouseUp(MouseEvent e) {
		// }
		//
		// public void mouseDown(MouseEvent e) {
		// TaskListUiUtil.openUrl(getTitle(), getTitleToolTip(),
		// BugzillaRepositoryUtil.getBugUrlWithoutLogin(
		// bugzillaInput.getBug().getRepositoryUrl(),
		// bugzillaInput.getBug().getId()));
		// if (e.stateMask == SWT.MOD3) {
		// // XXX come back to look at this ui
		// close();
		// }
		//
		// }
		// });
		// } else {
		// linkToBug.setEnabled(false);
		// }
		// linkToBug.addListener(SWT.FocusIn, new GenericListener());
		//
		// // Resize the composite, in case the new summary is longer than the
		// // previous one.
		// // Then redraw it to show the changes.
		// linkToBug.getParent().pack(true);
		// linkToBug.redraw();

		// String text = getTitleString();
		// generalTitleText.setText(text);
		// StyleRange sr = new StyleRange(generalTitleText.getOffsetAtLine(0),
		// text.length(), foreground, background,
		// SWT.BOLD);
		// generalTitleText.setStyleRange(sr);
		// generalTitleText.addListener(SWT.FocusIn, new GenericListener());
		//
		// // Resize the composite, in case the new summary is longer than the
		// // previous one.
		// // Then redraw it to show the changes.
		// generalTitleText.getParent().pack(true);
		// generalTitleText.redraw();
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
		// separatorComposite.setBackground(background);
		separatorComposite.setLayoutData(separatorData);
		newLayout(separatorComposite, 1, "", VALUE);
	}

	/**
	 * Submit the changes to the bug to the bugzilla server. (Public for testing
	 * purposes)
	 */
	protected abstract void submitBug();

	/**
	 * If there is no locally saved copy of the current bug, then it saved
	 * offline. Otherwise, any changes are updated in the file.
	 */
	public void saveBug() {
		try {
			updateBug();
			// IBugzillaBug bug = getBug();

			final BugzillaRepositoryConnector bugzillaRepositoryClient = (BugzillaRepositoryConnector) MylarTaskListPlugin
					.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
			changeDirtyStatus(false);
			bugzillaRepositoryClient.saveBugReport((BugzillaReport) getReport());
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "bug save offline failed", true);
		}

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
		// String title = getTitleString();
		// titleLabel.setText(title);
		setGeneralTitleText();
	}

	@Override
	public void setFocus() {
		form.setFocus();
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

	// public void handleEvent(Event event) {
	// if (event.widget instanceof CCombo) {
	// CCombo combo = (CCombo) event.widget;
	// if (comboListenerMap.containsKey(combo)) {
	// if (combo.getSelectionIndex() > -1) {
	// String sel = combo.getItem(combo.getSelectionIndex());
	// Attribute attribute = getBug().getAttribute(comboListenerMap.get(combo));
	// if (sel != null && !(sel.equals(attribute.getNewValue()))) {
	// attribute.setNewValue(sel);
	// for (IBugzillaAttributeListener client : attributesListeners) {
	// client.attributeChanged(attribute.getName(), sel);
	// }
	// changeDirtyStatus(true);
	// }
	// }
	// }
	// }
	// }

	/**
	 * Fires a <code>SelectionChangedEvent</code> to all listeners registered
	 * under <code>selectionChangedListeners</code>.
	 * 
	 * @param event
	 *            The selection event.
	 */
	protected void fireSelectionChanged(final SelectionChangedEvent event) {
		Object[] listeners = selectionChangedListeners.toArray();
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
			BugzillaReport bug = (BugzillaReport) getReport();
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

	// private FormText previousText = null;

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

	public void revealAllComments() {
		for (StyledText text : textHash.values()) {
			Composite comp = text.getParent();
			while (comp != null) {
				if (comp instanceof ExpandableComposite) {
					ExpandableComposite ex = (ExpandableComposite) comp;
					ex.setExpanded(true);
				}
				comp = comp.getParent();
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
				Composite comp = t.getParent();
				while (comp != null) {
					if (comp instanceof ExpandableComposite) {
						ExpandableComposite ex = (ExpandableComposite) comp;
						ex.setExpanded(true);
					}
					comp = comp.getParent();
				}
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
		// if (previousText != null && !previousText.isDisposed()) {
		// previousText.setsetSelection(0);
		// }

		// if (selectionComposite instanceof FormText)
		// previousText = (FormText) selectionComposite;

		if (selectionComposite != null) {

			// if (highlight && selectionComposite instanceof FormText &&
			// !selectionComposite.isDisposed())
			// ((FormText) selectionComposite).set.setSelection(0, ((FormText)
			// selectionComposite).getText().length());

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

			pos = pos - 60; // form.getOrigin().y;

		}
		if (!form.getBody().isDisposed())
			form.setOrigin(0, pos);
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

	// private void addHyperlinks(final StyledText styledText, Composite
	// composite) {
	//
	// StringMatcher javaElementMatcher = new StringMatcher("*(*.java:*)", true,
	// false);
	// String[] lines = styledText.getText().split("\r\n|\n");
	//
	// int totalLength = 0;
	// for (int x = 0; x < lines.length; x++) {
	//
	// String line = lines[x];
	// Position position = javaElementMatcher.find(line, 0, line.length());
	// if (position != null) {
	// String linkText = line.substring(position.getStart() + 1,
	// position.getEnd() - 1);
	// // Link hyperlink = new Link(styledText, SWT.NONE);
	// IRegion region = new Region(styledText.getText().indexOf(line) +
	// position.getStart(), position.getEnd()
	// - position.getStart());
	// addControl(styledText, region, linkText, line, HYPERLINK_TYPE_JAVA);
	// }
	//
	// IHyperlink[] bugHyperlinks = BugzillaUITools.findBugHyperlinks(0,
	// line.length(), line, 0);
	// if (bugHyperlinks != null) {
	// for (IHyperlink hyperlink : bugHyperlinks) {
	// String linkText = hyperlink.getHyperlinkText();
	// int index = linkText.lastIndexOf('=');
	// if (index >= 0) {
	// String taskId = linkText.substring(index + 1);
	// String href = repository.getUrl() + hyperlink.getHyperlinkText();
	// addControl(styledText, hyperlink.getHyperlinkRegion(), "bug# " + taskId,
	// href,
	// HYPERLINK_TYPE_TASK);
	// }
	//
	// }
	// }
	//
	// totalLength = totalLength + line.length();
	//
	// } // bottom of for loop
	//
	// // reposition widgets on paint event
	// styledText.addPaintObjectListener(new PaintObjectListener() {
	// public void paintObject(PaintObjectEvent event) {
	// StyleRange style = event.style;
	// int start = style.start;
	// Map<Integer, Control> controlMap = controls.get(styledText);
	// Control control = controlMap.get(start);
	// if (control != null) {
	// Point pt = control.getSize();
	// int x = event.x + MARGIN;
	// int y = event.y + event.ascent - 2 * pt.y / 3;
	// control.setLocation(x, y);
	// }
	// }
	// });
	// }

	// private void addControl(final StyledText styledText, IRegion region,
	// String linkText, String href,
	// final String listenerType) {
	// Hyperlink hyperlink = toolkit.createHyperlink(styledText, linkText,
	// SWT.NONE);
	// hyperlink.setText(linkText);
	// hyperlink.setFont(COMMENT_FONT);
	// hyperlink.setHref(href);
	// IHyperlinkListener hyperlinkListener =
	// MylarTaskListPlugin.getDefault().getTaskHyperlinkListeners().get(
	// listenerType);
	// if (hyperlinkListener != null) {
	// hyperlink.addHyperlinkListener(hyperlinkListener);
	// }
	// Map<Integer, Control> controlMap = controls.get(styledText);
	// if (controlMap == null) {
	// controlMap = new HashMap<Integer, Control>();
	// controls.put(styledText, controlMap);
	// }
	// controlMap.put(new Integer(region.getOffset()), hyperlink);
	// StyleRange style = new StyleRange();
	// style.start = region.getOffset();
	// style.length = region.getLength();
	// hyperlink.pack();
	// Rectangle rect = hyperlink.getBounds();
	// int ascent = 2 * rect.height / 3;
	// int descent = rect.height - ascent;
	// style.metrics = new GlyphMetrics(ascent + MARGIN, descent + MARGIN,
	// rect.width + 2 * MARGIN);
	// styledText.setStyleRange(style);
	// }
}
