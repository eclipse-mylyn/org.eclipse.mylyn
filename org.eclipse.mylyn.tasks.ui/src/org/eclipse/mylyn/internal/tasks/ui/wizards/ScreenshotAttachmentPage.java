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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolItem;

/**
 * A wizard page to create a screenshot from the display.
 * 
 * @author Balazs Brinkus (bug 160572)
 * @author Mik Kersten
 * @author Willian Mitsuda
 */
public class ScreenshotAttachmentPage extends WizardPage implements IImageCreator {

	private IAction captureAction;

	private IAction fitAction;

	private IAction cropAction;

	private IAction markAction;

	private IAction colorAction;

	private Image colorIcon;

	private Color markColor;

	private IAction clearAction;

	/**
	 * Original screenshot image; used for backup purposes
	 */
	private Image originalImage;

	/**
	 * Copy of {@link #originalImage original} image; all drawing operations are done here; base for the result image
	 */
	private Image workImage;

	/**
	 * Used to draw into {@link #workImage}
	 */
	private GC workImageGC;

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
	 * Temporary storage for selection start point, selection resizing initial reference point or previous mark point
	 * (it depends on current tool); this value is normalized to real image coordinates, no matter the zoom level (see
	 * {@link #scaleFactor})
	 */
	private Point startPoint;

	/**
	 * What sides I'm resizing when doing an selection {@link EditorAction#RESIZING_SELECTION resize}
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
	private static enum EditorAction {

		CROPPING, SELECTING, RESIZING_SELECTION, MOVING_SELECTION, MARKING;

	};

	/**
	 * What am I doing now?
	 */
	private EditorAction currentAction = EditorAction.CROPPING;

	protected ScreenshotAttachmentPage() {
		super("ScreenShotAttachment");
		setTitle("Capture Screenshot");
		setDescription("After capturing, you can crop the image and make drawings on it. This window will not be captured. "
				+ "Note that you can continue to interact with the workbench in order to set up the screenshot.");
	}

	public void createControl(Composite parent) {
		ViewForm vf = new ViewForm(parent, SWT.BORDER | SWT.FLAT);
		vf.horizontalSpacing = 0;
		vf.verticalSpacing = 0;
		setControl(vf);
		vf.setLayoutData(GridDataFactory.fillDefaults().create());

		allocateCursors();

		// TODO: need disabled versions of all toolbar icons
		ToolBarManager tbm = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.RIGHT);
		captureAction = new Action("&Capture Desktop", IAction.AS_PUSH_BUTTON) {

			private boolean isFirstCapture = true;

			@Override
			public void run() {
				captureScreenshotContent();
				setErrorMessage(null);
				if (isFirstCapture) {
					isFirstCapture = false;
					fitAction.setEnabled(true);
					cropAction.setEnabled(true);
					cropAction.setChecked(true);
					markAction.setEnabled(true);
					clearAction.setEnabled(false);
				}
			}

		};
		captureAction.setToolTipText("Capture Desktop");
		captureAction.setImageDescriptor(ImageDescriptor.createFromImage(TasksUiImages.getImage(TasksUiImages.IMAGE_CAPTURE)));

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

		fitAction = new Action("", IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				refreshCanvasSize();
			}
		};
		fitAction.setToolTipText("Fit Image");
		fitAction.setText("&Fit Image");
		fitAction.setImageDescriptor(ImageDescriptor.createFromImage(TasksUiImages.getImage(TasksUiImages.IMAGE_FIT)));
		fitAction.setChecked(true);
		fitAction.setEnabled(false);

		cropAction = new Action("C&rop", IAction.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				currentAction = EditorAction.CROPPING;
				cropAction.setChecked(true);
				markAction.setChecked(false);
				colorAction.setEnabled(false);
				canvas.redraw();
			}
		};
		cropAction.setToolTipText("Crop");
		cropAction.setImageDescriptor(TasksUiImages.CUT);
		cropAction.setEnabled(false);

		markAction = new Action("&Annotate", IAction.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				currentAction = EditorAction.MARKING;
				cropAction.setChecked(false);
				markAction.setChecked(true);
				colorAction.setEnabled(true);
				canvas.redraw();
			}
		};
		markAction.setToolTipText("Draw annotations on screenshot image");
		markAction.setImageDescriptor(TasksUiImages.EDIT);
//		markAction.setDisabledImageDescriptor(ImageDescriptor.createFromFile(getClass(), "mark_disabled.gif"));
		markAction.setEnabled(false);

		colorAction = new Action("", IAction.AS_DROP_DOWN_MENU) {
			@Override
			public void runWithEvent(final Event e) {
				final ColorSelectionWindow colorWindow = new ColorSelectionWindow(getControl().getShell()) {

					@Override
					protected Point getInitialLocation(Point initialSize) {
						ToolItem toolItem = (ToolItem) e.widget;
						Rectangle itemBounds = toolItem.getBounds();
						Point location = toolItem.getParent().toDisplay(itemBounds.x + itemBounds.width,
								itemBounds.y + itemBounds.height);
						location.x -= initialSize.x;
						return location;
					}

				};
				colorWindow.setBlockOnOpen(true);
				colorWindow.open();
				RGB color = colorWindow.getSelectedRGB();
				if (color != null) {
					setMarkColor(color);
				}
			}
		};
		colorAction.setToolTipText("Change pen color");
		colorIcon = new Image(getShell().getDisplay(), 16, 16);
		setMarkColor(new RGB(255, 85, 85));
		colorAction.setEnabled(false);

		clearAction = new Action("C&lear Annotations", IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				clearAction.setEnabled(false);
				workImageGC.drawImage(originalImage, 0, 0);
				canvas.redraw();
				markAttachmentDirty();
			}
		};
		clearAction.setToolTipText("Clear all annotations made on screenshot image");
		clearAction.setImageDescriptor(TasksUiImages.CLEAR);
		clearAction.setEnabled(false);

		tbm.add(createAndConfigureCI(captureAction));
		tbm.add(createAndConfigureCI(fitAction));
		tbm.add(new Separator());
		tbm.add(createAndConfigureCI(cropAction));
		tbm.add(createAndConfigureCI(markAction));
		tbm.add(createAndConfigureCI(colorAction));
		tbm.add(new Separator());
		tbm.add(createAndConfigureCI(clearAction));

		scrolledComposite = new ScrolledComposite(vf, SWT.V_SCROLL | SWT.H_SCROLL);
		canvas = new Canvas(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(canvas);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				if (workImage != null) {
					Rectangle imageBounds = workImage.getBounds();
					Rectangle canvasBounds = canvas.getClientArea();

					if (fitAction.isChecked()) {
						e.gc.drawImage(workImage, 0, 0, imageBounds.width, imageBounds.height, 0, 0,
								canvasBounds.width, canvasBounds.height);
					} else {
						e.gc.drawImage(workImage, 0, 0);
					}
					drawSelection(e.gc);
				} else {
//					page.setErrorMessage("Screenshot required");
					fitAction.setEnabled(false);
				}
			}
		});

		scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				if (fitAction.isChecked()) {
					refreshCanvasSize();
				}
			}
		});

		vf.setTopLeft(tbm.createControl(vf));
		vf.setContent(scrolledComposite);
		registerMouseListeners();
	}

	private ActionContributionItem createAndConfigureCI(IAction action) {
		ActionContributionItem ci = new ActionContributionItem(action);
		ci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return ci;
	}

	private void setMarkColor(RGB color) {
		if (markColor != null) {
			markColor.dispose();
		}
		markColor = new Color(getShell().getDisplay(), color);
		if (workImageGC != null) {
			workImageGC.setForeground(markColor);
		}

		GC colorGC = new GC(colorIcon);
		colorGC.setBackground(markColor);
		colorGC.fillRectangle(0, 0, 16, 16);
		colorGC.drawRectangle(0, 0, 15, 15);
		colorGC.dispose();

		colorAction.setImageDescriptor(ImageDescriptor.createFromImage(colorIcon));
	}

	@Override
	public void dispose() {
		disposeImageResources();
		if (markColor != null) {
			markColor.dispose();
		}
		if (colorIcon != null) {
			colorIcon.dispose();
		}

		canvas.setCursor(null);
		for (Cursor cursor : cursors.values()) {
			cursor.dispose();
		}
		super.dispose();
	}

	private void disposeImageResources() {
		if (originalImage != null) {
			originalImage.dispose();
		}
		if (workImageGC != null) {
			workImageGC.dispose();
		}
		if (workImage != null) {
			workImage.dispose();
		}
	}

	private static final int CURSOR_MARK_TOOL = -1;

	private void allocateCursors() {
		Display display = getShell().getDisplay();
		cursors.put(SWT.CURSOR_ARROW, new Cursor(display, SWT.CURSOR_ARROW));
		cursors.put(SWT.CURSOR_SIZEALL, new Cursor(display, SWT.CURSOR_SIZEALL));
		cursors.put(SWT.CURSOR_SIZENWSE, new Cursor(display, SWT.CURSOR_SIZENWSE));
		cursors.put(SWT.CURSOR_SIZENESW, new Cursor(display, SWT.CURSOR_SIZENESW));
		cursors.put(SWT.CURSOR_SIZENS, new Cursor(display, SWT.CURSOR_SIZENS));
		cursors.put(SWT.CURSOR_SIZEWE, new Cursor(display, SWT.CURSOR_SIZEWE));
		cursors.put(SWT.CURSOR_CROSS, new Cursor(display, SWT.CURSOR_CROSS));

		// TODO: allocate custom cursor for "mark" tool
		cursors.put(CURSOR_MARK_TOOL, new Cursor(display, SWT.CURSOR_HAND));
	}

	private Rectangle getScaledSelection() {
		if (currentSelection == null) {
			return null;
		}
		int x = (int) Math.round(currentSelection.x * scaleFactor);
		int y = (int) Math.round(currentSelection.y * scaleFactor);
		int right = (int) Math.round((currentSelection.x + currentSelection.width) * scaleFactor);
		int bottom = (int) Math.round((currentSelection.y + currentSelection.height) * scaleFactor);
		int width = Math.min(right, (int) Math.round((workImage.getBounds().width - 1) * scaleFactor)) - x;
		int height = Math.min(bottom, (int) Math.round((workImage.getBounds().height - 1) * scaleFactor)) - y;
		return new Rectangle(x, y, width, height);
	}

	@Override
	public boolean isPageComplete() {
		return workImage != null;
	}

	@Override
	public IWizardPage getNextPage() {
		NewAttachmentPage page = (NewAttachmentPage) getWizard().getPage("AttachmentDetails");
		page.setFilePath(InputAttachmentSourcePage.SCREENSHOT_LABEL);
		page.setContentType();
		return page;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private void captureScreenshotContent() {
		Display display = getShell().getDisplay();
		Shell wizardShell = getWizard().getContainer().getShell();
		wizardShell.setVisible(false);

		// NOTE: need a wait since the shell can take time to disappear (e.g. fade on Vista)
		try {
			Thread.sleep(400);
		} catch (InterruptedException e) {
			// ignore
		}

		disposeImageResources();
		Rectangle displayBounds = display.getBounds();
		originalImage = new Image(display, displayBounds);
		workImage = new Image(display, displayBounds);

		GC gc = new GC(display);
		gc.copyArea(originalImage, 0, 0);
		gc.copyArea(workImage, 0, 0);
		gc.dispose();

		workImageGC = new GC(workImage);
		workImageGC.setForeground(markColor);
		workImageGC.setLineWidth(4);
		workImageGC.setLineCap(SWT.CAP_ROUND);

		clearSelection();
		refreshCanvasSize();

		wizardShell.setVisible(true);
		setPageComplete(true);
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
		Rectangle imageBounds = workImage.getBounds();
		imageBounds.width--;
		imageBounds.height--;
		currentSelection.intersect(imageBounds);
	}

	/**
	 * Create the grab points to resize the selection; this method should be called every time the selection or zoom
	 * level is changed
	 */
	private void setUpGrabPoints() {
		grabPoints.clear();
		if (currentSelection == null) {
			return;
		}

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
		Rectangle imageBounds = workImage.getBounds();

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

		setUpGrabPoints();
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
		Rectangle imageBounds = workImage.getBounds();
		if (newX + originalSelection.width - 1 > imageBounds.width) {
			newX = imageBounds.width - originalSelection.width;
		}
		if (newY + originalSelection.height - 1 > imageBounds.height) {
			newY = imageBounds.height - originalSelection.height;
		}
		currentSelection = new Rectangle(newX, newY, originalSelection.width, originalSelection.height);

		setUpGrabPoints();
	}

	private void registerMouseListeners() {
		canvas.addMouseMoveListener(new MouseMoveListener() {

			/**
			 * If a selection is in course, moving the mouse around refreshes the selection rectangle
			 */
			public void mouseMove(MouseEvent e) {
				int scaledX = (int) Math.round(e.x / scaleFactor);
				int scaledY = (int) Math.round(e.y / scaleFactor);

				if (currentAction == EditorAction.SELECTING) {
					refreshCurrentSelection(scaledX, scaledY);
					canvas.redraw();
				} else if (currentAction == EditorAction.RESIZING_SELECTION) {
					refreshSelectionResize(scaledX, scaledY);
					canvas.redraw();
				} else if (currentAction == EditorAction.MOVING_SELECTION) {
					refreshSelectionPosition(scaledX, scaledY);
					canvas.redraw();
				} else if (currentAction == EditorAction.CROPPING && currentSelection != null) {
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

					// If I'm out, the default cursor for cropping mode is cross
					Cursor crossCursor = cursors.get(SWT.CURSOR_CROSS);
					if (!cursorSet && canvas.getCursor() != crossCursor) {
						canvas.setCursor(crossCursor);
					}
				} else if (currentAction == EditorAction.MARKING) {
					drawMarkLine(scaledX, scaledY);

					Cursor markCursor = cursors.get(CURSOR_MARK_TOOL);
					if (canvas.getCursor() != markCursor) {
						canvas.setCursor(markCursor);
					}
				}
			}

		});

		canvas.addMouseListener(new MouseAdapter() {

			/**
			 * Releasing the mouse button ends the selection or a drawing; compute the selection rectangle and redraw
			 * the cropped image
			 */
			public void mouseUp(MouseEvent e) {
				if (currentAction == EditorAction.SELECTING || currentAction == EditorAction.RESIZING_SELECTION
						|| currentAction == EditorAction.MOVING_SELECTION) {
					int scaledX = (int) Math.round(e.x / scaleFactor);
					int scaledY = (int) Math.round(e.y / scaleFactor);
					if (currentAction == EditorAction.SELECTING) {
						refreshCurrentSelection(scaledX, scaledY);
					} else if (currentAction == EditorAction.RESIZING_SELECTION) {
						refreshSelectionResize(scaledX, scaledY);
					} else if (currentAction == EditorAction.MOVING_SELECTION) {
						refreshSelectionPosition(scaledX, scaledY);
					}
					if (currentSelection.width == 0 && currentSelection.height == 0) {
						currentSelection = null;
					}
					setUpGrabPoints();
					startPoint = null;
					currentAction = EditorAction.CROPPING;

					canvas.redraw();
					markAttachmentDirty();
				} else if (currentAction == EditorAction.MARKING) {
					startPoint = null;
					markAttachmentDirty();
				}
			}

			/**
			 * Pressing mouse button starts a selection or a drawing; normalizes and marks the start point
			 */
			public void mouseDown(MouseEvent e) {
				int scaledX = (int) (e.x / scaleFactor);
				int scaledY = (int) (e.y / scaleFactor);

				if (currentAction == EditorAction.MARKING) {
					startPoint = new Point(scaledX, scaledY);
					drawMarkLine(scaledX, scaledY);
					canvas.setCursor(cursors.get(CURSOR_MARK_TOOL));
					return;
				} else if (currentAction != EditorAction.CROPPING) {
					return;
				}

				// Check the most appropriate action to follow; first check if I'm on some grab point
				if (currentSelection != null) {
					for (GrabPoint point : grabPoints) {
						if (point.grabArea.contains(e.x, e.y)) {
							originalSelection = currentSelection;
							currentAction = EditorAction.RESIZING_SELECTION;
							resizableSides = point.resizableSides;
							startPoint = new Point(scaledX, scaledY);
							canvas.redraw();
							return;
						}
					}
				}

				// Check if I could move the selection
				if (currentSelection != null && currentSelection.contains(scaledX, scaledY)) {
					originalSelection = currentSelection;
					currentAction = EditorAction.MOVING_SELECTION;
					startPoint = new Point(scaledX, scaledY);
					canvas.redraw();
					return;
				}

				// Do a simple selection
				canvas.setCursor(cursors.get(SWT.CURSOR_CROSS));
				currentAction = EditorAction.SELECTING;
				currentSelection = null;
				startPoint = new Point(scaledX, scaledY);
				setUpGrabPoints();
				canvas.redraw();
			}

		});

	}

	private void clearSelection() {
		currentSelection = null;
		startPoint = null;
		markAttachmentDirty();
	}

	/**
	 * Recalculates image canvas size based on "fit on canvas" setting, set up the grab points, and redraws
	 * <p>
	 * This method should be called whenever the {@link #workImage image} <strong>visible</strong> size is changed,
	 * which can happen when:
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
		if (fitAction.isChecked()) {
			// This little hack is necessary to get the client area without scrollbars; 
			// they'll be automatically restored if necessary after Canvas.setBounds()
			scrolledComposite.getHorizontalBar().setVisible(false);
			scrolledComposite.getVerticalBar().setVisible(false);

			Rectangle bounds = scrolledComposite.getClientArea();
			if (workImage != null) {
				Rectangle imageBounds = workImage.getBounds();
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
			if (workImage != null) {
				Rectangle imageBounds = workImage.getBounds();
				bounds.width = imageBounds.width;
				bounds.height = imageBounds.height;
			}
			canvas.setBounds(bounds);
		}
		setUpGrabPoints();
		canvas.redraw();
	}

	/**
	 * Decorates the screenshot canvas with the selection rectangle, resize grab points and other adornments
	 */
	private void drawSelection(GC gc) {
		if (currentSelection == null) {
			return;
		}
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
		gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawRectangle(scaledSelection);

		// Draw grab points
		gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
		for (GrabPoint point : grabPoints) {
			gc.fillRectangle(point.grabArea);
			gc.drawRectangle(point.grabArea);
		}
	}

	/**
	 * Connects the previous mark point to the new reference point, by drawing a new line
	 */
	private void drawMarkLine(int x, int y) {
		if (startPoint != null) {
			clearAction.setEnabled(true);
			workImageGC.drawLine(startPoint.x, startPoint.y, x, y);
			startPoint.x = x;
			startPoint.y = y;
			canvas.redraw();
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

	/**
	 * Creates the final screenshot
	 * 
	 * @return The final screenshot, with all markings, and cropped according to user settings; <strong>The caller is
	 *         responsible for disposing the returned image</strong>
	 */
	public Image createImage() {
		Image screenshot = new Image(getShell().getDisplay(), currentSelection != null ? currentSelection
				: workImage.getBounds());

		GC gc = new GC(screenshot);
		if (currentSelection != null) {
			gc.drawImage(workImage, currentSelection.x, currentSelection.y, currentSelection.width,
					currentSelection.height, 0, 0, currentSelection.width, currentSelection.height);
		} else {
			gc.drawImage(workImage, 0, 0);
		}
		gc.dispose();

		return screenshot;
	}

	private void markAttachmentDirty() {
		NewAttachmentWizard wizard = (NewAttachmentWizard) getWizard();
		((ImageAttachment) wizard.getAttachment()).markDirty();
	}

}
