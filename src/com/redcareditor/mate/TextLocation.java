package com.redcareditor.mate;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

public class TextLocation extends Position implements Comparable<Position> {
	private MateText mateText;

	public TextLocation(int offset, MateText mateText){
		super(offset);
		this.mateText = mateText;
	}
	
	public TextLocation(int line, int lineOffset, MateText mateText) {
		super(computeOffset(mateText, line, lineOffset));
		this.mateText = mateText;
	}
	
	public int getLine(){
		return mateText.getTextWidget().getLineAtOffset(getOffset());
	}
	
	public int getLineOffset(){
		return getOffset() - mateText.getTextWidget().getOffsetAtLine(getLine());
	}
	
	public int getOffset(){
		return offset;
	}

	public int compareTo(Position other) {
		return getOffset() - other.getOffset();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TextLocation) {
			return ((TextLocation) obj).compareTo(this) == 0;
		}
		return false;
	}
	
	public boolean equals(int line, int offset){
		return equals(new TextLocation(line,offset,mateText));
	}

	@Override
	public String toString() {
		return String.format("{%d,%d}", getLine(), getLineOffset());
	}
	
	private static int computeOffset(MateText mateText, int line, int offset){
		// Make line positive
		line = line > 0 ? line : 0;
		int lineLength = computeLineLength(mateText, line);
		// If offset is bigger than lineLength then set it to lineLength
		offset = offset < lineLength ? offset : lineLength;
		
		return mateText.getTextWidget().getOffsetAtLine(line)+offset;
	}
	
	private static int computeLineLength(MateText text,int line){
		StyledText st = text.getTextWidget();
		// If this line is the last one set end off line to the length else to the nextline's offset -1
		int endOffLineOffset = line+1 == st.getLineCount() ? st.getCharCount() : st.getOffsetAtLine(line+1);
		return endOffLineOffset - st.getOffsetAtLine(line);
	}
}
