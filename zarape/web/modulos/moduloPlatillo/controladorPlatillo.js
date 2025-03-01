let platillos = [];
let inputFileFotoPlatillo = null;

export function addFoto() {
    inputFileFotoPlatillo = document.getElementById("inputFileFotoPlatillo");
    inputFileFotoPlatillo.onchange = function(evt) { cargarFoto(); };
    document.getElementById("btnCargarFoto").onclick = function(evt) { inputFileFotoPlatillo.click(); };
}

function cargarFoto() {
    if (inputFileFotoPlatillo.files && inputFileFotoPlatillo.files[0]) {
        let reader = new FileReader();
        reader.onload = function (e) {
            let fotoB64 = e.target.result;
            document.getElementById("foto").src = fotoB64;
            document.getElementById("txtaFoto").value = fotoB64.substring(fotoB64.indexOf(",") + 1, fotoB64.length);
        };
        reader.readAsDataURL(inputFileFotoPlatillo.files[0]);
    }
}

function loadPlatillos() {
    let table = document.getElementById("renglones");
    let renglon = "";
    
    // Filtra solo los platillos activos (si es necesario)
    let platillosActivos = platillos.filter(platillo => platillo.producto.activo === 1);

    platillosActivos.forEach(platillo => {
        // Se crea la fila de la tabla con la información del platillo
        renglon += "<tr onclick='controladorGral.selectRegistro("+platillos.indexOf(platillo)+");'><td>" + platillo.producto.nombre + "</td>" +
                    "<td>" + platillo.producto.foto + "</td>" +
                    "<td>" + platillo.producto.descripcion + "</td>" +
                    "<td>" + platillo.producto.precio + "</td>" +
                    "<td>" + platillo.producto.categoria.descripcion + "</td>" +
                    
                    "</tr>";
    });
    
    table.innerHTML = renglon;
}

fetch('http://localhost:8080/zarape/api/platillo/getAllAlimentos')
    .then(response => response.json())
    .then(
        registro => {
            console.log(registro);
            platillos = registro; // Asume que la respuesta es un arreglo de platillos
            loadPlatillos();  // Carga los platillos activos
        })
    .catch(error => console.log('Error:', error));

export function selectRegistro(indice) {
    console.log("Datos seleccionados del platillo:", platillos[indice]);
    document.getElementById("id").value = platillos[indice].producto.idProducto;
    document.getElementById("nombre").value = platillos[indice].producto.nombre;
    document.getElementById("descripcion").value = platillos[indice].producto.descripcion;
    document.getElementById("precio").value = platillos[indice].producto.precio;
    document.getElementById("categoria").value = platillos[indice].producto.categoria.idCategoria;
}

function filtrarPlatillos() {
    const estatusActivo = document.getElementById("estatusSwitch").checked; // true: activos, false: inactivos

    // Filtrar los platillos según su estado activo/inactivo
    const platillosFiltrados = platillos.filter(platillo => platillo.producto.activo === (estatusActivo ? 1 : 0));

    // Actualiza la tabla con los platillos filtrados
    let tblPlatillos = document.getElementById("renglones");
    let renglon = "";
    platillosFiltrados.forEach(platillo => {
        renglon += "<tr onclick='controladorGral.selectRegistro(" + platillos.indexOf(platillo) + ");'>" +
                   "<td>" + platillo.producto.nombre + "</td>" +
                   "<td><img alt='' src='data:image/jpeg;base64," + platillo.producto.foto + "' style='width: 100px; height: 80px; object-fit: cover;'/></td>" +
                   "<td>" + platillo.producto.descripcion + "</td>" +
                   "<td>" + platillo.producto.precio + "</td>" +
                   "<td>" + platillo.producto.categoria.descripcion + "</td>" +
                   "</tr>";
    });

    tblPlatillos.innerHTML = renglon;
}

// Agregar el evento de cambio al switch de estado
document.getElementById("estatusSwitch").addEventListener("change", filtrarPlatillos);

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
let listCategorias = [];

function loadCategorias() {
    let v_categoria = document.getElementById("categoria");
    v_categoria.innerHTML = ""; // Limpia las opciones previas en categorías
    
    listCategorias.forEach(categoria => {
        let v_option = document.createElement("option");
        v_option.value = categoria.idCategoria;
        v_option.text = categoria.descripcion;
        v_categoria.appendChild(v_option);
    });
}

fetch('http://localhost:8080/zarape/api/zarape/getAllCategorias') // Cambia la URL a la que devuelva las categorías
    .then(response => response.json())
    .then(
        categorias => {
            console.log(categorias);
            listCategorias = categorias;
            loadCategorias();
        })
    .catch(error => console.error("Error al cargar categorías:", error));


