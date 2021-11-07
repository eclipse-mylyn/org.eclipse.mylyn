/*******************************************************************************
 * Copyright (c) 2010, 2021 Tom Seidel, Remus Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *
 * Contributors:
 *     Tom Seidel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.htmltext;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.htmltext.commands.Command;
import org.eclipse.mylyn.htmltext.commands.GetHtmlCommand;
import org.eclipse.mylyn.htmltext.commands.SetHtmlCommand;
import org.eclipse.mylyn.htmltext.configuration.Configuration;
import org.eclipse.mylyn.htmltext.events.NodeSelectionEvent;
import org.eclipse.mylyn.htmltext.listener.NodeSelectionChangeListener;
import org.eclipse.mylyn.htmltext.model.TriState;
import org.eclipse.mylyn.htmltext.util.ColorConverter;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Tom Seidel <tom.seidel@remus-software.org>
 */
public class HtmlComposer {

	/**
	 * A function which is called if the content of the editor has changed.
	 * <p>
	 * Unfortunately the underlying ckeditor cannot guarantee that every
	 * modification will be notified to the appended listeners. There is an
	 * additional polling mechanismus which tracks modifications.
	 * </p>
	 * 
	 * @author Tom Seidel <tom.seidel@remus-software.org>
	 */
	private class ModifiedFunction extends BrowserFunction {
		public ModifiedFunction(Browser browser) {
			super(browser, "_delegate_modified");
		}

		@Override
		public Object function(Object[] arguments) {
			if (arguments.length > 0) {
				String identifier = (String) arguments[0];
				Event event = new Event();
				event.widget = getBrowser();
				event.data = this;
				ModifyEvent modifyEvent = new ModifyEvent(event);
				if (pendingListenerCallBackMap.get(identifier) != null) {
					List<ModifyListener> list = pendingListenerCallBackMap
							.get(identifier);
					for (ModifyListener modifyListener : list) {
						modifyListener.modifyText(modifyEvent);
					}
				} else {
					if (modifyListenerList.size() > 0) {
						for (ModifyListener listener : modifyListenerList) {
							listener.modifyText(modifyEvent);
						}
					}
				}
				pendingListenerCallBackMap.remove(identifier);
			}

			return null;
		}

	}

	/**
	 * BrowserFunction that is called if the wrapped Ckeditor is initialized.
	 * 
	 * @author Tom Seidel <tom.seidel@remus-software.org>
	 */
	private class RenderCompleteFunction extends BrowserFunction {

		public RenderCompleteFunction(Browser browser) {
			super(browser, "_delegate_init");
		}

		@Override
		public Object function(Object[] arguments) {
			initialize();
			return null;
		}

	}

	/**
	 * BrowserFunction that delegates the event from ckeditor that is thrown if
	 * the selected dom-node changed.
	 * 
	 * @author Tom Seidel <tom.seidel@remus-software.org>
	 */
	private class SelectionChangedFunction extends BrowserFunction {

		public SelectionChangedFunction(Browser browser) {
			super(browser, "_delegate_selectionChanged");
		}

		@Override
		public Object function(Object[] arguments) {
			// check if listeners are registered. Could be that in the near
			// future the construction of NodeSelectionEvent is not so cheap
			// like at the moment.
			if (selectionListenerList.size() > 0) {
				NodeSelectionEvent nodeSelectionEvent = new NodeSelectionEvent(
						null);
				for (NodeSelectionChangeListener listener : selectionListenerList) {
					listener.selectedNodeChanged(nodeSelectionEvent);
				}
			}
			if (trackedCommands.size() > 0) {
				Set<String> keySet = trackedCommands.keySet();
				for (String string : keySet) {
					String valueOf = String
							.valueOf(evaluate("return integration.editor.getCommand('"
									+ string + "').state;"));
					TriState fromString = TriState.fromString(valueOf);
					if (fromString != trackedCommands.get(string).getState()) {
						trackedCommands.get(string).setState(fromString);
					}
				}
			}
			return null;
		}

	}

	/**
	 * The wrapped browser widget.
	 */
	private final Browser browser;

	/**
	 * A list of listeners which fire if the selected node within the html is
	 * changed.
	 */
	private transient List<NodeSelectionChangeListener> selectionListenerList = new ArrayList<NodeSelectionChangeListener>();

	private transient List<ModifyListener> modifyListenerList = new ArrayList<ModifyListener>();

	/**
	 * a temporary collection of commands that are executed before the ckeditor
	 * was initialized. If the ckeditor finishes its initialization all commands
	 * are executed.
	 * 
	 * @see HtmlComposer#initialize()
	 */
	private final List<Command> pendingCommands = Collections
			.synchronizedList(new ArrayList<Command>());

	/**
	 * A map of commands that were executed before the widget was initialized
	 * and their appending listeners which are still waiting for an event.
	 */
	private Map<Command, List<ModifyListener>> pendingListeners = new HashMap<Command, List<ModifyListener>>();

	/**
	 * A map of callback-Ids and their appended Listeners. This is
	 */
	private Map<String, List<ModifyListener>> pendingListenerCallBackMap = new HashMap<String, List<ModifyListener>>();

	/**
	 * Tracked {@link Command}s.
	 */
	private final Map<String, Command> trackedCommands = new HashMap<String, Command>();

	/**
	 * Flag if the ckeditor finishes its initialization and is ready for
	 * receiving commands.
	 */
	private boolean initialized;

	
	/**
	 * Constructs a new instance of a {@link Browser} and includes a ckeditor
	 * instance.
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @see Browser#Browser(Composite, int)
	 * 
	 */
	public HtmlComposer(final Composite parent, final int style) {
		this(parent, style, null);
		
	}
	/**
	 * Constructs a new instance of a {@link Browser} and includes a ckeditor
	 * instance.
	 * 
	 * @param parent
	 *            a composite control which will be the parent of the new
	 *            instance (cannot be null)
	 * @param style
	 *            the style of control to construct
	 * @param config the configuration for the html-widget
	 * @see Browser#Browser(Composite, int)
	 * @since 0.8
	 */
	public HtmlComposer(final Composite parent, final int style, Configuration config) {
		browser = new Browser(parent, style);
		browser.setMenu(new Menu(browser));
		new RenderCompleteFunction(browser);
		URL baseUrl;
		try {
			baseUrl = FileLocator.resolve(FileLocator.find(FrameworkUtil.getBundle(HtmlComposer.class), new Path(
					"/eclipsebridge/base.html"), Collections.emptyMap()));
			browser.setUrl(baseUrl.toString() + (config != null ? "?" + config.toQuery() : ""));
			browser.addProgressListener(new ProgressAdapter() {
				@Override
				public void completed(ProgressEvent event) {
					browser.execute("integration.eclipseRunning = true;");
					browser.removeProgressListener(this);
				}
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#addControlListener(org.eclipse.swt.events.ControlListener)
	 */
	public void addControlListener(final ControlListener listener) {
		browser.addControlListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Widget#addDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void addDisposeListener(final DisposeListener listener) {
		browser.addDisposeListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void addFocusListener(final FocusListener listener) {
		browser.addFocusListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#addHelpListener(org.eclipse.swt.events.HelpListener)
	 */
	public void addHelpListener(final HelpListener listener) {
		browser.addHelpListener(listener);
	}

	public void addModifyListener(ModifyListener listener) {
		modifyListenerList.add(listener);
	}

	public void addNodeSelectionChangeListener(
			NodeSelectionChangeListener listener) {
		selectionListenerList.add(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#addPaintListener(org.eclipse.swt.events.PaintListener)
	 */
	public void addPaintListener(final PaintListener listener) {
		browser.addPaintListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#addTraverseListener(org.eclipse.swt.events.TraverseListener)
	 */
	public void addTraverseListener(final TraverseListener listener) {
		browser.addTraverseListener(listener);
	}

	/**
	 * @param wHint
	 * @param hHint
	 * @return
	 * @see org.eclipse.swt.widgets.Control#computeSize(int, int)
	 */
	public Point computeSize(final int wHint, final int hHint) {
		return browser.computeSize(wHint, hHint);
	}

	/**
	 * @param wHint
	 * @param hHint
	 * @param changed
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	public Point computeSize(final int wHint, final int hHint,
			final boolean changed) {
		return browser.computeSize(wHint, hHint, changed);
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 * @see org.eclipse.swt.widgets.Scrollable#computeTrim(int, int, int, int)
	 */
	public Rectangle computeTrim(final int x, final int y, final int width,
			final int height) {
		return browser.computeTrim(x, y, width, height);
	}

	/**
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	public void dispose() {
		Collection<Command> values = trackedCommands.values();
		for (Command command : values) {
			command.dispose();
		}
		browser.dispose();
	}

	/**
	 * @param script
	 * @return
	 * @throws SWTException
	 * @see org.eclipse.swt.browser.Browser#evaluate(java.lang.String)
	 */
	public Object evaluate(String script) throws SWTException {
		return browser.evaluate(script);
	}

	/**
	 * @param script
	 * @return
	 * @see org.eclipse.swt.browser.Browser#execute(java.lang.String)
	 */
	public boolean execute(String script) {
		return browser.execute(script);
	}

	/**
	 * Executes a given command
	 * 
	 * @param command
	 *            the command to execute
	 */
	public void execute(Command command) {
		if (initialized) {
			/*
			 * if the command was executed while the ckeditor was not
			 * initialized yet. this is required to keep track of the listeners
			 * that needs to be notified if a command is executed before the
			 * widget was initialized but also to filter the listeners that were
			 * added to the widget after the originating command was scheduled.
			 */
			if (pendingListeners.get(command) != null) {
				String nanoTime = String.valueOf(System.nanoTime());
				pendingListenerCallBackMap.put(nanoTime,
						pendingListeners.get(command));
				execute("integration.pendingCommandIdentifier = \'" + nanoTime
						+ "\';");
				execute(command.getCommand());
				pendingListeners.remove(command);
			} else {
				execute("integration.pendingCommandIdentifier = \'\';");
				execute(command.getCommand());
			}
		} else {
			pendingListeners.put(command, new ArrayList<ModifyListener>(
					modifyListenerList));
			pendingCommands.add(command);
		}
	}

	/**
	 * Execute a command wit a result.
	 * 
	 * @param command
	 *            the command to execute
	 * @return the result of the execution.
	 */
	public Object executeWithReturn(Command command) {
		if (initialized) {
			return evaluate(command.getCommand());
		}
		return null;
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#forceFocus()
	 */
	public boolean forceFocus() {
		return browser.forceFocus();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getAccessible()
	 */
	public Accessible getAccessible() {
		return browser.getAccessible();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getBackground()
	 */
	public Color getBackground() {
		return browser.getBackground();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getBackgroundImage()
	 */
	public Image getBackgroundImage() {
		return browser.getBackgroundImage();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#getBackgroundMode()
	 */
	public int getBackgroundMode() {
		return browser.getBackgroundMode();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getBorderWidth()
	 */
	public int getBorderWidth() {
		return browser.getBorderWidth();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getBounds()
	 */
	public Rectangle getBounds() {
		return browser.getBounds();
	}

	public Browser getBrowser() {
		return browser;
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#getChildren()
	 */
	public Control[] getChildren() {
		return browser.getChildren();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Scrollable#getClientArea()
	 */
	public Rectangle getClientArea() {
		return browser.getClientArea();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getCursor()
	 */
	public Cursor getCursor() {
		return browser.getCursor();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#getData()
	 */
	public Object getData() {
		return browser.getData();
	}

	/**
	 * @param key
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#getData(java.lang.String)
	 */
	public Object getData(final String key) {
		return browser.getData(key);
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#getDisplay()
	 */
	public Display getDisplay() {
		return browser.getDisplay();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getEnabled()
	 */
	public boolean getEnabled() {
		return browser.getEnabled();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getFont()
	 */
	public Font getFont() {
		return browser.getFont();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getForeground()
	 */
	public Color getForeground() {
		return browser.getForeground();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Scrollable#getHorizontalBar()
	 */
	public ScrollBar getHorizontalBar() {
		return browser.getHorizontalBar();
	}

	/**
	 * Returns the current html content of the widget
	 * 
	 * @return the html
	 */
	public String getHtml() {
		GetHtmlCommand getHtmlCommand = new GetHtmlCommand();
		getHtmlCommand.setComposer(this);
		Object executeWithReturn = executeWithReturn(getHtmlCommand);
		if (executeWithReturn != null) {
			return String.valueOf(executeWithReturn);
		}
		return null;

	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#getLayout()
	 */
	public Layout getLayout() {
		return browser.getLayout();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getLayoutData()
	 */
	public Object getLayoutData() {
		return browser.getLayoutData();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#getLayoutDeferred()
	 */
	public boolean getLayoutDeferred() {
		return browser.getLayoutDeferred();
	}

	/**
	 * @param eventType
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#getListeners(int)
	 */
	public Listener[] getListeners(final int eventType) {
		return browser.getListeners(eventType);
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getLocation()
	 */
	public Point getLocation() {
		return browser.getLocation();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getMenu()
	 */
	public Menu getMenu() {
		return browser.getMenu();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getMonitor()
	 */
	public Monitor getMonitor() {
		return browser.getMonitor();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getParent()
	 */
	public Composite getParent() {
		return browser.getParent();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getRegion()
	 */
	public Region getRegion() {
		return browser.getRegion();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getShell()
	 */
	public Shell getShell() {
		return browser.getShell();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getSize()
	 */
	public Point getSize() {
		return browser.getSize();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#getStyle()
	 */
	public int getStyle() {
		return browser.getStyle();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#getTabList()
	 */
	public Control[] getTabList() {
		return browser.getTabList();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Scrollable#getVerticalBar()
	 */
	public ScrollBar getVerticalBar() {
		return browser.getVerticalBar();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#getVisible()
	 */
	public boolean getVisible() {
		return browser.getVisible();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#getWebBrowser()
	 */
	@Deprecated
	public Object getWebBrowser() {
		return browser.getWebBrowser();
	}

	void initialize() {
		new SelectionChangedFunction(browser);
		new ModifiedFunction(browser);
		initialized = true;
		for (Command command : pendingCommands) {
			execute(command);
		}
		pendingCommands.clear();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#isDisposed()
	 */
	public boolean isDisposed() {
		return browser.isDisposed();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#isEnabled()
	 */
	public boolean isEnabled() {
		return browser.isEnabled();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.browser.Browser#isFocusControl()
	 */
	public boolean isFocusControl() {
		return browser.isFocusControl();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#isLayoutDeferred()
	 */
	public boolean isLayoutDeferred() {
		return browser.isLayoutDeferred();
	}

	/**
	 * @param eventType
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#isListening(int)
	 */
	public boolean isListening(final int eventType) {
		return browser.isListening(eventType);
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#isReparentable()
	 */
	public boolean isReparentable() {
		return browser.isReparentable();
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Control#isVisible()
	 */
	public boolean isVisible() {
		return browser.isVisible();
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#layout()
	 */
	public void layout() {
		browser.layout();
	}

	/**
	 * @param changed
	 * @see org.eclipse.swt.widgets.Composite#layout(boolean)
	 */
	public void layout(final boolean changed) {
		browser.layout(changed);
	}

	/**
	 * @param changed
	 * @param all
	 * @see org.eclipse.swt.widgets.Composite#layout(boolean, boolean)
	 */
	public void layout(final boolean changed, final boolean all) {
		browser.layout(changed, all);
	}

	/**
	 * @param changed
	 * @see org.eclipse.swt.widgets.Composite#layout(org.eclipse.swt.widgets.Control[])
	 */
	public void layout(final Control[] changed) {
		browser.layout(changed);
	}

	/**
	 * @param control
	 * @see org.eclipse.swt.widgets.Control#moveAbove(org.eclipse.swt.widgets.Control)
	 */
	public void moveAbove(final Control control) {
		browser.moveAbove(control);
	}

	/**
	 * @param control
	 * @see org.eclipse.swt.widgets.Control#moveBelow(org.eclipse.swt.widgets.Control)
	 */
	public void moveBelow(final Control control) {
		browser.moveBelow(control);
	}

	/**
	 * @param eventType
	 * @param event
	 * @see org.eclipse.swt.widgets.Widget#notifyListeners(int,
	 *      org.eclipse.swt.widgets.Event)
	 */
	public void notifyListeners(final int eventType, final Event event) {
		browser.notifyListeners(eventType, event);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#pack()
	 */
	public void pack() {
		browser.pack();
	}

	/**
	 * @param changed
	 * @see org.eclipse.swt.widgets.Control#pack(boolean)
	 */
	public void pack(final boolean changed) {
		browser.pack(changed);
	}

	/**
	 * @param gc
	 * @return
	 * @see org.eclipse.swt.widgets.Control#print(org.eclipse.swt.graphics.GC)
	 */
	public boolean print(final GC gc) {
		return browser.print(gc);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#redraw()
	 */
	public void redraw() {
		browser.redraw();
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param all
	 * @see org.eclipse.swt.widgets.Control#redraw(int, int, int, int, boolean)
	 */
	public void redraw(final int x, final int y, final int width,
			final int height, final boolean all) {
		browser.redraw(x, y, width, height, all);
	}

	/**
	 * @see org.eclipse.swt.browser.Browser#refresh()
	 */
	public void refresh() {
		browser.refresh();
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Widget#removeDisposeListener(org.eclipse.swt.events.DisposeListener)
	 */
	public void removeDisposeListener(final DisposeListener listener) {
		browser.removeDisposeListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	public void removeFocusListener(final FocusListener listener) {
		browser.removeFocusListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#removeHelpListener(org.eclipse.swt.events.HelpListener)
	 */
	public void removeHelpListener(final HelpListener listener) {
		browser.removeHelpListener(listener);
	}

	public void removeModifyListener(ModifyListener listener) {
		modifyListenerList.remove(listener);
	}

	public void removeNodeSelectionChangeListener(
			NodeSelectionChangeListener listener) {
		selectionListenerList.remove(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.browser.Browser#removeOpenWindowListener(org.eclipse.swt.browser.OpenWindowListener)
	 */
	public void removeOpenWindowListener(final OpenWindowListener listener) {
		browser.removeOpenWindowListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#removePaintListener(org.eclipse.swt.events.PaintListener)
	 */
	public void removePaintListener(final PaintListener listener) {
		browser.removePaintListener(listener);
	}

	/**
	 * @param listener
	 * @see org.eclipse.swt.widgets.Control#removeTraverseListener(org.eclipse.swt.events.TraverseListener)
	 */
	public void removeTraverseListener(final TraverseListener listener) {
		browser.removeTraverseListener(listener);
	}

	/**
	 * @param color
	 * @see org.eclipse.swt.widgets.Control#setBackground(org.eclipse.swt.graphics.Color)
	 */
	public void setBackground(final Color color) {
		browser.setBackground(color);
		execute(new Command() {

			@Override
			public String getCommandIdentifier() {
				return "set_background_internal";
			}

			@Override
			public String getCommand() {
				String hexValue = color != null ? "#" +ColorConverter
						.convertRgbToHex(color.getRGB()) : "";
				return "document.getElementById(\'cke_editor1_arialbl\').nextSibling.style.backgroundColor = \'"
						+ hexValue + "\';";
			}
		});
	}

	/**
	 * @param image
	 * @see org.eclipse.swt.widgets.Control#setBackgroundImage(org.eclipse.swt.graphics.Image)
	 */
	public void setBackgroundImage(final Image image) {
		browser.setBackgroundImage(image);
	}

	/**
	 * @param mode
	 * @see org.eclipse.swt.widgets.Composite#setBackgroundMode(int)
	 */
	public void setBackgroundMode(final int mode) {
		browser.setBackgroundMode(mode);
	}

	/**
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @see org.eclipse.swt.widgets.Control#setBounds(int, int, int, int)
	 */
	public void setBounds(final int x, final int y, final int width,
			final int height) {
		browser.setBounds(x, y, width, height);
	}

	/**
	 * @param rect
	 * @see org.eclipse.swt.widgets.Control#setBounds(org.eclipse.swt.graphics.Rectangle)
	 */
	public void setBounds(final Rectangle rect) {
		browser.setBounds(rect);
	}

	/**
	 * @param capture
	 * @see org.eclipse.swt.widgets.Control#setCapture(boolean)
	 */
	public void setCapture(final boolean capture) {
		browser.setCapture(capture);
	}

	/**
	 * @param cursor
	 * @see org.eclipse.swt.widgets.Control#setCursor(org.eclipse.swt.graphics.Cursor)
	 */
	public void setCursor(final Cursor cursor) {
		browser.setCursor(cursor);
	}

	/**
	 * @param data
	 * @see org.eclipse.swt.widgets.Widget#setData(java.lang.Object)
	 */
	public void setData(final Object data) {
		browser.setData(data);
	}

	/**
	 * @param key
	 * @param value
	 * @see org.eclipse.swt.widgets.Widget#setData(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setData(final String key, final Object value) {
		browser.setData(key, value);
	}

	/**
	 * @param enabled
	 * @see org.eclipse.swt.widgets.Control#setEnabled(boolean)
	 */
	public void setEnabled(final boolean enabled) {
		browser.setEnabled(enabled);
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Composite#setFocus()
	 */
	public boolean setFocus() {
		boolean setFocus = browser.setFocus();
		browser.execute("integration.editor.focus();");
		return setFocus;
	}

	/**
	 * @param font
	 * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(final Font font) {
		browser.setFont(font);
	}

	/**
	 * @param color
	 * @see org.eclipse.swt.widgets.Control#setForeground(org.eclipse.swt.graphics.Color)
	 */
	public void setForeground(final Color color) {
		browser.setForeground(color);
	}

	/**
	 * Replaces the current content of the widget with the given html. For
	 * inserting html at the current selection use:
	 * 
	 * <pre>
	 * HtmlComposer.execute(&quot;integration.editor.insertHtml('myHtmlToInsert');&quot;);
	 * </pre>
	 * 
	 * @param html
	 */
	public void setHtml(String html) {
		SetHtmlCommand setHtmlCommand = new SetHtmlCommand();
		setHtmlCommand.setComposer(this);
		setHtmlCommand.setHtml(html);
		execute(setHtmlCommand);
	}

	/**
	 * @param layout
	 * @see org.eclipse.swt.widgets.Composite#setLayout(org.eclipse.swt.widgets.Layout)
	 */
	public void setLayout(final Layout layout) {
		browser.setLayout(layout);
	}

	/**
	 * @param layoutData
	 * @see org.eclipse.swt.widgets.Control#setLayoutData(java.lang.Object)
	 */
	public void setLayoutData(final Object layoutData) {
		browser.setLayoutData(layoutData);
	}

	/**
	 * @param defer
	 * @see org.eclipse.swt.widgets.Composite#setLayoutDeferred(boolean)
	 */
	public void setLayoutDeferred(final boolean defer) {
		browser.setLayoutDeferred(defer);
	}

	/**
	 * @param x
	 * @param y
	 * @see org.eclipse.swt.widgets.Control#setLocation(int, int)
	 */
	public void setLocation(final int x, final int y) {
		browser.setLocation(x, y);
	}

	/**
	 * @param location
	 * @see org.eclipse.swt.widgets.Control#setLocation(org.eclipse.swt.graphics.Point)
	 */
	public void setLocation(final Point location) {
		browser.setLocation(location);
	}

	/**
	 * @param menu
	 * @see org.eclipse.swt.widgets.Control#setMenu(org.eclipse.swt.widgets.Menu)
	 */
	public void setMenu(final Menu menu) {
		browser.setMenu(menu);
	}

	/**
	 * @param parent
	 * @return
	 * @see org.eclipse.swt.widgets.Control#setParent(org.eclipse.swt.widgets.Composite)
	 */
	public boolean setParent(final Composite parent) {
		return browser.setParent(parent);
	}

	/**
	 * @param redraw
	 * @see org.eclipse.swt.widgets.Control#setRedraw(boolean)
	 */
	public void setRedraw(final boolean redraw) {
		browser.setRedraw(redraw);
	}

	/**
	 * @param region
	 * @see org.eclipse.swt.widgets.Control#setRegion(org.eclipse.swt.graphics.Region)
	 */
	public void setRegion(final Region region) {
		browser.setRegion(region);
	}

	/**
	 * @param width
	 * @param height
	 * @see org.eclipse.swt.widgets.Control#setSize(int, int)
	 */
	public void setSize(final int width, final int height) {
		browser.setSize(width, height);
	}

	/**
	 * @param size
	 * @see org.eclipse.swt.widgets.Control#setSize(org.eclipse.swt.graphics.Point)
	 */
	public void setSize(final Point size) {
		browser.setSize(size);
	}

	/**
	 * @param tabList
	 * @see org.eclipse.swt.widgets.Composite#setTabList(org.eclipse.swt.widgets.Control[])
	 */
	public void setTabList(final Control[] tabList) {
		browser.setTabList(tabList);
	}

	/**
	 * @param visible
	 * @see org.eclipse.swt.widgets.Control#setVisible(boolean)
	 */
	public void setVisible(final boolean visible) {
		browser.setVisible(visible);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 * @see org.eclipse.swt.widgets.Control#toControl(int, int)
	 */
	public Point toControl(final int x, final int y) {
		return browser.toControl(x, y);
	}

	/**
	 * @param point
	 * @return
	 * @see org.eclipse.swt.widgets.Control#toControl(org.eclipse.swt.graphics.Point)
	 */
	public Point toControl(final Point point) {
		return browser.toControl(point);
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 * @see org.eclipse.swt.widgets.Control#toDisplay(int, int)
	 */
	public Point toDisplay(final int x, final int y) {
		return browser.toDisplay(x, y);
	}

	/**
	 * @param point
	 * @return
	 * @see org.eclipse.swt.widgets.Control#toDisplay(org.eclipse.swt.graphics.Point)
	 */
	public Point toDisplay(final Point point) {
		return browser.toDisplay(point);
	}

	/**
	 * @return
	 * @see org.eclipse.swt.widgets.Widget#toString()
	 */
	@Override
	public String toString() {
		return browser.toString();
	}

	public void trackCommand(Command command) {
		trackedCommands.put(command.getCommandIdentifier(), command);
	}

	/**
	 * @param traversal
	 * @return
	 * @see org.eclipse.swt.widgets.Control#traverse(int)
	 */
	public boolean traverse(final int traversal) {
		return browser.traverse(traversal);
	}

	public void untrackCommand(Command command) {
		trackedCommands.remove(command.getCommandIdentifier());
	}

}
