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
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A wizard page to create a screenshot from the display.
 * <p>
 * Note: this class uses {@link GC#setXORMode(boolean)} which is deprecated because of lack of Mac implementation
 * (bug#50228). The strategy to make it work on platforms XOR is implemented and still have a good looking in platforms
 * where it is not implemented, is to draw each line 2 times: first using a WHITE foreground followed by the same draw
 * using a BLACK foreground. On platforms where XOR is implemented, the second draw will have no effect, since anything
 * XOR black results in noop. On platforms where XOR is NOT implemented, the {@link GC#setXORMode(boolean)} is a noop,
 * so the end result will be a black line.
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

	/**
	 * Stores the selection rectangle; this value is normalized to real image coordinates, no matter the zoom level (see
	 * {@link #scaleFactor})
	 */
	private Rectangle currentSelection;

	/**
	 * Temporary storage for selection start point; this value is normalized to real image coordinates, no matter the
	 * zoom level (see {@link #scaleFactor})
	 * <p>
	 * This is also used to signal if a selection is in course if it is != null
	 */
	private Point selectionStartPoint;

	/**
	 * Scale factor of displayed image compared to the original image
	 */
	private double scaleFactor = 1.0;

	protected ScreenshotAttachmentPage(LocalAttachment attachment) {
		super("ScreenShotAttachment");
		setTitle("Capture Screenshot");
		setDescription("After capturing, drag the mouse to crop. This window will not be captured. "
				+ "Note that you can continue to interact with the workbench in order to set up the screenshot.");
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
		captureButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				captureScreenshotContent();
				page.setErrorMessage(null);
				fitButton.setEnabled(true);
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
		fitButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				refreshCanvasSize();
			}

		});
		fitButton.setSelection(true);
		fitButton.setEnabled(false);

		scrolledComposite = new ScrolledComposite(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.FILL)
				.grab(true, true)
				.create());

		canvas = new Canvas(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(canvas);
		canvas.addPaintListener(new PaintListener() {
			@SuppressWarnings("deprecation")
			public void paintControl(PaintEvent e) {
				if (screenshotImage != null) {
					Rectangle imageBounds = screenshotImage.getBounds();
					Rectangle canvasBounds = canvas.getClientArea();

					if (fitButton.getSelection()) {
						e.gc.drawImage(screenshotImage, 0, 0, imageBounds.width, imageBounds.height, 0, 0,
								canvasBounds.width, canvasBounds.height);
					} else {
						e.gc.drawImage(screenshotImage, 0, 0);
					}

					if (currentSelection != null) {
						if (selectionStartPoint != null) {
							e.gc.setLineDash(new int[] { 4 });
							e.gc.setXORMode(true);
							e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
							e.gc.drawRectangle(getScaledSelection());
							e.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
							e.gc.drawRectangle(getScaledSelection());
							e.gc.setXORMode(false);
							e.gc.setLineStyle(SWT.LINE_SOLID);
						} else {
							drawRegion(e.gc);
						}
					}
				} else {
//					page.setErrorMessage("Screenshot required");
					fitButton.setEnabled(false);
				}
			}
		});

		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				refreshCanvasSize();
			}
		});

		registerMouseListeners();
	}

	private Rectangle getScaledSelection() {
		if (currentSelection == null) {
			return null;
		}
		int x = (int) Math.round(currentSelection.x * scaleFactor);
		int y = (int) Math.round(currentSelection.y * scaleFactor);
		int right = (int) Math.round((currentSelection.x + currentSelection.width) * scaleFactor);
		int bottom = (int) Math.round((currentSelection.y + currentSelection.height) * scaleFactor);
		int width = Math.min(right, (int) Math.round((screenshotImage.getBounds().width - 1) * scaleFactor)) - x;
		int height = Math.min(bottom, (int) Math.round((screenshotImage.getBounds().height - 1) * scaleFactor)) - y;
		return new Rectangle(x, y, width, height);
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

		// NOTE: need a wait since the shell can take time to disappear (e.g. fade on Vista)
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// ignore
		}

		display.asyncExec(new Runnable() {
			public void run() {
				Rectangle displayBounds = display.getBounds();
				screenshotImage = new Image(display, displayBounds);

				GC gc = new GC(display);
				gc.copyArea(screenshotImage, 0, 0);
				gc.dispose();

				clearSelection();
				refreshCanvasSize();

				wizardShell.setVisible(true);
				if (screenshotImage != null) {
					setPageComplete(true);
				}
			}
		});
	}

	/**
	 * Sets the selection rectangle based on the initial selection start point previously set in
	 * {@link #selectionStartPoint} and the end point passed as parameters to this method
	 * <p>
	 * The coordinates are based on the real image coordinates
	 */
	private void refreshCurrentSelection(int x, int y) {
		int startX = Math.min(selectionStartPoint.x, x);
		int startY = Math.min(selectionStartPoint.y, y);
		int width = Math.abs(selectionStartPoint.x - x);
		int height = Math.abs(selectionStartPoint.y - y);
		currentSelection = new Rectangle(startX, startY, width, height);

		// Decreases 1 pixel size from original image because Rectangle.intersect() consider them as right-bottom limit
		Rectangle imageBounds = screenshotImage.getBounds();
		imageBounds.width--;
		imageBounds.height--;
		currentSelection.intersect(imageBounds);
	}

	private void registerMouseListeners() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			/**
			 * If a selection is in course, moving the mouse around refreshes the selection rectangle
			 */
			public void mouseMove(MouseEvent e) {
				if (selectionStartPoint != null) {
					refreshCurrentSelection((int) Math.round(e.x / scaleFactor), (int) Math.round(e.y / scaleFactor));
					canvas.redraw();
				}
			}

		});

		canvas.addMouseListener(new MouseAdapter() {

			/**
			 * Releasing the mouse button ends the selection; compute the selection rectangle and redraw the cropped
			 * image
			 */
			public void mouseUp(MouseEvent e) {
				if (selectionStartPoint != null) {
					Display.getDefault().getActiveShell().setCursor(new Cursor(null, SWT.CURSOR_ARROW));

					refreshCurrentSelection((int) Math.round(e.x / scaleFactor), (int) Math.round(e.y / scaleFactor));
					if (currentSelection.width == 0 && currentSelection.height == 0) {
						currentSelection = null;
					}
					selectionStartPoint = null;

					canvas.redraw();
				}
			}

			/**
			 * Pressing mouse button starts a selection; normalizes and marks the start point
			 */
			public void mouseDown(MouseEvent e) {
				if (selectionStartPoint == null) {
					currentSelection = null;
					Display.getDefault().getActiveShell().setCursor(new Cursor(null, SWT.CURSOR_CROSS));
					selectionStartPoint = new Point((int) (e.x / scaleFactor), (int) (e.y / scaleFactor));
				}
			}

		});

	}

	private void clearSelection() {
		currentSelection = null;
		selectionStartPoint = null;
	}

	/**
	 * Recalculates image canvas size based on "fit on canvas" setting and redraws
	 * <p>
	 * This method should be called whenever the {@link #screenshotImage image} or zoom level is changed
	 */
	private void refreshCanvasSize() {
		if (fitButton.getSelection()) {
			// This little hack is necessary to get the client area without scrollbars; 
			// they'll be automatically restored if necessary after Canvas.setBounds()
			scrolledComposite.getHorizontalBar().setVisible(false);
			scrolledComposite.getVerticalBar().setVisible(false);

			Rectangle bounds = scrolledComposite.getClientArea();
			if (screenshotImage != null) {
				Rectangle imageBounds = screenshotImage.getBounds();
				if (imageBounds.width > bounds.width || imageBounds.height > bounds.height) {
					double xRatio = (double) bounds.width / imageBounds.width;
					double yRatio = (double) bounds.height / imageBounds.height;
					scaleFactor = Math.min(xRatio, yRatio);
					bounds.width = (int) Math.round(imageBounds.width * scaleFactor);
					bounds.height = (int) Math.round(imageBounds.height * scaleFactor);
				}
			}
			canvas.setBounds(bounds);
		} else {
			scaleFactor = 1.0;
			Rectangle bounds = scrolledComposite.getClientArea();
			if (screenshotImage != null) {
				Rectangle imageBounds = screenshotImage.getBounds();
				bounds.width = imageBounds.width;
				bounds.height = imageBounds.height;
			}
			canvas.setBounds(bounds);
		}
		canvas.redraw();
	}

	private void drawRegion(GC gc) {
		Rectangle scaledSelection = getScaledSelection();

		gc.setBackground(TaskListColorsAndFonts.GRAY);
		gc.setAdvanced(true);
		gc.setAlpha(120);

		Region invertedSelection = new Region();
		invertedSelection.add(canvas.getClientArea());
		invertedSelection.subtract(scaledSelection);
		gc.setClipping(invertedSelection);
		gc.fillRectangle(canvas.getClientArea());
		gc.setClipping((Region) null);

		gc.setAdvanced(false);

		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawRectangle(scaledSelection);
	}

	private Image getCropScreenshot() {
		if (currentSelection == null) {
			return screenshotImage;
		}

		Image image = new Image(Display.getDefault(), currentSelection);
		GC gc = new GC(image);
		gc.drawImage(screenshotImage, currentSelection.x, currentSelection.y, currentSelection.width,
				currentSelection.height, 0, 0, currentSelection.width, currentSelection.height);
		gc.dispose();

		return image;
	}

	public Image getScreenshotImage() {
		return getCropScreenshot();
	}

}
