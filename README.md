# Challenge Case (SPLC 2022)

## Title
A Benchmark for Active Learning of Variability-Intensive Systems

## Abstract
Behavioral models are the key enablers for behavioral analysis of Software Product Lines (SPL), including testing and model checking. Active model learning comes to the rescue when family behavioral models are non-existent or outdated. A key challenge on active model learning is to detect commonalities and variability efficiently and combine them into concise family models. Benchmarks and their associated metrics will play a key role in shaping the research agenda in this promising field and provide an effective means for comparing and identifying relative strengths and weaknesses in the forthcoming techniques. In this challenge, we seek benchmarks to evaluate the efficiency (e.g., learning time and memory footprint) and effectiveness (e.g., conciseness and accuracy of family models) of active model learning methods in the software product line context. These benchmark sets must contain the structural and behavioral variability models of at least one SPL. Each SPL in a benchmark must contain products that requires more than one round of model learning with respect to the basic active learning L* algorithm. Alternatively, tools supporting the synthesis of artificial benchmark models are also welcome.

## Repository structure

In the [splc_paper directory](splc_paper/), there is a class named Automata_Learning. You can run the [AutomataLearning.java](splc_paper/src/main/java/ir/ac/ut/fml/AutomataLearning.java) using an "sul" argument which specifies the address of a dot file expressing a Finite State Machine (FSM). This FSM model will be learned using the standard L* algorithm. Then the amounts of the following learning metrics will be printed:

1) The number of learning rounds
2) The number of MQ resets
3) The number of MQ symbols
4) The number of EQ resets
5) The number of EQ symbols

In this class, the learning parameters are set to the following default values:
- The equivalence oracle type: WpMethodEQOracle(2)
- The counterexample handling method: CLOSE_FIRST
- Caching is not used.

This simple example shows how the L* algorithm can be used to learn an FSM and how the values of learning metrics can be obtained.
Examples of SPLs with families of FSMs are found in the [sample_spl](sample_spl) directory.

## Generating Random FFSMs or Families of FSMs

To date there are no systematic procedures for generating synthetic datasets of families of FSMs with behavioral commonalities/variability for benchmarking active model learning techniques. Thus, the solution for this challenge can be alternatively provided as a tool for randomly generating behavioral variability models, such as Featured FSMs (_FFSMs_) or _families of FSMs_.

As an approach to generate random FFSM/FSMs models, we expect the participants to report a systematic process and implementation to synthesize:

- Random Featured Finite State Machines or 
- Random Families of Mealy machines.

In both cases, users shall be able to define a custom:

1. Variability source (i.e., feature model)
2. Randomness source (i.e., seed)
3. Number of states of the generated FSMs/FFSM
4. Input/output alphabets (i.e., set of symbols)

Additionally, we expect that such random FFSMs/families of FSMs must have shared commonalities, but present some behavioral variability, e.g.,:

- Addition/deletion of states
- Addition/deletion of input symbols
- Edits of output symbols in transitions
- Edits of tail states in transitions
