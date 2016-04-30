package com.munch.exchange.model.core.ib.bar;

import java.util.List;

public interface IbBarRecorderListener {
	
	public void barAdded(List<ExBar> bars);
	public void barReplaced(List<ExBar> bars);
	public void lastBarUpdated(ExBar bar);
	
	public void allBarsCleared();
	
	public void newCompletedBar(ExBar bar);

}
