package com.munch.exchange.model.core.ib.neural;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.obj.SerializeObject;

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
	
	@Transient
	private double score;
	
	@Transient
	private double trainingProfit;
	
	@Transient
	private double trainingRisk;
	
	@Transient
	private double backTestingProfit;
	
	@Transient
	private double backTestingRisk;
	
	
	
	public NeuralNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}

	public BasicNetwork getNetwork(){
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		BasicNetwork basicNetwork = (BasicNetwork)EncogDirectoryPersistence.loadObject(bis);
		return basicNetwork;
	}
	
	public NEATNetwork getNEATNetwork(){
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		NEATNetwork neatNetwork = (NEATNetwork)EncogDirectoryPersistence.loadObject(bis);
		return neatNetwork;
	}
	
	
	public void setNetwork(BasicNetwork basicNetwork){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,basicNetwork);
		network = bos.toByteArray();
	}
	
	public void setNetwork(NEATNetwork neatNetwork){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,neatNetwork);
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
	
	

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getTrainingProfit() {
		return trainingProfit;
	}

	public void setTrainingProfit(double trainingProfit) {
		this.trainingProfit = trainingProfit;
	}

	public double getTrainingRisk() {
		return trainingRisk;
	}

	public void setTrainingRisk(double trainingRisk) {
		this.trainingRisk = trainingRisk;
	}

	public double getBackTestingProfit() {
		return backTestingProfit;
	}

	public void setBackTestingProfit(double backTestingProfit) {
		this.backTestingProfit = backTestingProfit;
	}

	public double getBackTestingRisk() {
		return backTestingRisk;
	}

	public void setBackTestingRisk(double backTestingRisk) {
		this.backTestingRisk = backTestingRisk;
	}
	
	
	public static Serializable load(InputStream inputStream)
			throws IOException, ClassNotFoundException {
		Serializable object;
		ObjectInputStream in = null;
		in = new ObjectInputStream(inputStream);
		object = (Serializable) in.readObject();
		in.close();
		return object;
	}

	public static void save(OutputStream outputStream, final Serializable object)
			throws IOException {
		ObjectOutputStream out = null;

		out = new ObjectOutputStream(outputStream);
		out.writeObject(object);
		out.close();
	}
	

}
