/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Feb 13, 2005
 *
 */
package org.eclipse.mylar.ui.preferences;

import java.util.Arrays;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColorCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.ui.MylarUiPlugin;
import org.eclipse.mylar.ui.internal.views.Highlighter;
import org.eclipse.mylar.ui.internal.views.HighlighterImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


/**
 * Preference page for mylar. Allows for adding / removing highlighters
 * and gamma settings.
 * 
 * @author Ken Sueda
 */
public class MylarPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, SelectionListener, ICellEditorListener {

	// Table
	private Table table;

	// Table viewer
	private TableViewer tableViewer;

	// Color dialog for 2nd cell
	private ColorCellEditor colorDialogEditor;

	// stores the current selection by user
	private Highlighter selection = null;
	
	// content provider for the TableViewer
	private HighlighterContentProvider contentProvider = null;
	
	// Buttons for gamma setting
//	private Button lightened;
//	private Button darkened;
//	private Button standard;

	
	// Button for browsing file system
	private Button browse;
	private Text taskDirectoryText;
		
	// Set the table column property names
	private static final String LABEL_COLUMN = "Label";
	private static final String COLOR_COLUMN = "Color";
	private static final String TYPE_COLUMN = "Type";
	private static String[] columnNames = new String[] { LABEL_COLUMN,
			COLOR_COLUMN, TYPE_COLUMN, };
	static final String[] TYPE_ARRAY = { "Gradient", "Solid", "Intersection" };

//    private IntegerFieldEditor userStudyId;
    
	/**
	 * Constructor - set preference store to MylarUiPlugin store since
	 * the tasklist plugin needs access to the values stored from the preference
	 * page because it needs access to the highlighters on start up.
	 *
	 */
	public MylarPreferencePage() {		
		super();
		setPreferenceStore(MylarUiPlugin.getPrefs());		
	}
	
	@Override
	protected Control createContents(Composite parent) {
        Composite entryTable = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 4;
		entryTable.setLayout (layout);
		
		createTaskDirectoryControl(entryTable);		         
		createTable(entryTable);
		createTableViewer();
		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new HighlighterLabelProvider());
		tableViewer.setInput(MylarUiPlugin.getDefault().getHighlighterList());
//		createGammaSettingControl(entryTable);				        		
		return entryTable;
	}

	/**
	 * init workbench
	 */
	public void init(IWorkbench workbench) {	
		// don't have anything to initialize
	}

	/***************************************************************************
	 * SelectionListener Methods
	 **************************************************************************/

	/**
	 * Handle selection of an item in the menu.
	 */
	public void widgetDefaultSelected(SelectionEvent se) {
		widgetSelected(se);
	}

	/**
	 * Handle selection of an item in the menu.
	 */
	public void widgetSelected(SelectionEvent se) {
		// don't care when the widget is selected
	}

	/***************************************************************************
	 * PropertyPage Methods
	 **************************************************************************/

	/**
	 * Handle Ok and Apply
	 * Store all data in the preference store
	 */
	@Override
	public boolean performOk() {        
		getPreferenceStore().setValue(MylarUiPlugin.HIGHLIGHTER_PREFIX, MylarUiPlugin.getDefault().getHighlighterList().externalizeToString());
        		
//		ColorMap.GammaSetting gm = null;
//		if (standard.getSelection()) {
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_LIGHTENED,false);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_STANDARD,true);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_DARKENED,false);
//			gm = ColorMap.GammaSetting.STANDARD;
//		} else if (lightened.getSelection()) {
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_LIGHTENED,true);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_STANDARD,false);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_DARKENED,false);
//			gm = ColorMap.GammaSetting.LIGHTEN;
//		} else if (darkened.getSelection()) {
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_LIGHTENED,false);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_STANDARD,false);
//			getPreferenceStore().setValue(MylarUiPlugin.GAMMA_SETTING_DARKENED,true);
//			gm = ColorMap.GammaSetting.DARKEN;
//		}
//		// update gamma setting
//		MylarUiPlugin.getDefault().getColorMap().setGammaSetting(gm);
		
		String taskDirectory = taskDirectoryText.getText();
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");		
		getPreferenceStore().setValue(MylarPlugin.MYLAR_DIR, taskDirectory);
		return true;
	}

	/**
	 * Handle Cancel
	 * Undo all changes back to what is stored in preference store
	 */
	@Override
	public boolean performCancel() {
		String highlighters = getPreferenceStore().getString(MylarUiPlugin.HIGHLIGHTER_PREFIX);
		MylarUiPlugin.getDefault().getHighlighterList().internalizeFromString(highlighters);                
        		
		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		
//		lightened.setSelection(getPreferenceStore().getBoolean(
//				MylarUiPlugin.GAMMA_SETTING_LIGHTENED));
//		standard.setSelection(getPreferenceStore().getBoolean(
//				MylarUiPlugin.GAMMA_SETTING_STANDARD));
//		darkened.setSelection(getPreferenceStore().getBoolean(
//				MylarUiPlugin.GAMMA_SETTING_DARKENED));
		
		return true;
	}

	/**
	 * Handle RestoreDefaults
	 * Note: changes to default are not stored in the preference store
	 * until OK or Apply is pressed
	 */
	@Override
	public void performDefaults() {
		super.performDefaults();     
		
		MylarUiPlugin.getDefault().getHighlighterList().setToDefaultList();				
		contentProvider = new HighlighterContentProvider();
		tableViewer.setContentProvider(contentProvider);
		
//		standard.setSelection(getPreferenceStore().getDefaultBoolean(
//				MylarUiPlugin.GAMMA_SETTING_STANDARD));
//		lightened.setSelection(getPreferenceStore().getDefaultBoolean(
//				MylarUiPlugin.GAMMA_SETTING_LIGHTENED));
//		darkened.setSelection(getPreferenceStore().getDefaultBoolean(
//				MylarUiPlugin.GAMMA_SETTING_DARKENED));
//		
		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String taskDirectory = rootPath.toString() + "/" +MylarPlugin.MYLAR_DIR_NAME;
		taskDirectoryText.setText(taskDirectory);
		return;
	}

	/***************************************************************************
	 * ICellEditorListener Methods For ColorDialog box
	 **************************************************************************/

	/**
	 * applyEditorValue - method called when Color selected
	 */
	public void applyEditorValue() {
		Object obj = colorDialogEditor.getValue();
		if (obj instanceof RGB) {
			// create new color
			RGB rgb = (RGB) obj;
			Color c = new Color(Display.getCurrent(), rgb.red, rgb.green,
					rgb.blue);
			if (selection != null) {
				// selection is the highlighter that has been selected
				// set the core color to new color.
				// update Highlighter in contentProvider
				selection.setCore(c);
				contentProvider.updateHighlighter(selection);
			}
		} else {
            MylarPlugin.log("Received Unknown change in Editor: " + obj.getClass().toString(), this);
		}
	}

	public void cancelEditor() {
		// don't care about this
	}

	public void editorValueChanged(boolean oldValidState, boolean newValidState) {
		// don't care when the value is changed
	}

	/***************************************************************************
	 * Nested Classes for preference page
	 **************************************************************************/

	/**
	 * Class HighlighterLabelProvider - Label and image provider for tableViewer
	 */
	private class HighlighterLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public HighlighterLabelProvider() {
			// don't have any initialization to do
		}

		/**
		 * getColumnText - returns text for label and combo box cells
		 */
		public String getColumnText(Object obj, int columnIndex) {
			String result = "";
			if (obj instanceof Highlighter) {
				Highlighter h = (Highlighter) obj;
				switch (columnIndex) {
				case 0:
					// return name for label column
					result = h.getName();
					break;
				case 2:
					// return type for type column
					result = h.getType();
					break;
				default:
					break;
				}
			}
			return result;
		}

		/**
		 * getColumnImage - returns image for color column
		 */
		public Image getColumnImage(Object obj, int columnIndex) {
			if (obj instanceof Highlighter) {
				Highlighter h = (Highlighter) obj;
				switch (columnIndex) {
				case 1:
					HighlighterImageDescriptor des;
					if (h.isGradient()) {
						des = new HighlighterImageDescriptor(h.getBase(), h
								.getLandmarkColor());
					} else {
						des = new HighlighterImageDescriptor(h
								.getLandmarkColor(), h.getLandmarkColor());
					}
					return des.getImage();
				default:
					break;
				}
			}
			return null;
		}
	}

	/**
	 * Class HighLighterContentProvider - content provider for table viewer
	 */
	private class HighlighterContentProvider implements
			IStructuredContentProvider {
		
		/**
		 * getElements - returns array of Highlighters for table
		 */
		public Object[] getElements(Object inputElement) {
            return MylarUiPlugin.getDefault().getHighlighterList().getHighlighters().toArray();
		}

		public void dispose() {
			// don't care when we are disposed
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// don't care when the input changes
		}

		/**
		 * addHighlighter - notify the tableViewer to add a highlighter
		 * called when a highlighter is added to the HighlighterList
		 */
		public void addHighlighter(Highlighter hl) {
			tableViewer.add(hl);
		}

		/**
		 * removeHighlighter - notify the tableViewer to remove a highlighter
		 * called when a highlighter is removed from the HighlighterList
		 */
		public void removeHighlighter(Highlighter hl) {
			tableViewer.remove(hl);
		}

		/**
		 * updateHighlighter - notify the tableViewer to update a highlighter
		 * called when a highlighter property has been changed
		 */
		public void updateHighlighter(Highlighter hl) {
			tableViewer.update(hl, null);
		}
	}

	/**
	 * class HighlighterCellModifier - cellModifier for tableViewer
	 * handles all modification to the table
	 */
	private class HighlighterCellModifier implements ICellModifier {

		HighlighterCellModifier() {
			super();
		}

		public boolean canModify(Object element, String property) {
			return true;
		}

		/**
		 * getValue - returns content of the current selection
		 */
		public Object getValue(Object element, String property) {
			// Find the index of the column
			int columnIndex = Arrays.asList(columnNames).indexOf(property);
			Object res = null;
			if (element instanceof Highlighter) {
				Highlighter hl = (Highlighter) element;
				switch (columnIndex) {
				case 0: // LABEL_COLUMN
					// return label name
					res = new String(hl.getName());
					break;
				case 1: // COLOR_COLUMN
					// Store selected Highlighter. If color is changed, then
					// we need to modify this highlighter.
					selection = hl;
					return null;
				case 2: // KIND_COLUMN
					// return index of current value
					if (hl.isGradient()) {
						res = new Integer(0);
					} else if (hl.isIntersection()) {
						res = new Integer(2);
					} else {
						res = new Integer(1);
					}
					break;
				default:
					return null;
				}
			}
			return res;
		}

		/**
		 * modify - modifies Highlighter with new property
		 */
		public void modify(Object element, String property, Object value) {
			// Find the index of the column
			int columnIndex = Arrays.asList(columnNames).indexOf(property);

			TableItem item = (TableItem) element;
			Highlighter hl = (Highlighter) item.getData();
			switch (columnIndex) {
			case 0: // LABEL_COLUMN
				// change value of name
				if (value instanceof String) {
//					TableItem ti = (TableItem) element;
					hl.setName((String) value);
					
					// update contentprovider
					contentProvider.updateHighlighter(hl);
				}
				break;
			case 1: // COLOR_COLUMN
				// never gets called since color dialog is used.
				break;
			case 2: // KIND_COLUMN
				// sets new type
				if (value instanceof Integer) {
					int choice = ((Integer) value).intValue();
					switch (choice) {
					case 0:
						// Gradient
						hl.setGradient(true);
						hl.setIntersection(false);
						break;
					case 1:
						// Solid
						hl.setGradient(false);
						hl.setIntersection(false);
						break;
					case 2:
						// Instersection
						hl.setGradient(false);
						hl.setIntersection(true);
						break;
					default:
						break;
					}
					// update content provider
					contentProvider.updateHighlighter(hl);
				}
			default:
				break;
			}
			return;
		}

	}

	/**
	 * class HighlighterTableSorter - sort columns of table
	 * added to every column as a sorter
	 */
	private class HighlighterTableSorter extends ViewerSorter {

		public final static int LABEL = 1;
		public final static int COLOR = 2;
		public final static int TYPE = 3;
		private int criteria;

		/**
		 * set the criteria
		 */
		public HighlighterTableSorter(int criteria) {
			super();
			this.criteria = criteria;
		}

		/**
		 * compare - invoked when column is selected
		 * calls the actual comparison method for particular criteria
		 */
		@Override
		public int compare(Viewer viewer, Object o1, Object o2) {
			Highlighter h1 = (Highlighter) o1;
			Highlighter h2 = (Highlighter) o2;			
			switch (criteria) {
				case LABEL:
					return compareLabel(h1, h2);
				case COLOR:
					return compareImage(h1, h2);
				case TYPE:
					return compareType(h1, h2);
				default:
					return 0;
			}
		}
		/**
		 * compareLabel - compare by label
		 */
		protected int compareLabel(Highlighter h1, Highlighter h2) {
			return h1.getName().compareTo(h2.getName());
		}		
		/**
		 * compareImage - do nothing
		 */
		protected int compareImage(Highlighter h1, Highlighter h2) {
			return 0;	
		}
		/**
		 * compareType - compare by type
		 */
		protected int compareType(Highlighter h1, Highlighter h2) {
			return h1.getType().compareTo(h2.getType());
		}
		/**
		 * getCriteria
		 */
		public int getCriteria() {
			return criteria;
		}
	}
	
	/***************************************************************************
	 * Helper Functions
	 **************************************************************************/

	private void createTable(Composite parent) {
		Group tableComposite= new Group(parent, SWT.SHADOW_ETCHED_IN);
		tableComposite.setText("Task Context Highlighters");
		tableComposite.setLayout(new GridLayout(2, false));
		tableComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		int style = SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(tableComposite, style);

		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		table.setLayoutData(gridData);
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with Label
		TableColumn column = new TableColumn(table, SWT.LEAD, 0);
		column.setResizable(false);
		column.setText("Label");
		column.setWidth(150);
		column.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.LABEL));
			}
		});

		// 2nd column with highlighter Description
		column = new TableColumn(table, SWT.LEAD, 1);
		column.setResizable(false);
		column.setText("Color");
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.COLOR));
			}
		});

		// 3rd column with Type
		column = new TableColumn(table, SWT.LEAD, 2);
		column.setResizable(false);
		column.setText("Kind");
		column.setWidth(80);
		column.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new HighlighterTableSorter(HighlighterTableSorter.TYPE));
			}
		});
		createAddRemoveButtons(tableComposite);		
	}

	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(columnNames);

		CellEditor[] editors = new CellEditor[columnNames.length];

		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(20);
		((Text) textEditor.getControl()).setOrientation(SWT.LEFT_TO_RIGHT);
		editors[0] = textEditor;

		colorDialogEditor = new ColorCellEditor(table);
		colorDialogEditor.addListener(this);
		editors[1] = colorDialogEditor;

		editors[2] = new ComboBoxCellEditor(table, TYPE_ARRAY, SWT.READ_ONLY);

		tableViewer.setCellEditors(editors);
		tableViewer.setCellModifier(new HighlighterCellModifier());
	}
	
	private Button createButton(Composite parent, String text) {
		Button button = new Button(parent, SWT.TRAIL);
		button.setText(text);
		button.setVisible(true);
		button.addSelectionListener(this);
		return button;
	}

	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.BEGINNING;
		label.setLayoutData(data);
		return label;
	}

	private void createAddRemoveButtons(Composite parent) {

		Composite addRemoveComposite= new Composite(parent, SWT.LEAD);
		addRemoveComposite.setLayout(new GridLayout(2, false));
				
		Button add = new Button(addRemoveComposite, SWT.PUSH | SWT.CENTER);
		add.setText("Add");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		
		add.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Highlighter hl = MylarUiPlugin.getDefault().getHighlighterList().addHighlighter();
				contentProvider.addHighlighter(hl);
			}
		});

		Button delete = new Button(addRemoveComposite, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);
		
		delete.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				Highlighter hl = (Highlighter) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (hl != null) {
					MylarUiPlugin.getDefault().getHighlighterList().removeHighlighter(hl);
					contentProvider.removeHighlighter(hl);
				}
			}
		});
	}	

//	private void createGammaSettingControl(Composite parent) {
//		Group gammaSettingComposite= new Group(parent, SWT.SHADOW_ETCHED_IN);
//		
//		gammaSettingComposite.setLayout(new RowLayout());
//		gammaSettingComposite.setText("Gamma Setting");
//		lightened = new Button(gammaSettingComposite, SWT.RADIO);
//		lightened.setText("Lightened");
//		lightened.setSelection(getPreferenceStore().getBoolean(MylarUiPlugin.GAMMA_SETTING_LIGHTENED));
//		standard = new Button(gammaSettingComposite, SWT.RADIO);
//		standard.setText("Standard");
//		standard.setSelection(getPreferenceStore().getBoolean(MylarUiPlugin.GAMMA_SETTING_STANDARD));
//		darkened = new Button(gammaSettingComposite, SWT.RADIO);
//		darkened.setText("Darkened");
//		darkened.setSelection(getPreferenceStore().getBoolean(MylarUiPlugin.GAMMA_SETTING_DARKENED));		
//		return;
//	}
	
	private void createTaskDirectoryControl(Composite parent) {
		Group taskDirComposite= new Group(parent, SWT.SHADOW_ETCHED_IN);
		taskDirComposite.setText("Task Directory");
		taskDirComposite.setLayout(new GridLayout(2, false));
		taskDirComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		String taskDirectory = getPreferenceStore().getString(MylarPlugin.MYLAR_DIR);
		taskDirectory = taskDirectory.replaceAll("\\\\", "/");
		taskDirectoryText = new Text(taskDirComposite, SWT.BORDER);		
		taskDirectoryText.setText(taskDirectory);
		taskDirectoryText.setEditable(false);
		taskDirectoryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		
		browse = createButton(taskDirComposite, "Browse...");
		if (!MylarPlugin.getContextManager().hasActiveContext()) {
			browse.setEnabled(true);
		} else {
			browse.setEnabled(false);
			createLabel(taskDirComposite, "NOTE: you have an task active, deactivate it before changing directories");
		}
		browse.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Folder Selection");
				dialog.setMessage("Specify the folder for tasks");
				String dir = taskDirectoryText.getText();
				dir = dir.replaceAll("\\\\", "/");
				dialog.setFilterPath(dir);

				dir = dialog.open();
				if(dir == null || dir.equals(""))
					return;
				taskDirectoryText.setText(dir);
			}
		});        

	}	
}
