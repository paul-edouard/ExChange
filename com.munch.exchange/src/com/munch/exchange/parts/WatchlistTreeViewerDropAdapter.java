package com.munch.exchange.parts;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class WatchlistTreeViewerDropAdapter extends ViewerDropAdapter {
	
	private final Viewer viewer;
	
	private int location;
	
	public WatchlistTreeViewerDropAdapter(Viewer viewer){
		super(viewer);
		this.viewer = viewer;
	}
	
	@Override
	public void drop(DropTargetEvent event) {
		location = this.determineLocation(event);
	    //String target = (String) determineTarget(event);
		
	    String translatedLocation ="";
	    switch (location){
	    case 1 :
	      translatedLocation = "Dropped before the target ";
	      break;
	    case 2 :
	      translatedLocation = "Dropped after the target ";
	      break;
	    case 3 :
	      translatedLocation = "Dropped on the target ";
	      break;
	    case 4 :
	      translatedLocation = "Dropped into nothing ";
	      break;
	    }
	    
	    System.out.println(translatedLocation);
	   // System.out.println("The drop was done on the element: " + target);

		super.drop(event);
	}
	
	@Override
	public boolean performDrop(Object data) {
		// TODO Auto-generated method stub
		/*
		if(location==3 && group!=null){
			
			group.addRole(data.toString());
			
			if(provider.updateData(group)!=null){
				broker.send(IBrokerEvents.USER_GROUP_UPDATE, group);
			}
			
		}
		*/
		System.out.println("Drop performed: "+data.toString());
		
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		// TODO Auto-generated method stub
		/*
		if(target instanceof UserClientGroup ){
			
			UserClientGroup g=(UserClientGroup) target;
			
			if( !g.getName().equals(UsersPart.ALL_USERS_GROUP)){
			
			group=g.getGroup();
			return true;
			}
		}
		
		group=null;
		*/
		return true;
	}

}
