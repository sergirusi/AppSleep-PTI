#!/bin/bash

echo "Introdueix el teu nom d'usuari"

read user

php peticio/pet.php ${user}

camin="false"
tim=0

while [ 1 ]; do
	camin=$(php peticio/petcam.php ${user})
    echo ${camin}
    if [[ $camin == "false" ]]; then
        sleep 20
        tim=0
    else
	l=10
        #python cam.py
        echo "cam on"
        per=`python motion_detector.py --video 1.mp4 | tail -n 1`
        echo ${per}
        php peticio/postgraf.php ${user} ${per}
        if (( $(echo "$per < $l" |bc -l) )); then
            tim=$((tim + 80))
        fi
        echo "done"
        camin=$(php peticio/petcam.php ${user})
         per=`python motion_detector.py --video 2.mp4 | tail -n 1`
        echo ${per}
        php peticio/postgraf.php ${user} ${per}
        if (( $(echo "$per < $l" |bc -l) )); then
            tim=$((tim + 60))
        fi
        echo "done"
        camin=$(php peticio/petcam.php ${user})
         per=`python motion_detector.py --video 3.mp4 | tail -n 1`
        echo ${per}
        php peticio/postgraf.php ${user} ${per}
        if (( $(echo "$per < $l" |bc -l) )); then
            tim=$((tim + 66))
        fi
        echo "done"
        camin=$(php peticio/petcam.php ${user})
        echo ${tim}
        php peticio/posttemps.php ${user} ${tim}
    fi
done













