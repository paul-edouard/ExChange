package com.munch.exchange.model.core.ib.neural;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.encog.ml.ea.population.Population;
import org.encog.neural.neat.NEATNetwork;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;







import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;


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
	NeuralNetworkRating trainingRating = new NeuralNetworkRating();
	
	@Transient
	NeuralNetworkRating backTestingRating = new NeuralNetworkRating();
	
	@Transient
	private NEATPopulation population=null;
	
	@Transient
	private HashMap<NEATNetwork, NeuralNetworkRating> trainingRatingMap = new HashMap<NEATNetwork, NeuralNetworkRating>();

	@Transient
	private HashMap<NEATNetwork, NeuralNetworkRating> backTestingRatingMap = new HashMap<NEATNetwork, NeuralNetworkRating>();
	
	
	
	public NeuralNetwork() {
		super();
	}

	public BasicNetwork getNetwork(){
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		BasicNetwork basicNetwork = (BasicNetwork)EncogDirectoryPersistence.loadObject(bis);
		return basicNetwork;
	}
	
	public NEATPopulation getNEATPopulation(){
		if(population!=null)return population;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		population = (NEATPopulation)EncogDirectoryPersistence.loadObject(bis);
		return population;
	}
	
	
	public void setNetwork(BasicNetwork basicNetwork){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,basicNetwork);
		network = bos.toByteArray();
	}
	
	public void setNEATPopulation(Population population){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,population);
		network = bos.toByteArray();
	}
	
	public boolean isNEAT(){
		
		if(this.neuralArchitecture.getType()==ArchitectureType.Neat || this.neuralArchitecture.getType()==ArchitectureType.HyperNeat)
			return true;
		
		return false;
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
	
	
	public NeuralNetworkRating getTrainingRating() {
		return trainingRating;
	}

	public void setTrainingRating(NeuralNetworkRating trainingRating) {
		this.trainingRating = trainingRating;
	}

	public NeuralNetworkRating getBackTestingRating() {
		return backTestingRating;
	}

	public void setBackTestingRating(NeuralNetworkRating backTestingRating) {
		this.backTestingRating = backTestingRating;
	}

	public HashMap<NEATNetwork, NeuralNetworkRating> getTrainingRatingMap() {
		return trainingRatingMap;
	}

	public HashMap<NEATNetwork, NeuralNetworkRating> getBackTestingRatingMap() {
		return backTestingRatingMap;
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
