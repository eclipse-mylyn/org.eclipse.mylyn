/*******************************************************************************
 * Copyright (c) 2013, 2015 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the label provider
 *   Marc-Andre Laperle - Add Topic to dashboard
 *   Marc-Andre Laperle - Add Status to dashboard
 ******************************************************************************/

package org.eclipse.mylyn.gerrit.dashboard.ui.internal.model;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.mylyn.gerrit.dashboard.core.GerritTask;
import org.eclipse.mylyn.gerrit.dashboard.ui.GerritUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * This class implements the implementation of the Dashboard-Gerrit UI view label provider.
 * 
 * @author Jacques Bouthillier
 * @version $Revision: 1.0 $
 */
public class ReviewTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

	// ------------------------------------------------------------------------
	// Constants
	// ------------------------------------------------------------------------

	private final String EMPTY_STRING = ""; //$NON-NLS-1$

	// +2 Names of images used to represent review-checked
	public static final String CHECKED_IMAGE = "greenCheck.png"; //$NON-NLS-1$

	// -2 Names of images used to represent review-not OK
	public static final String NOT_OK_IMAGE = "redNot.png"; //$NON-NLS-1$

	// -1
	public static final String MINUS_ONE = "minusOne.png"; //$NON-NLS-1$

	// +1
	public static final String PLUS_ONE = "plusOne.png"; //$NON-NLS-1$

	// Names of images used to represent STAR FILLED
	public static final String STAR_FILLED = "starFilled.gif"; //$NON-NLS-1$

	// Names of images used to represent STAR OPEN
	public static final String STAR_OPEN = "starOpen.gif"; //$NON-NLS-1$

	// Value stored to define the state of the review item.
	public static final int NOT_OK_IMAGE_STATE = -2;

	public static final int CHECKED_IMAGE_STATE = 2;

	// Constant for the column with colors: CR, IC and V
	private static Display fDisplay = Display.getCurrent();

	private static Color RED = fDisplay.getSystemColor(SWT.COLOR_RED);

	private static Color GREEN = fDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);

	//Color used depending on the review state
	private static Color DEFAULT_COLOR = fDisplay.getSystemColor(SWT.COLOR_LIST_BACKGROUND);

//	private static Color INCOMING_COLOR = fDisplay.getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
	private static Color INCOMING_COLOR = fDisplay.getSystemColor(SWT.COLOR_WHITE);

	private static Color CLOSED_COLOR = fDisplay.getSystemColor(SWT.COLOR_WHITE);

//	private static Color INCOMING_COLOR = fDisplay.getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
//	private static Color CLOSED_COLOR = fDisplay.getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);

	// For the images
	private static ImageRegistry fImageRegistry = new ImageRegistry();

	/**
	 * Note: An image registry owns all of the image objects registered with it, and automatically disposes of them the SWT Display is
	 * disposed.
	 */
	static {

		String iconPath = "icons/view16/"; //$NON-NLS-1$

		fImageRegistry.put(CHECKED_IMAGE, GerritUi.getImageDescriptor(iconPath + CHECKED_IMAGE));

		fImageRegistry.put(NOT_OK_IMAGE, GerritUi.getImageDescriptor(iconPath + NOT_OK_IMAGE));

		fImageRegistry.put(MINUS_ONE, GerritUi.getImageDescriptor(iconPath + MINUS_ONE));

		fImageRegistry.put(PLUS_ONE, GerritUi.getImageDescriptor(iconPath + PLUS_ONE));

		fImageRegistry.put(STAR_FILLED, GerritUi.getImageDescriptor(iconPath + STAR_FILLED));

		fImageRegistry.put(STAR_OPEN, GerritUi.getImageDescriptor(iconPath + STAR_OPEN));
	}

	// ------------------------------------------------------------------------
	// Constructors
	// ------------------------------------------------------------------------
	public ReviewTableLabelProvider() {
	}

	// ------------------------------------------------------------------------
	// Methods
	// ------------------------------------------------------------------------

	/**
	 * Return an image representing the state of the object
	 * 
	 * @param int
	 *            aState
	 * @return Image
	 */
	private Image getReviewStateImage(int aState) {
		switch (aState) {
			case 2:
				return fImageRegistry.get(CHECKED_IMAGE);
			case 1:
				return fImageRegistry.get(PLUS_ONE);
			case 0:
				break;
			case -1:
				return fImageRegistry.get(MINUS_ONE);
			case -2:
				return fImageRegistry.get(NOT_OK_IMAGE);
			default:
				break;
		}
		return null;
	}

	/**
	 * Return an image representing the state of the object
	 * 
	 * @param int
	 *            aState
	 * @return Image
	 */
	private Image getVerifyStateImage(int aState) {
		switch (aState) {
			case 2:
			case 1:
				return fImageRegistry.get(CHECKED_IMAGE);
			case 0:
				break;
			case -1:
			case -2:
				return fImageRegistry.get(NOT_OK_IMAGE);
			default:
				break;
		}
		return null;
	}

	/**
	 * Return an image representing the state of the ID object
	 * 
	 * @param Boolean
	 *            aState
	 * @return Image
	 */
	private Image getReviewId(Boolean aState) {
		if (aState) {
			// True means the star is filled ??
			return fImageRegistry.get(STAR_FILLED);
		} else {
			//
			return fImageRegistry.get(STAR_OPEN);
		}
	}

	/**
	 * Return the text associated to the column
	 * 
	 * @param Object
	 *            structure of the table
	 * @param int
	 *            column index
	 * @return String text associated to the column
	 */
	@Override
	@SuppressWarnings("restriction")
	public String getColumnText(Object aObj, int aIndex) {
		// GerritPlugin.Ftracer.traceWarning("getColumnText object: " + aObj
		// + "\tcolumn: " + aIndex);
		if (aObj instanceof GerritTask reviewSummary) {
			//			String value = null;
			switch (aIndex) {
				case 0:
					return reviewSummary.getAttribute(GerritTask.IS_STARRED); // Needed for the sorter
				case 1:
					return reviewSummary.getAttribute(GerritTask.SHORT_CHANGE_ID);
				case 2:
					return reviewSummary.getAttribute(GerritTask.SUBJECT);
				case 3:
					String attribute = reviewSummary.getAttribute(GerritTask.STATUS);
					return attribute;
				case 4:
					return reviewSummary.getAttribute(GerritTask.OWNER);
				case 5:
					return reviewSummary.getAttribute(GerritTask.PROJECT);
				case 6:
					String branch = reviewSummary.getAttribute(GerritTask.BRANCH);
					String topic = reviewSummary.getAttribute(GerritTask.TOPIC);
					if (topic != null && !topic.isEmpty()) {
						branch += " (" + reviewSummary.getAttribute(GerritTask.TOPIC) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
					}
					return branch;
				case 7:
					return reviewSummary.getAttributeAsDate(GerritTask.DATE_MODIFICATION);
//			case 8:
//				value = reviewSummary.getAttribute(GerritTask.REVIEW_STATE);
//				if (null != value && !value.equals(EMPTY_STRING)) {
//					return formatValue (value);
//				}
//			case 9:
//				value = reviewSummary.getAttribute(GerritTask.IS_IPCLEAN);
//				if (null != value && !value.equals(EMPTY_STRING)) {
//					return formatValue (value);
//				}
//                return EMPTY_STRING;
//			case 10:
//				value = reviewSummary.getAttribute(GerritTask.VERIFY_STATE);
//				if (null != value && !value.equals(EMPTY_STRING)) {
//					return formatValue (value);
//				}
//                return EMPTY_STRING;
				default:
					return EMPTY_STRING;
			}
		}
		return EMPTY_STRING;
	}

//	/**
//	 * Format the numbering value to display
//	 * @param aSt
//	 * @return String
//	 */
//	private String formatValue (String aSt) {
//		int val = aSt.equals("")? 0 : Integer.parseInt(aSt, 10);
//		if ( val > 0) {
//			String st = "+" + aSt;
//			return st;
//		}
//		return aSt;
//
//	}

	/**
	 * Return the image associated to the column
	 * 
	 * @param Object
	 *            structure of the table
	 * @param int
	 *            column index
	 * @return Image Image according to the selected column
	 */
	@Override
	@SuppressWarnings("restriction")
	public Image getColumnImage(Object aObj, int aIndex) {
		String value = null;
		if (aObj instanceof GerritTask reviewSummary) {
			switch (aIndex) {
				case 0:
					value = reviewSummary.getAttribute(GerritTask.IS_STARRED);
					if (null != value && !value.equals(EMPTY_STRING)) {
						return getReviewId(Boolean.valueOf(value.toLowerCase()));
					}
					break;
				case 8:
					value = reviewSummary.getAttribute(GerritTask.REVIEW_STATE);

					if (null != value && !value.equals(EMPTY_STRING)) {
						int val = Integer.parseInt(value);
						return getReviewStateImage(val);
					}
					break;
				case 9:
					value = reviewSummary.getAttribute(GerritTask.VERIFY_STATE);

					if (null != value && !value.equals(EMPTY_STRING)) {
						int val = Integer.parseInt(value);
						return getVerifyStateImage(val);
					}
					break;
			}
		}
		return null;
	}

	/**
	 * Adjust the column color
	 * 
	 * @param Object
	 *            ReviewTableListItem
	 * @param int
	 *            columnIndex
	 */
	@Override
	public Color getForeground(Object aElement, int aColumnIndex) {
		if (aElement instanceof GerritTask) {
			int value = 0;
			if (aColumnIndex == ReviewTableDefinition.CR.ordinal()
//					|| aColumnIndex == ReviewTableDefinition.IC.ordinal()
					|| aColumnIndex == ReviewTableDefinition.VERIFY.ordinal()) {
				switch (aColumnIndex) {
					case 7: // ReviewTableDefinition.CR.ordinal():
//					st = item.getAttribute(GerritTask.REVIEW_STATE);
//					if (st != null) {
//						value = st.equals(EMPTY_STRING) ? 0 : Integer
//								.parseInt(st);
//					}
						break;
					case 8: // ReviewTableDefinition.IC.ordinal():
//					st = item.getAttribute(GerritTask.IS_IPCLEAN);
//					if (st != null) {
//						value = st.equals(EMPTY_STRING) ? 0 : Integer
//								.parseInt(st);
//					}
//					break;
//				case 9: // ReviewTableDefinition.VERIFY.ordinal():
//					st = item.getAttribute(GerritTask.VERIFY_STATE);
//					if (st != null) {
//						value = st.equals(EMPTY_STRING) ? 0 : Integer
//								.parseInt(st);
//					}
						break;
				}
				if (value < 0) {
					return RED;
				} else if (value > 0) {
					return GREEN;
				}
			}
		}
		return null;
	}

	@Override
	@SuppressWarnings("restriction")
	public Color getBackground(Object aElement, int aColumnIndex) {
		// GerritUi.Ftracer.traceInfo("getBackground column : " +
		// aColumnIndex +
		// " ]: "+ aElement );
		if (aElement instanceof GerritTask item) {
			//
			// To modify when we can verify the review state
			String state = item.getAttribute(GerritTask.IS_STARRED);
			if (state != null) {
				if (state.equals(Boolean.toString(true))) {
					return INCOMING_COLOR;

				} else if (state.equals(Boolean.toString(false))) {
					return CLOSED_COLOR;
				}
			}
		}

		return DEFAULT_COLOR;
	}

}
