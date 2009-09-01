package com.redcareditor.mate;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

public class TextLocation implements Comparable<TextLocation> {
	public int line;
	public int lineOffset;

	public TextLocation(int line, int lineOffset) {
		this.line = line;
		this.lineOffset = lineOffset;
	}
	
	public static TextLocation fromPosition(Position position, StyledText document){
		int line = document.getLineAtOffset(position.offset);
		int lineOffset = position.offset - document.getOffsetAtLine(line);
		
		return new TextLocation(
				line,
				lineOffset
		);
	}

	public int compareTo(TextLocation o) {
		if (line > o.line) {
			return 1;
		} else if (line < o.line) {
			return -1;
		} else {
			if (lineOffset > o.lineOffset) {
				return 1;
			} else if (lineOffset < o.lineOffset) {
				return -1;
			} else {
				return 0;
			}
		}
	}
	
	public Position toPosition(StyledText document){
		return new Position(document.getOffsetAtLine(line)+lineOffset,0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TextLocation) {
			return ((TextLocation) obj).compareTo(this) == 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("{%d,%d}", line, lineOffset);
	}
}
