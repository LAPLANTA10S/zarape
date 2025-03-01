function iniciarPedido() {
    
    let ticketData = {
        idTicket: -1,
        ticket: "S",
        fecha: new Date().toISOString().split("T")[0],  // Fecha actual en formato YYYY-MM-DD
        pagado: "N",
        cliente: {
            idCliente: 1  // Cambia por el ID real del cliente
        },
        sucursal: {
            idSucursal: 1  // Cambia por el ID real de la sucursal
        }
    };

    fetch("http://localhost:8080/zarape/api/comanda/agregarTicket", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "datosTicket=" + encodeURIComponent(JSON.stringify(ticketData))
    })
    .then(response => response.json())
    .then(data => {
        if (data.idTicket) {
            // Guardar el idTicket en el localStorage
            localStorage.setItem("idTicket", data.idTicket);

            Swal.fire({
                icon: "success",
                title: "Pedido iniciado",
                text: "El pedido se ha registrado con éxito.",
                confirmButtonText: "Aceptar"
            }).then(() => {
                window.location.href = "menu.html";  // Redirigir después de éxito
            });
        } else {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: "No se pudo iniciar el pedido.",
                confirmButtonText: "Aceptar"
            });
        }
    })
    .catch(error => {
        console.error("Error al enviar la petición:", error);
        Swal.fire({
            icon: "error",
            title: "Error de conexión",
            text: "No se pudo conectar con el servidor.",
            confirmButtonText: "Aceptar"
        });
    });
}

