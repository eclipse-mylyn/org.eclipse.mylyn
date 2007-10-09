/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.LocalAttachment;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * A wizard page to create a screenshot from the display.
 * 
 * @author Balazs Brinkus (bug 160572)
 * @author Mik Kersten
 */
public class ScreenshotAttachmentPage extends WizardPage {

	private ScreenshotAttachmentPage page;

	private LocalAttachment attachment;

	private Button captureButton;

	private Button fitButton;

	private Image screenshotImage;

	private Canvas canvas;

	private ScrolledComposite scrolledComposite;

	private Rectangle cropRegionBounds;

	private boolean isCropFit;

	private boolean isCropActive;

	private Rectangle displayBounds;

	private Rectangle scrolledBounds;

	protected ScreenshotAttachmentPage(LocalAttachment attachment) {
		super("ScreenShotAttachment");
		setTitle("Capture Screenshot");
		setDescription("After capturing, drag the mouse to crop. This window will not be captured. " +
				"Note that you can continue to prepare the screenshot.");
		this.attachment = attachment;
		this.page = this;
	}

	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		setControl(composite);

		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		buttonsComposite.setLayout(new GridLayout(3, false));
		captureButton = new Button(buttonsComposite, SWT.PUSH);
		captureButton.setText("Capture Desktop");
		captureButton.setImage(TasksUiImages.getImage(TasksUiImages.IMAGE_CAPTURE));
		captureButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				captureScreenshotContent();
				page.setErrorMessage(null);
				fitButton.setEnabled(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				//ignore
			}

		});
		
//		captureDelayedButton = new Button(buttonsComposite, SWT.PUSH);
//		final String captureIn = "Capture in ";
//		final int secondsDelay = 1;
//		captureDelayedButton.setText(captureIn + secondsDelay +" seconds");
//		captureDelayedButton.setImage(TasksUiImages.getImage(TasksUiImages.IMAGE_CAPTURE));
//		captureDelayedButton.addSelectionListener(new SelectionListener() {
//
//			public void widgetSelected(SelectionEvent e) {
//				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//					public void run() {
//						getShell().setVisible(false);
//						for (int i = 1; i <= secondsDelay; i++) {
//							try {
//								Thread.sleep(1000);
////								captureDelayedButton.setText("Capture in " + (secondsDelay-i) + " seconds");
//							} catch (InterruptedException e1) {
//								// ignore
//							}	
//						}
//						captureScreenshotContent();
//						page.setErrorMessage(null);
//						fitButton.setEnabled(true);
//						captureDelayedButton.setText(captureIn + secondsDelay +" seconds");			
//						getShell().setVisible(true);
//					}
//				});
//			}
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				//ignore
//			}
//		});
		

		fitButton = new Button(buttonsComposite, SWT.TOGGLE);
		fitButton.setText("Fit Image");
		fitButton.setImage(TasksUiImages.getImage(TasksUiImages.IMAGE_FIT));
		fitButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				drawCanvas();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

		});
		fitButton.setSelection(true);
		fitButton.setEnabled(false);
		isCropFit = true;
		isCropActive = false;

		scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, true)
				.create());

		canvas = new Canvas(scrolledComposite, SWT.DOUBLE_BUFFERED | SWT.NONE);
		scrolledComposite.setContent(canvas);
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				if (screenshotImage != null) {
					Rectangle imageBounds = screenshotImage.getBounds();
					Rectangle canvasBounds = canvas.getBounds();

					if (fitButton.getSelection())
						e.gc.drawImage(screenshotImage, 0, 0, imageBounds.width, imageBounds.height, 0, 0,
								canvasBounds.width, canvasBounds.height);
					else
						e.gc.drawImage(screenshotImage, 0, 0);

					if (cropRegionBounds != null)
						drawRegion(e.gc);

				} else {
//					page.setErrorMessage("Screenshot required");
					fitButton.setEnabled(false);
				}
			}
		});

		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				drawCanvas();
			}
		});

		canvas.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (!isCropActive && screenshotImage != null) {
					drawCanvas();
					cropScreenshotContent();
				}
			}
		});

	}

	@Override
	public boolean isPageComplete() {
		if (screenshotImage == null)
			return false;
		return true;
	}

	@Override
	public IWizardPage getNextPage() {
		NewAttachmentPage page = (NewAttachmentPage) getWizard().getPage("AttachmentDetails");
		attachment.setContentType("image/jpeg");
		page.setFilePath(InputAttachmentSourcePage.SCREENSHOT_LABEL);
		page.setContentType();
		getCropScreenshot();
		return page;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private void captureScreenshotContent() {

		final Display display = Display.getDefault();
		final Shell wizardShell = getWizard().getContainer().getShell();
		wizardShell.setVisible(false);
		isCropFit = fitButton.getSelection();
		
		// NOTE: need a wait since the shell can take time to disappear (e.g. fade on Vista)
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// ignore
		}

		display.asyncExec(new Runnable() {
			public void run() {
				GC gc = new GC(display);
				Rectangle displayBounds = display.getBounds();
				screenshotImage = new Image(display, displayBounds);
				gc.copyArea(screenshotImage, 0, 0);
				gc.drawRectangle(0, 0, displayBounds.width - 1, displayBounds.height - 1);
				gc.dispose();
				drawCanvas();
				wizardShell.setVisible(true);
				if (screenshotImage != null) {
					setPageComplete(true);
				}
			}
		});
	}

	private void cropScreenshotContent() {

		final Point downPoint = new Point(-1, -1);

		final Display display = Display.getDefault();

		final Rectangle originalBounds = screenshotImage.getBounds();

		final GC cropRegionGC = new GC(canvas);

		isCropActive = true;

		final MouseMoveListener mouseMoveListener = new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				if (downPoint.x != -1 && downPoint.y != -1) {
					int width = e.x - downPoint.x;
					int height = e.y - downPoint.y;
					canvas.redraw();
					cropRegionGC.drawRectangle(downPoint.x, downPoint.y, width, height);
				}
			}
		};

		final MouseListener mouseListener = new MouseListener() {
			public void mouseUp(MouseEvent e) {
				if (downPoint.x != -1 && downPoint.y != -1) {

					if (downPoint.x > e.x) {
						int x = downPoint.x;
						downPoint.x = e.x;
						e.x = x;
					}
					if (downPoint.y > e.y) {
						int y = downPoint.y;
						downPoint.y = e.y;
						e.y = y;
					}

					isCropActive = false;

					int width;
					if (e.x > originalBounds.width) {
						width = originalBounds.width - downPoint.x;
					} else if (e.x < 0) {
						width = downPoint.x;
						downPoint.x = 0;
					} else {
						width = e.x - downPoint.x;
					}

					int height;
					if (e.y > originalBounds.height) {
						height = originalBounds.height - downPoint.y;
					} else if (e.y < 0) {
						height = downPoint.y;
						downPoint.y = 0;
					} else {
						height = e.y - downPoint.y;
					}

					display.getActiveShell().setCursor(new Cursor(null, SWT.CURSOR_ARROW));

					if (width == 0 || height == 0)
						cropRegionBounds = null;
					else
						cropRegionBounds = new Rectangle(downPoint.x, downPoint.y, width, height);

					canvas.removeMouseMoveListener(mouseMoveListener);
					canvas.removeMouseListener(this);
					downPoint.x = -1;
					downPoint.y = -1;

					cropRegionGC.dispose();

					drawCanvas();
				}
			}

			public void mouseDown(MouseEvent e) {
				if (downPoint.x == -1 && downPoint.y == -1) {
					cropRegionBounds = null;
					isCropFit = fitButton.getSelection();
					display.getActiveShell().setCursor(new Cursor(null, SWT.CURSOR_CROSS));
					downPoint.x = e.x;
					downPoint.y = e.y;
					canvas.addMouseMoveListener(mouseMoveListener);
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}
		};

		canvas.addMouseListener(mouseListener);

	}

	private void drawCanvas() {
		if (fitButton.getSelection()) {
			Rectangle bounds = scrolledComposite.getBounds();
			bounds.x = 0;
			bounds.y = 0;
			bounds.width -= 5;
			bounds.height -= 5;
			canvas.setBounds(bounds);
		} else {
			canvas.setBounds(screenshotImage.getBounds());
		}
		scrolledComposite.redraw();
		canvas.redraw();
	}

	private void drawRegion(GC gc) {

		Rectangle displayBounds = Display.getDefault().getBounds();
		Rectangle bounds = scrolledComposite.getBounds();
		bounds.width -= 5;
		bounds.height -= 5;

		double ratioX = (double) displayBounds.width / (double) bounds.width;
		double ratioY = (double) displayBounds.height / (double) bounds.height;

		Rectangle newCropRegion = new Rectangle(0, 0, 0, 0);

		if (isCropFit && !fitButton.getSelection()) {
			newCropRegion.x = (int) (cropRegionBounds.x * ratioX);
			newCropRegion.width = (int) (cropRegionBounds.width * ratioX);
			newCropRegion.y = (int) (cropRegionBounds.y * ratioY);
			newCropRegion.height = (int) (cropRegionBounds.height * ratioY);
		} else if (!isCropFit && fitButton.getSelection()) {
			newCropRegion.x = (int) (cropRegionBounds.x / ratioX);
			newCropRegion.width = (int) (cropRegionBounds.width / ratioX);
			newCropRegion.y = (int) (cropRegionBounds.y / ratioY);
			newCropRegion.height = (int) (cropRegionBounds.height / ratioY);
		} else {
			newCropRegion = cropRegionBounds;
		}

		Rectangle topRegionBounds = new Rectangle(0, 0, displayBounds.width, newCropRegion.y);
		int bottomRegionY = newCropRegion.y + newCropRegion.height;
		int bottomRegionHeight = displayBounds.height - bottomRegionY;
		Rectangle bottomRegionBounds = new Rectangle(0, bottomRegionY, displayBounds.width, bottomRegionHeight);
		Rectangle leftRegionBounds = new Rectangle(0, newCropRegion.y, newCropRegion.x, newCropRegion.height);
		int rightRegionX = newCropRegion.x + newCropRegion.width;
		int rightRegionWidth = displayBounds.width - rightRegionX;
		Rectangle rightRegionBounds = new Rectangle(rightRegionX, newCropRegion.y, rightRegionWidth,
				newCropRegion.height);

		gc.setLineStyle(SWT.LINE_DASH);
		gc.setAdvanced(true);
		gc.setAlpha(200);

		gc.fillRectangle(topRegionBounds);
		gc.fillRectangle(bottomRegionBounds);
		gc.fillRectangle(leftRegionBounds);
		gc.fillRectangle(rightRegionBounds);

		gc.drawRectangle(newCropRegion);
	}

	private Image getCropScreenshot() {
		displayBounds = null;
		scrolledBounds = null;
		
		if (cropRegionBounds == null)
			return screenshotImage;

		Rectangle bounds;
		if (!isCropFit) {
			bounds = cropRegionBounds;
		} else {
			bounds = new Rectangle(0, 0, 0, 0);
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				public void run() {
					displayBounds = Display.getDefault().getBounds();
					scrolledBounds = scrolledComposite.getBounds();
				}
			});

			if (displayBounds == null) {
				return null;
			} else {
				double ratioX = (double) displayBounds.width / (double) scrolledBounds.width; 
				double ratioY = (double) displayBounds.height / (double) scrolledBounds.height;
				bounds.x = (int) (cropRegionBounds.x * ratioX);
				bounds.width = (int) (cropRegionBounds.width * ratioX);
				bounds.y = (int) (cropRegionBounds.y * ratioY);
				bounds.height = (int) (cropRegionBounds.height * ratioY);
			}
		}

		Image image = new Image(Display.getDefault(), bounds);
		GC gc = new GC(image);
		gc.drawImage(screenshotImage, bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, bounds.width,
				bounds.height);
		gc.drawRectangle(0, 0, bounds.width - 1, bounds.height - 1);
		gc.dispose();

		return image;
	}

	public Image getScreenshotImage() {
		return getCropScreenshot();
	}

}
