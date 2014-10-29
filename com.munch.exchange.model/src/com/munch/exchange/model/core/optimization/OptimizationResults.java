package com.munch.exchange.model.core.optimization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.exceptions.NeurophException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.munch.exchange.model.xml.XmlParameterElement;


public class OptimizationResults extends XmlParameterElement implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6455797898639549819L;
	
	
	static final String FIELD_Type="Type";
	static final String FIELD_Results="Results";
	
	static private final String MOVING_AVERAGE_STR="Moving Average";
	static private final String MACD_STR="MACD";
	static private final String BILLINGER_BAND_STR="Bollinger Band";
	static private final String PARABOLIC_SAR_STR="Parabolic SAR";
	static private final String RELATIVE_STRENGTH_INDEX_STR="Relative Strength Index";
	static private final String NMAW_STR="NMAW";
	static private final String NONE_STR="None";
	static private final String NEURAL_NETWORK_OUTPUT_DAY_STR="Neural Network Ouput day";
	static private final String NEURAL_NETWORK_OUTPUT_HOUR_STR="Neural Network Ouput hour";
	static private final String NEURAL_NETWORK_OUTPUT_MINUTE_STR="Neural Network Ouput minute";
	static private final String NEURAL_NETWORK_OUTPUT_SECONDE_STR="Neural Network Ouput seconde";
	
	
	
	private LinkedList<ResultEntity> results=new LinkedList<ResultEntity>();
	private Type type=Type.NONE;
	private int maxResult=200;
	
	public OptimizationResults(){
		
	}
	
	public boolean addResult(ResultEntity result){
		results.addFirst(result);
		Collections.sort(results);
		//Collections.reverse(list);
		if(results.size()>maxResult)
			results.removeLast();
		
		return result==results.getFirst();
		
	}
	
	public ResultEntity getBestResult(){
		if(results.isEmpty())return null;
		return results.getFirst();
	}
	
	public double compareBestResultWith(ResultEntity reference){
		ResultEntity best=this.getBestResult();
		
		if(reference==null || best ==null)return Double.NaN;
		
		if(reference.getValue()==0)return Double.NaN;
		
		
		double percent=100.0*(reference.getValue()-best.getValue())/reference.getValue();
		
		return percent;
		
	}
	
	
	
	public void setMaxResult(int maxResult) {
	this.maxResult = maxResult;
	}
	

	public int getMaxResult() {
		return maxResult;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
	changes.firePropertyChange(FIELD_Type, this.type, this.type = type);}
	

	public LinkedList<ResultEntity> getResults() {
		return results;
	}



	public enum Type { MOVING_AVERAGE, MACD,
						BILLINGER_BAND,PARABOLIC_SAR ,
						RELATIVE_STRENGTH_INDEX, NMAW,
						NEURAL_NETWORK_OUTPUT_DAY,
						NEURAL_NETWORK_OUTPUT_HOUR,
						NEURAL_NETWORK_OUTPUT_MINUTE,
						NEURAL_NETWORK_OUTPUT_SECONDE,
						NONE};
	
	public static String OptimizationTypeToString(Type type){
		switch (type) {
		case MOVING_AVERAGE:
			return MOVING_AVERAGE_STR;
		case MACD:
			return MACD_STR;
		case BILLINGER_BAND:
			return BILLINGER_BAND_STR;
		case PARABOLIC_SAR:
			return PARABOLIC_SAR_STR;
		case RELATIVE_STRENGTH_INDEX:
			return RELATIVE_STRENGTH_INDEX_STR;
		case NMAW:
			return NMAW_STR;
		case NEURAL_NETWORK_OUTPUT_DAY:
			return NEURAL_NETWORK_OUTPUT_DAY_STR;
		case NEURAL_NETWORK_OUTPUT_HOUR:
			return NEURAL_NETWORK_OUTPUT_HOUR_STR;
		case NEURAL_NETWORK_OUTPUT_MINUTE:
			return NEURAL_NETWORK_OUTPUT_MINUTE_STR;
		case NEURAL_NETWORK_OUTPUT_SECONDE:
			return NEURAL_NETWORK_OUTPUT_SECONDE_STR;
			

		default:
			return NONE_STR;
		}
	}
	
	public static Type stringToOptimizationType(String type){
		if(type.equals(MOVING_AVERAGE_STR))
			return Type.MOVING_AVERAGE;
		else if(type.equals(MACD_STR)){
			return Type.MACD;
		}
		else if(type.equals(BILLINGER_BAND_STR)){
			return Type.BILLINGER_BAND;
		}
		else if(type.equals(PARABOLIC_SAR_STR)){
			return Type.PARABOLIC_SAR;
		}
		else if(type.equals(RELATIVE_STRENGTH_INDEX_STR)){
			return Type.RELATIVE_STRENGTH_INDEX;
		}
		else if(type.equals(NMAW_STR)){
			return Type.NMAW;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_DAY_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_DAY;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_HOUR_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_HOUR;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_MINUTE_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_MINUTE;
		}
		else if(type.equals(NEURAL_NETWORK_OUTPUT_SECONDE_STR)){
			return Type.NEURAL_NETWORK_OUTPUT_SECONDE;
		}
		
		
		return Type.NONE;
		
	}
	
	
	
	/**
     * Saves neural network into the specified file.
     *
     * @param filePath file path to save network into
     */
    public void save(String filePath) {
        ObjectOutputStream out = null;
        try {
            File file = new File(filePath);
            out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            out.writeObject(this);
            out.flush();
        } catch (IOException ioe) {
             throw new NeurophException("Could not write the optimization result to file!", ioe);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    
    
    /**
     * Loads and return s neural network instance from specified file
     * @param file neural network file
     * @return neural network instance
     */
    public static OptimizationResults createFromFile(File file) {
        ObjectInputStream oistream = null;

        try {
            if (!file.exists()) {
                throw new FileNotFoundException("Cannot find file: " + file);
            }

            oistream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            OptimizationResults nnet = (OptimizationResults) oistream.readObject();
            return nnet;

        } catch (IOException ioe) {
             throw new NeurophException("Could not read the optimization result file!", ioe);
            //ioe.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
             throw new NeurophException("Class not found while trying to read the optimization result from file!", cnfe);
           // cnfe.printStackTrace();
        } finally {
            if (oistream != null) {
                try {
                    oistream.close();
                } catch (IOException ioe) {
                }
            }
        }
    }    
    
    public static OptimizationResults createFromFile(String filePath) {
        File file = new File(filePath);
        return OptimizationResults.createFromFile(file);
    }
	
	
	

	@Override
	public String toString() {
		String ret="OptimizationResults [ type=" + type
				+ ", maxResult=" + maxResult + ", results=\n";
		
		for(ResultEntity ent:this.results){
			ret+=String.valueOf(ent)+"\n";
		}
		
		ret+="]";
		return ret;
	}

	@Override
	protected void initAttribute(Element rootElement) {
		results.clear();
		this.setType(stringToOptimizationType(rootElement.getAttribute(FIELD_Type)));
		
	}

	@Override
	protected void initChild(Element childElement) {
		
		ResultEntity ent=new ResultEntity();
		if(childElement.getTagName().equals(ent.getTagName())){
			ent.init(childElement);
			results.add(ent);
		}
		
	}

	@Override
	protected void setAttribute(Element rootElement) {
		rootElement.setAttribute(FIELD_Type,OptimizationTypeToString(this.getType()));
	}

	@Override
	protected void appendChild(Element rootElement, Document doc) {
		for(ResultEntity ent:results){
			rootElement.appendChild(ent.toDomElement(doc));
		}
	}

}
