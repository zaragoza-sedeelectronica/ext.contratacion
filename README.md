<p align="right"><img width=50% src="https://github.com/TBFY/general/blob/master/figures/tbfy-logo.png"></p>
<p align="right"><img width=40% src="https://github.com/TBFY/edp-economic-data-portal/blob/master/logo.png"></p>

### OpenCity-Ext - Contratación Pública ###

# Introducción

A continuación se indican las instrucciones necesarias para la descarga y configuración del módulo de "Contratación Pública" del proyecto OpenCity-Ext desarrollado por el Ayuntamiento de Zaragoza.

Los servicios desarrollados en este módulo han sido desarrollados bajo el proyecto europeo <a href="https://www.zaragoza.es/sede/portal/theybuyforyou/">They Buy For You</a>

## Manual de instalación ##

Primero, hay que realizar los siguientes pasos del manual general:

* instalar y comprobar versiones software
* configurar la base de datos
* clonar repositorios
* instalación de librerías en repositorio local
* configuración del módulo
* configuración servidor
* prueba del módulo
* otras configuraciones

# Instalar y comprobar versiones software

Las versiones mínimas del software son:

* Java 1.8
* Maven >= 3.0.5
* Oracle 11
* Apache Server (2.2 versión usada en las pruebas)
* Eclipse 2019-03 (recomendada)

Una vez instalado Eclipse, hay que instalar  maven integration for eclipse desde la siguiente url http://download.eclipse.org/releases/indigo/ y seleccionando la opción "Generarl Purpose -> m2e"

# Configurar la base de datos

El sistema de contratación pública utiliza un usuario general para las bases de datos. Este ususario permite la gestión de los datos de usuarios de la plataforma y los datos de contratación pública.

Por lo tanto, hay que configurar una única bases de datos para que el modulo funcione correctamente. Para eso hay que realizar los siguientes pasos:

* generar el usuario en Oracle
* ejecutar los scripts de generación de las bases de datos
* configurar la base de datos en el proyecto opencity.ext.web

A continuación se detallan los pasos a seguir en cada uno de ellos.

## Generar el usuario Oracle

Como se indica en el apartado anterior, hay que generar un usuario para la base de datos. El usuario se debe generar en Oracle ya que el sistema de contratación pública utiliza Oracle.

Se aconseja que el nombre del usuario sea "general" ya que es el que se utiliza en el core de la aplicación. A continuación se muestran los comandos que deben utilizarse en la consola de SQL*Plus, si se utiliza dicha consola para realizar este paso.

Para generar un usuario, se utilizarían los comandos:
```
CREATE USER general IDENTIFIED BY "password";
```

Con este paso, ya estaría el usuario creado.

## Ejecutar los scripts de generación de las bases de datos

El siguiente paso es crear las tablas y cargar los datos de prueba disponibles para este módulo. Los scripts que se van a importar están disponibles en la carpeta [scripts-bbdd/](scripts-bbdd/).

El orden de ejecución es el siguiente:

* 0.1.db-admin.sql
* 0.2.db-user.sql
* 1.ddl.sql
* 2.data.sql
* 3.restricciones-indices.sql
* 4.secuencias.sql
* 5.vistas.sql
* 6.organigrama.sql
	
Una vez realizados estos pasos, ya estaría lista la base de datos en Oracle. 

## Configurar la base de datos en el proyecto opencity.ext.web

Los datos de conexión a la BBDD se define en `opencity.ext.web/src/main/resources/META-INF/context.xml` (es necesario copiar `opencity.ext.web/src/main/resources/META-INF/context.xmltemplate` como `opencity.ext.web/src/main/resources/META-INF/context.xml` para modificarlo):

```
<Resource name="jdbc/WebGeneralDS" auth="Container"
              type="javax.sql.DataSource" driverClassName="oracle.jdbc.OracleDriver"
              url="jdbc:oracle:thin:@localhost:1111/XXXX"
              username="general" password="password" maxActive="20" maxIdle="10"
              maxWait="-1"/>
```

Hay que indicar los valores de los campos `url` y `password`.

# Clonar repositorio:

A continuación debe clonarse en local este repositorio https://github.com/zaragoza-sedeelectronica/ext.contratacion


# Instalación de librerías en repositorio local:

Existen librerías que no están disponibles en repositorios maven, son las que se encuentran en la carpeta [librerias](librerias/) y se deben instalar en el repositorio maven de local ejecutando los siguientes comandos:

```
$ mvn install:install-file -DgroupId=org.zaragoza -DartifactId=opencity.ext.core -Dversion=0.0.1 -Dpackaging=jar -Dfile=librerias/opencity.ext.core-0.0.1.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=org.zaragoza -DartifactId=opencity.ext.core.test -Dversion=0.0.1 -Dpackaging=jar -Dfile=librerias/opencity.ext.core.test-0.0.1.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc5 -Dversion=11.2.0 -Dpackaging=jar -Dfile=librerias/ojdbc5.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=virtuoso.jena.driver -DartifactId=virtjdbc -Dversion=3 -Dpackaging=jar -Dfile=librerias/virtjdbc.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=virtuoso.jena -DartifactId=virt_jena -Dversion=3 -Dpackaging=jar -Dfile=librerias/virt_jena.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=geoapi -Dversion=2.0 -Dpackaging=jar -Dfile=librerias/geoapi.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=geoapi-nogenerics -Dversion=2.1-M2 -Dpackaging=jar -Dfile=librerias/geoapi-nogenerics.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=deegree2-pre -Dversion=2 -Dpackaging=jar -Dfile=librerias/deegree2-pre.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=gsl-coordinate-transformation -Dversion=1.0-jdk15 -Dpackaging=jar -Dfile=librerias/gsl-coordinate-transformation.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=gt2-epsg-wkt -Dversion=2.3.5 -Dpackaging=jar -Dfile=librerias/gt2-epsg-wkt.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=gt2-referencing -Dversion=2.3.0 -Dpackaging=jar -Dfile=librerias/gt2-referencing.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=iaaa_csct -Dversion=1.6.2 -Dpackaging=jar -Dfile=librerias/iaaa_csct.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=j3dcore -Dversion=1.3.0 -Dpackaging=jar -Dfile=librerias/j3dcore.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=j3dutils -Dversion=1.3.0 -Dpackaging=jar -Dfile=librerias/j3dutils.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=jai_codec -Dversion=1.1.1_01 -Dpackaging=jar -Dfile=librerias/jai_codec.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=jai_core -Dversion=1.1.1_01 -Dpackaging=jar -Dfile=librerias/jai_core.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=jGridShiftApi -Dversion=2.0 -Dpackaging=jar -Dfile=librerias/jGridShiftApi.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=jsr108 -Dversion=0.01 -Dpackaging=jar -Dfile=librerias/jsr108.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=jts -Dversion=1.6 -Dpackaging=jar -Dfile=librerias/jts.jar -DgeneratePom=true

$ mvn install:install-file -DgroupId=idezar -DartifactId=vecmath -Dversion=1.3.1 -Dpackaging=jar -Dfile=librerias/vecmath.jar -DgeneratePom=true
```

# Configuración del módulo

Lo primero sería realizar, desde una línea de comandos, la siguiente instrucción:

```
$ mvn clean install
```

dentro de los proyectos `opencity.ext.organigrama` y `opencity.ext.contratacion` en este orden ya que `opencity.ext.contratacion` depende de `opencity.ext.organigrama` para su compilación.

Una vez realizado el paso anterior, hay que configurar la ubicación de las vistas del proyecto. Este configuración se define en `opencity.ext.web/src/main/resources/application.properties` (es necesario copiar `opencity.ext.web/src/main/resources/application.properties.template` como `opencity.ext.web/src/main/resources/application.properties` para modificarlo):

```
thymeleaf.view=<path-opencity.ext.web>/src/main/webapp/vistas/
path.i18n=<path-opencity.ext.web>/src/main/webapp/i18n/messages
datasource.prefix=java:/comp/env/
```

Hay que sustituir el valor del campo "<path-opencity.ext.web>" por la localización completa del proyecto en el sistema de ficheros. Por ejemplo, si el proyecto está en "/home/contratacion/opencity.ext.web", el fichero tendría los siguientes valores:

```
thymeleaf.view=/home/contratacion/opencity.ext.web/src/main/webapp/vistas/
path.i18n=/home/contratacion/opencity.ext.web/src/main/webapp/i18n/messages
datasource.prefix=java:/comp/env/
```

Esta definición se puede realizar para cada uno de los entornos diponibles modificando los ficheros en las carpetas `resources`, `resources-dev`, `resources-prod` y `resources-test` dentro del proyecto `opencity.ext.web`. Por defecto, sólo hay que realizarlo en la carpeta `resources`.

# Configuración servidor

Antes de poder realizar las pruebas, hay que configurar una serie de accesos. Por un lado:

* el acceso a la aplicación: por defecto, cuando se arranca el módulo, se lanza en http://localhost:8888/opencityext. Para un correcto funcionamiento, se recomienda que el acceso sea a través de http://localhost/opencityext
* el acceso a la carpeta cont: esta carpeta se encuentra en el repositorio y debe ser accesible como http://localhost/cont, sin número de puerto, para una visualización correcta de las vistas.

A continuación se indica como se ha realizado durante las pruebas de manera ilustrativa.

La carpeta `cont` se sirve mediante un servidor Apache Tomcat arrancado en el puerto 8080. Por lo tanto, todo el contenido de la carpeta está disponible en la dirección http://localhost:8080/cont

El módulo contratación pública, como se ha indicado, cuando se arranca está disponible en http://localhost:8888/opencity

Por lo tanto, para servir ambas direcciones sin el puerto, se configura el servidor Apache para que pueda servir contenidos estáticos y se configuraca un proxy de la siguiente manera:

```
ProxyPass /opencityext http://localhost:8888/opencityext
ProxyPassReverse /opencityext http://localhost:8888/opencityext

ProxyPass /cont http://localhost:8080/cont
ProxyPassReverse /cont http://localhost:8080/cont
```

Si el contenido del directorio `cont` no se sirve mediante un servidor sino como un contenido estático, hay que habilitar los modulos `proxy` y `http_proxy` en el servidor Apache e incluir en el fichero de configuración:
```
Alias /cont /<path>/cont
<Directory  <path>/cont>
    Options Indexes FollowSymLinks
    AllowOverride None
    Require all granted
</Directory>
```

# Prueba del módulo

Para lanzar el módulo, hay que ejecutar la siguiente instrucción dentro del proyecto `opencity.ext.web`:
```
$ mvn tomcat7:run
```

Para probar que funciona correctamente acceder a:
```
http://localhost/opencityext/servicio/contratacion-publica/
```

Existe una parte de administración del módulo a la que se puede acceder con el usuario "admin" y contraseña "prueba" a través del siguiente enlace:
```
http://localhost/opencityext/servicio/contratacion-publica/admin
```

# Otras configuraciones

Con los pasos indicados anteriormente, ya podría trabajar con el módulo sin problemas. No obstante, existentes más configuraciones que pueden realizarse, las cuales se indican a continuación.

## Trabajar con proxy
Configurar maven en $HOME/.m2/ crear el fichero settings.xml
```
<settings>
  <proxies>
    <proxy>
      <id>http_proxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>proxy.red.zaragoza.es</host>
      <port>8080</port>
    </proxy>
    <proxy>
      <id>https_proxy</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>proxy.red.zaragoza.es</host>
      <port>8080</port>
    </proxy>
 </proxies>
</settings>
```
$ mvn -Dmaven.tomcat.port=8888 -Dhttp.proxyHost=<host> -Dhttp.proxyPort=<port> -Dhttps.proxyHost=<host> -Dhttps.proxyPort=<port> tomcat7:run

## Modificar la caché
Por defecto se almacenan en caché todas las peticiones `GET` para evitarlo, se debe anotar el método del controlador que no se quiera almacenar en caché con la anotación `@NoCache`, la configuración de la caché se encuentra en el fichero `src/main/resources/ehcache.xml`.

Por otro lado se puede utilizar la caché propia de spring:
* `@Cache(Cache.DURACION_1MIN)` el elemento se cachea durante 1 minuto
* `@Cache(Cache.DURACION_5MIN)` el elemento se cachea durante 5 minutos
* `@Cache(Cache.DURACION_30MIN)` el elemento se cachea durante 30 minutos
* `@Cache(Cache.DURACION_1DIA)` el elemento se cachea durante 1 día 
