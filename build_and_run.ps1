$ErrorActionPreference = "Stop"

# Paths
$JAVAFX_LIB = "C:\Users\hecto\OneDrive\Escritorio\Hector\CURSO 2\IPC\javafx-sdk-24.0.1\lib"
$LIBS = "lib\lib\IPC2025.jar;lib\lib\sqlite-jdbc-3.49.1.0.jar"
$SRC_DIR = "src"
$OUT_DIR = "build\classes"

# Create output dir
If (!(Test-Path $OUT_DIR)) {
    New-Item -ItemType Directory -Force -Path $OUT_DIR | Out-Null
}

Write-Host "Compilando clases Java..."
# Get all java files
$javaFiles = Get-ChildItem -Path $SRC_DIR -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

Write-Host "Limpiando BOM de archivos Java..."
foreach ($file in $javaFiles) {
    $content = Get-Content -Path $file -Raw
    $utf8NoBom = New-Object System.Text.UTF8Encoding($False)
    [System.IO.File]::WriteAllText($file, $content, $utf8NoBom)
}

# Compile
$javaFiles | Out-File -FilePath "sources.txt" -Encoding ascii
$javacArgs = @(
    "-d", $OUT_DIR,
    "-cp", $LIBS,
    "--module-path", $JAVAFX_LIB,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.swing",
    "-encoding", "UTF-8",
    "@sources.txt"
)
& javac @javacArgs

Write-Host "Copiando recursos (FXML, CSS, imagenes)..."
# Copy all non-java files to build/classes
Get-ChildItem -Path $SRC_DIR -Recurse | Where-Object { 
    $_.Extension -ne ".java" -and -not $_.PSIsContainer 
} | ForEach-Object {
    $relativePath = $_.FullName.Substring((Resolve-Path $SRC_DIR).Path.Length + 1)
    $targetPath = Join-Path $OUT_DIR $relativePath
    $targetDir = Split-Path $targetPath -Parent
    
    if (!(Test-Path $targetDir)) {
        New-Item -ItemType Directory -Force -Path $targetDir | Out-Null
    }
    Copy-Item $_.FullName -Destination $targetPath -Force
}

Write-Host "Ejecutando la aplicacion..."
# Run
$javaArgs = @(
    "-cp", "$OUT_DIR;$LIBS",
    "--module-path", $JAVAFX_LIB,
    "--add-modules", "javafx.controls,javafx.fxml,javafx.swing",
    "poiupv.PoiUPVApp"
)
& java @javaArgs
