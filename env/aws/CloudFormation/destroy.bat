@echo off
setlocal

cd > tmpfile
set /p CURRENT_PATH= < tmpfile
del tmpfile
python %CURRENT_PATH%\\destroy.py

endlocal
