/*******************************************************************************
 * Copyright (c) 2009 Hiroyuki Inaba and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Hiroyuki Inaba - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.ui;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Hiroyuki Inaba
 */
public class SelectToolAction extends Action implements IMenuCreator {

	public static final int CAPTURE_DROP_DOWN_MENU = 1;

	public static final int ZOOM_DROP_DOWN_MENU = 2;

	public static final int DRAWLINE_TOOLBAR = 3;

	public static final int DRAWARROW_TOOLBAR = 4;

	public static final int DRAWBOX_TOOLBAR = 5;

	public static final int DRAWTEXT_TOOLBAR = 6;

	public static final int LINETYPE_TOOLBAR = 7;

	public static final int LINEBOLD_TOOLBAR = 8;

	public static final int COLOR_TOOLBAR = 9;

	public SelectToolAction(Composite parent, int tool) {
		this(parent, "", tool); //$NON-NLS-1$
	}

	public SelectToolAction(String label, int tool) {
		this(null, label, tool);
	}

	public SelectToolAction(Composite parent, String label, int tool) {
		super(label, AS_DROP_DOWN_MENU);
		setMenuCreator(this);
		showMenuAlways = true;
		this.parent = parent;
		setToolAction(tool);
	}

	private void setToolAction(int tool) {
		if (tool == CAPTURE_DROP_DOWN_MENU) {
			selectedItemID = CAPTURE_DESKTOP;
			initMenu(
					tool,
					new ToolActionItem[] {
							new ToolActionItem(CAPTURE_DESKTOP, Messages.SelectToolAction_Desktop,
									ScreenshotImages.MONITOR_OBJ), //
							new ToolActionItem(CAPTURE_CLIPBOARD, Messages.SelectToolAction_Clipboard,
									ScreenshotImages.CLIPBOARD_OBJ), //
							new ToolActionItem(CAPTURE_FILE, Messages.SelectToolAction_File, ScreenshotImages.FILE_OBJ),
							new ToolActionItem(CAPTURE_RECTANGLE, Messages.SelectToolAction_Selected_Rectangle,
									ScreenshotImages.SEL_RECT) });
			return;
		}

		if (tool == ZOOM_DROP_DOWN_MENU) {
			selectedItemID = ZOOM_FIT;
			initMenu(tool, new ToolActionItem[] { new ToolActionItem(ZOOM_FIT, Messages.SelectToolAction_Fit, null),
					new ToolActionItem(50, Messages.SelectToolAction_ZoomHalf, null),
					new ToolActionItem(100, Messages.SelectToolAction_Zoom1X, null),
					new ToolActionItem(200, Messages.SelectToolAction_Zoom2X, null),
					new ToolActionItem(400, Messages.SelectToolAction_Zoom4X, null),
					new ToolActionItem(800, Messages.SelectToolAction_Zoom8X, null) });
			return;
		}

		if (tool == LINETYPE_TOOLBAR) {
			selectedItemID = SWT.LINE_SOLID;
			initBar(tool,
					new ToolActionItem[] {
							new ToolActionItem(SWT.LINE_SOLID, Messages.SelectToolAction_Solid_Line,
									ScreenshotImages.LINE_SOLD),
							new ToolActionItem(SWT.LINE_DOT, Messages.SelectToolAction_Dotted_Line,
									ScreenshotImages.LINE_DOT),
							new ToolActionItem(SWT.LINE_DASH, Messages.SelectToolAction_Dashed_Line,
									ScreenshotImages.LINE_DASH),
							new ToolActionItem(SWT.LINE_DASHDOT, Messages.SelectToolAction_Dashed_Line_1_dot,
									ScreenshotImages.LINE_DASH1D),
							new ToolActionItem(SWT.LINE_DASHDOTDOT, Messages.SelectToolAction_Dashed_Line_2_dots,
									ScreenshotImages.LINE_DASH2D) });
			return;
		}

		if (tool == DRAWLINE_TOOLBAR) {
			showSelection = true;
			selectedItemID = DRAW_FREE;
			initBar(tool, new ToolActionItem[] {
					new ToolActionItem(DRAW_FREE, Messages.SelectToolAction_Free, ScreenshotImages.EDIT_FREE),
					new ToolActionItem(DRAW_LINE, Messages.SelectToolAction_Line, ScreenshotImages.EDIT_LINE) });
			return;
		}
		if (tool == DRAWARROW_TOOLBAR) {
			showSelection = true;
			selectedItemID = DRAW_ARROW1;
			initBar(tool, new ToolActionItem[] {
					new ToolActionItem(DRAW_ARROW1, Messages.SelectToolAction_Single_Side_Arrow,
							ScreenshotImages.EDIT_ARROW1),
					new ToolActionItem(DRAW_ARROW2, Messages.SelectToolAction_Both_Side_Arrow,
							ScreenshotImages.EDIT_ARROW2) });
			selectedItemID = -1;
			return;
		}
		if (tool == DRAWBOX_TOOLBAR) {
			showSelection = true;
			selectedItemID = DRAW_RBOX;
			initBar(
					tool,
					new ToolActionItem[] {
							new ToolActionItem(DRAW_BOX, Messages.SelectToolAction_Rectangle, ScreenshotImages.EDIT_BOX),
							new ToolActionItem(DRAW_RBOX, Messages.SelectToolAction_Round_Rectangle,
									ScreenshotImages.EDIT_RBOX),
							new ToolActionItem(DRAW_OVAL, Messages.SelectToolAction_Oval, ScreenshotImages.EDIT_OVAL),
							new ToolActionItem(DRAW_FILL_BOX, Messages.SelectToolAction_Fill_Rectangle,
									ScreenshotImages.EDIT_FILL_BOX),
							new ToolActionItem(DRAW_FILL_RBOX, Messages.SelectToolAction_Fill_Round_Rectangle,
									ScreenshotImages.EDIT_FILL_RBOX),
							new ToolActionItem(DRAW_FILL_OVAL, Messages.SelectToolAction_Fill_Oval,
									ScreenshotImages.EDIT_FILL_OVAL) });
			selectedItemID = -1;
			return;
		}
		if (tool == DRAWTEXT_TOOLBAR) {
			showSelection = true;
			selectedItemID = DRAW_TEXT;
			FontData fontData = parent.getShell().getFont().getFontData()[0];
			stringCustom = fontData.toString();
			intgerCustom = rgb2int(255, 0, 0);
			selectedItemID = intgerCustom;
			initFont(tool);
			selectedItemID = -1;
			return;
		}

		if (tool == LINEBOLD_TOOLBAR) {
			selectedItemID = 4;
			initBar(tool, new ToolActionItem[] {
					new ToolActionItem(1, Messages.SelectToolAction_1dot, ScreenshotImages.LINE_BOLD1),
					new ToolActionItem(2, Messages.SelectToolAction_2dots, ScreenshotImages.LINE_BOLD2),
					new ToolActionItem(4, Messages.SelectToolAction_4dots, ScreenshotImages.LINE_BOLD4),
					new ToolActionItem(8, Messages.SelectToolAction_8dots, ScreenshotImages.LINE_BOLD8) });
			return;
		}

		if (tool == COLOR_TOOLBAR) {
			intgerCustom = rgb2int(255, 85, 85);
			selectedItemID = intgerCustom;
			initColor(tool);
			return;
		}
	}

	private void initBar(int tool, ToolActionItem[] items) {
		this.items = items;
		setId(tool + ""); //$NON-NLS-1$
		toolButton = new ToolComposite(parent, SWT.CASCADE) {

			@Override
			public void clickBody() {
				selectItem(getToolTipText(), null);
			}

			@Override
			public void clickMenu(int x, int y) {
				dropDownMenu = new Menu(parent.getShell(), SWT.POP_UP);
				addActionsToMenu();
				Point p = parent.toDisplay(x, y);
				dropDownMenu.setLocation(p.x, p.y);
				dropDownMenu.setVisible(true);
				dropDownMenu = null;
			}

			@Override
			public boolean isSelect() {
				return (selectedItemID >= 0);
			}
		};

		if (items != null) {
			for (ToolActionItem actionItem : items) {
				if (actionItem.id == selectedItemID) {
					toolButton.setImage(actionItem.image.createImage());
					toolButton.setToolTipText(actionItem.label);
				}
			}
		}
	}

	private void initFont(int tool) {
		this.items = null;
		setId(tool + ""); //$NON-NLS-1$
		toolButton = new ToolComposite(parent, SWT.CASCADE) {

			@Override
			public void clickBody() {
				selectedItemID = DRAW_TEXT;
				if (toolButton != null) {
					FontData fontData = new FontData(stringCustom);
					toolButton.setImage(createFontImage(fontData, int2rgb(intgerCustom), true));
					toolButton.redraw();
				}
				run();
			}

			@Override
			public void clickMenu(int x, int y) {
				if (Platform.getWS().equalsIgnoreCase(Platform.WS_WIN32)
						|| Platform.getWS().equalsIgnoreCase(Platform.WS_WPF)) {
					invokeFontDialog();
				} else {
					Menu rightClickMenu = new Menu(parent.getShell(), SWT.POP_UP);
					MenuItem menuItem = new MenuItem(rightClickMenu, SWT.PUSH);
					menuItem.setText(Messages.SelectToolAction_Font_);
					menuItem.addListener(SWT.Selection, new Listener() {
						public void handleEvent(final Event event) {
							invokeFontDialog();
						}
					});
					menuItem = new MenuItem(rightClickMenu, SWT.PUSH);
					menuItem.setText(Messages.SelectToolAction_Color_);
					menuItem.addListener(SWT.Selection, new Listener() {
						public void handleEvent(final Event event) {
							invokeColorDialog();
						}
					});
					Point p = parent.toDisplay(x, y);
					rightClickMenu.setLocation(p.x, p.y);
					rightClickMenu.setVisible(true);
				}
			}

			@Override
			public boolean isSelect() {
				return (selectedItemID >= 0);
			}

			public void invokeFontDialog() {
				FontData fontData = new FontData(stringCustom);
				FontDialog fontWindow = new FontDialog(parent.getShell());
				fontWindow.setFontList(new FontData[] { fontData });
				fontWindow.setRGB(int2rgb(intgerCustom));
				fontData = fontWindow.open();
				if (fontData != null) {
					intgerCustom = rgb2int(fontWindow.getRGB());
					stringCustom = fontData.toString();
					toolButton.setToolTipText(font2string(fontData));
					clickBody();
				}
			}

			public void invokeColorDialog() {
				ColorDialog colorWindow = new ColorDialog(parent.getShell());
				colorWindow.setRGB(int2rgb(intgerCustom));
				RGB rgb = colorWindow.open();
				if (rgb != null) {
					intgerCustom = rgb2int(rgb);
					clickBody();
				}
			}
		};

		FontData fontData = new FontData(stringCustom);
		toolButton.setToolTipText(font2string(fontData));
		toolButton.setImage(createFontImage(fontData, int2rgb(intgerCustom), false));
	}

	private void initColor(int tool) {
		this.items = null;
		setId(tool + ""); //$NON-NLS-1$
		toolButton = new ToolComposite(parent, SWT.NONE) {

			@Override
			public void clickMenu(int x, int y) {
				ColorDialog colorWindow = new ColorDialog(parent.getShell());
				colorWindow.setRGB(int2rgb(intgerCustom));
				RGB rgb = colorWindow.open();
				if (rgb != null) {
					intgerCustom = rgb2int(rgb);
					selectedItemID = intgerCustom;
					toolButton.setImage(createColorImage(int2rgb(intgerCustom)));
					toolButton.redraw();
				}
			}
		};
		toolButton.setToolTipText(""); //$NON-NLS-1$
		toolButton.setImage(createColorImage(int2rgb(intgerCustom)));
	}

	private Image createColorImage(RGB rgb) {
		Display display = parent.getDisplay();
		Color BACKGROUND = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		Color DARK_GRAY = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		int x = 16, y = 16;
		Image image = new Image(display, x, y);
		GC gc = new GC(image);
		gc.setForeground(DARK_GRAY);
		gc.setBackground(BACKGROUND);
		gc.fillRectangle(0, 0, x, y);
		gc.setBackground(new Color(display, rgb));
		gc.fillRectangle(1, 2, x - 2, y - 4);
		gc.drawRectangle(1, 2, x - 3, y - 5);
		gc.dispose();
		return image;
	}

	private Image createFontImage(FontData fontData, RGB rgb, boolean select) {
		Display display = parent.getDisplay();
		Color ButtonFace = display.getSystemColor((select) ? SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW
				: SWT.COLOR_WIDGET_BACKGROUND);
		int x = 16, y = 16;
		Image image = new Image(display, x, y);
		GC gc = new GC(image);
		gc.setBackground(ButtonFace);
		gc.fillRectangle(0, 0, x, y);
		String label = "A"; //$NON-NLS-1$
		fontData.height = 11;
		gc.setFont(new Font(display, fontData));
		gc.setForeground(new Color(display, rgb));
		Point sz = gc.textExtent(label);
		gc.drawText(label, (x - sz.x) / 2, (y - sz.y) / 2 + 1, true);
		gc.dispose();
		return image;
	}

	private void initMenu(int tool, ToolActionItem[] items) {
		this.items = items;
		setId(tool + ""); //$NON-NLS-1$
		if (items != null) {
			setImageDescriptor(items[0].image);
			setToolTipText(items[0].label);
			selectedItemID = items[0].id;
		}
	}

	private class ToolActionItem {
		int id;

		String label;

		ImageDescriptor image;

		ToolActionItem(int id, String label, ImageDescriptor image) {
			this.id = id;
			this.label = label;
			this.image = image;
		}
	}

	private class ToolComposite extends Composite {
		private Image image;

		private final boolean bMenu;

		private boolean bMouse;

		private static final int S = 1; // Border for Selected rectangle

		private static final int B = 2; // Border for Shadow rectangle  

		private static final int G = 3; // Gap

		private static final int M = 4; // Image width for Sub menu  

		private final Point iconSize = new Point(S + B + 16 + B + S, S + B + 16 + B + S);

		private final Point maxSize = new Point(iconSize.x + G + M + G, iconSize.y);

		public ToolComposite(Composite parent, int style) {
			super(parent, style);
			bMenu = (style & SWT.CASCADE) != 0;

			addListener(SWT.Paint, new Listener() {
				public void handleEvent(Event e) {
					Color background = e.gc.getBackground();
					Color foreground = e.gc.getForeground();
					Display display = e.widget.getDisplay();
					Color NORMAL_SHADOW = display.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
					boolean enabled = getEnabled();
					if (bMouse && enabled) {
						e.gc.setForeground(NORMAL_SHADOW);
						if (bMenu) {
							e.gc.drawRectangle(0, 0, maxSize.x - 1, maxSize.y - 1);
							e.gc.drawLine(iconSize.x, 0, iconSize.x, maxSize.y);
						} else {
							e.gc.drawRectangle(0, 0, iconSize.x - 1, iconSize.y - 1);
						}
					}
					if (showSelection && isSelect() && enabled) {
						Color HIGHLIGHT_SHADOW = display.getSystemColor(SWT.COLOR_WIDGET_HIGHLIGHT_SHADOW);
						Color LIGHT_SHADOW = display.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
						Color SELECTION = display.getSystemColor(SWT.COLOR_LIST_SELECTION);
						e.gc.setBackground(HIGHLIGHT_SHADOW);
						e.gc.fillRectangle(0, 0, iconSize.x, iconSize.y);
						e.gc.setForeground(NORMAL_SHADOW);
						e.gc.drawLine(S, S, iconSize.x - S - 1, S);
						e.gc.drawLine(S + 1, S + 1, iconSize.x - S - 2, S + 1);
						e.gc.drawLine(S, S, S, iconSize.y - S - 1);
						e.gc.drawLine(S + 1, S + 1, S + 1, iconSize.y - S - 2);
						e.gc.setForeground(HIGHLIGHT_SHADOW);
						e.gc.drawLine(iconSize.x - S - 1, S + 1, iconSize.x - S - 1, iconSize.y - S - 1);
						e.gc.drawLine(S, iconSize.y - S - 1, iconSize.x - S - 1, iconSize.y - S - 1);
						e.gc.setForeground(LIGHT_SHADOW);
						e.gc.drawLine(iconSize.x - S - 2, S + 2, iconSize.x - S - 2, iconSize.y - S - 2);
						e.gc.drawLine(S + 1, iconSize.x - S - 2, iconSize.x - S - 2, iconSize.y - S - 2);
						e.gc.setForeground(SELECTION);
						e.gc.drawRectangle(0, 0, iconSize.x - 1, iconSize.y - 1);
					}

					if (getEnabled()) {
						e.gc.drawImage(image, S + B, S + B);
					} else {
						Image disabled = new Image(display, image, SWT.IMAGE_DISABLE);
						e.gc.drawImage(disabled, S + B, S + B);
					}

					if (bMenu) {
						if (Platform.getWS().equalsIgnoreCase(Platform.WS_WIN32)
								|| Platform.getWS().equalsIgnoreCase(Platform.WS_WPF)) {
							Color FOREGROUND = display.getSystemColor((getEnabled()) ? SWT.COLOR_WIDGET_FOREGROUND
									: SWT.COLOR_WIDGET_DARK_SHADOW);
							e.gc.setForeground(FOREGROUND);
							int x = iconSize.x + G;
							int y = S + B + M;
							int h = 6;
							while (h >= 0) {
								e.gc.drawLine(x, y, x, y + h);
								x++;
								y++;
								h -= 2;
							}
						} else {
							Color FOREGROUND = display.getSystemColor((getEnabled()) ? SWT.COLOR_WIDGET_DARK_SHADOW
									: SWT.COLOR_WIDGET_NORMAL_SHADOW);
							e.gc.setForeground(FOREGROUND);
							int x = iconSize.x + G;
							int y = S + B + M;
							int h = 4;
							e.gc.drawLine(x, y, x + h / 2 + 1, y + h / 2 + 1);
							e.gc.drawLine(x, y + 1, x + h / 2, y + h / 2 + 1);
							e.gc.drawLine(x, y + h + 1, x + h / 2, y + h / 2 + 1);
							e.gc.drawLine(x, y + h + 2, x + h / 2, y + h / 2 + 2);
						}
					}
					e.gc.setBackground(background);
					e.gc.setForeground(foreground);
					//System.out.println(e.toString());
				}
			});

			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (e.x < iconSize.x && e.y < iconSize.y) {
						if (showSelection) {
							clickBody();
							return;
						} else {
							invokeMenu((Composite) e.widget);
						}
					} else {
						invokeMenu((Composite) e.widget);
					}
				}

				private void invokeMenu(Composite widget) {
					Rectangle b = widget.getBounds();
					Point p = widget.getLocation();
					p.x += b.width + 3;
					bMouse = false;
					redraw();
					clickMenu(p.x, p.y);
				}
			});

			addListener(SWT.MouseEnter, new Listener() {

				public void handleEvent(Event event) {
					if (getEnabled()) {
						bMouse = true;
						redraw();
					}
				}
			});
			addListener(SWT.MouseMove, new Listener() {

				public void handleEvent(Event event) {
					if (getEnabled()) {
						bMouse = true;
						redraw();
					}
				}
			});
			addListener(SWT.MouseExit, new Listener() {

				public void handleEvent(Event event) {
					if (getEnabled()) {
						bMouse = false;
						redraw();
					}
				}
			});
		}

		@Override
		public Point computeSize(int wHint, int hHint, boolean changed) {
			if (!getVisible()) {
				return new Point(0, 0);
			}
			return maxSize;
		}

		public void setImage(Image image) {
			this.image = image;
		}

		public void clickBody() {
		}

		public void clickMenu(int x, int y) {
		}

		public boolean isSelect() {
			return false;
		}
	}

	private ToolActionItem[] items;

	private int selectedItemID;

	private ToolComposite toolButton;

	private Menu dropDownMenu;

	private int intgerCustom;

	private String stringCustom;

	private boolean showMenuAlways;

	public boolean isShowMenuAlways() {
		return showMenuAlways;
	}

	public void setShowMenuAlways(boolean showMenuAlways) {
		this.showMenuAlways = showMenuAlways;
	}

	@Override
	public void runWithEvent(Event event) {
		if (showMenuAlways) {
			IMenuCreator mc = getMenuCreator();
			if (mc != null) {
				Widget item = event.widget;
				ToolItem ti = (ToolItem) item;
				Menu m = mc.getMenu(ti.getParent());
				if (m != null) {
					// position the menu below the drop down item
					Rectangle itemBounds = ti.getBounds();
					Point point = ti.getParent().toDisplay(itemBounds.x, itemBounds.y + itemBounds.height);
					m.setLocation(point.x, point.y); // waiting for SWT
					m.setVisible(true);
					return; // we don't fire the action
				}
			}
		}
		super.runWithEvent(event);
	}

	public static final int ZOOM_FIT = 0;

	public static final int DRAW_FREE = 0;

	public static final int DRAW_LINE = 1;

	public static final int DRAW_ARROW1 = 2;

	public static final int DRAW_ARROW2 = 3;

	public static final int DRAW_BOX = 10;

	public static final int DRAW_RBOX = 11;

	public static final int DRAW_FILL_BOX = 12;

	public static final int DRAW_FILL_RBOX = 13;

	public static final int DRAW_OVAL = 20;

	public static final int DRAW_FILL_OVAL = 21;

	public static final int DRAW_TEXT = 30;

	public static final int CAPTURE_DESKTOP = 0;

	public static final int CAPTURE_CLIPBOARD = 1;

	public static final int CAPTURE_FILE = 2;

	public static final int CAPTURE_RECTANGLE = 3;

	public int getSelect() {
		return selectedItemID;
	}

	public void setUnselect() {
		selectedItemID = -1;
		if (toolButton != null) {
			if (getId().equals(DRAWTEXT_TOOLBAR + "")) { //$NON-NLS-1$
				FontData fontData = new FontData(stringCustom);
				toolButton.setImage(createFontImage(fontData, int2rgb(intgerCustom), false));
			}
			toolButton.redraw();
		}
	}

	public int getIntgerCustom() {
		return intgerCustom;
	}

	public String getStringCustom() {
		return stringCustom;
	}

	protected void selectItem(String label, ImageDescriptor image) {
		for (ToolActionItem actionItem : items) {
			if (actionItem.label.equals(label)) {
				selectedItemID = actionItem.id;
				if (getId().equals(CAPTURE_DROP_DOWN_MENU + "")) { //$NON-NLS-1$
					if (selectedItemID == CAPTURE_FILE) {
						FileDialog dialog = new FileDialog(parent.getShell());
						dialog.setFileName(filename);
						dialog.setFilterExtensions(new String[] { "*.bmp;*.jpg;*.png", "*.*" }); //$NON-NLS-1$ //$NON-NLS-2$
						String result = dialog.open();
						if (result != null && result.length() > 0) {
							filename = result;
							run();
						}
					} else {
						run();
					}
					selectedItemID = CAPTURE_DESKTOP;
				} else {
					setToolTipText(label);
					if (image != null) {
						setImageDescriptor(image);
					}
					if (toolButton != null) {
						toolButton.setToolTipText(label);
						if (image != null) {
							toolButton.setImage(image.createImage());
						}
						toolButton.redraw();
					}
					run();
				}
				break;
			}
		}
	}

	private void addActionsToMenu() {
		for (ToolActionItem actionItem : items) {
			Action action = new Action() {
				@Override
				public void run() {
					selectItem(getText(), getImageDescriptor());
				}
			};
			action.setText(actionItem.label);
			if (actionItem.image != null) {
				action.setImageDescriptor(actionItem.image);
			}
			updateAction(action, actionItem.id);
			ActionContributionItem contributionItem = new ActionContributionItem(action);
			contributionItem.fill(dropDownMenu, -1);
		}
	}

	private void updateAction(Action action, int id) {
		action.setChecked(id == selectedItemID);
		if (getId().equals(CAPTURE_DROP_DOWN_MENU + "")) { //$NON-NLS-1$
			if (id == CAPTURE_CLIPBOARD) {
				action.setEnabled(existImageOnClipboard());
			} else if (id == CAPTURE_RECTANGLE) {
				action.setEnabled(isEnableRectangle());
			}
		}
	}

	protected boolean isEnableRectangle() {
		return false;
	}

	public void dispose() {
		if (toolButton != null) {
			toolButton.dispose();
			toolButton = null;
		}
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
			dropDownMenu = null;
		}
	}

	public Menu getMenu(Control parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	public Menu getMenu(Menu parent) {
		if (dropDownMenu != null) {
			dropDownMenu.dispose();
		}
		dropDownMenu = new Menu(parent);
		addActionsToMenu();
		return dropDownMenu;
	}

	private final Composite parent;

	private boolean showSelection;

	private String filename;

	public boolean getVisible() {
		if (toolButton != null) {
			return toolButton.getVisible();
		}
		return false;
	}

	public void setVisible(boolean visible) {
		if (toolButton != null) {
			toolButton.setVisible(visible);
			toolButton.redraw();
		}
	}

	public boolean getEnabled() {
		if (toolButton != null) {
			return toolButton.getEnabled();
		}
		return false;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (toolButton != null) {
			toolButton.setEnabled(enabled);
			toolButton.redraw();
		}
	}

	public Image getFileImage() {
		try {
			Image image = new Image(parent.getShell().getDisplay(), filename);
			return image;
		} catch (Exception e) {
			// None
		}
		return null;
	}

	public Image getClipboardImage() {
		try {
			Clipboard clipboard = new Clipboard(parent.getShell().getDisplay());
			ImageTransfer imageTransfer = ImageTransfer.getInstance();
			Object data = clipboard.getContents(imageTransfer);
			if (data instanceof ImageData) {
				Image image = new Image(parent.getShell().getDisplay(), (ImageData) data);
				return image;
			}
		} catch (Exception e) {
			// None
		}
		return null;
	}

	private boolean existImageOnClipboard() {
		try {
			Clipboard clipboard = new Clipboard(parent.getShell().getDisplay());
			ImageTransfer imageTransfer = ImageTransfer.getInstance();
			Object data = clipboard.getContents(imageTransfer);
			if (data instanceof ImageData) {
				return true;
			}
		} catch (Exception e) {
			// None
		}
		return false;
	}

	public static int rgb2int(RGB rgb) {
		return rgb2int(rgb.red, rgb.green, rgb.blue);
	}

	public static int rgb2int(int r, int g, int b) {
		return (r << 16) + (g << 8) + b;
	}

	public static RGB int2rgb(int rgb) {
		return new RGB(rgb >> 16, (rgb >> 8) & 0x00ff, rgb & 0x00ff);
	}

	private String font2string(FontData fontData) {
		String info = NLS.bind(Messages.SelectToolAction_Font_Name_Size, //
				new Object[] { fontData.getName(), fontData.getHeight() + "" }); //$NON-NLS-1$
		int style = fontData.getStyle();
		if ((style & SWT.BOLD) != 0) {
			info = info + Messages.SelectToolAction_Font_Bold;
		}
		if ((style & SWT.ITALIC) != 0) {
			info = info + Messages.SelectToolAction_Font_Italic;
		}
		return info;
	}
}
