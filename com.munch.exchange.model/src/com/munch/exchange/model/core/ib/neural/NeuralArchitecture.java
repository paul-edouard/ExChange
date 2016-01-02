package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.munch.exchange.model.core.ib.Copyable;


@Entity
public class NeuralArchitecture implements Serializable, Copyable<NeuralArchitecture>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2002340365998939571L;

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;
	
	
	@OneToMany(mappedBy="neuralArchitecture",cascade=CascadeType.ALL)
	private List<NeuralNetwork> neuralNetworks;
	
	
	@Override
	public NeuralArchitecture copy() {
		// TODO Auto-generated method stub
		return null;
	}

}
