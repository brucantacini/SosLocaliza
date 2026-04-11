# Uso (na raiz do projeto SosLocaliza):
#   . .\scripts\load-local-env.ps1
# Depois: java -version, .\mvnw.cmd compile, .\mvnw.cmd spring-boot:run
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$envFile = Join-Path $ProjectRoot ".env.local"
if (-not (Test-Path $envFile)) {
    $envFile = Join-Path $ProjectRoot ".env"
}
if (-not (Test-Path $envFile)) {
    Write-Host "Crie .env.local (recomendado) copiando .env.example — veja JAVA_HOME." -ForegroundColor Yellow
    return
}

Get-Content $envFile -Encoding UTF8 | ForEach-Object {
    $line = $_.Trim()
    if ($line -match '^\s*#' -or $line -eq '') { return }
    if ($line -match '^([A-Za-z_][A-Za-z0-9_]*)=(.*)$') {
        $val = $matches[2].Trim()
        if (($val.StartsWith('"') -and $val.EndsWith('"')) -or ($val.StartsWith("'") -and $val.EndsWith("'"))) {
            $val = $val.Substring(1, $val.Length - 2)
        }
        Set-Item -Path ("Env:{0}" -f $matches[1]) -Value $val
    }
}

if (-not $env:JAVA_HOME) {
    Write-Warning "JAVA_HOME não está definido em $envFile"
    return
}

$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
Write-Host "OK: JAVA_HOME=$env:JAVA_HOME" -ForegroundColor Green
