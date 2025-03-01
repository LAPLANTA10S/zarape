// Variables globales
let bebidas = [];
let alimentos = [];
let pedido = [];
let idTicket = localStorage.getItem("idTicket") || null;
console.log("el ticket es: " + idTicket);

// Cargar bebidas y platillos al iniciar la aplicación
document.addEventListener("DOMContentLoaded", async () => {
    try {
        const responseBebidas = await fetch('http://localhost:8080/zarape/api/bebida/getAllBebida');
        const dataBebidas = await responseBebidas.json();
        bebidas = dataBebidas;
        loadBebidas();
    } catch (error) {
        console.error('Error al cargar bebidas:', error);
        showNotification("Error al cargar el menú de bebidas", "error");
    }

    try {
        const responsePlatillos = await fetch('http://localhost:8080/zarape/api/platillo/getAllAlimentos');
        const dataPlatillos = await responsePlatillos.json();
        alimentos = dataPlatillos;
        loadPlatillos();
    } catch (error) {
        console.error('Error al cargar alimentos:', error);
        showNotification("Error al cargar el menú de platillos", "error");
    }
});

// Renderiza las bebidas en el contenedor del menú
function loadBebidas() {
    const contenedor = document.getElementById("productos");
    if (contenedor) {
        contenedor.innerHTML = bebidas.map(bebida => `
            <div class="bebida-card" onclick="agregarBebida(${bebida.idBebida})">
                ${bebida.producto.foto 
                    ? `<img src="data:image/jpeg;base64,${bebida.producto.foto}" 
                        alt="${bebida.producto.nombre}" class="bebida-img">` 
                    : ''
                }
                <h3>${bebida.producto.nombre}</h3>
                <p class="bebida-descripcion">${bebida.producto.descripcion}</p>
                <p>$${bebida.producto.precio.toFixed(2)}</p>
            </div>
        `).join('');
    }
}

// Renderiza los platillos en el contenedor del menú
function loadPlatillos() {
    const contenedor = document.getElementById("productos");
    if (contenedor) {
        contenedor.innerHTML += alimentos.map(platillo => `
            <div class="alimento-card" onclick="agregarPlatillo(${platillo.idAlimento})">
                ${platillo.producto.foto 
                    ? `<img src="data:image/jpeg;base64,${platillo.producto.foto}" 
                        alt="${platillo.producto.nombre}" class="alimento-img">` 
                    : ''
                }
                <h3>${platillo.producto.nombre}</h3>
                <p class="alimento-descripcion">${platillo.producto.descripcion}</p>
                <p>$${platillo.producto.precio.toFixed(2)}</p>
            </div>
        `).join('');
    }
}

// Agrega una bebida al pedido o incrementa su cantidad si ya existe
function agregarBebida(idBebida) {
    const bebidaExistente = pedido.find(p => p.idBebida === idBebida);
    const bebida = bebidas.find(b => b.idBebida === idBebida);
    
    if (bebidaExistente) {
        bebidaExistente.cantidad++;
    } else {
        pedido.push({ ...bebida, cantidad: 1, tipo: 'bebida' });
    }
    actualizarPedido();
}

// Agrega un platillo al pedido o incrementa su cantidad si ya existe
function agregarPlatillo(idAlimento) {
    const platilloExistente = pedido.find(p => p.idAlimento === idAlimento);
    const platillo = alimentos.find(a => a.idAlimento === idAlimento);
    
    if (platilloExistente) {
        platilloExistente.cantidad++;
    } else {
        pedido.push({ ...platillo, cantidad: 1, tipo: 'platillo' });
    }
    actualizarPedido();
}

// Modifica la cantidad de un producto del pedido
function modificarCantidad(idProducto, cambio, tipo) {
    let index;
    if (tipo === 'bebida') {
        index = pedido.findIndex(p => p.idBebida === idProducto && p.tipo === tipo);
    } else if (tipo === 'platillo') {
        index = pedido.findIndex(p => p.idAlimento === idProducto && p.tipo === tipo);
    }

    if (index !== -1) {
        pedido[index].cantidad += cambio;
        if (pedido[index].cantidad < 1) {
            pedido.splice(index, 1);
        }
        actualizarPedido();
    }
}

// Actualiza la interfaz del pedido y recalcula el total
function actualizarPedido() {
    const listaPedido = document.getElementById("lista-pedido");
    if (listaPedido) {
        const subtotal = pedido.reduce((sum, item) => sum + (item.producto.precio * item.cantidad), 0);
        const iva = subtotal * 0.16; // 16% de IVA
        const total = subtotal + iva;

        listaPedido.innerHTML = `
            ${pedido.map(item => `
                <div class="ticket-item">
                    <p>${item.producto.nombre+" "}${(" $"+item.producto.precio+" ")}</p>
                    <div class="cantidad-container">
                        <button class="btn-menos" onclick="modificarCantidad(${item.producto.idProducto}, -1, '${item.tipo}')">-</button>
                        <span>${item.cantidad}</span>
                        <button class="btn-mas" onclick="modificarCantidad(${item.producto.idProducto}, 1, '${item.tipo}')">+</button>
                    </div>
                    <p>$${(item.cantidad * item.producto.precio)}</p>
                </div>
            `).join('')}
            <h3>Subtotal: $${subtotal.toFixed(2)}</h3>
            <h3>IVA (16%): $${iva.toFixed(2)}</h3>
            <h3>Total: $${total.toFixed(2)}</h3>
        `;
    }
}


// Muestra el modal de pago si hay productos en el pedido
function enviarPedido() {
    if (pedido.length === 0) {
        alert("No hay productos en el pedido.");
        return;
    }
    const modal = document.getElementById('cardModal');
    if (modal) modal.style.display = 'flex';
}

// Función para validar datos de tarjeta
function validarTarjeta(data) {
    const errores = [];

    // Limpiar y preparar los campos
    const titular = data.titular ? data.titular.trim() : "";
    const numero = data.numero ? data.numero.replace(/\D/g, '') : "";
    const mm = parseInt(data.mm, 10);
    const yy = parseInt(data.yy, 10);

    // Validar nombre del titular
    if (!/^[A-Za-zÁÉÍÓÚáéíóúñÑ ]{3,}$/.test(titular)) {
        errores.push("Nombre del titular inválido");
    }

    // Validar número de tarjeta
    if (!/^\d{16}$/.test(numero) || !luhnCheck(numero)) {
        errores.push("Número de tarjeta inválido");
    }

    // Validar fecha de expiración (MM/YY)
    if (isNaN(mm) || mm < 1 || mm > 12) {
        errores.push("Mes de expiración inválido");
    } else {
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear() % 100;
        const currentMonth = currentDate.getMonth() + 1;
        if (yy < currentYear || (yy === currentYear && mm < currentMonth)) {
            errores.push("Tarjeta expirada");
        }
    }

    // Validar CVV
    if (!/^\d{3,4}$/.test(data.cvv ? data.cvv.trim() : "")) {
        errores.push("CVV inválido");
    }

    if (errores.length > 0) {
        showNotification(errores.join('\n'), 'error');
        return false;
    }
    return true;
}

// Implementación clara del algoritmo Luhn para validar números de tarjeta
function luhnCheck(numero) {
    let suma = 0;
    let duplicar = false;
    // Recorrer los dígitos de derecha a izquierda
    for (let i = numero.length - 1; i >= 0; i--) {
        let digito = parseInt(numero[i], 10);
        if (duplicar) {
            digito *= 2;
            if (digito > 9) digito -= 9;
        }
        suma += digito;
        duplicar = !duplicar;
    }
    return suma % 10 === 0;
}

// Simula el proceso de pago y muestra la confirmación
async function procesarPago(datosTarjeta) {
    try {
        
        /// Procedimiento agregarDetalleTicket
        if (!idTicket) {
            alert("No se ha encontrado un ticket activo.");
            return;
        }

        let detalles = pedido.map(item => ({
            ticket: { idTicket: parseInt(idTicket) },
            producto: { idProducto: item.producto.idProducto },
            cantidad: item.cantidad,
            precio: item.producto.precio * item.cantidad
        }));

        detalles.forEach(detalle => {
            fetch('http://localhost:8080/zarape/api/comanda/agregarDetalleTicket', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `datosDetalle=${encodeURIComponent(JSON.stringify(detalle))}`
            })
            .then(response => response.json())
            .then(data => {
                console.log("Detalle agregado:", data);
            })
            .catch(error => {
                console.error("Error al agregar detalle:", error);
            });
        });

        // Procedimiento para agregar la comanda
        const comanda = {
            ticket: { idTicket: parseInt(idTicket) },
            activo: 1  // Usamos los detalles previamente procesados
        };

        fetch('http://localhost:8080/zarape/api/comanda/agregarComanda', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `datosComanda=${encodeURIComponent(JSON.stringify(comanda))}`
        })
        .then(response => response.json())
        .then(data => {
            console.log("Comanda agregada:", data);
        })
        .catch(error => {
            console.error("Error al agregar comanda:", error);
        });

        // Procedimiento para actualizar el ticket
        const ticket = {
            idTicket: parseInt(idTicket),  // Asegúrate de pasar el idTicket correcto
            ticket: "S",
            fecha: new Date().toISOString().split("T")[0],  // Fecha actual en formato YYYY-MM-DD
            pagado: "S",
            cliente: {
                idCliente: 1  // Cambia por el ID real del cliente
            },
            sucursal: {
                idSucursal: 1  // Cambia por el ID real de la sucursal
            },
            activo: 2
        };

        fetch('http://localhost:8080/zarape/api/comanda/actualizarTicket', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `datosTicket=${encodeURIComponent(JSON.stringify(ticket))}`
        })
        .then(response => response.json())
        .then(data => {
            console.log("Ticket actualizado:", data);
        })
        .catch(error => {
            console.error("Error al actualizar ticket:", error);
        });

        showNotification("Procesando pago...", 'info');
        
        // Simular espera de pago (por ejemplo, llamada a API de pago)
        await new Promise(resolve => setTimeout(resolve, 2000));
        
        // Mostrar mensaje de éxito con SweetAlert
        Swal.fire({
            title: "¡Pago realizado, " + datosTarjeta.titular + "!",
            text: "Pago procesado exitosamente.",
            icon: "success",
            showConfirmButton: true
        });
        
        // Limpiar datos y UI
        pedido = [];
        actualizarPedido();
        resetUI();
        
        // Tiempo de espera para la creacion de un nuevo ticket
        await new Promise(resolve => setTimeout(resolve, 1000));
        iniciarNuevoPedido();
        console.log("el ticket es: "+ idTicket);
        
    } catch (error) {
        showNotification("Error en el pago: " + error.message, 'error');
    }
}


// Muestra notificaciones en pantalla
function showNotification(mensaje, tipo = 'success') {
    const notificacion = document.createElement('div');
    notificacion.className = `notification ${tipo}`;
    notificacion.textContent = mensaje;
    
    document.body.appendChild(notificacion);
    setTimeout(() => notificacion.remove(), 3000);
}

// Muestra la superposición de confirmación del pago
function showConfirmation() {
    const confirmation = document.getElementById("confirmationOverlay");
    if (confirmation) {
        confirmation.style.display = "flex";
        confirmation.querySelector('.confirmation-content').classList.add('show');
    }
}

// Oculta la superposición de confirmación
function hideConfirmation() {
    const confirmation = document.getElementById("confirmationOverlay");
    if (confirmation) {
        confirmation.style.display = "none";
        confirmation.querySelector('.confirmation-content').classList.remove('show');
    }
}

// Reinicia la interfaz: limpia el pedido, actualiza la lista y restablece las tarjetas de productos
function resetUI() {
    pedido = [];
    actualizarPedido();
    
    const cards = document.querySelectorAll('.bebida-card');
    if (cards) {
        cards.forEach(card => {
            card.style.opacity = '1';
            card.style.pointerEvents = 'auto';
        });
    }
    
    const listaPedido = document.getElementById("lista-pedido");
    if (listaPedido) {
        listaPedido.innerHTML = `
            <div class="empty-state">
                <p>No hay productos en el pedido</p>
            </div>
        `;
    }
}

// Manejo del envío del formulario de pago
document.getElementById('cardForm')?.addEventListener('submit', e => {
    e.preventDefault();
    
    const formData = {
        titular: e.target.titular.value,
        numero: e.target.numero.value,
        mm: e.target.mm.value,
        yy: e.target.yy.value,
        cvv: e.target.cvv.value
    };
    
    if (validarTarjeta(formData)) {
        procesarPago(formData);
        e.target.reset();
        closeModal();
    }
});

function cancelarPedido() {
  pedido = [];
  actualizarPedido();
  resetUI();
  showNotification("Pedido cancelado", "info");
  
  // Procedimiento para agregar la comanda
        const comanda = {
            ticket: { idTicket: parseInt(idTicket) },
            activo: 0  // Usamos los detalles previamente procesados
        };

        fetch('http://localhost:8080/zarape/api/comanda/agregarComanda', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `datosComanda=${encodeURIComponent(JSON.stringify(comanda))}`
        })
        .then(response => response.json())
        .then(data => {
            console.log("Comanda agregada:", data);
        })
        .catch(error => {
            console.error("Error al agregar comanda:", error);
        });

        // Procedimiento para actualizar el ticket
        const ticket = {
            idTicket: parseInt(idTicket),  // Asegúrate de pasar el idTicket correcto
            ticket: "S",
            fecha: new Date().toISOString().split("T")[0],  // Fecha actual en formato YYYY-MM-DD
            pagado: "N",
            cliente: {
                idCliente: 1  // Cambia por el ID real del cliente
            },
            sucursal: {
                idSucursal: 1  // Cambia por el ID real de la sucursal
            },
            activo: 0
        };

        fetch('http://localhost:8080/zarape/api/comanda/actualizarTicket', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: `datosTicket=${encodeURIComponent(JSON.stringify(ticket))}`
        })
        .then(response => response.json())
        .then(data => {
            console.log("Ticket actualizado:", data);
        })
        .catch(error => {
            console.error("Error al actualizar ticket:", error);
        });
        
        iniciarNuevoPedido();
        console.log("el ticket es: "+ idTicket);
}

// Cierra el modal de pago
function closeModal() {
    const modal = document.getElementById("cardModal");
    if (modal) modal.style.display = 'none';
}

function iniciarNuevoPedido() {
    let ticketData = {
        idTicket: -1,  // El ID del ticket inicial será -1 hasta que se genere uno nuevo
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

    // Realizamos la petición para crear un nuevo ticket
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
            // Guardamos el nuevo idTicket en el localStorage
            localStorage.setItem("idTicket", data.idTicket);
            console.log("Nuevo idTicket generado:", data.idTicket);
            // Actualizamos el valor global de idTicket para que siempre esté sincronizado
            idTicket = data.idTicket;
        } else {
            console.error("No se pudo obtener un idTicket válido.");
        }
    })
    .catch(error => {
        console.error("Error al generar nuevo ticket:", error);
    });
}



