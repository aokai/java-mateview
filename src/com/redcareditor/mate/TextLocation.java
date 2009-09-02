package com.redcareditor.mate;

import org.eclipse.jface.text.Position;
import org.eclipse.swt.custom.StyledText;

public class TextLocation extends Position implements Comparable<Position> {
	private MateText mateText;

	public TextLocation(int line, int lineOffset, MateText mateText) {
		super(computeOffset(mateText, line, lineOffset));
		this.mateText = mateText;
	}
	
	public int getLine(){
		return mateText.getTextWidget().getLineAtOffset(offset);
	}
	
	public int getLineOffset(){
		return offset - mateText.getTextWidget().getOffsetAtLine(getLine());
	}

	public int compareTo(Position other) {
		return offset - other.offset;
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
		return mateText.getTextWidget().getOffsetAtLine(line)+offset;
	}
}
