Author: C.T.Fish

This project is a custom-built AI system designed to play a Chrome‑Dino–style side‑scrolling game using self‑designed neural networks and evolutionary learning.
The entire architecture is implemented from scratch in Java.
The goal of the project is to explore:
- How neural networks function at their core
- How to design scalable AI systems without external ML libraries
- How to build different types of ML architectures (Basic MLP, DQN, Image recognition, etc)
This repository contains multiple model versions as the architecture evolves, along with the environment (Primarily the "dino game" and training utilities.

Repository Navigation
- "AILearnsTRexGame" is my first look into AI and consists of a home-made chrome dinosaur game and a MLP
      a. SRC contains all of the various updates to the models labeled by Version numbers (neuralNetworkV#)
      b. neuralNetworkVT is the current updated model in progress
      c. Within each model, the "RunningTest" class will contain a changelog of what is updated between versions
- More folders will be added to the repository as I begin to learn and test new models and architecture

AILearnsTRexGame Neural Network Architecture
  - Fully custom feedforward neural network
  - Dynamic layer construction (supports arbitrary layer sizes)
  - Tanh activation and normalized inputs
  - Clean separation between parent and child networks
  - Deep‑copy logic for safe evolutionary reproduction
 
  Evolutionary Learning
  - Mutation‑based training (weights and biases)
  - Fitness evaluation based on in‑game performance
  - Automatic generation cycling
  - Population‑based learning (multi‑agent support)
  
  Game Integration
  - Real‑time decision making (jump/crouch)
  - Input normalization for consistent predictions
  - Obstacle distance calculations
  - Per‑agent physics and state tracking (for population mode)

