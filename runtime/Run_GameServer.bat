@echo off
title GameServer Run
cd /d %~dp0
java -Xms512m -Xmx1024m -cp GameServer.jar com.lineage.Server > server_output.log 2>&1
echo Server exited with code %errorlevel%
