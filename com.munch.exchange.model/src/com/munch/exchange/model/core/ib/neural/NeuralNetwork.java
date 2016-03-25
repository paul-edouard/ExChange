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
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.encog.ml.MLMethod;
import org.encog.ml.ea.codec.GeneticCODEC;
import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.population.Population;
import org.encog.neural.hyperneat.HyperNEATCODEC;
import org.encog.neural.neat.NEATCODEC;
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
	
	private String networkName;
	
	@Lob
	private byte[] pareto;
	
	private String paretoName;
	
	
	@Transient
	NeuralNetworkRating trainingRating = new NeuralNetworkRating();
	
	@Transient
	NeuralNetworkRating backTestingRating = new NeuralNetworkRating();
	
	@Transient
	private NEATPopulation population=null;
	
	@Transient
	private NEATPopulation paretoPopulation=null;
	
	@Transient
	private HashMap<Genome, NeuralNetworkRating> trainingRatingMap = new HashMap<Genome, NeuralNetworkRating>();

	@Transient
	private HashMap<Genome, NeuralNetworkRating> backTestingRatingMap = new HashMap<Genome, NeuralNetworkRating>();
	
	
	
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
		
		if(network==null)return null;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(network);
		population = (NEATPopulation)EncogDirectoryPersistence.loadObject(bis);
		population.setName(getNetworkName());
		return population;
	}
	
	public NEATPopulation getParetoPopulation(){
		if(paretoPopulation!=null)return paretoPopulation;
		
		if(pareto==null)return null;
		
		ByteArrayInputStream bis = new ByteArrayInputStream(pareto);
		paretoPopulation = (NEATPopulation)EncogDirectoryPersistence.loadObject(bis);
		paretoPopulation.setName(getParetoName());
		return paretoPopulation;
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
	
	public void setParetoPopulation(Population population){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		EncogDirectoryPersistence.saveObject(bos,population);
		pareto = bos.toByteArray();
	}
	
	
	public boolean isNEAT(){
		
		if(this.neuralArchitecture.getType()==ArchitectureType.Neat 
				|| this.neuralArchitecture.getType()==ArchitectureType.HyperNeat
				|| this.neuralArchitecture.getType()==ArchitectureType.NoveltySearchNeat)
			return true;
		
		return false;
	}
	
	public void evaluateNEATPopulation(int nbOfBestGenomes){
		if(this.getNEATPopulation()!=null)
			evaluatePopulation(this.getNEATPopulation(), nbOfBestGenomes);
	
	}
	
	public void evaluateParetoPopulation(){
		if(this.getParetoPopulation()!=null){
			evaluatePopulation(this.getParetoPopulation(), Integer.MAX_VALUE);
			
			
//			System.out.println("Evaluation of pareto population: size:" +this.getParetoPopulation().flatten().size());
		}
		
		
		
		
	}
	
	private void evaluatePopulation(NEATPopulation pop, int nbOfBestGenomes){
		
		System.out.println("Evaluation of population: "+pop.getName());
		
		
		GeneticCODEC codec=new NEATCODEC();
		
		List<Genome> genomes=pop.flatten();
		this.getNeuralArchitecture().prepareScoring(1, 0);
		
		if(this.getNeuralArchitecture().getType()==ArchitectureType.HyperNeat){
			codec=new HyperNEATCODEC();
			this.getNEATPopulation().setSubstrate(
					this.getNeuralArchitecture().createHyperNeatSubstrat());
		}
		
		NeuralConfiguration neuralConfiguration=this.getNeuralArchitecture().getNeuralConfiguration();
		
		int i=0;
		for(Genome genome:genomes){
//			System.out.println("Evaluation of genomes: "+i);
			
			if(i>=nbOfBestGenomes)break;
			
			System.out.println("Evaluation of genomes start: "+i);
			
			MLMethod method=(NEATNetwork)codec.decode(genome);
			
//			Evaluate the Training data set
			NeuralNetworkRating trainingRating=this.getNeuralArchitecture().calculateProfitAndRiskOfBlocks(
					neuralConfiguration.getTrainingBlocks(), method);
//			System.out.println("score training:"+trainingRating.getScore());
			trainingRating.setName("Training");
			trainingRatingMap.put(genome, trainingRating);
			
			
//			Evaluate the back testing data set
			NeuralNetworkRating backTestingRating=this.getNeuralArchitecture().calculateProfitAndRiskOfBlocks(
					neuralConfiguration.getBackTestingBlocks(), method);
			backTestingRating.setName("Back Testing");
			backTestingRatingMap.put(genome, backTestingRating);
			
			i++;
		}
		
		System.out.println("backTestingRatingMap size: "+backTestingRatingMap.size());
		
		
	}
	
	
	
	
	
	
	
	@Override
	public NeuralNetwork copy() {
		NeuralNetwork c=new NeuralNetwork();
		
		c.id=id;
		c.network=network;
		c.pareto=pareto;
		
		c.paretoName=paretoName;
		c.networkName=networkName;
		
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
	
	
	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getParetoName() {
		return paretoName;
	}

	public void setParetoName(String paretoName) {
		this.paretoName = paretoName;
	}

	public HashMap<Genome, NeuralNetworkRating> getTrainingRatingMap() {
		return trainingRatingMap;
	}

	public HashMap<Genome, NeuralNetworkRating> getBackTestingRatingMap() {
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
