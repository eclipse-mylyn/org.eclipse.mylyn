/*
 * Created on 13.11.2007
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * @author Benjamin Pasero
 * @author Mik Kersten
 */
public abstract class AbstractNotificationPopup extends Window {

	private static final int DEFAULT_WIDTH = 400;

	private static final int DEFAULT_HEIGHT = 100;

	protected LocalResourceManager resources;

	private NotificationPopupColors color;

	private final Display display;

	private Shell shell;

	private Region lastUsedRegion;

	public AbstractNotificationPopup(Display display) {
		this(display, SWT.NO_TRIM | SWT.ON_TOP);
	}

	public AbstractNotificationPopup(Display display, int style) {
		super(new Shell(display));
		setShellStyle(style);

		this.display = display;
		resources = new LocalResourceManager(JFaceResources.getResources());
		initResources();
	}

	protected abstract String getPopupShellTitle();

	/**
	 * Override to populate with notifications.
	 * @param parent
	 */
	protected void createContentArea(Composite parent) {
		// empty by default
	}
	
	/**
	 * Override to customize the title bar
	 */
	protected void createTitleArea(Composite parent) {
		((GridData) parent.getLayoutData()).heightHint = 24;

		Label titleImageLabel = new Label(parent, SWT.NONE);
		Image[] images = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getImages();
		// TODO: fix hardcoded reference
		titleImageLabel.setImage(images[3]);

		Label titleTextLabel = new Label(parent, SWT.NONE);
		titleTextLabel.setText(getPopupShellTitle());
		titleTextLabel.setFont(TaskListColorsAndFonts.BOLD);
		titleTextLabel.setForeground(color.getTitleText());
		titleTextLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		titleTextLabel.setCursor(parent.getDisplay().getSystemCursor(SWT.CURSOR_HAND));

		Label button = new Label(parent, SWT.NONE);
		button.setImage(TasksUiImages.getImage(TasksUiImages.NOTIFICATION_CLOSE));

		button.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				// ignore
			}

			public void mouseDown(MouseEvent e) {
				// ignore
			}

			public void mouseUp(MouseEvent e) {
				close();
			}

		});
	}
	
	private void initResources() {
		color = new NotificationPopupColors(display, resources);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		shell = newShell;
		newShell.setBackground(color.getBorder());
	}

	public void create() {
		super.create();
		addRegion(shell);
	}

	private void addRegion(Shell shell) {
		Region region = new Region();
		Point s = shell.getSize();

		/* Add entire Shell */
		region.add(0, 0, s.x, s.y);

		/* Subtract Top-Left Corner */
		region.subtract(0, 0, 5, 1);
		region.subtract(0, 1, 3, 1);
		region.subtract(0, 2, 2, 1);
		region.subtract(0, 3, 1, 1);
		region.subtract(0, 4, 1, 1);

		/* Subtract Top-Right Corner */
		region.subtract(s.x - 5, 0, 5, 1);
		region.subtract(s.x - 3, 1, 3, 1);
		region.subtract(s.x - 2, 2, 2, 1);
		region.subtract(s.x - 1, 3, 1, 1);
		region.subtract(s.x - 1, 4, 1, 1);

		/* Dispose old first */
		if (shell.getRegion() != null)
			shell.getRegion().dispose();

		/* Apply Region */
		shell.setRegion(region);

		/* Remember to dispose later */
		lastUsedRegion = region;
	}

	protected Control createContents(Composite parent) {
		((GridLayout) parent.getLayout()).marginWidth = 1;
		((GridLayout) parent.getLayout()).marginHeight = 1;

		/* Outer Composite holding the controls */
		final Composite outerCircle = new Composite(parent, SWT.NO_FOCUS);
		outerCircle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;

		outerCircle.setLayout(layout);

		/* Title area containing label and close button */
		final Composite titleCircle = new Composite(outerCircle, SWT.NO_FOCUS);
		titleCircle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		titleCircle.setBackgroundMode(SWT.INHERIT_FORCE);

		layout = new GridLayout(4, false);
		layout.marginWidth = 3;
		layout.marginHeight = 0;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 3;

		titleCircle.setLayout(layout);

		titleCircle.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle clArea = titleCircle.getClientArea();
				Image newBGImage = new Image(titleCircle.getDisplay(), clArea.width, clArea.height);
				GC gc = new GC(newBGImage);

				/* Gradient */
				drawGradient(gc, clArea);

				/* Fix Region Shape */
				fixRegion(gc, clArea);

				gc.dispose();

				Image oldBGImage = titleCircle.getBackgroundImage();
				titleCircle.setBackgroundImage(newBGImage);

				if (oldBGImage != null)
					oldBGImage.dispose();
			}

			private void drawGradient(GC gc, Rectangle clArea) {
				gc.setForeground(color.getGradientBegin());
				gc.setBackground(color.getGradientEnd());
				gc.fillGradientRectangle(clArea.x, clArea.y, clArea.width, clArea.height, true);
			}

			private void fixRegion(GC gc, Rectangle clArea) {
				gc.setForeground(color.getBorder());

				/* Fill Top Left */
				gc.drawPoint(2, 0);
				gc.drawPoint(3, 0);
				gc.drawPoint(1, 1);
				gc.drawPoint(0, 2);
				gc.drawPoint(0, 3);

				/* Fill Top Right */
				gc.drawPoint(clArea.width - 4, 0);
				gc.drawPoint(clArea.width - 3, 0);
				gc.drawPoint(clArea.width - 2, 1);
				gc.drawPoint(clArea.width - 1, 2);
				gc.drawPoint(clArea.width - 1, 3);
			}
		});

		/* Create Title Area */
		createTitleArea(titleCircle);

		/* Outer composite to hold content controlls */
		Composite outerContentCircle = new Composite(outerCircle, SWT.NONE);

		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		outerContentCircle.setLayout(layout);
		outerContentCircle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outerContentCircle.setBackground(outerCircle.getBackground());

		/* Middle composite to show a 1px black line around the content controls */
		Composite middleContentCircle = new Composite(outerContentCircle, SWT.NO_FOCUS);

		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.marginTop = 1;

		middleContentCircle.setLayout(layout);
		middleContentCircle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		middleContentCircle.setBackground(color.getBorder());

		/* Inner composite containing the content controls */
		Composite innerContentCircle = new Composite(middleContentCircle, SWT.NO_FOCUS);
		innerContentCircle.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 5;

		innerContentCircle.setLayout(layout);

		((GridLayout) innerContentCircle.getLayout()).marginLeft = 5;
		((GridLayout) innerContentCircle.getLayout()).marginRight = 2;
		innerContentCircle.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		/* Content Area */
		createContentArea(innerContentCircle);

		return outerCircle;
	}
	
	protected void initializeBounds() {
		Rectangle clArea = getPrimaryClientArea();
		int initialHeight = shell.computeSize(DEFAULT_WIDTH, SWT.DEFAULT).y;

		Point size = new Point(DEFAULT_WIDTH, initialHeight + DEFAULT_HEIGHT);

		shell.setLocation(clArea.width + clArea.x - size.x, clArea.height + clArea.y - size.y);
		shell.setSize(size);
	}

	private Rectangle getPrimaryClientArea() {
		Monitor primaryMonitor = shell.getDisplay().getPrimaryMonitor();
		return (primaryMonitor != null) ? primaryMonitor.getClientArea() : shell.getDisplay().getClientArea();
	}

	public boolean close() {
		resources.dispose();
		if (lastUsedRegion != null)
			lastUsedRegion.dispose();

		return super.close();
	}
}