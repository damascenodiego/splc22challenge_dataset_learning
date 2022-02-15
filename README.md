# Challenge Case (SPLC 2022)

## Title
A Dataset for Active Learning of  Variability-Intensive Systems

## Abstract
Behavioral models are the key enablers for behavioral analysis of Software Product Lines (SPL), including testing and model checking. Active model learning comes to the rescue when family behavioral models are non-existent or outdated. A key challenge on active model learning is to detect commonalities and variability efficiently and combine them into concise family models. A dataset and its associated metrics will play a key role in shaping the research agenda in this promising field and provide an effective means for comparing and identifying relative strengths and weaknesses in the forthcoming techniques. In this challenge, we seek a dataset for evaluating the efficiency (e.g., learning time and memory footprint) and effectiveness (e.g., conciseness and accuracy of family models) of active model learning methods in the software product line context. This dataset will contain the structural and behavioral variability models of at least one SPL. Alternatively, the dataset can be provided as a tool supporting the random generation of behavioral variability models. Each SPL in this dataset should contain at least one product that requires more than one round for model learning with respect to the basic active learning L* algorithm.

## Repository structure

//TODO

## Generation of Random FFSMs or Families of FSMs

To date there are no systematic procedures for generating synthetic datasets of families of (referred to as Finite State Machine - FSM) with behavioral commonalities/variability for benchmarking active model learning techniques. Thus, the solution for this challenge can be alternatively provided as a tool for randomly generating behavioral variability models, such as _FFSMs_ or _families of FSMs_.

As an approach to generate random FFSM/FSMs models, we expect the authors to report a systematic process and implementation to synthesize:

- Random Featured Finite State Machines (FFSM) or 
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
