package com.munch.exchange.model.core.ib.neural;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import com.munch.exchange.model.core.ib.Copyable;


@Entity
public class NeuralNetwork implements Serializable, Copyable<NeuralNetwork>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8813545243757215277L;

	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ARCHITECTURE_ID")
	private NeuralArchitecture neuralArchitecture;
	
	
	@Lob
	private byte[] network;
	
	
	public NeuralNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BasicNetwork getNetwork(){
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		BasicNetwork basicNetwork = (BasicNetwork)EncogDirectoryPersistence.loadObject(bis);
		return basicNetwork;
	}
	
	public void setNetwork(BasicNetwork basicNetwork){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,basicNetwork);
		network = bos.toByteArray();
	}
	
	
	
	@Override
	public NeuralNetwork copy() {
		NeuralNetwork c=new NeuralNetwork();
		
		c.id=id;
		c.network=network;
		
		return c;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public NeuralArchitecture getNeuralArchitecture() {
		return neuralArchitecture;
	}

	public void setNeuralArchitecture(NeuralArchitecture neuralArchitecture) {
		this.neuralArchitecture = neuralArchitecture;
	}

	public void setNetwork(byte[] network) {
		this.network = network;
	}
	
	
	

}
