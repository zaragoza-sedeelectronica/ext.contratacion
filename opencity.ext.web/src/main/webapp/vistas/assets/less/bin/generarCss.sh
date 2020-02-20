#!/bin/bash
#Anyadir en bashrc
export CONTPRE=/home/ob544e/Documentos/cont
#export CONTPRO=/RedAyto/J/cont
DESTINO="$CONTPRE"
if [ "$1" = "pro" ]
	then
		DESTINO="${CONTPRO}"
		echo "\nGenerando CSS para PRO" $DESTINO
	else
		echo "\nGenerando CSS para PRE" $DESTINO
fi
#lessc --yui-compress ../bootstrap.less $DESTINO"/plantillas/bs/css/main.css"
lessc -x ../bootstrap-2.3.2-gen.less $DESTINO"/assets/css/main-ciudad.min.css"
lessc -x ../bootstrap-3.3.7-gen.less $DESTINO"/assets/css/main-sede.min.css"
lessc -x ../bootstrapTurismo.less $DESTINO"/assets/css/turismo.min.css"
lessc -x ../bootstrapEtopia.less $DESTINO"/assets/css/etopia.min.css"
FILESIZE_MAIN_CIUDAD_BS2=$(stat -c%s $DESTINO"/plantillas/bs/css/main.css")
FILESIZE_MAIN_SEDE=$(stat -c%s $DESTINO"/plantillas/sede/css/main.min.css")
FILESIZE_TURISMO=$(stat -c%s $DESTINO"/plantillas/bs/css/turismo.css")
FILESIZE_ETOPIA=$(stat -c%s $DESTINO"/plantillas/bs/css/etopia.css")
echo "\nLESS compilado correctamente. (`date +"%d/%m/%Y %H:%M"`)"
echo "----------------------------------------------------------"
echo "Tamaño de /assets/css/main-ciudad.min.css = $FILESIZE_MAIN_CIUDAD_BS2 bytes."
echo "Tamaño de /assets/css/main-sede.min.css = $FILESIZE_MAIN_SEDE bytes."
echo "Tamaño de /assets/css/turismo.min.css = $FILESIZE_TURISMO bytes."
echo "Tamaño de /assets/css/etopia.min.css = $FILESIZE_ETOPIA bytes.\n"
