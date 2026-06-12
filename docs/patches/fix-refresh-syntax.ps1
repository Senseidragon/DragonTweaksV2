$file = "C:\Users\sense\Desktop\DragonTweaksV2\scripts\memsearch-refresh.ps1"

$old = '    "$memoryDir\domains\minecraft\approved"`
    "$memoryDir\projects\dragontweaksv2\approved"
    --force -c $collection'

$new = '    "$memoryDir\domains\minecraft\approved" `
    "$memoryDir\projects\dragontweaksv2\approved" `
    --force -c $collection'

$content = Get-Content $file -Raw -Encoding UTF8
if (-not $content.Contains($old)) {
    Write-Host "ERROR: expected text not found -- already fixed or file differs." -ForegroundColor Red
    exit 1
}
$content = $content.Replace($old, $new)
[System.IO.File]::WriteAllText($file, $content, [System.Text.Encoding]::UTF8)
Write-Host "Syntax fixed." -ForegroundColor Green
