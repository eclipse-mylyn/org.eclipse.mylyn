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
package org.eclipse.mylar.internal.tasklist.ui.editors;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.tasklist.Comment;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask.RepositoryTaskSyncState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.themes.IThemeManager;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public abstract class AbstractRepositoryTaskEditor extends EditorPart {

	protected static final String CONTEXT_MENU_ID = "#BugEditor";

	public static final String REPOSITORY_TEXT_ID = "org.eclipse.mylar.tasklist.ui.fonts.task.editor.comment";

	public static final String HYPERLINK_TYPE_TASK = "task";

	public static final String HYPERLINK_TYPE_JAVA = "java";

	private static final String LABEL_BUTTON_SUBMIT = "Submit to Repository";

	private static final String LABEL_SECTION_ACTIONS = "Actions";

	private static final String LABEL_SECTION_ATTRIBUTES = "Attributes";

	private static final String LABEL_SECTION_ATTACHMENTS = "Attachments";

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

	// public static final Font COMMENT_FONT =
	// JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);

	public static final Font HEADER_FONT = JFaceResources.getDefaultFont();

	public static final int DESCRIPTION_WIDTH = 79 * 8; // 500;

	public static final int DESCRIPTION_HEIGHT = 10 * 14;

	// protected Color background;
	//
	// protected Color foreground;

	protected AbstractBugEditorInput editorInput;

	private MylarTaskEditor parentEditor = null;

	protected RepositoryTaskOutlineNode taskOutlineModel = null;

	// private static int MARGIN = 0;// 5

	protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E MMM dd, yyyy hh:mm aa");

	// "yyyy-MM-dd HH:mm"

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

	// protected CCombo versionCombo;
	//
	// protected CCombo platformCombo;
	//
	// protected CCombo priorityCombo;
	//
	// protected CCombo severityCombo;
	//
	// protected CCombo milestoneCombo;
	//
	// protected CCombo componentCombo;

	protected Button addSelfToCCCheck;

	// protected Text urlText;

	protected Text summaryText;

	protected Text addCommentsText;

	// protected Text assignedTo;

	protected Text attachmentDesc;

	protected Text attachmentComment;

	protected Button submitButton;

	protected Table attachmentsTable;

	protected TableViewer attachmentTableViewer;

	protected String[] attachmentsColumns = { "Description", "Type", "Creator", "Created" };

	protected int[] attachmentsColumnWidths = { 200, 100, 100, 200 };

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

	protected RepositoryTaskEditorCopyAction copyAction;

	// private Action revealAllAction;

	protected RetargetAction pasteAction;

	protected Composite editorComposite;

	// protected CLabel titleLabel;

	// protected ScrolledComposite scrolledComposite;

	// protected Composite scrolledComposite;

	// protected Composite infoArea;

	// protected Hyperlink linkToBug;

	// protected StyledText generalTitleText;

	// private static List<String> contentTypes;

	private static Map<String, String> extensions2Types;

	static {
		/* For possible UI */
		// contentTypes = new LinkedList<String>();
		// contentTypes.add("text/plain");
		// contentTypes.add("text/html");
		// contentTypes.add("application/xml");
		// contentTypes.add("image/gif");
		// contentTypes.add("image/jpeg");
		// contentTypes.add("image/png");
		// contentTypes.add("application/octet-stream");
		extensions2Types = new HashMap<String, String>();
		extensions2Types.put("txt", "text/plain");
		extensions2Types.put("html", "text/html");
		extensions2Types.put("htm", "text/html");
		extensions2Types.put("jpg", "image/jpeg");
		extensions2Types.put("jpeg", "image/jpeg");
		extensions2Types.put("gif", "image/gif");
		extensions2Types.put("png", "image/png");
		extensions2Types.put("xml", "application/xml");
		extensions2Types.put("zip", "application/octet-stream");
		extensions2Types.put("tar", "application/octet-stream");
		extensions2Types.put("gz", "application/octet-stream");
	}

	private List<IRepositoryTaskAttributeListener> attributesListeners = new ArrayList<IRepositoryTaskAttributeListener>();

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

	protected HashMap<CCombo, RepositoryTaskAttribute> comboListenerMap = new HashMap<CCombo, RepositoryTaskAttribute>();

	private IRepositoryTaskSelection lastSelected = null;

	protected final ISelectionListener selectionListener = new ISelectionListener() {
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			if ((part instanceof ContentOutline) && (selection instanceof StructuredSelection)) {
				Object select = ((StructuredSelection) selection).getFirstElement();
				if (select instanceof RepositoryTaskOutlineNode) {
					RepositoryTaskOutlineNode n = (RepositoryTaskOutlineNode) select;

					if (n != null && lastSelected != null
							&& OutlineTools.getHandle(n).equals(OutlineTools.getHandle(lastSelected))) {
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
					RepositoryTaskAttribute attribute = comboListenerMap.get(combo);
					if (sel != null && !(sel.equals(attribute.getValue()))) {
						attribute.setValue(sel);
						for (IRepositoryTaskAttributeListener client : attributesListeners) {
							client.attributeChanged(attribute.getName(), sel);
						}
						changeDirtyStatus(true);
					}
				}
			}
		}
	}

	/**
	 * Creates a new <code>AbstractRepositoryTaskEditor</code>. Sets up the
	 * default fonts and cut/copy/paste actions.
	 */
	public AbstractRepositoryTaskEditor() {
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

		copyAction = new RepositoryTaskEditorCopyAction(this);
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
	 * @return The task data this editor is displaying.
	 */
	public abstract RepositoryTaskData getRepositoryTaskData();

	public String getNewCommentText() {
		return addCommentsTextBox.getText();
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
	protected RepositoryTaskEditorCopyAction getCopyAction() {
		return copyAction;
	}

	@Override
	public void createPartControl(Composite parent) {

		if (getRepositoryTaskData() == null) {
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout());
			Label noBugLabel = new Label(composite, SWT.NULL);
			noBugLabel.setText("Could not resolve bug");
			return;
		}

		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		// String truncatedSummary = getBug().getSummary();
		// int maxLength = 50;
		// if (truncatedSummary.length() > maxLength) {
		// truncatedSummary = truncatedSummary.substring(0, maxLength) + "...";
		// }
		// form.setFont(COMMENT_FONT);

		editorComposite = form.getBody();
		editorComposite.setLayout(new GridLayout());
		editorComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Header information

		Composite summaryComposite = toolkit.createComposite(editorComposite);
		summaryComposite.setLayout(new GridLayout(2, false));
		GridDataFactory.fillDefaults().grab(true, false).applyTo(summaryComposite);
		addSummaryText(summaryComposite);
		toolkit.paintBordersFor(summaryComposite);
		Composite headerInfoComposite = toolkit.createComposite(editorComposite);
		headerInfoComposite.setLayout(new GridLayout(6, false));
		toolkit.createLabel(headerInfoComposite, "Task# ").setFont(TITLE_FONT);
		toolkit.createText(headerInfoComposite, "" + getRepositoryTaskData().getId(), SWT.FLAT | SWT.READ_ONLY);

		toolkit.createLabel(headerInfoComposite, " Opened: ").setFont(TITLE_FONT);
		String openedDateString = "";
		if (getRepositoryTaskData().getCreated() != null) {
			openedDateString = simpleDateFormat.format(getRepositoryTaskData().getCreated());
		}
		toolkit.createText(headerInfoComposite, openedDateString, SWT.FLAT | SWT.READ_ONLY);

		toolkit.createLabel(headerInfoComposite, " Modified: ").setFont(TITLE_FONT);
		String lastModifiedDateString = "";		
		if (getRepositoryTaskData().getLastModified(repository.getTimeZoneId()) != null) {
			lastModifiedDateString = simpleDateFormat.format(getRepositoryTaskData().getLastModified(repository.getTimeZoneId()));
		}
		toolkit.createText(headerInfoComposite, lastModifiedDateString, SWT.FLAT | SWT.READ_ONLY);

		// openedText.setFont(TITLE_FONT);
		// display = parent.getDisplay();
		// background = JFaceColors.getBannerBackground(display);
		// foreground = JFaceColors.getBannerForeground(display);

		// createInfoArea(editorComposite);
		createContextMenu();
		Composite attribComp = createAttributeLayout();
		createCustomAttributeLayout(attribComp);
		createDescriptionLayout(form.getBody());
		createAttachmentLayout();
		createCommentLayout(toolkit, form);
		createButtonLayouts(toolkit, form.getBody());

		// WorkbenchHelpSystem.getInstance().setHelp(parent,

		editorComposite.setMenu(contextMenuManager.createContextMenu(editorComposite));
		form.reflow(true);
		getSite().getPage().addSelectionListener(selectionListener);
		getSite().setSelectionProvider(selectionProvider);
	}

	public abstract void createCustomAttributeLayout();

	/**
	 * Create a context menu for this editor.
	 */
	protected void createContextMenu() {
		contextMenuManager = new MenuManager(CONTEXT_MENU_ID);
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
		getSite().registerContextMenu(CONTEXT_MENU_ID, contextMenuManager, getSite().getSelectionProvider());
	}

	/**
	 * Creates the attribute layout, which contains most of the basic attributes
	 * of the bug (some of which are editable).
	 */
	protected Composite createAttributeLayout() {

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
		editorInput.setToolTipText(title);

		int currentCol = 1;

		for (RepositoryTaskAttribute attribute : getRepositoryTaskData().getAttributes()) {

			// String key = attribute.getID();
			String name = attribute.getName();
			String value = "";
			// try {
			value = checkText(attribute.getValue());
			// value =
			// checkText(BugzillaRepositoryUtil.decodeStringFromCharset(attribute.getValue(),
			// getReport().getCharset()));
			// } catch (UnsupportedEncodingException e1) {
			// // ignore
			// }
			// "+name+"
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
					// text.setFont(COMMENT_FONT);
					text.setLayoutData(textData);
					toolkit.paintBordersFor(textFieldComposite);
					text.setData(attribute);
					text.addListener(SWT.KeyUp, new Listener() {
						public void handleEvent(Event event) {
							String sel = text.getText();
							RepositoryTaskAttribute a = (RepositoryTaskAttribute) text.getData();
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
		toolkit.paintBordersFor(attributesComposite);
		// make sure that we are in the first column
		if (currentCol > 1) {
			while (currentCol <= attributesLayout.numColumns) {
				toolkit.createLabel(attributesComposite, "");
				// newLayout(attributesComposite, 1, "", PROPERTY);
				currentCol++;
			}
		}
		return attributesComposite;
	}

	/**
	 * Adds a text field to display and edit the bug's summary.
	 * 
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addSummaryText(Composite attributesComposite) {
		// newLayout(attributesComposite, 1, "Summary:", PROPERTY);
		toolkit.createLabel(attributesComposite, "Summary:").setFont(TITLE_FONT);
		summaryText = toolkit.createText(attributesComposite, getRepositoryTaskData().getSummary(), SWT.FLAT);
		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Font summaryFont = themeManager.getCurrentTheme().getFontRegistry().get(REPOSITORY_TEXT_ID);
		summaryText.setFont(summaryFont);
		GridData summaryTextData = new GridData(GridData.FILL_HORIZONTAL);// HORIZONTAL_ALIGN_FILL
		summaryTextData.horizontalSpan = 1;
		// summaryTextData.widthHint = 200;

		summaryText.setLayoutData(summaryTextData);
		summaryText.addListener(SWT.KeyUp, new SummaryListener());
		summaryText.addListener(SWT.FocusIn, new GenericListener());
	}

	protected void createAttachmentLayout() {
		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_ATTACHMENTS);
		section.setExpanded(getRepositoryTaskData().getAttachments().size() > 0);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Composite attachmentsComposite = toolkit.createComposite(section);
		attachmentsComposite.setLayout(new GridLayout());
		attachmentsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		section.setClient(attachmentsComposite);

		if (getRepositoryTaskData().getAttachments().size() > 0) {

			attachmentsTable = toolkit.createTable(attachmentsComposite, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
			attachmentsTable.setLinesVisible(true);
			attachmentsTable.setHeaderVisible(true);
			attachmentsTable.setLayout(new GridLayout());
			GridData tableGridData = new GridData(GridData.FILL_BOTH);
			// tableGridData.heightHint = 100;
			tableGridData.widthHint = DESCRIPTION_WIDTH;
			attachmentsTable.setLayoutData(tableGridData);

			for (int i = 0; i < attachmentsColumns.length; i++) {
				TableColumn column = new TableColumn(attachmentsTable, SWT.LEFT, i);
				column.setText(attachmentsColumns[i]);
				column.setWidth(attachmentsColumnWidths[i]);
			}

			TableViewer attachmentsTableViewer = new TableViewer(attachmentsTable);
			attachmentsTableViewer.setUseHashlookup(true);
			attachmentsTableViewer.setColumnProperties(attachmentsColumns);

			attachmentsTableViewer.setSorter(new ViewerSorter() {
				public int compare(Viewer viewer, Object e1, Object e2) {
					RepositoryAttachment attachment1 = (RepositoryAttachment) e1;
					RepositoryAttachment attachment2 = (RepositoryAttachment) e2;
					Date created1 = attachment1.getDateCreated();
					Date created2 = attachment2.getDateCreated();
					if (created1 != null && created2 != null) {
						return attachment1.getDateCreated().compareTo(attachment2.getDateCreated());
					} else {
						return 0;
					}
				}
			});

			attachmentsTableViewer.setContentProvider(new IStructuredContentProvider() {
				public Object[] getElements(Object inputElement) {
					return getRepositoryTaskData().getAttachments().toArray();
				}

				public void dispose() {
					// ignore
				}

				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					// ignore
				}
			});

			attachmentsTableViewer.setLabelProvider(new ITableLabelProvider() {

				public Image getColumnImage(Object element, int columnIndex) {
					// RepositoryAttachment attachment = (RepositoryAttachment)
					// element;
					return null;
				}

				public String getColumnText(Object element, int columnIndex) {
					RepositoryAttachment attachment = (RepositoryAttachment) element;
					switch (columnIndex) {
					case 0:
						return attachment.getDescription();
					case 1:
						return attachment.getContentType();
					case 2:
						return attachment.getCreator();
					case 3:
						return attachment.getDateCreated().toString();
					}
					return "unrecognized column";
				}

				public void addListener(ILabelProviderListener listener) {
					// ignore

				}

				public void dispose() {
					// ignore

				}

				public boolean isLabelProperty(Object element, String property) {
					// ignore
					return false;
				}

				public void removeListener(ILabelProviderListener listener) {
					// ignore

				}

			});

			attachmentsTableViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					String address = repository.getUrl() + "/attachment.cgi?id=";
					if (!event.getSelection().isEmpty()) {
						StructuredSelection selection = (StructuredSelection) event.getSelection();
						RepositoryAttachment attachment = (RepositoryAttachment) selection.getFirstElement();
						address += attachment.getId() + "&amp;action=view";
						;
						TaskUiUtil.openUrl(address);
					}
				}
			});

			attachmentsTableViewer.setInput(getRepositoryTaskData());

		} else {
			toolkit.createLabel(attachmentsComposite, "No attachments");
		}

		/* Add a file chooser to add new attachments */
		Composite addAttachmentComposite = toolkit.createComposite(attachmentsComposite);
		addAttachmentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addAttachmentComposite.setLayout(new GridLayout(2, false));

		Button addAttachmentButton = toolkit.createButton(addAttachmentComposite, "Add an Attachment...", SWT.PUSH);
		final Text fname = new Text(addAttachmentComposite, SWT.LEFT);// toolkit.createText(addAttachmentComposite,
		// "");
		fname.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Composite addAttachmentInfo = toolkit.createComposite(addAttachmentComposite);
		addAttachmentInfo.setSize(2, 1);
		addAttachmentInfo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		addAttachmentInfo.setLayout(new GridLayout(3, false));

		toolkit.createLabel(addAttachmentInfo, "Description: ");
		attachmentDesc = toolkit.createText(addAttachmentInfo, "");
		attachmentDesc.setEnabled(false);
		attachmentDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		toolkit.createLabel(addAttachmentInfo, "Comment: ");
		attachmentComment = toolkit.createText(addAttachmentInfo, "");
		attachmentComment.setEnabled(false);
		attachmentComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		final Button isPatchButton = toolkit.createButton(addAttachmentInfo, "Patch", SWT.CHECK);

		addAttachmentInfo.setVisible(false);

		/* File Chooser listener */
		addAttachmentButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				FileDialog fileChooser = new FileDialog(attachmentsComposite.getShell(), SWT.OPEN);
				String file = fileChooser.open();

				// Check if the dialog was canceled or an error occured
				if (file == null) {
					return;
				}
				// update UI
				fname.setText(file);
			}
		});

		/*
		 * Attachment file name listener, update the local attachment
		 * accordingly
		 */
		fname.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				LocalAttachment att = getRepositoryTaskData().getNewAttachment();
				if (att == null) {
					att = new LocalAttachment();
					att.setReport(getRepositoryTaskData());
				}
				if ("".equals(fname.getText())) {
					attachmentDesc.setEnabled(false);
					attachmentComment.setEnabled(false);
				} else {
					addAttachmentInfo.setVisible(true);
					attachmentDesc.setEnabled(true);
					attachmentComment.setEnabled(true);
				}
				att.setFilePath(fname.getText());
				getRepositoryTaskData().setNewAttachment(att);

				/* TODO jpound - UI for content type */
				// Determine type by extension
				int index = fname.getText().lastIndexOf(".");
				if (index < 0) {
					att.setContentType("text/plain");
				} else {
					String ext = fname.getText().substring(index + 1);
					String type = extensions2Types.get(ext.toLowerCase());
					if (type != null) {
						att.setContentType(type);
					} else {
						att.setContentType("text/plain");
					}
				}
			}
		});

		/* Listener for isPatch */
		isPatchButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				LocalAttachment att = getRepositoryTaskData().getNewAttachment();
				att.setPatch(isPatchButton.getSelection());
			}
		});

	}

	protected abstract void createCustomAttributeLayout(Composite composite);

	protected void createDescriptionLayout(Composite composite) {
		final Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_DESCRIPTION);
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

		final Composite sectionComposite = toolkit.createComposite(section);
		section.setClient(sectionComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		sectionComposite.setLayout(addCommentsLayout);
		GridData sectionCompositeData = new GridData(GridData.FILL_HORIZONTAL);
		sectionComposite.setLayoutData(sectionCompositeData);

		TextViewer viewer = addRepositoryText(repository, sectionComposite, getRepositoryTaskData().getDescription());
		final StyledText styledText = viewer.getTextWidget();
		styledText.addListener(SWT.FocusIn, new DescriptionListener());
		styledText.setLayout(new GridLayout());
		GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(styledText);

		texts.add(textsindex, styledText);
		textHash.put(getRepositoryTaskData().getDescription(), styledText);
		textsindex++;
	}

	/**
	 * A listener for selection of the description field.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider,
					new StructuredSelection(new RepositoryTaskSelection(getRepositoryTaskData().getId(),
							getRepositoryTaskData().getRepositoryUrl(), LABEL_SECTION_DESCRIPTION, true,
							getRepositoryTaskData().getSummary()))));
		}
	}

	protected void createCommentLayout(FormToolkit toolkit, final ScrolledForm form) {

		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_COMMENTS);
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

		ImageHyperlink hyperlink = toolkit.createImageHyperlink(section, SWT.NONE);
		hyperlink.setBackgroundMode(SWT.INHERIT_NONE);
		hyperlink.setBackground(section.getTitleBarBackground());
		hyperlink.setImage(TaskListImages.getImage(TaskListImages.EXPAND_ALL));
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				revealAllComments();
			}
		});

		section.setTextClient(hyperlink);

		// Additional (read-only) Comments Area
		Composite addCommentsComposite = toolkit.createComposite(section);
		section.setClient(addCommentsComposite);
		GridLayout addCommentsLayout = new GridLayout();
		addCommentsLayout.numColumns = 1;
		addCommentsComposite.setLayout(addCommentsLayout);
		// addCommentsComposite.setBackground(background);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(addCommentsComposite);
		// End Additional (read-only) Comments Area
		// Date lastSynced = new Date();
		// if(this.getEditorInput() instanceof ExistingBugEditorInput) {
		// ExistingBugEditorInput input =
		// (ExistingBugEditorInput)this.getEditorInput();
		// lastSynced = input.getRepositoryTask().getLastSynchronized();
		//		}
		
		
		StyledText styledText = null;
		for (Iterator<Comment> it = getRepositoryTaskData().getComments().iterator(); it.hasNext();) {
			final Comment comment = it.next();

			// skip comment 0 as it is the description
			if (comment.getNumber() == 0)
				continue;

			ExpandableComposite expandableComposite = toolkit.createExpandableComposite(addCommentsComposite,
					ExpandableComposite.TREE_NODE);

			//if (comment.getCreated().after(lastSynced)) {
			if (!it.hasNext()) {
				expandableComposite.setExpanded(true);
			}

			expandableComposite.setText(comment.getNumber() + ": " + comment.getAuthorName() + ", "
					+ simpleDateFormat.format(comment.getCreated()));

			expandableComposite.addExpansionListener(new ExpansionAdapter() {
				public void expansionStateChanged(ExpansionEvent e) {
					form.reflow(true);
				}
			});

			expandableComposite.setLayout(new GridLayout());
			expandableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			Composite ecComposite = toolkit.createComposite(expandableComposite);
			GridLayout ecLayout = new GridLayout();
			ecLayout.marginHeight = 0;
			ecLayout.marginBottom = 10;
			ecLayout.marginLeft = 10;
			ecComposite.setLayout(ecLayout);
			ecComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			expandableComposite.setClient(ecComposite);
			// toolkit.paintBordersFor(expandableComposite);

			// TODO: Attachments are no longer 'attached' to Comments

			// if (comment.hasAttachment()) {
			//
			// Link attachmentLink = new Link(ecComposite, SWT.NONE);
			//
			// String attachmentHeader;
			//
			// if (!comment.isObsolete()) {
			// attachmentHeader = " Attached: " +
			// comment.getAttachmentDescription() + " [<a>view</a>]";
			// } else {
			// attachmentHeader = " Deprecated: " +
			// comment.getAttachmentDescription();
			// }
			// // String result = MessageFormat.format(attachmentHeader, new
			// // String[] { node
			// // .getLabelText() });
			//
			// attachmentLink.addSelectionListener(new SelectionAdapter() {
			// /*
			// * (non-Javadoc)
			// *
			// * @see
			// org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
			// */
			// public void widgetSelected(SelectionEvent e) {
			// String address = repository.getUrl() + "/attachment.cgi?id=" +
			// comment.getAttachmentId()
			// + "&amp;action=view";
			// TaskUiUtil.openUrl(address, address, address);
			//
			// }
			// });
			//
			// attachmentLink.setText(attachmentHeader);
			//
			// }

			TextViewer viewer = addRepositoryText(repository, ecComposite, comment.getText());
			styledText = viewer.getTextWidget();
			GridDataFactory.fillDefaults().hint(DESCRIPTION_WIDTH, SWT.DEFAULT).applyTo(styledText);

			// code for outline
			commentStyleText.add(styledText);
			texts.add(textsindex, styledText);
			textHash.put(comment, styledText);
			textsindex++;
		}

		Section sectionAdditionalComments = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR
				| Section.TWISTIE);
		sectionAdditionalComments.setText(LABEL_SECTION_NEW_COMMENT);
		sectionAdditionalComments.setExpanded(true);

		sectionAdditionalComments.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		sectionAdditionalComments.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});

		Composite newCommentsComposite = toolkit.createComposite(sectionAdditionalComments);
		newCommentsComposite.setLayout(new GridLayout());
		newCommentsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addCommentsText = toolkit.createText(newCommentsComposite, getRepositoryTaskData().getNewComment(), SWT.MULTI
				| SWT.V_SCROLL | SWT.WRAP);

		IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
		Font newCommnetFont = themeManager.getCurrentTheme().getFontRegistry().get(REPOSITORY_TEXT_ID);
		addCommentsText.setFont(newCommnetFont);
		toolkit.paintBordersFor(newCommentsComposite);
		GridData addCommentsTextData = new GridData(GridData.FILL_HORIZONTAL);
		addCommentsTextData.widthHint = DESCRIPTION_WIDTH;
		addCommentsTextData.heightHint = DESCRIPTION_HEIGHT;
		addCommentsTextData.grabExcessHorizontalSpace = true;

		addCommentsText.setLayoutData(addCommentsTextData);

		addCommentsText.addListener(SWT.KeyUp, new Listener() {

			public void handleEvent(Event event) {
				String sel = addCommentsText.getText();
				if (!(getRepositoryTaskData().getNewComment().equals(sel))) {
					getRepositoryTaskData().setNewComment(sel);
					changeDirtyStatus(true);
				}
				validateInput();
			}
		});
		addCommentsText.addListener(SWT.FocusIn, new NewCommentListener());
		addCommentsTextBox = addCommentsText;

		sectionAdditionalComments.setClient(newCommentsComposite);

		// TODO: move into ExistingBugEditor commands section
		// // if they aren't already on the cc list create an add self check box
		//
		// RepositoryTaskAttribute owner =
		// getReport().getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);
		//
		// // Don't add addselfcc check box if the user is the bug owner
		// if (owner != null &&
		// owner.getValue().indexOf(repository.getUserName()) != -1) {
		// return;
		// }
		// // Don't add addselfcc if already there
		// RepositoryTaskAttribute ccAttribute =
		// getReport().getAttribute(RepositoryTaskAttribute.USER_CC);
		// if (ccAttribute != null &&
		// ccAttribute.getValues().contains(repository.getUserName())) {
		// return;
		// }
		// RepositoryTaskAttribute addselfcc =
		// getReport().getAttribute(BugzillaReportElement.ADDSELFCC.getKeyString());
		// if (addselfcc == null) {
		// // addselfcc =
		// //
		// BugzillaRepositoryUtil.makeNewAttribute(BugzillaReportElement.ADDSELFCC);
		// getReport().setAttributeValue(BugzillaReportElement.ADDSELFCC.getKeyString(),
		// "0");
		// } else {
		// addselfcc.setValue("0");
		// }
		//
		// final Button addSelfButton =
		// toolkit.createButton(newCommentsComposite, "Add " +
		// repository.getUserName()
		// + " to CC list", SWT.CHECK);
		//
		// addSelfButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// if (addSelfButton.getSelection()) {
		// getReport().setAttributeValue(BugzillaReportElement.ADDSELFCC.getKeyString(),
		// "1");
		// // connector.getAttributeFactory().setAttributeValue(getReport(),
		// // BugzillaReportElement.ADDSELFCC.getKeyString(), "1");
		// } else {
		// getReport().setAttributeValue(BugzillaReportElement.ADDSELFCC.getKeyString(),
		// "0");
		// }
		// }
		// });
	}

	protected abstract void validateInput();

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
		// submitButtonData.widthHint =
		// AbstractRepositoryTaskEditor.WRAP_LENGTH;
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
		RepositoryTextViewer commentViewer = new RepositoryTextViewer(repository, composite, SWT.MULTI | SWT.WRAP);

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

	protected abstract void submitBug();

	/**
	 * If there is no locally saved copy of the current bug, then it saved
	 * offline. Otherwise, any changes are updated in the file.
	 */
	public void saveBug() {
		try {
			updateBug();

			final AbstractRepositoryConnector repositoryClient = (AbstractRepositoryConnector) MylarTaskListPlugin
					.getRepositoryManager().getRepositoryConnector(getRepositoryTaskData().getRepositoryKind());

			IEditorInput input = this.getEditorInput();
			if (input instanceof ExistingBugEditorInput) {
				ExistingBugEditorInput existingInput = (ExistingBugEditorInput) input;
				AbstractRepositoryTask repositoryTask = existingInput.getRepositoryTask();
				// AbstractRepositoryTask repositoryTask = getRepositoryTask();
				if (repositoryTask != null) {
					if (getRepositoryTaskData().hasChanges()) {
						repositoryTask.setSyncState(RepositoryTaskSyncState.OUTGOING);
					} else {
						repositoryTask.setSyncState(RepositoryTaskSyncState.SYNCHRONIZED);
					}
					MylarTaskListPlugin.getTaskListManager().getTaskList().notifyRepositoryInfoChanged(repositoryTask);
				}
			}

			repositoryClient.saveOffline(getRepositoryTaskData());
			changeDirtyStatus(false);
			if (parentEditor != null) {
				parentEditor.notifyTaskChanged();
			}
		} catch (Exception e) {
			MylarStatusHandler.fail(e, "bug save offline failed", true);
		}

	}

	protected abstract void updateBug();

	/**
	 * Refreshes any text labels in the editor that contain information that
	 * might change.
	 */
	protected void updateEditor() {
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
		setPartName(editorInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		saveBug();
		updateEditor();
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
	// for (IRepositoryTaskAttributeListener client : attributesListeners) {
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
	public class GenericListener implements Listener {
		public void handleEvent(Event event) {
			RepositoryTaskData bug = (RepositoryTaskData) getRepositoryTaskData();
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(bug.getId(), bug.getRepositoryUrl(), bug.getLabel(), false, bug
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
	public void handleSummaryEvent() {
		String sel = summaryText.getText();
		RepositoryTaskAttribute a = getRepositoryTaskData().getAttribute(RepositoryTaskAttribute.SUMMARY);
		if (!(a.getValue().equals(sel))) {
			a.setValue(sel);
			changeDirtyStatus(true);
		}
	}

	/*----------------------------------------------------------*
	 * CODE TO SCROLL TO A COMMENT OR OTHER PIECE OF TEXT
	 *----------------------------------------------------------*/

	/** List of the StyledText's so that we can get the previous and the next */
	protected ArrayList<StyledText> texts = new ArrayList<StyledText>();

	protected HashMap<Object, StyledText> textHash = new HashMap<Object, StyledText>();

	protected List<StyledText> commentStyleText = new ArrayList<StyledText>();

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
		for (StyledText text : commentStyleText) {
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
		} else if (o instanceof RepositoryTaskData) {
			focusOn(null, highlight);
		}
	}

	public void selectDescription() {
		for (Object o : textHash.keySet()) {
			if (o.equals(editorInput.getRepositoryTaskData().getDescription())) {
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

	private RepositoryTaskOutlinePage outlinePage = null;

	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (outlinePage == null && editorInput != null) {
				outlinePage = new RepositoryTaskOutlinePage(taskOutlineModel);
			}
			return outlinePage;
		}
		return super.getAdapter(adapter);
	}

	public RepositoryTaskOutlineNode getOutlineModel() {
		return taskOutlineModel;
	}

	public RepositoryTaskOutlinePage getOutline() {
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
				if (getSite() != null && getSite().getPage() != null && !AbstractRepositoryTaskEditor.this.isDisposed())
					if (parentEditor != null) {
						getSite().getPage().closeEditor(parentEditor, false);
					} else {
						getSite().getPage().closeEditor(AbstractRepositoryTaskEditor.this, false);
					}
			}
		});
	}

	public void addAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.add(listener);
	}

	public void removeAttributeListener(IRepositoryTaskAttributeListener listener) {
		attributesListeners.remove(listener);
	}

	public void setParentEditor(MylarTaskEditor parentEditor) {
		this.parentEditor = parentEditor;
	}

	public RepositoryTaskOutlineNode getTaskOutlineModel() {
		return taskOutlineModel;
	}

	public void setTaskOutlineModel(RepositoryTaskOutlineNode taskOutlineModel) {
		this.taskOutlineModel = taskOutlineModel;
	}

	/**
	 * A listener for selection of the textbox where a new comment is entered
	 * in.
	 */
	protected class NewCommentListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(getRepositoryTaskData().getId(), getRepositoryTaskData()
							.getRepositoryUrl(), "New Comment", false, getRepositoryTaskData().getSummary()))));
		}
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
