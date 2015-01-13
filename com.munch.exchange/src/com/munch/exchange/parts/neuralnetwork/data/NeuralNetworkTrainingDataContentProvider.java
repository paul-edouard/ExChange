package com.munch.exchange.parts.neuralnetwork.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;

import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.parts.neuralnetwork.data.NeuralNetworkInputConfiguratorContentProvider.NeuralNetworkSerieCategory;

public class NeuralNetworkTrainingDataContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {

	
	private static Logger logger = Logger.getLogger(NeuralNetworkTrainingDataContentProvider.class);
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		
		//logger.info("Get children called");
		
		if(parentElement instanceof Configuration){
			Configuration p_el=(Configuration)parentElement;
			Object[]  objs=new Object[2];
			
			objs[0]=p_el.getLastInput();
			objs[1]=p_el.getTrainingSet();
			
			return objs;
			
		}
		
		if(parentElement instanceof DataSet){
			DataSet d=(DataSet)parentElement;
			
			if(d.getRows().size()<=100){
				List<DataSetRow> b = new ArrayList<DataSetRow>(d.getRows());
				Collections.reverse(b);
				return b.toArray();
			}
			
			LinkedList<DataSet> children=new LinkedList<DataSet>();
			
			if(d.getLabel().equals(Configuration.ROOT_DATA_SET)){
				DataSet child=new DataSet(d.getInputSize(), d.getOutputSize());
				int k=0;
				int i=0;
				for(i=0;i<d.getRows().size();i++){
					if(child.size()==100){
						child.setLabel("["+(i-1)+".."+k+"]");
						children.addFirst(child);
						child=new DataSet(d.getInputSize(), d.getOutputSize());
						k=i;
					}
					child.addRow(d.getRows().get(i));
				}
				
				if(child.size()>0){
					child.setLabel("["+(i-1)+".."+k+"]");
					children.addFirst(child);
				}
				
			}
			
			logger.info(d.getLabel()+", number of childs:"+children.size());
			
			return children.toArray();
		}
		
		logger.info("Null will be return");
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof DataSet){
			DataSet el=(DataSet)element;
			return el.getRows().size()>0;
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		//logger.info("Input elemtent"+inputElement);
		//logger.info("Input elemtent"+inputElement.getClass());
		
		if(inputElement instanceof DataSet || inputElement instanceof Configuration){
			return this.getChildren(inputElement);
		}
		return null;
	}

}
