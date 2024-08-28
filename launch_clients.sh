#!/bin/bash
# Script pour lancer 10 instances de ChatClient

for i in {1...10}; do
  # Lancer le ChatClient avec le bon chemin vers le .jar et la classe principale

  java -cp build/libs/ChatClient-1.0-SNAPSHOT.jar org.example.ChatClient &
done
