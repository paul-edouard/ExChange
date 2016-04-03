package com.munch.exchange.model.core.ib.neural;

import com.ib.controller.Types.BarSize;
import com.munch.exchange.model.core.ib.IbContract;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.ReferenceData;
import com.munch.exchange.model.core.ib.neural.NeuralConfiguration.SplitStrategy;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2016-04-03T16:32:59.698+0200")
@StaticMetamodel(NeuralConfiguration.class)
public class NeuralConfiguration_ {
	public static volatile SingularAttribute<NeuralConfiguration, Integer> id;
	public static volatile SingularAttribute<NeuralConfiguration, IbContract> contract;
	public static volatile SingularAttribute<NeuralConfiguration, String> name;
	public static volatile SingularAttribute<NeuralConfiguration, Long> creationDate;
	public static volatile ListAttribute<NeuralConfiguration, NeuralInput> neuralInputs;
	public static volatile SingularAttribute<NeuralConfiguration, BarSize> size;
	public static volatile SingularAttribute<NeuralConfiguration, Integer> percentOfTrainingData;
	public static volatile SingularAttribute<NeuralConfiguration, ReferenceData> referenceData;
	public static volatile SingularAttribute<NeuralConfiguration, SplitStrategy> splitStrategy;
	public static volatile ListAttribute<NeuralConfiguration, NeuralTrainingElement> neuralTrainingElements;
	public static volatile ListAttribute<NeuralConfiguration, NeuralArchitecture> neuralArchitectures;
	public static volatile ListAttribute<NeuralConfiguration, IsolatedNeuralArchitecture> isolatedArchitectures;
}
