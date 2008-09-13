/*******************************************************************************
 * Copyright (c) 2004, 2008 Jeff Pound and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvement
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;

/**
 * Shows a preview of an attachment.
 * 
 * @author Jeff Pound
 * @author Steffen Pingel
 */
// TODO 3.1 rename to PreviewAttachmentPage
public class PreviewAttachmentPage2 extends WizardPage {

	private static final String DESCRIPTION = "Review the attachment before submitting";

	protected static final int MAX_TEXT_SIZE = 50000;

	private static final String PAGE_NAME = "PreviewAttachmentPage";

	private static final String TITLE = "Attachment Preview";

	private final Set<String> imageTypes;

	private final TaskAttachmentModel model;

	private Button runInBackgroundButton;

	private ScrolledComposite scrolledComposite;

	private final Set<String> textTypes;

	private Composite contentComposite;

	public PreviewAttachmentPage2(TaskAttachmentModel model) {
		super(PAGE_NAME);
		this.model = model;
		setTitle(TITLE);
		setDescription(DESCRIPTION);

		textTypes = new HashSet<String>();
		textTypes.add("text/plain");
		textTypes.add("text/html");
		textTypes.add("text/html");
		textTypes.add("application/xml");

		imageTypes = new HashSet<String>();
		imageTypes.add("image/jpeg");
		imageTypes.add("image/gif");
		imageTypes.add("image/png");
	}

	private void adjustScrollbars(Rectangle imgSize) {
		Rectangle clientArea = scrolledComposite.getClientArea();

		ScrollBar hBar = scrolledComposite.getHorizontalBar();
		hBar.setMinimum(0);
		hBar.setMaximum(imgSize.width - 1);
		hBar.setPageIncrement(clientArea.width);
		hBar.setIncrement(10);

		ScrollBar vBar = scrolledComposite.getVerticalBar();
		vBar.setMinimum(0);
		vBar.setMaximum(imgSize.height - 1);
		vBar.setPageIncrement(clientArea.height);
		vBar.setIncrement(10);
	}

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

		contentComposite = new Composite(composite, SWT.NONE);
		contentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		contentComposite.setLayout(new GridLayout());

		runInBackgroundButton = new Button(composite, SWT.CHECK);
		runInBackgroundButton.setText("Run in background");
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			Control[] children = contentComposite.getChildren();
			for (Control control : children) {
				control.dispose();
			}
			if (isTextAttachment() || isImageAttachment()) {
				Object content = getContent(contentComposite);
				if (content instanceof String) {
					createTextPreview(contentComposite, (String) content);
				} else if (content instanceof Image) {
					createImagePreview(contentComposite, (Image) content);
				}
			} else {
				createGenericPreview(contentComposite);
			}
			contentComposite.layout(true, true);
		}
		super.setVisible(visible);
	}

	private void createErrorPreview(Composite composite, String message) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_BOTH));
		label.setText(message);
	}

	private void createGenericPreview(Composite composite) {
		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_BOTH));
		// TODO 3.1 put filename on model
		String name = model.getSource().getName();
		TaskAttachmentMapper taskAttachment = TaskAttachmentMapper.createFrom(model.getAttribute());
		if (taskAttachment.getFileName() != null) {
			name = taskAttachment.getFileName();
		}
		label.setText("Attaching File '" + name + "'\nA preview the type '" + model.getContentType()
				+ "' is currently not available");
	}

	private void createImagePreview(Composite composite, final Image bufferedImage) {
		scrolledComposite = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite canvasComposite = new Composite(scrolledComposite, SWT.NONE);
		canvasComposite.setLayout(GridLayoutFactory.fillDefaults().create());
		Canvas canvas = new Canvas(canvasComposite, SWT.NO_BACKGROUND);
		final Rectangle imgSize = bufferedImage.getBounds();
		canvas.setLayoutData(GridDataFactory.fillDefaults().align(SWT.CENTER, SWT.CENTER).grab(true, true).hint(
				imgSize.width, imgSize.height).create());
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent event) {
				event.gc.drawImage(bufferedImage, 0, 0);
			}
		});
		canvas.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent event) {
				bufferedImage.dispose();
			}
		});
		canvas.setSize(imgSize.width, imgSize.height);
		scrolledComposite.setMinSize(imgSize.width, imgSize.height);
		scrolledComposite.setContent(canvasComposite);
		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent event) {
				adjustScrollbars(imgSize);
			}

		});
		adjustScrollbars(imgSize);
	}

	private void createTextPreview(Composite composite, String contents) {
		Text text = new Text(composite, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = composite.getBounds().y;
		gd.widthHint = composite.getBounds().x;
		text.setLayoutData(gd);
		text.setText(contents);
	}

	private Object getContent(final Composite composite) {
		final Object result[] = new Object[1];
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						monitor.beginTask("Preparing preview", IProgressMonitor.UNKNOWN);
						final InputStream in = model.getSource().createInputStream(monitor);
						try {
							if (isTextAttachment()) {
								StringBuilder content = new StringBuilder();
								BufferedReader reader = new BufferedReader(new InputStreamReader(in));
								String line;
								while ((line = reader.readLine()) != null && content.length() < MAX_TEXT_SIZE
										&& !monitor.isCanceled()) {
									content.append(line);
									content.append("\n");
								}
								result[0] = content.toString();
							} else if (isImageAttachment()) {
								Display.getDefault().syncExec(new Runnable() {
									public void run() {
										// Uses double buffering to paint the image; there was a weird behavior
										// with transparent images and flicker with large images
										Image originalImage = new Image(getShell().getDisplay(), in);
										final Image bufferedImage = new Image(getShell().getDisplay(),
												originalImage.getBounds());
										GC gc = new GC(bufferedImage);
										gc.setBackground(composite.getBackground());
										gc.fillRectangle(originalImage.getBounds());
										gc.drawImage(originalImage, 0, 0);
										gc.dispose();
										originalImage.dispose();
										result[0] = bufferedImage;
									}
								});
							}
						} catch (IOException e) {
							throw new InvocationTargetException(e);
						} finally {
							try {
								in.close();
							} catch (IOException e) {
								StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN,
										"Failed to close file", e));
							}
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, "Error generating preview", e));
			createErrorPreview(composite, "Could not create preview");
			return null;
		} catch (InterruptedException e) {
			return null;
		}
		return result[0];
	}

	private boolean isImageAttachment() {
		return imageTypes.contains(model.getContentType());
	}

	private boolean isTextAttachment() {
		return textTypes.contains(model.getContentType());
	}

	public boolean runInBackground() {
		return runInBackgroundButton.getSelection();
	}

}
