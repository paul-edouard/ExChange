package com.munch.exchange.model.core.neuralnetwork.training;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class TrainingBlock extends XmlParameterElement {
	
	static final String FIELD_Start="Start";
	static final String FIELD_End="End";
	static final String FIELD_IsTraining="IsTraining";
	
	private int start=-1;
	private int end=-1;
	
	private boolean isTraining;
	
	public TrainingBlock(){
		super();
	}

	public TrainingBlock(int start, int end) {
		super();
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
	changes.firePropertyChange(FIELD_Start, this.start, this.start = start);
	}
	

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
	changes.firePropertyChange(FIELD_End, this.end, this.end = end);
	}
	

	public boolean isTraining() {
		return isTraining;
	}

	public void setTraining(boolean isTraining) {
	changes.firePropertyChange(FIELD_IsTraining, this.isTraining, this.isTraining = isTraining);
	}
	

	@Override
	public String toString() {
		return "\nTrainingBlock [start=" + start + ", end=" + end
				+", nb of values="+(end-start+1)+ ", isTraining=" + isTraining + "]";
	}

	@Override
	protected void initAttribute(Element rootElement) {
		this.setStart(Integer.parseInt(rootElement.getAttribute(FIELD_Start)));
		this.setEnd(Integer.parseInt(rootElement.getAttribute(FIELD_End)));
		this.setTraining(Boolean.parseBoolean(rootElement.getAttribute(FIELD_IsTraining)));
	}

	@Override
	protected void initChild(Element childElement) {}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Start,String.valueOf(this.getStart()));
		rootElement.setAttribute(FIELD_End,String.valueOf(this.getEnd()));
		rootElement.setAttribute(FIELD_IsTraining,String.valueOf(this.isTraining()));

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {}

}
