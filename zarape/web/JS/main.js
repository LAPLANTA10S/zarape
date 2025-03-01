let controladorGral; // Variable para el controlador

function cargarInicio() {
    fetch('inicio.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById("maincontent").innerHTML = html;
        });
}

function cargarModuloSucursal() {
    fetch('modulos/moduloSucursal/sucursal.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById("maincontent").innerHTML = html;
            import('../modulos/moduloSucursal/controladorSucursal.js').then(
                function (controller) {
                controladorGral = controller; // Asigna el módulo importado
            });
        });
}

function cargarModuloEmpleado() {
    fetch('modulos/moduloEmpleado/empleado.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById("maincontent").innerHTML = html;
            // Importa el primer módulo
            import('../modulos/moduloEmpleado/controladorEmpleado.js').then(
                function (controller) {
                controladorGral = controller; // Asigna el primer módulo importado         
            });
        });
}

function cargarModuloCliente() {
    fetch('modulos/moduloCliente/cliente.html?v=123')
        .then(response => response.text())
        .then(html => {
            document.getElementById("maincontent").innerHTML = html;
            import('../modulos/moduloCliente/controladorCliente.js?v=123').then(module => {
                controladorGral = module; // Asigna el módulo importado
            });
        });
}
        
function cargarModuloPlatillo() {
    fetch('modulos/moduloPlatillo/platillo.html')
        .then(response => response.text())
        .then(html => {
            document.getElementById("maincontent").innerHTML = html;
            // Importa el controlador del módulo de platillos
            import('../modulos/moduloPlatillo/controladorPlatillo.js').then(
                function (controller) {
                    controladorGral = controller; // Asigna el módulo importado
                });
        });
}

async function CerrarSesion() {
    try {
        // Obtenemos el usuario desde localStorage
        const nombreUsuario = localStorage.getItem('nombreUsuario');

        if (!nombreUsuario) {
            console.error('No se encontró el nombre de usuario');
            return;
        }

        const url = new URL('http://localhost:8080/zarape/api/usuario/logout');
        url.search = new URLSearchParams({ 'nombreU': nombreUsuario });

        const response = await fetch(url, {
            method: 'GET',
            headers: { 'Content-Type': 'application/json' }
        });

        if (response.ok) {
            const result = await response.json();
            if (result.message) {
                console.log(result.message); // "Sesión cerrada correctamente"
            } else if (result.error) {
                console.error(result.error); // "Usuario no encontrado..."
            }
        } else {
            console.error('Error al comunicarse con el servidor');
        }

        // Eliminamos el usuario del localStorage al cerrar sesión
//        localStorage.removeItem('nombreUsuario');

    } catch (error) {
        console.error('Error:', error);
    }
}

let usuario = localStorage.getItem("nombreUsuario");
//console.log(localStorage.getItem("token"));
