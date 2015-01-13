package com.munch.exchange.services.internal;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.munch.exchange.model.core.DatePoint;
import com.munch.exchange.model.core.ExchangeRate;
import com.munch.exchange.model.core.Stock;
import com.munch.exchange.model.core.historical.HistoricalPoint;
import com.munch.exchange.model.core.neuralnetwork.Configuration;
import com.munch.exchange.model.core.neuralnetwork.NNetwork;
import com.munch.exchange.model.core.neuralnetwork.NetworkArchitecture;
import com.munch.exchange.model.core.neuralnetwork.PeriodType;
import com.munch.exchange.model.core.neuralnetwork.TimeSeries;
import com.munch.exchange.model.core.neuralnetwork.TimeSeriesCategory;
import com.munch.exchange.model.core.neuralnetwork.ValuePoint;
import com.munch.exchange.model.core.neuralnetwork.ValuePointList;
import com.munch.exchange.model.tool.DateTool;
import com.munch.exchange.model.xml.Xml;
import com.munch.exchange.services.IHistoricalDataProvider;
import com.munch.exchange.services.INeuralNetworkProvider;
import com.munch.exchange.utils.ProfitUtils;


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
	public synchronized boolean load(Stock stock) {
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		//if(!stock.getHistoricalData().isEmpty())return false;
		
		File localFile=new File(getFileName(stock,NeuronalNetworkFileStr));
		NNetwork network=new NNetwork();
		if(localFile.exists()){
			//Set the Network Save Path
			//NetworkArchitecture.setNetworkSavePath(this.getSavePath(stock));
			
			if( Xml.load(network, localFile.getAbsolutePath())){
				
				String localPath=this.getNetworkArchitecturesLocalSavePath(stock);
				//Set the local path of all NetworkArchitectures
				for(Configuration config:network.getConfigurations()){
					for(NetworkArchitecture archi:config.getNetworkArchitectures())
						archi.setLocalSavePath(localPath);
				}
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
	
	
	public String getNetworkArchitecturesLocalSavePath(Stock stock){
		if(stock==null)return "";
		if(stock.getDataPath()==null)return "";
		if(stock.getDataPath().isEmpty())return "";
		
		String path=this.getSavePath(stock);
		File dirPath=new File(path);
		if(!dirPath.isDirectory())return "";
		
		return path;
	}
	
	
	/*
	public synchronized boolean loadArchitectureResults(Stock stock, NetworkArchitecture archi){
		if(stock==null)return false;
		if(stock.getDataPath()==null)return false;
		if(stock.getDataPath().isEmpty())return false;
		
		if(archi==null)return false;
		
		if(archi.isResultLoaded())return true;
		
		File dirPath=new File(this.getSavePath(stock));
		if(!dirPath.isDirectory())return false;
		
		archi.loadResultsFromPath(this.getSavePath(stock));
		return true;
		
	}*/

	@Override
	public synchronized boolean save(Stock stock) {
		if(stock==null)return false;
		
		String fileStr=getFileName(stock,NeuronalNetworkFileStr);
		
		//Set the Network Save Path
		//NetworkArchitecture.setNetworkSavePath(this.getSavePath(stock));
		
		
		logger.info("Writing file: "+fileStr);
		return Xml.save(stock.getNeuralNetwork(), fileStr);
	}
	
	/*
	public synchronized boolean saveArchitectureResults(Stock stock, NetworkArchitecture archi){
		if(stock==null)return false;
		if(archi==null)return false;
		
		String savePath=this.getSavePath(stock);
		if(savePath.isEmpty())return false;
		
		archi.saveResultsToPath(savePath);
		return true;
		
	}
	*/

	
	@Override
	public synchronized void createAllValuePoints(Configuration configuration, boolean forceCreation) {
		
		if(!forceCreation){
			if(configuration.areAllTimeSeriesAvailable() 
					&& configuration.getOutputPointList()!=null 
					&& !configuration.getOutputPointList().isEmpty())
			
			return;
		}
		
		//Test if the stock has a defined parent
		if(configuration.getParent()==null){
			logger.error("The configation has not defined parent!");
			return;
		}
		Stock stock=configuration.getParent();
		
		//Check if the historical data are loaded
		LinkedList<HistoricalPoint> hisPointList=stock.getHistoricalData().getNoneEmptyPoints();
		if(hisPointList.isEmpty()){
			logger.error("No historical data found! Please load them first");
			return;
		}
		
		
		//TODO
		
		//=======================================================
		//==Step 0: Create the Output Value Points             ==
		//=======================================================
		ValuePointList outputPointList=calculateMaxProfitOutputList(stock, ProfitUtils.PENALTY );
		configuration.setOutputPointList(outputPointList);
		if(configuration.getOutputPointList().isEmpty()){
			logger.error("The output point list is empty");
			return;
		}
		
		//=======================================================
		//==Step 1: search the last available input point date ==
		//=======================================================
		if(configuration.getPeriod()==PeriodType.DAY){
			//Rate Series
			for(TimeSeries series:configuration.getTimeSeriesFromCategory(TimeSeriesCategory.RATE)){
				int nbOfValues=series.getNumberOfPastValues();
				configuration.setLastInputPointDate(hisPointList.get(nbOfValues-1).getDate());
			}
			
			//Target Output
			for(TimeSeries series:configuration.getTimeSeriesFromCategory(TimeSeriesCategory.TARGET_OUTPUT)){
				int nbOfValues=series.getNumberOfPastValues();
				configuration.setLastInputPointDate(outputPointList.get(nbOfValues).getDate());
			}
			
		}
		//Last output point
		configuration.setLastInputPointDate(configuration.getOutputPointList().getFirst().getDate());
		
		logger.info("Last output point: "+DateTool.dateToDayString(configuration.getLastInputPointDate()));
		
		
		//===========================================
		//==  Step 2: create the input value lists ==
		//===========================================
		if(configuration.getPeriod()==PeriodType.DAY){
			//Rate Series
			for(TimeSeries series:configuration.getTimeSeriesFromCategory(TimeSeriesCategory.RATE)){
				series.getInputValues().clear();
				for(HistoricalPoint his_point:hisPointList){
					ValuePoint point=new ValuePoint(his_point.getDate(),his_point.get(series.getName()));
					//if(point.getDate().before(configuration.getLastInputPointDate()))continue;
					//======================================
					//==Set the next value date: + 1 DAY  ==
					//======================================
					Calendar expectedNextValue=Calendar.getInstance();
					expectedNextValue.setTimeInMillis(his_point.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
					point.setNextValueDate(expectedNextValue);
					
					series.getInputValues().add(point);
				}
				
				logger.info("Series: "+series.getName()
						+", first point: "+DateTool.dateToDayString(series.getInputValues().getFirst().getDate())
						+", last point: "+DateTool.dateToDayString(series.getInputValues().getLast().getDate())
						+", Size:"+series.getInputValues().size());
			}
			//Financial Series
			/*
			for(TimeSeries series:configuration.getTimeSeriesFromCategory(TimeSeriesCategory.FINANCIAL)){
				//series.adaptInputValuesToMasterValuePointList(masterValuePointList);
			}
			*/
			
			//Target Output
			for(TimeSeries series:configuration.getTimeSeriesFromCategory(TimeSeriesCategory.TARGET_OUTPUT)){
				series.getInputValues().clear();
				ValuePoint lastPoint=null;
				for(ValuePoint point:outputPointList){
					if(lastPoint!=null){
						ValuePoint outputPoint=new ValuePoint(point.getDate(), lastPoint.getValue());
						//if(point.getDate().before(configuration.getLastInputPointDate()))continue;
						//======================================
						//==Set the next value date: + 1 DAY  ==
						//======================================
						Calendar expectedNextValue=Calendar.getInstance();
						expectedNextValue.setTimeInMillis(point.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
						outputPoint.setNextValueDate(expectedNextValue);
						
						series.getInputValues().add(outputPoint);
					}
					lastPoint=point;
				}
				
				//Create the last point
				Calendar lastValueDate=Calendar.getInstance();
				lastValueDate.setTimeInMillis(lastPoint.getDate().getTimeInMillis()+PeriodType.DAY.getPeriod());
				ValuePoint lastOutputPoint=new ValuePoint(lastValueDate, lastPoint.getValue());
				
				Calendar expectedNextValue=Calendar.getInstance();
				expectedNextValue.setTimeInMillis(lastValueDate.getTimeInMillis()+PeriodType.DAY.getPeriod());
				lastOutputPoint.setNextValueDate(expectedNextValue);
				
				
				series.getInputValues().add(lastOutputPoint);
				
				logger.info("Series: "+series.getName()
						+", first point: "+DateTool.dateToDayString(series.getInputValues().getFirst().getDate())
						+", last point: "+DateTool.dateToDayString(series.getInputValues().getLast().getDate())
						+", Size:"+series.getInputValues().size());
				
				
			}
			
		}
		
		
		
		
		
	}

	
	public ValuePointList calculateMaxProfitOutputList(Stock stock) {
		ValuePointList list=new ValuePointList();
		LinkedList<HistoricalPoint> pList=stock.getHistoricalData().getNoneEmptyPoints();
		if(pList.size()==0){
			logger.info("Error: No historical data found for stock: "+stock.getFullName()+
					"\n Cannot calculate the Max Profit!");
			return null;
		}
		
		HistoricalPoint lastPoint=null;
		for(HistoricalPoint point:pList){
			if(lastPoint!=null){
				float diff=point.get(DatePoint.FIELD_Close)-lastPoint.get(DatePoint.FIELD_Close);
				if(diff>0){
					list.add(new ValuePoint(lastPoint.getDate(), 1.0));
				}
				else{
					list.add(new ValuePoint(lastPoint.getDate(), -1.0));
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
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 5184761223315339159L;
		//private boolean isFixedCalculated=false;
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
						vpoint=new ValuePoint(lastPoint.getDate(), -1.0);
					}
					vpoint.setMetaData(	String.valueOf(diff)+";"+
										String.valueOf(startValue)+";"+
										String.valueOf(endValue));
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
			if(this.isEmpty())return bl;
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
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2340474530665998257L;


		public ValuePointList toValuePointList(){
			ValuePointList list=new ValuePointList();
			
			for(Block b:this){
				for(BlockPoint bp:b){
					list.add(bp.val_point);
					//logger.info("Val Point: "+bp.val_point.toString());
				}
			}
			return list;
		}
		
		
		void isolateNoFixedBlocks(){
			
			//boolean isolatedEnd=false;
			if(this.isEmpty())return;
			
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
					matrix[i][j]=-1;
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
					if(last==null || last.getFirst().val_point.getValue()==-1)
						profit-=current.penalty*current.getStartValue();
					if(next==null ||  next.getFirst().val_point.getValue()==-1)
						profit-=current.penalty*current.getEndValue();
				}
			}
			
			return profit;
		}
		
	}
	
	
	private ValuePointList calculateMaxProfitOutputList(Stock stock,
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
		BasicConfigurator.configure();
		
		ExchangeRateProviderLocalImpl exChangeProvider=new ExchangeRateProviderLocalImpl();
		exChangeProvider.init("C:\\Users\\paul-edouard\\Desktop\\Programierung\\03_Boerse\\99_TEST_DATA");
		
		Stock stock=(Stock)exChangeProvider.load("DAI.DE");
		logger.info("stock loaded: "+stock.getFullName());
		
		IHistoricalDataProvider his_data_provider=new HistoricalDataProviderLocalImpl();
		his_data_provider.load(stock);
		LinkedList<HistoricalPoint> pList=stock.getHistoricalData().getNoneEmptyPoints();
		ValuePointList hisPoints=new ValuePointList();
		for(HistoricalPoint point:pList){
			hisPoints.add(new ValuePoint(point.getDate(), point.get(DatePoint.FIELD_Close)));
		}
		
		NeuralNetworkLocalImpl nn_provider=new NeuralNetworkLocalImpl();
		ValuePointList maxProfitSignal=nn_provider.calculateMaxProfitOutputList(stock);
		logger.info("List: "+maxProfitSignal.size());
		
		
		double[] penalties={0,0.0025,0.005,0.011};
		for(int i=0;i<penalties.length;i++){
			double penalty=penalties[i];
			
			double profit=ProfitUtils.calculate(
					maxProfitSignal.toDoubleArray(),
					hisPoints.toDoubleArray(),
					0.5,
					penalty);
			
			ValuePointList maxPenaltyProfitSignal=nn_provider.calculateMaxProfitOutputList(stock, penalty);
			
			double maxPenalty=ProfitUtils.calculate(
					maxPenaltyProfitSignal.toDoubleArray(),
					hisPoints.toDoubleArray(),
					0.5,
					penalty);
			
			
			logger.info("Signal         Max Profit: "+profit+", Penalty: "+penalty);
			logger.info("Signal Max Penalty Profit: "+maxPenalty+", Penalty: "+penalty);
		}
		
		
		
		
		
		
		
		
		
		
		/*
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
		*/
		
	}
	
	
}
