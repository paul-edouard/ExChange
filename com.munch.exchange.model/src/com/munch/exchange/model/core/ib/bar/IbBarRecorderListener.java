package com.munch.exchange.model.core.ib.bar;

import java.util.List;

public interface IbBarRecorderListener {
	
	public void barAdded(List<IbBar> bars);
	public void barReplaced(List<IbBar> bars);
	public void lastBarUpdated(IbBar bar);
	
	public void allBarsCleared();
	
	public void newCompletedBar(IbBar bar);

}
