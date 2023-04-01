@echo off

for /f "usebackq delims=" %%i in ("qiita_urls.txt") do (
    curl -sS "%%i.md" > "%%~ni.md"
)
