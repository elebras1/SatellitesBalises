# SatellitesBalises

## Table des matières

1. [Introduction](#introduction)
2. [Architecture générale](#architecture-générale)
3. [Le Modèle (Model)](#le-modèle-model)
4. [La Vue (View)](#la-vue-view)
5. [Patron de conception Stratégie](#patron-de-conception-stratégie)
6. [Patron de conception Observateur](#patron-de-conception-observateur)
7. [Le système de Programme](#le-système-de-programme)
8. [Diagrammes UML](#diagrammes-uml)
9. [Flux d'exécution](#flux-dexécution)
10. [L'interpréteur Benglemsc](#linterpréteur-benglemsc)
11. [Synthèse des patrons de conception](#synthèse-des-patrons-de-conception)
12. [Conclusion](#conclusion)

---

## 1. Introduction

Le projet **SatellitesBalises** est une simulation interactive qui modélise le comportement de bouées marines et de satellites en orbite. Le système simule :

- Le déplacement autonome des entités (bouées et satellites)
- La communication événementielle entre les entités via un système d'observateurs
- La collecte et la synchronisation de données entre bouées et satellites
- Les cycles de plongée et remontée des bouées
- L'affichage graphique en temps réel (avec ou sans UI)
- Un interpréteur de langage DSL (Benglemsc) pour créer dynamiquement des entités

### Objectifs pédagogiques

Ce projet illustre l'utilisation de plusieurs patrons de conception :
- **Stratégie** : pour la gestion modulaire des déplacements
- **Observateur** : pour la communication événementielle découplée
- **Programme** : pour l'orchestration des comportements complexes
- **Interface/Implémentation** : avec `Mobile`, `World`, `Program`, `Context`

---

## 2. Architecture générale

### 2.1 Structure des packages

```
org
├── model/                    # Modèle métier
│   ├── Mobile.java          (interface)
│   ├── Buoy.java
│   └── Satellite.java
├── strategy/                 # Stratégies de mouvement
│   ├── MovementStrategy.java (interface)
│   └── movement/
│       ├── HorizontalMovement.java
│       ├── HorizontalMovementSatellite.java
│       ├── SinusMovement.java
│       ├── DiveMovement.java
│       ├── ToSurfaceMovement.java
│       └── VerticalMovement.java
├── event/                    # Événements
│   ├── MovementEvent.java
│   ├── PositionChangedEvent.java
│   ├── StartSyncViewEvent.java
│   ├── EndSyncViewEvent.java
│   ├── WaitingEvent.java
│   ├── StartSyncEvent.java
│   ├── EndSyncEvent.java
│   ├── DataCollectionEvent.java
│   ├── DataCollectionCompleteEvent.java
│   └── DiveEvent.java
├── eventHandler/             # Gestionnaire d'événements
│   ├── AbstractEvent.java
│   └── EventHandler.java
├── view/                     # Vues graphiques
│   ├── BuoyView.java
│   ├── BuoyViewHeadless.java
│   ├── SatelliteView.java
│   └── SatelliteViewHeadless.java
├── simulation/               # Orchestration
│   ├── Context.java         (interface)
│   ├── SimulationContext.java
│   ├── World.java           (interface)
│   ├── SimulationWithUI.java
│   ├── SimulationWithoutUI.java
│   ├── EditorCode.java
│   └── program/
│       ├── Program.java     (interface)
│       ├── BuoyProgram.java
│       └── SatelliteProgram.java
└── interpreter/              # Interpréteur DSL
    └── BenglemscInterpreter.java
```

### 2.2 Principe architectural

- **Modèle** : `Mobile` (interface), `Buoy`, `Satellite` - logique métier
- **Vue** : `BuoyView`, `SatelliteView` - affichage graphique réactif
- **Contrôleur/Orchestration** : `Program`, `World`, `SimulationContext`
- **Stratégie** : Délégation du comportement de mouvement
- **Observateur** : Communication événementielle via `EventHandler`

---

## 3. Le Modèle (Model)

### 3.1 Interface Mobile

`Mobile` est l'interface centrale qui définit le contrat pour toute entité mobile dans la simulation.

**Responsabilités** :
```java
public interface Mobile {
    EventHandler getEventHandler();
    void move();
    Point getPoint();
    void setPoint(Point point);
    int getMobileWidth();
    MovementStrategy getMovementStrategy();
    void setMovementStrategy(MovementStrategy movementStrategy);
    int getDataCollected();
    void setDataCollected(int dataCollected);
    Point getStartDepth();
    void startSyncingData();
    void stopSyncingData();
    boolean isSyncing();
    void start();
    void stop();
    void onStartSync(Mobile mobile);
    void onEndSync(Mobile mobile);
    boolean canSync(Mobile mobile);
}
```

### 3.2 Classe Buoy (Bouée)

**Attributs principaux** :
```java
private final EventHandler eventHandler;
private final int width;
private Point point;
private MovementStrategy movementStrategy;
private int dataCollected;
private final int maxData;
private boolean isCollecting = true;
private boolean isSyncing = false;
private final Random random;
private final Point startDepth;
private boolean isMoving = false;
```

**Comportement spécifique** :

#### Collecte de données
```java
private void collectData() {
    if (this.isCollecting && this.dataCollected < this.maxData) {
        this.dataCollected += 1 + this.random.nextInt(5);
        if (this.dataCollected >= this.maxData) {
            this.dataCollected = this.maxData;
            this.onDataCollectionComplete();
        }
    }
}
```
- Collecte aléatoire : entre 1 et 5 unités de données par cycle
- Déclenchement automatique de `DataCollectionCompleteEvent` à capacité maximale

#### Synchronisation avec satellite
```java
public boolean canSync(Mobile mobile) {
    Point sourcePosition = this.getPoint();
    Point targetPosition = mobile.getPoint();
    
    return (sourcePosition.x) >= (targetPosition.x - 20) 
        && (sourcePosition.x) <= (targetPosition.x + 20)
        && this.getDataCollected() != 0 
        && !this.isCollecting() 
        && mobile.isSyncing() == false 
        && this.isSyncing() == false;
}
```

**Conditions de synchronisation** :
- Proximité horizontale : ±20 pixels du satellite
- Bouée doit avoir des données (`dataCollected != 0`)
- Bouée ne collecte plus (`!isCollecting`)
- Aucune synchronisation en cours

#### Processus de synchronisation asynchrone
```java
public void startSync(Satellite satellite) {
    this.getEventHandler().send(new StartSyncViewEvent(this));
    this.startSyncingData();
    satellite.startSync();
    
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    
    scheduler.schedule(() -> {
        // Transfert des données après 2 secondes
        satellite.setDataCollected(satellite.getDataCollected() + this.getDataCollected());
        this.setDataCollected(0);
        this.getEventHandler().send(new EndSyncEvent(this));
        scheduler.shutdown();
    }, 2, TimeUnit.SECONDS);
}
```

**Caractéristiques** :
-  Non-bloquant : utilise `ScheduledExecutorService`
-  Durée fixe : 2 secondes
-  Transfert complet des données vers le satellite
-  Remise à zéro du compteur de la bouée

**États de la bouée** :
1. **SOUS_EAU** : déplacement horizontal sous l'eau, collecte de données
2. **PLEINE** : capacité maximale atteinte, arrêt de collecte
3. **REMONTÉE** : montée verticale vers la surface
4. **EN_SURFACE** : en attente d'un satellite pour synchronisation
5. **SYNCHRONISATION** : transfert des données (2 secondes)
6. **PLONGÉE** : descente verticale pour retourner sous l'eau

**Événements émis** :
- `PositionChangedEvent` : à chaque mouvement
- `DataCollectionCompleteEvent` : capacité maximale
- `StartSyncViewEvent` : début de synchronisation (visuel)
- `EndSyncEvent` : fin de transfert
- `EndSyncViewEvent` : fin de synchronisation (visuel)
- `DiveEvent` : début de plongée

### 3.3 Classe Satellite

**Attributs principaux** :
```java
private final EventHandler eventHandler;
private final int width;
private Point point;
private MovementStrategy movementStrategy;
private int dataCollected;
private boolean isSyncing = false;
private boolean isMoving = false;
```

**Comportement spécifique** :

Le satellite a un rôle plus passif :
- Il se déplace en orbite (mouvement horizontal constant)
- Il écoute les événements des bouées
- Il reçoit et stocke les données lors des synchronisations
- Il n'a pas de limite de capacité

**Méthodes de synchronisation** :
```java
public void startSync() {
    this.startSyncingData();
    this.getEventHandler().send(new StartSyncViewEvent(this));
}

public void endSync() {
    this.stopSyncingData();
    this.getEventHandler().send(new EndSyncViewEvent(this));
}
```

**Différence avec Buoy** :

- Pas de collecte de données autonome

---

## 4. La Vue (View)

### 4.1 Principe de fonctionnement

Les vues sont des **observateurs** du modèle. Elles écoutent les événements et mettent à jour l'affichage en conséquence. Le projet propose deux variantes :

- **Avec UI** : `BuoyView`, `SatelliteView` (affichage graphique complet)
- **Sans UI (Headless)** : `BuoyViewHeadless`, `SatelliteViewHeadless` (affichage minimal)

### 4.2 BuoyView

**Responsabilités** :
- Charger et afficher l'image de la bouée (`submarine.png`) ou un élément graphique JComponent
- Mettre à jour la position graphique en temps réel
- Changer l'apparence lors de la synchronisation

**Événements écoutés** :
```java
// Implémente EventListener
public void onEvent(AbstractEvent event) {
    if (event instanceof PositionChangedEvent) {
        updatePosition();      // Déplace l'image
    } else if (event instanceof StartSyncViewEvent) {
        highlightSync();       // Change couleur/opacité
    } else if (event instanceof EndSyncViewEvent) {
        resetVisual();         // Retour à l'apparence normale
    }
}
```

**Flux typique** :
```
Buoy.move()
    └─> eventHandler.send(PositionChangedEvent)
            └─> BuoyView.onEvent(PositionChangedEvent)
                    └─> buoyView.setLocation(newPoint)
```

### 4.3 SatelliteView

Fonctionnement identique à `BuoyView` :
- Charge l'image `satellite.png` ou un élément graphique JComponent
- Réagit aux mêmes événements (`PositionChangedEvent`, `StartSyncViewEvent`, `EndSyncViewEvent`)

### 4.4 Variantes Headless

`BuoyViewHeadless` et `SatelliteViewHeadless` sont utilisées dans `SimulationWithoutUI` :
- Affichage minimal (pas d'effet visuel lors de la sync)
- Performance optimisée
- Permet de tester la logique sans interface graphique

### 4.5 Découplage Modèle-Vue

**Principe clé** : Le modèle ne connaît pas la vue.

```
┌─────────┐                    ┌──────────────┐
│  Buoy   │───sends event────> │ EventHandler │
└─────────┘                    └──────┬───────┘
                                      │
                         ┌────────────┴─────────────┐
                         │                          │
                    ┌────▼────┐              ┌─────▼──────┐
                    │BuoyView │              │OtherListener│
                    └─────────┘              └────────────┘
```

 **Avantages** :
- Changement de vue sans modifier le modèle
- Plusieurs vues pour le même modèle
- Testabilité : simulation sans UI

---

## 5. Patron de conception Stratégie

### 5.1 Principe du pattern Stratégie

Le pattern **Stratégie** encapsule une famille d'algorithmes et les rend interchangeables. L'algorithme varie indépendamment des clients.

**Dans ce projet** : Les mouvements des `Mobile` sont externalisés dans des stratégies.

### 5.2 Interface MovementStrategy

```java
public interface MovementStrategy {
    void move(Mobile mobile);
}
```

**Contrat** :
- Reçoit un `Mobile` en paramètre
- Modifie sa position via `mobile.setPoint(newPoint)`
- Peut accéder au contexte via le mobile

### 5.3 Stratégies disponibles

#### HorizontalMovement
```java
public class HorizontalMovement implements MovementStrategy {
    private final Context context;
    private final int speed;
    
    @Override
    public void move(Mobile mobile) {
        Point currentPoint = mobile.getPoint();
        int newX = currentPoint.x + speed;
        
        // Wraparound : retour au début
        if (newX > ((SimulationContext)context).getWidth()) {
            newX = 0;
        }
        
        mobile.setPoint(new Point(newX, currentPoint.y));
    }
}
```
- **Usage** : Bouées **sous l'eau** pendant la collecte de données
- **Comportement** : Déplacement horizontal constant avec retour cyclique

#### HorizontalMovementSatellite
Similaire à `HorizontalMovement` mais adapté aux satellites :
- Même logique de wraparound
- Utilisé pour l'orbite des satellites

#### SinusMovement
```java
@Override
public void move(Mobile mobile) {
    Point currentPoint = mobile.getPoint();
    int newX = currentPoint.x + speed;
    
    // Trajectoire sinusoïdale
    int newY = (int)(amplitude * Math.sin(newX / frequency) + yOffset);
    
    if (newX > context.getWidth()) {
        newX = 0;
    }
    
    mobile.setPoint(new Point(newX, newY));
}
```
- **Usage** : Bouées **sous l'eau** pendant la collecte de données
- **Comportement** : Ondulation verticale pendant le déplacement horizontal

#### DiveMovement
```java
public class DiveMovement implements MovementStrategy {
    private final int targetDepth;
    private final int speed;
    
    @Override
    public void move(Mobile mobile) {
        Point currentPoint = mobile.getPoint();
        
        if (currentPoint.y < targetDepth) {
            int newY = currentPoint.y + speed;
            mobile.setPoint(new Point(currentPoint.x, newY));
        }
        // Arrêt automatique à la profondeur cible
    }
}
```
- **Usage** : Plongée de la bouée
- **Comportement** : Descente verticale jusqu'à `targetDepth`

#### ToSurfaceMovement
```java
@Override
public void move(Mobile mobile) {
    Point currentPoint = mobile.getPoint();
    int seaLevel = context.getSeaLevel();
    
    if (currentPoint.y > seaLevel) {
        int newY = currentPoint.y - speed;
        mobile.setPoint(new Point(currentPoint.x, newY));
    }
}
```
- **Usage** : Remontée de la bouée
- **Comportement** : Remontée verticale jusqu'au niveau de la mer

### 5.4 Changement dynamique de stratégie

**Exemple : Cycle de vie d'une bouée**

```java
// Phase 1 : Sous l'eau - mouvement horizontal et collecte
buoy.setMovementStrategy(new HorizontalMovement(context, speed));

// Phase 2 : Capacité maximale → Remontée déclenchée
buoy.setMovementStrategy(new ToSurfaceMovement(context, speed));

// Phase 3 : En surface → Synchronisation avec satellite

// Phase 4 : Après sync → Plongée
buoy.setMovementStrategy(new DiveMovement(context, speed));

// Phase 5 : Retour à la profondeur initiale → Reprise mouvement horizontal
buoy.setMovementStrategy(new HorizontalMovement(context, speed));
```

### 5.5 Avantages du pattern Stratégie

 **Extensibilité** : Ajout de nouvelles stratégies sans modifier `Mobile`  
 **Open/Closed Principle** : Ouvert à l'extension, fermé à la modification  
 **Testabilité** : Test unitaire isolé de chaque stratégie  
 **Flexibilité runtime** : Changement de comportement en cours d'exécution  
 **Élimination des conditionnelles** : Pas de `if/switch` dans `Mobile.move()`  

---

## 6. Patron de conception Observateur

### 6.1 Principe du pattern Observateur

Le pattern **Observateur** définit une relation un-à-plusieurs : quand un objet (Subject) change d'état, tous ses observateurs sont automatiquement notifiés.

**Dans ce projet** : Communication événementielle entre entités et vues.

### 6.2 Implémentation

#### EventHandler (Subject)

```java
public class EventHandler {
    private Map<Class<? extends AbstractEvent>, List<Object>> listeners;
    
    public void registerListener(Class<? extends AbstractEvent> eventType, 
                                 Object listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                 .add(listener);
    }
    
    public void send(AbstractEvent event) {
        List<Object> targets = listeners.get(event.getClass());
        if (targets != null) {
            for (Object listener : targets) {
                // Invocation via réflexion ou interface
                notifyListener(listener, event);
            }
        }
    }
}
```

**Caractéristiques** :
- Enregistrement par type d'événement
- Multicast : un événement → plusieurs listeners
- Thread-safe avec `CopyOnWriteArrayList` dans la simulation

#### AbstractEvent (Message)

```java
public abstract class AbstractEvent {
    protected Object source;
    
    public Object getSource() {
        return source;
    }
}
```

Tous les événements héritent de `AbstractEvent` et portent une référence à leur source.

### 6.3 Types d'événements

#### Événements de mouvement
- **MovementEvent** : Signal global de mise à jour (envoyé par les `Program`)
- **PositionChangedEvent** : Nouvelle position disponible (envoyé par les `Mobile`)

#### Événements de synchronisation
- **StartSyncViewEvent** : Début de synchronisation (affichage)
- **StartSyncEvent** : Début logique de synchronisation
- **EndSyncEvent** : Fin du transfert de données
- **EndSyncViewEvent** : Fin de synchronisation (affichage)
- **WaitingEvent** : Bouée en attente d'un satellite

#### Événements de données
- **DataCollectionCompleteEvent** : Capacité maximale atteinte

#### Événements de plongée
- **DiveEvent** : Début de plongée

### 6.4 Flux d'événements : Synchronisation bouée-satellite

**Scénario complet** :

```
1. Buoy collecte des données sous l'eau
   └─> à chaque move() si isCollecting
   
2. Buoy atteint maxData
   └─> DataCollectionCompleteEvent
           └─> BuoyProgram.onDataCollectionComplete()
                   └─> buoy.setMovementStrategy(ToSurfaceMovement)

3. Buoy remonte à la surface
   └─> Atteint le niveau de la mer
   └─> Vérifie canSync() avec satellites

4. Satellite à proximité détecté
   └─> buoy.startSync(satellite)
           ├─> StartSyncViewEvent → BuoyView, SatelliteView
           ├─> satellite.startSync()
           │       └─> StartSyncViewEvent → SatelliteView
           └─> Schedule transfert (2 secondes)

5. Transfert terminé (après 2s)
   └─> EndSyncEvent → Satellite
           └─> satellite.onEndSync()
                   └─> EndSyncViewEvent

6. Buoy termine la sync
   └─> EndSyncViewEvent → BuoyView
   └─> DiveEvent
           └─> BuoyProgram.onEndSync()
                   └─> buoy.setMovementStrategy(DiveMovement)
```

### 6.5 Enregistrement des listeners

**Dans SimulationWithUI** :

```java
// Vue écoute le modèle
buoy.getEventHandler().registerListener(PositionChangedEvent.class, buoyView);
buoy.getEventHandler().registerListener(StartSyncViewEvent.class, buoyView);
buoy.getEventHandler().registerListener(EndSyncViewEvent.class, buoyView);

// Programme écoute le modèle
buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, buoyProgram);
buoy.getEventHandler().registerListener(DataCollectionEvent.class, buoyProgram);
buoy.getEventHandler().registerListener(DiveEvent.class, buoyProgram);

// Modèle écoute le programme
buoyProgram.getEventHandler().registerListener(MovementEvent.class, buoy);

// Satellite écoute la bouée
buoy.getEventHandler().registerListener(WaitingEvent.class, satellite);
buoy.getEventHandler().registerListener(StartSyncEvent.class, satellite);
buoy.getEventHandler().registerListener(EndSyncEvent.class, satellite);
```

### 6.6 Avantages du pattern Observateur

 **Découplage fort** : Émetteur et récepteurs indépendants  
 **Extensibilité** : Ajout de nouveaux listeners sans modifier le sujet  
 **Broadcast naturel** : Un événement → N récepteurs  
 **Réactivité** : Mises à jour automatiques et immédiates  
 **Traçabilité** : Flux d'événements explicite  

---

## 7. Le système de Programme

### 7.1 Interface Program

Le `Program` est un patron d'orchestration qui coordonne le comportement complexe des entités.

```java
public interface Program {
    EventHandler getEventHandler();
    void registerListenerToModel(Class<? extends AbstractEvent> eventClass, Object listener);
    void onDataCollectionComplete(Mobile mobile);
    void onDataCollection(Mobile mobile);
    void onEndSync(Mobile mobile);
    void process();
    default void onNewBuoy(Buoy buoy) {}
    default void onNewSatellite(Satellite satellite) {}
}
```

**Responsabilités** :
- Gérer le cycle de vie des entités
- Orchestrer les changements de stratégie
- Réagir aux événements métier
- Coordonner les interactions entre entités

### 7.2 BuoyProgram

**Rôle** : Gère le cycle de vie complet d'une bouée.

```java
public class BuoyProgram implements Program {
    private final SimulationContext context;
    private final EventHandler eventHandler;
    private final MovementStrategy movementStrategyOrigin;
    private final Buoy buoy;
    
    @Override
    public void onDataCollectionComplete(Mobile mobile) {
        // Bouée pleine → Remontée
        mobile.setMovementStrategy(new ToSurfaceMovement(this.context, 1));
    }
    
    @Override
    public void onDataCollection(Mobile mobile) {
        // Sous l'eau → Collecte
        mobile.setMovementStrategy(this.movementStrategyOrigin);
        ((Buoy)mobile).collectingData();
    }
    
    @Override
    public void onEndSync(Mobile mobile) {
        // Sync terminée → Plongée
        mobile.setMovementStrategy(
            new DiveMovement((int)mobile.getStartDepth().getY(), 1)
        );
    }
    
    @Override
    public void process() {
        // Génère le signal de mouvement
        this.eventHandler.send(new MovementEvent(this));
    }
    
    @Override
    public void onNewSatellite(Satellite satellite) {
        // Abonnement automatique aux satellites
        this.registerListenerToModel(WaitingEvent.class, satellite);
        this.registerListenerToModel(StartSyncEvent.class, satellite);
        this.registerListenerToModel(EndSyncEvent.class, satellite);
    }
}
```

**Machine à états implicite** :

```
SOUS_EAU (HorizontalMovement)
    │ collecte données en profondeur
    ▼ maxData atteint
REMONTÉE (ToSurfaceMovement)
    │ atteint surface
    ▼ satellite détecté
SYNCHRONISATION (en surface)
    │ transfert terminé
    ▼
PLONGÉE (DiveMovement)
    │ atteint profondeur initiale
    ▼ recommence collecte
SOUS_EAU
```

### 7.3 SatelliteProgram

**Rôle** : Gère le satellite et ses abonnements aux bouées.

```java
public class SatelliteProgram implements Program {
    private final EventHandler eventHandler;
    private final Satellite satellite;
    
    @Override
    public void process() {
        this.eventHandler.send(new MovementEvent(this));
    }
    
    @Override
    public void onNewBuoy(Buoy buoy) {
        // Abonnement automatique aux nouvelles bouées
        buoy.getEventHandler().registerListener(WaitingEvent.class, this.satellite);
        buoy.getEventHandler().registerListener(StartSyncEvent.class, this.satellite);
        buoy.getEventHandler().registerListener(EndSyncEvent.class, this.satellite);
    }
}
```

**Caractéristiques** :
- Plus simple que `BuoyProgram` (pas de changement de stratégie)
- Gère uniquement les abonnements et le mouvement
- S'abonne automatiquement aux nouvelles bouées

### 7.4 Avantages du système Program

 **Séparation des préoccupations** : Logique métier ≠ orchestration  
 **Réutilisabilité** : Un programme par type d'entité  
 **Extensibilité** : Nouveaux programmes sans modifier le modèle  
 **Testabilité** : Test isolé de l'orchestration  

---

## 8. Diagrammes UML

### 8.1 Diagramme de classes - Modèle

```
┌─────────────────────────────────┐
│      <<interface>>              │
│         Mobile                  │
├─────────────────────────────────┤
│ + getEventHandler()             │
│ + move()                        │
│ + getPoint(): Point             │
│ + setPoint(Point)               │
│ + getMovementStrategy()         │
│ + setMovementStrategy()         │
│ + getDataCollected(): int       │
│ + setDataCollected(int)         │
│ + startSyncingData()            │
│ + stopSyncingData()             │
│ + isSyncing(): boolean          │
│ + start()                       │
│ + stop()                        │
│ + canSync(Mobile): boolean      │
│ + onStartSync(Mobile)           │
│ + onEndSync(Mobile)             │
└───────────┬─────────────────────┘
            │
            │ <<implements>>
      ┌─────┴──────┐
      │            │
┌─────▼──────┐  ┌──▼────────────┐
│    Buoy    │  │   Satellite   │
├────────────┤  ├───────────────┤
│ - width    │  │ - width       │
│ - point    │  │ - point       │
│ - maxData  │  │ - dataColl.   │
│ - dataColl.│  │ - isSyncing   │
│ - isColl.  │  │ - isMoving    │
│ - isSyncing│  │               │
│ - random   │  │               │
│ - startD.  │  │               │
│ - isMoving │  │               │
├────────────┤  ├───────────────┤
│ + collectD.│  │ + startSync() │
│ + canSync()│  │ + endSync()   │
│ + startSync│  │               │
│ + endSync()│  │               │
└────────────┘  └───────────────┘
```

### 8.2 Diagramme de classes - Stratégie

```
┌───────────────────────────────┐
│    <<interface>>              │
│    MovementStrategy           │
├───────────────────────────────┤
│ + move(Mobile): void          │
└─────────────┬─────────────────┘
              │
              │ <<implements>>
    ┌─────────┼─────────┬────────────┬──────────────┐
    │         │         │            │              │
┌───▼───┐ ┌──▼──────┐ ┌▼───────┐ ┌▼──────────┐ ┌▼─────────┐
│Horiz. │ │Horiz.   │ │Sinus   │ │Dive       │ │ToSurface │
│Move   │ │MoveSat  │ │Movement│ │Movement   │ │Movement  │
├───────┤ ├─────────┤ ├────────┤ ├───────────┤ ├──────────┤
│-contxt│ │-context │ │-context│ │-targetDep │ │-context  │
│-speed │ │-speed   │ │-speed  │ │-speed     │ │-speed    │
│       │ │         │ │-amplit.│ │           │ │          │
│       │ │         │ │-freq   │ │           │ │          │
└───────┘ └─────────┘ └────────┘ └───────────┘ └──────────┘
```

### 8.3 Diagramme de classes - Observateur

```
┌──────────────────┐         ┌──────────────────┐
│  EventHandler    │         │  EventListener   │
├──────────────────┤         │   (implicit)     │
│ - listeners      │         ├──────────────────┤
│   Map<Class,List>│         │ + onEvent(Event) │
├──────────────────┤         └────────┬─────────┘
│ + register()     │                  │
│ + send(Event)    │                  │ <<implements>>
└──────────────────┘        ┌─────────┼──────────┐
         △                  │         │          │
         │                  │         │          │
         │           ┌──────▼───┐ ┌──▼─────┐ ┌──▼──────┐
         │           │ BuoyView │ │Satellit│ │ Buoy    │
┌────────┴────────┐  │          │ │eView   │ │Program  │
│ AbstractEvent   │  └──────────┘ └────────┘ └─────────┘
├─────────────────┤
│ - source        │
└─────────────────┘
       △
       │ <<extends>>
 ┌─────┼─────┬──────────┬────────────┬───────────┐
 │     │     │          │            │           │
┌┴──┐ ┌┴───┐ ┌┴────┐ ┌──┴─────┐ ┌───┴────┐ ┌───┴────┐
│Mov│ │Pos │ │Start│ │End     │ │Data    │ │Dive    │
│emt│ │Chgd│ │Sync │ │Sync    │ │CollCpl │ │Event   │
│Evt│ │Evt │ │View │ │View    │ │Event   │ │        │
└───┘ └────┘ └─────┘ └────────┘ └────────┘ └────────┘
```

### 8.4 Diagramme de classes - Program et World

```
┌───────────────────────────┐
│    <<interface>>          │
│       Program             │
├───────────────────────────┤
│ + getEventHandler()       │
│ + registerListener()      │
│ + onDataCollComplete()    │
│ + onDataCollection()      │
│ + onEndSync()             │
│ + process()               │
│ + onNewBuoy()             │
│ + onNewSatellite()        │
└───────────┬───────────────┘
            │
            │ <<implements>>
      ┌─────┴──────┐
      │            │
┌─────▼──────┐  ┌──▼────────────┐
│BuoyProgram │  │SatelliteProgram│
├────────────┤  ├────────────────┤
│ - context  │  │ - satellite    │
│ - eventHdl │  │ - eventHandler │
│ - stratOri │  │                │
│ - buoy     │  │                │
└────────────┘  └────────────────┘

┌───────────────────────────┐
│    <<interface>>          │
│        World              │
├───────────────────────────┤
│ + getContext()            │
│ + createBuoy()            │
│ + createSatellite()       │
└───────────┬───────────────┘
            │
            │ <<implements>>
      ┌─────┴──────────┐
      │                │
┌─────▼────────┐  ┌────▼──────────┐
│SimulationWith│  │SimulationWith │
│UI            │  │outUI          │
├──────────────┤  ├───────────────┤
│ - context    │  │ - context     │
│ - space      │  │ - space       │
│ - programs   │  │ - programs    │
└──────────────┘  └───────────────┘
```

### 8.5 Diagramme de séquence - Synchronisation complète

```
Buoy         BuoyProgram    EventHandler   Satellite    SatelliteView
 │                │              │              │              │
 │──move()────────│              │              │              │
 │ (sous l'eau)   │              │              │              │
 │                │              │              │              │
 │──collectData()->│              │              │              │
 │  (maxData)     │              │              │              │
 │                │              │              │              │
 │──DataCollectionCompleteEvent->│              │              │
 │                │<─────────────│              │              │
 │                │              │              │              │
 │<─setMovementStrategy──────────│              │              │
 │  (ToSurfaceMovement)          │              │              │
 │                │              │              │              │
 │──move()────────│              │              │              │
 │  (remontée)    │              │              │              │
 │                │              │              │              │
 │──canSync(sat)->│──────────────│────────────> │              │
 │  true          │              │              │              │
 │                │              │              │              │
 │──startSync()───│──────────────│─────────────>│              │
 │                │              │              │              │
 │──StartSyncViewEvent──────────>│──────────────│─────────────>│
 │                │              │              │   (highlight) │
 │                │              │              │              │
 │──satellite.startSync()────────│──────────────│─────────────>│
 │                │              │  StartSyncViewEvent          │
 │                │              │              │              │
 │  [Schedule 2s] │              │              │              │
 │                │              │              │              │
 │  [après 2s]    │              │              │              │
 │                │              │              │              │
 │──transfert─────│──────────────│─────────────>│              │
 │  données       │              │              │              │
 │                │              │              │              │
 │──EndSyncEvent──│──────────────│─────────────>│              │
 │                │              │              │              │
 │<─onEndSync()───│──────────────│              │              │
 │                │              │              │              │
 │──EndSyncViewEvent─────────────│──────────────│─────────────>│
 │                │              │              │    (reset)   │
 │                │              │              │              │
 │──DiveEvent─────│──────────────│              │              │
 │                │<─────────────│              │              │
 │<─setMovementStrategy──────────│              │              │
 │  (DiveMovement)│              │              │              │
 │                │              │              │              │
```

### 8.6 Diagramme d'états - Cycle de vie d'une Bouée

```
                    ┌──────────────┐
                    │  [Initial]   │
                    └──────┬───────┘
                           │
                           │ start()
                           ▼
              ┌────────────────────────┐
              │      SOUS_EAU          │
              │  HorizontalMovement    │
              │  (déplacement sous     │
              │   l'eau horizontal)    │
              │  isCollecting = true   │
              │  isMoving = true       │
              └───┬──────────────┬─────┘
                  │              │
    dataCollected │              │  
    >= maxData    │              │  
                  │              │
                  ▼              │
         ┌────────────────┐      │
         │  REMONTÉE      │      │
         │ ToSurfaceMove  │      │
         │ (monte vers la │      │
         │  surface)      │      │
         │ isCollecting=F │      │
         └───────┬────────┘      │
                 │                │
                 │ y <= seaLevel  │
                 │ (surface       │
                 │  atteinte)     │
                 ▼                │
         ┌────────────────┐      │
         │ EN_SURFACE     │      │
         │ ATTENTE_SYNC   │      │
         │ (immobile)     │      │
         └───────┬────────┘      │
                 │                │
                 │ canSync()      │
                 │ && satellite   │
                 │ à proximité    │
                 ▼                │
         ┌────────────────┐      │
         │ SYNCHRONISATION│      │
         │ (en surface)   │      │
         │ isSyncing=true │      │
         │ [durée: 2s]    │      │
         └───────┬────────┘      │
                 │                │
                 │ EndSyncEvent   │
                 ▼                │
         ┌────────────────┐      │
         │  PLONGÉE       │      │
         │ DiveMovement   │      │
         │ (descend vers  │      │
         │  profondeur)   │      │
         │ isSyncing=false│      │
         └───────┬────────┘      │
                 │                │
                 │ y >= targetY   │
                 │ (profondeur    │
                 │  initiale)     │
                 └────────────────┘
                         │
                         └─> retour SOUS_EAU
```

### 8.7 Diagramme de séquence - Boucle principale

```
SimulationWithUI  BuoyProgram  Buoy  MovementStrategy  BuoyView
       │               │         │          │             │
       │──process()────>│         │          │             │
       │               │         │          │             │
       │               │─MovementEvent────> │             │
       │               │         │          │             │
       │               │         │──move()──>│             │
       │               │         │          │             │
       │               │         │<─Point───│             │
       │               │         │          │             │
       │               │         │──setPoint()            │
       │               │         │          │             │
       │               │         │─PositionChangedEvent──>│
       │               │         │          │             │
       │               │         │          │    (updateUI)
       │               │         │          │             │
       │◄──sleep(10ms)─┘         │          │             │
       │                         │          │             │
       │──process()──────────────>          │             │
       │                                    │             │
       │       (boucle infinie)             │             │
       │                                    │             │
```

### 8.8 Diagramme de séquence - Création dynamique d'entités (Interpreter)

```
EditorCode    BenglemscInterpreter    World    SimulationWithUI
    │                  │                │             │
    │──runCode()──────>│                │             │
    │                  │                │             │
    │                  │──visit(AST)───>│             │
    │                  │                │             │
    │                  │──createBuoy()──│────────────>│
    │                  │                │             │
    │                  │                │──addBuoy()──│
    │                  │                │             │
    │                  │                │──register───│
    │                  │                │  Listeners  │
    │                  │                │             │
    │                  │<───Buoy────────│<────────────│
    │                  │                │             │
    │                  │──buoy.start()──│             │
    │                  │                │             │
    │<─────────────────│                │             │
    │  (bouée active)  │                │             │
```

---

## 9. Flux d'exécution

### 9.1 Initialisation avec UI

**Classe** : `SimulationWithUI`

```java
public SimulationWithUI(SimulationContext context) {
    this.context = context;
    this.space = new NiSpace("Simulation Space", 
                             new Dimension(context.getWidth(), context.getHeight()));
    this.programs = new CopyOnWriteArrayList<>();
    this.initialize();
}

private void initialize() {
    this.addSea();
}

private void addSea() {
    NiRectangle sea = new NiRectangle();
    sea.setDimension(new Dimension(
        this.context.getWidth(), 
        this.context.getHeight() - this.context.getSeaLevel()
    ));
    sea.setBackground(Color.BLUE);
    sea.setLocation(0, this.context.getSeaLevel());
    this.space.add(sea);
}
```

**Étapes** :
1. Création du contexte de simulation (`SimulationContext`)
2. Création de l'espace graphique (`NiSpace`)
3. Ajout de la mer (rectangle bleu)
4. Liste vide de programmes (sera remplie dynamiquement)

### 9.2 Création dynamique d'entités

**Via l'interpréteur Benglemsc** :

```java
@Override
public Buoy createBuoy(int width, int maxData, int x, int y, 
                       MovementStrategy movementStrategy) throws IOException {
    Buoy buoy = this.addBuoy(width, maxData, x, y, movementStrategy);
    BuoyProgram program = new BuoyProgram(buoy, this.context);
    this.programs.add(program);
    this.registerNewBuoy(buoy, program);
    return buoy;
}

private void registerNewBuoy(Buoy buoy, BuoyProgram buoyProgram) {
    // Notifier tous les programmes existants
    for (Program program : this.programs) {
        program.onNewBuoy(buoy);
    }
    
    // Programme écoute le modèle
    buoyProgram.getEventHandler().registerListener(MovementEvent.class, buoy);
    buoy.getEventHandler().registerListener(DataCollectionCompleteEvent.class, buoyProgram);
    buoy.getEventHandler().registerListener(DataCollectionEvent.class, buoyProgram);
    buoy.getEventHandler().registerListener(DiveEvent.class, buoyProgram);
}
```

**Flux** :
1. Création de la bouée (`addBuoy`)
2. Création de son programme associé
3. Ajout à la liste des programmes
4. Enregistrement des listeners bidirectionnels
5. Notification des autres programmes (pour abonnements croisés)

### 9.3 Boucle principale

```java
public void process() {
    this.space.openInWindow();
    
    while (true) {
        try {
            Thread.sleep(10);  // ~100 FPS
            for(Program program : this.programs) {
                program.process();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

**Chaque cycle (10ms)** :
1. Chaque `Program` appelle `process()`
2. Chaque `Program` envoie `MovementEvent`
3. Les `Mobile` reçoivent `MovementEvent` → appellent `move()`
4. Les stratégies calculent la nouvelle position
5. Les `Mobile` émettent `PositionChangedEvent`
6. Les vues mettent à jour l'affichage

### 9.4 Cycle de vie complet d'une bouée

**Phase 1 : Sous l'eau - Collecte**
```
Buoy.move()
    └─> movementStrategy.move(this)  [HorizontalMovement]
            └─> Déplacement horizontal sous l'eau
    └─> collectData()
            └─> dataCollected += random(1-5)
            └─> if (dataCollected >= maxData)
                    └─> DataCollectionCompleteEvent
```

**Phase 2 : Remontée vers la surface**
```
BuoyProgram.onDataCollectionComplete()
    └─> buoy.setMovementStrategy(ToSurfaceMovement)

Buoy.move()
    └─> movementStrategy.move(this)  [ToSurfaceMovement]
            └─> y -= speed (monte vers la surface)
            └─> if (y <= seaLevel) arrêt
```

**Phase 3 : En surface - Détection satellite**
```
Buoy (immobile en surface)
    └─> for each Satellite in range
            └─> if (canSync(satellite))
                    └─> startSync(satellite)
```

**Phase 4 : Synchronisation en surface**
```
Buoy.startSync(satellite)
    ├─> StartSyncViewEvent → vues
    ├─> this.startSyncingData()
    ├─> satellite.startSync()
    │       └─> StartSyncViewEvent → vues
    └─> ScheduledExecutorService.schedule(2 seconds)
            └─> transfert données
            └─> EndSyncEvent
```

**Phase 5 : Plongée vers la profondeur**
```
Buoy.endSync()
    ├─> EndSyncViewEvent → vues
    ├─> this.stopSyncingData()
    └─> DiveEvent

BuoyProgram.onEndSync()
    └─> buoy.setMovementStrategy(DiveMovement)

Buoy.move()
    └─> movementStrategy.move(this)  [DiveMovement]
            └─> y += speed (descend)
            └─> if (y >= targetDepth) arrêt
```

**Phase 6 : Retour sous l'eau**
```
(Détection de retour à la profondeur de départ)

BuoyProgram.onDataCollection()
    ├─> buoy.setMovementStrategy(movementStrategyOrigin)
    └─> buoy.collectingData()

→ Retour en Phase 1 (déplacement horizontal sous l'eau)
```

### 9.5 Abonnements croisés

**Lors de l'ajout d'une nouvelle bouée** :

```
SimulationWithUI.createBuoy()
    └─> registerNewBuoy(buoy, buoyProgram)
            └─> for each Program (including SatellitePrograms)
                    └─> program.onNewBuoy(buoy)

SatelliteProgram.onNewBuoy(buoy)
    └─> buoy.getEventHandler().registerListener(WaitingEvent, satellite)
    └─> buoy.getEventHandler().registerListener(StartSyncEvent, satellite)
    └─> buoy.getEventHandler().registerListener(EndSyncEvent, satellite)
```

**Lors de l'ajout d'un nouveau satellite** :

```
SimulationWithUI.createSatellite()
    └─> registerNewSatellite(satellite, satelliteProgram)
            └─> for each Program (including BuoyPrograms)
                    └─> program.onNewSatellite(satellite)

BuoyProgram.onNewSatellite(satellite)
    └─> this.registerListenerToModel(WaitingEvent, satellite)
    └─> this.registerListenerToModel(StartSyncEvent, satellite)
    └─> this.registerListenerToModel(EndSyncEvent, satellite)
```

**Résultat** : Abonnements bidirectionnels automatiques entre toutes les bouées et tous les satellites.

---

## 10. L'interpréteur Benglemsc

### 10.1 Principe

**Benglemsc** est un DSL (Domain Specific Language) personnalisé pour créer dynamiquement des entités dans la simulation.

**Fonctionnalités** :
- Création de stratégies de mouvement
- Création de bouées et satellites
- Appel de méthodes sur les objets créés
- Syntaxe simple et intuitive

### 10.2 Syntaxe du langage

```benglemsc
// Création de stratégies
m1 := new HorizontalMovement(1);
m2 := new HorizontalMovementSatellite(1);
m3 := new SinusMovement(2);

// Création de bouées
b1 := new Buoy(64, 2000, 400, 500, m1);
b2 := new Buoy(64, 3000, 300, 450, m3);

// Création de satellites
s1 := new Satellite(64, 500, 150, m2);
s2 := new Satellite(64, 550, 140, m2);

// Démarrage des entités
b1.start();
b2.start();
s1.start();
s2.start();
```

**Éléments du langage** :
- **Variables** : `nom := valeur`
- **Instanciation** : `new ClassName(arg1, arg2, ...)`
- **Appel de méthode** : `object.method()`
- **Types supportés** : `int`, objets (`Buoy`, `Satellite`, `MovementStrategy`)

### 10.3 Implémentation de l'interpréteur

```java
public class BenglemscInterpreter extends BenglemscBaseVisitor<Object> {
    private final Map<String, Object> variables = new HashMap<>();
    private final World world;
    
    @Override
    public Object visitVarAssign(BenglemscParser.VarAssignContext ctx) {
        String varName = ctx.ID().getText();
        Object value = this.visit(ctx.expr());
        this.variables.put(varName, value);
        return value;
    }
    
    @Override
    public Object visitNewExpr(BenglemscParser.NewExprContext ctx) {
        String className = ctx.ID().getText();
        Object[] args = (Object[]) this.visit(ctx.argList());
        return this.createObject(className, args);
    }
    
    private Object createObject(String className, Object[] args) {
        return switch (className) {
            case "Buoy" -> this.createBuoy(args);
            case "Satellite" -> this.createSatellite(args);
            case "HorizontalMovement" -> 
                new HorizontalMovement(this.world.getContext(), (Integer) args[0]);
            case "SinusMovement" -> 
                new SinusMovement(this.world.getContext(), (Integer) args[0]);
            case "HorizontalMovementSatellite" -> 
                new HorizontalMovementSatellite(this.world.getContext(), (Integer) args[0]);
            default -> throw new RuntimeException("Classe inconnue: " + className);
        };
    }
}
```

**Architecture** :
- Utilise **ANTLR** pour la génération du parseur
- Patron **Visitor** pour parcourir l'AST
- **Réflexion Java** pour l'invocation de méthodes

### 10.4 Interface EditorCode

**Classe** : `EditorCode`

```java
public class EditorCode extends Frame {
    private final TextArea codeArea;
    private final World world;
    private final BenglemscInterpreter interpreter;
    
    private void runCode() {
        String code = this.codeArea.getText().trim();
        
        CharStream input = CharStreams.fromString(code);
        BenglemscLexer lexer = new BenglemscLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        BenglemscParser parser = new BenglemscParser(tokens);
        
        BenglemscParser.ProgramContext tree = parser.program();
        interpreter.visit(tree);
    }
}
```

**Fonctionnalités** :
- Éditeur de code avec coloration syntaxique basique
- Bouton "Exécuter" : parse et exécute le code
- Bouton "Effacer" : vide l'éditeur
- Code par défaut fourni au démarrage

### 10.5 Avantages de l'interpréteur

 **Flexibilité** : Création d'entités sans recompilation  
 **Expérimentation** : Test rapide de configurations  
 **Pédagogie** : Illustration de concepts de compilation  
 **Extensibilité** : Ajout facile de nouvelles commandes  

---

## 11. Synthèse des patrons de conception

### 11.1 Tableau récapitulatif

| Patron | Implémentation | Avantages | Localisation |
|--------|---------------|-----------|--------------|
| **Stratégie** | `MovementStrategy` + implémentations | Changement dynamique de comportement | `org.strategy.movement.*` |
| **Observateur** | `EventHandler` + `AbstractEvent` | Communication découplée | `org.eventHandler.*`, `org.event.*` |
| **Programme** | `Program` + `BuoyProgram` + `SatelliteProgram` | Orchestration complexe | `org.simulation.program.*` |
| **Interface** | `Mobile`, `World`, `Context`, `Program` | Polymorphisme et extensibilité | `org.model.*`, `org.simulation.*` |
| **Visitor** | `BenglemscInterpreter` | Parcours d'AST | `org.interpreter.*` |

### 11.2 Interactions entre patrons

```
        ┌──────────────┐
        │  STRATÉGIE   │  ◄──── Changement dynamique
        └───────┬──────┘        par Programme
                │
                ▼
        ┌──────────────┐
        │    MOBILE    │  ────> Émet événements
        └───────┬──────┘
                │
                ▼
        ┌──────────────┐
        │ OBSERVATEUR  │  ────> Notifie Vues
        └───────┬──────┘        et Programmes
                │
                ▼
        ┌──────────────┐
        │  PROGRAMME   │  ────> Orchestre
        └──────────────┘        changements stratégie
```

---

## 12. Conclusion

### 12.1 Points forts du projet

 **Architecture claire** : Séparation stricte modèle/vue/contrôle  
 **Extensibilité** : Ajout facile de nouvelles stratégies, événements, entités  
 **Testabilité** : Composants découplés et testables unitairement  
 **Flexibilité** : Comportements modifiables à l'exécution  
 **Pédagogie** : Illustration de multiples patrons de conception  

### 12.2 Évolutions possibles

**Fonctionnelles** :
- Ajout de nouveaux types d'entités (drones, stations de base)

**Techniques** :
- Persistance des données collectées (base de données)
- Mode multi-thread pour performances
- Tests unitaires et d'intégration
- Logs structurés avec SLF4J

---

**Documentation technique du projet SatellitesBalises**  
**Version** : 1.0  
**Date** : Novembre 2025  
**Auteur** : Christian Esteban NUNEZ GUAJARDO, Erwan LE BRAS, Sully MILLET