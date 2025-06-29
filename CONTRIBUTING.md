# Guía de contribución

## Preparación
Ejecuta antes de cada commit:

```bash
sbt scalafmtAll
sbt test
```

Esto asegura un formato uniforme y pruebas en verde.

## Revisión
Revisa tu diff buscando marcas de conflicto como `<<<<<<<` o `>>>>>>>` y resuélvelas antes de confirmar.

## Pull Request
1. Verifica que los comandos de preparación no generen errores.
2. Asegúrate de que no existen marcas de conflicto.
3. Actualiza tu rama con `main`.
4. Abre un PR limpio y describe brevemente tus cambios.
