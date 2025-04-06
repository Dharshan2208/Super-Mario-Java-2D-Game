# Super Mario Java 2D Game

A full-featured 2D platformer game inspired by Super Mario Bros, implemented entirely in Java. This project showcases object-oriented programming techniques while providing an entertaining gaming experience with classic platformer mechanics.

## Features

- Fluid character movement with physics-based gameplay
- Multiple levels with increasing difficulty
- Classic enemies and power-ups
- Attractive graphics with smooth animations
- Score tracking and lives system

## How to Run

### Prerequisites
- Java JDK 8 or higher
- NetBeans IDE (recommended) or any Java IDE

### Running the Game

#### Using NetBeans IDE:
1. Open the project in NetBeans
2. Click on "Run Project" or press F6

#### Using Command Line:
```bash
# Navigate to the project directory
cd Super-Mario-Java-2D-Game

# Compile the source code
javac -d build/classes -cp src src/com/TETOSOFT/tilegame/GameEngine.java

# Run the game
java -cp build/classes com.TETOSOFT.tilegame.GameEngine
```

#### Using JAR File:
```bash
java -jar dist/SuperMiroGame.jar
```

## Game Controls
- **Left/Right Arrow Keys**: Move the player left/right
- **Space**: Jump
- **ESC**: Exit the game

## Object-Oriented Programming Concepts

This project demonstrates all key OOP concepts:

### 1. Abstraction
- `Creature` is an abstract class that defines common behavior for game entities
- Abstract methods like `getMaxSpeed()` define behaviors without implementation details
- The game engine abstracts game loop logic from rendering and input handling

### 2. Encapsulation
- Private fields with public getters/setters (e.g., in `Sprite`, `Animation` classes)
- Implementation details are hidden within classes, exposing only necessary interfaces
- State management is encapsulated within respective classes

### 3. Inheritance
- Class hierarchy: `Sprite` → `Creature` → (`Player`, `Grub`, `Fly`)
- `PowerUp` extends `Sprite`
- `GameEngine` extends `GameCore`
- Subclasses inherit and extend functionality from parent classes

### 4. Polymorphism
- Method overriding: Different creatures implement their own versions of methods like `getMaxSpeed()`, `update()`, `isFlying()`
- Runtime polymorphism through inheritance hierarchies
- Different creature types behave uniquely while sharing common interfaces

### 5. Interface Implementation
- The input manager implements multiple Java interfaces: `KeyListener`, `MouseListener`, `MouseMotionListener`
- Component-based architecture with interfaces guiding implementation contracts

### 6. Composition/Aggregation
- Composition: `Sprite` contains an `Animation` object
- Aggregation: `GameEngine` manages but doesn't own the lifecycle of `TileMap`, `MapLoader`, etc.
- Complex objects composed of simpler objects in a hierarchical structure

### 7. Exception Handling
- Try-catch blocks for resource loading, reflection operations, and I/O operations
- Graceful error handling for file operations and game state management
- Recovery mechanisms when resources fail to load

## Architecture Overview

The game follows a component-based architecture:
- **Graphics Package**: Handles rendering, animations, and sprites
- **Input Package**: Manages keyboard and mouse input
- **TileGame Package**: Contains game logic, level management, and entity behavior
- **Test Package**: Provides the game core framework


## Acknowledgments

- Original game design and concept by Nintendo
- Game is developed for educational purposes

---

Please visit the developer's website: http://www.mohamedtalaat.net/
