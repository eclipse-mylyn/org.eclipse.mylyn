/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Refactored into commons
 *     Itema AS - Added configure button, bug #329897
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.forms;

import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.GradientCanvas;
import org.eclipse.mylyn.internal.commons.workbench.CommonsWorkbenchPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Control designed to display notification messages. These messages may contain links that can be clicked by the user.
 * 
 * @since 3.7
 * @author Robert Elves
 * @author Steffen Pingel
 * @author Torkild Ulvøy Resheim
 */
public abstract class MessageControl {

	protected static Font setHeaderFontSizeAndStyle(Control text) {
		float sizeFactor = 1.2f;
		Font initialFont = text.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData element : fontData) {
			element.setHeight((int) (element.getHeight() * sizeFactor));
			element.setStyle(element.getStyle() | SWT.BOLD);
		}
		final Font textFont = new Font(text.getDisplay(), fontData);
		text.setFont(textFont);
		text.addDisposeListener(e -> textFont.dispose());
		Color color = CommonsWorkbenchPlugin.getDefault().getFormColors(text.getDisplay()).getColor(IFormColors.TITLE);
		text.setForeground(color);
		return textFont;
	}

	private ImageHyperlink closeLink;

	private Link descriptionLabel;

	private GradientCanvas head;

	private GridData headData;

	private Label imageLabel;

	private final Composite parent;

	private Label titleLabel;

	private String eventId;

	public MessageControl(Composite parent) {
		this.parent = parent;
	}

	/**
	 * Disposes of the widget and performs a layout update.
	 */
	protected void close() {
		if (head != null && !head.isDisposed()) {
			head.dispose();
		}
		if (!parent.isDisposed()) {
			parent.layout(true);
		}
	}

	/**
	 * Returns <code>true</code> if the control has closed and is currently not showing any messages.
	 * 
	 * @return <code>true</code> if the control is closed
	 */
	protected boolean isClosed() {
		return head == null || head.isDisposed();
	}

	/**
	 * Implement to handle the message control closing.
	 */
	protected abstract void closeMessage();

	/**
	 * Returns the shell of the parent composite.
	 * 
	 * @return the shell
	 */
	protected Shell getShell() {
		return parent.getShell();
	}

	/**
	 * Creates the GUI of the service message control.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the control
	 */
	public Control createControl(Composite parent) {
		FormColors colors = CommonsWorkbenchPlugin.getDefault().getFormColors(parent.getDisplay());
		head = new GradientCanvas(parent, SWT.NONE);
		GridLayout headLayout = new GridLayout();
		headLayout.marginHeight = 0;
		headLayout.marginWidth = 0;
		headLayout.horizontalSpacing = 0;
		headLayout.verticalSpacing = 0;
		headLayout.numColumns = 1;
		head.setLayout(headLayout);
		headData = new GridData(SWT.FILL, SWT.TOP, true, false);
		head.setLayoutData(headData);

		Color top = colors.getColor(IFormColors.H_GRADIENT_END);
		Color bot = colors.getColor(IFormColors.H_GRADIENT_START);
		head.setBackgroundGradient(new Color[] { bot, top }, new int[] { 100 }, true);
		head.setSeparatorVisible(true);
		head.setSeparatorAlignment(SWT.TOP);

		head.putColor(IFormColors.H_BOTTOM_KEYLINE1, colors.getColor(IFormColors.H_BOTTOM_KEYLINE1));
		head.putColor(IFormColors.H_BOTTOM_KEYLINE2, colors.getColor(IFormColors.H_BOTTOM_KEYLINE2));
		head.putColor(IFormColors.H_HOVER_LIGHT, colors.getColor(IFormColors.H_HOVER_LIGHT));
		head.putColor(IFormColors.H_HOVER_FULL, colors.getColor(IFormColors.H_HOVER_FULL));
		head.putColor(IFormColors.TB_TOGGLE, colors.getColor(IFormColors.TB_TOGGLE));
		head.putColor(IFormColors.TB_TOGGLE_HOVER, colors.getColor(IFormColors.TB_TOGGLE_HOVER));

		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 3;
		head.setLayout(layout);

		imageLabel = new Label(head, SWT.NONE);

		titleLabel = new Label(head, SWT.NONE);

		setHeaderFontSizeAndStyle(titleLabel);

		Composite buttonsComp = new Composite(head, SWT.NONE);
		TableWrapData data = new TableWrapData();
		data.align = TableWrapData.RIGHT;
		buttonsComp.setLayoutData(data);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.horizontalSpacing = 0;
		gLayout.verticalSpacing = 0;
		gLayout.marginHeight = 0;
		gLayout.marginWidth = 0;
		gLayout.verticalSpacing = 0;

		buttonsComp.setLayout(gLayout);

		createLinkControls(buttonsComp);

		// spacer
		new Label(head, SWT.NONE).setText(" "); //$NON-NLS-1$

		descriptionLabel = new Link(head, SWT.WRAP);
		descriptionLabel.addSelectionListener(getLinkListener());

		data = new TableWrapData();
		data.colspan = 2;
		data.grabHorizontal = true;
		descriptionLabel.setLayoutData(data);

		return head;
	}

	protected void createLinkControls(Composite buttonsComp) {
		closeLink = new ImageHyperlink(buttonsComp, SWT.NONE);
		closeLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CLOSE));
		//		data = new TableWrapData();
		//		data.align = TableWrapData.RIGHT;
		//		closeLink.setLayoutData(data);
		closeLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				closeMessage();
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				closeLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CLOSE_HOVER));
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				closeLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CLOSE));
			}
		});
	}

	/**
	 * Must be implemented for custom handling of description links.
	 * 
	 * @see #setDescription(String)
	 * @return the selection listener
	 */
	protected abstract SelectionListener getLinkListener();

	protected void setDescription(String description) {
		descriptionLabel.setText(description);
		parent.layout(true, true);
	}

	/**
	 * Sets the title of the control.
	 * 
	 * @param title
	 *            the title to set
	 */
	protected void setTitle(String title) {
		titleLabel.setText(title);
		head.layout(true);
	}

	protected String getEventId() {
		return eventId;
	}

	/**
	 * Sets the eventId of the message being displayed. How to handle certain kind of messages can be configured in preference settings if
	 * this property is set to a legal value. The event identifiers are declared using the
	 * <i>org.eclipse.mylyn.commons.notifications.notifications</i> extension point.
	 * 
	 * @param eventId
	 *            the event identifier for the displayed message
	 */
	protected void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * Sets the title image of the control.
	 * 
	 * @param image
	 *            the title image
	 */
	protected void setTitleImage(Image image) {
		imageLabel.setImage(image);
		head.layout(true);
	}

	/**
	 * Creates the control unless the parent has been disposed.
	 */
	protected boolean ensureControl() {
		if (parent.isDisposed()) {
			return false;
		}

		if (head == null || head.isDisposed()) {
			createControl(parent);
		}
		return true;
	}

}
