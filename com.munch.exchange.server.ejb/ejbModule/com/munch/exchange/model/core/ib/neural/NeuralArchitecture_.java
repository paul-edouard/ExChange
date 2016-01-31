package com.munch.exchange.model.core.ib.neural;

import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.Activation;
import com.munch.exchange.model.core.ib.neural.NeuralArchitecture.ArchitectureType;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-01-30T20:21:54.678+0100")
@StaticMetamodel(NeuralArchitecture.class)
public class NeuralArchitecture_ {
	public static volatile SingularAttribute<NeuralArchitecture, Integer> id;
	public static volatile SingularAttribute<NeuralArchitecture, String> name;
	public static volatile SingularAttribute<NeuralArchitecture, NeuralConfiguration> neuralConfiguration;
	public static volatile ListAttribute<NeuralArchitecture, NeuralNetwork> neuralNetworks;
	public static volatile SingularAttribute<NeuralArchitecture, ArchitectureType> type;
	public static volatile SingularAttribute<NeuralArchitecture, String> hiddenLayerDescription;
	public static volatile SingularAttribute<NeuralArchitecture, Long> volume;
	public static volatile SingularAttribute<NeuralArchitecture, Activation> activation;
}
