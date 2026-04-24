# Academic Nautical Exam Simulator

A desktop application for practising coastal navigation exercises in academic settings. Students load nautical chart problems, use on-screen tools to work through them, submit their answers, and receive immediate feedback. Session statistics are persisted across launches via an embedded SQLite database.

---

![Java 17+](https://img.shields.io/badge/Java-v17+-007396?style=for-the-badge&logo=java&logoColor=white)
![JavaFX 24](https://img.shields.io/badge/JavaFX-v24-FF0000?style=for-the-badge&logo=oracle&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Persistence-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![Antigravity](https://img.shields.io/badge/Workflow-Antigravity-ff69b4?style=for-the-badge&logo=python&logoColor=white)
![MVC Architecture](https://img.shields.io/badge/Pattern-MVC-green?style=for-the-badge)

## Overview

The simulator recreates the workflow of a real coastal navigation exam. A student selects a problem — either manually from a numbered list or at random — reads the scenario in a dedicated text panel, and uses the interactive chart canvas to reason through it. When ready, they select one of four multiple-choice answers and submit. The application records each hit and fault, aggregating them into a per-user statistics view that spans all sessions.

The chart canvas supports the full set of analogue tools a navigator would use on paper:

- protractor overlay for bearing measurement
- ruler with free rotation for distance estimation
- compass tool for point-to-point pixel distances
- freehand line and arc drawing (fixed or free radius)
- point markers in four styles (circle, cross, asterisk, star)
- text annotation at any position
- lat/lon crosshair lines for coordinate reference
- colour editing and element removal

All drawing is non-destructive within a session. The chart pane is zoomable and pannable via a slider and scroll bars.

---

## Architecture

The project follows a layered MVC structure.

The view layer consists of FXML files designed in Scene Builder, located in `src/vista/`. Each screen has a dedicated controller in `src/javafxmlapplication/`.

The model layer is packaged as an external JAR (`lib/IPC2025.jar`) and exposes the domain entities — `User`, `Problem`, `Answer`, `Session` — along with a `Navigation` facade that coordinates all database operations. Persistence uses the JDBC SQLite driver (`lib/sqlite-jdbc`) writing to `data.db` in the working directory.

The `poiupv` package contains the application entry point (`PoiUPVApp`) and the `Poi` value class used to manage points of interest on the chart canvas.

```
src/
├── javafxmlapplication/   # JavaFX controllers (one per FXML screen)
├── poiupv/                # Entry point + POI model
└── vista/                 # FXML layouts + CSS
lib/
└── lib/
    ├── IPC2025.jar        # Domain model and Navigation facade
    └── sqlite-jdbc-*.jar  # SQLite JDBC driver
data.db                    # Persistent user and session data
```

---

## Screens

| Screen | Controller | Purpose |
|---|---|---|
| Authentication | `FXMLAutenticarseController` | Login with nickname and password |
| Registration | `FXMLRegistrarseController` | Create account with avatar and birthdate |
| Work canvas | `FXMLTrabajoController` | Main drawing and exam workspace |
| Problem picker | `ElegirProblemaController` | Select or randomise a problem |
| Profile editor | `PerfilPanelController` | Update password, email, avatar |
| Statistics | `EstadisticasController` | Aggregated hit/fault summary |
| Ruler rotation | `RotarReglaController` | Floating slider to rotate the ruler overlay |
| Close session | `CerrarSesionController` | Persist session stats and exit |
| Close app | `CerrarAplicacionController` | Confirm application exit |

---

## Getting Started

### Prerequisites

- JDK 17 or later
- NetBeans IDE (Ant-based build) — or any IDE that supports Ant projects
- JavaFX SDK 24 on the module path

### Running

Open the project in NetBeans and select Run → Run Project (F6). The Ant build script handles classpath resolution for the bundled JARs automatically.

Alternatively, from the project root using Ant:

```bash
ant clean jar
java -jar dist/PoiUPVApp.jar
```

#### Running without an IDE (Windows)

If you don't have NetBeans or Ant installed, a PowerShell script is included to automatically compile and launch the project:

```powershell
.\build_and_run.ps1
```

*Note: This script requires `javac` and `java` to be available in your system's PATH, and you may need to update the `$JAVAFX_LIB` path inside the script to match your local JavaFX SDK installation.*

### First launch

On first launch, create an account via the registration screen. Choose any avatar image (PNG or JPG) or use the default one. Once logged in, the work canvas opens with all tools available. Select a problem from the picker to begin an exam session.

---

## Distribution Note

This project is distributed as source code and does not include a precompiled executable .jar file. This decision responds to technical and transparency considerations:
* JavaFX Architecture: Modern JavaFX applications require native libraries specific to each platform (Windows, macOS, Linux) that cannot be packaged universally in a single .jar without specialized tools.
* External Dependencies: The application uses SQLite JDBC and JavaFX components that require specific classpath configuration, complicating distribution via a simple JAR file.
* Academic Transparency: By providing the complete source code, the project's transparency is guaranteed and it facilitates code review, debugging, and customization by other developers.

For a robust multi-platform distribution, it would be recommended to use tools that exceed the academic scope of this project, which is why we opt for distribution as source code with clear compilation instructions.

## Author

Héctor Zamorano García

Project developed collaboratively with:
* Unai Soler
* Jordi Encabo

## Notes

This project was created for academic and training purposes.
Occasional AI assistance (Copilot) was used only for syntax suggestions, materials from Stack Overflow and some technical videos were also consulted.
All architecture, logic, UX decisions, and debugging were performed manually.

## Version and Requirements

* Java 17+
* JavaFX 24
* SQLite JDBC Driver
* Scene Builder (optional, for editing FXML views)

## License

This project is provided for educational use.
Redistribution or commercial use requires explicit permission from the authors.
