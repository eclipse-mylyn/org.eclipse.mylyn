/*******************************************************************************
 * Copyright (c) 2012, 2014 Ericsson
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - primary API and implementation
 *     Git Hub, Inc. - fixes for bug 354570
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.CommonImageManger;
import org.eclipse.mylyn.internal.reviews.ui.Messages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileVersion;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.IUser;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.widgets.Display;

/**
 * Base support for reviews labels.
 *
 * @author Miles Parker
 * @author Steffen Pingel
 * @author Kevin Sawicki
 * @author Lei Zhu
 */
public abstract class ReviewsLabelProvider extends TableStyledLabelProvider {

	private static final int TOOLTIP_CHAR_WIDTH = 100;

	static final int LINE_NUMBER_WIDTH = 4;

	static final int MAXIMUM_COMMENT_LENGTH = 300;

	static final SimpleDateFormat COMMENT_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm a"); //$NON-NLS-1$

	static final SimpleDateFormat EXTENDED_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z"); //$NON-NLS-1$

	public final static Styler AUTHOR_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.font = JFaceResources.getTextFont();
			textStyle.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
		}
	};

	public final static Styler COMMENT_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.font = JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT);
		}
	};

	public final static Styler COMMENT_DATE_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
			textStyle.font = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			textStyle.foreground = colorRegistry.get(JFacePreferences.DECORATIONS_COLOR);
		}
	};

	public final static Styler LINE_NUMBER_STYLE = new Styler() {
		@Override
		public void applyStyles(TextStyle textStyle) {
			ColorRegistry colorRegistry = JFaceResources.getColorRegistry();
			textStyle.foreground = colorRegistry.get(JFacePreferences.COUNTER_COLOR);
			textStyle.font = JFaceResources.getFontRegistry().get(JFaceResources.TEXT_FONT);
		}
	};

	public static final TableColumnProvider ITEMS_COLUMN = new TableColumnProvider(Messages.ReviewsLabelProvider_Item,
			80, 800, true) {

		CommonImageManger imageManager = new CommonImageManger();

		@Override
		public Image getImage(Object element) {
			if (element instanceof IReview) {
				return imageManager.getFileImage("review"); //$NON-NLS-1$
			}
			if (element instanceof IReviewItemSet) {
				return CommonImages.getImage(ReviewsImages.CHANGE_LOG);
			}
			if (element instanceof IReviewItem item) {
				Image image = imageManager.getFileImage(item.getName());
				if (element instanceof IFileItem) {
					ImageDescriptor baseImage = ImageDescriptor.createFromImage(image);
					IFileItem fileItem = (IFileItem) element;
					IFileVersion base = fileItem.getBase();
					IFileVersion target = fileItem.getTarget();
					if (base != null && target != null) {
						if (base.getPath() == null && target.getPath() != null) {
							ImageDescriptor overlay = ReviewsImages.OVERLAY_ADDED;
							image = CommonImages.getImageWithOverlay(baseImage, overlay, false, false);
						} else if (base.getPath() != null && target.getPath() == null) {
							ImageDescriptor overlay = ReviewsImages.OVERLAY_REMOVED;
							image = CommonImages.getImageWithOverlay(baseImage, overlay, false, false);
						} else if (base.getPath() != null && target.getPath() != null) {
							if (!base.getPath().equals(target.getPath())) {
								ImageDescriptor overlay = ReviewsImages.OVERLAY_RENAMED;
								image = CommonImages.getImageWithOverlay(baseImage, overlay, false, false);
							}
						}
					}
				}
				return image;
			}
			if (element instanceof IComment comment) {
				//See https://bugs.eclipse.org/bugs/show_bug.cgi?id=334967#c16
				//			IComment comment = (IComment) element;
				//			if (StringUtils.startsWith(comment.getAuthor().getDisplayName(), "Hudson")) {
				//				return CommonImages.getImage(ReviewsImages.SERVER);
				//			}
				if (comment.isMine()) {
					return CommonImages.getImage(CommonImages.PERSON_ME);
				}
				return CommonImages.getImage(CommonImages.PERSON);
			}
			if (element instanceof GlobalCommentsNode) {
				return CommonImages.getImage(TasksUiImages.TASK);
			}
			if (element instanceof ILocation) {
				return CommonImages.getImage(ReviewsImages.REVIEW_QUOTE);
			}
			return null;
		};

		@Override
		public String getText(Object element) {
			if (element instanceof GlobalCommentsNode) {
				return org.eclipse.mylyn.internal.reviews.ui.Messages.Reviews_GeneralCommentsText;
			}
			if (element instanceof RetrievingContentsNode) {
				return org.eclipse.mylyn.reviews.ui.spi.editor.Messages.Reviews_RetrievingContents;
			}
			if (element instanceof Collection) {
				Collection<?> collection = (Collection<?>) element;
				if (collection.size() == 1) {
					return getText(collection.iterator().next());
				}
			}
			if (element instanceof IReview review) {
				return NLS.bind(Messages.ReviewsLabelProvider_Change_X, review.getId());
			}
			if (element instanceof IFileVersion version) {
				String text = getText(version.getFile());
				text += version.getName();
				return text;
			}
			if (element instanceof IFileItem) {
				//Note, we want platform independent separator here.
				return StringUtils.substringAfterLast(((IFileItem) element).getName(), "/"); //$NON-NLS-1$
			}
			if (element instanceof IReviewItem) {
				return ((IReviewItem) element).getName();
			}
			if (element instanceof IComment comment) {
				String desc = comment.getDescription();
				//desc = StringUtils.normalizeSpace(desc);
				return desc;
			}
			if (element instanceof IUser) {
				String displayName = ((IUser) element).getDisplayName();
				if (!StringUtils.isEmpty(displayName)) {
					return displayName;
				}
				return Messages.ReviewsLabelProvider_Unknown;
			}
			if (element instanceof Date) {
				return NLS.bind(Messages.ReviewsLabelProvider_X_ago,
						DateUtil.getRelativeDuration(System.currentTimeMillis() - ((Date) element).getTime()));
			}
			if (element instanceof ILineLocation) {
				int min = ((ILineLocation) element).getRangeMin();
				int max = ((ILineLocation) element).getRangeMax();
				String text = Integer.toString(min);
				if (min != max) {
					text += "-" + max; //$NON-NLS-1$
				}
				return text;
			}
			return super.getText(element);
		}

		@Override
		public StyledString getStyledText(Object element) {
			Styler styler = null;
			if (element instanceof IComment || element instanceof RetrievingContentsNode) {
				styler = COMMENT_STYLE;
			}
			StyledString styledString = new StyledString(getText(element), styler);
			if (element instanceof GlobalCommentsNode) {
				element = ((GlobalCommentsNode) element).getReview();
			}
			addCommentContainerStatsStyle(element, styledString);
			addCommentLineNumberStyle(element, styledString);
			addFilePathStyle(element, styledString);
			return styledString;
		}

		@Override
		public String getToolTipText(Object element) {
			if (element instanceof IComment comment) {
				String desc = comment.getDescription();
				desc = StringUtils.replace(desc, "\r\n", "\n"); //$NON-NLS-1$ //$NON-NLS-2$
				String[] lines = desc.split("\n"); //$NON-NLS-1$
				List<String> seperated = new ArrayList<>();
				for (String line : lines) {
					String newLine = ""; //$NON-NLS-1$
					String[] splitByWholeSeparator = StringUtils.splitByWholeSeparator(line, " "); //$NON-NLS-1$
					int count = 0;
					for (String word : splitByWholeSeparator) {
						count += word.length();
						if (count > TOOLTIP_CHAR_WIDTH) {
							seperated.add(newLine);
							newLine = word;
							count = word.length();
						} else {
							if (count > word.length()) {
								newLine += " "; //$NON-NLS-1$
							}
							newLine += word;
						}
					}
					seperated.add(newLine);
				}
				return StringUtils.join(seperated, "\n"); //$NON-NLS-1$
			}
			if (element instanceof IFileItem fileItem) {
				IFileVersion base = fileItem.getBase();
				IFileVersion target = fileItem.getTarget();
				if (target != null) {
					if (target.getPath() != null && base.getPath() == null) {
						return NLS.bind(Messages.ReviewsLabelProvider_X_Revision_Y, target.getPath(),
								target.getDescription());
					} else if (target.getPath() == null && base.getPath() != null) {
						return NLS.bind(Messages.ReviewsLabelProvider_X_Revision_Y, base.getPath(),
								target.getDescription());
					} else if (target.getPath() != null && !target.getPath().equals(base.getPath())) {
						return NLS.bind(Messages.ReviewsLabelProvider_X_renamed_from_Y_Z,
								new Object[] { target.getPath(), base.getPath(), target.getDescription() });
					} else {
						return NLS.bind(Messages.ReviewsLabelProvider_X_Revision_Y,
								target.getPath() == null ? target.getName() : target.getPath(),
								target.getDescription());
					}
				}
			}
			if (element instanceof IUser user) {
				String text = user.getDisplayName();
				if (!StringUtils.isEmpty(user.getEmail())) {
					text += NLS.bind(Messages.ReviewsLabelProvider_Bracket_X_bracket, user.getEmail());
				}
				return text;
			}
			if (element instanceof Date) {
				return EXTENDED_DATE_FORMAT.format((Date) element);
			}
			return null;
		}

		@Override
		public boolean isStyled() {
			return true;
		};
	};

	public static final TableColumnProvider ARTIFACT_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN,
			Messages.ReviewsLabelProvider_Artifact, 50, 400, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IFileItem) {
				return element;
			}
			if (element instanceof IFileVersion) {
				return ((IFileVersion) element).getFile();
			}
			if (element instanceof IComment comment) {
				return adapt(comment.getItem());
			}
			if (element instanceof IReview) {
				return new GlobalCommentsNode((IReview) element);
			}
			return null;
		}

		@Override
		public StyledString getStyledText(Object element) {
			//We don't want to include stats for this usage
			StyledString styledString = new StyledString(getText(element));
			addCommentLineNumberStyle(element, styledString);
			addFilePathStyle(adapt(element), styledString);
			return styledString;
		}

		@Override
		public boolean isStyled() {
			return true;
		};
	};

	public static final TableColumnProvider COMMENTS_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN,
			Messages.ReviewsLabelProvider_Comment, 50, 400, true) {
		@Override
		public Object adapt(Object element) {
			if (element instanceof IComment) {
				return element;
			}
			return null;
		};

		@Override
		public StyledString getStyledText(Object element) {
			return new StyledString(getText(element), COMMENT_STYLE);
		}

		@Override
		public boolean isStyled() {
			return true;
		};
	};

	public static final TableColumnProvider AUTHORS_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN,
			Messages.ReviewsLabelProvider_Author, 10, 120, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IComment comment) {
				return comment.getAuthor();
			}
			if (element instanceof IReviewItem item) {
				return item.getAddedBy();
			}
			if (element instanceof IReview item) {
				return item.getOwner();
			}
			return null;
		}
	};

	public static final TableColumnProvider DATE_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN,
			Messages.ReviewsLabelProvider_Last_Change, 10, 120, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IDated item) {
				if (item.getModificationDate() != null) {
					return item.getModificationDate();
				}
				if (item.getCreationDate() != null) {
					return item.getCreationDate();
				}
			}
			if (element instanceof IFileVersion item) {
				return item.getDescription();
			}
			return null;
		};
	};

	public static final TableColumnProvider SINGLE_COLUMN = new TableColumnProvider(Messages.ReviewsLabelProvider_Items,
			10, 120, false) {

		@Override
		public Image getImage(Object element) {
			return ITEMS_COLUMN.getImage(element);
		};

		@Override
		public String getText(Object element) {
			if (element instanceof GlobalCommentsNode) {
				return org.eclipse.mylyn.internal.reviews.ui.Messages.Reviews_GeneralCommentsText;
			}
			if (element instanceof Collection) {
				Collection<?> collection = (Collection<?>) element;
				if (collection.size() == 1) {
					return getText(collection.iterator().next());
				}
			}
			if (element instanceof IReview review) {
				return NLS.bind(Messages.ReviewsLabelProvider_Change_X, review.getId());
			}
			if (element instanceof IFileItem file) {
				String fileName = StringUtils.substringAfterLast(file.getName(), File.separator);
				return fileName;
			}
			if (element instanceof IReviewItem) {
				return ((IReviewItem) element).getName();
			}
			if (element instanceof IComment comment) {
				String desc = comment.getDescription();
				return desc;
			}
			if (element instanceof IUser user) {
				return user.getDisplayName();
			}
			if (element instanceof Date) {
				return COMMENT_DATE_FORMAT.format(element);
			}
			if (element instanceof ILineLocation) {
				int min = ((ILineLocation) element).getRangeMin();
				int max = ((ILineLocation) element).getRangeMax();
				String text = min + ""; //$NON-NLS-1$
				if (min != max) {
					text += "-" + max; //$NON-NLS-1$
				}
				return text;
			}
			return super.getText(element);
		}

		@Override
		public StyledString getStyledText(Object element) {
			String text = getText(element);
			StyledString styledString = new StyledString();
			if (text != null) {
				if (element instanceof IComment comment) {
					String commentText = text;
					int textLength = 0;
					String authorText = ""; //$NON-NLS-1$
					if (comment.getAuthor() != null) {
						authorText = getText(comment.getAuthor());
						textLength += authorText.length();
					}
					int descSpaceRemaining = MAXIMUM_COMMENT_LENGTH - textLength;
					if (commentText.length() > descSpaceRemaining + 3) { //Account for ellipses
						commentText = NLS.bind(Messages.ReviewsLabelProvider_X_dot_dot_dot,
								commentText.substring(0, descSpaceRemaining - 3));
					}
					commentText = StringUtils.rightPad(commentText, MAXIMUM_COMMENT_LENGTH - textLength);

					styledString.append(authorText + " ", AUTHOR_STYLE); //$NON-NLS-1$
					styledString.append(commentText + " ", COMMENT_STYLE); //$NON-NLS-1$
					styledString.append(getText(comment.getCreationDate()) + " ", COMMENT_DATE_STYLE); //$NON-NLS-1$
				} else {
					styledString.append(text);
				}
				if (element instanceof GlobalCommentsNode) {
					element = ((GlobalCommentsNode) element).getReview();
				}
				addCommentContainerStatsStyle(element, styledString);
				if (element instanceof IFileItem) {
					IReviewItem item = (IReviewItem) element;
					styledString.append("  " + item.getName(), StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
				}
			} else {
				styledString.append(element.toString());
			}
			return styledString;
		}
	};

	private final boolean includeAuthors;

	public static class Flat extends ReviewsLabelProvider {
		TableColumnProvider[] columns = { ARTIFACT_COLUMN, COMMENTS_COLUMN, AUTHORS_COLUMN, DATE_COLUMN };

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public static class Tree extends ReviewsLabelProvider {
		TableColumnProvider[] columns = { ITEMS_COLUMN, AUTHORS_COLUMN, DATE_COLUMN };

		public Tree() {
			this(false);
		}

		public Tree(boolean includeAuthors) {
			super(includeAuthors);
		}

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public static class Single extends ReviewsLabelProvider {
		TableColumnProvider[] columns = { SINGLE_COLUMN };

		@Override
		public Image getImage(Object element) {
			return ITEMS_COLUMN.getImage(element);
		}

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public static class Simple extends ReviewsLabelProvider {
		TableColumnProvider[] columns = { SINGLE_COLUMN };

		@Override
		public Image getImage(Object element) {
			if (element instanceof IReviewItem) {
				return super.getImage(element);
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IReviewItem) {
				return ((IReviewItem) element).getName();
			}
			return super.getText(element);
		}

		@Override
		public StyledString getStyledText(Object element) {
			StyledString styledString = new StyledString(getText(element));
			addCommentContainerStatsStyle(element, styledString);
			return styledString;
		}

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public ReviewsLabelProvider() {
		this(false);
	}

	public ReviewsLabelProvider(boolean includeAuthors) {
		this.includeAuthors = includeAuthors;
	}

	@Override
	public String getText(Object element) {
		return ITEMS_COLUMN.getText(element);
	}

	@Override
	public StyledString getStyledText(Object element) {
		return getStyledText(element, true);
	}

	public StyledString getStyledText(Object element, boolean includeCommentStats) {
		Styler styler = null;
		if (element instanceof IComment) {
			styler = COMMENT_STYLE;
		}
		StyledString styledString = new StyledString();
		if (includeAuthors) {
			addAuthorStyle(element, styledString);
		}
		styledString.append(getText(element), styler);
		if (element instanceof GlobalCommentsNode) {
			element = ((GlobalCommentsNode) element).getReview();
		}
		if (includeCommentStats) {
			addCommentContainerStatsStyle(element, styledString);
		}
		addCommentLineNumberStyle(element, styledString);
		addFilePathStyle(element, styledString);
		return styledString;
	}

	@Override
	public Image getImage(Object element) {
		return ITEMS_COLUMN.getImage(element);
	}

	public static void addAuthorStyle(Object element, StyledString styledString) {
		if (element instanceof IComment comment) {
			String name = comment.getAuthor().getDisplayName();
			String firstName = StringUtils.substringBefore(name, " "); //$NON-NLS-1$
			styledString.append(firstName + ": ", AUTHOR_STYLE); //$NON-NLS-1$
		}
	}

	public static void addCommentLineNumberStyle(Object element, StyledString styledString) {
		if (element instanceof IComment comment) {
			String[] locationStrings = new String[comment.getLocations().size()];
			int index = 0;
			for (ILocation location : comment.getLocations()) {
				locationStrings[index++] = ITEMS_COLUMN.getText(location);
			}
			String locationString = StringUtils.join(locationStrings, ", "); //$NON-NLS-1$
			styledString.append(" " + locationString, LINE_NUMBER_STYLE); //$NON-NLS-1$
		}
	}

	public static void addFilePathStyle(Object element, StyledString styledString) {
		if (element instanceof IFileItem) {
			IReviewItem item = (IReviewItem) element;
			styledString.append("  " + item.getName(), StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
		}
	}

	public static void addCommentContainerStatsStyle(Object element, StyledString styledString) {
		if (element instanceof ICommentContainer container) {
			String statsText = getStatsText(container);
			styledString.append(statsText, StyledString.DECORATIONS_STYLER);
		}
	}

	public static String getIndexText(IComment comment) {
		long index = comment.getIndex();
		return index < Long.MAX_VALUE ? Long.toString(index) : ""; //$NON-NLS-1$
	}

	public static String getStatsText(ICommentContainer container) {
		if (container instanceof IReviewItemSet && ((IReviewItemSet) container).getItems().size() == 0) {
			return Messages.ReviewsLabelProvider_Question_mark;
		}
		List<? extends IComment> comments;
		if (container instanceof IReview) {
			comments = container.getComments();
		} else {
			comments = container.getAllComments();
		}
		int commentCount = comments.size();
		int draftCount = 0;
		for (IComment comment : comments) {
			if (comment.isDraft()) {
				draftCount += 1;
			}
		}
		commentCount -= draftCount;
		String statsText = ""; //$NON-NLS-1$
		if (commentCount > 0 && draftCount > 0) {
			statsText = NLS.bind(Messages.ReviewsLabelProvider_X_comments_Y_drafts, commentCount, draftCount);
		} else if (commentCount > 0) {
			statsText = NLS.bind(Messages.ReviewsLabelProvider_X_comments, commentCount);
		} else if (draftCount > 0) {
			statsText = NLS.bind(Messages.ReviewsLabelProvider_X_drafts, draftCount);
		}
		return statsText;
	}

	@Override
	public abstract TableColumnProvider[] getColumnProviders();
}
