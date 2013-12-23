package com.munch.exchange.services;

import com.munch.exchange.model.core.Project;

public interface IProjectProvider {
	
	/**
	 * save the given project
	 * 
	 * @param p
	 * @return
	 */
	public boolean save(Project p);
	
	
	/**
	 * tries to delete a project
	 * 
	 * @param p
	 * @return
	 */
	public boolean delete(Project p);


}
