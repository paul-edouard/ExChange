package com.munch.exchange.parts.neural;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.NEATPopulation;

import com.munch.exchange.model.core.encog.NoveltySearchGenome;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration;
import com.munch.exchange.model.core.ib.neural.NeuralNetwork;
import com.munch.exchange.model.core.ib.neural.NeuralNetworkRating;

public class NeuralConfiguationArchitectureContentProvider implements
		IStructuredContentProvider, ITreeContentProvider {
	
	
	
	public NeuralConfiguationArchitectureContentProvider() {
		super();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof NeuralConfiguration){
			NeuralConfiguration neuralConfiguration=(NeuralConfiguration) parentElement;
			return neuralConfiguration.getNeuralArchitectures().toArray();
		}
		else if(parentElement instanceof NeuralArchitecture){
			NeuralArchitecture neuralArchitecture=(NeuralArchitecture) parentElement;
			return neuralArchitecture.getNeuralNetworks().toArray();
		}
		else if(parentElement instanceof NeuralNetwork){
			NeuralNetwork neuralNetwork=(NeuralNetwork) parentElement;
			
			if(neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.NoveltySearchNeat
					|| neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.Neat 
					|| neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.HyperNeat){
				
//				System.out.println("Novelty search Netwotk found!");
				
				List<Object> objects=new LinkedList<>();
				if(neuralNetwork.getNEATPopulation()!=null){
					Object[] networkAndPop=new Object[2];
					networkAndPop[0]=neuralNetwork;
					networkAndPop[1]=neuralNetwork.getNEATPopulation();
					objects.add(networkAndPop);
				}
				if(neuralNetwork.getParetoPopulation()!=null){
					Object[] networkAndPop=new Object[2];
					networkAndPop[0]=neuralNetwork;
					networkAndPop[1]=neuralNetwork.getParetoPopulation();
					objects.add(networkAndPop);
				}
				
//				System.out.println("Nb of objects: "+objects.size());
				
				return objects.toArray();
			}
			else{
				if(neuralNetwork.getTrainingRating().getChildren().isEmpty())
					return null;
				if(neuralNetwork.getBackTestingRating().getChildren().isEmpty())
					return null;
				
				Object[] objects=new Object[2];
				objects[0]=neuralNetwork.getTrainingRating();
				objects[1]=neuralNetwork.getBackTestingRating();
				return objects;
			}
		}
		else if(parentElement instanceof Object[]){
			Object[] parentObjects =(Object[]) parentElement;
			if(parentObjects.length==2 && 
					parentObjects[0] instanceof NeuralNetwork &&
					parentObjects[1] instanceof NEATPopulation){
				NeuralNetwork neuralNetwork=(NeuralNetwork) parentObjects[0];
				NEATPopulation pop=(NEATPopulation)parentObjects[1];
				List<Genome> genomes=pop.flatten();
				List<Object[]> objects=new LinkedList<>();
				for(Genome genome:genomes){
					if(!neuralNetwork.getBackTestingRatingMap().containsKey(genome))
						continue;
					if(!neuralNetwork.getTrainingRatingMap().containsKey(genome))
						continue;
					
					Object[] ratings=new Object[5];
					ratings[0]=genome;
					ratings[1]=neuralNetwork.getTrainingRatingMap().get(genome);
					ratings[2]=neuralNetwork.getBackTestingRatingMap().get(genome);
					ratings[3]=neuralNetwork;
					ratings[4]=pop;
					
								
					objects.add(ratings);
				}
				
				if(objects.isEmpty())
					return null;
				
				return objects.toArray();
			}
			else if(parentObjects.length==5 && 
					parentObjects[0] instanceof Genome){
				Object[] ratings=new Object[2];
				ratings[0]=parentObjects[1];
				ratings[1]=parentObjects[2];
				
				return ratings;
			}
			
		}
		else if(parentElement instanceof NeuralNetworkRating){
			NeuralNetworkRating rating=(NeuralNetworkRating)parentElement;
			return rating.getChildren().toArray();
		}
		
		
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof NeuralConfiguration){
			NeuralConfiguration neuralConfiguration=(NeuralConfiguration) element;
			return !neuralConfiguration.getNeuralArchitectures().isEmpty();
		}
		else if(element instanceof NeuralArchitecture){
			NeuralArchitecture neuralArchitecture=(NeuralArchitecture) element;
			return !neuralArchitecture.getNeuralNetworks().isEmpty();
		}
		else if(element instanceof NeuralNetwork){
			NeuralNetwork neuralNetwork=(NeuralNetwork) element;
			
			if(neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.NoveltySearchNeat
					|| neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.Neat 
					|| neuralNetwork.getNeuralArchitecture().getType()==ArchitectureType.HyperNeat){
				
				if(neuralNetwork.getNEATPopulation()!=null)return true;
				
			}
			else{
				if(neuralNetwork.getTrainingRating().getChildren().isEmpty())
					return false;
				if(neuralNetwork.getBackTestingRating().getChildren().isEmpty())
					return false;
				return true;
			}
		}
		else if(element instanceof  Object[]){
			Object[] objects =(Object[]) element;
			if(objects.length==2 && 
					objects[0] instanceof NeuralNetwork &&
					objects[1] instanceof NEATPopulation){
				
				NeuralNetwork neuralNetwork=(NeuralNetwork) objects[0];
				NEATPopulation pop=(NEATPopulation)objects[1];
				System.out.println("Population: "+pop.getName());
				List<Genome> genomes=pop.flatten();
//				System.out.println("Population: "+pop.getName());
				for(Genome genome:genomes){
					if(neuralNetwork.getBackTestingRatingMap().containsKey(genome))
						return true;
					if(neuralNetwork.getTrainingRatingMap().containsKey(genome))
						return true;
				
				}
				
			}
			else if(objects.length==5 && objects[0] instanceof Genome){
				return true;
			}
			
			
		}
		else if(element instanceof NeuralNetworkRating){
			NeuralNetworkRating rating=(NeuralNetworkRating)element;
			return !rating.getChildren().isEmpty();
		}
		
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof NeuralConfiguration ||
				inputElement instanceof NeuralArchitecture ||
				inputElement instanceof NeuralNetwork ||
				inputElement instanceof NeuralNetworkRating ){
			return getChildren(inputElement);
		}
		return null;
	}

}
