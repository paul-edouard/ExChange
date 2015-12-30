package com.munch.exchange.model.core.ib.chart.signals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.Instrumenter;
import org.moeaframework.analysis.collector.Accumulator;
import org.moeaframework.analysis.sensitivity.EpsilonHelper;
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.util.progress.ProgressEvent;
import org.moeaframework.util.progress.ProgressListener;

import com.munch.exchange.model.core.moea.InjectedSolutionsAlgorithmFactory;


public class IbChartSignalOptimizationController {
	
	/**
	 * The collection of listeners which are notified when the controller state
	 * changes.
	 */
	private final LinkedList<IbChartSignalOptimizationControllerListener> listeners;
	
	
	/**
	 * The collection of all results.
	 */
	private final Map<String, List<Accumulator>> accumulators;
	
	
	/**
	 * The last accumulator to be generated; or {@code null} if no last
	 * accumulator exists or has been previously cleared.
	 */
	private Accumulator lastAccumulator;
	

	/**
	 * {@code true} if the last run's trace should be drawn separately;
	 * {@code false} otherwise.
	 */
	private boolean showLastTrace = false;
	
	/**
	 * {@code true} if the hypervolume indicator collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeHypervolume = false;
	
	/**
	 * {@code true} if the generational distance indicator collector is
	 * included; {@code false} otherwise.
	 */
	private boolean includeGenerationalDistance = true;
	
	/**
	 * {@code true} if the inverted generational distance indicator collector is
	 * included; {@code false} otherwise.
	 */
	private boolean includeInvertedGenerationalDistance = true;
	
	/**
	 * {@code true} if the spacing indicator collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeSpacing = true;
	
	/**
	 * {@code true} if the additive &epsilon;-indicator collector is
	 * included; {@code false} otherwise.
	 */
	private boolean includeAdditiveEpsilonIndicator = true;
	
	/**
	 * {@code true} if the contribution indicator collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeContribution = true;
	
	/**
	 * {@code true} if the R1 indicator collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeR1 = false;
	
	/**
	 * {@code true} if the R2 indicator collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeR2 = true;
	
	/**
	 * {@code true} if the R3 indicator collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includeR3 = false;
	
	/**
	 * {@code true} if the &epsilon;-progress collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeEpsilonProgress = true;
	
	/**
	 * {@code true} if the adaptive multimethod variation collector is included;
	 * {@code false} otherwise.
	 */
	private boolean includeAdaptiveMultimethodVariation = true;
	
	/**
	 * {@code true} if the adaptive time continuation collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeAdaptiveTimeContinuation = true;
	
	/**
	 * {@code true} if the elapsed time collector is included; {@code false} 
	 * otherwise.
	 */
	private boolean includeElapsedTime = true;
	
	/**
	 * {@code true} if the approximation set collector is included; 
	 * {@code false} otherwise.
	 */
	private boolean includeApproximationSet = true;
	
	/**
	 * {@code true} if the population size collector is included; {@code false}
	 * otherwise.
	 */
	private boolean includePopulationSize = true;
	
	/**
	 * The run progress of the current job being evaluated.
	 */
	private volatile int runProgress;
	
	/**
	 * The overall progress of the current job being evaluated.
	 */
	private volatile int overallProgress;
	
	/**
	 * The thread running the current job; or {@code null} if no job is
	 * running.
	 */
	private volatile Thread thread;
	
	/**
	 * Toggles between showing individual trace lines when {@code true} and
	 * quantiles when {@code false}.
	 */
	private boolean showIndividualTraces;
	
	/**
	 * The executor for the current run.
	 */
	private Executor executor;
	
	
	/**
	 * The Signal to optimize
	 */
	private IbChartSignal signal;


	public IbChartSignalOptimizationController() {
		super();
		//this.signal = signal;
		
		listeners=new LinkedList<IbChartSignalOptimizationControllerListener>();
		accumulators = new HashMap<String, List<Accumulator>>();
	}
	
	
	/**
	 * Adds the specified listener to receive all subsequent controller events.
	 * 
	 * @param listener the listener to receive controller events
	 */
	public void addControllerListener(IbChartSignalOptimizationControllerListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes the specified listener so it no longer receives controller
	 * events.
	 * 
	 * @param listener the listener to no longer receive controller events
	 */
	public void removeControllerListener(IbChartSignalOptimizationControllerListener listener) {
		listeners.remove(listener);
	}
	

	/**
	 * Fires a {@code MODEL_CHANGED} controller event.
	 */
	protected void fireModelChangedEvent() {
		fireEvent(new IbChartSignalOptimizationControllerEvent(this, 
				IbChartSignalOptimizationControllerEvent.Type.MODEL_CHANGED));
	}
	
	/**
	 * Fires a {@code STATE_CHANGED} controller event.
	 */
	protected void fireStateChangedEvent() {
		fireEvent(new IbChartSignalOptimizationControllerEvent(this, 
				IbChartSignalOptimizationControllerEvent.Type.STATE_CHANGED));
	}
	
	/**
	 * Fires a {@code PROGRESS_CHANGED} controller event.
	 */
	protected void fireProgressChangedEvent() {
		fireEvent(new IbChartSignalOptimizationControllerEvent(this,
				IbChartSignalOptimizationControllerEvent.Type.PROGRESS_CHANGED));
	}
	

	/**
	 * Fires a {@code VIEW_CHANGED} controller event.
	 */
	public void fireViewChangedEvent() {
		fireEvent(new IbChartSignalOptimizationControllerEvent(this,
				IbChartSignalOptimizationControllerEvent.Type.VIEW_CHANGED));
	}
	
	/**
	 * Fires the specified controller event.  All listeners will receive this
	 * event on the event dispatch thread.
	 * 
	 * @param event the controller event to fire
	 */
	protected synchronized void fireEvent(final IbChartSignalOptimizationControllerEvent event) {
		/*
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
			*/
				for(IbChartSignalOptimizationControllerListener listener:listeners ){
					listener.controllerStateChanged(event);
				}
				/*
			}
				
		});
		*/
	}
	
	
	/**
	 * Adds a new result to this controller.  If the specified key already
	 * exists, the accumulator is appended to the existing results.  A
	 * {@code MODEL_CHANGED} event is fired.
	 * 
	 * @param key the result key identifying the algorithm and problem
	 *        associated with these results
	 * @param accumulator the accumulator storing the results
	 */
	public void add(String key, Accumulator accumulator) {
		synchronized (accumulators) {
			if (!accumulators.containsKey(key)) {
				accumulators.put(key, new CopyOnWriteArrayList<Accumulator>());
			}
			
			accumulators.get(key).add(accumulator);
			lastAccumulator = accumulator;
		}
		
		fireModelChangedEvent();
	}
	
	
	
	/**
	 * Clears all results from this collector.  A {@code MODEL_CHANGED} event
	 * is fired.
	 */
	public void clear() {
		if (accumulators.isEmpty()) {
			return;
		}
		
		synchronized (accumulators) {
			accumulators.clear();
			lastAccumulator = null;
		}
		
		fireModelChangedEvent();
	}
	
	/**
	 * Returns an unmodifiable collection containing the results associated
	 * with the specified key.
	 * 
	 * @param key the result key
	 * @return an unmodifiable collection containing the results associated
	 *         with the specified key
	 */
	public List<Accumulator> get(String key) {
		synchronized (accumulators) {
			return Collections.unmodifiableList(accumulators.get(key));
		}
	}
	
	/**
	 * Returns an unmodifiable set of result keys contained in this controller.
	 * 
	 * @return an unmodifiable set of result keys contained in this controller
	 */
	public Set<String> getKeys() {
		synchronized (accumulators) {
			return Collections.unmodifiableSet(accumulators.keySet());
		}
	}
	
	/**
	 * Returns the last accumulator to be generated; or {@code null} if no last
	 * accumulator exists or has been previously cleared.
	 * 
	 * @return the last accumulator to be generated; or {@code null} if no last
	 *         accumulator exists or has been previously cleared
	 */
	public Accumulator getLastAccumulator() {
		synchronized (accumulators) {
			return lastAccumulator;
		}
	}
	
	/**
	 * Clears the last accumulator.  Subsequent invocations of
	 * {@link #getLastAccumulator()} will return {@code null} until a new
	 * accumulator is generated.
	 */
	public void clearLastAccumulator() {
		synchronized (accumulators) {
			lastAccumulator = null;
		}
	}
	
	/**
	 * Updates the progress of this controller.  A {@code PROGRESS_CHANGED}
	 * event is fired.
	 * 
	 * @param currentEvaluation the current evaluation number
	 * @param currentSeed the current seed number
	 * @param totalEvaluations the total number of evaluations
	 * @param totalSeeds the total number of seeds
	 */
	protected void updateProgress(int currentEvaluation, int currentSeed,
			int totalEvaluations, int totalSeeds) {
		
		runProgress = currentEvaluation;
		overallProgress = currentSeed;
		
		fireProgressChangedEvent();
	}
	
	/**
	 * Creates and displays a dialog containing a statistical comparison of
	 * the selected results.
	 */
	public String showStatistics(List<String> selectedResults) {
		
		IbChartSignalProblem problem= null;
		
		try {
			problem = new IbChartSignalProblem(signal);
			
			double epsilon = EpsilonHelper.getEpsilon(problem);
			
			Analyzer analyzer = new Analyzer()
					//.withProblem(problemName)
					.withProblemClass(IbChartSignalProblem.class, signal)
					.withEpsilon(epsilon)
					.showAggregate()
					.showStatisticalSignificance();
			
			if (getIncludeHypervolume()) {
				analyzer.includeHypervolume();
			}
			
			if (getIncludeGenerationalDistance()) {
				analyzer.includeGenerationalDistance();
			}
			
			if (getIncludeInvertedGenerationalDistance()) {
				analyzer.includeInvertedGenerationalDistance();
			}
			
			if (getIncludeSpacing()) {
				analyzer.includeSpacing();
			}
			
			if (getIncludeAdditiveEpsilonIndicator()) {
				analyzer.includeAdditiveEpsilonIndicator();
			}
			
			if (getIncludeContribution()) {
				analyzer.includeContribution();
			}
			
			if (getIncludeR1()) {
				analyzer.includeR1();
			}
			
			if (getIncludeR2()) {
				analyzer.includeR2();
			}
			
			if (getIncludeR3()) {
				analyzer.includeR3();
			}
			
			for (String key : selectedResults) {
			for (Accumulator accumulator : get(key)) {
					if (!accumulator.keySet().contains("Approximation Set")) {
						continue;
					}
					
					NondominatedPopulation population = 
							new EpsilonBoxDominanceArchive(epsilon);
					List<?> list = (List<?>)accumulator.get("Approximation Set",
							accumulator.size("Approximation Set")-1);
					
					for (Object object : list) {
						population.add((Solution)object);
					}
					
					analyzer.add(key, population);
			}
			}
			
			
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			analyzer.printAnalysis(new PrintStream(stream));
			
			return stream.toString();
			
			
		} finally {
			if (problem != null) {
				problem.close();
			}
		}
		
		
	}
	
	/**
	 * Launches a thread to run the current evaluation job.
	 */
	public void run() {
		if (thread != null) {
			System.err.println("job already running");
			return;
		}
		
		//final String problemName = frame.getProblem();
		final String algorithmName = signal.getAlgorithmName();
		final String barSize=signal.getBarSize();
		final int numberOfEvaluations = signal.getNumberOfEvaluations();
		final int numberOfSeeds = signal.getNumberOfSeeds();
		
		thread = new Thread() {
			
			public void run() {
				try {
					updateProgress(0,0, numberOfEvaluations,numberOfSeeds);

					// setup the instrumenter to collect the necessary info
					Instrumenter instrumenter = new Instrumenter()
							.withFrequency(50)
							.withProblemClass(IbChartSignalProblem.class, signal);
							
							//.withProblem(problemName);
					
					// lookup predefined epsilons for this problem
					Problem problem = null;
					Population startPopulation=null;
					int startPopSize=0;
					try {
						problem = new IbChartSignalProblem(signal);
						
						double epsilon=EpsilonHelper.getEpsilon(
								problem);
						
						instrumenter.withEpsilon(EpsilonHelper.getEpsilon(
								problem));
						
						startPopulation=((IbChartSignalProblem)problem).createStartPopulation(epsilon);
						startPopSize=startPopulation.size();
						
						instrumenter.withReferenceSet(startPopulation);
						
						
						
					} finally {
						if (problem != null) {
							problem.close();
						}
					}
					
					
					
					if (getIncludeHypervolume() && startPopSize>=2) {
						instrumenter.attachHypervolumeCollector();
					}
					
					if (getIncludeGenerationalDistance()) {
						instrumenter.attachGenerationalDistanceCollector();
					}
					
					if (getIncludeInvertedGenerationalDistance()) {
						instrumenter.attachInvertedGenerationalDistanceCollector();
					}
					
					if (getIncludeSpacing()) {
						instrumenter.attachSpacingCollector();
					}
					
					if (getIncludeAdditiveEpsilonIndicator()) {
						instrumenter.attachAdditiveEpsilonIndicatorCollector();
					}
					
					if (getIncludeContribution()) {
						instrumenter.attachContributionCollector();
					}
					
					if (getIncludeR1()) {
						instrumenter.attachR1Collector();
					}
					
					if (getIncludeR2()) {
						instrumenter.attachR2Collector();
					}
					
					if (getIncludeR3()) {
						instrumenter.attachR3Collector();
					}
					
					if (getIncludeEpsilonProgress()) {
						instrumenter.attachEpsilonProgressCollector();
					}
					
					if (getIncludeAdaptiveMultimethodVariation()) {
						instrumenter.attachAdaptiveMultimethodVariationCollector();
					}
					
					if (getIncludeAdaptiveTimeContinuation()) {
						instrumenter.attachAdaptiveTimeContinuationCollector();
					}
					
					if (getIncludeElapsedTime()) {
						instrumenter.attachElapsedTimeCollector();
					}
					
					if (getIncludeApproximationSet()) {
						instrumenter.attachApproximationSetCollector();
					}
					
					if (getIncludePopulationSize()) {
						instrumenter.attachPopulationSizeCollector();
					}
					
					
					
					// setup the progress listener to receive updates
					ProgressListener listener = new ProgressListener() {
						
						@Override
						public void progressUpdate(ProgressEvent event) {
							
							//System.out.println("event.getCurrentNFE: "+event.getCurrentNFE());
							//System.out.println("event.getMaxNFE(): "+event.getMaxNFE());
							//System.out.println("Is Seed finied: "+event.isSeedFinished());
							
							
							updateProgress(
									event.getCurrentNFE(),
									event.getCurrentSeed(),
									event.getMaxNFE()
									,event.getTotalSeeds()
									);
							
							if (event.isSeedFinished()) {
								Executor executor = event.getExecutor();
								Instrumenter instrumenter =
										executor.getInstrumenter();
								
								add(barSize+"#"+algorithmName,
										instrumenter.getLastAccumulator());
							}
						}
						
					};
					
					
					instrumenter.withFrequency(50);
					
					// setup the executor to run for the desired time
					executor = new Executor()
							.withSameProblemAs(instrumenter)
							.withInstrumenter(instrumenter)
							.withAlgorithm(algorithmName)
							.withMaxEvaluations(numberOfEvaluations)
							.distributeOnAllCores()
							.withProgressListener(listener);
					
					//Add the already save parameters in the start population
					if(startPopulation!=null && !startPopulation.isEmpty()){
						System.out.println("Add the saved results");
						AlgorithmFactory factory=new InjectedSolutionsAlgorithmFactory(startPopulation);
						executor.usingAlgorithmFactory(factory);
					}
					
					
					// run the executor using the listener to collect results
					executor.runSeeds(numberOfSeeds);
					
				} catch (Exception e) {
					handleException(e);
				} finally {
					thread = null;
					fireStateChangedEvent();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		fireStateChangedEvent();
	}

	/**
	 * Notifies the controller that it should cancel the current evaluation job.
	 */
	public void cancel() {
		if (executor != null) {
			executor.cancel();
		}
	}
	
	/**
	 * Returns {@code true} if this controller is currently processing an
	 * evaluation job; {@code false} otherwise.
	 * 
	 * @return {@code true} if this controller is currently processing an
	 * evaluation job; {@code false} otherwise
	 */
	public boolean isRunning() {
		return thread != null;
	}

	/**
	 * Returns {@code true} if the last run's trace is displayed; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if the last run's trace is displayed; {@code false}
	 *         otherwise
	 */
	public boolean getShowLastTrace() {
		return showLastTrace;
	}

	/**
	 * Sets the display of the last run's trace.
	 * 
	 * @param showLastTrace {@code true} if the last run's trace is displayed; 
	 *        {@code false} otherwise
	 */
	public void setShowLastTrace(boolean showLastTrace) {
		this.showLastTrace = showLastTrace;
		
		fireViewChangedEvent();
	}

	/**
	 * Returns {@code true} if the hypervolume indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the hypervolume indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeHypervolume() {
		return includeHypervolume;
	}

	/**
	 * Sets the inclusion of the hypervolume indicator collector.
	 * 
	 * @param includeHypervolume {@code true} if the hypervolume collector is
	 *        included; {@code false} otherwise
	 */
	public void setIncludeHypervolume(boolean includeHypervolume) {
		this.includeHypervolume = includeHypervolume;
	}

	/**
	 * Returns {@code true} if the generational distance indicator collector
	 * is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the generational distance indicator collector 
	 *         is included; {@code false} otherwise
	 */
	public boolean getIncludeGenerationalDistance() {
		return includeGenerationalDistance;
	}

	/**
	 * Sets the inclusion of the generational distance indicator collector.
	 * 
	 * @param includeGenerationalDistance {@code true} if the generational
	 *        distance indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeGenerationalDistance(
			boolean includeGenerationalDistance) {
		this.includeGenerationalDistance = includeGenerationalDistance;
	}

	/**
	 * Returns {@code true} if the inverted generational distance indicator 
	 * collector is included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the inverted generational distance indicator
	 *         collector is included; {@code false} otherwise
	 */
	public boolean getIncludeInvertedGenerationalDistance() {
		return includeInvertedGenerationalDistance;
	}

	/**
	 * Sets the inclusion of the inverted generational distance indicator 
	 * collector.
	 * 
	 * @param includeInvertedGenerationalDistance {@code true} if the inverted
	 *        generational distance indicator collector is included; 
	 *        {@code false} otherwise
	 */
	public void setIncludeInvertedGenerationalDistance(
			boolean includeInvertedGenerationalDistance) {
		this.includeInvertedGenerationalDistance = 
				includeInvertedGenerationalDistance;
	}

	/**
	 * Returns {@code true} if the spacing indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the spacing indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeSpacing() {
		return includeSpacing;
	}

	/**
	 * Sets the inclusion of the spacing indicator collector.
	 * 
	 * @param includeSpacing {@code true} if the spacing indicator collector is
	 *        included; {@code false} otherwise
	 */
	public void setIncludeSpacing(boolean includeSpacing) {
		this.includeSpacing = includeSpacing;
	}

	/**
	 * Returns {@code true} if the additive &epsilon;-indicator collector is 
	 * included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the additive &epsilon;-indicator collector is 
	 *         included; {@code false} otherwise
	 */
	public boolean getIncludeAdditiveEpsilonIndicator() {
		return includeAdditiveEpsilonIndicator;
	}

	/**
	 * Sets the inclusion of the additive &epsilon;-indicator collector.
	 * 
	 * @param includeAdditiveEpsilonIndicator {@code true} if the additive 
	 *        &epsilon;-indicator collector is included; {@code false} otherwise
	 */
	public void setIncludeAdditiveEpsilonIndicator(
			boolean includeAdditiveEpsilonIndicator) {
		this.includeAdditiveEpsilonIndicator = includeAdditiveEpsilonIndicator;
	}

	/**
	 * Returns {@code true} if the contribution indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the contribution indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeContribution() {
		return includeContribution;
	}

	/**
	 * Sets the inclusion of the contribution indicator collector.
	 * 
	 * @param includeContribution {@code true} if the contribution indicator
	 *        collector is included; {@code false} otherwise
	 */
	public void setIncludeContribution(boolean includeContribution) {
		this.includeContribution = includeContribution;
	}
	
	/**
	 * Returns {@code true} if the R1 indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the R1 indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeR1() {
		return includeR1;
	}
	
	/**
	 * Sets the inclusion of the R1 indicator collector.
	 * 
	 * @param includeR1 {@code true} if the R1 indicator collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeR1(boolean includeR1) {
		this.includeR1 = includeR1;
	}
	
	/**
	 * Returns {@code true} if the R2 indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the R2 indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeR2() {
		return includeR2;
	}
	
	/**
	 * Sets the inclusion of the R2 indicator collector.
	 * 
	 * @param includeR2 {@code true} if the R2 indicator collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeR2(boolean includeR2) {
		this.includeR2 = includeR2;
	}
	
	/**
	 * Returns {@code true} if the R3 indicator collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the R3 indicator collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeR3() {
		return includeR3;
	}
	
	/**
	 * Sets the inclusion of the R3 indicator collector.
	 * 
	 * @param includeR3 {@code true} if the R3 indicator collector is included;
	 *        {@code false} otherwise
	 */
	public void setIncludeR3(boolean includeR3) {
		this.includeR3 = includeR3;
	}

	/**
	 * Returns {@code true} if the &epsilon;-progress collector is included;
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the &epsilon;-progress collector is included;
	 *         {@code false} otherwise
	 */
	public boolean getIncludeEpsilonProgress() {
		return includeEpsilonProgress;
	}

	/**
	 * Sets the inclusion of the &epsilon;-progress collector.
	 * 
	 * @param includeEpsilonProgress {@code true} if the &epsilon;-progress
	 *        collector is included; {@code false} otherwise
	 */
	public void setIncludeEpsilonProgress(boolean includeEpsilonProgress) {
		this.includeEpsilonProgress = includeEpsilonProgress;
	}

	/**
	 * Returns {@code true} if the adaptive multimethod variation collector is 
	 * included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the adaptive multimethod variation collector is 
	 *         included; {@code false} otherwise
	 */
	public boolean getIncludeAdaptiveMultimethodVariation() {
		return includeAdaptiveMultimethodVariation;
	}

	/**
	 * Sets the inclusion of the adaptive multimethod variation collector.
	 * 
	 * @param includeAdaptiveMultimethodVariation {@code true} if the adaptive
	 *        multimethod variation collector is included; {@code false} 
	 *        otherwise
	 */
	public void setIncludeAdaptiveMultimethodVariation(
			boolean includeAdaptiveMultimethodVariation) {
		this.includeAdaptiveMultimethodVariation = 
				includeAdaptiveMultimethodVariation;
	}

	/**
	 * Returns {@code true} if the adaptive time continuation collector is 
	 * included; {@code false} otherwise.
	 * 
	 * @return {@code true} if the adaptive time continuation collector is 
	 *         included; {@code false} otherwise
	 */
	public boolean getIncludeAdaptiveTimeContinuation() {
		return includeAdaptiveTimeContinuation;
	}

	/**
	 * Sets the inclusion of the adaptive time continuation collector.
	 * 
	 * @param includeAdaptiveTimeContinuation {@code true} if the adaptive time
	 *        continuation collector is included; {@code false} otherwise
	 */
	public void setIncludeAdaptiveTimeContinuation(
			boolean includeAdaptiveTimeContinuation) {
		this.includeAdaptiveTimeContinuation = includeAdaptiveTimeContinuation;
	}

	/**
	 * Returns {@code true} if the elapsed time collector is included; 
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the elapsed time collector is included; 
	 *         {@code false} otherwise
	 */
	public boolean getIncludeElapsedTime() {
		return includeElapsedTime;
	}

	/**
	 * Sets the inclusion of the elapsed time collector.
	 * 
	 * @param includeElapsedTime {@code true} if the elapsed time collector is 
	 *        included; {@code false} otherwise
	 */
	public void setIncludeElapsedTime(boolean includeElapsedTime) {
		this.includeElapsedTime = includeElapsedTime;
	}

	/**
	 * Returns {@code true} if the approximation set collector is included; 
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the approximation set collector is included; 
	 *         {@code false} otherwise
	 */
	public boolean getIncludeApproximationSet() {
		return includeApproximationSet;
	}

	/**
	 * Sets the inclusion of the approximation set collector.
	 * 
	 * @param includeApproximationSet {@code true} if the approximation set 
	 *        collector is included; {@code false} otherwise
	 */
	public void setIncludeApproximationSet(boolean includeApproximationSet) {
		this.includeApproximationSet = includeApproximationSet;
	}

	/**
	 * Returns {@code true} if the population size collector is included; 
	 * {@code false} otherwise.
	 * 
	 * @return {@code true} if the population size collector is included; 
	 *         {@code false} otherwise
	 */
	public boolean getIncludePopulationSize() {
		return includePopulationSize;
	}

	/**
	 * Sets the inclusion of the population size collector.
	 * 
	 * @param includePopulationSize {@code true} if the population size 
	 *        collector is included; {@code false} otherwise
	 */
	public void setIncludePopulationSize(boolean includePopulationSize) {
		this.includePopulationSize = includePopulationSize;
	}
	
	/**
	 * Returns the run progress of the current job being evaluated.  The run
	 * progress measures the number of evaluations completed thus far.
	 * 
	 * @return the run progress of the current job being evaluated
	 */
	public int getRunProgress() {
		return runProgress;
	}
	
	/**
	 * Returns the overall progress of the current job being evaluated.  The
	 * overall progress measures the number of seeds completed thus far.
	 * 
	 * @return the overall progress of the current job being evaluated
	 */
	public int getOverallProgress() {
		return overallProgress;
	}
	
	public void setSignal(IbChartSignal signal) {
		this.signal = signal;
	}


	/**
	 * Returns {@code true} if individual traces are shown; {@code false} if
	 * quantiles are shown.
	 * 
	 * @return {@code true} if individual traces are shown; {@code false} if
	 *         quantiles are shown
	 */
	public boolean getShowIndividualTraces() {
		return showIndividualTraces;
	}

	/**
	 * Set to {@code true} to show individual traces; {@code false} to show
	 * quantiles.
	 * 
	 * @param showIndividualTraces {@code true} to show individual traces;
	 *        {@code false} to show quantiles
	 */
	public void setShowIndividualTraces(boolean showIndividualTraces) {
		if (this.showIndividualTraces != showIndividualTraces) {
			this.showIndividualTraces = showIndividualTraces;
			
			fireViewChangedEvent();
		}
	}
	

	/**
	 * Handles an exception, possibly displaying a dialog box containing details
	 * of the exception.
	 * 
	 * @param e the exception
	 */
	protected void handleException(Exception e) {
		e.printStackTrace();
		
		String message = e.getMessage() == null ? e.toString() : e.getMessage();
		
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			message += " - " + e.getCause().getMessage();
		}
		
		System.err.println(message);
		
		/*
		JOptionPane.showMessageDialog(
				frame, 
				message, 
				"Error", 
				JOptionPane.ERROR_MESSAGE);
				*/
	}
	

}
