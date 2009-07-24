/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Balazs Brinkus - initial API and implementation
 *     Tasktop Technologies - improvements
 *     Willian Mitsuda - improvements
 *     Hiroyuki Inaba - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.provisional.commons.ui;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.commons.ui.Messages;
import org.eclipse.mylyn.internal.commons.ui.SelectToolAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

/**
 * A wizard page to create a screenshot from the display.
 * <p>
 * NOTE: this class exposes a lot of implementation detail and is likely to change.
 * 
 * @author Balazs Brinkus
 * @author Willian Mitsuda
 * @author Mik Kersten
 * @author Hiroyuki Inaba
 */
public class ScreenshotCreationPage extends WizardPage {

	private SelectToolAction captureAction;

	private SelectToolAction fitAction;

	private IAction cropAction;

	private IAction markAction;

	private IAction clearAction;

	private IAction undoAction;

	private IAction redoAction;

	private Composite paletteArea;

	private int lastDrawAction;

	private SelectToolAction drawLineToolbar;

	private SelectToolAction drawArrowToolbar;

	private SelectToolAction drawBoxToolbar;

	private SelectToolAction drawTextToolbar;

	private SelectToolAction lineTypeToolbar;

	private SelectToolAction lineBoldToolbar;

	private SelectToolAction drawColorToolbar;

	private boolean imageDirty;

	/**
	 * Original screenshot image; used for backup purposes
	 */
	private Image originalImage;

	/**
	 * Copy of {@link #originalImage original} image; all drawing operations are done here; base for the result image
	 */
	private Image workImage;

	private Image previousImage;

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
	private final Map<Integer, Cursor> cursors = new HashMap<Integer, Cursor>();

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

	private boolean isFirstCapture = true;

	private Text textArea;

	/**
	 * Mouse event history. Entry is [0] MouseDown/MouseMove/MouseUp, [1] x, [2] y
	 */
	private List<int[]> historyMouseEvent = new ArrayList<int[]>();

	/**
	 * Draw tool history. Entry is [0] drawHistory index, [1] FREE/LINE/BOX/OVAL, [2] Line type, [3] Bold, [4] R/G/B
	 */
	private List<int[]> historyDrawTool = new ArrayList<int[]>();

	private List<StringBuffer> historyDrawText = new ArrayList<StringBuffer>();

	private List<String> historyDrawFont = new ArrayList<String>();

	private int historyCheckpoint = 0;

	public ScreenshotCreationPage() {
		super("ScreenShotAttachment"); //$NON-NLS-1$
		setTitle(Messages.ScreenshotCreationPage_CAPTURE_SCRRENSHOT);
		setDescription(Messages.ScreenshotCreationPage_After_capturing
				+ Messages.ScreenshotCreationPage_NOTE_THAT_YOU_CONTINUTE);
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

		captureAction = new SelectToolAction(getShell(), Messages.ScreenshotCreationPage_Capture_Desktop_C,
				SelectToolAction.CAPTURE_DROP_DOWN_MENU) {

			@Override
			public void run() {
				if (captureAction.getSelect() == SelectToolAction.CAPTURE_DESKTOP) {
					captureScreenshotContent();
				} else if (captureAction.getSelect() == SelectToolAction.CAPTURE_CLIPBOARD) {
					captureScreenshotContent(captureAction.getClipboardImage());
				} else if (captureAction.getSelect() == SelectToolAction.CAPTURE_RECTANGLE) {
					captureScreenshotContentFromSelection();
				} else {
					captureScreenshotContent(captureAction.getFileImage());
				}
				setErrorMessage(null);
				if (isFirstCapture) {
					isFirstCapture = false;
					fitAction.setEnabled(true);
					cropAction.setEnabled(true);
					cropAction.setChecked(true);
					markAction.setEnabled(true);
					drawLineToolbar.setEnabled(true);
					drawArrowToolbar.setEnabled(true);
					drawBoxToolbar.setEnabled(true);
					drawTextToolbar.setEnabled(true);
				}

				historyMouseEvent = new ArrayList<int[]>();
				historyDrawTool = new ArrayList<int[]>();
				historyDrawText = new ArrayList<StringBuffer>();
				historyDrawFont = new ArrayList<String>();
				historyCheckpoint = 0;
				undoAction.setEnabled(false);
				redoAction.setEnabled(false);
				clearAction.setEnabled(false);
			}

			@Override
			protected boolean isEnableRectangle() {
				return (currentSelection != null);
			}
		};
		captureAction.setToolTipText(Messages.ScreenshotCreationPage_Capture_Desktop);
		captureAction.setImageDescriptor(ImageDescriptor.createFromImage(CommonImages.getImage(CommonImages.IMAGE_CAPTURE)));
		captureAction.setShowMenuAlways(false);

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

		fitAction = new SelectToolAction(Messages.ScreenshotCreationPage_Fit_Image_F,
				SelectToolAction.ZOOM_DROP_DOWN_MENU) {
			@Override
			public void run() {
				refreshCanvasSize();
			}
		};
		fitAction.setToolTipText(Messages.ScreenshotCreationPage_Fit_Image);
		fitAction.setImageDescriptor(ImageDescriptor.createFromImage(CommonImages.getImage(CommonImages.IMAGE_FIT)));
		//fitAction.setChecked(true);
		fitAction.setEnabled(false);

		cropAction = new Action(Messages.ScreenshotCreationPage_Crop_R, IAction.AS_RADIO_BUTTON) {
			@Override
			public void run() {
				currentAction = EditorAction.CROPPING;
				cropAction.setChecked(true);
				markAction.setChecked(false);
//				undoAction.setEnabled(false);
//				redoAction.setEnabled(false);
				canvas.redraw();
			}
		};
		cropAction.setToolTipText(Messages.ScreenshotCreationPage_Crop);
		cropAction.setImageDescriptor(CommonImages.CUT);
		cropAction.setEnabled(false);

		markAction = new Action(Messages.ScreenshotCreationPage_Annotate, IAction.AS_RADIO_BUTTON) {
			@Override
			public void setChecked(boolean checked) {
				super.setChecked(checked);
				if (paletteArea != null) {
					if (checked) {
						if (getSelectDrawToolbar() < 0) {
							setSelectDrawToolbar(lastDrawAction);
						}
					} else {
						int select = getSelectDrawToolbar();
						if (select >= 0) {
							lastDrawAction = select;
							unselectDrawToolbar();
						}
					}
					boolean isDrawText = (drawTextToolbar.getSelect() >= 0) ? false : checked;
					//drawLineToolbar.setEnabled(checked);
					//drawArrowToolbar.setEnabled(checked);
					//drawBoxToolbar.setEnabled(checked);
					//drawTextToolbar.setEnabled(checked);
					drawColorToolbar.setEnabled(isDrawText);
					lineTypeToolbar.setEnabled(isDrawText);
					lineBoldToolbar.setEnabled(isDrawText);
				}
			}

			@Override
			public void run() {
				currentAction = EditorAction.MARKING;
				cropAction.setChecked(false);
				markAction.setChecked(true);
//				undoAction.setEnabled(false);
//				redoAction.setEnabled(false);
				canvas.redraw();
			}
		};
		markAction.setToolTipText(Messages.ScreenshotCreationPage_DRAW_ANNOTATION_ON_SCREENSHOT_IMAGE);
		markAction.setImageDescriptor(CommonImages.EDIT);
//		markAction.setDisabledImageDescriptor(ImageDescriptor.createFromFile(getClass(), "mark_disabled.gif"));
		markAction.setEnabled(false);

		clearAction = new Action(Messages.ScreenshotCreationPage_Clear, IAction.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				clearAction.setEnabled(false);
				workImageGC.drawImage(originalImage, 0, 0);
				canvas.redraw();
				setImageDirty(true);

				historyMouseEvent = new ArrayList<int[]>();
				historyDrawTool = new ArrayList<int[]>();
				historyDrawText = new ArrayList<StringBuffer>();
				historyDrawFont = new ArrayList<String>();
				historyCheckpoint = 0;
				undoAction.setEnabled(false);
				redoAction.setEnabled(false);
			}
		};
		clearAction.setToolTipText(Messages.ScreenshotCreationPage_Clear_all_annotations_made_on_screenshot_image);
		clearAction.setImageDescriptor(CommonImages.CLEAR);
		clearAction.setEnabled(false);

		undoAction = new Action(Messages.ScreenshotCreationPage_Undo) {
			@Override
			public void run() {
				if (historyCheckpoint > 0) {
					historyCheckpoint--;
					drawAnnotationHistory();
				}
				if (historyCheckpoint == 0) {
					undoAction.setEnabled(false);
				}
				if (historyCheckpoint < historyDrawTool.size()) {
					redoAction.setEnabled(true);
				}
			}
		};
		undoAction.setToolTipText(Messages.ScreenshotCreationPage_Undo_annotation);
		undoAction.setImageDescriptor(CommonImages.UNDO);
		undoAction.setEnabled(false);

		redoAction = new Action(Messages.ScreenshotCreationPage_Redo) {
			@Override
			public void run() {
				if (historyCheckpoint < historyDrawTool.size()) {
					historyCheckpoint++;
					drawAnnotationHistory();
				}
				if (historyCheckpoint > 0) {
					undoAction.setEnabled(true);
				}
				if (historyCheckpoint >= historyDrawTool.size()) {
					redoAction.setEnabled(false);
				}
			}
		};
		redoAction.setToolTipText(Messages.ScreenshotCreationPage_Redo_annotation);
		redoAction.setImageDescriptor(CommonImages.REDO);
		redoAction.setEnabled(false);

		tbm.add(createAndConfigureCI(captureAction));
		tbm.add(new Separator());
		tbm.add(createAndConfigureCI(fitAction));
		tbm.add(createAndConfigureCI(cropAction));
		tbm.add(createAndConfigureCI(markAction));
		tbm.add(new Separator());
		tbm.add(createAndConfigureCI(clearAction));
		tbm.add(createAndConfigureCI(undoAction));
		tbm.add(createAndConfigureCI(redoAction));
		tbm.add(new Separator());

		Composite body = new Composite(vf, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		createPaletteBars(body);
		lastDrawAction = getSelectDrawToolbar();
		unselectDrawToolbar();

		scrolledComposite = new ScrolledComposite(body, SWT.V_SCROLL | SWT.H_SCROLL);
		scrolledComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		canvas = new Canvas(scrolledComposite, SWT.DOUBLE_BUFFERED);
		scrolledComposite.setContent(canvas);
		canvas.addPaintListener(new PaintListener() {

			public void paintControl(PaintEvent e) {
				if (workImage != null) {
					Rectangle imageBounds = workImage.getBounds();
					Rectangle canvasBounds = canvas.getClientArea();

					int zoom = fitAction.getSelect();
					switch (zoom) {
					case SelectToolAction.ZOOM_FIT:
						e.gc.drawImage(workImage, 0, 0, imageBounds.width, imageBounds.height, //
								0, 0, canvasBounds.width, canvasBounds.height);
						break;
					case 50:
						e.gc.drawImage(workImage, 0, 0, imageBounds.width, imageBounds.height, //
								0, 0, imageBounds.width / 2, imageBounds.height / 2);
						break;
					case 100:
						e.gc.drawImage(workImage, 0, 0);
						break;
					default:
						e.gc.drawImage(workImage, 0, 0, imageBounds.width, imageBounds.height, //
								0, 0, imageBounds.width * zoom / 100, imageBounds.height * zoom / 100);
						break;
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
				if (fitAction.getSelect() == SelectToolAction.ZOOM_FIT) {
					refreshCanvasSize();
				}
			}
		});
		scrolledComposite.setEnabled(false);

		vf.setTopLeft(tbm.createControl(vf));
		vf.setContent(body);
		registerMouseListeners();

		vf.setLayoutData(new GridData(GridData.FILL_BOTH));

		Dialog.applyDialogFont(vf);
	}

	private void createPaletteBars(Composite body) {
		paletteArea = new Composite(body, SWT.NONE);
		paletteArea.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		RowLayout rowlayout = new RowLayout(SWT.VERTICAL);
		rowlayout.marginRight += 1;
		paletteArea.setLayout(rowlayout);

		paletteArea.addListener(SWT.Paint, new Listener() {
			public void handleEvent(Event e) {
				Color gcForeground = e.gc.getForeground();
				Rectangle bounds = ((Composite) e.widget).getBounds();
				Color border = e.widget.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
				e.gc.setForeground(border);
				e.gc.drawLine(bounds.width - 1, 0, bounds.width - 1, bounds.height);
				e.gc.setForeground(gcForeground);
			}
		});
		paletteArea.addMenuDetectListener(new MenuDetectListener() {

			public void menuDetected(MenuDetectEvent e) {
				Menu rightClickMenu = new Menu(Display.getDefault().getActiveShell(), SWT.POP_UP);
				MenuItem menuItem = new MenuItem(rightClickMenu, SWT.CHECK);
				menuItem.setText(Messages.ScreenshotCreationPage_Show_Line_Type_Selector);
				menuItem.setSelection(lineTypeToolbar.getVisible());
				menuItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(final Event event) {
						lineTypeToolbar.setVisible(!lineTypeToolbar.getVisible());
						paletteArea.layout();
					}
				});
				menuItem = new MenuItem(rightClickMenu, SWT.CHECK);
				menuItem.setText(Messages.ScreenshotCreationPage_Show_Line_Bold_Selector);
				menuItem.setSelection(lineBoldToolbar.getVisible());
				menuItem.addListener(SWT.Selection, new Listener() {
					public void handleEvent(final Event event) {
						lineBoldToolbar.setVisible(!lineBoldToolbar.getVisible());
						paletteArea.layout();
					}
				});
				rightClickMenu.setLocation(e.x, e.y);
				rightClickMenu.setVisible(true);
			}
		});
		drawLineToolbar = new SelectToolAction(paletteArea, SelectToolAction.DRAWLINE_TOOLBAR) {

			@Override
			public void run() {
				markAction.run();
				drawArrowToolbar.setUnselect();
				drawBoxToolbar.setUnselect();
				drawTextToolbar.setUnselect();
				drawColorToolbar.setEnabled(true);
				lineTypeToolbar.setEnabled(true);
				lineBoldToolbar.setEnabled(true);
			}
		};
		drawLineToolbar.setEnabled(false);
		drawArrowToolbar = new SelectToolAction(paletteArea, SelectToolAction.DRAWARROW_TOOLBAR) {

			@Override
			public void run() {
				markAction.run();
				drawLineToolbar.setUnselect();
				drawBoxToolbar.setUnselect();
				drawTextToolbar.setUnselect();
				drawColorToolbar.setEnabled(true);
				lineTypeToolbar.setEnabled(true);
				lineBoldToolbar.setEnabled(true);
			}
		};
		drawArrowToolbar.setEnabled(false);
		drawBoxToolbar = new SelectToolAction(paletteArea, SelectToolAction.DRAWBOX_TOOLBAR) {

			@Override
			public void run() {
				markAction.run();
				drawLineToolbar.setUnselect();
				drawArrowToolbar.setUnselect();
				drawTextToolbar.setUnselect();
				drawColorToolbar.setEnabled(true);
				lineTypeToolbar.setEnabled(true);
				lineBoldToolbar.setEnabled(true);
			}
		};
		drawBoxToolbar.setEnabled(false);
		drawTextToolbar = new SelectToolAction(paletteArea, SelectToolAction.DRAWTEXT_TOOLBAR) {

			@Override
			public void run() {
				markAction.run();
				drawLineToolbar.setUnselect();
				drawArrowToolbar.setUnselect();
				drawBoxToolbar.setUnselect();
				drawColorToolbar.setEnabled(false);
				lineTypeToolbar.setEnabled(false);
				lineBoldToolbar.setEnabled(false);
			}
		};
		drawTextToolbar.setEnabled(false);
		drawColorToolbar = new SelectToolAction(paletteArea, SelectToolAction.COLOR_TOOLBAR);
		drawColorToolbar.setEnabled(false);
		lineTypeToolbar = new SelectToolAction(paletteArea, SelectToolAction.LINETYPE_TOOLBAR);
		lineTypeToolbar.setEnabled(false);
		lineTypeToolbar.setVisible(false);
		lineBoldToolbar = new SelectToolAction(paletteArea, SelectToolAction.LINEBOLD_TOOLBAR);
		lineBoldToolbar.setEnabled(false);
		lineBoldToolbar.setVisible(false);
	}

	private void setSelectDrawToolbar(int drawTool) {
		if (drawLineToolbar.setSelect(drawTool)) {
			drawArrowToolbar.setUnselect();
			drawBoxToolbar.setUnselect();
			drawTextToolbar.setUnselect();
			return;
		}
		if (drawArrowToolbar.setSelect(drawTool)) {
			drawLineToolbar.setUnselect();
			drawBoxToolbar.setUnselect();
			drawTextToolbar.setUnselect();
			return;
		}
		if (drawBoxToolbar.setSelect(drawTool)) {
			drawLineToolbar.setUnselect();
			drawArrowToolbar.setUnselect();
			drawTextToolbar.setUnselect();
			return;
		}
		drawLineToolbar.setUnselect();
		drawArrowToolbar.setUnselect();
		drawBoxToolbar.setUnselect();
		drawTextToolbar.setSelect(drawTool);
	}

	private void unselectDrawToolbar() {
		drawLineToolbar.setUnselect();
		drawArrowToolbar.setUnselect();
		drawBoxToolbar.setUnselect();
		drawTextToolbar.setUnselect();
	}

	private int getSelectDrawToolbar() {
		int drawTool;
		if ((drawTool = drawLineToolbar.getSelect()) >= 0) {
			return drawTool;
		}
		if ((drawTool = drawArrowToolbar.getSelect()) >= 0) {
			return drawTool;
		}
		if ((drawTool = drawBoxToolbar.getSelect()) >= 0) {
			return drawTool;
		}
		if ((drawTool = drawTextToolbar.getSelect()) >= 0) {
			return drawTool;
		}
		return -1;
	}

	private ActionContributionItem createAndConfigureCI(IAction action) {
		ActionContributionItem ci = new ActionContributionItem(action);
		ci.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		return ci;
	}

	@Override
	public void dispose() {
		disposeImageResources();

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

	private static final long CAPTURE_DELAY = 400;

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
		int width = Math.min(right, (int) Math.round(workImage.getBounds().width * scaleFactor)) - x;
		int height = Math.min(bottom, (int) Math.round(workImage.getBounds().height * scaleFactor)) - y;
		return new Rectangle(x, y, width, height);
	}

	private Rectangle getOutsideSelection(Rectangle rectangle) {
		if (rectangle == null) {
			return null;
		}
		return new Rectangle(rectangle.x - SQUARE_SIZE * 2, rectangle.y - SQUARE_SIZE * 2, //
				rectangle.width + SQUARE_SIZE * 4 + 1, rectangle.height + SQUARE_SIZE * 4 + 1);
	}

	private static final int[][] grapGroupPoints = { //
	/*    */{ 0, 0, 0 }, { 1, 0, 0 }, { 2, 0, 1 }, { 3, 0, 2 }, { 4, 0, 2 }, //
			{ 0, 1, 0 }, /*         *//*         *//*         */{ 4, 1, 2 }, //
			{ 0, 2, 3 }, /*         *//*         *//*         */{ 4, 2, 4 }, //
			{ 0, 3, 5 }, /*         *//*         *//*         */{ 4, 3, 7 }, //
			{ 0, 4, 5 }, { 1, 4, 5 }, { 2, 4, 6 }, { 3, 4, 7 }, { 4, 4, 7 } };

	private static final int[] grapScanOrder = { 0, 4, 1, 3, 2 };

	private int getGrabPoint(int x, int y) {
		if (currentSelection == null) {
			return -1;
		}

		Rectangle inside = getScaledSelection();
		Rectangle outside = getOutsideSelection(inside);
		int[] xGroupPoint = { outside.x, //
				inside.x, //
				inside.x + SQUARE_SIZE * 4, //
				inside.x + inside.width - SQUARE_SIZE * 4, //
				inside.x + inside.width, //
				outside.x + outside.width };
		int[] yGroupPoint = { outside.y, //
				inside.y, //
				inside.y + SQUARE_SIZE * 4, //
				inside.y + inside.height - SQUARE_SIZE * 4, //
				inside.y + inside.height, //
				outside.y + outside.height };
		int xGroup = -1, yGroup = -1;
		for (int element : grapScanOrder) {
			if (xGroupPoint[element] <= x && x <= xGroupPoint[element + 1]) {
				xGroup = element;
				break;
			}
		}
		if (xGroup < 0) {
			return -1;
		}
		for (int element : grapScanOrder) {
			if (yGroupPoint[element] <= y && y <= yGroupPoint[element + 1]) {
				yGroup = element;
				break;
			}
		}
		if (yGroup < 0) {
			return -1;
		}
		for (int[] element : grapGroupPoints) {
			if (element[0] == xGroup && element[1] == yGroup) {
				return element[2];
			}
		}
		return -1;
	}

	@Override
	public boolean isPageComplete() {
		return workImage != null;
	}

	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}

	private void captureScreenshotContent(Image image) {
		final Display display = getShell().getDisplay();
		disposeImageResources();
		originalImage = image;
		Rectangle displayBounds = originalImage.getBounds();
		workImage = new Image(display, displayBounds.width, displayBounds.height);
		GC gc = new GC(workImage);
		gc.drawImage(originalImage, 0, 0);
		gc.dispose();

		workImageGC = new GC(workImage);
		workImageGC.setLineCap(SWT.CAP_ROUND);

		scrolledComposite.setEnabled(true);
		clearSelection();
		refreshCanvasSize();

		setPageComplete(true);
	}

	private void captureScreenshotContentFromSelection() {
		Display display = getShell().getDisplay();

		Image image = new Image(display, currentSelection);
		GC gc = new GC(image);
		gc.drawImage(workImage, currentSelection.x, currentSelection.y, currentSelection.width,
				currentSelection.height, 0, 0, currentSelection.width, currentSelection.height);
		gc.dispose();
		disposeImageResources();

		originalImage = image;
		Rectangle displayBounds = originalImage.getBounds();
		workImage = new Image(display, displayBounds.width, displayBounds.height);
		gc = new GC(workImage);
		gc.drawImage(originalImage, 0, 0);
		gc.dispose();

		workImageGC = new GC(workImage);
		workImageGC.setLineCap(SWT.CAP_ROUND);

		scrolledComposite.setEnabled(true);
		clearSelection();
		refreshCanvasSize();

		setPageComplete(true);
	}

	private void captureScreenshotContent() {
		final Display display = getShell().getDisplay();
		final Shell wizardShell = getWizard().getContainer().getShell();
		wizardShell.setVisible(false);

		// this code needs to run asynchronously to allow the workbench to refresh before the screen is captured  
		UIJob job = new UIJob("Capturing Screenshot") { //$NON-NLS-1$

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				disposeImageResources();
				Rectangle displayBounds = display.getBounds();
				originalImage = new Image(display, displayBounds.width, displayBounds.height);
				workImage = new Image(display, displayBounds.width, displayBounds.height);

				GC gc = new GC(display);
				gc.copyArea(originalImage, displayBounds.x, displayBounds.y);
				gc.copyArea(workImage, displayBounds.x, displayBounds.y);
				gc.dispose();

				workImageGC = new GC(workImage);
				workImageGC.setLineCap(SWT.CAP_ROUND);

				scrolledComposite.setEnabled(true);
				clearSelection();
				refreshCanvasSize();

				wizardShell.setVisible(true);
				setPageComplete(true);

				return Status.OK_STATUS;
			}
		};
		// NOTE: need a wait since the shell can take time to disappear (e.g. fade on Vista)
		job.schedule(CAPTURE_DELAY);
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

	private static final int grabPointCurosr[] = { SWT.CURSOR_SIZENWSE, SWT.CURSOR_SIZENS, SWT.CURSOR_SIZENESW,
			SWT.CURSOR_SIZEWE, SWT.CURSOR_SIZEWE, SWT.CURSOR_SIZENESW, SWT.CURSOR_SIZENS, SWT.CURSOR_SIZENWSE };

	private static final SelectionSide[][] grabPointResizableSides = { { SelectionSide.LEFT, SelectionSide.TOP },
			{ SelectionSide.TOP }, { SelectionSide.TOP, SelectionSide.RIGHT }, { SelectionSide.LEFT },
			{ SelectionSide.RIGHT }, { SelectionSide.LEFT, SelectionSide.BOTTOM }, { SelectionSide.BOTTOM },
			{ SelectionSide.BOTTOM, SelectionSide.RIGHT } };

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
			if (originalSelection.x + originalSelection.width + deltaX > imageBounds.width) {
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
			if (originalSelection.y + originalSelection.height + deltaY > imageBounds.height) {
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
		Rectangle imageBounds = workImage.getBounds();
		if (newX + originalSelection.width > imageBounds.width) {
			newX = imageBounds.width - originalSelection.width;
		}
		if (newY + originalSelection.height > imageBounds.height) {
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
					int info = getGrabPoint(e.x, e.y);
					if (info >= 0) {
						canvas.setCursor(cursors.get(grabPointCurosr[info]));
						cursorSet = true;
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
					if (startPoint != null) {
						int drawTool = getSelectDrawToolbar();
						if (drawTool == SelectToolAction.DRAW_FREE) {
							int[] history = new int[3];
							history[0] = SWT.MouseMove;
							history[1] = scaledX;
							history[2] = scaledY;
							historyMouseEvent.add(history);
						} else {
							int[] history = historyMouseEvent.get(historyMouseEvent.size() - 1);
							if (history[0] == SWT.MouseMove) {
								history[1] = scaledX;
								history[2] = scaledY;
							} else {
								history = new int[3];
								history[0] = SWT.MouseMove;
								history[1] = scaledX;
								history[2] = scaledY;
								historyMouseEvent.add(history);
							}
						}
					}

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
			@Override
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

					startPoint = null;
					currentAction = EditorAction.CROPPING;

					canvas.redraw();
					setImageDirty(true);
				} else if (currentAction == EditorAction.MARKING) {
					if (startPoint != null) {
						int drawTool = getSelectDrawToolbar();
						if (drawTool != SelectToolAction.DRAW_FREE) {
							if (drawTool == SelectToolAction.DRAW_TEXT) {
								drawAnnotationText();
							}
							previousImage.dispose();
							previousImage = null;
						}

						int[] history = new int[3];
						history[0] = SWT.MouseUp;
						history[1] = 0;
						history[2] = 0;
						historyMouseEvent.add(history);
					}

					startPoint = null;
					setImageDirty(true);
				}
			}

			/**
			 * Input annotation text and draw text
			 */
			private void drawAnnotationText() {
				workImageGC.drawImage(previousImage, 0, 0);
				canvas.redraw();

				int[] history = historyMouseEvent.get(historyMouseEvent.size() - 1);
				if (history[0] != SWT.MouseMove) {
					historyCheckpoint--;
					updateAnnotationHistory();
					return;
				}

				int endedPoint_x, endedPoint_y;
				if (history[1] < startPoint.x) {
					endedPoint_x = startPoint.x;
					startPoint.x = history[1];
				} else {
					endedPoint_x = history[1];
				}
				if (history[2] < startPoint.y) {
					endedPoint_y = startPoint.y;
					startPoint.y = history[2];
				} else {
					endedPoint_y = history[2];
				}
				final Rectangle bounds = new Rectangle(startPoint.x, startPoint.y, endedPoint_x - startPoint.x,
						endedPoint_y - startPoint.y);
				textArea = new Text(canvas, SWT.MULTI | SWT.WRAP);
				int xs = (int) Math.round(startPoint.x * scaleFactor);
				int ys = (int) Math.round(startPoint.y * scaleFactor);
				int xe = (int) Math.round(endedPoint_x * scaleFactor);
				int ye = (int) Math.round(endedPoint_y * scaleFactor);
				textArea.setBounds(new Rectangle(xs, ys, xe - xs, ye - ys));
				FontData fontData = new FontData(drawTextToolbar.getStringCustom());
				if (scaleFactor != 1.0) {
					fontData.setHeight((int) Math.round(fontData.getHeight() * scaleFactor));
				}
				textArea.setFont(new Font(getShell().getDisplay(), fontData));
				textArea.setForeground(new Color(getShell().getDisplay(),
						SelectToolAction.int2rgb(drawTextToolbar.getIntgerCustom())));
				textArea.setTabs(1);
				Point point = textArea.getCaretLocation();
				textArea.setBounds(new Rectangle(xs - point.x, ys, xe - xs + point.x + point.x, ye - ys));
				textArea.setFocus();
				textArea.addListener(SWT.Deactivate, new Listener() {

					public void handleEvent(Event event) {
						String text = textArea.getText();
						{
							String newtext = ""; //$NON-NLS-1$
							int currpos = 0;
							int charpos = currpos;
							textArea.setTopIndex(0);
							textArea.setSelection(currpos);
							int linepos = textArea.getCaretLineNumber();
							boolean remove1st = false;
							String line;
							while (currpos < text.length()) {
								int y = textArea.getCaretLineNumber();
								if (linepos != y) {
									line = text.substring(charpos, currpos);
									if (line.endsWith("\n")) { //$NON-NLS-1$
										line = line.substring(0, line.length() - 1);
									}
									newtext = newtext + "\n" + line; //$NON-NLS-1$
									remove1st = true;
									charpos = currpos;
									linepos = y;
								}
								currpos++;
								textArea.setSelection(currpos);
							}
							line = text.substring(charpos, currpos);
							if (line.endsWith("\n")) { //$NON-NLS-1$
								line = line.substring(0, line.length() - 1);
							}
							if (line.length() > 0) {
								newtext = newtext + "\n" + text.substring(charpos, currpos); //$NON-NLS-1$
								remove1st = true;
							}
							currpos = newtext.indexOf("\r"); //$NON-NLS-1$
							while (currpos > 0) {
								newtext = newtext.substring(0, currpos) + newtext.substring(currpos + 1);
								currpos = newtext.indexOf("\r"); //$NON-NLS-1$
							}
							newtext = newtext.replace("\t", " "); //$NON-NLS-1$//$NON-NLS-2$
							if (remove1st) {
								newtext = newtext.substring(1);
							}
							text = newtext;
						}

						textArea.dispose();
						textArea = null;

						if (text.length() > 0) {
							historyDrawText.get(historyCheckpoint - 1).append(text);
							Color color = workImageGC.getForeground();
							FontData fontData = new FontData(drawTextToolbar.getStringCustom());
							workImageGC.setFont(new Font(getShell().getDisplay(), fontData));
							workImageGC.setForeground(new Color(getShell().getDisplay(),
									SelectToolAction.int2rgb(drawTextToolbar.getIntgerCustom())));
							workImageGC.setClipping(bounds);
							workImageGC.drawText(text, bounds.x, bounds.y, true);
							workImageGC.setClipping((Rectangle) null);
							workImageGC.setForeground(color);
						} else {
							historyCheckpoint--;
							updateAnnotationHistory();
						}
						canvas.redraw();
					}
				});
			}

			/**
			 * Pressing mouse button starts a selection or a drawing; normalizes and marks the start point
			 */
			@Override
			public void mouseDown(MouseEvent e) {
				int scaledX = (int) (e.x / scaleFactor);
				int scaledY = (int) (e.y / scaleFactor);

				if (currentAction == EditorAction.MARKING) {
					updateAnnotationHistory();

					int drawTool = getSelectDrawToolbar();
					int[] history = new int[5];
					history[0] = historyMouseEvent.size();
					history[1] = drawTool;
					history[2] = (lineTypeToolbar != null) ? lineTypeToolbar.getSelect() : SWT.LINE_DOT;
					history[3] = (lineBoldToolbar != null) ? lineBoldToolbar.getSelect() : 1;
					RGB rgb;
					if (drawTool == SelectToolAction.DRAW_TEXT) {
						rgb = SelectToolAction.int2rgb(drawTextToolbar.getIntgerCustom());
					} else {
						rgb = SelectToolAction.int2rgb(drawColorToolbar.getSelect());
					}
					history[4] = (rgb.red << 16) + (rgb.green << 8) + rgb.blue;
					historyDrawTool.add(history);
					historyDrawText.add(new StringBuffer());
					if (drawTool == SelectToolAction.DRAW_TEXT) {
						FontData fontData = new FontData(drawTextToolbar.getStringCustom());
						historyDrawFont.add(fontData.toString());
					} else {
						historyDrawFont.add(""); //$NON-NLS-1$
					}
					historyCheckpoint = historyDrawTool.size();

					history = new int[3];
					history[0] = SWT.MouseDown;
					history[1] = scaledX;
					history[2] = scaledY;
					historyMouseEvent.add(history);
					undoAction.setEnabled(true);

					if (drawTool != SelectToolAction.DRAW_FREE) {
						Display display = getShell().getDisplay();
						previousImage = new Image(display, workImage.getBounds());
						GC gc = new GC(previousImage);
						gc.drawImage(workImage, 0, 0);
						gc.dispose();
					}

					if (drawTool != SelectToolAction.DRAW_TEXT) {
						workImageGC.setLineStyle(lineTypeToolbar.getSelect());
						workImageGC.setLineWidth(lineBoldToolbar.getSelect());
						workImageGC.setForeground(new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect())));
					} else {
						workImageGC.setLineStyle(SWT.LINE_DOT);
						workImageGC.setLineWidth(1);
						workImageGC.setForeground(new Color(getShell().getDisplay(), 0, 0, 0));
					}

					startPoint = new Point(scaledX, scaledY);
					drawMarkLine(scaledX, scaledY);
					canvas.setCursor(cursors.get(CURSOR_MARK_TOOL));
					return;
				} else if (currentAction != EditorAction.CROPPING) {
					return;
				}

				// Check the most appropriate action to follow; first check if I'm on some grab point
				if (currentSelection != null) {
					int info = getGrabPoint(e.x, e.y);
					if (info >= 0) {
						originalSelection = currentSelection;
						currentAction = EditorAction.RESIZING_SELECTION;
						resizableSides = new HashSet<SelectionSide>();
						for (SelectionSide side : grabPointResizableSides[info]) {
							resizableSides.add(side);
						}
						startPoint = new Point(scaledX, scaledY);
						canvas.redraw();
						return;
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

				canvas.redraw();
			}
		});
	}

	private void clearSelection() {
		currentSelection = null;
		startPoint = null;
		setImageDirty(true);
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
		if (fitAction.getSelect() == SelectToolAction.ZOOM_FIT) {
			// This little hack is necessary to get the client area without scrollbars; 
			// they'll be automatically restored if necessary after Canvas.setBounds()
			scrolledComposite.getHorizontalBar().setVisible(false);
			scrolledComposite.getVerticalBar().setVisible(false);

			Rectangle bounds = scrolledComposite.getClientArea();
			if (workImage != null) {
				Rectangle imageBounds = workImage.getBounds();
				double xRatio = (double) bounds.width / imageBounds.width;
				double yRatio = (double) bounds.height / imageBounds.height;
				scaleFactor = Math.min(xRatio, yRatio);
				bounds.width = (int) Math.round(imageBounds.width * scaleFactor);
				bounds.height = (int) Math.round(imageBounds.height * scaleFactor);
			}
			canvas.setBounds(bounds);
		} else {
			scaleFactor = fitAction.getSelect(); // 50, 100, 200, 400 or 800
			scaleFactor = scaleFactor / 100;
			Rectangle bounds = scrolledComposite.getClientArea();
			if (workImage != null) {
				Rectangle imageBounds = workImage.getBounds();
				bounds.width = (int) Math.round(imageBounds.width * scaleFactor);
				bounds.height = (int) Math.round(imageBounds.height * scaleFactor);
			}
			canvas.setBounds(bounds);
		}

		canvas.redraw();
	}

	private void updateAnnotationHistory() {
		int[] history;
		if (historyCheckpoint < historyDrawTool.size()) {
			history = historyDrawTool.get(historyCheckpoint);
			while (history[0] < historyMouseEvent.size()) {
				historyMouseEvent.remove(historyMouseEvent.size() - 1);
			}
			while (historyCheckpoint < historyDrawTool.size()) {
				historyDrawTool.remove(historyDrawTool.size() - 1);
			}
			while (historyCheckpoint < historyDrawText.size()) {
				historyDrawText.remove(historyDrawText.size() - 1);
			}
			while (historyCheckpoint < historyDrawFont.size()) {
				historyDrawFont.remove(historyDrawFont.size() - 1);
			}
			redoAction.setEnabled(false);
		}

		undoAction.setEnabled(historyCheckpoint > 0);
	}

	/**
	 * Draw Annotation with history
	 */
	private void drawAnnotationHistory() {
		workImageGC.drawImage(originalImage, 0, 0);
		Color backBackground = workImageGC.getBackground();
		Color backForeground = workImageGC.getForeground();
		int backLineStyle = workImageGC.getLineStyle();
		int backLineWidth = workImageGC.getLineWidth();
		int[] history;
		for (int c = 0; c < historyCheckpoint; c++) {
			history = historyDrawTool.get(c);
			int toolKind = history[1];
			int boldlKind = history[3];
			workImageGC.setLineStyle(history[2]);
			workImageGC.setLineWidth(boldlKind);
			workImageGC.setForeground(new Color(getShell().getDisplay(), //
					history[4] >> 16, //
					(history[4] >> 8) & 0x00ff, //
					history[4] & 0x00ff));

			int h = history[0];
			history = historyMouseEvent.get(h);
			int start_x = history[1];
			int start_y = history[2];
			for (h++; h < historyMouseEvent.size(); h++) {
				history = historyMouseEvent.get(h);
				if (history[0] == SWT.MouseUp) {
					break;
				}
				int x = history[1];
				int y = history[2];
				if (toolKind == SelectToolAction.DRAW_FREE) {
					workImageGC.drawLine(start_x, start_y, x, y);
					start_x = x;
					start_y = y;
				} else {
					if (start_x == x && start_y == y) {
						workImageGC.drawLine(start_x, start_y, x, y);
					} else {
						int rounded;
						int width = x - start_x;
						int height = y - start_y;
						switch (toolKind) {
						case SelectToolAction.DRAW_LINE:
							workImageGC.drawLine(start_x, start_y, x, y);
							break;
						case SelectToolAction.DRAW_ARROW1:
							workImageGC.setBackground(workImageGC.getForeground());
							drawArrowLine(start_x, start_y, x, y, false);
							break;
						case SelectToolAction.DRAW_ARROW2:
							workImageGC.setBackground(workImageGC.getForeground());
							drawArrowLine(start_x, start_y, x, y, true);
							break;
						case SelectToolAction.DRAW_BOX:
							workImageGC.drawRectangle(start_x, start_y, width, height);
							break;
						case SelectToolAction.DRAW_RBOX:
							rounded = boldlKind * 8;
							workImageGC.drawRoundRectangle(start_x, start_y, width, height, rounded, rounded);
							break;
						case SelectToolAction.DRAW_OVAL:
							workImageGC.drawOval(start_x, start_y, width, height);
							break;
						case SelectToolAction.DRAW_FILL_BOX:
							workImageGC.setBackground(workImageGC.getForeground());
							workImageGC.fillRectangle(start_x, start_y, width, height);
							break;
						case SelectToolAction.DRAW_FILL_RBOX:
							rounded = boldlKind * 8;
							workImageGC.setBackground(workImageGC.getForeground());
							workImageGC.fillRoundRectangle(start_x, start_y, width, height, rounded, rounded);
							break;
						case SelectToolAction.DRAW_FILL_OVAL:
							workImageGC.setBackground(workImageGC.getForeground());
							workImageGC.fillOval(start_x, start_y, width, height);
							break;
						case SelectToolAction.DRAW_TEXT:
							StringBuffer text = historyDrawText.get(c);
							{
								Font backFont = workImageGC.getFont();
								FontData fontData = new FontData(historyDrawFont.get(c));
								workImageGC.setFont(new Font(getShell().getDisplay(), fontData));
								workImageGC.setClipping(start_x, start_y, width, height);
								workImageGC.drawText(text.toString(), start_x, start_y, true);
								workImageGC.setClipping((Rectangle) null);
								workImageGC.setFont(backFont);
							}
							break;
						}
					}
				}
			}
		}
		workImageGC.setBackground(backBackground);
		workImageGC.setForeground(backForeground);
		workImageGC.setLineStyle(backLineStyle);
		workImageGC.setLineWidth(backLineWidth);

		canvas.redraw();
	}

	/**
	 * Decorates the screenshot canvas with the selection rectangle, resize grab points and other adornments
	 */
	private void drawSelection(GC gc) {
		if (currentSelection == null) {
			return;
		}
		Rectangle inside = getScaledSelection();

		// Draw shadow
		gc.setBackground(CommonColors.GRAY_MID);
		gc.setAdvanced(true);
		gc.setAlpha(120);

		Region invertedSelection = new Region();
		invertedSelection.add(canvas.getClientArea());
		invertedSelection.subtract(inside);
		gc.setClipping(invertedSelection);
		gc.fillRectangle(canvas.getClientArea());
		gc.setClipping((Region) null);
		invertedSelection.dispose();

		gc.setAdvanced(false);

		// Draw selection rectangle
		gc.setLineStyle(SWT.LINE_SOLID);
		gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawRectangle(inside);

//		// Draw grab points
//		gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
//		gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_BLACK));
//		for (GrabPoint point : grabPoints) {
//			gc.fillRectangle(point.grabArea);
//			gc.drawRectangle(point.grabArea);
//		}
		gc.setForeground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		Rectangle outside = getOutsideSelection(inside);
		gc.drawRectangle(outside);
		gc.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.fillRectangle(outside.x, outside.y, //
				SQUARE_SIZE * 6, SQUARE_SIZE * 2);
		gc.fillRectangle(outside.x + outside.width - SQUARE_SIZE * 6, outside.y, //
				SQUARE_SIZE * 6, SQUARE_SIZE * 2);
		gc.fillRectangle(outside.x, outside.y, //
				SQUARE_SIZE * 2, SQUARE_SIZE * 6);
		gc.fillRectangle(outside.x + outside.width - SQUARE_SIZE * 2, outside.y, //
				SQUARE_SIZE * 2, SQUARE_SIZE * 6);
		gc.fillRectangle(outside.x, outside.y + outside.height - SQUARE_SIZE * 6, //
				SQUARE_SIZE * 2, SQUARE_SIZE * 6);
		gc.fillRectangle(outside.x + outside.width - SQUARE_SIZE * 2, outside.y + outside.height - SQUARE_SIZE * 6, //
				SQUARE_SIZE * 2, SQUARE_SIZE * 6);
		gc.fillRectangle(outside.x, outside.y + outside.height - SQUARE_SIZE * 2, //
				SQUARE_SIZE * 6, SQUARE_SIZE * 2);
		gc.fillRectangle(outside.x + outside.width - SQUARE_SIZE * 6, outside.y + outside.height - SQUARE_SIZE * 2, //
				SQUARE_SIZE * 6, SQUARE_SIZE * 2);
	}

	/**
	 * Connects the previous mark point to the new reference point, by drawing a new line, rectangle or oval
	 */
	private void drawMarkLine(int x, int y) {
		if (startPoint != null) {
			clearAction.setEnabled(true);
			int drawTool = getSelectDrawToolbar();
			if (drawTool == SelectToolAction.DRAW_FREE) {
				workImageGC.drawLine(startPoint.x, startPoint.y, x, y);
				startPoint.x = x;
				startPoint.y = y;
			} else {
				workImageGC.drawImage(previousImage, 0, 0);
				if (startPoint.x == x && startPoint.y == y) {
					workImageGC.drawLine(startPoint.x, startPoint.y, x, y);
				} else {
					Color backColor;
					Color markColor;
					int rounded;
					int width = x - startPoint.x;
					int height = y - startPoint.y;
					switch (drawTool) {
					case SelectToolAction.DRAW_LINE:
						workImageGC.drawLine(startPoint.x, startPoint.y, x, y);
						break;
					case SelectToolAction.DRAW_ARROW1:
						backColor = workImageGC.getBackground();
						markColor = new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect()));
						workImageGC.setBackground(markColor);
						drawArrowLine(startPoint.x, startPoint.y, x, y, false);
						workImageGC.setBackground(backColor);
						break;
					case SelectToolAction.DRAW_ARROW2:
						backColor = workImageGC.getBackground();
						markColor = new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect()));
						workImageGC.setBackground(markColor);
						drawArrowLine(startPoint.x, startPoint.y, x, y, true);
						workImageGC.setBackground(backColor);
						break;
					case SelectToolAction.DRAW_BOX:
						workImageGC.drawRectangle(startPoint.x, startPoint.y, width, height);
						break;
					case SelectToolAction.DRAW_RBOX:
						rounded = lineBoldToolbar.getSelect() * 8;
						workImageGC.drawRoundRectangle(startPoint.x, startPoint.y, width, height, rounded, rounded);
						break;
					case SelectToolAction.DRAW_OVAL:
						workImageGC.drawOval(startPoint.x, startPoint.y, width, height);
						break;
					case SelectToolAction.DRAW_FILL_BOX:
						backColor = workImageGC.getBackground();
						markColor = new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect()));
						workImageGC.setBackground(markColor);
						workImageGC.fillRectangle(startPoint.x, startPoint.y, width, height);
						workImageGC.setBackground(backColor);
						break;
					case SelectToolAction.DRAW_FILL_RBOX:
						rounded = lineBoldToolbar.getSelect() * 8;
						backColor = workImageGC.getBackground();
						markColor = new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect()));
						workImageGC.setBackground(markColor);
						workImageGC.fillRoundRectangle(startPoint.x, startPoint.y, width, height, rounded, rounded);
						workImageGC.setBackground(backColor);
						break;
					case SelectToolAction.DRAW_FILL_OVAL:
						backColor = workImageGC.getBackground();
						markColor = new Color(getShell().getDisplay(),
								SelectToolAction.int2rgb(drawColorToolbar.getSelect()));
						workImageGC.setBackground(markColor);
						workImageGC.fillOval(startPoint.x, startPoint.y, width, height);
						workImageGC.setBackground(backColor);
						break;
					case SelectToolAction.DRAW_TEXT:
						workImageGC.fillRectangle(startPoint.x, startPoint.y, width, height);
						workImageGC.drawRectangle(startPoint.x, startPoint.y, width, height);
						break;
					}
				}
			}
			canvas.redraw();
		}
	}

	public void drawArrowLine(int xs, int ys, int xe, int ye, boolean bothsides) {
		int width = xe - xs, height = ye - ys;
		int bold = workImageGC.getLineWidth();
		int leng = (bold == 8) ? bold * 4 : (bold == 4) ? bold * 6 : (bold == 2) ? bold * 8 : bold * 10;
		double delta = Math.PI / 6.0;
		double theta = Math.atan2(height, width);

		// Draw line
		if (bothsides) {
			workImageGC.drawLine( // 
					xs + (int) (leng / 2 * Math.cos(theta)), //
					ys + (int) (leng / 2 * Math.sin(theta)), //
					xe - (int) (leng / 2 * Math.cos(theta)), //
					ye - (int) (leng / 2 * Math.sin(theta)));
		} else {
			workImageGC.drawLine( //
					xs, //
					ys, //
					xe - (int) (leng / 2 * Math.cos(theta)), //
					ye - (int) (leng / 2 * Math.sin(theta)));
		}

		// Draw ending side arrow
		workImageGC.setLineWidth(1);

		int[] point = { xe, ye, //
				xe - (int) (leng * Math.cos(theta - delta)), //
				ye - (int) (leng * Math.sin(theta - delta)), //
				xe - (int) (leng * Math.cos(theta + delta)), //
				ye - (int) (leng * Math.sin(theta + delta)) };
		workImageGC.fillPolygon(point);

		// Draw starting side arrow
		if (bothsides) {
			int[] point2 = { xs, ys, //
					xs + (int) (leng * Math.cos(theta - delta)), //
					ys + (int) (leng * Math.sin(theta - delta)), //
					xs + (int) (leng * Math.cos(theta + delta)), //
					ys + (int) (leng * Math.sin(theta + delta)) };
			workImageGC.fillPolygon(point2);
		}

		workImageGC.setLineWidth(bold);
	}

	private static enum SelectionSide {

		LEFT, RIGHT, TOP, BOTTOM;

	};

	private static final int SQUARE_SIZE = 3;

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
		this.imageDirty = false;

		return screenshot;
	}

	public void setImageDirty(boolean pageDirty) {
		this.imageDirty = pageDirty;
	}

	public boolean isImageDirty() {
		return imageDirty;
	}
}
