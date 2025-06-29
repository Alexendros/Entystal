# Protocolo para Pull Requests

Sigue estos pasos para minimizar conflictos al enviar tus cambios:

1. **Crea una rama** a partir de `main` con un nombre descriptivo.
2. **Sincroniza frecuentemente** ejecutando:
   ```bash
   git pull --rebase origin main
   ```
3. Antes de abrir el PR, ejecuta localmente:
   ```bash
   sbt scalafmtAll
   sbt test
   ```
4. Abre el PR y verifica que la acci\u00f3n de CI pase. Si falla el paso *Comprobar conflictos de fusi\u00f3n*, realiza un `git rebase origin/main` para resolverlos y vuelve a subir tu rama.
5. Una vez aprobado, el PR se fusionar\u00e1 usando *squash* o *rebase* para mantener un historial limpio.

Este flujo ayuda a mantener un repositorio ordenado y evita la mayor\u00eda de conflictos.
