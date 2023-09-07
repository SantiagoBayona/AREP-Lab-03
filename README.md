# TALLER 3: MICROFRAMEWORKS WEB

Servidor web para soportar una funcionalidad similar a la de Spark.

### Prerrequisitos
- Java
- Maven

### Instalación

1. Clonar el repositorio

```
git clone https://github.com/SantiagoBayona/AREP-Lab-03
```

2. Dentro del directorio del proyecto lo construimos

```
mvn package
```

## Ejecución

1. Corremos el servidor

```
mvn exec:java -"Dexec.mainClass"="edu.escuelaing.HttpServer"
```

2. Ingresamos a la página mediante esta URL en un navegador

```
https://localhost:35000
```

## Pruebas

Para probar el servidor debemos solicitarle alguno de los recursos

![src/main/resources/img/](readme/Carpeta.png)

```
https://localhost:35000/imgg.png
```

Al hacerlo vemos que el recurso solicitado carga en el navegador

![src/main/resources/img/](readme/Prueba.png)

![src/main/resources/img/](readme/index.png)


## Construido con

* Java
* Maven
* Git
