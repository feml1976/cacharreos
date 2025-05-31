Clonar en tu computadora
# Navega a donde quieres crear el directorio  
cd /ruta/donde/quieres/el/proyecto  

# Clona el repositorio  
git clone https://github.com/tu-usuario/nombre-del-repo.git  

# Entra al directorio  
cd nombre-del-repo  

Opción 2: Crear directorio local primero
1. Crear directorio local:
# Crear y entrar al directorio  
mkdir mi-proyecto  
cd mi-proyecto  

# Inicializar Git  
git init  

# Crear un archivo inicial (opcional)  
echo "# Mi Proyecto" > README.md  

# Hacer el primer commit  
git add .  
git commit -m "Primer commit"  

2. Conectar con GitHub:
# Agregar el repositorio remoto (crea primero el repo en GitHub)  
git remote add origin https://github.com/feml1976/cacharreos.git

# Subir cambios  
git branch -M main  
git push -u origin main  
Comandos básicos para mantenerlo actualizado:
# Ver estado de archivos  
git status  

# Agregar archivos modificados  
git add .  
# o agregar archivo específico  
git add nombre-archivo.txt  

# Hacer commit  
git commit -m "Descripción de los cambios"  

# Subir cambios a GitHub  
git push  

# Descargar cambios desde GitHub  
git pull  