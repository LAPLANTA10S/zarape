function login() {
    const usuario = document.getElementById("usuario").value;
    const contrasena = document.getElementById("contrasena").value;

    if (!usuario || !contrasena) {
        Swal.fire({
            icon: "warning",
            title: "Campos incompletos",
            text: "Por favor, completa todos los campos."
        });
        return;
    }

    const data = new URLSearchParams();
    data.append('u', usuario);
    data.append('c', contrasena);

    const ruta = "http://localhost:8080/zarape/api/usuario/login";  

    fetch(ruta, {
        method: "POST",
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded;charset=UTF-8'
        },
        body: data.toString()
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Error de red o servidor.');
        }
        return response.json();
    })
    .then(data => {
        if (data && data.idUsuario && data.activo === 1) {
            localStorage.setItem('usuario', usuario);
            localStorage.setItem('idUsuario', data.idUsuario);
            localStorage.setItem('nombre', data.nombre);
            localStorage.setItem('activo', data.activo);
            localStorage.setItem('rol', data.rol); // Guardamos el rol en localStorage
            localStorage.setItem('inicioSesion', 'true');

            Swal.fire({
                title: "¡BIENVENIDO " + data.nombre + "!",
                text: "Inicio de sesión exitoso.",
                icon: "success",
                showConfirmButton: true
            }).then(() => {
                if (data.rol === 1) {
                    loginToken();
                    location.href = "principal.html";  
                } else if (data.rol === 2) {
                    location.href = "menu.html";  
                } else {
                    Swal.fire("ERROR", "Rol no reconocido.", "error");
                }
            });
        } else {
            Swal.fire("ERROR", "¡CREDENCIALES INCORRECTAS O USUARIO INACTIVO!", "error");
        }
    })
    .catch(error => {
        console.error('Error:', error);
        Swal.fire("Error", "Error de red o servidor.", "error");
    });
}

async function loginToken() {
    const usuario = document.getElementById('usuario').value;
    const contrasena = document.getElementById('contrasena').value;

    // Guardamos en localStorage
    localStorage.setItem('nombreUsuario', usuario);

    const url = new URL('http://localhost:8080/zarape/api/usuario/cheecky');
    url.search = new URLSearchParams({
        'nombreU': usuario,
        'contrasena': contrasena
    });

    const response = await fetch(url, {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' }
    });
}

document.addEventListener("DOMContentLoaded", async function () {
    const usuarioGuardado = localStorage.getItem('nombreUsuario');

    if (usuarioGuardado) {
        const url = new URL('http://localhost:8080/zarape/api/usuario/verificarToken');
        url.searchParams.append('nombreU', usuarioGuardado);

        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: { 'Content-Type': 'application/json' }
            });

            const data = await response.json();

            if (data.tokenValido) {
                localStorage.setItem('token', data.tokenValido);
                location.href = "principal.html"; // Redirige si el token es válido
            }else{
                
            }
        } catch (error) {
            console.error("Error verificando el token:", error);
        }
    }
});