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

import java.util.Collections;
import java.util.Date;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.GradientCanvas;
import org.eclipse.mylyn.internal.tasks.core.notifications.IServiceMessageListener;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessage;
import org.eclipse.mylyn.internal.tasks.core.notifications.ServiceMessageEvent;
import org.eclipse.mylyn.internal.tasks.ui.ITasksUiPreferenceConstants;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.actions.AddRepositoryAction;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.wizards.Messages;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.handlers.IHandlerService;
import org.osgi.framework.Version;

/**
 * @author Robert Elves
 * @author Steffen Pingel
 */
public class TaskListServiceMessageControl implements IServiceMessageListener {

	private Label imageLabel;

	private Label titleLabel;

	private Link descriptionLabel;

	private GridData headData;

	private final Composite parent;

	private GradientCanvas head;

	private ImageHyperlink closeLink;

	private ImageHyperlink settingsLink;

	private ServiceMessage currentMessage;

	public TaskListServiceMessageControl(Composite parent) {
		this.parent = parent;
	}

	private void setTitleImage(Image image) {
		imageLabel.setImage(image);
	}

	private void setTitle(String title) {
		titleLabel.setText(title);
	}

	private void setDescription(String description) {
		descriptionLabel.setText(description);
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

		settingsLink = new ImageHyperlink(buttonsComp, SWT.NONE);
		settingsLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_PREFERENCES));
//		TableWrapData data = new TableWrapData();
//		data.align = TableWrapData.RIGHT;
//		settingsLink.setLayoutData(data);
		settingsLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
						TaskListServiceMessageControl.this.parent.getShell(),
						"org.eclipse.mylyn.tasks.ui.preferences", null, null); //$NON-NLS-1$
				if (pref != null) {
					pref.open();
				}
			}

			@Override
			public void linkEntered(HyperlinkEvent e) {
				settingsLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_PREFERENCES_HOVER));
			}

			@Override
			public void linkExited(HyperlinkEvent e) {
				settingsLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_PREFERENCES));
			}
		});

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

		// spacer
		new Label(head, SWT.NONE).setText(" "); //$NON-NLS-1$

		descriptionLabel = new Link(head, SWT.WRAP);
		descriptionLabel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.text != null) {
					if (e.text.toLowerCase().equals("connect")) { //$NON-NLS-1$
						closeMessage();
						new AddRepositoryAction().run();
					} else if (e.text.toLowerCase().equals("discovery")) { //$NON-NLS-1$
						closeMessage();
						final Command discoveryWizardCommand = TasksUiInternal.getConfiguredDiscoveryWizardCommand();
						if (discoveryWizardCommand != null && discoveryWizardCommand.isEnabled()) {
							IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
									IHandlerService.class);
							try {
								discoveryWizardCommand.executeWithChecks(TaskListServiceMessageControl.createExecutionEvent(
										discoveryWizardCommand, handlerService));
							} catch (Exception e1) {
								IStatus status = new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, NLS.bind(
										Messages.SelectRepositoryConnectorPage_discoveryProblemMessage,
										new Object[] { e1.getMessage() }), e1);
								TasksUiInternal.logAndDisplayStatus(
										Messages.SelectRepositoryConnectorPage_discoveryProblemTitle, status);
							}
						}
					}
				}
			}
		});

		data = new TableWrapData();
		data.colspan = 2;
		descriptionLabel.setLayoutData(data);

		return head;
	}

	static ExecutionEvent createExecutionEvent(Command command, IHandlerService handlerService) {
		return new ExecutionEvent(command, Collections.emptyMap(), null,
				TasksUiInternal.createDiscoveryWizardEvaluationContext(handlerService));
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
		Color color = TasksUiPlugin.getDefault().getFormColors(text.getDisplay()).getColor(IFormColors.TITLE);
//		Color color = PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getColorRegistry().get(
//				CommonThemes.COLOR_COMPLETED);
		text.setForeground(color);
		return textFont;
	}

	public void handleEvent(final ServiceMessageEvent event) {
		switch (event.getEventKind()) {
		case MESSAGE_UPDATE:
			IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
			preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_CHECKTIME, new Date().getTime());
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

	public void setMessage(ServiceMessage message) {
		if (!parent.isDisposed() && message != null && (head == null || head.isDisposed())) {
			createControl(parent);
			if (message.getETag() != null && message.getLastModified() != null) {
				IPreferenceStore preferenceStore = TasksUiPlugin.getDefault().getPreferenceStore();
				preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_ETAG, message.getETag());
				preferenceStore.setValue(ITasksUiPreferenceConstants.LAST_SERVICE_MESSAGE_LAST_MODIFIED,
						message.getLastModified());
			}

			this.currentMessage = message;

			setTitle(message.getTitle());
			setDescription(message.getDescription());
			setTitleImage(Dialog.getImage(message.getImage()));
			parent.layout(true);
		}
	}

}
