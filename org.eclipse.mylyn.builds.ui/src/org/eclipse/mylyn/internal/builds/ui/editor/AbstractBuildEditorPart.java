/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.editor;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;
import org.eclipse.emf.databinding.FeaturePath;
import org.eclipse.emf.databinding.IEMFValueProperty;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonUiUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.EditorUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public abstract class AbstractBuildEditorPart extends AbstractFormPart {

	private Control control;

	private BuildEditorPage page;

	private String partId;

	private String partName;

	private Section section;

	public AbstractBuildEditorPart() {
	}

	protected Binding bind(Text text, Class<? extends IBuildElement> clazz, FeaturePath path) {
		IWidgetValueProperty textProp = WidgetProperties.text(SWT.Modify);
		IEMFValueProperty property = EMFProperties.value(path);
		IObservableValue uiObservable = textProp.observe(text);
		IObservableValue modelObservable = property.observe(getInput(clazz));

		UpdateValueStrategy modelToTargetStrategy = null;
		EStructuralFeature feature = path.getFeaturePath()[path.getFeaturePath().length - 1];
		String name = feature.getEType().getName();
		if (name.equals("ELong")) {
			modelToTargetStrategy = new UpdateValueStrategy();
			modelToTargetStrategy.setConverter(new TimestampToStringConverter());
		} else {
			modelToTargetStrategy = new EMFUpdateValueStrategy();
		}

		return getPage().getDataBindingContext().bindValue(uiObservable, modelObservable, null, modelToTargetStrategy);
	}

	protected Binding bind(Text text, Class<? extends IBuildElement> clazz, EStructuralFeature feature) {
		return bind(text, clazz, FeaturePath.fromList(feature));
	}

	protected abstract Control createContent(Composite parent, FormToolkit toolkit);

	public Control createControl(Composite parent, final FormToolkit toolkit) {
		boolean expand = shouldExpandOnCreate();
		section = createSection(parent, toolkit, expand);
		if (expand) {
			Control content = createContent(section, toolkit);
			section.setClient(content);
		} else {
			section.addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanged(ExpansionEvent event) {
					if (section.getClient() == null) {
						Control content = createContent(section, toolkit);
						section.setClient(content);
						getPage().reflow();
					}
				}
			});
		}
		setSection(toolkit, section);
		return control;
	}

	/**
	 * Clients can implement to provide attribute overlay text
	 * 
	 * @param section
	 */
	private void createInfoOverlay(Composite composite, Section section, FormToolkit toolkit) {
		String text = getInfoOverlayText();
		if (text == null) {
			return;
		}

		final Label label = toolkit.createLabel(composite, CommonUiUtil.toLabel(text));
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		label.setBackground(null);
		label.setVisible(!section.isExpanded());

		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanging(ExpansionEvent e) {
				label.setVisible(!e.getState());
			}
		});
	}

	protected Label createLabel(Composite parent, FormToolkit toolkit, String value) {
		Label label = toolkit.createLabel(parent, value);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, boolean expandedState) {
		int style = ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;
		if (expandedState) {
			style |= ExpandableComposite.EXPANDED;
		}
		return createSection(parent, toolkit, style);
	}

	protected Section createSection(Composite parent, FormToolkit toolkit, int style) {
		Section section = toolkit.createSection(parent, style);
		section.setText(getPartName());
		return section;
	}

	protected Text createTextReadOnly(Composite parent, FormToolkit toolkit, String value) {
		Text text = new Text(parent, SWT.FLAT | SWT.READ_ONLY);
		text.setFont(EditorUtil.TEXT_FONT);
		text.setData(FormToolkit.KEY_DRAW_BORDER, Boolean.FALSE);
		text.setText(value);
		return text;
	}

	protected void fillToolBar(ToolBarManager toolBarManager) {
	}

	private IBuild getBuild() {
		return getPlan().getLastBuild();
	}

	public Control getControl() {
		return control;
	}

	/**
	 * Clients can override to show summary information for the part.
	 */
	protected String getInfoOverlayText() {
		return null;
	}

	protected <T extends IBuildElement> T getInput(Class<T> clazz) {
		if (clazz == IBuildPlan.class) {
			return (T) getPlan();
		} else if (clazz == IBuild.class) {
			return (T) getBuild();
		}
		return null;
	}

	public BuildEditorPage getPage() {
		return page;
	}

	public String getPartId() {
		return partId;
	}

	public String getPartName() {
		return partName;
	}

	private IBuildPlan getPlan() {
		return getPage().getEditorInput().getPlan();
	}

	public Section getSection() {
		return section;
	}

	public void initialize(BuildEditorPage page) {
		this.page = page;
	}

	protected void inputChanged(IBuildElement oldInput, IBuildElement newInput) {
	}

	void setControl(Control control) {
		this.control = control;
	}

	void setPartId(String partId) {
		this.partId = partId;
	}

	protected void setPartName(String partName) {
		this.partName = partName;
	}

	protected void setSection(FormToolkit toolkit, Section section) {
		if (section.getTextClient() == null) {
			ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
			fillToolBar(toolBarManager);

			// TODO toolBarManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

			if (toolBarManager.getSize() > 0) {
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

	protected abstract boolean shouldExpandOnCreate();
}
