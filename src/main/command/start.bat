REM start.bat '-b -r -m "c:\master\path" -c "c:\copy\1" "c:\copy\2" "c:\copy\n" -a "c:\archive"'
${java.home}\bin\java -cp lib\* -p modules -m ${project.module}/${project.main.class} %1