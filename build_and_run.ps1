param(
    [string]$JavaFxLib = $env:JAVAFX_LIB
)

$ErrorActionPreference = "Stop"

if (-not $JavaFxLib) {
    throw "Pass the JavaFX lib path with -JavaFxLib or set JAVAFX_LIB"
}

if (-not (Test-Path -LiteralPath $JavaFxLib -PathType Container)) {
    throw "JavaFX lib directory not found: $JavaFxLib"
}

$JavaFxLib = (Resolve-Path -LiteralPath $JavaFxLib).Path
$SourceDirectory = "src"
$OutputDirectory = "build\classes"
$Libraries = "lib\lib\IPC2025.jar;lib\lib\sqlite-jdbc-3.49.1.0.jar"
$JavaFiles = @(Get-ChildItem -Path $SourceDirectory -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName)

if ($JavaFiles.Count -eq 0) {
    throw "No Java source files found in $SourceDirectory"
}

New-Item -ItemType Directory -Force -Path $OutputDirectory | Out-Null

$JavacArgs = @(
    "-d", $OutputDirectory,
    "-cp", $Libraries,
    "--module-path", $JavaFxLib,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.swing",
    "-encoding", "UTF-8"
) + $JavaFiles

Write-Host "Compiling Java sources..."
& javac @JavacArgs
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed with exit code $LASTEXITCODE"
}

$SourceRoot = (Resolve-Path -LiteralPath $SourceDirectory).Path
Write-Host "Copying FXML, CSS, and image resources..."
Get-ChildItem -Path $SourceDirectory -Recurse -File |
    Where-Object { $_.Extension -ne ".java" } |
    ForEach-Object {
        $RelativePath = $_.FullName.Substring($SourceRoot.Length).TrimStart([char[]]@(
            [System.IO.Path]::DirectorySeparatorChar,
            [System.IO.Path]::AltDirectorySeparatorChar
        ))
        $TargetPath = Join-Path $OutputDirectory $RelativePath
        $TargetDirectory = Split-Path $TargetPath -Parent

        New-Item -ItemType Directory -Force -Path $TargetDirectory | Out-Null
        Copy-Item -LiteralPath $_.FullName -Destination $TargetPath -Force
    }

$JavaArgs = @(
    "-cp", "$OutputDirectory;$Libraries",
    "--module-path", $JavaFxLib,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.swing",
    "poiupv.PoiUPVApp"
)

Write-Host "Starting the application..."
& java @JavaArgs
if ($LASTEXITCODE -ne 0) {
    throw "Application exited with code $LASTEXITCODE"
}
