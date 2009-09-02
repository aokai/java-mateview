
package com.redcareditor.mate;

import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

import com.redcareditor.onig.Match;
import com.redcareditor.onig.Rx;

public class Scope implements Comparable<Scope>{
	public MateText mateText;
	private StyledText styledText;
	
	public String name;
	public Pattern pattern;
	
	public boolean isOpen;
	public boolean isCapture;
	
	public Match openMatch;
	public Match closeMatch;
	
	public TextLocation start;
	public TextLocation innerStart;
	public TextLocation innerEnd;
	public TextLocation end;
	
	public Rx closingRegex;
	public String beginMatchString;
	public String endMatchString;
	
	public Scope parent;
	public SortedSet<Scope> children;
	
	public Scope(MateText mt, String name) {
		this.mateText = mt;
		this.styledText = mt.getTextWidget();
		this.name = name;
		this.children = new TreeSet<Scope>();
	}
	
	public void clearAfter(int lineIx, int something) {
		// TODO: port this method
	}

	public Scope scopeAt(int line, int offset) {
		TextLocation location = new TextLocation(line, offset, mateText);
		return scopeAt(location);
	}
	
	public Scope scopeAt(TextLocation location){
		if (start.compareTo(location) <= 0 || parent == null) {
			if (isOpen ||  end.compareTo(location) >= 0) {
				
				for (Scope child : children) {
					if (child.containsLoc(location)) {
						return child.scopeAt(location);
					}
				}
				return this;
			}
		}
		return null;
	}
	
	private boolean containsLoc(TextLocation loc) {
		return (start.compareTo(loc) <= 0) && (end.compareTo(loc) >= 0);
	}

	public Scope containingDoubleScope(int line) {
		Scope scope = this;
		
		while(scope.parent != null){
			if(	!(scope.pattern instanceof SinglePattern) && 
				!scope.isCapture &&
				!(scope.startLine() == line && scope.startLineOffset() == 0)
			){
				return scope;
			}
		}
		return null;
	}
	
	public boolean surfaceIdenticalTo(Scope other) {
		// TODO: port me
		return false;
	}

	public boolean surfaceIdenticalToModuloEnding(Scope other) {
		// TODO: port me
		return false;
	}

	public boolean overlapsWith(Scope other) {
		if(start.compareTo(other.start) >= 0 ){
			return start.compareTo(other.end) <= 0;
		}else{
			return other.start.compareTo(end) <= 0;
		}
	}

	public void addChild(Scope newChild) {
		children.add(newChild);
	}
	
	public void removeChild(Scope child) {
		children.remove(child);
	}

	private int comparePositions(Position a, Position b) {
		return a.offset - b.offset;
	}

	public Scope firstChildAfter(int line, int offset) {
//		stdout.printf("\"%s\".first_child_after(%d, %d)\n", name, loc.line, loc.line_offset);
		TextLocation location = new TextLocation(line, offset, mateText);
		for (Scope child : children) {
			if (child.start.compareTo(location) >= 0) {
				return child;
			}
		}
		return null;
	}

	private TextLocation createTextLocation(int line, int offset) {
		TextLocation location = new TextLocation(line,offset,mateText);
		try {
			mateText.getDocument().addPosition(location);
			return location;
		}
		catch(BadLocationException e) {
			System.out.printf("BadLocationException in Scope (%d, %d)\n", location.getLine(), location.getLineOffset());
			e.printStackTrace();
		}
		return null;
	}
	
	public void setStartPos(int line, int offset, boolean hasLeftGravity) {
		this.start = createTextLocation(line,offset);
	}

	public void setInnerStartPos(int line, int offset, boolean hasLeftGravity) {
		this.innerStart = createTextLocation(line, offset);
	}

	public void setInnerEndPos(int line, int offset, boolean c) {
		this.innerEnd = createTextLocation(line, offset);
	}

	public void setEndPos(int line, int offset, boolean c) {
		this.end = createTextLocation(line, offset);
	}
	
	public int startLine() {
		if (start == null)
			return 0;
		else
			return styledText.getLineAtOffset(start.offset);
	}

	public int endLine() {
		if (end == null)
			return styledText.getLineCount() - 1;
		else
			return styledText.getLineAtOffset(end.offset);
	}

	public int startLineOffset() {
		if (start == null)
			return 0;
		else
			return start.offset - styledText.getOffsetAtLine(startLine());
	}

	public int innerEndLineOffset() {
		if (innerEnd == null)
			return styledText.getCharCount();
		else
			return innerEnd.offset - styledText.getOffsetAtLine(endLine());
	}
	
	public int endLineOffset() {
		if (end == null)
			return styledText.getCharCount() - styledText.getOffsetAtLine(endLine());
		else
			return end.offset - styledText.getOffsetAtLine(endLine());
	}
	
	public int startOffset() {
		return this.start.offset;
	}
	
	public int endOffset() {
		return this.end.offset;
	}
	
	public String pretty(int indent) {
		StringBuilder prettyString = new StringBuilder("");
		for (int i = 0; i < indent; i++)
			prettyString.append("  ");
		if (this.isCapture)
			prettyString.append("c");
		else
			prettyString.append("+");
		
		if (this.name != null)
			prettyString.append(" " + this.name);
		else
			prettyString.append(" " + "[noname]");
		
		if (this.pattern instanceof DoublePattern && 
				this.isCapture == false && 
				((DoublePattern) this.pattern).contentName != null) 
			prettyString.append(" " + ((DoublePattern) this.pattern).contentName);
		prettyString.append(" (");
		// TODO: port these sections once Positions are figured out
//		if (startPos == null) {
//			prettyString.append("inf");
//		}
//		else {
			prettyString.append(String.format(
					"%d,%d",
					startLine(), 
					startLineOffset()));
//		}
		prettyString.append(")-(");
//		if (endPos == null) {
//			prettyString.append("inf");
//		}
//		else {
			prettyString.append(String.format(
					"%d,%d",
					endLine(), 
					endLineOffset()));
//		}
		prettyString.append(")");
		prettyString.append((isOpen ? " open" : " closed"));
		prettyString.append("\n");

		indent += 1;
		for (Scope child : this.children) {
			prettyString.append(child.pretty(indent));
		}
		
		return prettyString.toString();
	}

	public int compareTo(Scope o) {
		int startCompare = start.compareTo(o.start);
		if(startCompare == 0){
			return end.compareTo(o.end);
		}
		return startCompare;
	}
}
