package com.munch.exchange.model.core.ib.bar;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;

public class IbBarRecorder {
	
	
	
	private LinkedList<IbBar> barList=new LinkedList<IbBar>();
	private HashMap<Long, IbBar> barMap=new HashMap<Long, IbBar>();
	private IbBar lastReceivedBar=null;
	private IbBar firstReceivedBar=null;
	
	private BarSize barSize=BarSize._1_min;
	private WhatToShow whatToShow=WhatToShow.MIDPOINT;
	
	
	private List<IbBarRecorderListener> listeners=new LinkedList<IbBarRecorderListener>();
	
	
	public void addBar(IbBar bar){
		LinkedList<IbBar> bars=new LinkedList<IbBar>();
		bars.add(bar);
		addBars(bars);
	}
	
	public void addBars(List<IbBar> newBars){
		if(newBars==null || newBars.isEmpty())
			return;
		
		LinkedList<IbBar> addedBars=new LinkedList<IbBar>();
		LinkedList<IbBar> replacedBars=new LinkedList<IbBar>();
		IbBar localLastReceivedBar=null;
		
		
		//The Map is empty
		for(IbBar bar:newBars){
		if(barMap.isEmpty() || !barMap.containsKey(bar.getTime())){
				barMap.put(bar.getTime(), bar);
				addedBars.add(bar);
				if(localLastReceivedBar==null ||
						localLastReceivedBar.getTime()<bar.getTime()){
					localLastReceivedBar=bar;
				}
				if(firstReceivedBar==null || firstReceivedBar.getTime()>bar.getTime()){
					firstReceivedBar=bar;
				}
		}
		//The map will be updated
		else{
			//System.out.println("Replace bar!!");
			IbBar oldBar=barMap.get(bar.getTime());
			if(oldBar.isRealTime()){
				
				//System.out.println("is real time!!");
				barMap.put(bar.getTime(), bar);
				replacedBars.add(bar);
				
				if(localLastReceivedBar==null ||
						localLastReceivedBar.getTime()<=bar.getTime()){
					localLastReceivedBar=bar;
				}
				
			}
			else if(!oldBar.isCompleted()){
				//System.out.println("update uncompeted bar!!");
				oldBar.integrateData(bar);
				barMap.put(oldBar.getTime(), oldBar);
				replacedBars.add(oldBar);
				
				if(localLastReceivedBar==null ||
						localLastReceivedBar.getTime()<=bar.getTime()){
					localLastReceivedBar=bar;
				}
				
			}
				
			
		}
		}
		
		
		if(!addedBars.isEmpty()){
			Collections.sort(addedBars);
			fireBarAdded(addedBars);
		}
		if(!replacedBars.isEmpty()){
			Collections.sort(replacedBars);
			fireBarReplaced(replacedBars);
		}
		
		//System.out.println(" Test localLastReceivedBar==null");
		if(localLastReceivedBar==null)return;
		
		//System.out.println("localLastReceivedBar!=null!");
		
		if(lastReceivedBar==null || lastReceivedBar.getTime()==localLastReceivedBar.getTime()){
			//System.out.println("last");
			lastReceivedBar=localLastReceivedBar;
			fireLastBarUpdated(lastReceivedBar);
		}
		else if(lastReceivedBar.getTime()<localLastReceivedBar.getTime()){
			//System.out.println("lastReceivedBar is now old");
			if(!lastReceivedBar.isCompleted()){
				lastReceivedBar.setCompleted(true);
				//System.out.println("fireNewCompletedBar");
				fireNewCompletedBar(lastReceivedBar);
			}
			lastReceivedBar=localLastReceivedBar;
			fireLastBarUpdated(lastReceivedBar);
		}
		
	}
	
	
	
	private void clearAll(){
		if(barMap.isEmpty())return;
		barList.clear();
		barMap.clear();
		lastReceivedBar=null;
		fireAllBarsCleared();
	}
	
	
	public List<IbBar> getAllBars(){
		updateBarList();
		return barList;
	}
	
	
	public List<IbBar> getAllCompletedBars(){
		barList.clear();
		barList.addAll(barMap.values());
		Collections.sort(barList);
		if(!barList.getLast().isCompleted()){
			barList.removeLast();
		}
		return barList;
	}

 	public boolean isEmpty(){
		return barMap.isEmpty();
	}
	
	private void updateBarList(){
		barList.clear();
		barList.addAll(barMap.values());
		Collections.sort(barList);
	}

	
	//Listener
	
	public IbBar getLastReceivedBar() {
		return lastReceivedBar;
	}

	public IbBar getFirstReceivedBar() {
		return firstReceivedBar;
	}

	public BarSize getBarSize() {
		return barSize;
	}

	public void setBarSize(BarSize barSize) {
		if(barSize!=this.barSize){
			this.barSize = barSize;
			clearAll();
		}
	}

	public WhatToShow getWhatToShow() {
		return whatToShow;
	}

	public void setWhatToShow(WhatToShow whatToShow) {
		if(this.whatToShow != whatToShow){
			this.whatToShow = whatToShow;
			clearAll();
		}
	}

	public void addListener(IbBarRecorderListener listener){
		listeners.add(listener);
	}
	public void removeListener(IbBarRecorderListener listener){
		listeners.remove(listener);
	}
	
	private void fireBarAdded(LinkedList<IbBar> addedBars){
		for(IbBarRecorderListener listener:listeners){
			listener.barAdded(addedBars);
		}
	}
	private void fireBarReplaced(LinkedList<IbBar> replacedBars){
		for(IbBarRecorderListener listener:listeners){
			listener.barReplaced(replacedBars);
		}
	}
	private void fireNewCompletedBar(IbBar bar){
		for(IbBarRecorderListener listener:listeners){
			listener.newCompletedBar(bar);
		}
	}
	private void fireLastBarUpdated(IbBar bar){
		for(IbBarRecorderListener listener:listeners){
			listener.lastBarUpdated(bar);
		}
	}
	
	private void fireAllBarsCleared(){
		for(IbBarRecorderListener listener:listeners){
			listener.allBarsCleared();
		}
	}
	
	
	
}
