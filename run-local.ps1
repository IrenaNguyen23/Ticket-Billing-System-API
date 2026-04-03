param(
    [switch]$NoGateway
)

$repoRoot = Split-Path -Parent $MyInvocation.MyCommand.Path

function Load-EnvFile([string]$path) {
    if (-not (Test-Path $path)) { return }
    Get-Content $path | ForEach-Object {
        $line = $_.Trim()
        if ($line -eq "" -or $line.StartsWith("#")) { return }
        if ($line -match "^([^=]+?)=(.*)$") {
            $name = $matches[1].Trim()
            $value = $matches[2]
            if ($value.StartsWith('"') -and $value.EndsWith('"')) {
                $value = $value.Substring(1, $value.Length - 2)
            }
            Set-Item -Path "Env:$name" -Value $value
        }
    }
}

$envLocal = Join-Path $repoRoot ".env.local"
$envDefault = Join-Path $repoRoot ".env"
if (Test-Path $envLocal) {
    Load-EnvFile $envLocal
} elseif (Test-Path $envDefault) {
    Load-EnvFile $envDefault
}

$gradleCmd = "gradle"
$gradlewBat = Join-Path $repoRoot "gradlew.bat"
if (Test-Path $gradlewBat) {
    $gradleCmd = $gradlewBat
}

$services = @(
    "auth-service",
    "trip-service",
    "billing-service",
    "payment-service"
)
if (-not $NoGateway) {
    $services += "api-gateway"
}

foreach ($svc in $services) {
    $cmd = "cd `"$repoRoot`"; & `"$gradleCmd`" :$svc:bootRun"
    Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-Command", $cmd -WorkingDirectory $repoRoot
}

Write-Host "Started services: $($services -join ', ')"
Write-Host "Close the opened PowerShell windows to stop them."
