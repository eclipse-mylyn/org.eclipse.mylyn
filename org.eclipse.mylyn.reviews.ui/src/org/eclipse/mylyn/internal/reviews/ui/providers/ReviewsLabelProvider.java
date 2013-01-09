/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.mylyn.commons.core.DateUtil;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsImages;
import org.eclipse.mylyn.internal.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.IDated;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IFileRevision;
import org.eclipse.mylyn.reviews.core.model.ILineLocation;
import org.eclipse.mylyn.reviews.core.model.ILocation;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.ITaskReference;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;
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
 */
public abstract class ReviewsLabelProvider extends TableStyledLabelProvider {

	private static final String GLOBAL_COMMENTS_NAME = "Global";

	private static final int TOOLTIP_CHAR_WIDTH = 100;

	static final int LINE_NUMBER_WIDTH = 4;

	static final int MAXIMUM_COMMENT_LENGTH = 300;

	static final SimpleDateFormat COMMENT_DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm a");

	static final SimpleDateFormat EXTENDED_DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");

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

	public static final TableColumnProvider ITEMS_COLUMN = new TableColumnProvider("Item", 80, 800, true) {

		@Override
		public Image getImage(Object element) {
			if (element instanceof IReview) {
				return ReviewsUiPlugin.getDefault().getImageManager().getFileImage("review");
			}
			if (element instanceof IReviewItemSet) {
				return CommonImages.getImage(ReviewsImages.CHANGE_LOG);

			}
			if (element instanceof IReviewItem) {
				IReviewItem item = (IReviewItem) element;
				return ReviewsUiPlugin.getDefault().getImageManager().getFileImage(item.getName());
			}
			if (element instanceof IComment) {
				//See https://bugs.eclipse.org/bugs/show_bug.cgi?id=334967#c16
				//			IComment comment = (IComment) element;
				//			if (StringUtils.startsWith(comment.getAuthor().getDisplayName(), "Hudson")) {
				//				return CommonImages.getImage(ReviewsImages.SERVER);
				//			}
				//TODO: We'd like to return PERSON_ME if user, but need to figure out how to get that w/o creating too much coupling.
				return CommonImages.getImage(CommonImages.PERSON);
			}
			if (element instanceof GlobalCommentsNode) {
				return CommonImages.getImage(TasksUiImages.TASK);
			}
			return null;
		};

		@Override
		public String getText(Object element) {
			if (element instanceof GlobalCommentsNode) {
				return GLOBAL_COMMENTS_NAME;
			}
			if (element instanceof Collection) {
				Collection<?> collection = (Collection<?>) element;
				if (collection.size() == 1) {
					return getText(collection.iterator().next());
				}
			}
			if (element instanceof ITaskReference) {
				ITaskReference ref = (ITaskReference) element;
				return ref.getTaskId();
			}
			if (element instanceof IReview) {
				IReview review = (IReview) element;
				ITaskReference reviewTask = review.getReviewTask();
				if (reviewTask != null) {
					return getText(reviewTask);
				}
				return "Change " + review.getId();
			}
			if (element instanceof IFileRevision) {
				IFileRevision revision = (IFileRevision) element;
				String text = getText(revision.getFile());
				text += revision.getName();
				return text;
			}
			if (element instanceof IFileItem) {
				//Note, we want platform independent separator here.
				return StringUtils.substringAfterLast(((IFileItem) element).getName(), "/");
			}
			if (element instanceof IReviewItem) {
				return ((IReviewItem) element).getName();
			}
			if (element instanceof IComment) {
				IComment comment = (IComment) element;
				String desc = comment.getDescription();
				//desc = StringUtils.normalizeSpace(desc);
				return desc;
			}
			if (element instanceof IUser) {
				return ((IUser) element).getDisplayName();
			}
			if (element instanceof Date) {
				return DateUtil.getRelativeDuration(System.currentTimeMillis() - ((Date) element).getTime()) + " ago";
			}
			if (element instanceof ILineLocation) {
				int min = ((ILineLocation) element).getRangeMin();
				int max = ((ILineLocation) element).getRangeMax();
				String text = min + "";
				if (min != max) {
					text += "-" + max;
				}
				return text;
			}
			return super.getText(element);
		}

		@Override
		public StyledString getStyledText(Object element) {
			Styler styler = null;
			if (element instanceof IComment) {
				styler = COMMENT_STYLE;
			}
			StyledString styledString = new StyledString(getText(element), styler);
			if (element instanceof GlobalCommentsNode) {
				element = ((GlobalCommentsNode) element).getReview();
			}
			addTopicContainerStatsStyle(element, styledString);
			addTopicLineNumberStyle(element, styledString);
			addFilePathStyle(element, styledString);
			return styledString;
		}

		@Override
		public String getToolTipText(Object element) {
			if (element instanceof IComment) {
				IComment comment = (IComment) element;
				String desc = comment.getDescription();
				desc = StringUtils.replace(desc, "\r\n", "\n");
				String[] lines = desc.split("\n");
				List<String> seperated = new ArrayList<String>();
				for (String line : lines) {
					String newLine = "";
					String[] splitByWholeSeparator = StringUtils.splitByWholeSeparator(line, " ");
					int count = 0;
					for (String word : splitByWholeSeparator) {
						count += word.length();
						if (count > TOOLTIP_CHAR_WIDTH) {
							seperated.add(newLine);
							newLine = word;
							count = word.length();
						} else {
							if (count > word.length()) {
								newLine += " ";
							}
							newLine += word;
						}
					}
					seperated.add(newLine);
				}
				return StringUtils.join(seperated, "\n");
			}
			if (element instanceof IFileItem) {
				IFileItem fileItem = (IFileItem) element;
				return fileItem.getTarget().getPath() + " Revision: " + fileItem.getTarget().getRevision();
			}
			if (element instanceof IUser) {
				IUser user = (IUser) element;
				String text = user.getDisplayName();
				if (!StringUtils.isEmpty(user.getEmail())) {
					text += " <" + user.getEmail() + ">";
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

	public static final TableColumnProvider ARTIFACT_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN, "Artifact",
			50, 400, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IFileItem) {
				return element;
			}
			if (element instanceof IFileRevision) {
				return ((IFileRevision) element).getFile();
			}
			if (element instanceof ITopic) {
				ITopic topic = (ITopic) element;
				return adapt(topic.getItem());
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
			addTopicLineNumberStyle(element, styledString);
			addFilePathStyle(adapt(element), styledString);
			return styledString;
		}

		@Override
		public boolean isStyled() {
			return true;
		};
	};

	public static final TableColumnProvider COMMENTS_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN, "Comment",
			50, 400, true) {
		@Override
		public Object adapt(Object element) {
			if (element instanceof ITopic) {
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

	public static final TableColumnProvider AUTHORS_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN, "Author",
			10, 120, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IComment) {
				IComment comment = (IComment) element;
				return comment.getAuthor();
			}
			if (element instanceof IReviewItem) {
				IReviewItem item = (IReviewItem) element;
				return item.getAddedBy();
			}
			if (element instanceof IReview) {
				IReview item = (IReview) element;
				return item.getOwner();
			}
			return null;
		}
	};

	public static final TableColumnProvider DATE_COLUMN = new AdaptingTableColumnProvider(ITEMS_COLUMN, "Last Change",
			10, 120, false) {

		@Override
		public Object adapt(Object element) {
			if (element instanceof IDated) {
				IDated item = (IDated) element;
				if (item.getModificationDate() != null) {
					return item.getModificationDate();
				}
				if (item.getCreationDate() != null) {
					return item.getCreationDate();
				}
			}
			if (element instanceof IFileRevision) {
				IFileRevision item = (IFileRevision) element;
				return item.getRevision();
			}
			return null;
		};
	};

	public static final TableColumnProvider SINGLE_COLUMN = new TableColumnProvider("Items", 10, 120, false) {

		@Override
		public Image getImage(Object element) {
			return ITEMS_COLUMN.getImage(element);
		};

		@Override
		public String getText(Object element) {
			if (element instanceof GlobalCommentsNode) {
				return GLOBAL_COMMENTS_NAME;
			}
			if (element instanceof Collection) {
				Collection<?> collection = (Collection<?>) element;
				if (collection.size() == 1) {
					return getText(collection.iterator().next());
				}
			}
			if (element instanceof ITaskReference) {
				ITaskReference ref = (ITaskReference) element;
				return ref.getTaskId();
			}
			if (element instanceof IReview) {
				IReview review = (IReview) element;
				ITaskReference reviewTask = review.getReviewTask();
				if (reviewTask != null) {
					return getText(reviewTask);
				}
				return "Change " + review.getId();
			}
			if (element instanceof IFileItem) {
				IFileItem file = (IFileItem) element;
				String fileName = StringUtils.substringAfterLast(file.getName(), File.separator);
				return fileName;
			}
			if (element instanceof IReviewItem) {
				return ((IReviewItem) element).getName();
			}
			if (element instanceof IComment) {
				IComment comment = (IComment) element;
				String desc = comment.getDescription();
				return desc;
			}
			if (element instanceof IUser) {
				IUser user = (IUser) element;
				return user.getDisplayName();
			}
			if (element instanceof Date) {
				return COMMENT_DATE_FORMAT.format(element);
			}
			if (element instanceof ILineLocation) {
				int min = ((ILineLocation) element).getRangeMin();
				int max = ((ILineLocation) element).getRangeMax();
				String text = min + "";
				if (min != max) {
					text += "-" + max;
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
				if (element instanceof IComment) {
					IComment comment = (IComment) element;
					String commentText = text;
					int textLength = 0;
					String authorText = "";
					if (comment.getAuthor() != null) {
						authorText = getText(comment.getAuthor());
						textLength += authorText.length();
					}
					int descSpaceRemaining = (MAXIMUM_COMMENT_LENGTH - textLength);
					if (commentText.length() > descSpaceRemaining + 3) { //Account for ellipses
						commentText = commentText.substring(0, descSpaceRemaining - 3) + "..."; //$NON-NLS-1$
					}
					commentText = StringUtils.rightPad(commentText, MAXIMUM_COMMENT_LENGTH - textLength);

					styledString.append(authorText + " ", AUTHOR_STYLE);
					styledString.append(commentText + " ", COMMENT_STYLE);
					styledString.append(getText(comment.getCreationDate()) + " ", COMMENT_DATE_STYLE);
				} else {
					styledString.append(text);
				}
				if (element instanceof GlobalCommentsNode) {
					element = ((GlobalCommentsNode) element).getReview();
				}
				if (element instanceof ITopicContainer) {
					ITopicContainer container = (ITopicContainer) element;
					String statsText = getStatsText(container);
					styledString.append(statsText, StyledString.DECORATIONS_STYLER);
				}
				if (element instanceof IFileItem) {
					IReviewItem item = (IReviewItem) element;
					styledString.append("  " + item.getName(), StyledString.QUALIFIER_STYLER);
				}
			} else {
				styledString.append(element.toString());
			}
			return styledString;
		}
	};

	public static class Flat extends ReviewsLabelProvider {
		TableColumnProvider[] columns = new TableColumnProvider[] { ARTIFACT_COLUMN, COMMENTS_COLUMN, AUTHORS_COLUMN,
				DATE_COLUMN };

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public static class Tree extends ReviewsLabelProvider {
		TableColumnProvider[] columns = new TableColumnProvider[] { ITEMS_COLUMN, AUTHORS_COLUMN, DATE_COLUMN };

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	public static class Single extends ReviewsLabelProvider {
		TableColumnProvider[] columns = new TableColumnProvider[] { SINGLE_COLUMN };

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
		TableColumnProvider[] columns = new TableColumnProvider[] { SINGLE_COLUMN };

		@Override
		public Image getImage(Object element) {
			if (element instanceof IReviewItem) {
				IReviewItem item = (IReviewItem) element;
				return ReviewsUiPlugin.getDefault().getImageManager().getFileImage(item.getName());
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
			addTopicContainerStatsStyle(element, styledString);
			return styledString;
		}

		@Override
		public TableColumnProvider[] getColumnProviders() {
			return columns;
		}
	}

	@Override
	public String getText(Object element) {
		return ITEMS_COLUMN.getText(element);
	}

	public StyledString getStyledText(Object element) {
		return getStyledText(element, true);
	}

	public StyledString getStyledText(Object element, boolean includeCommentStats) {
		Styler styler = null;
		if (element instanceof IComment) {
			styler = COMMENT_STYLE;
		}
		StyledString styledString = new StyledString(getText(element), styler);
		if (element instanceof GlobalCommentsNode) {
			element = ((GlobalCommentsNode) element).getReview();
		}
		if (includeCommentStats) {
			addTopicContainerStatsStyle(element, styledString);
		}
		addTopicLineNumberStyle(element, styledString);
		addFilePathStyle(element, styledString);
		return styledString;
	}

	@Override
	public Image getImage(Object element) {
		return ITEMS_COLUMN.getImage(element);
	}

	public static void addTopicLineNumberStyle(Object element, StyledString styledString) {
		if (element instanceof ITopic) {
			ITopic comment = (ITopic) element;
			String[] locationStrings = new String[comment.getLocations().size()];
			int index = 0;
			for (ILocation location : comment.getLocations()) {
				locationStrings[index++] = ITEMS_COLUMN.getText(location);
			}
			String locationString = StringUtils.join(locationStrings, ", ");
			styledString.append(" " + locationString, LINE_NUMBER_STYLE);
		}
	}

	public static void addFilePathStyle(Object element, StyledString styledString) {
		if (element instanceof IFileItem) {
			IReviewItem item = (IReviewItem) element;
			styledString.append("  " + item.getName(), StyledString.QUALIFIER_STYLER);
		}
	}

	public static void addTopicContainerStatsStyle(Object element, StyledString styledString) {
		if (element instanceof ITopicContainer) {
			ITopicContainer container = (ITopicContainer) element;
			String statsText = getStatsText(container);
			styledString.append(statsText, StyledString.DECORATIONS_STYLER);
		}
	}

	public static String getIndexText(IComment comment) {
		long index = comment.getIndex();
		return index < Long.MAX_VALUE ? index + "" : "";
	}

	public static String getStatsText(ITopicContainer container) {
		List<? extends IComment> comments;
		if (container instanceof IReview) {
			comments = container.getTopics();
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
		String statsText = "";
		if (commentCount > 0 && draftCount > 0) {
			statsText = NLS.bind("  [{0} comments, {1} drafts]", commentCount, draftCount);
		} else if (commentCount > 0) {
			statsText = NLS.bind("  [{0} comments]", commentCount);
		} else if (draftCount > 0) {
			statsText = NLS.bind("  [{0} drafts]", draftCount);
		}
		return statsText;
	}

	@Override
	public abstract TableColumnProvider[] getColumnProviders();
}
