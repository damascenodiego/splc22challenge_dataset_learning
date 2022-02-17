package com.automatalearning1.dir_1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.checkerframework.checker.nullness.qual.Nullable;

import br.usp.icmc.labes.mealyInference.utils.ExperimentAndLearner;
import br.usp.icmc.labes.mealyInference.utils.Utils;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandler;
import de.learnlib.algorithms.lstar.ce.ObservationTableCEXHandlers;
import de.learnlib.algorithms.lstar.closing.ClosingStrategies;
import de.learnlib.algorithms.lstar.closing.ClosingStrategy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealy;
import de.learnlib.algorithms.lstar.mealy.ExtensibleLStarMealyBuilder;
import de.learnlib.api.SUL;
import de.learnlib.api.oracle.EquivalenceOracle;
import de.learnlib.api.oracle.MembershipOracle;
import de.learnlib.api.statistic.StatisticSUL;
import de.learnlib.driver.util.MealySimulatorSUL;
import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.oracle.equivalence.WpMethodEQOracle;
import de.learnlib.oracle.membership.SULOracle;
import de.learnlib.util.ExperimentDebug.MealyExperiment;
import net.automatalib.automata.transducers.MealyMachine;
import net.automatalib.automata.transducers.impl.compact.CompactMealy;
import net.automatalib.commons.util.Pair;
import net.automatalib.serialization.InputModelDeserializer;
import net.automatalib.serialization.dot.DOTParsers;
import net.automatalib.visualization.VisualizationHelper.EdgeAttrs;
import net.automatalib.words.Word;

public class Automata_Learning {

	public static final String SUL = "sul";
	public static final String HELP = "help";

	public static final String[] closingStrategiesAvailable = { "CloseFirst", "CloseShortest" };
	private static final String RIVEST_SCHAPIRE_ALLSUFFIXES = "RivestSchapireAllSuffixes";
	public static final String[] ceHandlersAvailable = { "ClassicLStar", "MalerPnueli", "RivestSchapire",
			RIVEST_SCHAPIRE_ALLSUFFIXES, "Shahbaz", "Suffix1by1" };

	public static final Function<Map<String, String>, Pair<@Nullable String, @Nullable Word<String>>> MEALY_EDGE_WORD_STR_PARSER = attr -> {
		final String label = attr.get(EdgeAttrs.LABEL);
		if (label == null) {
			return Pair.of(null, null);
		}

		final String[] tokens = label.split("/");

		if (tokens.length != 2) {
			return Pair.of(null, null);
		}

		Word<String> token2 = Word.epsilon();
		token2 = token2.append(tokens[1]);
		return Pair.of(tokens[0], token2);
	};

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		CommandLineParser parser = new BasicParser();
		Options options = createOptions();
		HelpFormatter formatter = new HelpFormatter();

		int array_length = 5;

		int[] array_1 = new int[array_length];
		Arrays.fill(array_1, 0);

		try {

			CommandLine line = parser.parse(options, args);
			if (line.hasOption(HELP)) {
				formatter.printHelp("Options:", options);
				System.exit(0);
			}

			if (!line.hasOption(SUL)) {
				throw new IllegalArgumentException("must provide a SUL");
			}

			File sul_1 = new File(line.getOptionValue(SUL));

			ClosingStrategy<Object, Object> closing_strategy = ClosingStrategies.CLOSE_FIRST;
			ObservationTableCEXHandler<Object, Object> ce_handler = ObservationTableCEXHandlers.RIVEST_SCHAPIRE;

			CompactMealy<String, Word<String>> mealy = LoadMealy(sul_1);
			Utils.getInstance();
			SUL<String, Word<String>> sulSim = new MealySimulatorSUL<>(mealy, Utils.OMEGA_SYMBOL);

			// Counters for MQs
			StatisticSUL<String, Word<String>> mq_sym = new SymbolCounterSUL<>("MQ", sulSim);
			StatisticSUL<String, Word<String>> mq_rst = new ResetCounterSUL<>("MQ", mq_sym);

			SUL<String, Word<String>> mq_sul = mq_rst;

			MembershipOracle<String, Word<Word<String>>> mq_oracle = new SULOracle<String, Word<String>>(mq_sul);

			// Counters for EQs
			StatisticSUL<String, Word<String>> eq_sym = new SymbolCounterSUL<>("EQ", sulSim);
			StatisticSUL<String, Word<String>> eq_rst = new ResetCounterSUL<>("EQ", eq_sym);

			SUL<String, Word<String>> eq_sul = eq_rst;

			EquivalenceOracle<MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eq_oracle = null;
			MembershipOracle<String, Word<Word<String>>> oracleForEQoracle = new SULOracle<>(eq_sul);
			eq_oracle = new WpMethodEQOracle<>(oracleForEQoracle, 2);

			ExperimentAndLearner experiment_pair = learningLStarM(mealy, mq_oracle, eq_oracle, ce_handler,
					closing_strategy);

			MealyExperiment experiment = experiment_pair.getExperiment();
			experiment.setProfile(true);
			experiment.setLogOT(true);
			experiment.run();

			// statistics array
			array_1[0] += experiment.getRounds().getCount();
			array_1[1] += ExtractValue(mq_rst.getStatisticalData().getSummary());
			array_1[2] += ExtractValue(mq_sym.getStatisticalData().getSummary());
			array_1[3] += ExtractValue(eq_rst.getStatisticalData().getSummary());
			array_1[4] += ExtractValue(eq_sym.getStatisticalData().getSummary());

			System.out.println("Learning algorithm: L_star");
			System.out.println("Number of learning rounds: " + array_1[0]);
			System.out.println("Number of MQ [resets]: " + array_1[1]);
			System.out.println("Number of MQ [symbols]: " + array_1[2]);
			System.out.println("Number of EQ [resets]: " + array_1[3]);
			System.out.println("Number of EQ [symbols]: " + array_1[4]);

		} catch (Exception e) {
			formatter.printHelp("Options:", options);
			System.err.println("Unexpected Exception");
			e.printStackTrace();
		}

	}

	private static ExperimentAndLearner learningLStarM(CompactMealy<String, Word<String>> mealyss,
			MembershipOracle<String, Word<Word<String>>> mqOracle,
			EquivalenceOracle<? super MealyMachine<?, String, ?, Word<String>>, String, Word<Word<String>>> eqOracle,
			ObservationTableCEXHandler<Object, Object> handler, ClosingStrategy<Object, Object> strategy) {
		List<Word<String>> initPrefixes = new ArrayList<>();
		initPrefixes.add(Word.epsilon());
		List<Word<String>> initSuffixes = new ArrayList<>();
		Word<String> word = Word.epsilon();
		for (String symbol : mealyss.getInputAlphabet()) {
			initSuffixes.add(word.append(symbol));
		}

		// Construct standard L*M instance
		ExtensibleLStarMealyBuilder<String, Word<String>> builder = new ExtensibleLStarMealyBuilder<String, Word<String>>();
		builder.setAlphabet(mealyss.getInputAlphabet());
		builder.setOracle(mqOracle);
		builder.setInitialPrefixes(initPrefixes);
		builder.setInitialSuffixes(initSuffixes);
		builder.setCexHandler(handler);
		builder.setClosingStrategy(strategy);

		ExtensibleLStarMealy<String, Word<String>> learner = builder.create();

		// The experiment will execute the main loop of active learning
		MealyExperiment<String, Word<String>> experiment = new MealyExperiment<String, Word<String>>(learner, eqOracle,
				mealyss.getInputAlphabet());

		ExperimentAndLearner p = new ExperimentAndLearner(learner, experiment);
		return p;
	}

	private static int ExtractValue(String string_1) {
		// TODO Auto-generated method stub
		int value_1 = 0;
		int j = string_1.lastIndexOf(" ");
		String string_2 = "";
		if (j >= 0) {
			string_2 = string_1.substring(j + 1);
		}
		value_1 = Integer.parseInt(string_2);
		return value_1;
	}

	private static CompactMealy<String, Word<String>> LoadMealy(File fsm_file) {
		// TODO Auto-generated method stub
		InputModelDeserializer<String, CompactMealy<String, Word<String>>> parser_1 = DOTParsers
				.mealy(MEALY_EDGE_WORD_STR_PARSER);
		CompactMealy<String, Word<String>> mealy = null;
		String file_name = fsm_file.getName();
		if (file_name.endsWith("txt")) {
			try {
				mealy = Utils.getInstance().loadMealyMachine(fsm_file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mealy;
		} else if (file_name.endsWith("dot")) {
			try {
				mealy = parser_1.readModel(fsm_file).model;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mealy;
		}

		return null;
	}

	private static Options createOptions() {
		Options options = new Options();
		options.addOption(SUL, true, "System Under Learning");
		options.addOption(HELP, false, "Shows help");
		return options;
	}

}