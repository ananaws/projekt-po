# Programowanie obiektowe - projekt
Projekt został stworzony w Java 21 używając gradle (z kotlin dsl) build system.
## Opis projektu
Tematem projektu jest symulacja agentowa.. pustyni z rybami, które żyją poza wodą, i gepardami.
## Funkcjonalność
- Graficzne przedstawianie stanu planszy
- Pathfinding z użyciem A*
- Proste AI dla zwierząt
## Uruchamianie
- Sklonować repo
- ./gradlew -q --console plain run
Testowane tylko na Linuxie.
## Struktura projektu
src/main/java
├── Board.java
├── entities
│   ├── animals
│   │   ├── Animal.java
│   │   ├── Fish.java
│   │   └── Leopard.java
│   ├── blocks
│   │   ├── Block.java
│   │   ├── Rock.java
│   │   └── Water.java
│   ├── EAnimalStates.java
│   ├── EItemStates.java
│   ├── Entity.java
│   ├── ifaces
│   │   ├── Consumer.java
│   │   ├── Edible.java
│   │   ├── Lifetime.java
│   │   └── MovingEdible.java
│   └── items
│       ├── CatFood.java
│       ├── EdibleItem.java
│       └── Item.java
├── LogWriter.java
├── Main.java
├── pathfinding
│   ├── aStar.java
│   ├── Node.java
│   ├── Pathfinder.java
│   └── PfState.java
└── Position.java
