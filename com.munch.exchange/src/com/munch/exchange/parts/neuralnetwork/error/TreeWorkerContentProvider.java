package com.munch.exchange.parts.neuralnetwork.error;

import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class TreeWorkerContentProvider implements IStructuredContentProvider,
		ITreeContentProvider {
	
	private static Logger logger = Logger.getLogger(TreeWorkerContentProvider.class);
	
	private Workers workers;
	

	public TreeWorkerContentProvider(String workersGroup) {
		super();
		this.workers = new Workers(workersGroup);
	}

	
	
	public Workers getWorkers() {
		return workers;
	}


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
		if(parentElement instanceof Workers){
			Workers p_el=(Workers)parentElement;
			return p_el.children.toArray();
			
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof Worker){
			Worker el=(Worker)element;
			return el.parent;
			
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof Workers){
			Workers el=(Workers)element;
			return el.children.size()>0;
			
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Workers){
			return this.getChildren(inputElement);
		}
		return null;
	}
	
	
	public class Workers{
		
		public String groupName;
		public LinkedList<Worker> children=new LinkedList<Worker>();
		
		public Workers(String groupName) {
			super();
			this.groupName = groupName;
		}

		public void addChild(Worker worker){
			children.add(worker);
			worker.parent=this;
		}
		
		public Worker getWorkerFromId(int id){
			for(Worker worker:children){
				if(worker.id==id)return worker;
			}
			return null;
		}
		
		public void clear(){
			children.clear();
		}
		
		public Worker getWorkerFromDimension(int dimension){
			for(Worker worker:children){
				if(worker.dimension==dimension)return worker;
			}
			return null;
		}
		
	}
	
	public class Worker{
		
		public int id;
		public int dimension;
		//public int secondsOfInactivity;
		public String statusManager;
		public String statusArchitecture;
		public String statusOptimization;
		public String statusLearning;
		public Workers parent;
		public Calendar lastReaction;
		
		public Worker(int id, int dimension) {
			super();
			this.id = id;
			this.dimension = dimension;
			statusManager="No Status";
			statusArchitecture="No Status";
			statusOptimization="No Status";
			resetLastReaction();
		}
		
		public void resetLastReaction(){
			lastReaction=Calendar.getInstance();
		}
		
		
	}
	

}
