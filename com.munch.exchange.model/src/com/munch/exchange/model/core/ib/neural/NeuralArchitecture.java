package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.engine.network.activation.ActivationLinear;
import org.encog.engine.network.activation.ActivationRamp;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.mathutil.Equilateral;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.pattern.ElmanPattern;
import org.encog.neural.pattern.FeedForwardPattern;
import org.encog.neural.pattern.JordanPattern;
import org.encog.neural.pattern.NeuralNetworkPattern;

import com.munch.exchange.model.core.ib.Copyable;
import com.munch.exchange.model.core.ib.IbContract;


@Entity
public class NeuralArchitecture implements Serializable, Copyable<NeuralArchitecture>, CalculateScore{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2002340365998939571L;
	
	public static enum  ArchitectureType {
		FeedFoward, Elman, Jordan, Neat;
		
		public NeuralNetworkPattern getPattern(){
			if(this == FeedFoward){
				return new FeedForwardPattern();
			}
			else if(this == Elman){
				return new ElmanPattern();
			}
			else if(this == Jordan){
				return new JordanPattern();
			}
			else{
				return null;
			}
		}
	}
	
	public static enum Position{
		LONG,NEUTRAL,SHORT;
		
		public static int getInt(Position position){
			if(position==LONG){
				return 0;
			}
			else if(position==NEUTRAL){
				return 1;
			}
			else{
				return 2;
			}
		}
		
		public static Position getPosition(int intPos){
			if(intPos==0)
				return LONG;
			else if(intPos==2){
				return SHORT;
			}
			else{
				return NEUTRAL;
			}
		}
	}
	
	public static enum Activation{
		TANH,Linear,LOG,Ramp;
		
		public ActivationFunction getFunc(){
			if(this == TANH){
				return new ActivationTANH();
			}
			else if(this == Linear){
				return new ActivationLinear();
			}
			else if(this == LOG){
				return new ActivationLOG();
			}
			else if(this == Ramp){
				return new ActivationRamp();
			}
			else{
				return new ActivationTANH();
			}
		}
	}
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
 	private int id;
	
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CONFIGURATION_ID")
	private NeuralConfiguration neuralConfiguration;

	@OneToMany(mappedBy="neuralArchitecture",cascade=CascadeType.ALL)
	private List<NeuralNetwork> neuralNetworks=new LinkedList<NeuralNetwork>();
	
	@Enumerated(EnumType.STRING)
	private ArchitectureType type=ArchitectureType.FeedFoward;
	
	private String hiddenLayerDescription="";
	
	@Enumerated(EnumType.STRING)
	private Activation activation=Activation.TANH;
	
	@Transient
	private Equilateral equilateralOutput;
	
	public NeuralArchitecture() {
		super();
	}

	@Override
	public NeuralArchitecture copy() {
		NeuralArchitecture cp=new NeuralArchitecture();
		
		cp.id=this.id;
		cp.name=this.name;
//		cp.neuralConfiguration=this.neuralConfiguration;
		cp.type=this.type;
		cp.hiddenLayerDescription=this.hiddenLayerDescription;
		cp.activation=this.activation;
		
		cp.neuralNetworks=new LinkedList<NeuralNetwork>();
		for(NeuralNetwork network:this.neuralNetworks){
			cp.neuralNetworks.add(network.copy());
		}
		
		return cp;
	}
	
	@Override
	public double calculateScore(MLMethod method) {
		BasicNetwork basicNetwork=(BasicNetwork) method;
		
		
		
		return 0;
	}

	@Override
	public boolean shouldMinimize() {
		return false;
	}

	@Override
	public boolean requireSingleThreaded() {
		return false;
	}
	
	private  BasicNetwork createNetwork(){
		
		
//		Get the number of inputs
		int nbOfInputNeurons=0;
		for (NeuralInput input : neuralConfiguration.getNeuralInputs()) {
			nbOfInputNeurons+=input.getComponents().size();
		}
		
//		Get the number of output
		if(neuralConfiguration.getContract().allowShortPosition()){
			equilateralOutput=new Equilateral(3, -1, 1);
		}
		else{
			equilateralOutput=new Equilateral(2, -1, 1);
		}
		int nbOfOutputNeurons=encode(Position.NEUTRAL).length;
		
		int[] hiddenLayers=hiddenLayerDescriptionToIntArray();
		
		//Create the pattern
		NeuralNetworkPattern pattern=type.getPattern();
		if(pattern==null)return null;
		
		//Set the pattern
		pattern.setInputNeurons(nbOfInputNeurons);
		for(int i=0;i<hiddenLayers.length;i++){
			pattern.addHiddenLayer(hiddenLayers[i]);
		}
		pattern.setOutputNeurons(nbOfOutputNeurons);
		pattern.setActivationFunction(activation.getFunc());
		
		BasicNetwork network = (BasicNetwork)pattern.generate();
		network.reset();
		
		return network;
	}
	
	private int[] hiddenLayerDescriptionToIntArray(){
		if(hiddenLayerDescription == null|| hiddenLayerDescription.isEmpty())
			return null;
		
		String[] hidddenLayersDes=hiddenLayerDescription.split(",");
		int[] hiddenLayers=new int[hidddenLayersDes.length];
		for(int i=0;i<hidddenLayersDes.length;i++){
			hiddenLayers[i]=Integer.valueOf(hidddenLayersDes[i]);
		}
		
		return hiddenLayers;
	}
	
	
	private double[] encode(Position pos){
		if(equilateralOutput==null)return null;
		
		return equilateralOutput.encode(Position.getInt(pos));
	}
	
	private Position decode(double[] activations){
		if(equilateralOutput==null)return Position.NEUTRAL;
		
		return Position.getPosition(equilateralOutput.decode(activations));
		
	}
	
	
//	#######################
//	##   GETTER & SETTER ##
//	#######################

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Activation getActivation() {
		return activation;
	}

	public void setActivation(Activation activation) {
		this.activation = activation;
	}

	public NeuralConfiguration getNeuralConfiguration() {
		return neuralConfiguration;
	}

	public void setNeuralConfiguration(NeuralConfiguration neuralConfiguration) {
		this.neuralConfiguration = neuralConfiguration;
	}

	public List<NeuralNetwork> getNeuralNetworks() {
		return neuralNetworks;
	}

	public void setNeuralNetworks(List<NeuralNetwork> neuralNetworks) {
		this.neuralNetworks = neuralNetworks;
	}

	public ArchitectureType getType() {
		return type;
	}

	public void setType(ArchitectureType type) {
		this.type = type;
	}

	
	public String getHiddenLayerDescription() {
		return hiddenLayerDescription;
	}

	public void setHiddenLayerDescription(String hiddenLayerDescription) {
		this.hiddenLayerDescription = hiddenLayerDescription;
	}

	public Equilateral getEquilateralOutput() {
		return equilateralOutput;
	}

	public void setEquilateralOutput(Equilateral equilateralOutput) {
		this.equilateralOutput = equilateralOutput;
	}

	public static void main(String args[])
	{
		
		Equilateral equilateralOutput=new Equilateral(3, -1, 1);
		double[] encodeLong=equilateralOutput.encode(Position.getInt(Position.LONG));
		System.out.println("Encode LONG: "+Arrays.toString(encodeLong));
		
		double[] encodeNeutral=equilateralOutput.encode(Position.getInt(Position.NEUTRAL));
		System.out.println("Encode NEUTRAL: "+Arrays.toString(encodeNeutral));
		
		double[] encodeShort=equilateralOutput.encode(Position.getInt(Position.SHORT));
		System.out.println("Encode SHORT: "+Arrays.toString(encodeShort));
		
		double[] testDecode={-0.7,-0.7};
		Position position=Position.getPosition(equilateralOutput.decode(testDecode));
		System.out.println("Decoden "+Arrays.toString(testDecode)+": "+position.toString());
		
	}
	
	
	

}
