# Proyecto AREP
Este proyecto ofrece un servidor web que responde con recursos tanto dinámicos como estáticos


## Prerrequisitos
Es necesario tener instalado Java 1.8 y Apache Maven 3.6.0


## Ejecutar
### Descagar dependencias y compilar
```
mvn package
```
### Documentación
```
mvn javadoc:jar
```
La documentación generada por el comando anterior se encuentra en la ruta **target\apidocs**
### Ejecutar
```
java -cp target/classes:target/dependency/* edu.escuelaing.arem.project.Controller
```
### Despliegue
arem-project.herokuapp.com
#### Recursos Estáticos
```
https://arem-project.herokuapp.com/prueba.html
https://arem-project.herokuapp.com/git.png
```
#### Recursos Dinámicos
```
https://arem-project.herokuapp.com/app/prueba
https://arem-project.herokuapp.com/app/suma?num=1&num2=50
```

## Documento
Para más detalle del funcionamiento del proyecto ver el [documento](https://github.com/Santiago-Rocha/arem-project/blob/master/Articulo.pdf) adjunto

## Autor 
Santiago Rocha Durán - Escuela Colombiana de Ingenieria Julio Garavito
## Licencia
Este proyecto está licenciado bajo GNU General Public License - ver [LICENSE](https://github.com/Santiago-Rocha/arem-project/blob/master/LICENSE.txt) para más detalles 


