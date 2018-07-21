/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.mylyn.internal.tasks.ui.notifications.TaskDiffUtil;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

@SuppressWarnings("restriction")
public class BugzillaFlagPart extends AbstractTaskEditorPart {
	private static final int LABEL_WIDTH = 110;

	private static final int COLUMN_WIDTH = 140;

	private static final int COLUMN_GAP = 20;

	private static final int MULTI_COLUMN_WIDTH = COLUMN_WIDTH + 5 + COLUMN_GAP + LABEL_WIDTH + 5 + COLUMN_WIDTH;

	private static final int MULTI_ROW_HEIGHT = 55;

	private boolean hasIncoming;

	private List<AbstractAttributeEditor> flagEditors;

	private String infoOverlayText = null;

	private String infoOverlayTooltipText = null;

	public BugzillaFlagPart() {
		setPartName(Messages.BugzillaFlagPart_Flags_PartName);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		initialize();
		int style = ExpandableComposite.TWISTIE | ExpandableComposite.LEFT_TEXT_CLIENT_ALIGNMENT;
		if (hasIncoming) {
			style |= ExpandableComposite.EXPANDED;
		}
		Section flagSection = createSection(parent, toolkit, style);
		EditorUtil.setTitleBarForeground(flagSection, toolkit.getColors().getColor(IFormColors.TITLE));

		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		flagSection.setLayout(gl);
		flagSection.setLayoutData(gd);

		Composite flagComposite = toolkit.createComposite(flagSection);

		GridLayout flagsLayout = EditorUtil.createSectionClientLayout();
		flagsLayout.numColumns = 4;
		flagsLayout.horizontalSpacing = 7;
		flagsLayout.verticalSpacing = 6;
		flagComposite.setLayout(flagsLayout);

		GridData flagsData = new GridData(GridData.FILL_BOTH);
		flagsData.horizontalSpan = 1;
		flagsData.grabExcessVerticalSpace = false;
		flagComposite.setLayoutData(flagsData);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).applyTo(flagComposite);

		createAttributeControls(flagComposite, toolkit, flagsLayout.numColumns);
		flagSection.setClient(flagComposite);
		toolkit.paintBordersFor(flagComposite);
		setSection(toolkit, flagSection);
	}

	private void initialize() {
		hasIncoming = false;
		flagEditors = new ArrayList<AbstractAttributeEditor>();
		int used = 0;
		int unused = 0;
		String usedDetail = ""; //$NON-NLS-1$
		String unusedDetail = ""; //$NON-NLS-1$
		Map<String, TaskAttribute> attributes = getTaskData().getRoot().getAttributes();
		for (TaskAttribute attribute : attributes.values()) {
			if (!BugzillaAttribute.KIND_FLAG.equals(attribute.getMetaData().getKind())) {
				continue;
			}

			TaskAttribute stateAttribute = attribute.getAttribute("state"); //$NON-NLS-1$
			if (stateAttribute == null) {
				continue;
			}
			String val = stateAttribute.getValue();
			if (val != null && !val.equals("") && !val.equals(" ")) { //$NON-NLS-1$ //$NON-NLS-2$
				if (used > 0) {
					usedDetail += ", "; //$NON-NLS-1$
				}
				used++;
				usedDetail += stateAttribute.getMetaData().getLabel();
			} else {
				if (unused > 0) {
					unusedDetail += ", "; //$NON-NLS-1$
				}
				unused++;
				unusedDetail += stateAttribute.getMetaData().getLabel();
			}
			AbstractAttributeEditor attributeEditor = createAttributeEditor(attribute);
			if (attributeEditor != null) {
				flagEditors.add(attributeEditor);
				if (getModel().hasIncomingChanges(attribute)) {
					hasIncoming = true;
				}
			}
		}
		Comparator<AbstractAttributeEditor> attributeSorter = createAttributeEditorSorter();
		if (attributeSorter != null) {
			Collections.sort(flagEditors, attributeSorter);
		}

		if (used > 0) {
			infoOverlayText = NLS.bind(
					" " + Messages.BugzillaFlagPart_Fleg_Section_Title, "" + (unused + used), "" + used); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
		} else {
			infoOverlayText = NLS.bind(" " + Messages.BugzillaFlagPart_Fleg_Section_Title_Short, "" + (unused + used)); //$NON-NLS-1$ //$NON-NLS-2$ 
		}

		usedDetail = NLS.bind(
				"{0} {1}", used == 1 ? Messages.BugzillaFlagPart_used_flag + Messages.BugzillaFlagPart_is : Messages.BugzillaFlagPart_used_flags + Messages.BugzillaFlagPart_are, usedDetail); //$NON-NLS-1$
		unusedDetail = NLS.bind(
				"{0} {1}", unused == 1 ? Messages.BugzillaFlagPart_unused_flag + Messages.BugzillaFlagPart_is : Messages.BugzillaFlagPart_unused_flags + Messages.BugzillaFlagPart_are, unusedDetail); //$NON-NLS-1$ 
		if (used > 0 && unused > 0) {
			infoOverlayTooltipText = NLS.bind("{0}\n{1}", usedDetail, unusedDetail); //$NON-NLS-1$
		} else {
			infoOverlayTooltipText = used > 0 ? usedDetail : unusedDetail;
		}
	}

	/**
	 * Create a comparator by which attribute editors will be sorted. By default attribute editors are sorted by layout
	 * hint priority. Subclasses may override this method to sort attribute editors in a custom way.
	 * 
	 * @return comparator for {@link AbstractAttributeEditor} objects
	 */
	protected Comparator<AbstractAttributeEditor> createAttributeEditorSorter() {
		return new Comparator<AbstractAttributeEditor>() {
			public int compare(AbstractAttributeEditor o1, AbstractAttributeEditor o2) {
				int p1 = (o1.getLayoutHint() != null) ? o1.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				int p2 = (o2.getLayoutHint() != null) ? o2.getLayoutHint().getPriority() : LayoutHint.DEFAULT_PRIORITY;
				return p1 - p2;
			}
		};
	}

	private void createAttributeControls(Composite attributesComposite, FormToolkit toolkit, int columnCount) {
		int currentColumn = 1;
		int currentPriority = 0;
		for (AbstractAttributeEditor attributeEditor : flagEditors) {
			int priority = (attributeEditor.getLayoutHint() != null)
					? attributeEditor.getLayoutHint().getPriority()
					: LayoutHint.DEFAULT_PRIORITY;
			if (priority != currentPriority) {
				currentPriority = priority;
				if (currentColumn > 1) {
					while (currentColumn <= columnCount) {
						getManagedForm().getToolkit().createLabel(attributesComposite, ""); //$NON-NLS-1$
						currentColumn++;
					}
					currentColumn = 1;
				}
			}

			if (attributeEditor.hasLabel()) {
				attributeEditor.createLabelControl(attributesComposite, toolkit);
				Label label = attributeEditor.getLabelControl();
				String text = label.getText();
				String shortenText = TaskDiffUtil.shortenText(label, text, LABEL_WIDTH);
				label.setText(shortenText);
				if (!text.equals(shortenText)) {
					label.setToolTipText(text);
				}
				GridData gd = GridDataFactory.fillDefaults()
						.align(SWT.RIGHT, SWT.CENTER)
						.hint(LABEL_WIDTH, SWT.DEFAULT)
						.create();
				if (currentColumn > 1) {
					gd.horizontalIndent = COLUMN_GAP;
					gd.widthHint = LABEL_WIDTH + COLUMN_GAP;
				}
				label.setLayoutData(gd);
				currentColumn++;
			}

			attributeEditor.createControl(attributesComposite, toolkit);
			LayoutHint layoutHint = attributeEditor.getLayoutHint();
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, false, false);
			if (layoutHint != null
					&& !(layoutHint.rowSpan == RowSpan.SINGLE && layoutHint.columnSpan == ColumnSpan.SINGLE)) {
				if (layoutHint.rowSpan == RowSpan.MULTIPLE) {
					gd.heightHint = MULTI_ROW_HEIGHT;
				}
				if (layoutHint.columnSpan == ColumnSpan.SINGLE) {
					gd.widthHint = COLUMN_WIDTH;
					gd.horizontalSpan = 1;
				} else {
					gd.widthHint = MULTI_COLUMN_WIDTH;
					gd.horizontalSpan = columnCount - currentColumn + 1;
				}
			} else {
				gd.widthHint = COLUMN_WIDTH;
				gd.horizontalSpan = 1;
			}
			attributeEditor.getControl().setLayoutData(gd);

			getTaskEditorPage().getAttributeEditorToolkit().adapt(attributeEditor);

			currentColumn += gd.horizontalSpan;
			currentColumn %= columnCount;
		}
	}

	protected String getInfoOverlayText() {
		return infoOverlayText;
	}

	protected String getInfoOverlayTooltipText() {
		return infoOverlayTooltipText;
	}

	private void createInfoOverlay(Composite composite, Section section, FormToolkit toolkit) {
		String text = getInfoOverlayText();
		if (text == null) {
			return;
		}

		final Label label = toolkit.createLabel(composite, LegacyActionTools.escapeMnemonics(text));
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setBackground(null);
		label.setVisible(!section.isExpanded());
		label.setToolTipText(getInfoOverlayTooltipText());

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				label.setVisible(!e.getState());
			}
		});
	}

	@Override
	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			if (toolBarManager.getSize() > 0 || getInfoOverlayText() != null) {
				Composite toolbarComposite = toolkit.createComposite(section);
				toolbarComposite.setBackground(null);
				RowLayout rowLayout = new RowLayout();
				rowLayout.marginLeft = 0;
				rowLayout.marginRight = 0;
				rowLayout.marginTop = 0;
				rowLayout.marginBottom = 0;
				rowLayout.center = true;
				toolbarComposite.setLayout(rowLayout);

				createInfoOverlay(toolbarComposite, section, toolkit);

				toolBarManager.createControl(toolbarComposite);
				section.clientVerticalSpacing = 0;
				section.descriptionVerticalSpacing = 0;
				section.setTextClient(toolbarComposite);
			}
		}
		setControl(section);
	}
}
