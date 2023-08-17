#!/bin/sh
# ./start.sh '-b -r -m "/master/path" -c "/copy/1" "/copy/2" "/copy/n" -a "/archive"'
${java.home}/bin/java -cp 'lib/*' -p 'modules' -m ${project.module}/${project.main.class} $1