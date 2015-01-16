package com.munch.exchange.model.core.neuralnetwork.training;

import java.util.LinkedList;
import java.util.Random;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;

public class TrainingBlocks extends XmlParameterElement {
	
	
	//static final String FIELD_Start="Blocks";
	static final String FIELD_TrainingRate="TrainingRate";
	static final String FIELD_NbOfBLocks="NbOfBLocks";
	
	LinkedList<TrainingBlock> blocks=new LinkedList<TrainingBlock>();
	private double trainingRate=-1;
	private int nbOfBlocks=0;
	
	
	
	
	public void createBlocks(DataSet dataset){
		blocks.clear();
		//int total=dataset.size();
		int midBlockLength=dataset.size()/nbOfBlocks;
		
		int k=1;
		int i=0;
		TrainingBlock block=new TrainingBlock();
		for(i=0;i<dataset.size();i++){
			DataSetRow row=dataset.getRowAt(i);
			
			if(block.getStart()<0)
				block.setStart(i);
			
			if(i==0)continue;
			
			DataSetRow lastRow=dataset.getRowAt(i-1);
			
			if(i>=(k*midBlockLength) && lastRow.getDesiredOutput()[0]!=row.getDesiredOutput()[0]){
				block.setEnd(i);
				blocks.add(block);
				block=new TrainingBlock();
				k++;
			}
		}
		block.setEnd(i-1);
		blocks.add(block);
		
		setTrainingStateOfBlocks();
		
	//	System.out.println(this);
		
	}
	
	private void setTrainingStateOfBlocks(){
		Random r=new Random();
		for(TrainingBlock block:blocks){
			block.setTraining(false);
		}
		
		double currentTrainingRate=0;
		while(currentTrainingRate<=this.getTrainingRate()){
			int i=r.nextInt(blocks.size());
			blocks.get(i).setTraining(true);
			
			double nbOfTraining=0;
			for(TrainingBlock block:blocks){
				if(block.isTraining())nbOfTraining+=1.0;
			}
			
			currentTrainingRate=nbOfTraining/((double)blocks.size() );
			
		}
		
		
	}
	
	

	public LinkedList<TrainingBlock> getBlocks() {
		return blocks;
	}


	public int getNbOfBlocks() {
		return nbOfBlocks;
	}

	public void setNbOfBlocks(int nbOfBlocks) {
	changes.firePropertyChange(FIELD_NbOfBLocks, this.nbOfBlocks, this.nbOfBlocks = nbOfBlocks);}
	

	public double getTrainingRate() {
		return trainingRate;
	}

	public void setTrainingRate(double trainingRate) {
	changes.firePropertyChange(FIELD_TrainingRate, this.trainingRate, this.trainingRate = trainingRate);
	}
	

	@Override
	public String toString() {
		return "TrainingBlocks [blocks=" + blocks + ", trainingRate="
				+ trainingRate + ", nbOfBlocks=" + nbOfBlocks + "]";
	}



	@Override
	protected void initAttribute(Element rootElement) {
		this.setTrainingRate(Double.parseDouble(rootElement.getAttribute(FIELD_TrainingRate)));
		this.setNbOfBlocks(Integer.parseInt(rootElement.getAttribute(FIELD_NbOfBLocks)));
		

	}

	@Override
	protected void initChild(Element childElement) {
		//blocks.clear();
		TrainingBlock block=new TrainingBlock();
		
		if(childElement.getTagName().equals(block.getTagName())){
			block.init(childElement);
			blocks.add(block);
		}
		

	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_TrainingRate,String.valueOf(this.getTrainingRate()));
		rootElement.setAttribute(FIELD_NbOfBLocks,String.valueOf(this.getNbOfBlocks()));

	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(TrainingBlock block : this.blocks){
			rootElement.appendChild(block.toDomElement(doc));
		}

	}

}
