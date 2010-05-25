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

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonThemes;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientCanvas;
import org.eclipse.mylyn.internal.tasks.core.notifications.IServiceMessageListener;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessageEvent;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.osgi.framework.Version;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskListServiceMessageControl implements IServiceMessageListener {

	private ImageHyperlink imageHyperlink;

	private Hyperlink titleHyperlink;

	private Label descriptionLabel;

	private GridData headData;

	private final Composite parent;

	private GradientCanvas head;

	private ImageHyperlink closeLink;

	private ServiceMessage currentMessage;

	private String messageUrl;

	public TaskListServiceMessageControl(Composite parent) {
		this.parent = parent;
	}

	private void setTitleImage(Image image) {
		imageHyperlink.setImage(image);
	}

	private void setTitle(String title) {
		titleHyperlink.setText(title);
	}

	private void setDescription(String description) {
		descriptionLabel.setText(description);
	}

	private void addHyperlinkListener(IHyperlinkListener listener) {
		titleHyperlink.addHyperlinkListener(listener);
		imageHyperlink.addHyperlinkListener(listener);
	}

	protected void setMessageUrl(String url) {
		messageUrl = url;
	}

	public Control createControl(Composite parent) {
		FormColors colors = TasksUiPlugin.getDefault().getFormColors(parent.getDisplay());
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

		imageHyperlink = new ImageHyperlink(head, SWT.NONE);

		titleHyperlink = new Hyperlink(head, SWT.NONE);

		setHeaderFontSizeAndStyle(titleHyperlink);

		addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (messageUrl != null) {
					TasksUiUtil.openUrl(messageUrl);
				}

			}
		});

		closeLink = new ImageHyperlink(head, SWT.NONE);
		closeLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CLOSE));
		TableWrapData data = new TableWrapData();
		data.align = TableWrapData.RIGHT;
		closeLink.setLayoutData(data);
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

		// spacer
		new Label(head, SWT.NONE).setText(" "); //$NON-NLS-1$

		descriptionLabel = new Label(head, SWT.WRAP);
		data = new TableWrapData();
		data.colspan = 2;
		descriptionLabel.setLayoutData(data);
		return head;
	}

	private void closeMessage() {
		if (currentMessage != null) {
			TasksUiPlugin.getDefault().getPreferenceStore().setValue(
					ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID, currentMessage.getId());
		}
		if (head != null && !head.isDisposed()) {
			head.dispose();
		}
		if (!parent.isDisposed()) {
			parent.layout(true);
		}
	}

	// From EditorUtil
	private static Font setHeaderFontSizeAndStyle(Control text) {
		float sizeFactor = 1.2f;
		Font initialFont = text.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (FontData element : fontData) {
			element.setHeight((int) (element.getHeight() * sizeFactor));
			element.setStyle(element.getStyle() | SWT.BOLD);
		}
		final Font textFont = new Font(text.getDisplay(), fontData);
		text.setFont(textFont);
		text.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				textFont.dispose();
			}
		});
		Color color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(
				CommonThemes.COLOR_COMPLETED);
		text.setForeground(color);
		return textFont;
	}

	public void handleEvent(final ServiceMessageEvent event) {
		switch (event.getEventKind()) {
		case MESSAGE_UPDATE:
			IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
			String lastMessageId = preferenceStore.getString(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ID);
			for (final ServiceMessage message : event.getMessages()) {
				if (!message.isValid() || message.getId().equals("-1")) { //$NON-NLS-1$
					continue;
				}

				if (!lastMessageId.equals(message.getId()) && isForCurrentVersion(message)) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							setMessage(message);
						}
					});
				}
			}
			break;
		case STOP:
			if (head != null && !head.isDisposed()) {
				head.dispose();
			}
			if (parent != null && !parent.isDisposed()) {
				parent.layout(true);
			}
			break;
		}
	}

	private boolean isForCurrentVersion(ServiceMessage message) {
		if (message.getVersion() == null) {
			return true;
		}

		try {
			VersionRange version = new VersionRange(message.getVersion());
			String versionString = (String) TasksUiPlugin.getDefault().getBundle().getHeaders().get("Bundle-Version"); //$NON-NLS-1$
			return version.isIncluded(new Version(versionString));
		} catch (IllegalArgumentException e) {
			// invalid version range
			return false;
		}
	}

	protected void setMessage(ServiceMessage message) {
		if (!parent.isDisposed() && message != null && (head == null || head.isDisposed())) {
			createControl(parent);
			IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
			preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ETAG, message.getETag());
			preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_LAST_MODIFIED,
					message.getLastModified());

			this.currentMessage = message;

			setTitle(message.getTitle());
			setDescription(message.getDescription());
			setTitleImage(Dialog.getImage(message.getImage()));
			if (message.getUrl() != null) {
				setMessageUrl(message.getUrl());
			}
			parent.layout(true);
		}
	}

}
