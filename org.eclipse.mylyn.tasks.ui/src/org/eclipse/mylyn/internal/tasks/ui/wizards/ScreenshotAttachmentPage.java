/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
 * @author Willian Mitsuda
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
	 * Stores the original selection rectangle, before a selection resize/move operation starts
	 */
	private Rectangle originalSelection;

	/**
	 * Temporary storage for selection start point or selection resizing initial reference point; this value is
	 * normalized to real image coordinates, no matter the zoom level (see {@link #scaleFactor})
	 */
	private Point startPoint;

	/**
	 * What sides I'm resizing when doing an selection {@link Action#RESIZING_SELECTION resize}
	 */
	private Set<SelectionSide> resizableSides = EnumSet.noneOf(SelectionSide.class);

	/**
	 * Scale factor of displayed image compared to the original image
	 */
	private double scaleFactor = 1.0;

	/**
	 * Manages allocated cursors
	 */
	private Map<Integer, Cursor> cursors = new HashMap<Integer, Cursor>();

	/**
	 * Available actions for the screenshot editor
	 */
	private static enum Action {

		IDLE, SELECTING, RESIZING_SELECTION, MOVING_SELECTION;

	};

	/**
	 * What am I doing now?
	 */
	private Action currentAction = Action.IDLE;

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

		allocateCursors();

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

					if (currentAction == Action.IDLE) {
						if (currentSelection != null) {
							drawSelection(e.gc);
						}
					} else if (currentAction == Action.SELECTING || currentAction == Action.RESIZING_SELECTION
							|| currentAction == Action.MOVING_SELECTION) {
						if (currentSelection != null) {
							drawSelectionPreview(e.gc);
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
				if (fitButton.getSelection()) {
					refreshCanvasSize();
				}
			}
		});

		registerMouseListeners();
	}

	@Override
	public void dispose() {
		canvas.setCursor(null);
		for (Cursor cursor : cursors.values()) {
			cursor.dispose();
		}
		super.dispose();
	}

	private void allocateCursors() {
		Display display = Display.getCurrent();
		cursors.put(SWT.CURSOR_ARROW, new Cursor(display, SWT.CURSOR_ARROW));
		cursors.put(SWT.CURSOR_SIZEALL, new Cursor(display, SWT.CURSOR_SIZEALL));
		cursors.put(SWT.CURSOR_SIZENWSE, new Cursor(display, SWT.CURSOR_SIZENWSE));
		cursors.put(SWT.CURSOR_SIZENESW, new Cursor(display, SWT.CURSOR_SIZENESW));
		cursors.put(SWT.CURSOR_SIZENS, new Cursor(display, SWT.CURSOR_SIZENS));
		cursors.put(SWT.CURSOR_SIZEWE, new Cursor(display, SWT.CURSOR_SIZEWE));
		cursors.put(SWT.CURSOR_CROSS, new Cursor(display, SWT.CURSOR_CROSS));
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
	 * Sets the selection rectangle based on the initial selection start point previously set in {@link #startPoint} and
	 * the end point passed as parameters to this method
	 * <p>
	 * The coordinates are based on the real image coordinates
	 */
	private void refreshCurrentSelection(int x, int y) {
		int startX = Math.min(startPoint.x, x);
		int startY = Math.min(startPoint.y, y);
		int width = Math.abs(startPoint.x - x);
		int height = Math.abs(startPoint.y - y);
		currentSelection = new Rectangle(startX, startY, width, height);

		// Decreases 1 pixel size from original image because Rectangle.intersect() consider them as right-bottom limit
		Rectangle imageBounds = screenshotImage.getBounds();
		imageBounds.width--;
		imageBounds.height--;
		currentSelection.intersect(imageBounds);
	}

	/**
	 * Create the grab points to resize the selection; this method should be called every time the selection or zoom
	 * level is changed
	 */
	private void setUpGrabPoints() {
		if (currentSelection == null) {
			return;
		}

		canvas.setCursor(null);
		grabPoints.clear();
		Rectangle scaledSelection = getScaledSelection();
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x, scaledSelection.y, SWT.CURSOR_SIZENWSE, EnumSet.of(
				SelectionSide.LEFT, SelectionSide.TOP)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x + scaledSelection.width / 2, scaledSelection.y,
				SWT.CURSOR_SIZENS, EnumSet.of(SelectionSide.TOP)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x + scaledSelection.width, scaledSelection.y,
				SWT.CURSOR_SIZENESW, EnumSet.of(SelectionSide.TOP, SelectionSide.RIGHT)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x, scaledSelection.y + scaledSelection.height / 2,
				SWT.CURSOR_SIZEWE, EnumSet.of(SelectionSide.LEFT)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x + scaledSelection.width, scaledSelection.y
				+ scaledSelection.height / 2, SWT.CURSOR_SIZEWE, EnumSet.of(SelectionSide.RIGHT)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x, scaledSelection.y + scaledSelection.height,
				SWT.CURSOR_SIZENESW, EnumSet.of(SelectionSide.LEFT, SelectionSide.BOTTOM)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x + scaledSelection.width / 2, scaledSelection.y
				+ scaledSelection.height, SWT.CURSOR_SIZENS, EnumSet.of(SelectionSide.BOTTOM)));
		grabPoints.add(GrabPoint.createGrabPoint(scaledSelection.x + scaledSelection.width, scaledSelection.y
				+ scaledSelection.height, SWT.CURSOR_SIZENWSE, EnumSet.of(SelectionSide.BOTTOM, SelectionSide.RIGHT)));
	}

	private void refreshSelectionResize(int x, int y) {
		currentSelection = new Rectangle(originalSelection.x, originalSelection.y, originalSelection.width,
				originalSelection.height);
		int deltaX = x - startPoint.x;
		int deltaY = y - startPoint.y;
		Rectangle imageBounds = screenshotImage.getBounds();

		// Check current selection limits
		if (resizableSides.contains(SelectionSide.LEFT)) {
			deltaX = Math.min(deltaX, originalSelection.width);
			if (originalSelection.x + deltaX < 0) {
				deltaX = -originalSelection.x;
			}
		}
		if (resizableSides.contains(SelectionSide.RIGHT)) {
			deltaX = Math.max(deltaX, -originalSelection.width);
			if (originalSelection.x + originalSelection.width + deltaX - 1 > imageBounds.width) {
				deltaX = imageBounds.width - (originalSelection.x + originalSelection.width);
			}
		}
		if (resizableSides.contains(SelectionSide.TOP)) {
			deltaY = Math.min(deltaY, originalSelection.height);
			if (originalSelection.y + deltaY < 0) {
				deltaY = -originalSelection.y;
			}
		}
		if (resizableSides.contains(SelectionSide.BOTTOM)) {
			deltaY = Math.max(deltaY, -originalSelection.height);
			if (originalSelection.y + originalSelection.height + deltaY - 1 > imageBounds.height) {
				deltaY = imageBounds.height - (originalSelection.y + originalSelection.height);
			}
		}

		// Adjust corresponding sides
		if (resizableSides.contains(SelectionSide.LEFT)) {
			currentSelection.x += deltaX;
			currentSelection.width -= deltaX;
		}
		if (resizableSides.contains(SelectionSide.RIGHT)) {
			currentSelection.width += deltaX;
		}
		if (resizableSides.contains(SelectionSide.TOP)) {
			currentSelection.y += deltaY;
			currentSelection.height -= deltaY;
		}
		if (resizableSides.contains(SelectionSide.BOTTOM)) {
			currentSelection.height += deltaY;
		}
	}

	private void refreshSelectionPosition(int x, int y) {
		int newX = originalSelection.x + (x - startPoint.x);
		int newY = originalSelection.y + (y - startPoint.y);
		if (newX < 0) {
			newX = 0;
		}
		if (newY < 0) {
			newY = 0;
		}
		Rectangle imageBounds = screenshotImage.getBounds();
		if (newX + originalSelection.width - 1 > imageBounds.width) {
			newX = imageBounds.width - originalSelection.width;
		}
		if (newY + originalSelection.height - 1 > imageBounds.height) {
			newY = imageBounds.height - originalSelection.height;
		}
		currentSelection = new Rectangle(newX, newY, originalSelection.width, originalSelection.height);
	}

	private void registerMouseListeners() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			/**
			 * If a selection is in course, moving the mouse around refreshes the selection rectangle
			 */
			public void mouseMove(MouseEvent e) {
				if (currentAction == Action.SELECTING) {
					// Selection in course
					refreshCurrentSelection((int) Math.round(e.x / scaleFactor), (int) Math.round(e.y / scaleFactor));
					canvas.redraw();
				} else if (currentAction == Action.RESIZING_SELECTION) {
					refreshSelectionResize((int) Math.round(e.x / scaleFactor), (int) Math.round(e.y / scaleFactor));
					canvas.redraw();
				} else if (currentAction == Action.MOVING_SELECTION) {
					refreshSelectionPosition((int) Math.round(e.x / scaleFactor), (int) Math.round(e.y / scaleFactor));
					canvas.redraw();
				} else if (currentAction == Action.IDLE && currentSelection != null) {
					boolean cursorSet = false;

					// No selection in course, but have something selected; first test if I'm hovering some grab point
					for (GrabPoint point : grabPoints) {
						if (point.grabArea.contains(e.x, e.y)) {
							canvas.setCursor(cursors.get(point.cursorType));
							cursorSet = true;
							break;
						}
					}

					// Test if I'm inside selection, so I can move it
					if (!cursorSet && getScaledSelection().contains(e.x, e.y)) {
						canvas.setCursor(cursors.get(SWT.CURSOR_SIZEALL));
						cursorSet = true;
					}
					if (!cursorSet && canvas.getCursor() != null) {
						canvas.setCursor(null);
					}
				}
			}

		});

		canvas.addMouseListener(new MouseAdapter() {

			/**
			 * Releasing the mouse button ends the selection; compute the selection rectangle and redraw the cropped
			 * image
			 */
			public void mouseUp(MouseEvent e) {
				if (currentAction == Action.SELECTING || currentAction == Action.RESIZING_SELECTION
						|| currentAction == Action.MOVING_SELECTION) {
					canvas.setCursor(cursors.get(SWT.CURSOR_ARROW));

					int scaledX = (int) Math.round(e.x / scaleFactor);
					int scaledY = (int) Math.round(e.y / scaleFactor);
					if (currentAction == Action.SELECTING) {
						refreshCurrentSelection(scaledX, scaledY);
					} else if (currentAction == Action.RESIZING_SELECTION) {
						refreshSelectionResize(scaledX, scaledY);
					} else if (currentAction == Action.MOVING_SELECTION) {
						refreshSelectionPosition(scaledX, scaledY);
					}
					if (currentSelection.width == 0 && currentSelection.height == 0) {
						currentSelection = null;
					}
					setUpGrabPoints();
					startPoint = null;
					currentAction = Action.IDLE;

					canvas.redraw();
				}
			}

			/**
			 * Pressing mouse button starts a selection; normalizes and marks the start point
			 */
			public void mouseDown(MouseEvent e) {
				if (currentAction != Action.IDLE) {
					return;
				}

				// Check the most appropriate action to follow; first check if I'm on some grab point
				if (currentSelection != null) {
					for (GrabPoint point : grabPoints) {
						if (point.grabArea.contains(e.x, e.y)) {
							originalSelection = currentSelection;
							currentAction = Action.RESIZING_SELECTION;
							resizableSides = point.resizableSides;
							startPoint = new Point((int) (e.x / scaleFactor), (int) (e.y / scaleFactor));
							canvas.redraw();
							return;
						}
					}
				}

				// Check if I could move the selection
				if (currentSelection != null
						&& currentSelection.contains((int) (e.x / scaleFactor), (int) (e.y / scaleFactor))) {
					originalSelection = currentSelection;
					currentAction = Action.MOVING_SELECTION;
					startPoint = new Point((int) (e.x / scaleFactor), (int) (e.y / scaleFactor));
					canvas.redraw();
					return;
				}

				// Do a simple selection
				canvas.setCursor(cursors.get(SWT.CURSOR_CROSS));
				currentAction = Action.SELECTING;
				currentSelection = null;
				startPoint = new Point((int) (e.x / scaleFactor), (int) (e.y / scaleFactor));
				canvas.redraw();
			}

		});

	}

	private void clearSelection() {
		currentSelection = null;
		startPoint = null;
	}

	/**
	 * Recalculates image canvas size based on "fit on canvas" setting, set up the grab points, and redraws
	 * <p>
	 * This method should be called whenever the {@link #screenshotImage image} <strong>visible</strong> size is
	 * changed, which can happen when:
	 * <p>
	 * <ul>
	 * <li>The "Fit Image" setting is changed, so the image zoom level changes
	 * <li>The image changes (by recapturing)
	 * <li>The canvas is resized (indirectly happens by resizing the wizard page) <strong>AND</strong> "Fit Image"
	 * setting is ON
	 * </ul>
	 * <p>
	 * Calling this method under other circumstances may lead to strange behavior in the scrolled composite
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
		setUpGrabPoints();
		canvas.redraw();
	}

	/**
	 * Decorates the screenshot canvas with the selection rectangle; this is done while still selecting, so it does not
	 * have all adornments of a {@link #drawSelection(GC) finished} selection
	 */
	@SuppressWarnings("deprecation")
	private void drawSelectionPreview(GC gc) {
		gc.setLineDash(new int[] { 4 });
		gc.setXORMode(true);
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		gc.drawRectangle(getScaledSelection());
		gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		gc.drawRectangle(getScaledSelection());
		gc.setXORMode(false);
		gc.setLineStyle(SWT.LINE_SOLID);
	}

	/**
	 * Decorates the screenshot canvas with the selection rectangle, resize grab points and other adornments
	 */
	private void drawSelection(GC gc) {
		Rectangle scaledSelection = getScaledSelection();

		// Draw shadow
		gc.setBackground(TaskListColorsAndFonts.GRAY);
		gc.setAdvanced(true);
		gc.setAlpha(120);

		Region invertedSelection = new Region();
		invertedSelection.add(canvas.getClientArea());
		invertedSelection.subtract(scaledSelection);
		gc.setClipping(invertedSelection);
		gc.fillRectangle(canvas.getClientArea());
		gc.setClipping((Region) null);
		invertedSelection.dispose();

		gc.setAdvanced(false);

		// Draw selection rectangle
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawRectangle(scaledSelection);

		// Draw grab points
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		for (GrabPoint point : grabPoints) {
			gc.fillRectangle(point.grabArea);
			gc.drawRectangle(point.grabArea);
		}
	}

	private static enum SelectionSide {

		LEFT, RIGHT, TOP, BOTTOM;

	};

	private static final int SQUARE_SIZE = 3;

	private static class GrabPoint {

		public Rectangle grabArea;

		public int cursorType;

		public Set<SelectionSide> resizableSides;

		public static GrabPoint createGrabPoint(int x, int y, int cursorType, Set<SelectionSide> resizableSides) {
			GrabPoint point = new GrabPoint();
			point.grabArea = new Rectangle(x - SQUARE_SIZE, y - SQUARE_SIZE, SQUARE_SIZE * 2 + 1, SQUARE_SIZE * 2 + 1);
			point.cursorType = cursorType;
			point.resizableSides = resizableSides;
			return point;
		}

	}

	private List<GrabPoint> grabPoints = new ArrayList<GrabPoint>(8);

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
