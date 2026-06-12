$file = "C:\Users\sense\Desktop\DragonTweaksV2\.memsearch\memory\projects\dragontweaksv2\approved\memsearch-docker-milvus-setup.md"

$old = '## Reindex (correct collection)

```
memsearch index "C:/Users/sense/Desktop/DragonTweaksV2/.memsearch/memory/" --force -c ms_dragontweaksv2_4403422f
```'

$new = '## Reindex (correct collection)

Run the guarded refresh script. Do NOT index .memsearch/memory/ directly:

```
.\scripts\memsearch-refresh.ps1
```

WARNING: Never run memsearch index against .memsearch/memory/ or any path
containing candidates/, deprecated/, rejected/, or raw/. Those subtrees
contain unvalidated memory and will pollute the collection.'

$content = Get-Content $file -Raw -Encoding UTF8
if (-not $content.Contains($old)) {
    Write-Host "ERROR: expected text not found -- already applied or file changed." -ForegroundColor Red
    exit 1
}
$content = $content.Replace($old, $new)
[System.IO.File]::WriteAllText($file, $content, [System.Text.Encoding]::UTF8)
Write-Host "Patch 04 applied." -ForegroundColor Green
