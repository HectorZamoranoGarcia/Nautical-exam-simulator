# Nautical Exam Simulator

A JavaFX desktop app for practising coastal navigation exercises. Students choose a chart problem, work through it with on-screen navigation tools, and check their answers. The app also keeps hit and fault totals between sessions.

![Java 22+](https://img.shields.io/badge/Java-22%2B-007396?style=flat-square&logo=openjdk&logoColor=white)
![JavaFX 24](https://img.shields.io/badge/JavaFX-24-FF0000?style=flat-square&logo=openjfx&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-3-003B57?style=flat-square&logo=sqlite&logoColor=white)

## Features

- Select a problem from a numbered list or choose one at random
- Use a protractor, ruler, compass, and chart drawing tools
- Add lines, arcs, point markers, and text annotations
- Zoom and pan around the nautical chart
- Review hit and fault totals across sessions

## Project layout

```text
src/
├── javafxmlapplication/   JavaFX controllers
├── poiupv/                Application entry point and chart point model
├── resources/             Icons and chart images
└── vista/                 FXML layouts and CSS
lib/lib/
├── IPC2025.jar            Domain model and navigation layer
└── sqlite-jdbc-3.49.1.0.jar
data.db                    SQLite database used by the app
```

The application code in this repository uses the `IPC2025.jar` model and SQLite JDBC driver from `lib/lib`. It reads and writes `data.db` in the working directory. The database file is tracked in this repository, so make a copy before using the app if you need to keep its current contents.

## Requirements

- JDK 22 or later
- JavaFX SDK 24
- NetBeans with its `JAVAFX` library configured, or PowerShell on Windows

Java sources target version 21. JavaFX 24 itself needs JDK 22 or later.

## Run on Windows

Set the JavaFX SDK `lib` directory when launching the included script:

```powershell
.\build_and_run.ps1 -JavaFxLib "C:\path\to\javafx-sdk-24.0.1\lib"
```

The script compiles the Java files, copies FXML, CSS, and image resources, then launches the app. It does not modify the source files.

## Run from NetBeans

Open the project and configure a NetBeans library named `JAVAFX` with the JavaFX SDK 24 libraries. Then run the project from the IDE.

## Screens

| Screen | Controller | Purpose |
|---|---|---|
| Authentication | `FXMLAutenticarseController` | Sign in |
| Registration | `FXMLRegistrarseController` | Create an account |
| Work canvas | `FXMLTrabajoController` | Solve and submit problems |
| Problem picker | `ElegirProblemaController` | Choose a problem |
| Profile editor | `PerfilPanelController` | Update profile details |
| Statistics | `EstadisticasController` | View hit and fault totals |
| Ruler rotation | `RotarReglaController` | Rotate the ruler overlay |

## Authors

- Héctor Zamorano García
- Unai Soler
- Jordi Encabo

Copilot was used occasionally for syntax suggestions. The authors made the project and design decisions and handled implementation and debugging.

## License

This project is licensed under the [MIT License](LICENSE).
