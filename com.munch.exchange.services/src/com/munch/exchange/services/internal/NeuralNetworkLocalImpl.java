package com.munch.exchange.services.internal;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.Logger;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NeuralNetwork;
import com.munch.exchange.model.core.neuralnetwork.PeriodType;
import com.munch.exchange.model.core.neuralnetwork.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.TimeSeriesCategory;
import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.INeuralNetworkProvider;


public class NeuralNetworkLocalImpl implements INeuralNetworkProvider {
	
	
	final private static String NeuronalNetworkPathStr="NeuronalNetwork";
	final private static String NeuronalNetworkFileStr="NeuronalNetwork.xml";
	
	private static Logger logger = Logger.getLogger(NeuralNetworkLocalImpl.class);
	
	
	private String getSavePath(Stock stock){
		File dir=new File(stock.getDataPath()+File.separator+NeuronalNetworkPathStr);
		if(dir.exists()){
			return dir.getAbsolutePath();
		}
		
		if(dir.mkdirs()){
			return dir.getAbsolutePath();
		}
		return "";
	}
	
	private String getFileName(Stock stock, String str){
		return this.getSavePath(stock)+File.separator+str;
	}
	
	

	@Override
	public boolean load(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,NeuronalNetworkFileStr));
		NeuralNetwork network=new NeuralNetwork();
		if(localFile.exists()){
			//TODO loading neural network info
			
			if( Xml.load(network, localFile.getAbsolutePath())){
				stock.setNeuralNetwork(network);
				logger.info("Neural Network localy found for "+stock.getFullName());
				return true;
			}
			
		}
		else{
			stock.setNeuralNetwork(network);
			logger.info("New neural network created for "+stock.getFullName());
			return true;
		}
		
		logger.info("No neuronal network saved for "+stock.getFullName());
		return false;
	}

	@Override
	public boolean save(Stock stock) {
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,NeuronalNetworkFileStr);
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getNeuralNetwork(), fileStr);
	}

	
	@Override
	public void createAllInputPoints(Stock stock) {
		LinkedList<HistoricalPoint> hisPointList=stock.getHistoricalData().getNoneEmptyPoints();
		Configuration config=stock.getNeuralNetwork().getConfiguration();
		//TODO
		
		//Step 1: search the last available input point date
		if(config.getPeriod()==PeriodType.DAY){
			//Rate Series
			for(TimeSeries series:config.getTimeSeriesFromCategory(TimeSeriesCategory.RATE)){
				int nbOfValues=series.getNumberOfPastValues();
				config.setLastInputPointDate(hisPointList.get(nbOfValues-1).getDate());
			}
		}
		//Last output point
		config.setLastInputPointDate(config.getOutputPointList().getFirst().getDate());
		
		
		//Step 2: create the input value lists
		if(config.getPeriod()==PeriodType.DAY){
			//Rate Series
			for(TimeSeries series:config.getTimeSeriesFromCategory(TimeSeriesCategory.RATE)){
				series.getInputValues().clear();
				for(HistoricalPoint his_point:hisPointList){
					ValuePoint point=new ValuePoint(his_point.getDate(),his_point.get(series.getName()));
					series.getInputValues().add(point);
				}
			}	
		}
		
		
	}

	@Override
	public ValuePointList calculateMaxProfitOutputList(Stock stock) {
		ValuePointList list=new ValuePointList();
		LinkedList<HistoricalPoint> pList=stock.getHistoricalData().getNoneEmptyPoints();
		
		HistoricalPoint lastPoint=null;
		for(HistoricalPoint point:pList){
			if(lastPoint!=null){
				float diff=point.get(DatePoint.FIELD_Close)-lastPoint.get(DatePoint.FIELD_Close);
				if(diff>0){
					list.add(new ValuePoint(lastPoint.getDate(), 1.0));
				}
				else{
					list.add(new ValuePoint(lastPoint.getDate(), 0.0));
				}
			}
			lastPoint=point;
		}
		
		return list;
	}
	
	private class BlockPoint{
		public ValuePoint val_point;
		public double diff;
		public double startValue;
		public double endValue;
		
		public BlockPoint(ValuePoint val_point, double startValue,double endValue){
			this.val_point=val_point;
			this.diff=endValue-startValue;
			this.startValue=startValue;
			this.endValue=endValue;
		}
	}
	
	private class Block extends LinkedList<BlockPoint>{
		
		private boolean isFixedCalculated=false;
		private boolean isFixed=false;
		private double sum;
		private double penalty;
		
		public Block(LinkedList<HistoricalPoint> pList,double penalty){
			this.penalty=penalty;
			
			HistoricalPoint lastPoint=null;
			for(HistoricalPoint point:pList){
				if(lastPoint!=null){
					double startValue=lastPoint.get(DatePoint.FIELD_Close);
					double endValue=point.get(DatePoint.FIELD_Close);
					double diff=endValue-startValue;
					ValuePoint vpoint=null;
					if(diff>0){
						vpoint=new ValuePoint(lastPoint.getDate(), 1.0);
					}
					else{
						vpoint=new ValuePoint(lastPoint.getDate(), 0.0);
					}
					
					this.add(new BlockPoint(vpoint, startValue, endValue));
					
				}
				lastPoint=point;
			}
		}
		
		public double getStartValue(){
			return this.getFirst().startValue;
		}
		
		public double getEndValue(){
			return this.getLast().endValue;
		}
		
		public Block(double penalty){
			this.penalty=penalty;
		}
		
		public BlockList split(){
			BlockList bl=new BlockList();
			
			double currentValue=this.getFirst().val_point.getValue();
			bl.add(new Block(this.penalty));
			for(BlockPoint point:this){
				if(currentValue!=point.val_point.getValue())
					bl.add(new Block(this.penalty));
				
				bl.getLast().add(point);
				currentValue=point.val_point.getValue();
			}
			
			
			for(Block block:bl)
				block.calculateDiffSum();
			
			return bl;
		}
		
		
		public void calculateDiffSum(){
			sum=0;
			for(BlockPoint p:this){
				sum+=p.diff;
			}
			
			//logger.info("Block sum: "+sum+", penalty val: "+((this.getStartValue()+this.getEndValue())*penalty));
			
			if(Math.abs(sum)>=(this.getStartValue()+this.getEndValue())*penalty){
				isFixed=true;
			}
			
			//logger.info("Is Fix: "+isFixed);
			
		}
		
		public void setValue(double val){
			for(BlockPoint p:this){
					p.val_point.setValue(val);
			}
		}
	}
	
	private class BlockList extends LinkedList<Block>{
		
		
		public ValuePointList toValuePointList(){
			ValuePointList list=new ValuePointList();
			
			for(Block b:this){
				for(BlockPoint bp:b){
					list.add(bp.val_point);
				}
			}
			return list;
		}
		
		
		void isolateNoFixedBlocks(){
			
			//boolean isolatedEnd=false;
			boolean isolatedStart=!this.getFirst().isFixed;
			BlockList noneFixedBlockList=new BlockList();
			for(Block b:this){
				noneFixedBlockList.add(b);
				
				if(noneFixedBlockList.size()==1){
					continue;
				}
				
				if(noneFixedBlockList.size()==2 && 
						noneFixedBlockList.getFirst().isFixed &&
						noneFixedBlockList.getLast().isFixed){
					noneFixedBlockList.clear();
					noneFixedBlockList.add(b);
					continue;
				}
				
			
				if(b.isFixed && (noneFixedBlockList.size()>1 || isolatedStart==true)){
					isolatedStart=false;
					
					//logger.info("None Fix Size: "+noneFixedBlockList.size()+", middle: "+noneFixedBlockList.get(1).isFixed);
					noneFixedBlockList.optimizeBlockListProfit();
					noneFixedBlockList.clear();
					noneFixedBlockList.add(b);
				}
			}
			if(noneFixedBlockList.size()>1)
				noneFixedBlockList.optimizeBlockListProfit();
			
		}
		
		private void optimizeBlockListProfit(){
			
			int startPos=0;
			int endPos=this.size()-1;
			if(this.getFirst().isFixed)startPos++;
			if(this.getLast().isFixed)endPos--;
			
			if(startPos>=this.size())return;
			if(endPos<0)return;
			
			int pow=endPos-startPos+1;
			double[][] combiMatrix=createCombiMatrix(pow);
			
			
			int bestColumnPos=0;
			double bestProfit=Double.MIN_VALUE;
			
			int row=(int)Math.pow(2, pow);
			
			//logger.info("Block Size: "+this.size()+",Pow: "+pow+", Column: "+row);
			
			for(int i=0;i<row;i++){
				for(int j=0;j<pow;j++){
					this.get(j+startPos).setValue(combiMatrix[j][i]);
				}
				
				double profit=this.calculateBlockProfit();
				if(profit>bestProfit){
					bestProfit=profit;
					bestColumnPos=i;
					//logger.info("Profit: "+profit);
				}
			}
			
			for(int j=0;j<pow;j++){
				this.get(j+startPos).setValue(combiMatrix[j][bestColumnPos]);
			}
			
		}
		
		private double[][] createCombiMatrix(int pow){
			int row=(int)Math.pow(2, pow);
			double[][] matrix=new double[pow][row];
			
			for(int i=0;i<pow;i++){
				for(int j=0;j<row;j++){
					matrix[i][j]=0;
				}
				
				int t=(int)Math.pow(2, i);
				for(int j=0;j<row;j+=2*t){
					for(int k=j;k<j+t;k++){
						matrix[i][k]=1;
					}
				}
				
				
			}
			
			return matrix;
		}
		
		
		private double calculateBlockProfit(){
			double profit=0;
			
			for(int i=0;i<this.size();i++){
				Block last=null;
				if(i>0)last=this.get(i-1);
				Block current=this.get(i);
				Block next=null;
				if(i<this.size()-1)next=this.get(i+1);
				
				if(current.getFirst().val_point.getValue()>0){
					profit+=current.sum;
					if(last==null || last.getFirst().val_point.getValue()==0)
						profit-=current.penalty*current.getStartValue();
					if(next==null ||  next.getFirst().val_point.getValue()==0)
						profit-=current.penalty*current.getEndValue();
				}
			}
			
			return profit;
		}
		
	}
	
	
	@Override
	public ValuePointList calculateMaxProfitOutputList(Stock stock,
			double penalty) {
		//ValuePointList list=new ValuePointList();
		LinkedList<HistoricalPoint> pList=stock.getHistoricalData().getNoneEmptyPoints();
		BlockList blockList=new Block(pList,penalty).split();
		blockList.isolateNoFixedBlocks();
		
		return blockList.toValuePointList();
	}
	
	
	/*
	@Override
	public ValuePointList calculateMaxProfitOutputList(Stock stock,
			double penalty) {
		
		ValuePointList list=new ValuePointList();
		LinkedList<HistoricalPoint> pList=stock.getHistoricalData().getNoneEmptyPoints();
		int i=0;
		for(HistoricalPoint point:pList){
			if(i<pList.size()-1)
				list.add(new ValuePoint(point.getDate(), 0.0));
			i++;
		}
		
		double limit=2*penalty;
		
		i=0;
		int j=i;
		boolean bought=false;
		while(i<pList.size()-1 && j<pList.size()-1){
			HistoricalPoint currentPoint=pList.get(i);
			//Get next cross limit index
			double diff=0;
			double value=currentPoint.get(DatePoint.FIELD_Close);
			
			if(bought){
				double n_value=pList.get(i+1).get(DatePoint.FIELD_Close);
				if(n_value>value){
					list.get(i).setValue(1.0);
					i=i+1;
					continue;
				}
			}
			
			
			
			int lastMinIndex=i;
			int lastMaxIndex=i;
			double lastMinValue=value;
			double lastMaxValue=value;
			//logger.info("0, i: "+i+", j: "+j);
			
			while(Math.abs(diff)<limit){
				j++;
				if(j>=pList.size()-1)break;
				double n_value=pList.get(j).get(DatePoint.FIELD_Close);
				
				if(n_value>=lastMaxValue){
					lastMaxValue=n_value;lastMaxIndex=j;
				}
				if(n_value<=lastMinValue){
					lastMinValue=n_value;lastMinIndex=j;
				}
				
				if(lastMaxIndex>lastMinIndex){
					if(bought)
						diff = (lastMaxValue - value)/value;
					else
						diff = (lastMaxValue - lastMinValue)/lastMinValue;
				}
				else{
					if(bought)
						diff = (lastMinValue - value)/value;
					else
						diff = (lastMinValue - lastMaxValue)/lastMaxValue;
				}
			}
			//logger.info("1, i: "+i+", j: "+j);
			if(diff>0){
				if(bought){
					for(int k=i;k<lastMaxIndex;k++){
						list.get(k).setValue(1.0);
					}
				}
				else{
					for(int k=lastMinIndex;k<lastMaxIndex;k++){
						list.get(k).setValue(1.0);
					}
				}
				i=lastMaxIndex;
				bought=true;
			}
			else{
				if(bought){
					for(int k=i;k<lastMaxIndex;k++){
						list.get(k).setValue(1.0);
					}
				}
				i=lastMinIndex;
				bought=false;
			}
			
			//logger.info("2, i: "+i+", j: "+j);
			
			
		}
		
		
		
		return list;
	}
	*/
	
	public static void main(String[] args){
		int pow=4;
		int row=(int)Math.pow(2, pow);
		double[][] matrix=new double[pow][row];
		
		for(int i=0;i<pow;i++){
			for(int j=0;j<row;j++){
				matrix[i][j]=0;
			}
			
			int t=(int)Math.pow(2, i);
			for(int j=0;j<row;j+=2*t){
				for(int k=j;k<j+t;k++){
					matrix[i][k]=1;
				}
			}
			
			
		}
		
		String out="";
		for(int i=0;i<pow;i++){
			out+="[";
			for(int j=0;j<row;j++){
				out+=matrix[i][j]+", ";
			}
			out+="]\n";
		}
		System.out.println(out);
		
		
	}
	
	
}
