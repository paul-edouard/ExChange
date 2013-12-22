package com.munch.exchange.model.core;



public interface ProjectDAOIF {
	
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
