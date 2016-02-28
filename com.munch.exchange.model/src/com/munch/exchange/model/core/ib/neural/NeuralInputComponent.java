package com.munch.exchange.model.core.ib.neural;

import java.io.Serializable;
import java.util.LinkedList;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import com.munch.exchange.model.core.ib.Copyable;

@Entity
public class NeuralInputComponent implements Serializable, Copyable<NeuralInputComponent>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4382165162373283836L;
	
	
	public static enum ComponentType {
		DIRECT, DIFF, MEAN;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Enumerated(EnumType.STRING)
	private ComponentType componentType=ComponentType.DIRECT;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="INPUT_ID")
	private NeuralInput neuralInput;
	
	
	private int offset=0;
	
	private int period=1;
	
	private double upperRange;
	
	private double lowerRange;
	
	@Transient
	private double[] values;
	
	@Transient
	private double[] times;
	
	@Transient
	private double[] adaptedValues;
	
	@Transient
	private double[] adaptedtimes;
	
	@Transient
	private NormalizedField normalizedField;
	
	@Transient
	private double[] normalizedValues;
	

	public NeuralInputComponent() {
		super();
	}

	@Override
	public NeuralInputComponent copy() {
		NeuralInputComponent c=new NeuralInputComponent();
		
		c.id=id;
		
		c.componentType=componentType;
		
		c.neuralInput=neuralInput;
		c.offset=offset;
		c.period=period;
		
		c.upperRange=upperRange;
		c.lowerRange=lowerRange;
		
		return c;
	}
	
	
	public String getName(){
		String name=neuralInput.getName()+" "+componentType.toString()+" "+offset;
		switch (this.componentType) {
		case DIFF:
			name+=" "+period;
			break;
		case MEAN:
			name+=" "+period;
			break;
		default:
			break;
		}
		
		
		return name;
	}
	
	public void computeValues(){
		
		if (values == null || times == null || values.length == 0
				|| times.length == 0) {

			double[][] valuesTimes = null;
			switch (componentType) {
			case DIRECT:
				valuesTimes = computeDirect(neuralInput.getValues(),
						neuralInput.getTimes(), offset);
				break;
			case DIFF:
				valuesTimes = computeDiff(neuralInput.getValues(),
						neuralInput.getTimes(), offset, period);
				break;
			case MEAN:
				valuesTimes = computeMean(neuralInput.getValues(),
						neuralInput.getTimes(), offset, period);
				break;
			}
			if (valuesTimes == null)
				return;

			values = valuesTimes[0];
			times = valuesTimes[1];
		}
	}
	
	public void computeRanges(){
		if(values==null)return;
		
		upperRange=Double.NEGATIVE_INFINITY;
		lowerRange=Double.POSITIVE_INFINITY;
		
		for(int i=0;i<values.length;i++){
			if(values[i]>upperRange){
				upperRange=values[i];
			}
			if(values[i]<lowerRange){
				lowerRange=values[i];
			}
		}
		
		
	}
	
	
	public void createNormalizedValues(double high, double low){
//		System.out.println("\nLower Range: "+lowerRange+", Upper Range: "+upperRange);
		if(normalizedField==null){
			normalizedField=new NormalizedField(NormalizationAction.Normalize, this.getName(), upperRange, lowerRange, high, low);
		}
		
		normalizedValues=new double[adaptedValues.length];
		for(int i=0;i<adaptedValues.length;i++){
			normalizedValues[i]=normalizedField.normalize(adaptedValues[i]);
//			if(i%1000==0)
//				System.out.println("Adpated Value: "+adaptedValues[i]+", Normalized Value: "+normalizedValues[i]);
		}
	}
	
	public double getNormalizedAdaptedValueAt(int i){
		return normalizedValues[i];
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

	public NeuralInput getNeuralInput() {
		return neuralInput;
	}

	public void setNeuralInput(NeuralInput neuralInput) {
		this.neuralInput = neuralInput;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public ComponentType getComponentType() {
		return componentType;
	}

	public void setComponentType(ComponentType componentType) {
		this.componentType = componentType;
	}

	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public double getUpperRange() {
		return upperRange;
	}

	public void setUpperRange(double upperRange) {
		this.upperRange = upperRange;
	}

	public double getLowerRange() {
		return lowerRange;
	}

	public void setLowerRange(double lowerRange) {
		this.lowerRange = lowerRange;
	}
	


	public double[] getValues() {
		return values;
	}
	


	public void setValues(double[] values) {
		this.values = values;
	}
	


	public double[] getTimes() {
		return times;
	}
	


	public void setTimes(double[] times) {
		this.times = times;
	}
	


	public double[] getAdaptedValues() {
		return adaptedValues;
	}
	


	public void setAdaptedValues(double[] adaptedValues) {
		this.adaptedValues = adaptedValues;
	}
	
	public void setAdaptedValues(LinkedList<Double> adaptedValues) {
		double[] adaptedValuesArray=new double[adaptedValues.size()];
		int i=0;
		for(Double value:adaptedValues){
			adaptedValuesArray[i]=value;
			i++;
		}
		this.setAdaptedValues(adaptedValuesArray);
	}
	


	public double[] getAdaptedtimes() {
		return adaptedtimes;
	}
	


	public void setAdaptedtimes(double[] adaptedtimes) {
		this.adaptedtimes = adaptedtimes;
	}
	
	public void setAdaptedtimes(LinkedList<Double> adaptedTimes) {
		double[] adaptedTimesArray=new double[adaptedTimes.size()];
		int i=0;
		for(Double time:adaptedTimes){
			adaptedTimesArray[i]=time;
			i++;
		}
		this.setAdaptedtimes(adaptedTimesArray);
	}
	
	
	private static double[][] computeDirect(double[] in_values, double[] in_times, int offset){
		if(in_values.length !=in_times.length)return null;
		int size=in_values.length-offset;
		if(size<1)return null;
		
		double[] values=new double[size];
		double[] times=new double[size];
		
		for(int i=0;i<size;i++){
			values[i]=in_values[i];
			times[i]=in_times[i+offset];
		}
		
		double[][] valuesTimes={values, times};
		return valuesTimes;
	}
	
	private static double[][] computeDiff(double[] in_values, double[] in_times, int offset, int period){
		double[][] in_valuesTimes = computeDirect(in_values, in_times, offset);
		if(in_valuesTimes==null)return null;
		
		int size=in_valuesTimes[0].length-period;
		double[] values=new double[size];
		double[] times=new double[size];
		
		for(int i=0;i<size;i++){
			values[i]=in_valuesTimes[0][i+period]-in_valuesTimes[0][i];
			times[i]=in_valuesTimes[1][i+period];
		}
		
		double[][] valuesTimes={values, times};
		return valuesTimes;
	}
	
	private static double[][] computeMean(double[] in_values, double[] in_times, int offset, int period){
		double[][] in_valuesTimes = computeDirect(in_values, in_times, offset);
		if(in_valuesTimes==null)return null;
		
		int size=in_valuesTimes[0].length-period;
		double[] values=new double[size];
		double[] times=new double[size];
		
		for(int i=0;i<size;i++){
			for(int j=0;j<=period;j++){
				values[i]+=in_valuesTimes[0][i+j];
			}
			values[i]/=period;
			times[i]=in_valuesTimes[1][i+period];
		}
		
		double[][] valuesTimes={values, times};
		return valuesTimes;
	}
	
	
	public static void main(String args[])
	{
		NormalizedField normalizedField=new NormalizedField(NormalizationAction.Normalize, "Test", 0.03843399999999941, -0.059494499999999784, 0.9, -0.9);
		
		System.out.println(normalizedField.normalize(-2.9199999999995896E-4));
		
	}

}
