package com.munch.exchange.model.core.ib.bar;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.ib.controller.Types.BarSize;
import com.ib.controller.Types.WhatToShow;

public class BarRecorder {
	
	
	
	private LinkedList<ExBar> barList=new LinkedList<ExBar>();
	private HashMap<Long, ExBar> barMap=new HashMap<Long, ExBar>();
	private ExBar lastReceivedBar=null;
	private ExBar firstReceivedBar=null;
	
	private BarSize barSize=BarSize._1_min;
	private WhatToShow whatToShow=WhatToShow.MIDPOINT;
	
	
	private List<BarRecorderListener> listeners=new LinkedList<BarRecorderListener>();
	
	
	public void addBar(ExBar bar){
		LinkedList<ExBar> bars=new LinkedList<ExBar>();
		bars.add(bar);
		addBars(bars);
	}
	
	public void addBars(List<ExBar> newBars){
		if(newBars==null || newBars.isEmpty())
			return;
		
		LinkedList<ExBar> addedBars=new LinkedList<ExBar>();
		LinkedList<ExBar> replacedBars=new LinkedList<ExBar>();
		ExBar localLastReceivedBar=null;
		
		
		//The Map is empty
		for(ExBar bar:newBars){
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
			ExBar oldBar=barMap.get(bar.getTime());
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
			Collections.sort(addedBars, new ExBarComparator());
			fireBarAdded(addedBars);
		}
		if(!replacedBars.isEmpty()){
			Collections.sort(replacedBars, new ExBarComparator());
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
	
	
	public List<ExBar> getAllBars(){
		updateBarList();
		return barList;
	}
	
	
	public List<ExBar> getAllCompletedBars(){
		barList.clear();
		barList.addAll(barMap.values());
		Collections.sort(barList, new ExBarComparator());
		if(!barList.isEmpty() && !barList.getLast().isCompleted()){
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
		Collections.sort(barList, new ExBarComparator());
	}

	
	//Listener
	
	public ExBar getLastReceivedBar() {
		return lastReceivedBar;
	}

	public ExBar getFirstReceivedBar() {
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

	public void addListener(BarRecorderListener listener){
		listeners.add(listener);
	}
	public void removeListener(BarRecorderListener listener){
		listeners.remove(listener);
	}
	
	private void fireBarAdded(LinkedList<ExBar> addedBars){
		for(BarRecorderListener listener:listeners){
			listener.barAdded(addedBars);
		}
	}
	private void fireBarReplaced(LinkedList<ExBar> replacedBars){
		for(BarRecorderListener listener:listeners){
			listener.barReplaced(replacedBars);
		}
	}
	private void fireNewCompletedBar(ExBar bar){
		for(BarRecorderListener listener:listeners){
			listener.newCompletedBar(bar);
		}
	}
	private void fireLastBarUpdated(ExBar bar){
		for(BarRecorderListener listener:listeners){
			listener.lastBarUpdated(bar);
		}
	}
	
	private void fireAllBarsCleared(){
		for(BarRecorderListener listener:listeners){
			listener.allBarsCleared();
		}
	}
	
	
	
}
