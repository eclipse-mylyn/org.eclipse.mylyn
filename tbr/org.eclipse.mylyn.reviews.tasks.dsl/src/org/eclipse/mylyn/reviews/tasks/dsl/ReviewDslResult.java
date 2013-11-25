/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *     Jacques Bouthillier (Ericsson) - Bug 422509 Prevent null pointer exception
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.dsl;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author mattk
 *
 */
public class ReviewDslResult {
	public enum Rating {
		PASSED, FAILED, WARNING, TODO
	}

	public static class FileComment {

		private String fileName;
		private String comment;
		private List<LineComment> lineComments = new ArrayList<LineComment>();

		public String getFileName() {
			return fileName;
		}

		public String getComment() {
			return comment;
		}

		public void setFileName(String path) {
			this.fileName = path;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public List<LineComment> getLineComments() {
			return lineComments;
		}

		public StringBuilder serialize(StringBuilder sb) {
			sb.append("File \"");
			sb.append(fileName);
			sb.append("\"");
			if (comment != null) {
				sb.append(" \"");
				sb.append(comment);
				sb.append("\"");
			}
			for (LineComment c : lineComments) {
				sb.append("\n");
				c.serialize(sb);
			}
			return sb;
		}

	}

	public static class LineComment {
		private int begin;
		private int end;
		private String comment;

		public int getBegin() {
			return begin;
		}

		public StringBuilder serialize(StringBuilder sb) {
			sb.append("Line ");
			sb.append(begin);
			if (begin != end) {
				sb.append(" - ");
				sb.append(end);
			}
			sb.append(": \"");
			sb.append(comment);
			sb.append("\"");
			return sb;
		}

		public void setBegin(int begin) {
			this.begin = begin;
		}

		public int getEnd() {
			return end;
		}

		public void setEnd(int end) {
			this.end = end;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

	}

	private Rating rating;
	private String comment;
	private List<FileComment> fileComments = new ArrayList<FileComment>();

	public Rating getRating() {
		return rating;
	}

	public String getComment() {
		return comment;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<FileComment> getFileComments() {
		return fileComments;
	}

	public StringBuilder serialize(StringBuilder sb) {
		sb.append("Review result: ");
		sb.append(rating!= null ? rating.toString(): "");
		if (comment != null) {
			sb.append(" \"");
			sb.append(comment);
			sb.append("\"");
		}
		for (FileComment c : fileComments) {
			sb.append("\n");
			c.serialize(sb);
		}
		return sb;
	}

}
