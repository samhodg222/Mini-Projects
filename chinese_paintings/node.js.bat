@echo off
for %%* in (.) do set projectName=%%~nx*
set loc=%CD%
cd /d C:\Program Files\nodejs
start nodevars.bat
