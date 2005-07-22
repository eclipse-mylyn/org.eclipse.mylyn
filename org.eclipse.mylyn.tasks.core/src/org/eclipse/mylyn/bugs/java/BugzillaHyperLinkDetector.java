package org.eclipse.mylar.bugs.java;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.internal.corext.dom.NodeFinder;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.IClassFileEditorInput;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.java.ui.editor.AbstractMylarHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.texteditor.ITextEditor;
/**
 * @author Shawn Minto
 *
 */
public class BugzillaHyperLinkDetector extends AbstractMylarHyperlinkDetector {

	
	public BugzillaHyperLinkDetector() {
		super();
	}

	@SuppressWarnings("unchecked")
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor = getEditor();
		if (region == null || textEditor == null || canShowMultipleHyperlinks || !(textEditor instanceof JavaEditor))
			return null;

		IEditorSite site= textEditor.getEditorSite();
		if (site == null)
			return null;

		IJavaElement javaElement= getInputJavaElement(textEditor);
		if (javaElement == null)
			return null;

		CompilationUnit ast= JavaPlugin.getDefault().getASTProvider().getAST(javaElement, ASTProvider.WAIT_NO, null);
		if (ast == null)
			return null;

		ASTNode node= NodeFinder.perform(ast, region.getOffset(), 1);
	
		if (node == null || !(node instanceof TextElement || node instanceof Block))
			return null;
	
		String comment = null;
		int commentStart = -1;
		
		if(node instanceof TextElement){
			TextElement element = (TextElement)node;
			comment = element.getText();
			commentStart = element.getStartPosition();
		} else if(node instanceof Block){
			Comment c = findComment(ast.getCommentList(), region.getOffset(), 1);
			if(c != null){
				try{
					IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
					String commentString = document.get(c.getStartPosition(), c.getLength());
					comment = getStringFromComment(c, region.getOffset(), commentString);
					commentStart = getLocationFromComment(c, comment, commentString) + c.getStartPosition();
				} catch (BadLocationException e){
					MylarPlugin.log(e, "Failed to get text for comment");
				}
			}
		}

		if(comment == null)
			return null;
		
		int startOffset= region.getOffset();
		int endOffset= startOffset + region.getLength();

		if(comment.toLowerCase().indexOf("bug") != -1){
	
			int start = comment.toLowerCase().indexOf("bug");
			int end = comment.indexOf(" ", start + 4);
			if(end == -1)
				end = comment.length();

			int bugId = Integer.parseInt(comment.substring(start+4, end).trim());
			
			start += commentStart;
			end += commentStart;
		
			if(startOffset >= start && endOffset <= end){
				IRegion sregion= new Region(start, end);
				return new IHyperlink[] {new BugzillaHyperLink(sregion, bugId)};
			}
		}
		return null;
	}

	private int getLocationFromComment(Comment c, String commentLine, String commentString) {
		if(commentLine == null){
			return -1;
		} else {
			return commentString.indexOf(commentLine);
		}
	}

	private String getStringFromComment(Comment comment, int desiredOffset, String commentString) {
		String [] parts = commentString.split("\n");
		if(parts.length > 1){
			int offset = comment.getStartPosition();
			for(String part: parts){
				int newOffset = offset + part.length() + 1;
				if(desiredOffset >= offset && desiredOffset <= newOffset){
					return part;
				}
				
			}
		} else {
			return commentString;
		}

		return null;
	}

	private Comment findComment(List<Comment> commentList, int offset, int i) {
		for(Comment comment: commentList){
			if(comment.getStartPosition() <= offset && (comment.getStartPosition() + comment.getLength() >= offset + i)){
				return comment;
			}
		}
		return null;
	}

	private IJavaElement getInputJavaElement(ITextEditor editor) {
		IEditorInput editorInput= editor.getEditorInput();
		if (editorInput instanceof IClassFileEditorInput)
			return ((IClassFileEditorInput)editorInput).getClassFile();

		if (editor instanceof CompilationUnitEditor)
			return JavaPlugin.getDefault().getWorkingCopyManager().getWorkingCopy(editorInput);

		return null;
	}	
}
